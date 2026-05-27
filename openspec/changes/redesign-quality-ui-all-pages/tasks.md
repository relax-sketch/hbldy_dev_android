## 1. Design System Foundation

- [x] 1.1 Replace the current workflow theming with fixed light quality-workflow design tokens for colors, spacing, corner radii, and action styles.
- [x] 1.2 Add reusable quality workflow UI primitives for top bars, cards, chips, buttons, status blocks, and empty/error containers.
- [x] 1.3 Refactor `QualityCheckApp.kt` into a route-level container and create dedicated screen files for source, scope, progress, summary, and detail pages.

## 2. Source And Scope Screens

- [x] 2.1 Rebuild the source selection screen to match the approved entry-page structure while preserving directory selection, rescanning, scan progress, scan metrics, and read-only messaging.
- [x] 2.2 Rebuild the scope screen to match the approved portrait design with compact `全量` and `测试` controls, filter card layout, redesigned plot cards, and bottom primary action.
- [x] 2.3 Implement the redesigned full-check disabled state and status card without changing existing full-check and test-mode behavior.

## 3. Progress, Summary, And Detail Screens

- [x] 3.1 Rebuild the in-progress screen around a progress summary card, supporting status blocks, and cancel action while preserving existing cancel semantics.
- [x] 3.2 Rebuild the summary screen with redesigned aggregate metric blocks, plot result cards, and rerun/range actions while preserving test-mode metric visibility.
- [x] 3.3 Rebuild the detail screen with a redesigned summary card, issue sections, secondary filter controls, ignore/unignore actions, and bottom return actions.

## 4. Supporting States And Adaptation

- [x] 4.1 Redesign empty, error, cancelled, and disabled states so they use the same visual system across the workflow.
- [x] 4.2 Add compact/medium/expanded layout behavior for phone/tablet and portrait/landscape across the redesigned screens.
- [x] 4.3 Ensure each page implementation is validated against its matching image in `图片参考` before and during reconstruction.

## 5. Verification

- [x] 5.1 Update or add focused tests for preserved navigation, scope behavior, detail filtering, and result routing after the UI refactor.
- [x] 5.2 Run the relevant Gradle test/build checks and fix any regressions caused by the screen split and redesigned UI components.
