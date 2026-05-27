## Context

The project is a single-activity Android Compose app that performs read-only quality checks on grassland ZDB data. The workflow and state model are already stable:

- Source directory selection and rescanning
- Scope selection with single-plot and full-check modes
- In-progress checking with cancel support
- Batch summary and per-plot detail review
- Ignore and unignore actions on issues

The problem is primarily front-end structure and presentation. Most of the workflow currently lives inside `QualityCheckApp.kt`, and the UI relies heavily on stock Material controls. The user has approved a high-fidelity redesign direction and explicitly wants:

- full-page visual rebuild
- fixed light theme
- tablet and orientation adaptation
- minimal backend changes
- per-page implementation grounded in the matching image under `D:\software_dev\图片参考`

## Goals / Non-Goals

**Goals:**

- Rebuild all five workflow screens so they visually align with the approved references.
- Introduce a dedicated quality-workflow design system with reusable tokens and UI primitives.
- Decompose the current screen implementation into maintainable page files and component files.
- Preserve existing workflow semantics, result routing, and interaction behavior.
- Support compact, medium, and expanded layouts for phone/tablet and portrait/landscape.
- Unify empty, error, cancelled, and disabled states into the redesigned UI language.

**Non-Goals:**

- Do not change quality rule execution behavior or ZDB read-only access.
- Do not re-architect the backend or review pipeline.
- Do not add dark theme or Android dynamic color compatibility.
- Do not build a generalized cross-project design system beyond what this workflow needs.

## Decisions

### 1. Keep backend and state flow intact, rebuild the UI layer around them

The redesign will preserve `QualityCheckViewModel`, `QualityCheckUiState`, and existing routing semantics, while replacing the current Compose screen structure and visual components.

Why:

- The workflow already matches the required behavior.
- Rebuilding UI around stable state is lower risk than mixing backend changes into a visual redesign.

Alternative considered:

- Rewriting state and routing together with the new UI. Rejected because it expands risk without user benefit.

### 2. Split the UI into route, screens, components, and design tokens

The current `QualityCheckApp.kt` will become a thin route container. Screen files will own page layout, shared components will own reusable UI primitives, and a dedicated design package will own colors, spacing, corner radii, shadows, and adaptive layout rules.

Why:

- High-fidelity redesign across five pages will become unmaintainable if it stays in one file.
- Reusable cards, chips, buttons, and top bars are needed across multiple pages.

Alternative considered:

- Keeping all UI in one file and only changing styles. Rejected because it would not scale to the required redesign.

### 3. Use a fixed light quality-workflow theme

The redesign will disable reliance on dynamic color and use a fixed palette tuned to the reference images.

Why:

- The reference direction is a tightly controlled light visual system.
- Dynamic color and dark theme would produce visible divergence from the target.

Alternative considered:

- Preserving dynamic theme compatibility. Rejected because it conflicts with high-fidelity replication.

### 4. Treat responsiveness as structural adaptation, not proportional scaling

Phone portrait remains the visual reference anchor. Wider layouts will adapt using max widths, two-column structures where helpful, and rebalanced spacing rather than trying to preserve phone proportions.

Why:

- Blindly scaling phone layouts to tablets wastes space and harms readability.
- The workflow benefits from dual-region layouts on scope, progress, and detail pages when width allows.

Alternative considered:

- Strict one-shape reuse across all devices. Rejected because it would underuse wide screens.

### 5. Preserve detail filters but visually demote them

The existing detail status and table-group filters remain part of the workflow, but they will be rendered as lighter secondary filter controls so the page still matches the review-oriented reference layout.

Why:

- The filtering behavior already exists and is useful.
- Removing it would regress functionality, while leaving it visually dominant would break the new design.

Alternative considered:

- Removing detail filters from the redesigned page. Rejected because it changes behavior.

### 6. Use the image references as an implementation constraint

Each page implementation step must begin by rereading the corresponding image from `图片参考`.

Why:

- The user explicitly requested image-by-image reconstruction rather than memory-based approximation.
- It reduces drift across a long multi-page implementation.

## Risks / Trade-offs

- [Large UI refactor across multiple screens] -> Keep state/view model intact and implement page-by-page with reusable primitives.
- [Detail filters may visually compete with the reference layout] -> Render them as secondary filter chips below the summary area instead of large tool controls.
- [Wide layouts may drift too far from the portrait reference] -> Use the portrait design as the spacing/color reference while allowing structural adaptation only where width demands it.
- [Current worktree already contains unrelated edits] -> Keep changes scoped to the redesign files and do not revert user work.
- [Reference images contain decorative status markers not backed by real data] -> Treat such elements as optional visual accents only when they do not invent false semantics.

## Migration Plan

1. Introduce the fixed light design tokens and reusable workflow components.
2. Move routing out of the current all-in-one UI file.
3. Rebuild screens one by one in this order: source, scope/full-mode, progress, summary, detail.
4. Fold error, empty, cancelled, and disabled states into the new components.
5. Run focused tests and build verification.

Rollback:

- Because backend behavior stays intact, rollback is limited to reverting the UI-layer changes if the redesign fails acceptance.

## Open Questions

无。
