## ADDED Requirements

### Requirement: The quality workflow SHALL use a unified fixed light visual system

The application SHALL render the grassland quality-check workflow screens with a fixed light design system that replaces default mixed Material styling with consistent background, card, button, chip, and semantic-status presentation.

#### Scenario: Viewing workflow screens after redesign
- **WHEN** the user opens any quality-check workflow screen
- **THEN** the screen uses the same fixed light palette, rounded white card language, and consistent primary/secondary action styling

### Requirement: The source selection screen SHALL be rebuilt as a card-based entry page

The application SHALL present the source selection step as a large-title page with a current-directory card, scan-status card, summary metrics card, read-only information card, and bottom primary action.

#### Scenario: Source directory is available
- **WHEN** the user has a saved or newly selected data directory
- **THEN** the page shows the current directory prominently and offers both directory selection and rescanning actions within the redesigned layout

#### Scenario: Scan results are available
- **WHEN** the scan completes
- **THEN** the page shows readable ZDB count, indexed plot count, and unusable file count in the redesigned metrics section

### Requirement: The scope selection screen SHALL preserve existing behavior within the redesigned layout

The application SHALL keep county filtering, plot search, plot selection, full-check mode, test mode, and start-check behavior while presenting them within the approved redesigned scope page structure.

#### Scenario: Normal scope selection
- **WHEN** full-check mode is off
- **THEN** the user can filter by county, search plot IDs, select one plot, and start checking only after one plot is selected

#### Scenario: Full-check scope selection
- **WHEN** full-check mode is on
- **THEN** the county filter, search field, and plot selection are visually disabled in the redesigned layout
- **AND** the start action checks all indexed plots

### Requirement: The progress screen SHALL present progress as a structured review page

The application SHALL show checking progress using a redesigned progress page that includes a progress summary card, current plot information, read-only explanation, cancel explanation, and cancel action.

#### Scenario: Check is running
- **WHEN** a quality check is in progress
- **THEN** the page shows completed plot count, total plot count, current plot, and an in-progress indicator within the redesigned layout

#### Scenario: User cancels during progress
- **WHEN** the user selects the cancel action
- **THEN** the workflow preserves already completed results and later surfaces them through the existing post-cancel result flow

### Requirement: The summary screen SHALL present aggregate and per-plot results in the redesigned layout

The application SHALL render batch results using a redesigned summary page with aggregate metric blocks, a per-plot result list, and bottom actions for rerun and range changes.

#### Scenario: Summary is shown after a batch run
- **WHEN** a full-check run completes
- **THEN** the page shows aggregate counts and a list of plot result cards using the redesigned summary layout

#### Scenario: Test mode is enabled
- **WHEN** test mode is on while viewing summary
- **THEN** the page shows passed-rule and executed-rule metrics in addition to the base result metrics

### Requirement: The detail screen SHALL preserve review behavior within the redesigned review layout

The application SHALL render plot detail as a redesigned review page with a plot summary card, issue sections, ignored/passed/skipped result sections, and bottom rerun/return actions, while preserving ignore behavior and existing detail filtering.

#### Scenario: Viewing plot detail
- **WHEN** the user opens a plot detail result
- **THEN** the page shows the plot identifier, compact counts, county label, executed-rule count, and grouped issue sections in the redesigned layout

#### Scenario: Filtering detail results
- **WHEN** the user changes detail status or table-group filters
- **THEN** the page updates the visible result groups while keeping those controls visually secondary to the main review content

#### Scenario: Ignoring or restoring an issue
- **WHEN** the user marks an issue ignored or cancels ignore
- **THEN** the application preserves the existing ignore semantics and updates the redesigned detail page accordingly

### Requirement: The redesigned workflow SHALL include empty, error, cancelled, and disabled states

The application SHALL render non-happy-path states inside the same redesigned UI system instead of falling back to plain text or inconsistent controls.

#### Scenario: No matching content is available
- **WHEN** a page has no results, no matching plots, or no readable sources
- **THEN** the page shows a dedicated empty-state presentation within the redesigned visual system

#### Scenario: An error occurs
- **WHEN** scanning, checking, or selection validation produces an error message
- **THEN** the page shows that error inside a styled status block within the redesigned layout

#### Scenario: Full-check mode disables controls
- **WHEN** full-check mode is enabled on the scope page
- **THEN** the affected controls remain visible but are clearly disabled within the redesigned visual hierarchy

### Requirement: The redesigned workflow SHALL adapt to phone/tablet and portrait/landscape layouts

The application SHALL support compact, medium, and expanded layout behavior for the quality workflow screens, preserving phone-portrait fidelity while structurally adapting wide layouts.

#### Scenario: Viewing the app on a phone portrait layout
- **WHEN** the app is shown in a compact portrait window
- **THEN** the workflow screens prioritize the portrait reference structure

#### Scenario: Viewing the app on a wide layout
- **WHEN** the app is shown in landscape or tablet width
- **THEN** the workflow screens reorganize spacing and section placement to use width effectively without changing workflow behavior
