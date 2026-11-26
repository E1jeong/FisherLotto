import {AbsoluteFill, Audio, Easing, Img, Sequence, interpolate, random, spring, staticFile, useCurrentFrame, useVideoConfig} from 'remotion';
import happyBoyThemeBgm from './bgm/happy_boy_theme.mp3';
import data from './data.json';
import badgePopSfx from './sfx/badge_pop.wav';
import countTickSfx from './sfx/count_tick.wav';
import hookDropSfx from './sfx/hook_drop.wav';
import nudgeSfx from './sfx/nudge.wav';
import outroSfx from './sfx/outro.wav';
import pullUpSfx from './sfx/pull_up.wav';
import punchSfx from './sfx/punch.wav';
import strikeSfx from './sfx/strike.wav';
import whipSfx from './sfx/whip.wav';

const Sfx = ({at = 0, dur, src, volume = 0.6}: {at?: number; dur: number; src: string; volume?: number}) => (
  <Sequence from={at} durationInFrames={dur}>
    <Audio src={src} volume={volume} />
  </Sequence>
);

type Stats = {lottoRound: number; pickDate: string; grade1: number; grade2: number; grade3: number; combiCount: number};
const cur = data.current as Stats;
const prev = data.previous as Stats | null;

const FONT = "'Pretendard', 'Malgun Gothic', 'Apple SD Gothic Neo', sans-serif";
const MUTED = 'rgba(255,255,255,0.72)';
const RANKS = [
  {key: 'grade1', label: '1등', color: '#FBBF24'},
  {key: 'grade2', label: '2등', color: '#CBD5E1'},
  {key: 'grade3', label: '3등', color: '#F59E0B'},
] as const;

const clamp = {extrapolateLeft: 'clamp', extrapolateRight: 'clamp'} as const;
const pct = (n: number, total: number) => (total > 0 ? (n / total) * 100 : 0);
const fmtPct = (v: number) => (v === 0 ? '0' : String(Number(v.toPrecision(3))));
const fmtInt = (n: number) => Math.round(n).toLocaleString('ko-KR');
const kstDate = (iso: string) => {
  const d = new Date(new Date(iso).getTime() + 9 * 3600e3);
  return `${d.getUTCFullYear()}.${String(d.getUTCMonth() + 1).padStart(2, '0')}.${String(d.getUTCDate()).padStart(2, '0')}`;
};

const Bubbles = () => {
  const f = useCurrentFrame();
  return (
    <>
      {[120, 300, 760, 920, 480, 640].map((x, i) => {
        const y = 2000 - ((f * (3 + (i % 3)) + i * 330) % 2200);
        const r = 10 + (i % 3) * 8;
        return (
          <div key={i} style={{position: 'absolute', left: x + Math.sin((f + i * 20) / 15) * 14, top: y, width: r, height: r, borderRadius: '50%', border: '3px solid rgba(255,255,255,0.25)'}} />
        );
      })}
    </>
  );
};

