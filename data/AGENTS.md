# data Module Guide

## Scope

- Repository implementations (`*RepositoryImpl.kt` or `*UseCaseImpl.kt`) fulfilling domain contracts.
- Remote network data sources and Retrofit API services (`LottoService.kt`, `UserService.kt`, `BillingService.kt`, `FcmService.kt`, `NewsService.kt`).
- Local persistence: Room database (`AppDatabase.kt`), DAO, and DataStore data sources (`UserLocalDataSource.kt`, etc.).
- Google Play Billing client integration (`BillingClientWrapper.kt`, `BillingRepositoryImpl.kt`).
- Boundary mappers: DTO ↔ Domain Model, Room Entity ↔ Domain Model.

## Orient First

- Read first in Wiki: `data/external-data.md`, `server/API.md`, `features/payments-and-subscriptions.md`, `technical/architecture.md`
- Core source entrypoints:
  - `data/src/main/java/com/queentech/data/usecase/login/UserRepositoryImpl.kt`, `GetUserUseCaseImpl.kt`, `SignUpUserUseCaseImpl.kt`
  - `data/src/main/java/com/queentech/data/usecase/lotto/GetExpectNumberUseCaseImpl.kt`, `GetLottoNumberUseCaseImpl.kt`, `GetLottoStatsUseCaseImpl.kt`, `ScanHistoryRepositoryImpl.kt`
  - `data/src/main/java/com/queentech/data/usecase/billing/BillingRepositoryImpl.kt`, `BillingClientWrapper.kt`
  - `data/src/main/java/com/queentech/data/usecase/fcm/FcmRepositoryImpl.kt`
  - `data/src/main/java/com/queentech/data/usecase/news/GetLotteryNewsUseCaseImpl.kt`
  - `data/src/main/java/com/queentech/data/database/AppDatabase.kt` — Room database definition and entities
  - `data/src/main/java/com/queentech/data/database/datastore/` — `UserLocalDataSource.kt`, `FcmLocalDataSource.kt`
  - `data/src/main/java/com/queentech/data/di/` — Hilt modules (`RetrofitModule.kt`, `RepositoryModule.kt`, `DatabaseModule.kt`, `DataSourceModule.kt`, `BillingModule.kt`)

## Boundary & Architecture Constraints

- **Dependency Direction**: Depends on `domain`. Must **never** depend on `presentation`.
- **Type Isolation**: Network DTOs and Room entities exist only in `data`. Always map to pure domain models before returning to domain or presentation.
- **Single Source of Truth**: Room for structured historical/scanned lotto records, DataStore (`user_prefs`) for user session, FCM token, and billing sync state.

## Change Gates

1. **Room Migration Mandate**: Schema changes to Room entities require an explicit migration decision (`AutoMigration` or manual `Migration`). Never silently alter persisted database tables.
2. **Mapper Required at Boundaries**: Never leak Retrofit DTOs, Room Entities, or Billing response objects into upper layers.
3. **No Hardcoded Secrets or Endpoints**: All API base URLs and keys must come from BuildConfig/environment or secure configuration.
4. **Billing Entitlement Safety**: Subscription state and token validations must follow idempotent verification.

## Verify

```powershell
.\gradlew.bat :data:test
```

```bash
./gradlew :data:test
```
