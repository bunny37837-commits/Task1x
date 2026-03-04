# TaskRemind Pro (Flutter)

Offline Android daily task reminder app built with Flutter + Dart.

## Delivered
- Task create/edit/delete
- Daily reminder per task
- Global reminder toggle
- Dark mode toggle (persisted)
- Overlay popup with **Done / Snooze 10m / Dismiss**
- 12-second auto-dismiss progress
- Release APK output

## Local build
```bash
export PATH=/workspace/flutter/bin:$PATH
export ANDROID_SDK_ROOT=/workspace/android-sdk
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64

flutter pub get
flutter analyze
flutter test
flutter build apk --release
```

APK path:
- `build/app/outputs/flutter-apk/app-release.apk`

## Android notes
- `minSdk = 26`
- `targetSdk = 34`
- `compileSdk = 36` (plugin compatibility requirement)
- Required permissions are declared in `android/app/src/main/AndroidManifest.xml`.

## Startup reliability
- App startup now uses a bootstrap loader with explicit error + retry UI to avoid black-screen launch failures when plugin/database initialization cannot complete.

## PR compatibility
- Binary files are avoided in this branch to keep PR update flow working with tooling that rejects binaries.
- Android launcher now references the system default icon (`@android:drawable/sym_def_app_icon`) instead of committed PNG mipmaps.

## GitHub Actions (browser-only workflow)
- CI workflow: `.github/workflows/android-ci.yml`
- Uses `actions/setup-java@v4` + `gradle/actions/setup-gradle@v5` as requested.
- Runs: `flutter pub get`, `flutter analyze`, `flutter test`, `flutter build apk --debug`.
- Uploads debug APK artifact from `build/app/outputs/flutter-apk/app-debug.apk`.
