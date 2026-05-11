# PRD: FisherLotto

## Goal

FisherLotto helps Korean lotto users check results, scan tickets, review history, generate prediction numbers, read lotto news, inspect statistics, and manage subscription-related benefits from one Android app.

## Users

- Primary users are lotto buyers who want to check results quickly after purchasing tickets.
- Some users repeatedly scan tickets and want a local record of past checks.
- Some users want lightweight prediction support, but the app must avoid implying guaranteed wins.
- Paid users expect subscription benefits such as ad removal or increased prediction-related usage to be reliable.

## Problems To Solve

- Users need a fast path from ticket QR scan to result confirmation.
- Users need latest draw information without manually searching.
- Users need scan and prediction history to remain understandable across app sessions.
- Lotto-related statistics should be accessible without overwhelming casual users.
- Auth, subscription, ads, and notifications can easily become confusing if their states are not explicit.

## Core Value

- Confirm lotto results quickly.
- Preserve useful local history for scanned tickets and generated predictions.
- Keep prediction features framed as entertainment/support, not certainty.
- Make subscription, ad, notification, and login states predictable.
- Keep the app useful even when provider calls fail, where local data is available.

## Core User Flows

1. User opens the app and checks latest winning numbers, rank information, and lotto news.
2. User scans a lotto QR code with the camera and sees whether the ticket won.
3. User reviews previously scanned QR tickets from local history.
4. User watches an ad or uses subscription benefits to generate prediction numbers.
5. User browses draw statistics with paginated historical data.
6. User logs in with Kakao or Google and manages account/subscription state.
7. User receives FCM or local notification updates where enabled.

## Major Features

1. Latest draw result
   - Winning numbers, bonus number, prize tiers, and related news.
2. QR result check
   - CameraX preview, ML Kit barcode recognition, lotto QR parsing, and result display.
3. QR scan history
   - Room-backed local history for scanned tickets and results.
4. Prediction numbers
   - Weekly prediction number generation with ad/subscription constraints.
5. Lotto news
   - Google News RSS parsing and cached display.
6. Statistics
   - Draw-by-draw winning number statistics with Paging 3.
7. Login and account
   - Kakao and Google social login, user profile, withdrawal, and state management.
8. Subscription
   - Google Play Billing subscription, purchase restoration, and benefit state.
9. Notifications
   - Firebase Cloud Messaging and local reminders for subscription-related events.

## Success Criteria

- A user can check latest results from the first screen without navigating through marketing content.
- QR scanning leads to a clear result or a clear failure reason.
- Local scan and prediction history remain available after app restart.
- Subscription state is reflected consistently across UI, ads, and prediction limits.
- Provider failures do not expose raw provider errors or secrets.
- Core module boundaries remain intact while adding features.

## Out Of Scope Until Explicitly Requested

- Guaranteed winning prediction claims.
- Server-side persistence of all user history unless a backend feature is explicitly planned.
- Broad redesigns that change navigation or architecture without a phase plan.
- Replacing Orbit MVI, Hilt, Room, Retrofit, or the multi-module architecture without an ADR.
- Secret rotation or production credential changes through AI-generated edits.

## Product Policies

- Prediction features must be worded as recommendation, entertainment, or support.
- User-facing errors should explain the next action rather than provider internals.
- Paid benefit behavior must be testable and conservative when billing state is unknown.
- Ads should not block critical result-checking paths unless explicitly required by the product policy.
