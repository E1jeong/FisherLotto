# FisherLotto AI Guide

## Start Here

- This guide is a navigation aid and execution safety guard, not a history archive.
- The Obsidian wiki at vault-relative `Dev/Project/Personal/FisherLotto` is the single source of truth for business context, product rules, server integration analysis, roadmap, and decision history. Resolve the vault through `_meta/routing-tables.md` or `obsidian-wiki-sync`, never a hardcoded file URL.
- Before resuming work or making non-trivial changes, follow the mandatory read order:
  1. `README.md` (Wiki entrypoint)
  2. `handoff.md` (Current state and unresolved blockers)
  3. `schema.md` (Project maintenance and evidence rules)
  4. `index.md` (Task navigation)
- Read `issues/needs-verification.md` when the task touches an unsettled claim or verification gap.
- Before multi-step or resumed implementation, ground the wiki context against live code, propose `step → verify` checkpoints, and confirm them before editing.
- Before proposing or executing changes, communicate in Korean. Explain command rationale and report completion status in Korean.
- Read the nearest module `AGENTS.md` before changing any module.
- Repository locations: company `C:\Users\Unionbiometrics\Desktop\dev\1.project\FisherLotto`; main `C:\Users\sumas\OneDrive\Desktop\dev\6.project\fisherlotto`.
- Related backend `lotto-sub-backend` is read-only unless server changes are explicitly requested.

## Product and Runtime/Pipeline Map

FisherLotto is an Android lotto companion app built with Multi-module Clean Architecture, Jetpack Compose, and Orbit MVI.

```text
       ┌─────────────────────────────────────────────────────────┐
       │                       app/ Module                       │
       │     (Hilt DI, Application Shell, FCM, WorkManager)      │
       └──────────────┬───────────────────────────┬──────────────┘
                      │                           │
                      ▼                           ▼
        ┌───────────────────────────┐   ┌───────────────────────────┐
        │   presentation/ Module    │   │       data/ Module        │
        │ (Compose UI, ViewModels,  │   │  (Room DB, Retrofit API,  │
        │   Orbit MVI, Navigation)  │   │   Play Billing, DataStore)│
        └─────────────┬─────────────┘   └─────────────┬─────────────┘
                      │                               │
                      │   ┌───────────────────────┐   │
                      └──►│    domain/ Module     │◄──┘
                          │ (Pure Kotlin Models,  │
                          │   UseCases, Contracts)│
                          └───────────────────────┘
```

## Module Map and First Reads

| Module | Guide | Ownership & Responsibility | First Source Entry Point | Related Wiki Topics |
| :--- | :--- | :--- | :--- | :--- |
| `domain/` | [`domain/AGENTS.md`](domain/AGENTS.md) | Pure Kotlin business models, UseCases, lotto algorithms | `domain/src/main/java/com/queentech/domain/usecase/lotto/GetExpectNumberUseCase.kt` | `features/predicted-numbers.md`, `technical/architecture.md` |
| `data/` | [`data/AGENTS.md`](data/AGENTS.md) | Repository implementations, Room DB, Retrofit APIs, Billing | `data/src/main/java/com/queentech/data/database/AppDatabase.kt`, `data/src/main/java/com/queentech/data/usecase/billing/BillingRepositoryImpl.kt` | `data/external-data.md`, `server/API.md`, `features/payments-and-subscriptions.md` |
| `presentation/` | [`presentation/AGENTS.md`](presentation/AGENTS.md) | Jetpack Compose UI, Orbit MVI ViewModels, Navigation | `presentation/src/main/java/com/queentech/presentation/main/home/HomeScreen.kt` | `UI/screen-layout.md`, `features/home.md`, `features/qr-winning-verification.md` |
| `app/` | [`app/AGENTS.md`](app/AGENTS.md) | Application shell, Hilt DI root, FCM Service, WorkManager | `app/src/main/java/com/queentech/fisherlotto/MainActivity.kt`, `app/src/main/java/com/queentech/fisherlotto/App.kt` | `features/FCM.md`, `operations/google-play-store.md`, `docs/RELEASE.md` |

## Task Router

