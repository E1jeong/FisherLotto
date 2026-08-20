# domain Module Guide

## Scope

- Pure Kotlin business logic, domain entities, repository interfaces, and usecase contracts.
- Independent lotto number generation/recommendation algorithms, statistical models, and lotto issue calculation.
- Free of any framework, Android SDK, UI, or database dependencies.

## Orient First

- Read first in Wiki: `features/predicted-numbers.md`, `features/statistics.md`, `technical/architecture.md`
- Core source entrypoints:
  - `domain/src/main/java/com/queentech/domain/model/` — pure domain entities (`GetExpectNumber.kt`, `GetLottoNumber.kt`, `User.kt`, `SubscriptionStatus.kt`, etc.)
  - `domain/src/main/java/com/queentech/domain/usecase/lotto/` — `GetExpectNumberUseCase.kt`, `GetLottoNumberUseCase.kt`, `GetLottoStatsUseCase.kt`, `ScanHistoryRepository.kt`, `LottoIssueRepository.kt`
  - `domain/src/main/java/com/queentech/domain/usecase/login/` — `UserRepository.kt`, `GetUserUseCase.kt`, `SignUpUserUseCase.kt`
  - `domain/src/main/java/com/queentech/domain/usecase/billing/` — `BillingRepository.kt`
  - `domain/src/main/java/com/queentech/domain/usecase/fcm/` — `FcmRepository.kt`
  - `domain/src/main/java/com/queentech/domain/usecase/news/` — `GetLotteryNewsUseCase.kt`

## Boundary & Architecture Constraints

- **Pure Kotlin Invariant**: Must remain pure Kotlin. Never import `android.*`, `androidx.*`, `retrofit2.*`, `androidx.room.*`, `androidx.compose.*`, or `dagger.hilt.android.*`.
- **No Upper or Lower Layer Leaks**: Does not know about `data` (no DTOs, no Room entities) or `presentation` (no UI State, no ViewModels).
- **Repository Contracts**: Defines repository interfaces only; implementations live exclusively in `data`.

## Change Gates

1. **Pure Kotlin Enforcement**: Any Android SDK or framework import will fail code review and build checks.
2. **Prediction Wording Policy**: Describe prediction features as recommendation, assistance, or entertainment. Never use wording that guarantees or implies a win in class names, docstrings, or test assertions.
3. **Immutability & Safety**: Domain models should be immutable `data class` with explicit validation.

## Verify

```powershell
.\gradlew.bat :domain:test
```

```bash
./gradlew :domain:test
```