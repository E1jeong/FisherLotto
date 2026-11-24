// 효과음 WAV 합성 스크립트 (외부 음원/저작권 이슈 없이 로컬에서 결정적으로 생성)
import {mkdirSync, writeFileSync} from 'node:fs';
import {join} from 'node:path';

const SR = 44100;
const OUT_DIR = join('src', 'sfx');

function mulberry32(seed) {
  let a = seed >>> 0;
  return () => {
    a |= 0;
    a = (a + 0x6d2b79f5) | 0;
    let t = Math.imul(a ^ (a >>> 15), 1 | a);
    t = (t + Math.imul(t ^ (t >>> 7), 61 | t)) ^ t;
    return ((t ^ (t >>> 14)) >>> 0) / 4294967296;
  };
}

function makeBuf(sec) {
  return new Float64Array(Math.ceil(sec * SR));
}

// 1차 저역/고역 필터 결합 가변 대역통과 (부드러운 물소리·휩팬 노이즈용)
function addFilteredNoise(buf, {startSec = 0, durSec, freqStart, freqEnd, q = 2.0, gain = 1, envFn, seed = 1, tremolHz = 0, tremolDepth = 0}) {
  const rand = mulberry32(seed);
  const startIdx = Math.floor(startSec * SR);
  const len = Math.floor(durSec * SR);
  let x1 = 0, x2 = 0, y1 = 0, y2 = 0;
  // 핑크 노이즈 근사(Paul Kellet)
  let b0 = 0, b1 = 0, b2 = 0;
  for (let i = 0; i < len && startIdx + i < buf.length; i++) {
    const u = i / Math.max(1, len - 1);
    const white = rand() * 2 - 1;
    b0 = 0.99765 * b0 + white * 0.099046;
    b1 = 0.96300 * b1 + white * 0.2965164;
    b2 = 0.57000 * b2 + white * 1.0526913;
    const pink = (b0 + b1 + b2 + white * 0.1848) * 0.25;

    const fc = freqStart + (freqEnd - freqStart) * u;
    const w0 = (2 * Math.PI * Math.min(fc, SR * 0.45)) / SR;
    const alpha = Math.sin(w0) / (2 * q);
    const cosw = Math.cos(w0);
    const a0 = 1 + alpha;
    const bb0 = alpha / a0;
    const bb1 = 0;
    const bb2 = -alpha / a0;
    const aa1 = (-2 * cosw) / a0;
    const aa2 = (1 - alpha) / a0;

    const y = bb0 * pink + bb1 * x1 + bb2 * x2 - aa1 * y1 - aa2 * y2;
    x2 = x1;
    x1 = pink;
    y2 = y1;
    y1 = y;

    const mod = tremolHz > 0 ? 1 - tremolDepth * 0.5 * (1 - Math.cos(2 * Math.PI * tremolHz * (i / SR))) : 1;
    buf[startIdx + i] += y * envFn(u) * gain * mod;
  }
}

function addSweepSine(buf, {startSec = 0, durSec, f0, f1, curve = 1, gain = 1, attackSec = 0.004, decayRate = 6, harmonic2 = 0}) {
  const startIdx = Math.floor(startSec * SR);
  const len = Math.floor(durSec * SR);
  const att = Math.max(1, Math.floor(attackSec * SR));
  let phase = 0;
  for (let i = 0; i < len && startIdx + i < buf.length; i++) {
    const u = i / Math.max(1, len - 1);
    const f = f0 + (f1 - f0) * Math.pow(u, curve);
    phase += (2 * Math.PI * f) / SR;
    const envIn = i < att ? i / att : 1;
    const envOut = Math.exp(-decayRate * u) * (u > 0.9 ? (1 - u) / 0.1 : 1);
    const sig = Math.sin(phase) + harmonic2 * Math.sin(phase * 2);
    buf[startIdx + i] += sig * envIn * envOut * gain;
  }
}

function addTone(buf, {startSec = 0, durSec, freq, gain = 1, attackSec = 0.005, decayRate = 5, overtone = 0.2}) {
  addSweepSine(buf, {startSec, durSec, f0: freq, f1: freq, gain, attackSec, decayRate, harmonic2: overtone});
}

