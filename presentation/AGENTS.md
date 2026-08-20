# presentation Module Guide

## Scope

- UI screens built with Jetpack Compose and Material 3.
- ViewModels powered by Orbit MVI 6.1.0 (`ContainerHost`).
- Navigation Compose graph and destination route bindings.
- UI state models, event handling, dialogs, custom components, and themes.

## Orient First

- Read first in Wiki: `UI/screen-layout.md`, `features/home.md`, `features/qr-winning-verification.md`, `features/predicted-numbers.md`, `features/login-and-membership.md`
- Core source entrypoints:
  - `presentation/src/main/java/com/queentech/presentation/main/home/` — `HomeScreen.kt`, `HomeViewModel.kt`
  - `presentation/src/main/java/com/queentech/presentation/main/expect_number/` — `ExpectNumberScreen.kt`, `ExpectNumberViewModel.kt`
  - `presentation/src/main/java/com/queentech/presentation/main/camera/` — `CameraScreen.kt`, `CameraViewModel.kt`, `LottoQrResult.kt`
  - `presentation/src/main/java/com/queentech/presentation/main/statistic/` — `StatisticScreen.kt`, `StatisticViewModel.kt`
  - `presentation/src/main/java/com/queentech/presentation/main/mypage/` — `MyPageScreen.kt`, `MyPageViewModel.kt`
  - `presentation/src/main/java/com/queentech/presentation/login/` — `LoginScreen.kt`, `LoginViewModel.kt`, `SignUpScreen.kt`, `SignUpViewModel.kt`
  - `presentation/src/main/java/com/queentech/presentation/component/` — Reusable dialogs, buttons, and text fields

## Boundary & Architecture Constraints

- **Dependency Direction**: Depends on `domain`. Must **never** depend on `data`.
- **MVI Architecture**: Follows Orbit MVI (`State`, `SideEffect`, `Intent`).
- **UI State Ownership**: ViewModels manage UI state transitions; composables are reactive renderers.

## Change Gates

1. **SideEffect Collection Mandate**: Every declared `sideEffect` must be explicitly collected by its corresponding Screen composable (`collectSideEffect`). An uncollected sideEffect silently drops user-facing notifications or error dialogs.
2. **Compose TextField State Rule**: Text fields hold their input value in `rememberSaveable`, not in Orbit state. Keystroke handlers that must reach the container must use `blockingIntent` to prevent character reordering/dropping.
3. **No Direct Data Layer Access**: Never import Repository implementations, Room entities, or Retrofit DTOs.
4. **Prediction Wording Policy**: UI strings, placeholders, and tooltips must use recommendation, assistance, or entertainment terms only. Never imply or promise a winning outcome.
5. **Email-Only Auth**: Do not wire social login buttons to SDKs; `SocialLoginButton.kt` is a detached UI shell.

## Verify

```powershell
.\gradlew.bat :presentation:test
```

```bash
./gradlew :presentation:test
```