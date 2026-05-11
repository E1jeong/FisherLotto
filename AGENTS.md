# Project: FisherLotto

FisherLotto is an Android lotto companion app for lotto result lookup, QR result checking, scan history, prediction number generation, lotto news, statistics, login, subscription, and notifications.

## Tech Stack

- Kotlin 2.0.0
- Android Gradle Plugin 8.7.3
- JDK 17
- Min SDK 26, target SDK 35, compile SDK 35
- Multi-module Clean Architecture: `app`, `presentation`, `domain`, `data`
- Jetpack Compose, Material 3, Navigation Compose
- Orbit MVI 6.1.0
- Hilt 2.49 with KSP
- Retrofit, OkHttp, Room, DataStore, Paging 3
- CameraX, ML Kit Barcode Scanning
- Firebase Auth, Messaging, Crashlytics
- Google Play Billing, AdMob

## Product Goals

- Let users check latest lotto winning numbers and prize information quickly.
- Let users scan lotto QR codes and keep local scan history.
- Provide prediction number generation and related statistics without overpromising certainty.
- Provide lotto-related news and useful app notifications.
- Keep subscription, ads, auth, and notification behavior understandable and safe.

## Related Projects

- App project: `<dev-root>\6.project\fisherlotto`
- Server project: `<dev-root>\7.server\lotto-sub-backend`
- From this app repository, the server project is at `..\..\7.server\lotto-sub-backend`.
- Treat the server project as read-only unless the user explicitly asks for server changes.
- When changing API contracts, subscription verification, or FCM behavior, inspect both Android client and server handlers before editing.
- Do not copy server secrets, environment values, tokens, or deployment config into this repository.

## Architecture Rules

- CRITICAL: `domain` must remain pure Kotlin and must not depend on Android, Firebase, Retrofit, Room, Compose, Hilt Android APIs, or resource files.
- CRITICAL: `presentation` may depend on `domain`, but must not depend on `data`.
- CRITICAL: `data` may depend on `domain` and owns repository/usecase implementations, API services, Room, DataStore, Billing wrappers, and provider adapters.
- CRITICAL: `app` wires application-level setup, DI entry points, Firebase/Kakao/AdMob initialization, WorkManager, and module composition.
- CRITICAL: UI state should follow Orbit MVI patterns: state, side effects, and intents should be explicit and testable.
- CRITICAL: Do not expose API keys, signing values, Kakao keys, billing tokens, auth credentials, provider raw errors, or stack traces in UI, logs, docs, or commits.
- CRITICAL: Do not hardcode local secret values from `local.properties`, signing configs, or `google-services.json`.
- Room schema changes must include a migration decision. Do not silently change persisted tables.
- Network DTOs, local entities, domain models, and UI state should not be collapsed into one type unless the scope is truly local and temporary.
- Version and dependency changes belong in `gradle/libs.versions.toml` unless the existing module pattern requires otherwise.

## Module Responsibilities

- `domain`: business models, repository interfaces, usecase interfaces/contracts, pure Kotlin business rules.
- `data`: repository implementations, remote/local data sources, API services, DTO/entity mapping, Room, DataStore, Billing integration.
- `presentation`: Compose screens, navigation, ViewModels, Orbit containers, UI state, UI-only models.
- `app`: Android application shell, manifest, app-level initialization, DI aggregation, build config, release packaging.

## Development Process

- Before implementing behavior changes, read `docs/PRD.md`, `docs/ARCHITECTURE.md`, and `docs/ADR.md`.
- For feature work, identify the affected layer first: domain contract, data implementation, presentation state/UI, or app wiring.
- Prefer small, surgical changes. Do not refactor adjacent code just because it is nearby.
- Add or update tests when behavior changes, especially for usecases, repository mapping, ViewModel state transitions, Room migrations, and provider error mapping.
- If product scope changes, update `docs/PRD.md` before implementation.
- If architecture or technology decisions change, update `docs/ADR.md` before implementation.
- If module ownership or data flow changes, update `docs/ARCHITECTURE.md`.

## Verification Commands

Use the smallest command that verifies the change:

```bash
./gradlew test
./gradlew assembleDebug
./gradlew :domain:test
./gradlew :data:test
./gradlew :presentation:test
```

On Windows PowerShell, use:

```powershell
.\gradlew.bat test
.\gradlew.bat assembleDebug
```

If a command cannot be run because of local SDK, emulator, signing, or network constraints, state that clearly with the attempted command.
