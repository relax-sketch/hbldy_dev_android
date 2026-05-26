## ADDED Requirements

### Requirement: Application loads a versioned embedded rule set
The application SHALL load quality-check rules packaged with the APK in a maintainable JSON configuration and SHALL expose the active rule-set version for the checking workflow.

#### Scenario: Load initial rule set
- **WHEN** the initial APK rule set is loaded
- **THEN** it contains the 264 enabled baseline-rule snapshots marked as mandatory and 10 SQL-converted additional test rules retaining their mandatory or advisory labels

### Requirement: Source ZDB databases remain read-only
The application MUST open user-selected `.zdb` sources only for reading, MUST enable SQLite query-only protection while checking, and MUST NOT write rule state, result state, or business-record changes to a source ZDB.

#### Scenario: Complete a quality check
- **WHEN** any single-plot or batch check completes
- **THEN** the source `.zdb` content remains unchanged and any persisted application state is stored outside that source database

### Requirement: Embedded rules are restricted to safe queries
The application SHALL accept embedded executable rule SQL only when it is a single `SELECT` query or `WITH ... SELECT` query and SHALL reject statements capable of modifying a database or its connection configuration.

#### Scenario: Reject modifying SQL
- **WHEN** a packaged rule contains an `INSERT`, `UPDATE`, `DELETE`, schema modification, database attachment, `PRAGMA`, or multiple statements
- **THEN** the rule is rejected from execution and its validation failure is surfaced for diagnosis

### Requirement: Rules execute only within the selected plot scope
The application SHALL apply each compatible rule only to the selected `PlotRef` records, directly constraining plot tables by original `YD_ID` and constraining related tables through verified plot or relation identifiers.

#### Scenario: Check one plot with related records
- **WHEN** a rule targets a sample-line, quadrat, or plant-survey table related to a selected plot
- **THEN** the rule can report only rows associated with that selected plot and cannot report records belonging solely to another plot in the same ZDB

#### Scenario: Execute a batch scope
- **WHEN** the user requests county or full-area checking
- **THEN** the engine executes the same single-plot rule behavior for each selected plot reference and reports execution progress

### Requirement: Incompatible rules are isolated
The application SHALL record a rule as skipped for a plot when required tables or fields are absent or when the rule cannot safely resolve its plot scope, and SHALL continue executing other compatible rules.

#### Scenario: Missing required table
- **WHEN** a selected ZDB does not include a table required by one embedded rule
- **THEN** the application records that rule as skipped with the missing-structure reason and continues checking with other rules
