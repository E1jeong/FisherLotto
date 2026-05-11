---
name: release-check
description: Use when working in FisherLotto and the user asks to prepare, review, or validate an Android release, build, version bump, signing-sensitive change, or store-facing change.
---

# Release Check Workflow

## Context To Read First

- `AGENTS.md`
- `docs/RELEASE.md`
- `docs/ARCHITECTURE.md`
- `app/build.gradle.kts`
- `gradle/libs.versions.toml`

## Checklist

1. Confirm `versionCode` and `versionName`.
2. Check signing config changes carefully.
3. Confirm no secret values are added to source.
4. Identify changes touching Billing, Auth, Firebase, FCM, Room, permissions, or release config.
5. Run or recommend release-appropriate Gradle commands.
6. Report commands that could not be run because of local SDK/signing/provider constraints.

## Output

Summarize:

- release readiness
- commands run
- blocking issues
- risky areas to manually verify
