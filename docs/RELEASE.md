# Release Checklist

Use this document when preparing FisherLotto for a build or store-facing release.

## Versioning

- Check `app/build.gradle.kts` `versionCode`.
- Check `app/build.gradle.kts` `versionName`.
- Confirm version changes match the intended release.

## Build

```powershell
.\gradlew.bat clean
.\gradlew.bat test
.\gradlew.bat assembleDebug
.\gradlew.bat assembleRelease
```

Only run release build commands when local signing and SDK setup are available.

## Secrets And Config

- Do not commit secret values from `local.properties`.
- Do not change signing passwords or key aliases through AI edits.
- Treat `google-services.json` and signing files as sensitive project assets.
- Confirm Kakao, Firebase, Billing, and AdMob configuration changes are intentional.

## Runtime Checks

- Latest lotto result loads.
- QR scan opens camera and handles invalid QR data.
- Scan history persists after app restart.
- Prediction number flow respects ad/subscription rules.
- Login state reflects Kakao/Google auth state.
- Subscription state is restored and reflected in UI.
- FCM/local notification behavior is not broken by background restrictions.

## Risk Notes

Before release, explicitly call out changes touching:

- Billing.
- Auth.
- Firebase/FCM.
- Room schema.
- Signing/release config.
- App permissions.
