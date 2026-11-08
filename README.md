# TAKX WebP plugin

TAKX (5.8) ei kuva WebP-plaatidega kaarte (nt `KVK50T_2024_WEBP.mbtiles`): kiht imporditakse,
aga plaadid jäävad tühjaks ja logis viga ei ole.

**Põhjus:** TAKX dekodeerib plaadid `android.graphics.BitmapFactory` asendusklassiga
(`android-port-*.jar`), mis kasutab `javax.imageio.ImageIO`-d. Java ImageIO-l pole WebP lugejat,
seega tagastab `decodeByteArray` WebP puhul `null`-i.

**Lahendus:** plugin registreerib käivitusel [TwelveMonkeys](https://github.com/haraldk/TwelveMonkeys)
WebP lugeja (puhas Java, BSD-3) ImageIO globaalsesse registrisse. Plugin töötab taustal
(`AutoStartMode.background`) ega ilmu Plugins-loendisse.

## Paigaldus

1. Kopeeri `plugin.webp-<versioon>.jar` kausta `C:\Program Files\TAKX\plugins\` (vaja administraatori õigusi).
2. Käivita TAKX uuesti.
3. `takx.log`-is peab olema rida `WebP ImageIO lugeja registreeritud`.

## Ehitamine

TAK.gov Artifactoryt pole vaja: kompileeritakse paigaldatud TAKX-i jar-ide vastu.

```
./build.sh                          # TAKX kaustas C:/Program Files/TAKX
TAKX_HOME=/tee/TAKX ./build.sh
```

Vaja: JDK 17, bash (Git Bash), `unzip`. Tulemus: `build/plugin.webp-<versioon>.jar`.
TwelveMonkeys jar-id (3.15.3, SHA1 kontrollitud Maven Centralist) on kaustas `lib/`.

## Teadaolevad piirangud

- TwelveMonkeys kasutab kroma ülesvalimisel lihtsamat meetodit kui libwebp: teravatel värviservadel
  on suures suurenduses väike erinevus (keskmiselt ~1,7/255). Tavavaates silmaga ei märka.
- Dekodeerimine võtab ~10 ms plaadi kohta (puhas Java).
- Seotud TAKX Platform 5.x API-ga; suure TAKX-i uuenduse järel võib olla vaja uuesti ehitada.
