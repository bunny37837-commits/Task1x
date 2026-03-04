# PLANS.md — Project Roadmap

## Status
- [ ] Not Started
- [x] In Progress — Current: V1 (Flutter rewrite)
- [ ] Complete

## V1 — Core Working Feature

### Goal
Deliver Flutter Android app with offline task CRUD, daily reminder scheduling, and overlay reminder actions.

### Acceptance Criteria
- [x] Flutter/Dart-only implementation.
- [x] Task create/edit/delete with daily time.
- [x] Global toggle + dark mode persisted.
- [x] Reminder callback attempts overlay with Done/Snooze/Dismiss + 12s countdown UI.

### Tasks
- [x] Remove Kotlin/Room/Compose implementation.
- [x] Add Flutter dependencies and app scaffold.
- [x] Implement offline storage + settings persistence.
- [x] Implement reminder scheduling and overlay entry flow.

## V2 — Complete Feature Set

### Goal
Production-hardening on-device behavior.

### Acceptance Criteria
- [ ] Validate overlay behavior across Android OEM variants.
- [ ] Add richer tests for app state and storage.
- [ ] Add permission status UX.

### Tasks
- [ ] Device matrix verification.
- [ ] Expand test suite.
- [ ] In-app permission state indicators.

## V3 — Production Ready

### Goal
Release polish.

### Acceptance Criteria
- [ ] Build/test fully green in CI/local.
- [ ] Docs complete.
- [ ] No critical known issues.

### Tasks
- [ ] Final verification loop.
- [ ] Release checklist.

## Risks & Blockers
| Risk | Impact | Mitigation |
|------|--------|------------|
| Flutter tooling unavailable in container | High | Verify via static review and document environment block |
| Overlay plugin behavior requires Android manifest setup | Medium | Validate on Android device/emulator as next step |
