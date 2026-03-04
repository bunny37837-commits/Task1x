# TaskRemind Pro

Offline Android daily task reminder app with exact alarms and floating overlay actions (Done / Snooze / Dismiss).

## Requirements
- Android Studio Iguana+ recommended
- Android SDK 34
- Min SDK 26

## Build
```bash
./gradlew :app:assembleDebug
```

## Test
```bash
./gradlew test
```

## Architecture
- Jetpack Compose UI
- Room database for `Task` and `Settings`
- AlarmManager exact alarms
- WorkManager worker triggered by alarm receiver
- Foreground overlay service for popup reminders
