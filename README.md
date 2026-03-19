# 🎮 Türkçe Tabu - J2ME Oyunu

<p align="center">
  <img src="logo.png" width="120" alt="Tabu Logo"/>
</p>

<p align="center">
  <b>Nokia ve eski telefonlar için Türkçe/İngilizce Tabu oyunu</b><br>
  J2ME (MIDP-2.0 / CLDC-1.1) • 1752+ Kart • 2-6 Takım
</p>

---

## 📱 Özellikler

- **1752 Türkçe kart** + 318 İngilizce kart
- **2-6 takım** desteği
- **Takım adı** değiştirme (T9 klavye ile)
- **Dil seçimi**: Türkçe / English
- **Ayarlanabilir** tur süresi (30, 45, 60, 90, 120 sn)
- **Ayarlanabilir** pas hakkı (0, 1, 2, 3, 5, Sınırsız)
- **Tabu = -3 puan** cezası
- Nokia 6303i dahil eski telefonlarda çalışır

---

## 🕹️ Kontroller

| Tuş | Oyun | Menü |
|-----|------|------|
| **5** / Orta | ✅ Doğru (+1) | Seç |
| **1** / Sol | ❌ Tabu (-3) | — |
| **3** / Sağ | ⏭ Pas | — |
| **2** / Yukarı | — | Yukarı |
| **8** / Aşağı | — | Aşağı |
| **\*** | Dur/Geri | Geri |

---

## 🔨 Derleme (Termux / Linux)

### Gereksinimler
- Java JDK 8+
- ProGuard (Nokia gibi eski telefonlar için)

### Adımlar

```bash
# 1. Bağımlılıkları indir
wget https://repo1.maven.org/maven2/org/microemu/microemulator/2.0.4/microemulator-2.0.4.jar -O midp.jar
wget https://github.com/Guardsquare/proguard/releases/download/v7.3.2/proguard-7.3.2.zip
unzip proguard-7.3.2.zip

# 2. Derle
mkdir -p build/classes
javac -source 8 -target 8 -classpath midp.jar -d build/classes \
  src/tabu/TabuData.java \
  src/tabu/EngData.java \
  src/tabu/GameCanvas.java \
  src/tabu/TabuMIDlet.java

# 3. JAR oluştur
jar cfm TurkceTabu.jar META-INF/MANIFEST.MF -C build/classes . logo.png

# 4. Nokia için preverify (eski telefonlar)
java -jar proguard-7.3.2/lib/proguard.jar @proguard.pro
jar xf TurkceTabu_verified.jar
for f in tabu/*.class; do
  printf '\xca\xfe\xba\xbe\x00\x00\x00\x2e' | dd of=$f bs=1 count=8 conv=notrunc 2>/dev/null
done
jar cfm TurkceTabu_final.jar META-INF/MANIFEST.MF tabu/*.class logo.png
```

---

## 📲 Kurulum

### J2ME Loader (Android)
1. `TurkceTabu.jar` dosyasını Android'e kopyala
2. J2ME Loader'ı aç → `+` butonuna bas
3. JAR dosyasını seç
4. Config: **MIDP-2.0 / CLDC-1.1**

### Nokia / Eski Telefon
1. `TurkceTabu_final.jar` dosyasını telefona aktar
2. Dosya yöneticisinden aç ve kur

---

## 📁 Proje Yapısı

```
TurkceTabu/
├── src/
│   └── tabu/
│       ├── TabuMIDlet.java   # Ana MIDlet
│       ├── GameCanvas.java   # Oyun mantığı ve UI
│       ├── TabuData.java     # 1752 Türkçe kart
│       └── EngData.java      # 318 İngilizce kart
├── META-INF/
│   └── MANIFEST.MF
├── logo.png                  # Uygulama ikonu
├── proguard.pro              # ProGuard yapılandırması
├── TurkceTabu.jad
└── README.md
```

---

## 👤 Geliştirici

**UmutK** — v2.0

---

## 📄 Lisans

MIT License
