---
name: project-workflow
description: Use this skill when starting, continuing, or completing any development task in this project. Handles full build lifecycle from planning to production.
---

# Project Workflow Skill

## On Project Start
1. Read SPEC.md completely
2. Read AGENTS.md completely
3. Read DECISIONS.md (if exists)
4. Generate PLANS.md with V1→V2→V3 milestones
5. Confirm structure before coding begins

## On Each Milestone
1. Work only on active milestone
2. Keep project runnable at all times
3. Run build + tests after every significant change
4. Fix all errors before moving forward
5. Update STATUS.md when milestone completes

## Compaction Guard (Long Runs)
After every major phase:
- Re-read SPEC.md
- Re-read DECISIONS.md
- Then continue

## Build Commands
[Project-specific — fill these when project starts]

```
# Example for Android:
./gradlew build
./gradlew test
./gradlew assembleDebug

# Example for Web/Node:
npm install
npm run build
npm test
```

## Verification Checklist (Every Milestone)
```
[ ] Build runs without error
[ ] Tests pass
[ ] No skipped/ignored errors
[ ] Output is runnable
[ ] STATUS.md updated
[ ] DECISIONS.md updated if decision made
```

## Commit Format
```
[V1] feat: description
[V1] fix: description
[V2] refactor: description
```

## Assumption Logging Format
```
ASSUMPTION: [what]
Reason:     [why]
Impact:     [what it affects]
Reversible: yes/no
```

## BLOCKED Format
```
BLOCKED — Critical input required
Options:
A) [option]
B) [option]
C) [option]
```

## Definition of DONE
Task complete only when:
- Build ✅
- Tests ✅
- Runnable ✅
- Docs updated ✅
- STATUS updated ✅