| Request Concerns | Read First in Wiki | First Source Path | Then Trace |
| :--- | :--- | :--- | :--- |
| **Home & Lottery News** | `features/home.md` | `presentation/src/main/java/com/queentech/presentation/main/home/HomeScreen.kt` | sibling `HomeViewModel.kt` → `domain/src/main/java/com/queentech/domain/usecase/news/GetLotteryNewsUseCase.kt` → `data/src/main/java/com/queentech/data/usecase/news/GetLotteryNewsUseCaseImpl.kt` |
| **Lotto Number Recommendation** | `features/predicted-numbers.md` | `domain/src/main/java/com/queentech/domain/usecase/lotto/GetExpectNumberUseCase.kt` | `presentation/src/main/java/com/queentech/presentation/main/expect_number/ExpectNumberViewModel.kt` → `ExpectNumberScreen.kt` |
| **QR Code Scan & History** | `features/qr-winning-verification.md` | `presentation/src/main/java/com/queentech/presentation/main/camera/CameraScreen.kt` | sibling `CameraViewModel.kt` → `domain/src/main/java/com/queentech/domain/usecase/lotto/ScanHistoryRepository.kt` → `data/src/main/java/com/queentech/data/usecase/lotto/ScanHistoryRepositoryImpl.kt` |
| **In-App Billing & Subscriptions** | `features/payments-and-subscriptions.md`<br>`../lotto-sub-backend/issues/payment-gaps.md` | `data/src/main/java/com/queentech/data/usecase/billing/BillingRepositoryImpl.kt` | sibling `BillingClientWrapper.kt` → `domain/src/main/java/com/queentech/domain/usecase/billing/BillingRepository.kt` → Server Receipt API |
| **Push Notifications (FCM)** | `features/FCM.md` | `app/src/main/java/com/queentech/fisherlotto/FisherLottoMessagingService.kt` | `data/src/main/java/com/queentech/data/usecase/fcm/FcmRepositoryImpl.kt` (Token sync) |
| **User Login & Account Lifecycle** | `features/login-and-membership.md`<br>`roadmap/email-verification-and-account-recovery.md` | `presentation/src/main/java/com/queentech/presentation/login/LoginViewModel.kt` | `domain/src/main/java/com/queentech/domain/usecase/login/GetUserUseCase.kt` → `data/src/main/java/com/queentech/data/usecase/login/UserRepositoryImpl.kt` |
| **Local Database & Room Schema** | `data/external-data.md` | `data/src/main/java/com/queentech/data/database/AppDatabase.kt` | `data/src/main/java/com/queentech/data/database/dao/` → Room Entities & Migrations |
| **Statistics & Chart Analysis** | `features/statistics.md` | `presentation/src/main/java/com/queentech/presentation/main/statistic/StatisticViewModel.kt` | `domain/src/main/java/com/queentech/domain/usecase/lotto/GetLottoStatsUseCase.kt` |

## Immutable Boundaries and Change Gates

1. **Domain Purity**: `domain` must remain 100% pure Kotlin. No Android, Firebase, Retrofit, Room, Compose, or Hilt Android imports.
2. **Layer Isolation**: `presentation` must never depend on `data`; `data` must never depend on `presentation`.
3. **Prediction Wording Policy (CRITICAL)**: Describe prediction features as recommendation, entertainment, or assistance. **Never use wording that guarantees or implies a win** in UI strings, logs, tests, docstrings, or commit messages.
4. **Secret Protection**: Never commit API keys, signing keystores, billing tokens, `google-services.json`, or credentials.
5. **Orbit MVI Integrity**: Every declared `sideEffect` must be collected in its Screen. Keystroke inputs must use `rememberSaveable` + `blockingIntent`.
6. **Room Migration Safety**: Persisted Room table modifications require an explicit migration plan (`AutoMigration` or `Migration`).
7. **Read-Only Server Boundary**: `lotto-sub-backend` is read-only unless server changes are explicitly requested.

## Build and Verification

### Windows PowerShell

```powershell
# Module tests
.\gradlew.bat :domain:test
.\gradlew.bat :data:test
.\gradlew.bat :presentation:test

# Full test suite & debug build
.\gradlew.bat test
.\gradlew.bat assembleDebug
```

### Linux / WSL / macOS

```bash
# Module tests
./gradlew :domain:test
./gradlew :data:test
./gradlew :presentation:test

# Full test suite & debug build
./gradlew test
./gradlew assembleDebug
```