// 물속 분위기: 수면 일렁임, 빛줄기, 부유물, 해초, 비네트. 인트로에서 강하게, 이후엔 숫자 가독성을 위해 약하게
const RAYS = [
  {x: 150, w: 230, a: 16},
  {x: 390, w: 170, a: 7},
  {x: 610, w: 270, a: -3},
  {x: 830, w: 190, a: -11},
  {x: 1010, w: 150, a: -19},
];
const KELP = [
  {x: 50, h: 560, w: 28},
  {x: 140, h: 400, w: 20},
  {x: 970, h: 480, w: 26},
  {x: 1050, h: 320, w: 18},
];
const Water = () => {
  const f = useCurrentFrame();
  const level = interpolate(f, [95, 130], [1, 0.45], clamp);
  const surface: string[] = [];
  for (let x = 0; x <= 1080; x += 30) surface.push(`${x},${110 + Math.sin(x / 95 + f / 9) * 14 + Math.sin(x / 41 - f / 6) * 6}`);
  return (
    <AbsoluteFill>
      {RAYS.map((r, i) => (
        <div key={i} style={{position: 'absolute', left: r.x - r.w / 2, top: -80, width: r.w, height: 2000, transformOrigin: '50% 0', transform: `rotate(${r.a + Math.sin(f / 45 + i * 1.3) * 2.5}deg)`, opacity: (0.55 + 0.45 * Math.sin(f / 22 + i * 2.1)) * level, filter: 'blur(18px)'}}>
          <div style={{width: '100%', height: '100%', clipPath: 'polygon(38% 0, 62% 0, 100% 100%, 0 100%)', background: 'linear-gradient(180deg, rgba(186,230,253,0.30) 0%, rgba(186,230,253,0.09) 45%, rgba(186,230,253,0) 80%)'}} />
        </div>
      ))}
      <svg width={1080} height={320} style={{position: 'absolute', top: 0, opacity: level}}>
        <defs>
          <linearGradient id="below-surface" x1="0" y1="0" x2="0" y2="1">
            <stop offset="0" stopColor="#BAE6FD" stopOpacity={0.35} />
            <stop offset="1" stopColor="#BAE6FD" stopOpacity={0} />
          </linearGradient>
        </defs>
        <rect y={90} width={1080} height={230} fill="url(#below-surface)" />
        <polygon points={`0,0 ${surface.join(' ')} 1080,0`} fill="rgba(186,230,253,0.22)" />
        <polyline points={surface.join(' ')} stroke="rgba(224,242,254,0.65)" strokeWidth={4} fill="none" />
      </svg>
      {Array.from({length: 40}, (_, i) => {
        const size = 2 + random(`ps${i}`) * 5;
        return (
          <div key={i} style={{position: 'absolute', left: random(`px${i}`) * 1080 + Math.sin(f / 30 + i) * 10, top: ((random(`py${i}`) * 2000 + f * (0.4 + random(`ps${i}`) * 1.2)) % 2000) - 40, width: size, height: size, borderRadius: '50%', background: 'rgba(224,242,254,0.6)', opacity: 0.2 + random(`ps${i}`) * 0.4, filter: size > 5 ? 'blur(1.5px)' : undefined}} />
        );
      })}
      <svg width={1080} height={1920} style={{position: 'absolute', inset: 0}}>
        {KELP.map((k, i) => {
          let d = `M${k.x} 1960`;
          for (let j = 1; j <= 20; j++) {
            const t = j / 20;
            d += ` L${k.x + Math.sin(f / 28 + i * 1.7 - t * 2.2) * t * t * 60} ${1960 - t * k.h}`;
          }
          return <path key={i} d={d} stroke="#03122C" strokeWidth={k.w} strokeLinecap="round" strokeLinejoin="round" fill="none" opacity={0.9} />;
        })}
      </svg>
      <AbsoluteFill style={{background: 'radial-gradient(ellipse 80% 70% at 50% 40%, rgba(2,8,23,0) 60%, rgba(2,8,23,0.45) 100%)'}} />
    </AbsoluteFill>
  );
};

// 물고기: 아이콘을 몸통/꼬리로 잘라 꼬리는 좌우로 치고(원근 압축) 몸통은 반동으로 살짝 휨. (x, y)는 입 위치
const FISH_W = 440;
const MOUTH = {x: 0.035, y: 0.48}; // 아이콘 안 입 위치(비율)
const Fish = ({x, y, rot, phase, amp}: {x: number; y: number; rot: number; phase: number; amp: number}) => {
  const wag = Math.sin(phase);
  const img = {position: 'absolute', width: '100%', height: '100%'} as const;
  return (
    <div style={{position: 'absolute', left: x - MOUTH.x * FISH_W, top: y - MOUTH.y * FISH_W, width: FISH_W, height: FISH_W, transformOrigin: `${MOUTH.x * 100}% ${MOUTH.y * 100}%`, transform: `rotate(${rot}deg)`}}>
      <div style={{position: 'absolute', inset: 0, transformOrigin: '45% 50%', transform: `rotate(${Math.sin(phase - 1.2) * amp * 0.15}deg)`}}>
        <Img src={staticFile('app_icon.png')} style={{...img, clipPath: 'inset(0 31% 0 0)'}} />
        <Img src={staticFile('app_icon.png')} style={{...img, clipPath: 'inset(0 0 0 67%)', transformOrigin: '69% 52%', transform: `rotate(${wag * amp}deg) scaleX(${1 - 0.22 * Math.abs(wag)})`}} />
      </div>
    </div>
  );
};

