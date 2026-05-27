## Context

The app already has a Compose workflow for source selection, scope selection, progress, result summary, and plot detail. `QualityCheckViewModel` holds scan results, filters, selected plot, selected scope, test mode, progress, and reviewed results. `QualityCheckEngine` already accepts `CheckScope.Single` and `CheckScope.All`, so this change should mostly reshape UI state and routing rather than change rule execution.

The recently added test-mode behavior exists as a large card on the scope screen. That behavior should remain, but the control should move to a small upper-right switch.

## Goals / Non-Goals

**Goals:**

- Make single-plot checking the default normal workflow.
- Keep plot ID search visible in normal mode.
- Remove visual clutter from scan statistics and scope radio choices.
- Keep full-check and test behavior available as small secondary switches.
- Persist the last explicitly selected county across app launches.
- Route single-plot checks directly to detail and full checks to summary.
- Support Android system back according to the current screen hierarchy.

**Non-Goals:**

- Do not change SQL rule execution semantics.
- Do not remove `CheckScope.County` from the domain model; it can remain unused by the UI.
- Do not persist selected plots across launches.
- Do not make full-check mode or test mode persistent unless existing code already persists test mode.

## Decisions

### 1. Treat `全量` as a mode switch, not another large scope choice

The UI will keep the primary workflow as county/search/list selection. `全量` becomes a small mode switch that temporarily disables the normal filters and selection and checks all indexed plots.

This keeps the screen compact and preserves full-check capability without reintroducing the removed radio controls.

### 2. Store only explicit county selections

The app will persist the county only when the user chooses an option from the county dropdown. Turning on `全量` will temporarily show `全域`, clear search, and clear selection, but it will not overwrite the stored county.

This preserves the user's last area choice while still making full-check mode clearly global.

### 3. Clean county labels from project folder names

The scanner already records the parent folder as `ZdbSourceRef.projectName`. Filtering labels should be derived from this value by removing a leading `综合监测-` prefix and a trailing timestamp segment such as `-20260527090338`.

This matches the field file structure where the project folder name is the operational county label.

### 4. Route results by run mode

Single-plot checks should complete directly into the plot detail screen. Full checks should continue to use the existing summary screen and per-plot drill-down.

This avoids forcing a one-row summary screen into the normal workflow.

### 5. Keep execution engine unchanged

The view model can still build `CheckScope.Single` or `CheckScope.All` and pass it to `QualityCheckEngine`. This change should not alter rule execution, read-only database access, issue fingerprinting, or ignored-issue reconciliation.

## Risks / Trade-offs

- [Persisted county is unavailable after new data downloads] -> Fall back to `全域` silently after scanning.
- [Two folders clean to the same county label] -> Show one dropdown option and include plots from all matching folders.
- [Full mode temporarily clears visible filters] -> Do not persist this temporary state, and restore the last explicit county when leaving full mode.
- [System back during progress could abandon work unexpectedly] -> Keep cancellation explicit and do not silently discard a running check.

## Migration Plan

No user data migration is required. A new local preference key can be added for the last selected county. Existing users without the key default to `全域`.

Rollback is limited to reinstalling the previous APK; source ZDB files are not modified by this change.

## Open Questions

无。