function finalize(buf, targetPeak = 0.85) {
  // 시작/끝 클릭 제거
  const fadeIn = Math.min(Math.floor(0.003 * SR), buf.length);
  const fadeOut = Math.min(Math.floor(0.015 * SR), buf.length);
  for (let i = 0; i < fadeIn; i++) buf[i] *= i / fadeIn;
  for (let i = 0; i < fadeOut; i++) buf[buf.length - 1 - i] *= i / fadeOut;

  let peak = 0;
  for (let i = 0; i < buf.length; i++) {
    const v = Math.abs(buf[i]);
    if (v > peak) peak = v;
  }
  if (peak > 0) {
    const scale = targetPeak / peak;
    for (let i = 0; i < buf.length; i++) {
      buf[i] = Math.tanh(buf[i] * scale * 1.05);
    }
  }
  return buf;
}

function writeWav(filename, samples) {
  const numSamples = samples.length;
  const dataSize = numSamples * 2;
  const ab = Buffer.alloc(44 + dataSize);
  ab.write('RIFF', 0);
  ab.writeUInt32LE(36 + dataSize, 4);
  ab.write('WAVE', 8);
  ab.write('fmt ', 12);
  ab.writeUInt32LE(16, 16); // PCM
  ab.writeUInt16LE(1, 20); // Linear PCM
  ab.writeUInt16LE(1, 22); // Mono
  ab.writeUInt32LE(SR, 24);
  ab.writeUInt32LE(SR * 2, 28);
  ab.writeUInt16LE(2, 32);
  ab.writeUInt16LE(16, 34);
  ab.write('data', 36);
  ab.writeUInt32LE(dataSize, 40);
  for (let i = 0; i < numSamples; i++) {
    const clamped = Math.max(-1, Math.min(1, samples[i]));
    ab.writeInt16LE(Math.round(clamped * 32767), 44 + i * 2);
  }
  writeFileSync(join(OUT_DIR, filename), ab);
}

mkdirSync(OUT_DIR, {recursive: true});

// 1. 낚싯바늘 입수 (부드러운 물방울 + 입수감)
{
  const b = makeBuf(0.55);
  addFilteredNoise(b, {durSec: 0.35, freqStart: 1200, freqEnd: 320, q: 1.8, gain: 0.7, seed: 11, envFn: (u) => Math.sin(Math.PI * Math.pow(u, 0.5)) * Math.exp(-3.5 * u)});
  addSweepSine(b, {startSec: 0.04, durSec: 0.14, f0: 290, f1: 680, curve: 1.3, gain: 0.55, decayRate: 4.5});
  addSweepSine(b, {startSec: 0.18, durSec: 0.13, f0: 420, f1: 860, curve: 1.4, gain: 0.35, decayRate: 5});
  writeWav('hook_drop.wav', finalize(b));
}

// 2. 물고기 머뭇거리며 미끼 톡 건드림 (수중 블룹)
{
  const b = makeBuf(0.24);
  addSweepSine(b, {startSec: 0.01, durSec: 0.12, f0: 240, f1: 560, curve: 1.4, gain: 0.7, decayRate: 5});
  addSweepSine(b, {startSec: 0.0, durSec: 0.08, f0: 140, f1: 75, curve: 0.8, gain: 0.35, decayRate: 7});
  writeWav('nudge.wav', finalize(b));
}

// 3. 입질 + 물결 + 거품 12개 폭발 (타격감 + 수중 거품 연타)
{
  const b = makeBuf(0.75);
  addSweepSine(b, {startSec: 0, durSec: 0.14, f0: 230, f1: 58, curve: 0.65, gain: 1.0, attackSec: 0.002, decayRate: 6});
  addFilteredNoise(b, {startSec: 0, durSec: 0.25, freqStart: 1900, freqEnd: 450, q: 1.4, gain: 0.85, seed: 22, envFn: (u) => Math.exp(-7 * u)});
  const bubbles = [
    [0.05, 360, 820, 0.45],
    [0.10, 480, 980, 0.42],
    [0.15, 410, 910, 0.36],
    [0.22, 540, 1120, 0.32],
    [0.31, 600, 1180, 0.26],
    [0.42, 660, 1260, 0.18],
  ];
  for (const [t, f0, f1, g] of bubbles) {
    addSweepSine(b, {startSec: t, durSec: 0.11, f0, f1, curve: 1.4, gain: g, decayRate: 5.5});
  }
  writeWav('strike.wav', finalize(b));
}

