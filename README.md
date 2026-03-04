# TaskRemind Pro (Flutter)

Offline Android daily task reminder app built with Flutter + Dart.

## Features
- Create, edit, delete tasks
- Daily reminder per task
- Global enable/disable reminders
- Overlay popup actions: Done / Snooze 10m / Dismiss
- 12s auto-dismiss progress
- Persisted dark mode and global settings

## Tech stack
- Flutter (Material 3)
- `sqflite` for local task storage
- `shared_preferences` for settings
- `android_alarm_manager_plus` for exact-style alarm callback scheduling
- `flutter_overlay_window` for floating overlay rendering

## Run
```bash
flutter pub get
flutter run -d android
```

## Test
```bash
flutter analyze
flutter test
```

## Merge/CI notes
- `gradle/wrapper/gradle-wrapper.jar` is intentionally not tracked to avoid binary PR failures.
- Regenerate the wrapper JAR locally with:
  - `gradle wrapper --gradle-version 8.12 --no-validate-url`
