# 어부로또

로또 당첨 결과 조회, QR 코드 당첨 확인, 예상 번호 발급, 뉴스 및 통계를 제공하는 Android 앱

## 문서

제품 요구사항·아키텍처·설계 결정·테스트 전략은 Obsidian의 `Dev/Project/Personal/FisherLotto` 위키에서 관리합니다. 위치는 [AGENTS.md](AGENTS.md)를 따라 확인하고, 현재 코드 버전의 릴리스 작업에는 [릴리스 체크리스트](docs/RELEASE.md)를 사용합니다. 기존 PRD·아키텍처·ADR·테스트 문서는 위키에 통합한 뒤 삭제했습니다.

## 프로젝트 소개

어부로또(Fisher Lotto)는 로또 사용자를 위한 통합 앱입니다.
최신 당첨 번호 확인부터 QR 코드 스캔을 통한 즉시 당첨 확인, AI 예상 번호 발급, 로또 관련 뉴스, 통계 분석, 구독 서비스까지 다양한 기능을 한 곳에서 이용할 수 있습니다.

## 주요 기능

| 기능 | 설명 | 상태 |
|------|------|------|
| **홈 (당첨 정보)** | 최신 회차 당첨 번호 및 등수별 당첨금 조회 + 로또 뉴스 | ✅ |
| **QR 당첨 확인** | 로또 용지의 QR 코드를 카메라로 스캔하여 당첨 여부 즉시 확인 | ✅ |
| **QR 스캔 이력** | 스캔한 복권 QR 이력 및 당첨 결과 로컬 저장 (Room DB) | ✅ |
| **로또 뉴스** | Google News RSS 기반 로또 관련 최신 뉴스 제공 (30분 캐싱) | ✅ |
| **예상 번호** | 주간 예상 번호 발급 (주 1회, 토요일 마감). 무료 10세트, 구독 30세트 | ✅ |
| **당첨 통계** | 회차별 등수 당첨자 수 통계 테이블 (직접 구현한 무한 스크롤) | ✅ |
| **로그인 / 회원가입** | 이메일 로그인 / 회원가입 | ✅ |
| **마이페이지** | 사용자 정보 관리 + 구독 관리 + 회원탈퇴 | ✅ |
| **구독 서비스** | Google Play Billing 기반 구독 결제 — 예상번호 발급 횟수 증가, 지난주 예상번호 당첨 확인 | ✅ |
| **푸시 알림** | Firebase Cloud Messaging 기반 알림 수신 + 구독 만료 D-3 로컬 알림 | ✅ |

## 기술 스택

### 아키텍처

- **다중 모듈 클린 아키텍처** — app / data / domain / presentation 4개 모듈 분리
- **Orbit MVI** 6.1.0 — 단방향 데이터 흐름 (상태와 일회성 효과)
- **Hilt** 2.49 — 의존성 주입 (DI)

### 사용자 인터페이스

- **Jetpack Compose** (BOM 2024.12.01)
- **Material 3**
- **Navigation Compose** 2.8.5 — 하단 내비게이션 기반 5탭 구조
- **Lottie Compose** — 애니메이션

### 네트워크 및 데이터

- **Retrofit** 2.9.0 + **OkHttp** 4.12.0 — REST API 통신 (타임아웃 30초, 로깅 인터셉터)
- **@Named Retrofit** — 서브 백엔드(`https://www.fisherlotto.com:3001/`) 단일 클라이언트
- **Kotlinx Serialization / Gson** — JSON 직렬화 (스캔 이력은 Serialization, API는 Gson)
- **Room** 2.6.1 — 로컬 데이터베이스 (예상번호 발급 이력 + QR 스캔 이력, DB v3)
- **DataStore** 1.1.1 — 사용자 정보 캐시 저장
- **Jsoup** — Google News RSS 파싱

### 카메라 및 머신러닝

- **CameraX** 1.4.1 — 실시간 카메라 프리뷰
- **ML Kit Barcode Scanning** 17.3.0 — QR 코드 인식 및 로또 당첨 결과 파싱

### 결제

- **Google Play Billing** 9.1.0 — 구독 상품 결제, 상태 관리, 구매 복원

### 알림

- **Firebase Cloud Messaging** — 푸시 알림 수신 및 구독 상태 자동 갱신
- **WorkManager** 2.9.0 — 구독 만료 D-3 로컬 알림 스케줄링

### 백엔드 및 인증

- **Firebase** Cloud Messaging / Crashlytics
- **서브 백엔드** — FCM 푸시 프록시 및 구독 검증 서버

> 소셜 로그인(Kakao, Google)은 구현된 적이 없고, 남아 있던 SDK 설정은 2026-08-12에 제거했습니다.
> 인증은 이메일 기반이며 서버 API로 처리합니다. Firebase Auth와 Realtime Database는 사용하지 않습니다.

### 테스트

- **JUnit 4** + **MockK** 1.13.12 — 단위 테스트
- **Turbine** 1.1.0 — Flow 테스트

## 모듈 구조

```
FisherLotto/
├── app/                # Application 클래스, Hilt 설정, Firebase 초기화, FCM 서비스, WorkManager
├── domain/             # 유스케이스·저장소 인터페이스, 모델 정의 (순수 Kotlin)
├── data/               # 유스케이스·API 서비스·저장소 구현, Room DB, 의존성 주입 모듈
├── presentation/       # Compose 화면, ViewModel (Orbit MVI), 내비게이션, 테마
└── gradle/             # 라이브러리 버전 목록 (libs.versions.toml)
```

### 모듈 의존성

```
app → presentation, data, domain
presentation → domain
data → domain
```

`domain` 모듈은 다른 모듈에 대한 의존성이 없는 순수 Kotlin 모듈로, 비즈니스 로직의 독립성을 보장합니다.

## 아키텍처 설계 포인트

- **의존성 역전 원칙(DIP)** — 유스케이스·저장소 인터페이스를 `domain`에 정의, 구현체는 `data`에 위치
- **Hilt @Binds** — 유스케이스 인터페이스와 구현체를 연결 (`LottoModule`, `NewsModule`, `BillingModule`)
- **@Named Retrofit** — 서브 백엔드 HTTPS 클라이언트를 이름으로 주입
- **Orbit MVI** — ViewModel의 상태 관리를 단방향으로 통일하여 예측 가능한 UI 상태 유지
- **Room 마이그레이션** — DB v1 → v2 (scan_history 테이블 추가) → v3 (bestRank 컬럼 추가, matchCount 제거)
- **라이브러리 버전 목록** — `libs.versions.toml`로 라이브러리 버전 중앙 관리

## 화면 구성 (5탭)

| 홈 | QR 당첨 확인 | 예상 번호 | 통계 | 내 정보 |
|:---:|:---:|:---:|:---:|:---:|
| 당첨 번호 + 뉴스 | 카메라 스캔 + 스캔 이력 | 번호 발급 | 회차별 통계 (페이지네이션) | 사용자 + 구독 관리 |

## 실행 환경

- **Android Studio**: Ladybug 이상
- **Kotlin**: 2.0.0
- **AGP**: 8.10.1
- **KSP**: 2.0.0-1.0.24
- **최소 지원 SDK**: 26 (Android 8.0)
- **대상 SDK**: 36
- **JDK**: 17
- **앱 버전**: 0.0.7 (versionCode 7)

## 블로그

개발 과정에서의 기술적 경험과 문제 해결 기록을 블로그에 정리하고 있습니다.

[Still Coding — 기술 블로그](https://still-coding.tistory.com)
