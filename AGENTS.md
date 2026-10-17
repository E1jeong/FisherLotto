# FisherLotto AI Guide

## Context

This is a code-navigation and safety guide, not project history. Product context, decisions, and detailed code tracing live in the vault-relative `Dev/Project/Personal/FisherLotto` wiki; resolve it through `_meta/routing-tables.md` or `obsidian-wiki-sync`, then follow the vault root `AGENTS.md`.

Keep the owner-facing `README.md` in Korean, preserving code identifiers. Use the nearest module guide before changing a module.

Repository skills: `.agents/skills/<name>/SKILL.md` (`android-feature`, `release-check`, `review`); if one matches the task and is not in your skill catalog, read it before starting.

## Code Map

| Module | Responsibility | Orient first | Local guide |
| --- | --- | --- | --- |
| `domain/` | Pure models and repository/use-case contracts | `domain/src/main/java/com/queentech/domain/` | `domain/AGENTS.md` |
| `data/` | Repositories, Retrofit, Room, DataStore, Billing | `data/src/main/java/com/queentech/data/` | `data/AGENTS.md` |
| `presentation/` | Compose UI screens and Orbit ViewModels | `presentation/src/main/java/com/queentech/presentation/` | `presentation/AGENTS.md` |
| `app/` | Android shell, DI, navigation graph, FCM, workers, signing | `app/src/main/` | `app/AGENTS.md` |

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
