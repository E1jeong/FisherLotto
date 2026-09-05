# Google Play Store Graphics Assets

어부로또(FisherLotto)의 구글 플레이 스토어 등록용 공식 그래픽 애셋입니다.

## 1. 현재 적용 애셋 (2026-09-05 배포)

| 파일명 | 용도 | 규격 | 비고 |
| :--- | :--- | :--- | :--- |
| [`google-play-icon.png`](./google-play-icon.png) | 구글 플레이 앱 아이콘 | 512 x 512 px (PNG) | 흰색 배경, 원본 황금 물고기 마스코트 적용 |
| [`google-play-feature-graphic.png`](./google-play-feature-graphic.png) | 스토어 그래픽 이미지 (Feature Graphic) | 1024 x 500 px (PNG) | 오션 딥블루 테마 (Style B), 로또 번호 및 기능 태그 포함 |

## 2. 대체 스타일 배너 (`alternatives/`)

* [`alternatives/feature-graphic-white.png`](./alternatives/feature-graphic-white.png): 화이트 & 글래스 테마 (Style A)
* [`alternatives/feature-graphic-minimal.png`](./alternatives/feature-graphic-minimal.png): 미니멀 스튜디오 테마 (Style C)

## 3. 애셋 생성 스크립트

* 소스 마스코트: `app/src/main/res/drawable/app_icon.png` (1254x1254 RGBA)
* 배너 생성기: 필요 시 동일 규격으로 재생성 가능