// 낚싯바늘(고리·굽음·미늘) + 미끼. 기준점은 고리 중심(60,14)
// BEND: 미끼가 달린 굽은 부분(입질 목표), HOLD: 물린 뒤 입이 붙는 바늘대 아래쪽(굽은 부분과 미끼는 입 속으로 들어가 안 보임)
const HOOK_S = 0.9;
const BEND = {x: 30, y: 162};
const HOLD = {x: 60, y: 140};
const Hook = ({x, y, tilt, f, caught}: {x: number; y: number; tilt: number; f: number; caught: boolean}) => {
  let worm = 'M38 176';
  for (let j = 1; j <= 7; j++) worm += ` L${38 + Math.sin(f / 5 + j * 0.9) * j * 1.4} ${176 + j * 10}`;
  const shank = caught ? 'M60 23 L60 150' : 'M60 23 L60 134 C60 180 16 182 16 140 L16 116';
  return (
    <svg width={100 * HOOK_S} height={260 * HOOK_S} viewBox="0 0 100 260" style={{position: 'absolute', left: x - 60 * HOOK_S, top: y - 14 * HOOK_S, overflow: 'visible', transformOrigin: `${60 * HOOK_S}px ${14 * HOOK_S}px`, transform: `rotate(${tilt}deg)`}}>
      <defs>
        <linearGradient id="steel" x1="0" y1="0" x2="1" y2="0">
          <stop offset="0" stopColor="#8A94A3" />
          <stop offset="0.45" stopColor="#F8FAFC" />
          <stop offset="1" stopColor="#6B7280" />
        </linearGradient>
        {/* 수직 직선만 남으면 bbox 폭이 0이라 위 그라데이션이 안 칠해지므로 바늘대 굵기 기준으로 따로 둠 */}
        <linearGradient id="steel-shank" gradientUnits="userSpaceOnUse" x1="56" y1="0" x2="64" y2="0">
          <stop offset="0" stopColor="#8A94A3" />
          <stop offset="0.45" stopColor="#F8FAFC" />
          <stop offset="1" stopColor="#6B7280" />
        </linearGradient>
        <linearGradient id="worm" x1="0" y1="0" x2="0" y2="1">
          <stop offset="0" stopColor="#F9A8D4" />
          <stop offset="1" stopColor="#DB2777" />
        </linearGradient>
      </defs>
      {!caught && (
        <>
          <path d="M60 148 C60 182 18 182 18 150" stroke="url(#worm)" strokeWidth={15} strokeLinecap="round" fill="none" />
          <path d={worm} stroke="url(#worm)" strokeWidth={12} strokeLinecap="round" strokeLinejoin="round" fill="none" />
        </>
      )}
      <circle cx={60} cy={14} r={9} stroke="#0F172A" strokeOpacity={0.45} strokeWidth={8} fill="none" />
      <circle cx={60} cy={14} r={9} stroke="url(#steel)" strokeWidth={5} fill="none" />
      <path d={shank} stroke="#0F172A" strokeOpacity={0.45} strokeWidth={11} strokeLinecap="round" fill="none" />
      <path d={shank} stroke={caught ? 'url(#steel-shank)' : 'url(#steel)'} strokeWidth={7} strokeLinecap="round" fill="none" />
      {!caught && (
        <>
          <path d="M9 120 L16 98 L23 120 Z" fill="#E2E8F0" stroke="#0F172A" strokeOpacity={0.45} strokeWidth={2} />
          <path d="M14 112 L5 126" stroke="url(#steel)" strokeWidth={4} strokeLinecap="round" />
        </>
      )}
    </svg>
  );
};

