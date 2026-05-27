## ADDED Requirements

### Requirement: Detail status count blocks
The system SHALL show five clickable status count blocks on the plot detail page: `全部`, `未通过`, `强制性`, `提示性`, and `已忽略`.

#### Scenario: Detail page shows status blocks
- **WHEN** a user opens a plot detail page after quality checking
- **THEN** the page displays the five status count blocks with counts for the current business group filter

#### Scenario: Status block filters result list
- **WHEN** the user taps one status count block
- **THEN** the detail list displays only results matching that status and the current business group filter

### Requirement: Failed status definition
The system SHALL define `未通过` as non-ignored mandatory issues plus non-ignored advisory issues.

#### Scenario: Failed excludes ignored and skipped results
- **WHEN** the user selects `未通过`
- **THEN** ignored issues, skipped rules, passed rules, and disabled rules are not included in the count or list

### Requirement: Detail business group filter
The system SHALL provide a business group filter with options `全部`, `样地表`, `样线表`, `测产样方`, `观测样方`, and `高大草灌`.

#### Scenario: Business group filters all result categories
- **WHEN** the user selects a business group
- **THEN** mandatory issues, advisory issues, ignored issues, skipped rules, and passed rules are filtered to that group where applicable

#### Scenario: All business group restores full group visibility
- **WHEN** the user selects `全部` in the business group filter
- **THEN** results from all recognized and unrecognized business groups are eligible for display according to the selected status filter

### Requirement: Plant survey table grouping
The system SHALL include plant survey table rules inside their corresponding `测产样方`, `观测样方`, or `高大草灌` business group rather than exposing a separate plant survey option.

#### Scenario: Plant survey result appears under sample group
- **WHEN** a plant survey rule belongs to a measurement, observation, or tall shrub sample context
- **THEN** the result appears when the corresponding business group is selected

### Requirement: Combined filter consistency
The system SHALL compute visible detail results and displayed counts from the same status and business group filter state.

#### Scenario: Counts match visible list
- **WHEN** the user changes either the status filter or the business group filter
- **THEN** the count blocks and visible result list update consistently from the same filtered result set
