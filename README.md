# 🎱 Fisher Lotto

로또 당첨 결과 조회, QR 코드 당첨 확인, 뉴스 및 통계를 제공하는 Android 앱

## 프로젝트 소개

Fisher Lotto는 로또 사용자를 위한 올인원 앱입니다.
최신 당첨 번호 확인부터 QR 코드 스캔을 통한 즉시 당첨 확인, 로또 관련 뉴스, 통계 분석, 소셜 로그인, 오픈뱅킹 연동까지 다양한 기능을 한 곳에서 이용할 수 있습니다.

## 주요 기능

| 기능 | 설명 | 상태 |
|------|------|------|
| **홈 (당첨 정보)** | 최신 회차 당첨 번호 및 등수별 당첨금 조회 + 로또 뉴스 | ✅ |
| **QR 당첨 확인** | 로또 용지의 QR 코드를 카메라로 스캔하여 당첨 여부 즉시 확인 | ✅ |
| **로또 뉴스** | Google News RSS 기반 로또 관련 최신 뉴스 제공 (30분 캐싱) | ✅ |
| **당첨 통계** | 회차별 등수 당첨자 수 통계 테이블 | ✅ |
| **로그인 / 회원가입** | 이메일 + 소셜 로그인 (Kakao, Google) | ✅ |
| **마이페이지** | 사용자 정보 관리 + 오픈뱅킹 연동 | ✅ |
| **오픈뱅킹** | OAuth 인증, 계좌 조회, 잔액 확인, 출금이체(송금) | ✅ |
| **예상 번호** | 번호 생성 기능 | 🚧 |

## 기술 스택

### Architecture
- **Multi-Module Clean Architecture** — app / data / domain / presentation 4개 모듈 분리
- **Orbit MVI** — 단방향 데이터 흐름 (State + SideEffect)
- **Hilt** — 의존성 주입 (DI)

### UI
- **Jetpack Compose** (BOM 2024.12.01)
- **Material 3**
- **Navigation Compose** — Bottom Navigation 기반 5탭 구조
- **Coil** — 이미지 로딩
- **Lottie Compose** — 애니메이션
- **ConstraintLayout Compose**

### Networking & Data
- **Retrofit + OkHttp** — REST API 통신 (타임아웃 30초, 로깅 인터셉터)
- **@Named 다중 Retrofit** — lotto 서버 / lotto-sub 서버 분리 관리
- **Kotlinx Serialization / Gson** — JSON 직렬화
- **Room** — 로컬 데이터베이스
- **DataStore** — 설정 및 캐시 저장
- **Jsoup** — Google News RSS 파싱

### Camera & ML
- **CameraX** — 실시간 카메라 프리뷰
- **ML Kit Barcode Scanning** — QR 코드 인식 및 로또 당첨 결과 파싱

### Backend & Auth
- **Firebase** Auth / Realtime Database / Cloud Messaging / Analytics
- **Kakao SDK** — 카카오 소셜 로그인
- **Google Play Services Auth** — 구글 소셜 로그인
- **오픈뱅킹 API** — 계좌 조회, 잔액 확인, 출금이체
- **Sub Backend (Vercel)** — 결제 및 오픈뱅킹 프록시 서버

### Testing
- **JUnit** + **MockK** — 단위 테스트
- **Turbine** — Flow 테스트
- **MockWebServer** — API 테스트
- **Compose UI Test** — UI 테스트

## 모듈 구조

```
FisherLotto/
├── app/                # Application 클래스, Hilt 설정, Firebase/Kakao 초기화
├── domain/             # UseCase 인터페이스, Model 정의 (순수 Kotlin)
├── data/               # UseCase 구현, API Service, Repository, DI 모듈
├── presentation/       # Compose UI, ViewModel, Navigation, Theme
└── gradle/             # Version Catalog (libs.versions.toml)
```

### 모듈 의존성

```
app → presentation, data, domain
presentation → domain
data → domain
```

`domain` 모듈은 다른 모듈에 대한 의존성이 없는 순수 Kotlin 모듈로, 비즈니스 로직의 독립성을 보장합니다.

## 아키텍처 설계 포인트

- **DIP (의존성 역전 원칙)** — UseCase 인터페이스를 `domain`에 정의, 구현체는 `data`에 위치
- **Hilt @Binds** — UseCase 인터페이스와 구현체를 연결 (`LottoModule`)
- **@Named Retrofit** — 다중 서버(`lotto` / `lotto-sub`) 인스턴스를 분리 관리
- **Orbit MVI** — ViewModel의 상태 관리를 단방향으로 통일하여 예측 가능한 UI 상태 유지
- **Version Catalog** — `libs.versions.toml`로 라이브러리 버전 중앙 관리

## 화면 구성 (5탭)

| 홈 | QR 당첨 확인 | 예상 번호 | 통계 | 내 정보 |
|:---:|:---:|:---:|:---:|:---:|
| 당첨 번호 + 뉴스 | 카메라 스캔 | 번호 생성 | 회차별 통계 | 사용자 + 오픈뱅킹 |

> 📸 스크린샷 추가 예정

## 실행 환경

- **Android Studio**: Ladybug 이상
- **Kotlin**: 2.0.0
- **Min SDK**: 26 (Android 8.0)
- **Target SDK**: 35
- **JDK**: 17

## 블로그

개발 과정에서의 기술적 경험과 문제 해결 기록을 블로그에 정리하고 있습니다.

👉 [Still Coding — 기술 블로그](https://still-coding.tistory.com)
