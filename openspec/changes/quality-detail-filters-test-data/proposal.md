## Why

The quality-check detail workflow needs clearer result drill-down for phone testing and field review. Operators need quick status counts, business-table filtering, temporarily disabled encrypted-sample rules, and a prepared local ZDB with predictable errors so the behavior can be verified on device.

## What Changes

- Add clickable colored summary blocks on the plot detail page for `全部`, `未通过`, `强制性`, `提示性`, and `已忽略`.
- Add business-table group filtering on the plot detail page for `全部`, `样地表`, `样线表`, `测产样方`, `观测样方`, and `高大草灌`.
- Treat plant survey tables as part of the corresponding sample business group rather than a separate visible filter.
- Temporarily disable the 70 JM encrypted-sample related rules without deleting rule definitions.
- Exclude disabled rules from execution and from all result counts and displays.
- Add documentation that describes the current quality-check page functions for later frontend redesign work.
- Inject deterministic errors into the local test ZDB copy at `D:\software_dev\test\综合监测-枣阳市-20260527103022.zdb`.
- Do not modify source code as part of this proposal step.

## Capabilities

### New Capabilities

- `quality-detail-result-filtering`: Plot detail result status counts, status filtering, business-table group filtering, and combined filter behavior.
- `quality-rule-soft-disable`: Temporary non-destructive disabling of JM encrypted-sample rules.
- `quality-current-page-documentation`: Current quality-check page function documentation for later visual redesign.
- `quality-test-zdb-error-preparation`: Deterministic local test ZDB error injection and verification records.

### Modified Capabilities

无。

## Impact

- Affects Android quality-check detail UI state and rendering when implemented.
- Affects rule loading or execution filtering when implemented.
- Affects result models or mapping helpers needed to derive business-table groups.
- Adds or updates focused unit tests for disabled rules, status filtering, table group filtering, and combined filtering.
- Adds a documentation artifact for existing page behavior.
- Mutates only the local test ZDB copy under `D:\software_dev\test` during implementation; the phone original ZDB remains untouched.
