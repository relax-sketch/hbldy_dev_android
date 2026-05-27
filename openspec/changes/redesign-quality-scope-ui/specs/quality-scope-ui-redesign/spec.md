## ADDED Requirements

### Requirement: Scope screen prioritizes plot selection
The application SHALL present the scope screen as a county/search/list plot-selection workflow without showing scan index statistics or the three large scope radio choices.

#### Scenario: View normal scope screen
- **WHEN** indexed plots are available and full-check mode is off
- **THEN** the scope screen shows a county dropdown, plot ID search field, plot list, start-check button, and small `全量` and `测试` switches
- **AND** it does not show the indexed-plot/ZDB statistics line
- **AND** it does not show large choices for selected-plot, current-county, or all-plot checking

### Requirement: County filter uses persistent full-area selection
The application SHALL include a `全域` county option, SHALL default to `全域` on first use, and SHALL restore the last explicitly selected county on later use when that county is still available.

#### Scenario: First scan defaults to full area
- **WHEN** the user scans data without a saved county selection
- **THEN** the scope screen selects `全域`
- **AND** the plot list includes all indexed plots

#### Scenario: Restore saved county
- **WHEN** the user previously selected a county and that county exists after a later scan
- **THEN** the scope screen selects that county
- **AND** the plot list is filtered to that county

#### Scenario: Saved county is unavailable
- **WHEN** the saved county does not exist after scanning current data
- **THEN** the scope screen falls back to `全域`
- **AND** the plot list includes all indexed plots

### Requirement: County labels come from project folder names
The application SHALL display and filter counties by cleaned project folder names from discovered ZDB sources.

#### Scenario: Clean standard monitoring folder name
- **WHEN** a plot comes from a source whose project folder is `综合监测-新洲区-20260527090338`
- **THEN** its county filter label is `新洲区`

### Requirement: Normal mode checks one selected plot
The application SHALL require a selected plot before checking when full-check mode is off, and SHALL route the completed run directly to that plot's detail screen.

#### Scenario: Start without selected plot
- **WHEN** full-check mode is off and no plot is selected
- **THEN** the start-check action is disabled or shows an inline empty-selection error
- **AND** no quality check starts

#### Scenario: Start selected plot check
- **WHEN** full-check mode is off and the user selects a plot then starts checking
- **THEN** the application checks only that selected plot
- **AND** after completion it shows the plot detail screen

### Requirement: Full-check mode checks all indexed plots
The application SHALL use the `全量` switch to temporarily disable normal filtering and selection and check all indexed plots.

#### Scenario: Enable full-check mode
- **WHEN** the user turns on `全量`
- **THEN** the county dropdown is disabled and temporarily shown as `全域`
- **AND** the plot ID search is disabled and cleared
- **AND** selected plot state is cleared
- **AND** plot list selection is disabled

#### Scenario: Start full check
- **WHEN** full-check mode is on and the user starts checking
- **THEN** the application checks all indexed plots
- **AND** after completion it shows the result summary screen

#### Scenario: Leave full-check mode
- **WHEN** the user turns off `全量`
- **THEN** the app restores the last explicitly selected county when available
- **AND** full-check mode has not overwritten the saved county preference

### Requirement: Test mode only controls passed-rule visibility
The application SHALL keep `测试` independent from check range selection.

#### Scenario: Toggle test mode
- **WHEN** the user toggles `测试`
- **THEN** the set of plots selected for checking does not change
- **AND** passed rules are shown or hidden according to the switch state

### Requirement: Plot detail shows compact result counts
The application SHALL show a compact count line on plot detail with mandatory, advisory, skipped, and ignored counts for that plot.

#### Scenario: View plot detail
- **WHEN** the user views a plot detail result
- **THEN** the title area includes `强制性 X · 提示性 X · 跳过 X · 忽略 X`

### Requirement: Android back follows screen hierarchy
The application SHALL handle Android system back consistently with top-bar navigation for the quality-check workflow.

#### Scenario: Back from single-plot detail
- **WHEN** the current result came from a single-plot run and the user presses Android back on the detail screen
- **THEN** the application returns to the scope screen

#### Scenario: Back from full-check detail
- **WHEN** the current result came from a full-check run and the user presses Android back on the detail screen
- **THEN** the application returns to the summary screen

#### Scenario: Back from summary and scope
- **WHEN** the user presses Android back on the summary screen
- **THEN** the application returns to the scope screen
- **AND WHEN** the user presses Android back on the scope screen
- **THEN** the application returns to the source-selection screen
