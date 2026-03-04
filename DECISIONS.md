# DECISIONS.md — Technical Decisions Log

## [DEC-001] Framework Choice
Date: 2026-03-04
Status: Decided

Decision: Flutter + Dart only.
Reason: User explicitly requested Flutter project and disallowed Kotlin/Java/Compose/Room.
Impact: Entire implementation is in Flutter Dart app + generated Flutter Android shell.

## [DEC-002] Local Persistence
Date: 2026-03-04
Status: Decided

Decision: `sqflite` for tasks and `shared_preferences` for global settings + overlay payload cache.
Reason: Offline-first persistence with no backend.
Impact: App remains fully local and network-independent at runtime.

## [DEC-003] Reminder + Overlay Triggering
Date: 2026-03-04
Status: Decided

Decision: `android_alarm_manager_plus` one-shot exact alarms per task and `flutter_overlay_window` for floating full-screen overlay UI.
Reason: Closest Flutter-native approach to “incoming-call style” reminder behavior.
Impact: Requires Android overlay/alarm/foreground permissions and device validation.

## [DEC-004] Android SDK Compilation Level
Date: 2026-03-04
Status: Decided

Decision: Compile SDK set to 36; target SDK remains 34 per specification; min SDK 26.
Reason: Current Flutter plugin versions (`shared_preferences_android`, `sqflite_android`) require compile SDK 36.
Impact: Keeps spec-compatible runtime target while satisfying build requirements.

## [DEC-005] Temporary Network Enablement for Toolchain Setup
Date: 2026-03-04
Status: Decided

Decision: Enable network only for build-tool bootstrap (Flutter SDK + Android SDK components).
Allowed Domains: `github.com`, `storage.googleapis.com`, `dl.google.com`, `archive.ubuntu.com`, `security.ubuntu.com`, `apt.llvm.org`, `mise.jdx.dev`.
Reason: Required to produce requested APK artifact in this environment.
Impact: Build tooling installed locally; app runtime remains fully offline.


## [DEC-006] Startup Bootstrap Reliability
Date: 2026-03-04
Status: Decided

Decision: Initialize alarm manager/database/settings behind a bootstrap widget with loading and explicit error/retry UI.
Reason: Direct pre-runApp initialization could leave a black launch screen when startup initialization fails or stalls.
Impact: App now always renders a visible state at startup and provides retry recovery.


## [DEC-007] Binary-Free Android Resource Policy
Date: 2026-03-04
Status: Decided

Decision: Remove committed PNG launcher assets and use Android system icon reference in manifest.
Reason: Branch update/PR tooling rejects binary files, blocking merges.
Impact: Launcher/icon binaries remain excluded from source changes; this decision does not apply to required Gradle wrapper binaries.


## [DEC-008] GitHub Actions CI Baseline
Date: 2026-03-04
Status: Decided

Decision: Standardize CI on `actions/setup-java@v4` (Temurin 17) + `gradle/actions/setup-gradle@v5` and run `flutter pub get`, `flutter analyze`, `flutter test`, `flutter build apk --debug`.
Reason: User requires browser-only GitHub workflow that is stable and Android-compatible.
Impact: PRs and pushes now receive consistent automated verification and a downloadable debug APK artifact.


## [DEC-009] Commit Gradle Wrapper JAR for CI Reliability
Date: 2026-03-04
Status: Superseded

Decision: Commit `gradle/wrapper/gradle-wrapper.jar` and pin wrapper distributions to Gradle 8.12.
Reason: Omitting wrapper JAR caused hard CI failures: `Unable to access jarfile .../gradle/wrapper/gradle-wrapper.jar`.
Impact: Replaced by DEC-010 because repository PR tooling rejects binary files.


## [DEC-010] Binary-Free CI Build Path
Date: 2026-03-04
Status: Decided

Decision: Keep wrapper JAR untracked and build in CI with direct Gradle invocation (`gradle -p android app:assembleDebug`) using `setup-gradle@v5`.
Reason: Repository PR tooling rejects binary files; committed wrapper JAR blocks branch updates.
Impact: CI remains green without wrapper JAR binaries in git.
