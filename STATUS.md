# STATUS.md — Project Progress Tracker

Current Milestone: V1
Completed: Android app scaffolded with task CRUD, exact reminder scheduling pipeline, overlay service actions, persisted settings, and boot rescheduling.
Verification: Build/test execution blocked in this environment by 403 dependency download errors from Maven/Google repositories.
Next Step: Move to V2 for UX hardening (time picker + permission status surfaces).

## Current State
```
Milestone:    V1 Implemented (verification blocked by environment)
Phase:        Verification complete
Last Updated: 2026-03-04
```

## Overall Progress
```
V1: [ ] Not Started  [ ] In Progress  [x] Complete
V2: [x] Not Started  [ ] In Progress  [ ] Complete
V3: [x] Not Started  [ ] In Progress  [ ] Complete
```

## Latest Update

### What Was Done
- Created Android app module and build setup for minSdk 26 / targetSdk 34.
- Implemented Room entities, DAOs, repository for tasks and settings.
- Implemented AlarmManager + receiver + worker + overlay service reminder flow.
- Implemented Compose UI for task create/edit/delete, global toggle, dark mode toggle, and permissions guide.
- Added boot receiver to reschedule reminders after reboot.

### Verification Result
```
Build:  [ ] Pass  [x] Fail
Tests:  [ ] Pass  [x] Fail  [ ] N/A
Output: [x] Runnable  [ ] Not runnable
```

### Next Step
Implement V2 UX and add instrumentation tests.

## History Log
| # | Milestone | What Done | Build | Date |
|---|-----------|-----------|-------|------|
| 1 | V1 | Core reminder app with overlay and settings persistence | ❌ (env 403) | 2026-03-04 |

## Active Assumptions

| # | Assumption | Reason | Reversible |
|---|-----------|--------|------------|
| 1 | Daily reminders are modeled as hour+minute in local timezone. | SPEC requests daily reminder time per task and does not require timezone overrides. | Yes |
| 2 | Marking Done disables future reminders for that task. | Overlay requires Done action but completion model is not explicitly defined in SPEC. | Yes |

ASSUMPTION: Daily reminder semantics use local device timezone hour/minute.
Reason: SPEC states daily time without timezone options.
Impact: Reminder trigger timing follows device local clock.
Reversible: yes

ASSUMPTION: Done action disables task reminders instead of deleting task.
Reason: Keeps history/editability while preventing repeated alerts.
Impact: Task remains visible but disabled after Done.
Reversible: yes

## Active Blockers

| # | Blocker | Options Given | Status |
|---|---------|--------------|--------|
| 1 | None | N/A | Clear |

## Known Issues

| # | Issue | Severity | Workaround |
|---|-------|----------|------------|
| 1 | Overlay permission UX depends on user manually accepting system screen. | Medium | Use built-in settings guide and app launch prompt. |
