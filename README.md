# Hold My Items — Minecraft 26.2 port (unofficial)

An unofficial port of **[Hold My Items](https://modrinth.com/mod/hold-my-items) 5.1.1** by
**sapling** from Minecraft 1.21.11 to **Minecraft 26.2** (Fabric).

Hold My Items is a client-side mod that overhauls first-person item/hand animations,
driven by Lua scripts (bundled LuaJ runtime) and a built-in resource pack. The original
mod is published under **CC0-1.0** (public domain) without a source repository, so this
port was produced by decompiling the official 1.21.11 jar and migrating the code to the
26.1 APIs. The Lua scripting API surface is unchanged — existing animation packs keep
working.

> **AI assistance**: This port was developed with the help of an AI coding assistant
> (API migration, mixin rewriting and debugging were done collaboratively and
> reviewed/play-tested by the maintainer).

Not affiliated with sapling or Mojang. All credit for the mod itself goes to sapling.

## Requirements

- Minecraft **26.2**
- Fabric Loader **0.19.3+**
- Fabric API
- Java **25+** (required by Minecraft 26.1)

Drop the jar from [Releases](../../releases) (or your own build) into `mods/`.

## Building

```bash
cd mod
JAVA_HOME=<path-to-jdk-25> ./gradlew build
# output: mod/build/libs/holdmyitems-5.1.2+26.2.jar
```

## Repository layout

| Path | Purpose |
|---|---|
| `mod/` | The ported mod (Fabric Loom 1.16, unobfuscated Mojang names) |
| `decomp-env/` | Throwaway Loom project for MC 1.21.11 that remaps the original jar to Mojang names (`gradle genSources` triggers the remap; the decompile is done with Vineflower) |
| `input/` | The original `HMI 5.1.1 (1.21.11).jar` the port was made from |
| `tools/audit_mixins.py` | Verifies every mixin target against the real MC jar via `javap` |
| `docs/superpowers/` | Design spec and implementation plan for the port |

## Port notes (1.21.11 → 26.1.2)

Minecraft 26.1 is the first unobfuscated release; Yarn/intermediary are discontinued, so
the mod now compiles directly against Mojang names. Key migrations:

- `LightTexture` → `LightCoordsUtil`; `BakedQuad`/`Material`/`CameraRenderState` moved packages
- `BlockRenderDispatcher` removed → block-in-hand mixin re-hosted on `BlockStateModelSet`,
  manual quad submission via `QuadInstance`/`putBakedQuad`
- `ItemRenderer.renderItem` static gone → item mixin rewritten against
  `ItemFeatureRenderer.renderItem(ItemSubmit)`
- `RenderType.create` and pipeline snippets are no longer public → `@Invoker` accessor +
  standalone `RenderPipeline` build
- Camera view matrices are lazy in 26.1 (`matrixPropertiesDirty`) — the camera-sway mixin
  must set the dirty bits itself
- Item components are late-bound: no `getDefaultInstance()` in static initializers
- `BlockColors.getColor` → nullable `getTintSource` (null = untinted, old `-1`)

Known limitation: blocks with *special* geometry (chests, beds, banners, skulls) lose their
dynamic geometry in the "held item as 3D block" mode — 26.1 removed the special-block
re-render path (`SpecialBlockModelRenderer`). Bells still render via their dedicated hook.

## Port notes (26.1.2 → 26.2)

Minecraft 26.2 reworked the feature-render pipeline again. Key migrations:

- `MultiBufferSource`/`OutlineBufferSource` deleted; the item quad loop moved from
  `ItemFeatureRenderer.renderItem(BufferSource, OutlineBufferSource, ItemSubmit)` into a
  two-pass `buildGroup` → `prepareSubmit(submit, foil)` split (`prepareMainSubmit` /
  `prepareOutlineSubmit` / `prepareFoilSubmit`); the submit record moved to
  `ItemFeatureRenderer.Submit` (identical shape). The item mixin was rewritten against
  `prepareSubmit`, submitting quads through the inherited
  `RenderTypeFeatureRenderer.getVertexBuilder(RenderType)` (reached via a dedicated
  accessor mixin — `@Invoker` only resolves members declared on the target class itself)
  and replicating the private `getFoilBuffer` logic (`RenderTypes.glint`/`glintTranslucent`
  + `SheetedDecalTextureGenerator`).
- `ItemInHandRenderer.renderHandsWithItems`/`renderArmWithItem` renamed to
  `submitHandsWithItems`/`submitArmWithItem` (same signatures; the `@Redirect` and
  `@Shadow` follow the rename).
- `FeatureRenderDispatcher.renderAllFeatures()` → `renderAllFeatures(SubmitNodeStorage)`.
- `GameRenderer.getMainCamera()` → `mainCamera()`; `Minecraft.getToastManager()` moved to
  `Gui.toastManager()`.
- `Camera` statics `FORWARDS`/`UP`/`LEFT` retyped `Vector3f` → `Vector3fc`.
- Particle pipeline: `SourceFactor`/`DestFactor` merged into `BlendFactor`; uniforms and
  samplers moved to `BindGroupLayout`s; `withVertexFormat` split into `withVertexBinding`
  + `withPrimitiveTopology`.
- `HangingSignRenderer.submitSpecial` and `HangingSignSpecialRenderer` deleted — the
  hanging-sign damage-flash suppression is folded into the item mixin (held hanging signs
  render through the item pipeline, the overlay is dropped for them).
- Resource pack metadata: 26.2 requires `min_format`/`max_format` for ranges above 64 and
  `supported_formats` for ranges spanning 17–64; the bundled pack declares both plus
  `pack_format: 88`.
- The bundled `pack_test`'s 3D item set (buckets, boats, rafts, ...) was incomplete in the
  26.1.2 port — the original pack's `models/item/*_3d.json` element models and textures
  were dropped, so the item-definition overrides resolved to missing models and 26.2's new
  cuboid model baker crashed on them. The 1.21.11 assets are restored and repaired for
  26.2: out-of-range face UVs (BlockBench negative-UV mirroring, e.g. `[-1, 2, 0, 2]` on
  `salmon_bucket_3d`) are clamped to 0–16, and 10 `*.png.mcmeta` animation files whose
  frame lists don't match their textures (64×64 PNGs declaring 19+ frames) were removed —
  the fish-in-bucket artwork is now static instead of animated. Held buckets/boats render
  as 3D items again with zero bake warnings.
- Held-item visuals fixed for 26.2: beds now render both halves (beds use `PART`
  HEAD/FOOT, not `DOUBLE_BLOCK_HALF`, so the old code only drew the foot half); held
  hanging signs render the `attached` state so the top chain bar is back (the removed
  special renderer used the CEILING_MIDDLE model); the bucket liquid elements are inset
  0.01 to avoid coplanar z-fighting with the walls. The held-item quad submission was
  reordered to draw opaque quads before translucent ones — 26.2's translucent item
  pipeline writes depth (`DepthStencilState.DEFAULT`), so drawing the translucent water
  first depth-rejected the bucket walls behind it and made the bucket see-through.

## Version 5.1.2

Public release of the 26.2 port. The build is identical to the archived
`releases/holdmyitems-5.1.1+26.2.jar` (26.2 migration plus the held-item fixes listed
in the 26.1.2 → 26.2 port notes above); the version number was bumped to 5.1.2 for
this release. Personal grip/offset tweaks are deliberately kept out of this branch.

## License

[CC0 1.0 Universal](LICENSE) — same as the original mod.
