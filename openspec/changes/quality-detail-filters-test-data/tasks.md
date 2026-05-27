## 1. Rule Soft Disable

- [x] 1.1 Add a reversible disabled-rule mechanism for the 70 JM encrypted-sample related rules.
- [x] 1.2 Filter disabled rules before rule execution.
- [x] 1.3 Exclude disabled rules from mandatory, advisory, skipped, ignored, passed, failed, and total counts.
- [x] 1.4 Add tests proving exactly 70 JM rules are disabled and absent from execution results.

## 2. Detail Result Filtering

- [x] 2.1 Add status filter state for `全部`, `未通过`, `强制性`, `提示性`, and `已忽略`.
- [x] 2.2 Add business group mapping for `样地表`, `样线表`, `测产样方`, `观测样方`, and `高大草灌`.
- [x] 2.3 Fold plant survey table rules into the matching sample business group.
- [x] 2.4 Add combined status and business group filtering for detail results.
- [x] 2.5 Add clickable colored count blocks on the plot detail page.
- [x] 2.6 Add the business group filter control on the plot detail page.
- [x] 2.7 Add tests for status counts, failed definition, business group mapping, and combined filters.

## 3. Current Page Documentation

- [x] 3.1 Document the current quality-check entry and navigation flow.
- [x] 3.2 Document the current scope page controls, filters, full/test switches, and start-check behavior.
- [x] 3.3 Document the current result page statistics, plot list, and statistic-click filtering behavior.
- [x] 3.4 Document the current plot detail page summary, result lists, ignore actions, passed display, skipped display, and Android back behavior.
- [x] 3.5 Review the document to ensure it describes only current behavior and excludes target redesign requirements.

## 4. Local Test ZDB Preparation

- [x] 4.1 Inspect the local test ZDB schema and available plot records.
- [x] 4.2 Record the local test ZDB hash before mutation.
- [x] 4.3 Inject deterministic errors across plot, transect, measurement sample, observation sample, and tall shrub data where supported by schema.
- [x] 4.4 Record the local test ZDB hash after mutation.
- [x] 4.5 Produce a concise mutation summary with affected plots, tables, fields, and intended validation failure types.

## 5. Verification

- [x] 5.1 Run `.\gradlew.bat testDebugUnitTest`.
- [x] 5.2 Run `.\gradlew.bat assembleDebug`.
- [x] 5.3 Install the debug build to the test device.
- [x] 5.4 Verify detail filters and injected ZDB errors on phone.
