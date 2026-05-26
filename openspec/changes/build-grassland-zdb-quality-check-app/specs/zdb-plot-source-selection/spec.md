## ADDED Requirements

### Requirement: User selects a grassland data directory
The application SHALL allow the user to select a directory through the Android system document-tree picker and SHALL retain read access needed to scan the selected `草原监测/数据` directory on later use.

#### Scenario: Select data directory
- **WHEN** the user selects the `草原监测/数据` directory through the system picker
- **THEN** the application stores the granted read authorization and displays the selected directory as the active data source

### Requirement: Application discovers valid ZDB sources
The application SHALL recursively search the selected directory for `.zdb` files, SHALL identify readable supported SQLite sources, and SHALL report files that cannot be indexed without blocking valid sources.

#### Scenario: Scan directory containing valid and invalid files
- **WHEN** the selected directory contains a supported non-empty `.zdb` and a zero-byte or unreadable `.zdb`
- **THEN** the application indexes the supported source and reports the invalid file count or reason without aborting the scan

### Requirement: Application indexes plots without changing source identifiers
The application SHALL build selectable plot entries from supported ZDB sources and SHALL preserve each source URI and original `YD_ID` as the internal identity of the entry.

#### Scenario: Duplicate plot identifiers across sources
- **WHEN** two discovered ZDB sources expose the same original `YD_ID`
- **THEN** the application presents distinct display entries by appending a display-only suffix such as `-2` while retaining the unmodified original identifier and source URI for checking

### Requirement: User selects a check scope
The application SHALL support plot ID entry or selection, county filtering defaulted to the full discovered area, and check scopes for one selected plot, all plots in the selected county, or all indexed plots.

#### Scenario: Select a county batch scope
- **WHEN** the user selects a county and chooses to check all plots in the current county
- **THEN** the application provides the checking workflow with only the indexed plot references associated with that county

#### Scenario: Select all plots
- **WHEN** the user leaves the county filter at its default full-area value and chooses all plots
- **THEN** the application provides the checking workflow with all valid indexed plot references
