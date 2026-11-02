# app Module Guide

## Scope

Android application shell, app-level DI, FCM, workers, navigation host, manifest, packaging, and release wiring.

## Orient First

- `app/src/main/java/com/queentech/fisherlotto/`
- `app/src/main/AndroidManifest.xml`

## Boundary & Architecture Constraints

The module assembles the other project modules. Keep business rules and repository queries outside this shell; services and workers delegate through domain contracts.

## Change Gates

- Handle camera and notification permission denial without aborting the app.
- Debug builds sign with the root `fisherlotto.jks`; release builds sign with it only when it exists, so CI builds unsigned and signs in `.github/workflows/deploy.yml`. Treat the keystore, its passwords, and `google-services.json` as machine-dependent configuration and never add their values to Git.

## Verify

```powershell
.\gradlew.bat :app:assembleDebug
```

Use `./gradlew :app:assembleDebug` outside Windows.
