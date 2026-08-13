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

## Planning Documents

- Do not create a repository-local `phases/` tree. Planned migrations and roadmap work belong in the FisherLotto Obsidian project wiki, as required by `AGENTS.md`.
- Write implementation plans, task documents, and walkthroughs in Korean.
- Keep acceptance criteria concrete and include the smallest Gradle command that verifies the planned behavior.

## Output Expectations

For implementation work, summarize:

- files changed
- behavior changed
- tests/commands run
- remaining risks