// 4. 몸부림치며 수면 위로 끌려나감 (상승 물살 + 떨림)
{
  const b = makeBuf(0.85);
  addFilteredNoise(b, {
    durSec: 0.82,
    freqStart: 280,
    freqEnd: 1750,
    q: 2.2,
    gain: 0.9,
    seed: 33,
    tremolHz: 14,
    tremolDepth: 0.35,
    envFn: (u) => Math.sin(Math.PI * Math.pow(u, 0.75)) * (0.4 + 0.6 * u),
  });
  addSweepSine(b, {startSec: 0.05, durSec: 0.65, f0: 140, f1: 420, curve: 1.6, gain: 0.22, decayRate: 1.5});
  writeWav('pull_up.wav', finalize(b));
}

// 5. 컷 전환 상향 휩 팬 (부드럽고 경쾌한 우시)
{
  const b = makeBuf(0.42);
  addFilteredNoise(b, {
    durSec: 0.40,
    freqStart: 260,
    freqEnd: 1900,
    q: 1.7,
    gain: 0.95,
    seed: 44,
    envFn: (u) => Math.sin(Math.PI * u) * Math.pow(Math.sin(Math.PI * u), 0.4),
  });
  addSweepSine(b, {startSec: 0.04, durSec: 0.26, f0: 95, f1: 190, curve: 1.1, gain: 0.28, decayRate: 3});
  writeWav('whip.wav', finalize(b));
}

// 6. 등급 배지 팝 (깔끔한 UI 팝)
{
  const b = makeBuf(0.18);
  addSweepSine(b, {startSec: 0, durSec: 0.055, f0: 360, f1: 920, curve: 0.85, gain: 0.8, attackSec: 0.002, decayRate: 5});
  addTone(b, {startSec: 0.015, durSec: 0.14, freq: 1046.5, gain: 0.42, attackSec: 0.003, decayRate: 8, overtone: 0.15});
  writeWav('badge_pop.wav', finalize(b));
}

// 7. 큰 숫자 임팩트 (단단한 서브 펀치 + 어택 클릭)
{
  const b = makeBuf(0.28);
  addSweepSine(b, {startSec: 0, durSec: 0.22, f0: 155, f1: 46, curve: 0.55, gain: 1.0, attackSec: 0.002, decayRate: 5.5});
  addSweepSine(b, {startSec: 0, durSec: 0.035, f0: 950, f1: 280, curve: 0.7, gain: 0.45, attackSec: 0.001, decayRate: 8});
  addFilteredNoise(b, {startSec: 0, durSec: 0.06, freqStart: 1200, freqEnd: 500, q: 1.5, gain: 0.3, seed: 55, envFn: (u) => Math.exp(-10 * u)});
  writeWav('punch.wav', finalize(b));
}

// 8. 카운트업 틱 롤 + 완료 차임 (0.83초 = 약 25프레임, Easing.out(cubic) 감속 리듬)
{
  const b = makeBuf(0.90);
  // 0..0.76s 동안 점점 느려지는 13개 틱
  const ticks = 13;
  for (let k = 0; k < ticks; k++) {
    const progress = k / (ticks - 1); // 0..1
    // cubic ease-out의 역함수로 시간 배치 -> 초반 빠르고 후반 느려짐
    const t = 0.76 * (1 - Math.pow(1 - progress, 1.8));
    const freq = 920 + progress * 460;
    addSweepSine(b, {startSec: t, durSec: 0.032, f0: freq, f1: freq * 0.72, curve: 0.8, gain: 0.42 + 0.15 * progress, attackSec: 0.001, decayRate: 9});
  }
  // 완료 차임 (0.78s)
  addTone(b, {startSec: 0.78, durSec: 0.12, freq: 1318.51, gain: 0.48, attackSec: 0.003, decayRate: 7, overtone: 0.1});
  writeWav('count_tick.wav', finalize(b));
}

// 9. 아웃트로 밝은 마무리 차임 (C5-E5-G5-C6 물방울 아르페지오)
{
  const b = makeBuf(1.15);
  const notes = [
    [0.02, 523.25, 0.55],
    [0.10, 659.25, 0.58],
    [0.18, 783.99, 0.62],
    [0.27, 1046.50, 0.72],
  ];
  for (const [t, f, g] of notes) {
    addTone(b, {startSec: t, durSec: 0.75, freq: f, gain: g, attackSec: 0.006, decayRate: 5.5, overtone: 0.12});
  }
  writeWav('outro.wav', finalize(b));
}

console.log('Generated 9 SFX files in src/sfx/');
