#!/usr/bin/env bash
# Ehitab TAKX-i plugina ilma TAK.gov Artifactoryta: kompileerib paigaldatud TAKX-i jar-ide vastu
# ja pakib TwelveMonkeys WebP dekoodri samasse jar-faili.
#
# Kasutus:  ./build.sh            (TAKX vaikimisi C:/Program Files/TAKX)
#           TAKX_HOME=... ./build.sh
set -euo pipefail

VERSION=1.0.0
TAKX_HOME="${TAKX_HOME:-C:/Program Files/TAKX}"
ROOT="$(cd "$(dirname "$0")" && pwd)"
sep=":"; case "$(uname -s)" in MINGW*|MSYS*|CYGWIN*) sep=";"; ROOT="$(cd "$ROOT" && pwd -W)";; esac
OUT="$ROOT/build"
JAR="$OUT/plugin.webp-$VERSION.jar"

[ -f "$TAKX_HOME/load/platform-5.8.0.jar" ] || { echo "TAKX-i ei leitud: $TAKX_HOME"; exit 1; }


# Kompileerimise klassirada: TAKX-i platvorm + kõik moodulid (ainult kompileerimiseks, jar-i ei lähe)
CP="$TAKX_HOME/load/platform-5.8.0.jar"
while IFS= read -r j; do CP="$CP$sep$j"; done < <(find "$TAKX_HOME/modules" -name '*.jar')
for j in "$ROOT"/lib/*.jar; do CP="$CP$sep$j"; done

rm -rf "$OUT"; mkdir -p "$OUT/classes"
javac --release 17 -encoding UTF-8 -proc:none -cp "$CP" -d "$OUT/classes" \
    $(find "$ROOT/src/main/java" -name '*.java')
cp -r "$ROOT/src/main/resources/." "$OUT/classes/"

# TwelveMonkeys klassid sisse (ilma META-INF-ita, et miski ei registreeruks ServiceLoaderi kaudu)
(cd "$OUT/classes" && for j in "$ROOT"/lib/*.jar; do unzip -q -o "$j"; done && rm -rf META-INF)
mkdir -p "$OUT/classes/META-INF/licenses"
cp "$ROOT/lib/TWELVEMONKEYS-LICENSE.txt" "$OUT/classes/META-INF/licenses/"

cat > "$OUT/MANIFEST.MF" <<EOF
Implementation-Version: $VERSION
Build-Date: $(date +%F)
Built-By: plugin.webp
Platform-Version: 5.8.0
EOF

jar --create --file "$JAR" --manifest "$OUT/MANIFEST.MF" -C "$OUT/classes" .
echo "Valmis: $JAR"
