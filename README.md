# TAKX WebP Imagery Support

A [TAKX](https://github.com/TAK-Product-Center/TAKX) plugin that makes WebP map tiles display,
e.g. MBTiles packages with `format=webp`.

## The problem

TAKX 5.8 imports WebP MBTiles without complaint, the layer shows up and is enabled, but the map
stays empty and nothing is logged.

TAKX decodes tiles with its `android.graphics.BitmapFactory` shim (`android-port-*.jar`), which
uses `javax.imageio.ImageIO`. Java's ImageIO has no WebP reader, so `decodeByteArray` returns
`null` for every WebP tile.

## The fix

On startup the plugin registers the [TwelveMonkeys](https://github.com/haraldk/TwelveMonkeys)
WebP reader (pure Java, BSD-3) in the global ImageIO registry. It runs in the background
(`AutoStartMode.background`) and does not appear in the Plugins list. Lossy, lossless and
alpha (VP8X) WebP are supported.

## Install

1. Download `plugin.webp-<version>.jar` from [Releases](../../releases).
2. Copy it to the TAKX `plugins` directory:
   - Windows: `C:\Program Files\TAKX\plugins\` (requires administrator rights)
   - Linux: `/opt/TAKX/plugins/` (default install location; note the upper-case `TAKX`)
3. Restart TAKX.
4. `takx.log` in the project directory should contain `WebP ImageIO reader registered`:
   - Windows: `C:\ProgramData\TAKX\ProjectData\Projects\<project>\`
   - Linux: `/usr/local/TAKX/ProjectData/Projects/<project>/`

If a WebP layer was added before the plugin was installed, toggle it off and on (or re-add it).

Tested with TAKX 5.8.0 on Windows and on Linux (Ubuntu 26.04, `takx-unix-jdk17-5.8.0.sh` installer).

## Build

No TAK.gov / Artifactory account is needed: the plugin is compiled against the jars of an
installed TAKX.

```
./build.sh                            # TAKX in C:/Program Files/TAKX
TAKX_HOME=/path/to/TAKX ./build.sh
```

Requires JDK 17, bash (e.g. Git Bash) and `unzip`. Output: `build/plugin.webp-<version>.jar`.
The TwelveMonkeys jars (3.15.3, SHA-1 verified against Maven Central) are in `lib/` and are
bundled into the plugin jar.

## Known limitations

- TwelveMonkeys uses simpler chroma upsampling than libwebp, so sharp colour edges differ
  slightly at high magnification (mean difference ~1.7/255). Not visible at normal zoom.
- Decoding takes ~10 ms per 256 px tile (pure Java).
- Built against the TAKX Platform 5.x API; a major TAKX update may require a rebuild.

## License

MIT, see [LICENSE](LICENSE). Bundled TwelveMonkeys ImageIO is BSD-3-Clause, see
[lib/TWELVEMONKEYS-LICENSE.txt](lib/TWELVEMONKEYS-LICENSE.txt).
