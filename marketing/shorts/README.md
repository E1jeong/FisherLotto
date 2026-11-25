# 회차별 통계 유튜브 쇼츠

Statistics 화면의 발급 번호 통계(`/api/lotto/stats`)로 20초 세로 영상(1080×1920)을 만드는 Remotion 템플릿입니다. 기획·표현 규칙은 위키 `FisherLotto/operations/youtube-shorts`에 있습니다.

## 매주 만들기

```powershell
npm install                # 처음 한 번만
npm run fetch              # 최신 회차 (특정 회차: npm run fetch -- 1250)
npm run render             # → out/shorts.mp4
```

- `npm run studio`: 브라우저 미리보기로 화면을 수정할 때 사용
- 전회차 통계가 없으면 `전회차 대비 …` 줄은 자동으로 숨겨집니다.
- `src/data.json`, `public/`, `out/`은 자동 생성 파일이라 git에서 제외됩니다.

## BGM 출처 표기 (필수)

BGM(`src/bgm/happy_boy_theme.mp3`)은 CC BY 4.0이라, 영상을 올릴 때마다 설명란에 아래 문구를 넣어야 합니다. YouTube 밖(릴스, 틱톡 등)에 올릴 때도 같습니다.

```text
Music: "Happy Boy Theme" by Kevin MacLeod, 크리에이티브 커먼즈 저작자 표시 4.0 라이선스 적용 (일부 구간 편집)
https://creativecommons.org/licenses/by/4.0/
출처: http://incompetech.com/music/royalty-free/index.html?isrc=USUAN1100647
아티스트: http://incompetech.com/
```
