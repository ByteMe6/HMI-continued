# Hold My Items — Minecraft 26.1.2 port (unofficial)

An unofficial port of **[Hold My Items](https://modrinth.com/mod/hold-my-items) 5.1.1** by **sapling**
from Minecraft 1.21.11 to **Minecraft 26.1.2** (Fabric).

Hold My Items is a client-side mod that overhauls first-person item/hand animations,
driven by Lua scripts (bundled LuaJ runtime) and a built-in resource pack. The original
mod is published under **CC0-1.0** (public domain) without a source repository, so this
port was produced by decompiling the official 1.21.11 jar and migrating the code to the
26.1 APIs. The Lua scripting API surface is unchanged — existing animation packs keep
working.

Not affiliated with sapling or Mojang. All credit for the mod itself goes to sapling.

## Requirements

- Minecraft **26.1.2**
- Fabric Loader **0.19.3+**
- Fabric API
- Java **25+** (required by Minecraft 26.1)

Drop the jar from [Releases](../../releases) (or your own build) into `mods/`.

## Building

```bash
cd mod
JAVA_HOME=<path-to-jdk-25> ./gradlew build
# output: mod/build/libs/holdmyitems-5.1.1+26.1.2.jar
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

## License

[CC0 1.0 Universal](LICENSE) — same as the original mod.
