## Why

The current grassland quality-check app already works functionally, but its UI is still close to default Material scaffolding and does not match the approved reference designs. This change is needed now because the project already has stable workflow and result behavior, so the next highest-value step is a full front-end rebuild that raises visual quality without disturbing the quality-check backend.

## What Changes

- Redesign all five quality-check workflow screens: source selection, scope selection, progress, summary, and plot detail.
- Replace the current mixed Material styling with a fixed light visual system that matches the approved reference direction: large rounded cards, light surfaces, green gradient primary actions, semantic status chips, and visually lighter secondary controls.
- Split the current monolithic `QualityCheckApp.kt` UI into route, screen, component, and design-token layers to make the redesign maintainable.
- Preserve existing quality-check behavior, including single-plot vs full-check flow, test-mode passed-rule visibility, ignore/unignore actions, and back navigation semantics.
- Add responsive layout behavior for phone/tablet and portrait/landscape, with phone portrait as the closest reference target and wider layouts adapting structure rather than copying phone proportions.
- Bring empty, error, cancelled, and disabled states into the same visual system instead of leaving them as plain text or default controls.
- Keep implementation image-driven: each page redesign is built against its corresponding reference image in `图片参考`.

## Capabilities

### New Capabilities

- `quality-ui-full-redesign`: Rebuilds the grassland quality-check workflow UI across all main screens with a unified fixed light design system, adaptive layouts, and preserved workflow behavior.

### Modified Capabilities

无。

## Impact

- Affects Android Compose UI under `app/src/main/java/com/example/myapplication/quality/ui`.
- Affects app theming under `app/src/main/java/com/example/myapplication/ui/theme`.
- Adds new reusable UI component and design-token files for the quality workflow.
- Requires focused updates to navigation/state tests and verification of adaptive layouts.