// 0–3.5초: 바늘이 흔들리며 내려옴 → 물고기가 헤엄쳐 와 머뭇·툭 건드림 → 덥석 물고 챔질·몸부림 → 위로 끌려 나감
const STRIKE = 62;
const hookAt = (f: number, fps: number) => {
  const descend = spring({frame: f, fps, config: {damping: 15}, durationInFrames: 30});
  const jerk = f >= STRIKE ? spring({frame: f - STRIKE, fps, config: {damping: 9, stiffness: 220}}) : 0;
  const pull = interpolate(f, [74, 108], [0, 1], {...clamp, easing: Easing.in(Easing.cubic)});
  const sway = f >= STRIKE ? 0 : Math.sin(f / 13);
  const eyeX = 540 + sway * 14;
  const eyeY = interpolate(descend, [0, 1], [-300, 800]) - jerk * 45 - pull * 1900;
  const tilt = sway * 5;
  const rad = (tilt * Math.PI) / 180;
  // 바늘 좌표(p)를 기울기까지 반영한 화면 좌표로 변환
  const at = (p: {x: number; y: number}) => {
    const bx = (p.x - 60) * HOOK_S;
    const by = (p.y - 14) * HOOK_S;
    return {x: eyeX + bx * Math.cos(rad) - by * Math.sin(rad), y: eyeY + bx * Math.sin(rad) + by * Math.cos(rad)};
  };
  return {eyeX, eyeY, tilt, sway, pull, bend: at(BEND), hold: at(HOLD)};
};
const tailSpeed = (f: number) => (f < 40 ? interpolate(f, [0, 40], [0.8, 0.35]) : f < 58 ? 0.3 : f < STRIKE ? 0.9 : f < 76 ? 1.1 : 0.9);
const tailAmp = (f: number) => (f < 40 ? 16 : f < 58 ? 10 : f < STRIKE ? 22 : f < 76 ? 26 : 22);

const Intro = () => {
  const f = useCurrentFrame();
  const {fps} = useVideoConfig();
  const h = hookAt(f, fps);
  let phase = 0;
  for (let i = 0; i < f; i++) phase += tailSpeed(i);

  let mx: number;
  let my: number;
  let rot: number;
  const hoverX = h.bend.x + 140;
  const hoverY = h.bend.y + 10;
  if (f < 40) {
    const t = Easing.out(Easing.cubic)(f / 40);
    mx = interpolate(t, [0, 1], [1420, hoverX]) - Math.sin(phase) * 3;
    my = interpolate(t, [0, 1], [1200, hoverY]) - Math.sin(t * Math.PI) * 40;
    rot = interpolate(t, [0, 1], [7, 0]);
  } else if (f < 58) {
    const nibble = Math.sin(interpolate(f, [46, 53], [0, 1], clamp) * Math.PI);
    mx = hoverX - nibble * 75;
    my = hoverY + Math.sin((f - 40) / 6) * 6;
    rot = -nibble * 4;
  } else if (f < STRIKE) {
    const t = Easing.in(Easing.quad)((f - 58) / (STRIKE - 58));
    mx = interpolate(t, [0, 1], [hoverX, h.hold.x]);
    my = interpolate(t, [0, 1], [hoverY, h.hold.y]);
    rot = -t * 4;
  } else {
    mx = h.hold.x;
    my = h.hold.y;
    const thrash = Math.sin((f - STRIKE) * 1.7) * 16 * interpolate(f, [STRIKE, 82], [1, 0.3], clamp);
    rot = thrash + h.pull * 80;
  }

  const age = f - STRIKE;
  const origin = hookAt(STRIKE, fps).hold;
  const line = `M${h.eyeX + 4} -20 Q${h.eyeX - h.sway * 22 + (f < STRIKE ? 10 : 0)} ${h.eyeY / 2} ${h.eyeX} ${h.eyeY - 9 * HOOK_S}`;
  return (
    <AbsoluteFill>
      <Sfx at={2} dur={20} src={hookDropSfx} volume={0.45} />
      <Sfx at={46} dur={10} src={nudgeSfx} volume={0.5} />
      <Sfx at={61} dur={26} src={strikeSfx} volume={0.75} />
      <Sfx at={75} dur={30} src={pullUpSfx} volume={0.55} />
      <svg width={1080} height={1920} style={{position: 'absolute', inset: 0}}>
        <path d={line} stroke="rgba(255,255,255,0.7)" strokeWidth={2.5} fill="none" />
      </svg>
      <Hook x={h.eyeX} y={h.eyeY} tilt={h.tilt} f={f} caught={f >= STRIKE} />
      <Fish x={mx} y={my} rot={rot} phase={phase} amp={tailAmp(f)} />
      {age >= 0 && (
        <>
          <div style={{position: 'absolute', left: origin.x - (12 + age * 9), top: origin.y - (12 + age * 9), width: (12 + age * 9) * 2, height: (12 + age * 9) * 2, borderRadius: '50%', border: '4px solid rgba(255,255,255,0.8)', opacity: interpolate(age, [0, 14], [1, 0], clamp)}} />
          {Array.from({length: 12}, (_, i) => {
            const r = 6 + random(`br${i}`) * 14;
            return (
              <div key={i} style={{position: 'absolute', left: origin.x + (random(`bx${i}`) - 0.5) * 6 * age + Math.sin(age / 4 + i) * 6, top: origin.y - (4 + random(`by${i}`) * 6) * age - 0.05 * age * age, width: r, height: r, borderRadius: '50%', border: '3px solid rgba(255,255,255,0.75)', opacity: interpolate(age, [0, 4, 30], [0, 1, 0], clamp)}} />
            );
          })}
        </>
      )}
    </AbsoluteFill>
  );
};

