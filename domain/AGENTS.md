# domain Module Guide

## Scope

Pure Kotlin models, repository contracts, use cases, and lotto rules.

## Orient First

- `domain/src/main/java/com/queentech/domain/model/`
- `domain/src/main/java/com/queentech/domain/usecase/`

## Boundary & Architecture Constraints

No Android, UI, networking, persistence, DI, DTO, or entity dependencies. Repository interfaces belong here; their implementations belong in `data`.

## Change Gates

- Keep models immutable unless mutability is required by a verified contract.
- Validate domain inputs explicitly where a rule requires it.

## Verify

```powershell
.\gradlew.bat :domain:test
```

Use `./gradlew :domain:test` outside Windows.
