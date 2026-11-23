# 회차별 통계 유튜브 쇼츠

Statistics 화면의 발급 번호 통계(`/api/lotto/stats`)로 15초 세로 영상(1080×1920)을 만드는 Remotion 템플릿입니다. 기획·표현 규칙은 위키 `FisherLotto/operations/youtube-shorts`에 있습니다.

## 매주 만들기

```powershell
npm install                # 처음 한 번만
npm run fetch              # 최신 회차 (특정 회차: npm run fetch -- 1250)
npm run render             # → out/shorts.mp4
```

- `npm run studio`: 브라우저 미리보기로 화면을 수정할 때 사용
- 전회차 통계가 없으면 `(전회차 대비 …)`는 자동으로 숨겨집니다.
- `src/data.json`, `public/`, `out/`은 자동 생성 파일이라 git에서 제외됩니다.
