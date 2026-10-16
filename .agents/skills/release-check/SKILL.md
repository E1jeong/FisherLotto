---
name: release-check
description: Use when working in FisherLotto and the user asks to prepare, review, or validate an Android release, build, version bump, signing-sensitive change, or store-facing change.
---

# Release Check Workflow

## Context To Read First

- `AGENTS.md`
- `app/build.gradle.kts`
- `gradle/libs.versions.toml`

Resolve the wiki through root guidance. Read only release-relevant policy or feature pages; product and architecture history are not mandatory release inputs.

## Checklist

1. Confirm `versionCode` and `versionName`.
2. Check signing config changes carefully; never change signing passwords or key aliases through AI edits.
3. Confirm no secret values are added to source.
4. Identify changes touching Billing, Auth, Firebase, FCM, Room, permissions, or release config.
5. Run or recommend release-appropriate Gradle commands.
6. Report commands that could not be run because of local SDK/signing/provider constraints.

## Runtime Checks

Verify on a device before a store-facing release:

- Latest lotto result loads.
- QR scan opens camera and handles invalid QR data.
- Scan history persists after app restart.
- Prediction number flow issues free sets without ads and paid sets for subscribers.
- Play Console Ads / Data Safety / privacy text match the no-ad, no-`AD_ID` binary before that AAB is published.
- Email login state is reflected in UI and survives restart.
- Subscription state is restored and reflected in UI.
- FCM/local notification behavior is not broken by background restrictions.

## Output

Summarize:

- release readiness
- commands run
- blocking issues
- risky areas to manually verify
