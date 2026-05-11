---
name: android-feature
description: Use when working in FisherLotto and the user asks to add, modify, or plan an Android app feature that may touch domain, data, presentation, or app wiring.
---

# Android Feature Workflow

## Context To Read First

Before proposing or implementing feature work, read:

- `AGENTS.md`
- `docs/PRD.md`
- `docs/ARCHITECTURE.md`
- `docs/ADR.md`
- Relevant existing files in `domain`, `data`, `presentation`, and `app`

## Workflow

1. Identify the feature boundary:
   - domain contract/rule
   - data/provider implementation
   - presentation ViewModel/UI state
   - app-level wiring
2. State assumptions and unresolved product decisions before writing code.
3. Keep steps small. Do not combine unrelated feature, refactor, and release work.
4. Prefer this implementation order when multiple layers are required:
   - domain contract/model/usecase
   - data implementation/mapping
   - presentation ViewModel state
   - Compose UI/navigation
   - app wiring
5. Add or update focused tests for changed behavior.
6. Run the smallest useful Gradle verification command, then broader checks if risk is high.

## Phase Step Template

Use this structure when creating `phases/{phase-name}/stepN.md`:

````markdown
# Step N: kebab-case-name

## Read First

- `/AGENTS.md`
- `/docs/PRD.md`
- `/docs/ARCHITECTURE.md`
- `/docs/ADR.md`
- {relevant existing files}

## Task

{specific implementation task with target files and boundaries}

## Acceptance Criteria

```powershell
.\gradlew.bat {focused-task}
```

## Verification

1. Run the acceptance command.
2. Check module boundaries from `docs/ARCHITECTURE.md`.
3. Update `phases/{phase-name}/index.json` with status and one-line summary.

## Do Not

- Do not change unrelated navigation or architecture. Reason: feature scope must stay reviewable.
- Do not leak provider DTO/entity types into UI/domain. Reason: provider boundaries must remain stable.
- Do not hardcode secrets. Reason: local and production credentials must remain outside source.
````

## Output Expectations

For implementation work, summarize:

- files changed
- behavior changed
- tests/commands run
- remaining risks
