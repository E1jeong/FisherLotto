import {AbsoluteFill, Easing, Img, Sequence, interpolate, spring, staticFile, useCurrentFrame, useVideoConfig} from 'remotion';
import data from './data.json';

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

// 0–3.5초: 바늘이 내려오고 물고기가 물려 화면 위로 끌려 올라감
const Intro = () => {
  const f = useCurrentFrame();
  const {fps} = useVideoConfig();
  const HOOK_X = 540;
  const W = 440;
  const descend = spring({frame: f, fps, config: {damping: 14}, durationInFrames: 30});
  const pull = interpolate(f, [48, 100], [0, 1], {...clamp, easing: Easing.in(Easing.cubic)});
  const hookY = interpolate(descend, [0, 1], [-80, 1000]) - pull * 1800;
  const caught = f >= 38;
  const swim = interpolate(f, [4, 38], [0, 1], {...clamp, easing: Easing.out(Easing.cubic)});
  const mouthX = caught ? HOOK_X : interpolate(swim, [0, 1], [1500, HOOK_X]);
  const mouthY = caught ? hookY + 40 : 1040 + Math.sin(f / 5) * 12;
  const shake = f >= 38 && f < 50 ? Math.sin(f * 2.2) * 10 : 0;
  const rot = pull * 75 + shake;
  return (
    <AbsoluteFill>
      <div style={{position: 'absolute', left: HOOK_X - 2, top: 0, width: 4, height: Math.max(0, hookY), background: 'rgba(255,255,255,0.85)'}} />
      <Img
        src={staticFile('app_icon.png')}
        style={{position: 'absolute', width: W, height: W, left: mouthX - 0.07 * W, top: mouthY - 0.5 * W, transformOrigin: '7% 50%', transform: `rotate(${rot}deg)`}}
      />
      <svg style={{position: 'absolute', left: HOOK_X - 40, top: hookY - 5, overflow: 'visible'}} width={80} height={80}>
        <path d="M40 5 L40 36 Q40 60 20 56 Q8 52 12 40" stroke="#E5E7EB" strokeWidth={7} fill="none" strokeLinecap="round" />
      </svg>
    </AbsoluteFill>
  );
};

const Header = () => {
  const f = useCurrentFrame();
  const {fps} = useVideoConfig();
  const s = spring({frame: f, fps, config: {damping: 18}});
  return (
    <div style={{position: 'absolute', top: 150, width: '100%', textAlign: 'center', opacity: s, transform: `translateY(${(1 - s) * 40}px)`}}>
      <div style={{fontSize: 104, fontWeight: 800}}>제{cur.lottoRound}회</div>
      <div style={{fontSize: 56, fontWeight: 700, marginTop: 8}}>어부로또 발급 번호 통계</div>
      <div style={{fontSize: 34, color: MUTED, marginTop: 14}}>{kstDate(cur.pickDate)} 추첨</div>
    </div>
  );
};

