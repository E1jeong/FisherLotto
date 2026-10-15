# data Module Guide

## Scope

Repository and use-case implementations (`usecase/`), Retrofit services and DTOs (`model/`), Room, DataStore, Billing, and DTO/entity-to-domain mappers.

## Orient First

- `data/src/main/java/com/queentech/data/usecase/`
- `data/src/main/java/com/queentech/data/database/`
- `data/src/main/java/com/queentech/data/di/`

## Boundary & Architecture Constraints

Depends on `domain` only. Keep DTOs, Room entities, and provider responses inside `data`; map them to domain models at the boundary.

## Change Gates

- Any persisted Room schema change requires bumping `AppDatabase.version` and registering a `Migration` in `di/DatabaseModule.kt`; `exportSchema = false`, so `AutoMigration` is unavailable.
- Billing entitlement and token handling must remain idempotent and provider-verified.

## Verify

```powershell
.\gradlew.bat :data:test
```

Use `./gradlew :data:test` outside Windows.
