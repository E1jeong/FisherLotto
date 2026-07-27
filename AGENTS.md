# Project: FisherLotto

FisherLotto is an Android lotto companion app for lotto result lookup, QR result checking, scan history, prediction number generation, lotto news, statistics, login, subscription, and notifications.

This file is the single source of AI agent instructions for this repository. `CLAUDE.md` only points here.

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

### Prediction Wording

- CRITICAL: Describe prediction features as recommendation, entertainment, or assistance. Never use wording that guarantees or implies a win.
- This applies to UI strings, store listings, documentation, and commit messages alike.

## Read Order Before Work

Read these in order before any behavior change, implementation, or review:

1. `AGENTS.md` (this file) — project overview, architecture rules, module responsibilities, development process, encoding rules, verification commands
2. `docs/PRD.md` — product goals, core user flows, feature scope, product policy
3. `docs/ARCHITECTURE.md` — module dependency direction, layer responsibilities, error handling, test strategy
4. `docs/ADR.md` — architecture decision records

Read additionally depending on the change:

- Test-related work → `docs/TESTING.md`
- Release, build, or signing work → `docs/RELEASE.md`

Working documents for planned migrations and roadmap items are **not** kept in this repository. They live in the Obsidian wiki under `Project/Personal/FisherLotto/`.

## Related Projects

- App project: `<dev-root>\6.project\fisherlotto`
- Server project: `<dev-root>\7.server\lotto-sub-backend`
- From this app repository, the server project is at `..\..\7.server\lotto-sub-backend`.
- Treat the server project as read-only unless the user explicitly asks for server changes.
- When changing API contracts, subscription verification, or FCM behavior, inspect both Android client and server handlers before editing.
- Do not copy server secrets, environment values, tokens, or deployment config into this repository.

## Architecture Rules

Violating any CRITICAL rule below means stopping work and telling the user, not working around it.

| Rule | Action on violation |
| --- | --- |
| `domain` stays pure Kotlin — no Android, Firebase, Retrofit, Room, Compose, or Hilt Android API dependency | Stop implementing, warn |
| `presentation` must not depend on `data` | Stop implementing, warn |
| `data` must not depend on `presentation` | Stop implementing, warn |
| UI state follows Orbit MVI (explicit state / sideEffect / intent) | Require pattern correction |
| Room schema changes require a migration decision | Require a migration plan |

- CRITICAL: `domain` must remain pure Kotlin and must not depend on Android, Firebase, Retrofit, Room, Compose, Hilt Android APIs, or resource files.
- CRITICAL: `presentation` may depend on `domain`, but must not depend on `data`.
- CRITICAL: `data` may depend on `domain` and owns repository/usecase implementations, API services, Room, DataStore, Billing wrappers, and provider adapters.
- CRITICAL: `app` wires application-level setup, DI entry points, Firebase/Kakao/AdMob initialization, WorkManager, and module composition.
- CRITICAL: UI state should follow Orbit MVI patterns: state, side effects, and intents should be explicit and testable.
- CRITICAL: Do not expose API keys, signing values, Kakao keys, billing tokens, auth credentials, provider raw errors, or stack traces in UI, logs, docs, or commits.
- CRITICAL: Do not hardcode local secret values from `local.properties`, signing configs, or `google-services.json`.
- Room schema changes must include a migration decision. Do not silently change persisted tables.
- Version and dependency changes belong in `gradle/libs.versions.toml` unless the existing module pattern requires otherwise.

## Type Boundary Rules

- Network DTOs exist only in `data`.
- Room entities exist only in `data`.
- Domain models exist only in `domain`.
- UI state models exist only in `presentation`.
- Add a mapper at every boundary. Never leak provider or storage types into an upper layer.
- Network DTOs, local entities, domain models, and UI state should not be collapsed into one type unless the scope is truly local and temporary.

## Module Responsibilities

- `domain`: business models, repository interfaces, usecase interfaces/contracts, pure Kotlin business rules.
- `data`: repository implementations, remote/local data sources, API services, DTO/entity mapping, Room, DataStore, Billing integration.
- `presentation`: Compose screens, navigation, ViewModels, Orbit containers, UI state, UI-only models.
- `app`: Android application shell, manifest, app-level initialization, DI aggregation, build config, release packaging.

## Agent Skills

Skills defined in this repository for AI agents:

- `.agents/skills/android-feature/SKILL.md` — feature implementation workflow and Phase Step template
- `.agents/skills/review/SKILL.md` — code review checklist and output format
- `.agents/skills/release-check/SKILL.md` — release validation procedure

## Development Process

- Before implementing behavior changes, read `docs/PRD.md`, `docs/ARCHITECTURE.md`, and `docs/ADR.md`.
- For feature work, identify the affected layer first: domain contract, data implementation, presentation state/UI, or app wiring.
- Prefer small, surgical changes. Do not refactor adjacent code just because it is nearby.
- Add or update tests when behavior changes, especially for usecases, repository mapping, ViewModel state transitions, Room migrations, and provider error mapping.
- If product scope changes, update `docs/PRD.md` before implementation.
- If architecture or technology decisions change, update `docs/ADR.md` before implementation.
- If module ownership or data flow changes, update `docs/ARCHITECTURE.md`.

## Encoding Rules

- Markdown and source files containing Korean text must be read and written as UTF-8.
- On Windows PowerShell, use `Get-Content -Encoding UTF8` when reading Korean Markdown files.
- Do not append to an already mojibake-corrupted document unless the task explicitly requires recovery.
- Prefer creating a new UTF-8 reference document over editing a corrupted historical note.

## Verification Commands

Use the smallest command that verifies the change:

```bash
./gradlew :domain:test
./gradlew :data:test
./gradlew :presentation:test
./gradlew test
./gradlew assembleDebug
```

On Windows PowerShell, use:

```powershell
.\gradlew.bat :domain:test
.\gradlew.bat :data:test
.\gradlew.bat :presentation:test
.\gradlew.bat test
.\gradlew.bat assembleDebug
```

If a command cannot be run because of local SDK, emulator, signing, or network constraints, state that clearly with the attempted command and the reason.

`:app:assembleRelease` availability is machine-dependent: it needs `app/google-services.json`, which is intentionally absent on some machines. When reporting a build failure, say which machine you were on.
