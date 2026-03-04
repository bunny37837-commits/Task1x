# DECISIONS.md — Technical Decisions Log

## [DEC-001] Framework Choice
Date: 2026-03-04
Status: Decided

Decision: Flutter + Dart only.
Reason: User explicitly requested Flutter project and disallowed Kotlin/Java/Compose/Room.
Rejected: Native Android Kotlin stack.
Impact: Entire implementation moved to `pubspec.yaml` + `lib/` Dart code.

## [DEC-002] Local Storage
Date: 2026-03-04
Status: Decided

Decision: `sqflite` for tasks, `shared_preferences` for settings.
Reason: Offline persistence for structured tasks plus simple key/value settings.
Rejected: Backend/cloud storage.
Impact: Fully offline data model.

## [DEC-003] Reminder Trigger
Date: 2026-03-04
Status: Decided

Decision: `android_alarm_manager_plus` callback schedules daily alarms per task.
Reason: Needed Android background trigger path in Flutter.
Rejected: Polling timers in app runtime.
Impact: Reminder callback can run when UI is not foreground.

## [DEC-004] Overlay Strategy
Date: 2026-03-04
Status: Decided

Decision: `flutter_overlay_window` with dedicated overlay entrypoint screen.
Reason: Closest Flutter-only approach to floating popup behavior.
Rejected: In-app dialog only.
Impact: Overlay permissions are required at runtime and OEM behavior must be device-tested.

## [DEC-005] Network Access
Date: 2026-03-04
Status: Not Required

Decision: Network OFF for app runtime.
Allowed Domains: None.
Reason: SPEC requires 100% offline app.

## [DEC-006] Wrapper Binary Handling for PR Compatibility
Date: 2026-03-04
Status: Decided

Decision: Do not commit `gradle/wrapper/gradle-wrapper.jar`; keep wrapper properties/scripts and document local regeneration.
Reason: Upstream PR tooling reports "binary files are not supported" and blocks smooth merge/review.
Rejected: Keeping the JAR committed (continues PR binary failure).
Impact: Contributors regenerate wrapper jar locally when needed using `gradle wrapper --gradle-version 8.12 --no-validate-url`.

## [DEC-007] Conflict-Safe Documentation Baseline
Date: 2026-03-04
Status: Decided

Decision: Standardize key project files (`SKILL.md`, `.gitignore`, `PLANS.md`, `DECISIONS.md`, `README.md`, `STATUS.md`) into clean conflict-free baseline content.
Reason: Branch merge requested with conflicts on these files; stable canonical text reduces repeated merge churn.
Rejected: Leaving per-branch divergent wording.
Impact: Future merges are simpler and automated conflict-marker checks are part of workflow.
