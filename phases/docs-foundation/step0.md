# Step 0: ai-docs-baseline

## Read First

- `/README.md`
- `/settings.gradle.kts`
- `/build.gradle.kts`
- `/app/build.gradle.kts`
- `/data/build.gradle.kts`
- `/domain/build.gradle.kts`
- `/presentation/build.gradle.kts`
- `/gradle/libs.versions.toml`

## Task

Create the AI-facing documentation baseline for FisherLotto without changing app code:

- `/AGENTS.md`
- `/docs/PRD.md`
- `/docs/ARCHITECTURE.md`
- `/docs/ADR.md`
- `/docs/TESTING.md`
- `/docs/RELEASE.md`
- `/.agents/skills/android-feature/SKILL.md`
- `/.agents/skills/review/SKILL.md`
- `/.agents/skills/release-check/SKILL.md`
- `/phases/index.json`
- `/phases/docs-foundation/index.json`
- `/phases/docs-foundation/step0.md`

## Acceptance Criteria

```powershell
rg --hidden --files -g "AGENTS.md" -g "docs/**" -g ".agents/**" -g "phases/**"
```

## Verification

1. Confirm the files exist.
2. Confirm no app source, Gradle, signing, or config values were changed.
3. Confirm the docs describe the current Android/Kotlin multi-module architecture.

## Do Not

- Do not modify production app code. Reason: this phase is documentation-only.
- Do not change Gradle dependencies or versions. Reason: no runtime behavior is being changed.
- Do not add local secret values. Reason: documentation must be safe to commit.
