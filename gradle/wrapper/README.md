# Gradle Wrapper policy

Gradle wrapper JAR omitted due to Codex binary limitation. CI reliably bootstraps via `gradle/actions/setup-gradle@v5`. Local devs can run `gradle wrapper` once if they want the full JAR.

Repository-tracked wrapper files:
- `gradlew`
- `gradlew.bat`
- `gradle/wrapper/gradle-wrapper.properties`

Pinned distribution:
- Gradle `9.4.0`
