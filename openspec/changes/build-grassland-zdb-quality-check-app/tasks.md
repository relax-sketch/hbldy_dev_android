## 1. Rule Assets And Test Data

- [x] 1.1 Define the embedded rule JSON schema, severity values, source metadata, rule-set version metadata, and parser validation tests.
- [x] 1.2 Extract the 264 enabled ZDB baseline rules into the embedded format, mark them mandatory, and add static safety and required-structure metadata.
- [x] 1.3 Select 10 grassland rules compatible with the verified schema, convert them to scoped SQL with explanations and actual-value output, and preserve their mandatory or advisory severity.
- [x] 1.4 Build deterministic SQLite test fixtures from the JSON business samples with matched and non-matched cases for each added test rule.

## 2. ZDB Discovery And Scope Selection

- [x] 2.1 Add domain models and repository boundaries for data-directory authorization, ZDB sources, plot references, counties, and check scopes.
- [x] 2.2 Implement persistent document-tree read authorization and recursive `.zdb` scanning with supported-source and invalid-file reporting.
- [x] 2.3 Implement read-only plot indexing from supported ZDB sources, including duplicate-display suffixes that preserve original source identity.
- [x] 2.4 Implement county filtering, manual plot-ID search/selection, and scope construction for one plot, current county, and all indexed plots.

## 3. Read-Only Quality Check Engine

- [x] 3.1 Implement rule repository loading for the packaged JSON rule set and expose active rule-set version and counts.
- [x] 3.2 Implement read-only SQLite source connections with query-only protection and verification that no source-writing operation is permitted.
- [x] 3.3 Implement table/field compatibility checks and safe skipped-rule results for missing or unscopable structures.
- [x] 3.4 Implement scoped query execution for plot, child-table, and second-level related-table rules, returning locator and actual-value issue details.
- [x] 3.5 Implement shared single-plot and batch orchestration with foreground progress updates and cancellation handling.

## 4. Result Review And Local Annotations

- [x] 4.1 Implement App-private persistence for ignored issue fingerprints without storing source business-data copies.
- [x] 4.2 Implement result classification and sorting for pending mandatory, pending advisory, and green ignored issues.
- [x] 4.3 Implement ignore, cancel-ignore, and recheck reconciliation so still-matching issues preserve annotations and no-longer-matching issues disappear from current results.

## 5. Android User Interface

- [x] 5.1 Replace the template content with Compose navigation/state for data source selection, scope selection, progress, result summary, and plot issue details.
- [x] 5.2 Build the data-source and scope UI with directory selection, scan status, county filter defaulted to all areas, plot-ID input/list selection, and three scope choices.
- [x] 5.3 Build batch progress and summary UI with aggregate issue counts and per-plot drill-down.
- [x] 5.4 Build plot detail UI showing rule severity, explanation, table, locator values, actual values, skipped rules, and ignore/recheck operations.

## 6. Verification And APK Delivery

- [x] 6.1 Add tests proving packaged SQL validation rejects write-capable statements and handles missing structures as skipped rules.
- [x] 6.2 Add fixture-based tests proving the 10 additional rules produce expected hit/pass cases and that related-table checks do not leak results across plots.
- [x] 6.3 Add tests proving single-plot, county, and full-area scopes yield consistent per-plot results and that duplicate IDs remain correctly separated by source.
- [x] 6.4 Add tests proving ignore persistence, cancel-ignore, and recheck reconciliation behavior.
- [ ] 6.5 Verify source ZDB file digests remain unchanged across scanning and checking, build an installable APK, and manually validate the real Android directory-selection workflow.
