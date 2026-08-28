# Architecture Decision Records

## Philosophy

FisherLotto should remain a small, understandable Android app even as it integrates many providers: lotto APIs, QR scanning, local storage, Firebase, billing, ads, and notifications. The architecture should make provider boundaries visible and keep business rules testable.

---

### ADR-001: Multi-module Clean Architecture

**Decision**: Keep the project split into `app`, `presentation`, `domain`, and `data`.

**Reason**: Lotto features touch UI, local data, remote providers, and app services. Module boundaries prevent provider and Android details from spreading into business contracts.

**Tradeoff**: Small changes may require edits across multiple modules. This is acceptable when the boundary keeps testing and review clearer.

---

### ADR-002: Pure Kotlin `domain`

**Decision**: `domain` remains a pure Kotlin module.

**Reason**: Usecases, repository interfaces, and business rules should be testable without Android SDK, provider SDKs, or emulator setup.

**Tradeoff**: Android-specific convenience types must be mapped at the edge instead of reused directly.

---

### ADR-003: Orbit MVI For Presentation State

**Decision**: Use Orbit MVI for ViewModel state and side effects.

**Reason**: Lotto result, scan, billing, login, and notification flows all have explicit loading, success, failure, and one-time event states. Orbit keeps these flows predictable.

**Tradeoff**: Developers must keep state and side effects intentionally separated.

---

### ADR-004: Hilt For Dependency Injection

**Decision**: Use Hilt with KSP for dependency injection.

**Reason**: The app has many provider implementations and module boundaries. DI keeps ViewModels and usecases dependent on contracts rather than concrete implementations.

**Tradeoff**: Bindings must be maintained carefully, especially when moving code between modules.

---

### ADR-005: Room/DataStore For Local Persistence

**Decision**: Use Room for structured local history and DataStore for lightweight preferences/user state.

**Reason**: QR scan history and prediction history need queryable persistence. Preferences and cached flags fit DataStore better.

**Tradeoff**: Room schema changes require migration planning and tests.

---

### ADR-006: Retrofit/OkHttp For Remote APIs

**Decision**: Use Retrofit and OkHttp for backend communication, with named clients where the app talks to distinct hosts.

**Reason**: Existing architecture separates main and sub backend concerns. Keeping client configuration centralized reduces accidental endpoint/key leakage.

**Tradeoff**: DTO mapping and error mapping must be maintained rather than passing raw responses through.

---

### ADR-007: Provider Integrations Stay Behind Boundaries

**Decision**: Billing, Firebase, Kakao, AdMob, CameraX, ML Kit, and RSS parsing should be wrapped or isolated at the appropriate edge.

**Reason**: Provider SDKs are volatile and hard to test directly. Wrapping them keeps UI and domain logic stable.

**Tradeoff**: Wrappers add small upfront cost, but reduce future feature coupling.

---

### ADR-008: Prediction Features Are Not Guarantees

**Decision**: Prediction numbers must be presented as recommendation/support/entertainment, not as guaranteed winning output.

**Reason**: Lotto outcomes are random. Product copy and AI/code changes must avoid misleading users.

**Tradeoff**: Marketing language is more constrained, but user trust and compliance risk are better protected.

---

### ADR-009: A Confirmed New Purchase Resets Its Cache In The Data Layer

**Decision**: After a newly completed purchase has its receipt accepted, `BillingRepositoryImpl` deletes the current week's local expected-number cache. It emits an in-memory event only to refresh an already-open expected-number screen; a later screen entry reads the deleted Room state. Restore and duplicate-receipt paths do not reset the cache.

**Reason**: The receipt API no longer supplies `reissued`, and cache validity is determined by a confirmed new purchase rather than the non-critical `/lotto/1077` side effect. Persisting a separate pending flag duplicates the Room state transition and can leave the app waiting for a response field that no longer exists.

**Tradeoff**: Billing now depends on the domain-level `LottoIssueRepository`. The in-memory event is intentionally not durable, but it carries no correctness burden because the cache deletion is already durable.

---

### ADR-010: Cache Policy Belongs To The Data Layer, Not The ViewModel

**Decision**: Read-through caching lives in the `data` implementation of a usecase, behind a local data source. The `domain` contract exposes only intent — `GetLotteryNewsUseCase(maxResults, query, forceRefresh)` — never the cache mechanism, its TTL, or whether a given call was served from cache. The first case is lottery news: `GetLotteryNewsUseCaseImpl` owns the 30-minute TTL and `NewsLocalDataSource` owns the DataStore keys.

**Reason**: `HomeViewModel` previously held a `SharedPreferences` handle and hand-rolled the TTL check, which put Android storage APIs and a serialization format inside `presentation` and made the cache untestable without Robolectric.

**Tradeoff**: The caller can no longer skip its loading indicator on a cache hit, because it cannot tell a cache hit from a fast network response. A brief spinner on cached loads was accepted rather than leaking cache state back through the contract. Storage also moved from `SharedPreferences` to the shared `user_prefs` DataStore, so any cache written by an older build is abandoned once, not migrated.

---

### ADR-011: Stay On The AGP 8.x Line For The Android 16 Upgrade

**Decision**: `compileSdk`/`targetSdk` 36 is taken with AGP 8.10.1 and Gradle 8.11.1, keeping Kotlin 2.0.0, KSP 2.0.0-1.0.24, Hilt 2.49, Room 2.6.1, and Compose BOM 2024.12.01 unchanged.

**Reason**: AGP 8.10 is the first release whose compatibility table states max API level 36 — the Android 16 setup page says "8.9.0-rc01 or higher", but the AGP 8.9 release notes still state max API 35, so 8.10 is the first version supported without relying on conflicting documentation. AGP 9.x is deliberately avoided: it drops the legacy Variant API that `hilt-android-gradle-plugin` still references, and the Crashlytics Gradle plugin has open crash reports against it.

**Tradeoff**: Kotlin 2.0.0 with Gradle 8.11.1 is outside the Kotlin team's officially tested range, so the combination is unsupported-but-working rather than guaranteed; it was verified by building. Staying on 8.x also means API 36.1 (`platforms;android-36.1`) cannot be used as `compileSdk`, since that requires AGP 8.13.
