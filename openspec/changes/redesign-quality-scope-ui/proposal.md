## Why

The current scope screen exposes internal scan statistics and three large range choices that make the normal field workflow harder than necessary. Operators need a cleaner screen focused on filtering and selecting one plot, while still retaining low-profile full-check and test controls for broader verification.

## What Changes

- Remove the visible indexed-plot/ZDB statistics line from the scope screen.
- Remove the three large range radio choices for selected plot, current county, and all plots.
- Keep the county dropdown, plot ID search, plot list, and start-check action as the main workflow.
- Add two small upper-right switches: `全量` for full-check mode and `测试` for passed-rule visibility.
- Add a persistent `全域` county option and restore the last explicitly selected county on later app launches.
- Derive county display labels from source project folder names such as `综合监测-新洲区-20260527090338` -> `新洲区`.
- In normal mode, require one selected plot and route directly to that plot's detail screen after checking.
- In full-check mode, disable and clear filters/selection temporarily, check all indexed plots, and route to the summary screen.
- Add a compact count line to the plot detail screen.
- Make Android system back follow the same hierarchy as the top-bar back actions.

## Capabilities

### New Capabilities

- `quality-scope-ui-redesign`: Refines scope selection, full-check/test switches, persisted county filtering, single-plot result routing, detail counts, and back navigation.

### Modified Capabilities

无。

## Impact

- Affects the Android app UI and state management in `app/src/main/java/com/example/myapplication/quality/ui`.
- Affects plot filtering labels in the domain/UI boundary.
- Adds local preference storage for the last selected county.
- Adds or updates focused unit tests for county label cleaning, filtering, run routing, and navigation state.
