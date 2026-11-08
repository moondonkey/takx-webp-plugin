#!/usr/bin/env bash
# Builds the TAKX plugin without TAK.gov Artifactory: compiles against the jars of an installed TAKX
# and bundles the TwelveMonkeys WebP decoder into the same jar.
#
# Usage:  ./build.sh            (TAKX defaults to C:/Program Files/TAKX)
#         TAKX_HOME=... ./build.sh
set -euo pipefail

VERSION=1.0.0
TAKX_HOME="${TAKX_HOME:-C:/Program Files/TAKX}"
ROOT="$(cd "$(dirname "$0")" && pwd)"
sep=":"; case "$(uname -s)" in MINGW*|MSYS*|CYGWIN*) sep=";"; ROOT="$(cd "$ROOT" && pwd -W)";; esac
OUT="$ROOT/build"
JAR="$OUT/plugin.webp-$VERSION.jar"

PLATFORM_JAR="$(ls "$TAKX_HOME"/load/platform-*.jar 2>/dev/null | sort -V | tail -1 || true)"
[ -n "$PLATFORM_JAR" ] || { echo "TAKX not found: $TAKX_HOME"; exit 1; }
PLATFORM_VERSION="$(basename "$PLATFORM_JAR" .jar)"; PLATFORM_VERSION="${PLATFORM_VERSION#platform-}"
echo "TAKX platform $PLATFORM_VERSION ($PLATFORM_JAR)"

# Compile classpath: TAKX platform + all modules (compile only, not bundled)
CP="$PLATFORM_JAR"
while IFS= read -r j; do CP="$CP$sep$j"; done < <(find "$TAKX_HOME/modules" -name '*.jar')
for j in "$ROOT"/lib/*.jar; do CP="$CP$sep$j"; done

rm -rf "$OUT"; mkdir -p "$OUT/classes"
javac --release 17 -encoding UTF-8 -proc:none -cp "$CP" -d "$OUT/classes" \
    $(find "$ROOT/src/main/java" -name '*.java')
cp -r "$ROOT/src/main/resources/." "$OUT/classes/"

# Bundle TwelveMonkeys classes (without META-INF, so nothing self-registers via ServiceLoader)
(cd "$OUT/classes" && for j in "$ROOT"/lib/*.jar; do unzip -q -o "$j"; done && rm -rf META-INF)
mkdir -p "$OUT/classes/META-INF/licenses"
cp "$ROOT/lib/TWELVEMONKEYS-LICENSE.txt" "$OUT/classes/META-INF/licenses/"

cat > "$OUT/MANIFEST.MF" <<EOF
Implementation-Version: $VERSION
Build-Date: $(date +%F)
Built-By: plugin.webp
Platform-Version: $PLATFORM_VERSION
EOF

jar --create --file "$JAR" --manifest "$OUT/MANIFEST.MF" -C "$OUT/classes" .
echo "Built: $JAR"
