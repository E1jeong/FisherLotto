// 사용법: npm run fetch [-- <회차>]  (회차 생략 시 최신 회차)
import {copyFile, mkdir, writeFile} from 'node:fs/promises';
import QRCode from 'qrcode';

const API = 'https://www.fisherlotto.com:3001/api/lotto/stats?round=';
const PLAY_URL = 'https://play.google.com/store/apps/details?id=com.queentech.fisherlotto';

async function getStats(round) {
  const res = await fetch(API + round);
  if (!res.ok) throw new Error(`HTTP ${res.status} (round=${round})`);
  const body = await res.json();
  return body.status === '8200' ? body : null;
}

const round = Number(process.argv[2] ?? 0);
if (!Number.isInteger(round) || round < 0) throw new Error(`잘못된 회차: ${process.argv[2]}`);

const current = await getStats(round);
if (!current) throw new Error(`${round || '최신'}회차 발급 통계가 없습니다.`);
const previous = await getStats(current.lottoRound - 1);
if (!previous) console.warn(`${current.lottoRound - 1}회차 통계가 없어 전회차 대비 값을 숨깁니다.`);

await writeFile('src/data.json', JSON.stringify({current, previous}, null, 2));
await mkdir('public', {recursive: true});
await copyFile('../../app/src/main/res/drawable/app_icon.png', 'public/app_icon.png');
await QRCode.toFile('public/qr.png', PLAY_URL, {width: 720, margin: 1});

console.log(`준비 완료: ${current.lottoRound}회 (전회차 ${previous ? '있음' : '없음'})`);
