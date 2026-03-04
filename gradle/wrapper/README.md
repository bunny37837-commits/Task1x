# Gradle Wrapper note

This repository intentionally does **not** commit `gradle/wrapper/gradle-wrapper.jar` because the PR tooling for this project rejects binary files.

CI builds use `gradle/actions/setup-gradle@v5` and invoke Gradle directly (`gradle -p android app:assembleDebug`) so wrapper JAR binaries are not required in git.
