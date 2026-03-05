# STATUS.md — Project Progress Tracker

Current Milestone: V1
Completed: Switched to Codex-compatible binary-free Gradle wrapper by removing `gradle/wrapper/gradle-wrapper.jar` from git while keeping wrapper scripts/properties pinned to Gradle 9.4.0; CI remains wrapper-command based with `gradle/actions/setup-gradle@v5`.
Verification: Wrapper policy/docs updated; merge-marker scan clean; local wrapper command requires bootstrap JAR (generated locally during validation, then removed from git state).
Next Step: Push `work-ci-fixed` and verify GitHub Actions `Android CI` passes with binary-free wrapper repository state.

## Current State
```
Milestone:    V1 complete (binary-free wrapper policy for Codex PR compatibility)
Phase:        V2 device hardening/verification
Last Updated: 2026-03-05
```

## Active Assumptions
ASSUMPTION: `gradle/actions/setup-gradle@v5` in CI reliably bootstraps wrapper execution without committing wrapper JAR in repository.
Reason: Codex PR flow blocks binary artifacts.
Impact: CI remains reproducible while repository stays binary-free.
Reversible: yes
