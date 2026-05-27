## 1. Scope Filtering State

- [x] 1.1 Add county label cleaning from ZDB project folder names and use it for dropdown options and filtering.
- [x] 1.2 Persist and restore the last explicitly selected county, falling back to `全域` when unavailable.
- [x] 1.3 Add full-check mode state that temporarily clears/disables filters and selected plot without overwriting the persisted county.

## 2. Scope Screen UI

- [x] 2.1 Remove the scope screen index statistics line, three large scope choices, and large test-mode card.
- [x] 2.2 Add compact upper-right `全量` and `测试` switches while keeping the county dropdown, plot ID search field, plot list, and start-check button.
- [x] 2.3 Keep normal mode focused on plot selection, requiring a selected plot before checking.

## 3. Run Routing And Navigation

- [x] 3.1 Build `CheckScope.Single` for normal mode and route completed single-plot runs directly to detail.
- [x] 3.2 Build `CheckScope.All` for full-check mode and route completed full-check runs to summary.
- [x] 3.3 Add compact plot-detail counts for mandatory, advisory, skipped, and ignored results.
- [x] 3.4 Handle Android system back consistently for scope, summary, and single/full detail screens.

## 4. Verification

- [x] 4.1 Add or update unit tests for county label cleaning, persisted county restoration, combined filtering, and full-check mode state transitions.
- [x] 4.2 Add or update tests for single/full run routing and detail counts.
- [x] 4.3 Build and run the relevant Android checks, then manually verify the updated workflow on the connected device if available.