// 컷 전환: 위로 빠르게 넘기는 휩(모션 블러) + 컷이 떠 있는 동안 천천히 줌인
const WHIP = 14;
const Cut = ({dur, children}: {dur: number; children: React.ReactNode}) => {
  const f = useCurrentFrame();
  // 들어오는 컷과 나가는 컷이 같은 진행값을 써야 서로 겹치지 않고 한 번의 팬처럼 이어짐
  const inP = interpolate(f, [0, WHIP], [0, 1], {...clamp, easing: Easing.inOut(Easing.cubic)});
  const outP = interpolate(f, [dur - WHIP, dur], [0, 1], {...clamp, easing: Easing.inOut(Easing.cubic)});
  const y = (1 - inP) * 1920 - outP * 1920;
  const blur = ((1 - inP) + outP) * 40;
  const zoom = interpolate(f, [0, dur], [1, 1.07]);
  return (
    <AbsoluteFill style={{transform: `translateY(${y}px) scale(${zoom})`, filter: blur > 0.5 ? `blur(${blur}px)` : undefined}}>
      <Sfx dur={15} src={whipSfx} volume={0.5} />
      {children}
    </AbsoluteFill>
  );
};

// 큰 숫자: 크게 줌인되며 박히고 카운트업
const PunchNumber = ({value, size, glow}: {value: number; size: number; glow: string}) => {
  const f = useCurrentFrame();
  const {fps} = useVideoConfig();
  const punch = spring({frame: f - 4, fps, config: {damping: 11, stiffness: 160}});
  const shown = interpolate(f, [4, 30], [0, value], {...clamp, easing: Easing.out(Easing.cubic)});
  return (
    <>
      <Sfx at={7} dur={12} src={punchSfx} volume={0.6} />
      {value > 0 && <Sfx at={5} dur={29} src={countTickSfx} volume={0.38} />}
      <div style={{fontSize: size, fontWeight: 900, lineHeight: 1, fontVariantNumeric: 'tabular-nums', textShadow: `0 0 60px ${glow}`, transform: `scale(${interpolate(punch, [0, 1], [2.2, 1])})`, opacity: interpolate(punch, [0, 0.3], [0, 1], clamp)}}>
        {fmtInt(shown)}
      </div>
    </>
  );
};

