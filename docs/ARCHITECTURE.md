# Architecture

## Overview

FisherLotto is a Kotlin Android app organized as a four-module Clean Architecture project. The project separates Android app wiring, UI state, business contracts, and data/provider implementations so feature work can be localized and reviewed against clear boundaries.

## Modules

```text
FisherLotto/
|-- app/           # Android application shell, app-level setup, DI aggregation, release config
|-- presentation/  # Compose UI, navigation, ViewModels, Orbit MVI state/effects
|-- domain/        # Pure Kotlin models, repository interfaces, usecase contracts/business logic
|-- data/          # Repository implementations, API/Room/DataStore/Billing/provider adapters
`-- gradle/        # Version catalog
```

## Dependency Direction

```text
app -> presentation, data, domain
presentation -> domain
data -> domain
domain -> no project module dependency
```

Rules:

- `domain` must stay Android-free and provider-free.
- `presentation` must not import from `data`.
- `data` must not import from `presentation`.
- `app` should compose modules and initialize app-level services, not hold business logic.

## Feature Flow

Typical feature flow:

```text
Compose Screen
-> Orbit ViewModel
-> domain UseCase / Repository Interface
-> data Repository Implementation
-> Remote API / Room / DataStore / Billing / Firebase
```

The reverse direction should happen through mapped return values, flows, or domain results, not by exposing provider-specific objects to UI.

## Layer Responsibilities

### `domain`

- Domain models and value objects.
- Repository interfaces.
- Usecase interfaces and pure business contracts.
- Business rules that do not require Android or provider SDKs.
- Unit tests for business behavior.

### `data`

- Retrofit services and DTOs.
- OkHttp configuration.
- Room entities, DAO, database, and migrations.
- DataStore access.
- Repository and usecase implementations where the existing feature pattern places implementation in `data`.
- Billing, RSS, and provider wrappers.
- Mapping between DTO/entity/provider types and domain types.

### `presentation`

- Compose screens and components.
- Navigation Compose graph.
- Orbit ViewModels.
- UI state and side effects.
- UI-only formatting and display models.
- ViewModel tests for state and side-effect transitions.

### `app`

- Application class and manifest-level wiring.
- Firebase, Kakao, AdMob, WorkManager, and process-level initialization.
- Hilt entry setup and module aggregation where needed.
- Build config, signing config, app version, release packaging.

## State Management

- Orbit MVI is the default presentation pattern.
- State should represent what the screen renders.
- Side effects should represent one-time events such as toast, navigation, permission prompts, or external UI triggers.
- ViewModels should depend on domain contracts, not concrete data implementations.

## Data Boundaries

- Keep network DTOs in `data`.
- Keep Room entities in `data`.
- Keep domain models in `domain`.
- Keep UI state models in `presentation`.
- Add mappers at boundaries instead of leaking provider or storage types upward.

## Provider Boundaries

- Retrofit and OkHttp calls belong in `data`.
- Room and DataStore access belong in `data`.
- Firebase/Auth/FCM wrappers should be isolated behind data/app boundaries.
- Google Play Billing should be wrapped so UI observes domain-level subscription state.
- CameraX and ML Kit are UI-adjacent and should stay in presentation/app-facing code unless a pure parser can be moved to domain.

## Error Handling

- Provider raw errors should be mapped before reaching UI.
- UI should receive user-safe messages or domain-level failure types.
- Do not expose secrets, raw stack traces, HTTP internals, billing tokens, or auth credentials.
- For unknown billing/auth states, prefer conservative behavior and clear UI state.

## Testing Strategy

- `domain`: pure unit tests for usecases and rules.
- `data`: repository tests with fake data sources, MockWebServer for APIs, Room in-memory tests for persistence/migrations.
- `presentation`: ViewModel tests with fake usecases/repositories, Orbit state and side-effect assertions, Compose tests where UI behavior is important.
- `app`: smoke build and integration checks for wiring-sensitive changes.

## Verification

Use focused Gradle commands when possible:

```bash
./gradlew :domain:test
./gradlew :data:test
./gradlew :presentation:test
./gradlew test
./gradlew assembleDebug
```

For Windows PowerShell:

```powershell
.\gradlew.bat :domain:test
.\gradlew.bat :data:test
.\gradlew.bat :presentation:test
.\gradlew.bat test
.\gradlew.bat assembleDebug
```
