## Context

The Android quality-check workflow already supports plot selection, rule execution, result review, ignored issues, skipped rules, and optional display of passed rules. The next work focuses on the plot detail page and local test data preparation, while preserving the existing scope page structure.

The current rule set contains 274 rules. Seventy JM encrypted-sample related rules have been identified and must be temporarily disabled without deleting their definitions. The local test ZDB copy under `D:\software_dev\test` may be overwritten; the phone original ZDB must not be changed.

## Goals / Non-Goals

**Goals:**

- Add clickable colored status count blocks on the plot detail page.
- Add a business-table group filter that works together with status filtering.
- Keep status counts and the visible result list derived from the same filtered model.
- Soft-disable the 70 JM rules before rule execution and remove them from all statistics.
- Produce a current-page function document for later frontend redesign work.
- Inject deterministic validation errors into the local copied ZDB for phone testing.

**Non-Goals:**

- Do not redesign the quality scope page in this change.
- Do not delete rule definitions from `rule-set.json`.
- Do not change the phone original ZDB.
- Do not include target redesign requirements in the gptimg2-facing current-page document.
- Do not implement anything during the proposal step.

## Decisions

### Use a soft-disable layer for JM rules

The disabled JM rules will be filtered before execution through a stable rule ID list or equivalent explicit marker. This keeps the rule definitions available for later restoration and avoids editing rule SQL or deleting rules.

Alternative considered: delete or comment out rules in the asset file. That would be harder to reverse safely and would make it less clear which rules were intentionally disabled.

### Derive business groups from rule/table metadata

Each displayed result will expose or derive a business group from the rule target table, required tables, or referenced SQL tables. The visible groups are `全部`, `样地表`, `样线表`, `测产样方`, `观测样方`, and `高大草灌`. Plant survey rules are folded into the corresponding sample group rather than shown as a separate filter.

Alternative considered: use raw database table names in the UI. That is less usable for field review and conflicts with the requested business labels.

### Keep filtering as a view model concern

The UI should store the selected status filter and selected business group, then compute visible results and counts from one filtering model. This avoids mismatches where a count block shows one number but the list displays another set of rows.

Alternative considered: let each composable compute its own counts and list filters. That duplicates logic and makes combined filtering easier to break.

### Treat `未通过` as active non-ignored failures only

`未通过` means non-ignored mandatory issues plus non-ignored advisory issues. It does not include skipped rules, ignored issues, passed rules, or disabled rules. Skipped rules are visible only through `全部` when their business group matches.

### Keep the page documentation factual

The documentation artifact will describe the current quality-check pages and behaviors only. It will not describe desired redesigns, because it is intended as input for a later gptimg2 visual redesign pass.

### Mutate only the local test ZDB copy

The implementation will inspect the local ZDB schema first, then apply deterministic updates to create a small number of clear failures across target business groups. It will record before/after hashes and an injection summary.

## Risks / Trade-offs

- Rule-to-business-group mapping may miss unusual table references. → Keep the mapping centralized and leave unknown results visible under `全部` without forcing an incorrect specific group.
- Disabling JM rules changes global counts. → Treat disabled rules as outside the executable rule set and cover this with tests.
- The local ZDB schema may differ from assumptions. → Inspect schema before writing updates and generate updates only against verified tables/columns.
- Existing worktree changes may overlap with implementation files. → Do not revert unrelated changes; implement on top of the current worktree and stage only intended files.

## Migration Plan

1. Add filtering and rule-disable behavior behind existing app flows.
2. Run unit tests and debug build.
3. Generate the current-page function document.
4. Mutate only `D:\software_dev\test\综合监测-枣阳市-20260527103022.zdb` and record hashes plus injection summary.
5. Install the debug build and verify the prepared test ZDB on phone.

Rollback is local and file-based: restore the prior APK/build state, remove or bypass the JM disabled-rule list, and replace the local test ZDB from a clean copy if needed.

## Open Questions

无。
