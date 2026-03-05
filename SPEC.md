TaskRemind Pro
Platform
Android only (minimum SDK 26)
What It Does
Daily task reminder app with floating overlay popup that appears over any screen even when app is closed. Exactly like Truecaller incoming call screen.
Features
Create, edit, delete tasks
Set daily reminder time per task
Global on/off toggle
Floating overlay when reminder fires: shows task title + Done / Snooze 10min / Dismiss buttons, linear countdown progress bar (12s auto-dismiss)
Settings screen: dark mode toggle, global enable/disable, permissions guide
Settings persisted locally (dark mode, global toggle)
Constraints
Android only, no iOS
100% offline, no internet
No cloud, no Firebase, no backend
Required Android Permissions
SYSTEM_ALERT_WINDOW — overlay over other apps
USE_FULL_SCREEN_INTENT — show overlay on lock screen
FOREGROUND_SERVICE — keep reminder service alive
FOREGROUND_SERVICE_SPECIAL_USE — required for overlay foreground service on Android 14+
RECEIVE_BOOT_COMPLETED — reschedule alarms after reboot
SCHEDULE_EXACT_ALARM / USE_EXACT_ALARM — fire reminder at exact time
POST_NOTIFICATIONS — show notification on Android 13+
Background Trigger Flow
WorkManager schedules exact alarm for each task
Alarm fires → WorkManager worker runs directly (not dependent on notification tap)
Worker reads task from local DB → calls showOverlay() immediately
Overlay appears automatically over any screen, even when app is closed
Data Models
Task: id, title, reminderTime, isEnabled
Settings: isDarkMode, globalEnabled
Done Means
APK installs on Android phone
Task creation works
Reminder fires at set time
Floating overlay appears automatically (no app open required)
Done and Snooze buttons work
Dark mode toggle works
App survives reboot (reminders reschedule)


Build Tooling Network Note
Temporary internet access is allowed only for local toolchain bootstrap (Flutter SDK and Android SDK packages). App runtime remains fully offline.
