# presentation Module Guide

## Scope

Compose UI, Orbit MVI ViewModels, navigation, UI state, dialogs, and reusable components.

## Orient First

- `presentation/src/main/java/com/queentech/presentation/main/`
- `presentation/src/main/java/com/queentech/presentation/login/`
- `presentation/src/main/java/com/queentech/presentation/component/`

## Boundary & Architecture Constraints

Depends on `domain`, never `data`. ViewModels own state transitions; composables render state and send intents.

## Change Gates

- Collect every declared Orbit `sideEffect` in its corresponding screen.
- Keep text input in `rememberSaveable`; use `blockingIntent` when a keystroke must reach the Orbit container.
- Do not wire detached social-login UI shells to provider SDKs.

## Verify

```powershell
.\gradlew.bat :presentation:test
```

Use `./gradlew :presentation:test` outside Windows.
