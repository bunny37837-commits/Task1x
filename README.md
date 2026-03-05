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
export ANDROID_HOME=/workspace/android-sdk
export JAVA_HOME=/root/.local/share/mise/installs/java/17.0.2

flutter pub get
flutter analyze
flutter test

# required for wrapper-based Android builds
cat > local.properties <<EOF
flutter.sdk=/workspace/flutter
sdk.dir=/workspace/android-sdk
EOF

./gradlew :app:assembleDebug --stacktrace
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
- Android launcher PNG binaries remain excluded; app uses system default icon (`@android:drawable/sym_def_app_icon`).
- Gradle wrapper JAR omitted due to Codex binary limitation. CI reliably bootstraps via `gradle/actions/setup-gradle@v5`. Local devs can run `gradle wrapper` once if they want the full JAR.

## GitHub Actions (browser-only workflow)
- CI workflow: `.github/workflows/android-ci.yml`
- Uses `actions/setup-java@v4` + `gradle/actions/setup-gradle@v5` as requested.
- Runs: `flutter pub get`, `flutter analyze`, `flutter test`, then `./gradlew :app:assembleDebug --stacktrace`.
- Uploads debug APK artifact from `build/app/outputs/apk/debug/app-debug.apk`.
- CI creates `local.properties` dynamically before Gradle execution.
