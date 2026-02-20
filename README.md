# 🎱 Fisher Lotto

로또 당첨 결과 조회, QR 코드 당첨 확인, 뉴스 및 통계를 제공하는 Android 앱

## 프로젝트 소개

Fisher Lotto는 로또 사용자를 위한 올인원 앱입니다.
최신 당첨 번호 확인부터 QR 코드 스캔을 통한 즉시 당첨 확인, 로또 관련 뉴스까지 한 곳에서 이용할 수 있습니다.

## 주요 기능

| 기능 | 설명 | 상태 |
|------|------|------|
| **홈 (당첨 정보)** | 최신 회차 당첨 번호 및 등수별 당첨금 조회 | ✅ |
| **QR 당첨 확인** | 로또 용지의 QR 코드를 카메라로 스캔하여 당첨 여부 즉시 확인 | ✅ |
| **로또 뉴스** | Google News RSS 기반 로또 관련 최신 뉴스 제공 (30분 캐싱) | ✅ |
| **당첨 통계** | 회차별 등수 당첨자 수 통계 테이블 | ✅ |
| **로그인 / 회원가입** | 이메일 기반 로그인 및 소셜 로그인 (Kakao, Google) | ✅ |
| **마이페이지** | 사용자 정보 관리 | ✅ |
| **예상 번호** | 번호 생성 기능 | 🚧 |

## 기술 스택

### Architecture
- **Multi-Module Clean Architecture** — app / data / domain / presentation 분리
- **Orbit MVI** — 단방향 데이터 흐름 (State + SideEffect)

### UI
- **Jetpack Compose** (BOM 2024.12.01)
- **Material 3**
- **Navigation Compose** — Bottom Navigation 기반 5탭 구조
- **Coil** — 이미지 로딩
- **Lottie Compose** — 애니메이션
- **ConstraintLayout Compose**

### Networking & Data
- **Retrofit + OkHttp** — REST API 통신
- **Kotlinx Serialization / Gson** — JSON 직렬화
- **Room** — 로컬 데이터베이스
- **DataStore** — 설정 및 캐시 저장
- **Jsoup** — Google News RSS 파싱

### DI & State
- **Hilt** — 의존성 주입
- **Orbit MVI** — ViewModel 상태 관리 (State, SideEffect)

### Camera & ML
- **CameraX** — 카메라 프리뷰
- **ML Kit Barcode Scanning** — QR 코드 인식 및 파싱

### Firebase
- Firebase Auth / Realtime Database / Cloud Messaging / Analytics

### Auth
- **Kakao SDK** — 카카오 소셜 로그인
- **Google Play Services Auth** — 구글 소셜 로그인

### Testing
- **JUnit** + **MockK** — 단위 테스트
- **Turbine** — Flow 테스트
- **MockWebServer** — API 테스트
- **Compose UI Test** — UI 테스트

## 모듈 구조

```
FisherLotto/
├── app/                # Application 클래스, Hilt 설정, Firebase 초기화
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

## 화면 구성

| 홈 | QR 당첨 확인 | 통계 | 로그인 |
|:---:|:---:|:---:|:---:|
| 당첨 번호 + 뉴스 | 카메라 스캔 | 회차별 통계 | 이메일 + 소셜 |

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
