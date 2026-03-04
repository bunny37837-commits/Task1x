# DECISIONS.md — Technical Decisions Log

## [DEC-001] Architecture Pattern
Date: 2026-03-04
Status: Decided

Decision: MVVM-style single-activity Compose app with repository layer.
Reason: Keeps state handling simple while separating UI from persistence/scheduling concerns.
Rejected: Multi-module architecture (too heavy for V1 scope).
Impact: ViewModel coordinates Room data and scheduler actions.

## [DEC-002] Database / Storage
Date: 2026-03-04
Status: Decided

Decision: Room database with `TaskEntity` and singleton `SettingsEntity`.
Reason: Reliable local offline persistence, typed schema, coroutine Flow support.
Rejected: SharedPreferences-only storage (weak schema, less suitable for list CRUD).
Impact: App state survives process death and reboot scheduling can query all tasks.

## [DEC-003] Key Libraries / Dependencies
Date: 2026-03-04
Status: Decided

Decision: Jetpack Compose, Room, WorkManager, Coroutines.
Reason: Native Android stack, stable support for offline local workflows.
Impact: Modern UI and lifecycle-safe background execution.

## [DEC-004] Network Access
Date: 2026-03-04
Status: Not Required

Decision: Network OFF.
Allowed Domains: None.
Reason: SPEC requires 100% offline operation and no backend/cloud.

## [DEC-005] Security Approach
Date: 2026-03-04
Status: Decided

Decision: Minimal-permission design with only Android reminder/overlay permissions from SPEC, and no credential handling.
Reason: App has no login/backend and should avoid collecting sensitive data.
Impact: Reduced attack surface and full offline behavior.

## [DEC-006] Reminder Trigger Pipeline
Date: 2026-03-04
Status: Decided

Decision: AlarmManager exact alarm per task -> BroadcastReceiver -> OneTimeWorkRequest -> foreground OverlayService.
Reason: Matches requirement for exact daily timing while using worker execution handoff and service-based overlay rendering.
Rejected: Notification-tap dependent flow (does not satisfy auto popup requirement).
Impact: Reminder overlay can fire when app UI is closed.
