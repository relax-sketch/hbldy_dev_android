## ADDED Requirements

### Requirement: Local test ZDB only
The system SHALL mutate only the local test ZDB copy at `D:\software_dev\test\综合监测-枣阳市-20260527103022.zdb` when preparing phone test data.

#### Scenario: Phone original is not modified
- **WHEN** test data preparation runs
- **THEN** no write is performed against the phone original ZDB path

### Requirement: Deterministic error injection
The test data preparation SHALL inject stable, reproducible data errors into the local test ZDB.

#### Scenario: Repeatable injected errors
- **WHEN** the preparation process is run against the same clean input ZDB
- **THEN** it produces the same set of intended table and field changes

### Requirement: Error coverage across business groups
The injected test errors SHALL cover key business groups including plot, transect, measurement sample, observation sample, and tall shrub data where the schema supports them.

#### Scenario: Business group coverage summary
- **WHEN** preparation finishes
- **THEN** the output summary lists affected plots, tables, fields, and intended validation failure types

### Requirement: Hash and summary records
The preparation process SHALL record the local ZDB hash before and after mutation and produce a concise mutation summary.

#### Scenario: Hashes prove local mutation
- **WHEN** preparation finishes
- **THEN** the before hash, after hash, and mutation summary are available for verification
