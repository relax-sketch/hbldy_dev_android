# Quality Scope UI Redesign

Date: 2026-05-27

## Goal

Revise the grassland ZDB quality-check scope screen so normal use is centered on selecting one plot from a larger, filterable list, while still keeping a low-profile full-check mode and test mode for broader verification.

The redesign removes the bulky scope-choice controls and index statistics from the scope screen. It keeps the plot ID search field, adds a persistent county filter with an explicit full-area option, and changes single-plot runs to land directly on the plot detail screen.

## Current Context

The app is a single-activity Android Compose app with screen state held in `QualityCheckViewModel`.

Relevant existing boundaries:

- `QualityCheckApp.kt` renders the source, scope, progress, summary, and detail screens.
- `QualityCheckViewModel.kt` owns scan results, filters, selected plot, selected scope, and check execution.
- `QualityCheckUiState.kt` carries UI state.
- `DefaultCheckScopeSelector` filters plots by county and plot ID and builds `CheckScope` values.
- `QualityCheckEngine` already accepts `CheckScope.Single` and `CheckScope.All`, so the engine does not need a new execution path.

## Scope Screen

The scope screen keeps the current page structure but removes these visible elements:

- The top index statistics line, such as `已索引样地：1 个；ZDB：2 个`.
- The three large radio-style scope choices:
  - `仅检查选中样地`
  - `检查当前区县`
  - `检查全部样地`
- The large test-mode card at the bottom.

The screen retains these primary controls:

- County dropdown.
- Plot ID search input.
- Plot list.
- Start-check button.

The upper-right area of the scope screen contains two small, low-profile switches:

- `全量`
- `测试`

These switches should be visually secondary. They are operational controls, not the primary workflow.

## County Filter

The county dropdown includes a special first option:

- `全域`

Behavior:

- First entry to the scope screen defaults to `全域`.
- `全域` displays all indexed plots.
- Selecting a county filters the plot list to plots from that county.
- The selected county is persisted locally and restored the next time the app opens.
- After scanning, if the persisted county is still available, it is restored.
- If the persisted county is no longer available, the selection falls back to `全域`.

County display names come from the ZDB source folder name, not from the database county code. The scanner already stores the parent folder name as `ZdbSourceRef.projectName`. The UI/filter label should clean that project name:

- `综合监测-新洲区-20260527090338` becomes `新洲区`.
- `综合监测-江岸区-20260526102825` becomes `江岸区`.

The exact cleaning rule is:

1. Remove a leading `综合监测-` prefix when present.
2. Remove a trailing timestamp segment matching `-` followed by digits when present.
3. Use the remaining nonblank text as the county label.
4. If cleaning produces a blank value, fall back to the original project folder name.

## Plot Filtering And Selection

The plot ID search field stays visible in normal mode.

Normal mode means `全量` is off:

- County dropdown is enabled.
- Plot ID search is enabled.
- Plot list is enabled.
- County filter and plot ID search are combined.
- The user must tap one plot before the start button is enabled.
- Starting a check builds a `CheckScope.Single` from the selected plot.

Full-check mode means `全量` is on:

- County dropdown is disabled and temporarily shown as `全域`.
- Plot ID search is disabled and cleared.
- Selected plot is cleared.
- Plot list selection is disabled.
- The start button checks all indexed plots.
- Starting a check builds a `CheckScope.All`.

Turning on full-check mode must not overwrite the persisted county choice. Only an explicit user selection in the county dropdown updates the saved county. When full-check mode is turned off, the app restores the previously selected county if it is still available; otherwise it shows `全域`.

No separate `检查当前区县` workflow is exposed in the UI.

## Test Mode

`测试` remains independent of the check range.

Behavior:

- It only controls whether passed rules are shown in the results and detail views.
- It does not change which plots are checked.
- It replaces the previous large `测试模式` card with a small upper-right switch.

## Result Navigation

Check completion routes depend on the selected mode:

- Single-plot run: after completion, navigate directly to the plot detail screen.
- Full-check run: after completion, navigate to the summary screen.

The detail screen title area should show the selected plot and a compact count line:

`强制性 X · 提示性 X · 跳过 X · 忽略 X`

The existing issue sections remain below that:

- Pending mandatory issues.
- Pending advisory issues.
- Skipped rules.
- Passed rules when test mode is on.
- Ignored issues.

The summary screen remains the full-check landing page and continues to show aggregate metrics and the per-plot result list.

## Back Navigation

Android system back should follow the same screen hierarchy as visible navigation:

- Detail returns to summary when the current run is a full-check run.
- Detail returns to scope when the current run is a single-plot run.
- Summary returns to scope.
- Scope returns to source selection.
- Progress keeps the existing cancel behavior; system back should not silently abandon a running check without using the same cancellation path.

The top app bar back action should match the system back behavior.

## State And Persistence

`QualityCheckUiState` needs separate fields for:

- `checkAllMode`
- `testMode`
- `selectedCounty`
- `plotQuery`
- `filteredPlots`
- `selectedPlot`

`selectedCounty = null` represents `全域`.

Persisted local preferences should store:

- Last selected county label.

They should not store:

- Last selected plot. Plot references depend on scanned ZDB files and can become stale across downloads.
- Full-check mode. The normal workflow should still be single-plot selection after reopening the app.
- Temporary `全域` display while full-check mode is enabled.
- Test mode, unless the current code already persists it. This change does not require test-mode persistence.

## Error Handling

If the user starts in normal mode without selecting a plot, show the existing style of inline error message and keep the user on the scope screen.

If full-check mode is on but no plots are indexed, keep the start button disabled or show the same inline empty-scope error.

If the persisted county is unavailable after a scan, silently fall back to `全域`. This is expected when the data directory changes or new ZDB downloads replace older folders.

If cleaning two project folder names produces the same county label, the dropdown shows that county once and includes plots from all matching folders.

## Testing

Focused tests should cover:

- County label cleaning from project folder names.
- First scan defaults to `全域`.
- A saved county is restored when available.
- A saved county falls back to `全域` when unavailable.
- County filter and plot ID search combine correctly.
- Normal mode requires a selected plot.
- Normal mode builds `CheckScope.Single` and routes to detail after completion.
- Full-check mode clears and disables filters and selection.
- Full-check mode builds `CheckScope.All` and routes to summary after completion.
- Detail count line uses the selected plot result counts for mandatory, advisory, skipped, and ignored items.
- System back moves through detail, summary, scope, and source according to the current run type.
