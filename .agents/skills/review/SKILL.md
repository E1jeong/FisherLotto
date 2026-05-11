---
name: review
description: Use when working in FisherLotto and the user asks to review local changes, validate a diff, check architecture guardrails, or verify Android project conventions.
---

# Project Review

## Inputs To Read

Before reviewing changes, read:

- `AGENTS.md`
- `docs/ARCHITECTURE.md`
- `docs/ADR.md`
- `docs/TESTING.md`

Then inspect changed files with Git.

## Checklist

Check:

1. Architecture compliance: module dependencies and layer responsibilities follow `docs/ARCHITECTURE.md`.
2. Domain purity: `domain` has no Android/provider dependency leaks.
3. Presentation boundary: `presentation` depends on domain contracts, not data implementations.
4. Provider safety: raw provider errors, secrets, billing tokens, auth credentials, and stack traces are not exposed.
5. Room safety: schema changes include migration decisions.
6. Orbit MVI consistency: state and side effects are explicit.
7. Tests: new or changed behavior has focused tests, or the gap is clearly justified.
8. Buildability: appropriate Gradle command was run or inability is clearly reported.

## Output

For code review requests:

- Lead with concrete findings ordered by severity.
- Include file and line references.
- If there are no findings, say so explicitly.
- Include this checklist table:

| Item | Result | Notes |
| --- | --- | --- |
| Architecture compliance | pass/fail | {detail} |
| Domain purity | pass/fail | {detail} |
| Presentation boundary | pass/fail | {detail} |
| Provider safety | pass/fail | {detail} |
| Room safety | pass/fail | {detail} |
| Orbit MVI consistency | pass/fail | {detail} |
| Tests | pass/fail | {detail} |
| Buildability | pass/fail | {detail} |

If there are violations, propose concrete fixes.
