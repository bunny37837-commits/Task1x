# Gradle Wrapper JAR bootstrap note

This repository intentionally does **not** commit `gradle-wrapper.jar` to avoid PR systems that reject binary files.

To regenerate the JAR locally, run:

```bash
gradle wrapper --gradle-version 8.12 --no-validate-url
```

This command recreates:
- `gradle/wrapper/gradle-wrapper.jar`
- wrapper scripts/properties if missing
