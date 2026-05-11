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
