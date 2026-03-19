#!/bin/bash
# =====================================================
# Turkce Tabu J2ME - Termux Derleme Scripti
# Kullanim: termux'ta bu klasore gelin, ./build.sh
# =====================================================

set -e

echo ""
echo "========================================"
echo "  Turkce Tabu J2ME - Derleme Basliyor"
echo "========================================"
echo ""

# --- Gerekli araclari kontrol et ---
if ! command -v javac &>/dev/null; then
    echo "[!] javac bulunamadi. Yukleniyor..."
    pkg install -y openjdk-17
fi

if ! command -v jar &>/dev/null; then
    echo "[!] jar bulunamadi. openjdk-17 icinde olmali..."
    pkg install -y openjdk-17
fi

# --- Klasorleri olustur ---
echo "[1/4] Klasorler hazirlaniyor..."
rm -rf build/classes
mkdir -p build/classes

# --- Derle ---
echo "[2/4] Kaynak kodlar derleniyor (586 kart)..."
javac -encoding UTF-8 \
      -source 8 -target 8 \
      -d build/classes \
      src/tabu/TabuMIDlet.java \
      src/tabu/TabuData.java \
      src/tabu/GameCanvas.java

echo "      Derleme basarili!"

# --- JAR olustur ---
echo "[3/4] JAR olusturuluyor..."
jar cfm TurkceTabu.jar META-INF/MANIFEST.MF \
    -C build/classes .

# --- JAD guncelle ---
echo "[4/4] JAD dosyasi guncelleniyor..."
JARSIZE=$(wc -c < TurkceTabu.jar)
sed -i "s/MIDlet-Jar-Size:.*/MIDlet-Jar-Size: $JARSIZE/" TurkceTabu.jad

echo ""
echo "========================================"
echo "  TAMAMLANDI!"
echo "========================================"
echo ""
echo "  Olusturulan: TurkceTabu.jar ($JARSIZE byte)"
echo ""
echo "  J2ME Loader Kurulum:"
echo "  1. TurkceTabu.jar dosyasini kopyala"
echo "  2. J2ME Loader ac -> + butonu"
echo "  3. JAR dosyasini sec"
echo "  4. Config: MIDP-2.0 / CLDC-1.1"
echo "  5. Oyunu baslat!"
echo ""
echo "  Oyun Kontrolleri:"
echo "  1 = TABU (-1 puan)"
echo "  5 = DOGRU (+1 puan)"
echo "  3 = PAS (puan yok)"
echo "  * = Dur / Geri"
echo "========================================"
