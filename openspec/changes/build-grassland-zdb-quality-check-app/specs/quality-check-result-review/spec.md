## ADDED Requirements

### Requirement: Application presents actionable quality-check results
The application SHALL present check summaries and issue details that distinguish mandatory problems, advisory problems, ignored problems, executed rules, and skipped rules.

#### Scenario: View batch summary
- **WHEN** a county or full-area check completes
- **THEN** the application shows per-plot summaries and aggregate counts for pending mandatory issues, pending advisory issues, ignored issues, executed rules, and skipped rules

#### Scenario: View issue detail
- **WHEN** the user opens a reported issue for a plot
- **THEN** the application displays the rule severity, title, explanation, checked table, record locator values, and actual matched values returned by the rule

### Requirement: User can mark a matched issue as ignored
The application SHALL allow the user to mark or unmark a currently matched issue as ignored, SHALL store that annotation only in application-private local storage, and SHALL continue to represent the issue as an SQL match.

#### Scenario: Ignore a pending issue
- **WHEN** the user marks a pending matched issue as ignored
- **THEN** the application records its issue fingerprint locally and displays the issue with a green ignored status at the end of the current plot's issue list

#### Scenario: Cancel an ignored annotation
- **WHEN** the user cancels the ignored state of a currently matched issue
- **THEN** the application removes the ignored annotation and returns the issue to the pending group appropriate to its severity

### Requirement: Rechecking preserves applicable annotations
The application SHALL support rechecking the current plot or original batch scope and SHALL restore an ignored status only for issues that remain matched with the same stable fingerprint.

#### Scenario: Ignored issue still matches after recheck
- **WHEN** an ignored issue is matched again for the same source URI, original plot ID, rule ID, and locator values
- **THEN** the application continues to display it as an ignored green issue positioned after pending issues

#### Scenario: Former issue no longer matches after recheck
- **WHEN** a previously ignored or pending issue is no longer returned by its SQL rule during the current recheck
- **THEN** the application omits that issue from the current result display

### Requirement: User can observe and cancel batch execution
The application SHALL show foreground progress while checking multiple plots and SHALL allow the user to cancel the current batch execution.

#### Scenario: Run full-area quality check
- **WHEN** the user checks all indexed plots
- **THEN** the application displays completed-versus-total progress and offers a cancellation action until execution finishes or is cancelled
