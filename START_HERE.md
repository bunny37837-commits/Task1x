# START_HERE.md — Quick Reference
# Read this once. Then follow the 3 steps.

---

## Your Job (3 Steps Only)

### Step 1 — Settings (Once)
```
Settings → General → Follow-up behavior → ON
Reasoning effort → xhigh
```

### Step 2 — Fill SPEC.md (Every New Project)
Open SPEC.md and fill:
- App name
- Goal (2 lines)
- Platform
- Core features
- Login type
- Done Means (one sentence)

### Step 3 — Trigger Codex
Paste this exactly:
```
Read SPEC.md and AGENTS.md completely.
Create PLANS.md based on the specification.
Set up project structure as defined.
Then begin V1 implementation following all rules.
```

---

## Mid-Project Commands

| Situation | Command |
|-----------|---------|
| Resume work | `Continue implementation.` |
| Next milestone | `Move to next milestone.` |
| Fix errors | `Fix failing builds and continue.` |
| Release | `Prepare production-ready build.` |

---

## File Ownership

| File | Who Writes | Purpose |
|------|-----------|---------|
| AGENTS.md | You (once) | Codex rules |
| SPEC.md | You (per project) | What to build |
| PLANS.md | Codex | Roadmap |
| DECISIONS.md | Codex | Tech choices |
| STATUS.md | Codex | Progress |
| .codex/skills/project/SKILL.md | You (once) | Workflow |

---

## Mental Model
```
SPEC.md     = What to build
AGENTS.md   = How to behave
SKILL.md    = How to execute
PLANS.md    = Roadmap
STATUS.md   = Where we are
DECISIONS.md = Why we chose what we chose
```

---

## Project Folder Structure
```
your-project/
│
├── AGENTS.md          ← rules (copy once, reuse always)
├── SPEC.md            ← fill this per project
├── PLANS.md           ← Codex generates
├── DECISIONS.md       ← Codex updates
├── STATUS.md          ← Codex updates
├── START_HERE.md      ← this file
│
├── .codex/
│   └── skills/
│       └── project/
│           └── SKILL.md   ← workflow instructions
│
└── src/               ← actual code
```

---

## One Rule to Remember
> You write SPEC.md → Codex builds everything else.
