# PLANS.md — Project Roadmap

## Status
- [ ] Not Started
- [x] In Progress — Current: V1
- [ ] Complete

## V1 — Core Working Feature

### Goal
Ship installable Android app with task CRUD, daily exact reminders, and floating overlay actions.

### Acceptance Criteria
- [x] Task create/edit/delete with daily time.
- [x] Reminder trigger pipeline works (AlarmReceiver -> Worker -> OverlayService).
- [x] Overlay includes task title + Done/Snooze/Dismiss + 12 second auto-dismiss progress.
- [x] Global enable toggle and settings persistence are implemented.

### Tasks
- [x] Set up Android project and dependencies.
- [x] Implement Room entities/DAO/repository.
- [x] Implement reminder scheduling + reboot rescheduling.
- [x] Build Compose screens for tasks and settings toggles.
- [x] Implement overlay foreground service UI and actions.

### Estimated Scope
Medium (single-module Android app, offline only).

## V2 — Complete Feature Set

### Goal
Harden permissions UX and richer validations for production-like behavior.

### Acceptance Criteria
- [ ] Runtime permission states are surfaced with explicit status indicators.
- [ ] Better time picker UX and task form validation.
- [ ] Add instrumentation tests for primary flows.

### Tasks
- [ ] Add permission status cards with deep links.
- [ ] Replace numeric time entry with proper time picker dialog.
- [ ] Add Android tests for task and settings flows.

## V3 — Production Ready

### Goal
Release-ready quality pass.

### Acceptance Criteria
- [ ] Build passes all checks
- [ ] No known critical issues
- [ ] Docs complete

### Tasks
- [ ] Expand QA matrix and manual verification checklist.
- [ ] Final verification loop.
- [ ] STATUS.md marked complete.

## Risks & Blockers
| Risk | Impact | Mitigation |
|------|--------|------------|
| Overlay behavior differs by OEM battery policies | High | Show permission guide and battery optimization instruction |
| Android 14 foreground service policy strictness | Medium | Use special-use FGS type and notification fallback |

## Milestone History
| Milestone | Completed On | Verified By |
|-----------|-------------|-------------|
| V1 | 2026-03-04 | Attempted build/tests; blocked by repository 403 in environment |