const RankCard = ({rank, index}: {rank: (typeof RANKS)[number]; index: number}) => {
  const f = useCurrentFrame() - 25 - index * 35;
  const {fps} = useVideoConfig();
  const s = spring({frame: f, fps, config: {damping: 16}});
  const count = cur[rank.key];
  const shown = interpolate(f, [0, 25], [0, count], clamp);
  const p = pct(count, cur.combiCount);
  let delta: {text: string; color: string} | null = null;
  if (prev) {
    const d = p - pct(prev[rank.key], prev.combiCount);
    delta = Math.abs(d) < 1e-12
      ? {text: '전회차와 동일', color: MUTED}
      : {text: `${d > 0 ? '▲' : '▼'} ${fmtPct(Math.abs(d))}%p`, color: d > 0 ? '#FCA5A5' : '#93C5FD'};
  }
  return (
    <div style={{position: 'absolute', left: 80, top: 700 + index * 250, width: 920, height: 220, borderRadius: 36, background: 'rgba(255,255,255,0.1)', border: '2px solid rgba(255,255,255,0.18)', display: 'flex', alignItems: 'center', gap: 40, padding: '0 44px', boxSizing: 'border-box', opacity: s, transform: `translateY(${(1 - s) * 60}px)`}}>
      <div style={{width: 140, height: 140, borderRadius: '50%', background: rank.color, color: '#0B1E4A', fontSize: 54, fontWeight: 800, display: 'flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0}}>{rank.label}</div>
      <div>
        <div style={{fontSize: 68, fontWeight: 800}}>{fmtInt(shown)}개 조합</div>
        <div style={{fontSize: 32, color: MUTED, marginTop: 4}}>
          {count > 0 ? `약 ${fmtInt(cur.combiCount / count)}조합 중 1개` : '이번 회차 일치 없음'} · {fmtPct(p)}%
        </div>
        {delta && <div style={{fontSize: 30, fontWeight: 700, color: delta.color, marginTop: 4}}>(전회차 대비 {delta.text})</div>}
      </div>
    </div>
  );
};

// 3.5–11초: 발급 수 + 1~3등 일치 조합
const Stats = () => {
  const f = useCurrentFrame();
  const total = interpolate(f, [0, 30], [0, cur.combiCount], clamp);
  const fadeOut = interpolate(f, [210, 228], [1, 0], clamp);
  return (
    <AbsoluteFill style={{opacity: fadeOut}}>
      <Header />
      <div style={{position: 'absolute', top: 520, width: '100%', textAlign: 'center'}}>
        <span style={{fontSize: 42, fontWeight: 700, padding: '16px 40px', borderRadius: 999, background: 'rgba(255,255,255,0.14)'}}>
          추천 조합 {fmtInt(total)}개 발급
        </span>
        <div style={{fontSize: 32, color: MUTED, marginTop: 36}}>1~3등 번호와 일치한 추천 조합</div>
      </div>
      {RANKS.map((r, i) => <RankCard key={r.key} rank={r} index={i} />)}
    </AbsoluteFill>
  );
};

// 11–15초: 앱 아이콘 + QR + 면책 문구
const Outro = () => {
  const f = useCurrentFrame();
  const {fps} = useVideoConfig();
  const s = spring({frame: f, fps, config: {damping: 12}});
  const fadeIn = interpolate(f, [0, 12], [0, 1], clamp);
  return (
    <AbsoluteFill style={{alignItems: 'center', opacity: fadeIn}}>
      <Img src={staticFile('app_icon.png')} style={{position: 'absolute', top: 230, width: 300, height: 300, transform: `scale(${s})`}} />
      <div style={{position: 'absolute', top: 560, fontSize: 92, fontWeight: 800}}>어부로또</div>
      <div style={{position: 'absolute', top: 690, fontSize: 40, color: MUTED}}>Play 스토어에서 '어부로또' 검색</div>
      <div style={{position: 'absolute', top: 790, padding: 24, borderRadius: 32, background: '#FFFFFF'}}>
        <Img src={staticFile('qr.png')} style={{width: 340, height: 340, display: 'block'}} />
      </div>
      <div style={{position: 'absolute', top: 1260, fontSize: 30, color: MUTED, textAlign: 'center', lineHeight: 1.5}}>
        본 통계는 참고·오락용이며<br />당첨을 보장하지 않습니다.
      </div>
    </AbsoluteFill>
  );
};

export const Shorts = () => (
  <AbsoluteFill style={{fontFamily: FONT, color: '#FFFFFF', background: 'linear-gradient(180deg, #0B3B8C 0%, #0A2A66 55%, #061A40 100%)'}}>
    <Bubbles />
    <Sequence durationInFrames={110}><Intro /></Sequence>
    <Sequence from={100} durationInFrames={230}><Stats /></Sequence>
    <Sequence from={330}><Outro /></Sequence>
  </AbsoluteFill>
);
