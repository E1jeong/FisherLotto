# app Module Guide

## Scope

- Android application shell and entry points (`MainActivity.kt`, `App.kt`).
- Application-level Dependency Injection (`@HiltAndroidApp`, `AppModule.kt`).
- Firebase Cloud Messaging integration (`FisherLottoMessagingService.kt`).
- Background workers (`SubscriptionExpiryWorker.kt`).
- Top-level navigation hosting (`NavigationHost.kt`, `NavigationBottomBar.kt`).
- AndroidManifest, app packaging, signing, and release configuration.

## Orient First

- Read first in Wiki: `features/FCM.md`, `operations/google-play-store.md`, `operations/oauth-and-keystore.md`, `docs/RELEASE.md`
- Core source entrypoints:
  - `app/src/main/java/com/queentech/fisherlotto/MainActivity.kt` — main entry activity
  - `app/src/main/java/com/queentech/fisherlotto/App.kt` — application class (Hilt, Firebase, AdMob init)
  - `app/src/main/java/com/queentech/fisherlotto/FisherLottoMessagingService.kt` — FCM push receiver
  - `app/src/main/java/com/queentech/fisherlotto/worker/SubscriptionExpiryWorker.kt` — WorkManager periodic subscription checker
  - `app/src/main/java/com/queentech/fisherlotto/navigation/NavigationHost.kt` — compose navigation host
  - `app/src/main/java/com/queentech/fisherlotto/AppModule.kt` — application-level DI module
  - `app/src/main/AndroidManifest.xml` — app manifest and permissions

## Boundary & Architecture Constraints

- **Assembly Role**: Aggregates `presentation`, `domain`, and `data`.
- **No Business Logic**: Must not contain domain logic, calculation algorithms, or repository queries directly.
- **Service Isolation**: Background workers and push services must delegate business operations to Domain UseCases.

## Change Gates

1. **No Business Logic in Shell**: All domain actions must flow through injected UseCases from `domain`.
2. **Secret & Key Protection**: Never commit `google-services.json`, keystores, or raw API secrets to Git.
3. **Graceful Permission Handling**: Camera and Notification permission flows must handle user denial gracefully without application abort.
4. **Machine-Dependent Release**: Release builds require machine-specific signing and `google-services.json`.

## Verify

```powershell
.\gradlew.bat :app:assembleDebug
```

```bash
./gradlew :app:assembleDebug
```