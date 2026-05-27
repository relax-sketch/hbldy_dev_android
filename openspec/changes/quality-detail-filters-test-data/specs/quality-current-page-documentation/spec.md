## ADDED Requirements

### Requirement: Current page function document
The project SHALL include a document describing the current quality-check page functions for later frontend redesign work.

#### Scenario: Documentation describes current pages
- **WHEN** the document is opened
- **THEN** it describes the current quality-check entry, scope page, result page, plot detail page, and Android back behavior

### Requirement: Documentation excludes target redesign requirements
The current-page function document SHALL describe existing behavior only and MUST NOT include target redesign requirements from this change.

#### Scenario: Documentation remains factual
- **WHEN** the document is reviewed as gptimg2 input
- **THEN** it can be used to understand current pages without mixing in requested future UI changes