const Rise = ({at, children}: {at: number; children: React.ReactNode}) => {
  const f = useCurrentFrame();
  const {fps} = useVideoConfig();
  const s = spring({frame: f - at, fps, config: {damping: 16}});
  return <div style={{opacity: s, transform: `translateY(${(1 - s) * 50}px)`}}>{children}</div>;
};

const Glow = ({color}: {color: string}) => {
  const f = useCurrentFrame();
  const r = 520 + Math.sin(f / 8) * 30;
  return <div style={{position: 'absolute', left: 540 - r, top: 820 - r, width: r * 2, height: r * 2, borderRadius: '50%', background: `radial-gradient(circle, ${color}55 0%, ${color}00 65%)`}} />;
};

const TotalCut = () => (
  <AbsoluteFill style={{alignItems: 'center', textAlign: 'center'}}>
    <Glow color="#60A5FA" />
    <div style={{position: 'absolute', top: 260, width: '100%'}}>
      <Rise at={0}>
        <div style={{fontSize: 120, fontWeight: 900}}>제{cur.lottoRound}회</div>
        <div style={{fontSize: 52, fontWeight: 700, marginTop: 8}}>어부로또 발급 번호 통계</div>
        <div style={{fontSize: 34, color: MUTED, marginTop: 12}}>{kstDate(cur.pickDate)} 추첨</div>
      </Rise>
    </div>
    <div style={{position: 'absolute', top: 720, width: '100%', display: 'flex', flexDirection: 'column', alignItems: 'center'}}>
      <PunchNumber value={cur.combiCount} size={170} glow="#60A5FA" />
      <Rise at={14}><div style={{fontSize: 60, fontWeight: 800, marginTop: 28}}>추천 조합 발급</div></Rise>
    </div>
  </AbsoluteFill>
);

const RankCut = ({rank}: {rank: (typeof RANKS)[number]}) => {
  const f = useCurrentFrame();
  const {fps} = useVideoConfig();
  const badge = spring({frame: f, fps, config: {damping: 10, stiffness: 180}});
  const count = cur[rank.key];
  const p = pct(count, cur.combiCount);
  let delta: {text: string; color: string} | null = null;
  if (prev) {
    const d = p - pct(prev[rank.key], prev.combiCount);
    delta = Math.abs(d) < 1e-12
      ? {text: '전회차와 동일', color: MUTED}
      : {text: `${d > 0 ? '▲' : '▼'} ${fmtPct(Math.abs(d))}%p`, color: d > 0 ? '#FCA5A5' : '#93C5FD'};
  }
  return (
    <AbsoluteFill style={{alignItems: 'center', textAlign: 'center'}}>
      <Sfx at={2} dur={10} src={badgePopSfx} volume={0.42} />
      <Glow color={rank.color} />
      <div style={{position: 'absolute', top: 300, display: 'flex', flexDirection: 'column', alignItems: 'center'}}>
        <div style={{width: 210, height: 210, borderRadius: '50%', background: rank.color, color: '#0B1E4A', fontSize: 84, fontWeight: 900, display: 'flex', alignItems: 'center', justifyContent: 'center', transform: `scale(${badge})`, boxShadow: `0 0 80px ${rank.color}99`}}>{rank.label}</div>
        <Rise at={4}><div style={{fontSize: 42, color: MUTED, marginTop: 30}}>{rank.label} 번호와 일치한 추천 조합</div></Rise>
      </div>
      <div style={{position: 'absolute', top: 680, display: 'flex', alignItems: 'baseline', gap: 16}}>
        <PunchNumber value={count} size={280} glow={rank.color} />
        <div style={{fontSize: 80, fontWeight: 800}}>개</div>
      </div>
      <div style={{position: 'absolute', top: 1010, width: '100%'}}>
        <Rise at={14}>
          <div style={{fontSize: 60, fontWeight: 800}}>{count > 0 ? `약 ${fmtInt(cur.combiCount / count)}조합 중 1개` : '이번 회차 일치 없음'}</div>
          <div style={{fontSize: 36, color: MUTED, marginTop: 12}}>발급 조합의 {fmtPct(p)}%</div>
        </Rise>
        {delta && (
          <Rise at={22}><div style={{fontSize: 42, fontWeight: 800, color: delta.color, marginTop: 18}}>전회차 대비 {delta.text}</div></Rise>
        )}
      </div>
    </AbsoluteFill>
  );
};

