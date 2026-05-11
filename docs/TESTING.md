# Testing Guide

## Goals

- Verify behavior at the smallest practical layer.
- Keep module boundary violations easy to catch.
- Avoid provider-dependent tests unless the task explicitly requires integration validation.

## Unit Tests

- `domain`: use JUnit and coroutine test utilities for pure business rules and usecases.
- `data`: use fakes, MockK, MockWebServer, and Room in-memory databases for repository and mapper behavior.
- `presentation`: use fake domain contracts, Turbine/coroutine test utilities, and Orbit state/side-effect assertions.

## Android/UI Tests

Use Android instrumentation or Compose UI tests when behavior depends on Android framework, permissions, navigation, or rendered UI interactions.

## Recommended Commands

```powershell
.\gradlew.bat :domain:test
.\gradlew.bat :data:test
.\gradlew.bat :presentation:test
.\gradlew.bat test
.\gradlew.bat assembleDebug
```

Run narrower commands first while developing, then broader commands before finishing risky changes.

## What To Test By Change Type

- Domain rule change: domain unit tests.
- Repository/API mapping change: data unit tests with fake or mocked provider responses.
- Room schema change: migration or database tests.
- ViewModel state change: presentation unit tests for state and side effects.
- Compose interaction change: Compose UI test if the behavior is not covered by ViewModel tests.
- Billing/auth/FCM change: wrapper-level tests where possible, plus manual verification notes if provider validation cannot run locally.

## Reporting Verification

When finishing work, state:

- Commands run.
- Whether they passed.
- Commands skipped and why.
- Any manual verification performed.
