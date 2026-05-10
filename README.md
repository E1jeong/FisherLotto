# Fisher Lotto

로또 당첨 결과 조회, QR 코드 당첨 확인, 예상 번호 발급, 뉴스 및 통계를 제공하는 Android 앱

## 프로젝트 소개

Fisher Lotto는 로또 사용자를 위한 올인원 앱입니다.
최신 당첨 번호 확인부터 QR 코드 스캔을 통한 즉시 당첨 확인, AI 예상 번호 발급, 로또 관련 뉴스, 통계 분석, 구독 서비스까지 다양한 기능을 한 곳에서 이용할 수 있습니다.

## 주요 기능

| 기능 | 설명 | 상태 |
|------|------|------|
| **홈 (당첨 정보)** | 최신 회차 당첨 번호 및 등수별 당첨금 조회 + 로또 뉴스 | ✅ |
| **QR 당첨 확인** | 로또 용지의 QR 코드를 카메라로 스캔하여 당첨 여부 즉시 확인 | ✅ |
| **QR 스캔 이력** | 스캔한 복권 QR 이력 및 당첨 결과 로컬 저장 (Room DB) | ✅ |
| **로또 뉴스** | Google News RSS 기반 로또 관련 최신 뉴스 제공 (30분 캐싱) | ✅ |
| **예상 번호** | 리워드 광고 시청 후 주간 예상 번호 발급 (주 1회, 토요일 마감) | ✅ |
| **당첨 통계** | 회차별 등수 당첨자 수 통계 테이블 (Paging 3 기반 페이지네이션) | ✅ |
| **로그인 / 회원가입** | 이메일 + 소셜 로그인 (Kakao, Google) | ✅ |
| **마이페이지** | 사용자 정보 관리 + 구독 관리 + 회원탈퇴 | ✅ |
| **구독 서비스** | Google Play Billing 기반 구독 결제 — 광고 제거, 예상번호 발급 횟수 증가, 지난주 예상번호 당첨 확인 | ✅ |
| **푸시 알림** | Firebase Cloud Messaging 기반 알림 수신 + 구독 만료 D-3 로컬 알림 | ✅ |

## 기술 스택

### Architecture
- **Multi-Module Clean Architecture** — app / data / domain / presentation 4개 모듈 분리
- **Orbit MVI** 6.1.0 — 단방향 데이터 흐름 (State + SideEffect)
- **Hilt** 2.49 — 의존성 주입 (DI)

### UI
- **Jetpack Compose** (BOM 2024.12.01)
- **Material 3**
- **Navigation Compose** 2.8.5 — Bottom Navigation 기반 5탭 구조
- **Paging 3** 3.3.5 — 통계 탭 페이지네이션
- **Coil** — 이미지 로딩
- **Lottie Compose** — 애니메이션
- **ConstraintLayout Compose**

### Networking & Data
- **Retrofit** 2.9.0 + **OkHttp** 4.12.0 — REST API 통신 (타임아웃 30초, 로깅 인터셉터)
- **@Named 다중 Retrofit** — 메인 서버(`fisherlotto.com:10907`, HTTP) / 서브 서버(`fisherlotto.com:3001`, HTTPS) 분리 관리
- **Kotlinx Serialization / Gson** — JSON 직렬화
- **Room** 2.6.1 — 로컬 데이터베이스 (예상번호 발급 이력 + QR 스캔 이력, DB v3)
- **DataStore** 1.1.1 — 사용자 정보 캐시 저장
- **Jsoup** — Google News RSS 파싱

### Camera & ML
- **CameraX** 1.4.1 — 실시간 카메라 프리뷰
- **ML Kit Barcode Scanning** 17.3.0 — QR 코드 인식 및 로또 당첨 결과 파싱

### Billing & Ads
- **Google Play Billing** 7.1.1 — 구독 상품 결제, 상태 관리, 구매 복원
- **Google AdMob** 23.0.0 — 리워드 광고 (예상 번호 발급 시)

### Notifications
- **Firebase Cloud Messaging** — 푸시 알림 수신 및 구독 상태 자동 갱신
- **WorkManager** 2.9.0 — 구독 만료 D-3 로컬 알림 스케줄링

### Backend & Auth
- **Firebase** Auth / Realtime Database / Cloud Messaging / Analytics / Crashlytics
- **Kakao SDK** 2.20.1 — 카카오 소셜 로그인
- **Google Play Services Auth** — 구글 소셜 로그인
- **Sub Backend (Vercel)** — FCM 푸시 프록시 + 구독 검증 서버

### Testing
- **JUnit 4** + **MockK** 1.13.12 — 단위 테스트
- **Turbine** 1.1.0 — Flow 테스트
- **MockWebServer** — API 테스트

## 모듈 구조

```
FisherLotto/
├── app/                # Application 클래스, Hilt 설정, Firebase/Kakao/AdMob 초기화, FCM 서비스, WorkManager
├── domain/             # UseCase 인터페이스, Repository 인터페이스, Model 정의 (순수 Kotlin)
├── data/               # UseCase 구현, API Service, Repository 구현, Room DB, DI 모듈
├── presentation/       # Compose UI, ViewModel (Orbit MVI), Navigation, Theme
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

- **DIP (의존성 역전 원칙)** — UseCase·Repository 인터페이스를 `domain`에 정의, 구현체는 `data`에 위치
- **Hilt @Binds** — UseCase 인터페이스와 구현체를 연결 (`LottoModule`, `NewsModule`, `BillingModule`)
- **@Named Retrofit** — 메인 서버(HTTP) / 서브 서버(HTTPS) 인스턴스를 분리 관리
- **Orbit MVI** — ViewModel의 상태 관리를 단방향으로 통일하여 예측 가능한 UI 상태 유지
- **Room Migration** — DB v1 → v2 (scan_history 테이블 추가) → v3 (bestRank 컬럼 추가, matchCount 제거)
- **Version Catalog** — `libs.versions.toml`로 라이브러리 버전 중앙 관리

## 화면 구성 (5탭)

| 홈 | QR 당첨 확인 | 예상 번호 | 통계 | 내 정보 |
|:---:|:---:|:---:|:---:|:---:|
| 당첨 번호 + 뉴스 | 카메라 스캔 + 스캔 이력 | 리워드 광고 + 번호 발급 | 회차별 통계 (페이지네이션) | 사용자 + 구독 관리 |

## 실행 환경

- **Android Studio**: Ladybug 이상
- **Kotlin**: 2.0.0
- **AGP**: 8.7.3
- **KSP**: 2.0.0-1.0.24
- **Min SDK**: 26 (Android 8.0)
- **Target SDK**: 35
- **JDK**: 17
- **Version**: 0.0.6 (versionCode 6)

## 블로그

개발 과정에서의 기술적 경험과 문제 해결 기록을 블로그에 정리하고 있습니다.

[Still Coding — 기술 블로그](https://still-coding.tistory.com)