// 3.5–16초: 발급 수 → 3등 → 2등 → 1등, 한 컷에 숫자 하나 (컷끼리 WHIP 프레임만큼 겹쳐 전환)
const STATS_DUR = 380;
const CUTS = [
  {dur: 80, node: <TotalCut />},
  ...[...RANKS].reverse().map((r) => ({dur: 114, node: <RankCut rank={r} />})),
];
const Stats = () => {
  let from = 0;
  return (
    <AbsoluteFill>
      {CUTS.map((c, i) => {
        const start = from;
        from += c.dur - WHIP;
        const dur = i === CUTS.length - 1 ? STATS_DUR - start : c.dur;
        return (
          <Sequence key={i} from={start} durationInFrames={dur}>
            <Cut dur={dur}>{c.node}</Cut>
          </Sequence>
        );
      })}
    </AbsoluteFill>
  );
};

// 16–20초: 앱 아이콘 + QR + 면책 문구
const Outro = () => {
  const f = useCurrentFrame();
  const {fps} = useVideoConfig();
  const s = spring({frame: f, fps, config: {damping: 12}});
  const fadeIn = interpolate(f, [0, 12], [0, 1], clamp);
  return (
    <AbsoluteFill style={{alignItems: 'center', opacity: fadeIn}}>
      <Sfx at={2} dur={40} src={outroSfx} volume={0.5} />
      <Img src={staticFile('app_icon.png')} style={{position: 'absolute', top: 230, width: 300, height: 300, transform: `scale(${s})`}} />
      <div style={{position: 'absolute', top: 560, fontSize: 92, fontWeight: 800}}>어부로또</div>
      <div style={{position: 'absolute', top: 690, fontSize: 40, color: MUTED}}>Play 스토어에서 '어부로또' 검색</div>
      <div style={{position: 'absolute', top: 790, padding: 24, borderRadius: 32, background: '#FFFFFF'}}>
        <Img src={staticFile('qr.png')} style={{width: 340, height: 340, display: 'block'}} />
      </div>
      <div style={{position: 'absolute', top: 1260, fontSize: 30, color: MUTED, textAlign: 'center', lineHeight: 1.5}}>
        추천 조합의 결과적인 통계 정보만을 제공하며,<br />당첨이 보장되지 않습니다.
      </div>
    </AbsoluteFill>
  );
};

// BGM: 앞 무음(약 1.4초)을 잘라 시작, 마지막 1초 페이드아웃 (CC BY 4.0 — 출처 표기는 README)
const BGM_VOLUME = 0.2;
const Bgm = () => {
  const {durationInFrames, fps} = useVideoConfig();
  return <Audio src={happyBoyThemeBgm} trimBefore={43} volume={(f) => interpolate(f, [durationInFrames - fps, durationInFrames - 1], [BGM_VOLUME, 0], clamp)} />;
};

export const Shorts = () => (
  <AbsoluteFill style={{fontFamily: FONT, color: '#FFFFFF', background: 'linear-gradient(180deg, #0B3B8C 0%, #0A2A66 55%, #061A40 100%)'}}>
    <Bgm />
    <Water />
    <Bubbles />
    <Sequence durationInFrames={110}><Intro /></Sequence>
    <Sequence from={100} durationInFrames={STATS_DUR}><Stats /></Sequence>
    <Sequence from={100 + STATS_DUR}><Outro /></Sequence>
    {/* 영상 전체 푸터: 하단 약 20%는 Shorts UI에 가려지므로 그 바로 위에 둔다 */}
    <div style={{position: 'absolute', top: 1480, width: '100%', textAlign: 'center', fontSize: 28, color: 'rgba(255,255,255,0.55)'}}>어부로또 제공</div>
  </AbsoluteFill>
);
