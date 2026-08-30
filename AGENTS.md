# FisherLotto AI Guide

## Context

This is a code-navigation and safety guide, not project history. Product context, decisions, and detailed code tracing live in the vault-relative `Dev/Project/Personal/FisherLotto` wiki; resolve it through `_meta/routing-tables.md` or `obsidian-wiki-sync`, then follow the vault root `AGENTS.md`.

Report plans and results in Korean. Use the nearest module guide before changing a module. The related `lotto-sub-backend` repository is read-only unless a server change is explicitly requested.

## Code Map

| Module | Responsibility | Orient first | Local guide |
| --- | --- | --- | --- |
| `domain/` | Pure models, contracts, and lotto rules | `domain/src/main/java/com/queentech/domain/` | `domain/AGENTS.md` |
| `data/` | Repositories, Retrofit, Room, DataStore, Billing | `data/src/main/java/com/queentech/data/` | `data/AGENTS.md` |
| `presentation/` | Compose UI, Orbit ViewModels, navigation | `presentation/src/main/java/com/queentech/presentation/` | `presentation/AGENTS.md` |
| `app/` | Android shell, DI, FCM, workers, release wiring | `app/src/main/` | `app/AGENTS.md` |

Dependencies: `app -> presentation, data, domain`; `presentation -> domain`; `data -> domain`; `domain` has no project-module dependency.

## Change Gates

- Preserve the dependency direction above; `presentation` never imports `data`.
- Never commit credentials, API keys, signing material, billing tokens, or `google-services.json`.
- Describe number recommendations only as recommendation, assistance, or entertainment; never imply a win.
- Keep `lotto-sub-backend` read-only unless the user explicitly requests server work.

## Verify

Run the narrowest relevant command:

```powershell
.\gradlew.bat :domain:test
.\gradlew.bat :data:test
.\gradlew.bat :presentation:test
.\gradlew.bat test
.\gradlew.bat assembleDebug
```

Use `./gradlew` with the same tasks on Linux, WSL, or macOS.
