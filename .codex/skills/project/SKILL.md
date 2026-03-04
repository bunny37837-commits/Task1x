# Project Skill — TaskRemind Pro (Flutter)

## Build commands
- `flutter pub get`
- `flutter analyze`
- `flutter test`

## Repo workflow
1. Re-read `SPEC.md`, `PLANS.md`, `DECISIONS.md`, and `STATUS.md`.
2. Implement one milestone at a time.
3. Before committing, verify no conflict markers exist: `rg -n "^(<<<<<<<|=======|>>>>>>>)"`.
4. Run analyze + tests when Flutter SDK is available.
5. Update docs and status files.

## Verification steps
- Static analysis passes.
- Tests pass.
- Android run validates reminder + overlay behavior.

## Conventions
- Dart + Flutter only.
- Keep logic in pure Dart classes and state layer.
- Offline-only persistence.
