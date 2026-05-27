## ADDED Requirements

### Requirement: JM rules are temporarily disabled
The system SHALL temporarily disable the 70 JM encrypted-sample related rules without deleting their rule definitions.

#### Scenario: Disabled rule definitions remain available
- **WHEN** the app loads the rule set
- **THEN** the JM rule definitions still exist in the rule source or disabled-rule metadata for later restoration

### Requirement: Disabled rules do not execute
The system SHALL exclude disabled JM rules before rule execution.

#### Scenario: Quality check skips disabled rules before execution
- **WHEN** a quality check starts
- **THEN** disabled JM rules are not evaluated against the ZDB

### Requirement: Disabled rules are absent from all result statistics
The system SHALL exclude disabled JM rules from mandatory, advisory, skipped, ignored, passed, failed, and total display counts.

#### Scenario: Disabled rule does not affect detail counts
- **WHEN** a disabled JM rule would otherwise pass, fail, or skip
- **THEN** it does not appear in result lists and does not change any displayed count

### Requirement: Disabled rule count is verifiable
The implementation SHALL provide a testable way to verify that exactly 70 JM rules are disabled.

#### Scenario: Disabled count test
- **WHEN** the disabled-rule filter is tested against the bundled rule set
- **THEN** the disabled JM rule count is 70
