# Project Skill — TaskRemind Pro

## Build commands
- `./gradlew :app:assembleDebug`
- `./gradlew test`

## Repo workflow
1. Read `SPEC.md`, `PLANS.md`, `DECISIONS.md`, and `STATUS.md`.
2. Implement one milestone at a time.
3. Run build + tests before closing milestone.
4. Update `STATUS.md` and `DECISIONS.md` with evidence.

## Verification steps
- Build debug APK.
- Run unit tests.
- Verify app is installable and starts.

## Project conventions
- Kotlin + Jetpack Compose.
- Room for local persistence.
- AlarmManager + WorkManager for reminder flow.
- Foreground overlay service for reminder popups.
