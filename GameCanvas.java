package tabu;

import javax.microedition.lcdui.*;
import java.util.Random;

public class GameCanvas extends Canvas implements Runnable {

    private static final int S_MENU      = 0;
    private static final int S_AYAR      = 1;
    private static final int S_TAKIM_AD  = 2;
    private static final int S_HAZIR     = 3;
    private static final int S_OYUN      = 4;
    private static final int S_FLASH     = 5;
    private static final int S_TUR_BIT   = 6;

    private static final int K0    = 48;
    private static final int K1    = 49;
    private static final int K2    = 50;
    private static final int K3    = 51;
    private static final int K4    = 52;
    private static final int K5    = 53;
    private static final int K6    = 54;
    private static final int K7    = 55;
    private static final int K8    = 56;
    private static final int K9    = 57;
    private static final int KSTAR = 42;
    private static final int KHASH = 35;

    private static final int C_BG     = 0x0D1117;
    private static final int C_CARD   = 0x161B22;
    private static final int C_DARK   = 0x21262D;
    private static final int C_BORDER = 0x30363D;
    private static final int C_RED    = 0xE94560;
    private static final int C_GREEN  = 0x3FB950;
    private static final int C_YELLOW = 0xD4A017;
    private static final int C_WHITE  = 0xF0F6FC;
    private static final int C_GRAY   = 0x8B949E;
    private static final int C_LGRAY  = 0x484F58;
    private static final int C_GOLD   = 0xFFD700;
    private static final int C_ORANGE = 0xFF8C00;

    private static final int[] SURELER   = {30,45,60,90,120};
    private static final int[] PAS_HAKKI = {0,1,2,3,5,99}; // 99=sinirsiz

    private static final String[] TUS_HARFLER = {
        " 0",".,!?1","ABC2","DEF3","GHI4","JKL5","MNO6","PQRS7","TUV8","WXYZ9"
    };

    private TabuMIDlet midlet;
    private Thread thread;
    private boolean running = true;
    private int ekran = S_MENU;
    private int W, H;

    // Ayarlar
    private int  takimSayisi = 2;
    private int  turSuresi   = 60;
    private int  sureIdx     = 2;
    private int  pasHakkiIdx = 2;   // varsayilan 2 pas
    private int  dil         = 0;   // 0=Turkce, 1=Ingilizce
    private int  ayarSec     = 0;   // 0=takim 1=sure 2=pas 3=dil

    // Oyun
    private int[]    puan;
    private String[] takimAdlari;
    private int      aktif      = 0;
    private int[]    deste;
    private int      desteIdx   = 0;
    private int      kalanSure;
    private long     turStart;
    private boolean  turAktif   = false;
    private int      turDogru   = 0;
    private int      turTabu    = 0;
    private int      turPas     = 0;
    private int      kalanPas   = 0;
    private long     flashStart = 0;

    // Takim adi
    private int    adDuzenTakim  = 0;
    private StringBuffer adDuzenBuffer = new StringBuffer();
    private int    adSonTus      = -1;
    private int    adTusIdx      = 0;
    private long   adSonZaman    = 0;

    private int menuSec = 0;
    private static final String[][] MENU_DILLER = {
        {"OYUNA BASLA","AYARLAR","TAKIM ADLARI","CIKIS"},
        {"START GAME","SETTINGS","TEAM NAMES","EXIT"}
    };

    public GameCanvas(TabuMIDlet m) {
        midlet = m;
        setFullScreenMode(true);
        W = getWidth(); H = getHeight();
        takimAdlari = new String[]{"Takim 1","Takim 2","Takim 3","Takim 4","Takim 5","Takim 6"};
        puan = new int[6];
        thread = new Thread(this);
        thread.start();
    }

    public void run() {
        while (running) {
            W = getWidth(); H = getHeight();
            if (turAktif) {
                kalanSure = turSuresi - (int)((System.currentTimeMillis()-turStart)/1000);
                if (kalanSure <= 0) { kalanSure=0; turBit(); }
            }
            if (ekran==S_FLASH && System.currentTimeMillis()-flashStart>900) ekran=S_OYUN;
            if (ekran==S_TAKIM_AD && adSonTus>=0 && System.currentTimeMillis()-adSonZaman>1500) adSonTus=-1;
            repaint();
            serviceRepaints();
            try { Thread.sleep(80); } catch (InterruptedException e) {}
        }
    }

    protected void paint(Graphics g) {
        W = getWidth(); H = getHeight();
        fillR(g, C_BG, 0, 0, W, H);
        switch(ekran) {
            case S_MENU:     paintMenu(g);    break;
            case S_AYAR:     paintAyar(g);    break;
            case S_TAKIM_AD: paintTakimAd(g); break;
            case S_HAZIR:    paintHazir(g);   break;
            case S_OYUN:     paintOyun(g);    break;
            case S_FLASH:    paintFlash(g);   break;
            case S_TUR_BIT:  paintTurBit(g);  break;
        }
    }

    // ── MENU ──────────────────────────────────────────────────────────────────
    private void paintMenu(Graphics g) {
        Font fL  = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD,  Font.SIZE_LARGE);
        Font fM  = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD,  Font.SIZE_MEDIUM);
        Font fS  = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL);
        Font fSB = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD,  Font.SIZE_SMALL);

        // Piksel T
        fillR(g, C_DARK, 0, 0, W, H/3);
        int px=W/2-30, py=10, ps=9;
        g.setColor(C_GOLD);   g.fillRect(px,    py,      60, ps*2);
        g.setColor(C_GOLD);   g.fillRect(px+22, py+ps*2, 16, ps*5);
        g.setColor(0xC8960C); g.fillRect(px+2,  py+2,    58, ps*2-2);
        g.setColor(0xC8960C); g.fillRect(px+24, py+ps*2+2,14,ps*5-2);
        g.setColor(0xFFEC8B); g.fillRect(px+4,  py+2,    10, 4);
        int logoAreaH = H/3;

        // TABU
        g.setColor(C_GOLD); g.setFont(fL);
        ciz(g, "TABU", W/2, logoAreaH+2);

        // Dil etiketi
        g.setColor(dil==0 ? C_YELLOW : C_ORANGE); g.setFont(fSB);
        String dilStr = dil==0 ? "TR" : "EN";
        String dilLabel = new StringBuffer("[").append(dilStr).append("]").toString();
        g.drawString(dilLabel, W-fSB.stringWidth(dilLabel)-5, logoAreaH+4, Graphics.TOP|Graphics.LEFT);

        // Animasyonlu kelime sayisi
        double pulse = Math.sin(System.currentTimeMillis()/400.0);
        int bright = (int)(200+55*pulse);
        g.setColor((bright<<16)|((bright*200/255)<<8)|0);
        g.setFont(fSB);
        ciz(g, "10.000+ KELIME", W/2, logoAreaH+fL.getHeight()+7+(int)(3*pulse));

        // Butonlar
        String[] menuItems = MENU_DILLER[dil];
        int btnH=Math.max(28,H/10), btnW=W*4/5, bX=(W-btnW)/2;
        int startY=logoAreaH+fL.getHeight()+fSB.getHeight()+16;
        for (int i=0; i<menuItems.length; i++) {
            int bY=startY+i*(btnH+5);
            boolean sel=(i==menuSec);
            fillRnd(g, sel?C_RED:C_DARK, bX, bY, btnW, btnH);
            g.setColor(C_BORDER); g.drawRoundRect(bX,bY,btnW,btnH,8,8);
            g.setColor(sel?C_WHITE:C_GRAY); g.setFont(fM);
            ciz(g, menuItems[i], W/2, bY+btnH/2-fM.getHeight()/2);
        }
        g.setColor(C_LGRAY); g.setFont(fS);
        ciz(g, dil==0?"2/8:Sec  5:Gir":"2/8:Select  5:Enter", W/2, H-fS.getHeight()-2);
    }

    // ── AYARLAR ───────────────────────────────────────────────────────────────
    private void paintAyar(Graphics g) {
        Font fM = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD,  Font.SIZE_MEDIUM);
        Font fS = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL);

        fillR(g, C_DARK, 0, 0, W, 30);
        g.setColor(C_GOLD); g.setFont(fM);
        ciz(g, dil==0?"AYARLAR":"SETTINGS", W/2, 7);
        g.setColor(C_GRAY); g.setFont(fS);
        g.drawString(dil==0?"* Geri":"* Back", 5, 8, Graphics.TOP|Graphics.LEFT);

        int rH=Math.max(32,H/8), rW=W-20, rX=10;

        String[] basliklar = dil==0
            ? new String[]{"TAKIM SAYISI","TUR SURESI","PAS HAKKI","DIL / LANGUAGE"}
            : new String[]{"TEAM COUNT","ROUND TIME","SKIP LIMIT","LANGUAGE"};

        // Ok goster/gizle mantigı
        String solOk0  = takimSayisi>2      ? "< " : "  ";
        String sagOk0  = takimSayisi<6      ? " >" : "  ";
        String solOk1  = sureIdx>0          ? "< " : "  ";
        String sagOk1  = sureIdx<SURELER.length-1 ? " >" : "  ";
        String solOk2  = pasHakkiIdx>0      ? "< " : "  ";
        String sagOk2  = pasHakkiIdx<PAS_HAKKI.length-1 ? " >" : "  ";
        String pasVal  = PAS_HAKKI[pasHakkiIdx]==99 ? (dil==0?"Sinirsiz":"Unlimited") : new StringBuffer().append(PAS_HAKKI[pasHakkiIdx]).append(" ").append(dil==0?"pas":"skip").toString();

        String[] degerler = {
            new StringBuffer(solOk0).append(takimSayisi).append(sagOk0).toString(),
            new StringBuffer(solOk1).append(turSuresi).append(" ").append(dil==0?"sn":"sec").append(sagOk1).toString(),
            new StringBuffer(solOk2).append(pasVal).append(sagOk2).toString(),
            new StringBuffer("< ").append(dil==0?"Turkce":"English").append(" >").toString()
        };

        for (int i=0; i<4; i++) {
            int rY=38+i*(rH+6);
            boolean sel=(ayarSec==i);
            fillRnd(g, C_CARD, rX, rY, rW, rH);
            if (sel) { g.setColor(C_YELLOW); g.drawRoundRect(rX,rY,rW,rH,8,8); }
            g.setColor(C_GRAY); g.setFont(fS);
            g.drawString(basliklar[i], rX+8, rY+4, Graphics.TOP|Graphics.LEFT);
            g.setColor(C_WHITE); g.setFont(fM);
            ciz(g, degerler[i], W/2, rY+rH/2-fM.getHeight()/2+3);
        }

        g.setColor(C_LGRAY); g.setFont(fS);
        ciz(g, dil==0?"2/8:Satir  4/6:Deger  5:Kaydet":"2/8:Row  4/6:Value  5:Save", W/2, H-fS.getHeight()-2);
    }

    // ── TAKIM ADI ─────────────────────────────────────────────────────────────
    private void paintTakimAd(Graphics g) {
        Font fM = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD,  Font.SIZE_MEDIUM);
        Font fS = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL);

        fillR(g, C_DARK, 0, 0, W, 30);
        g.setColor(C_GOLD); g.setFont(fM);
        ciz(g, "TAKIM ADLARI", W/2, 7);

        int rH=Math.max(34,H/9), rW=W-20, rX=10;
        for (int i=0; i<takimSayisi; i++) {
            int rY=38+i*(rH+5);
            boolean sel=(i==adDuzenTakim);
            fillRnd(g, sel?C_DARK:C_CARD, rX, rY, rW, rH);
            if (sel) { g.setColor(C_YELLOW); g.drawRoundRect(rX,rY,rW,rH,8,8); }
            g.setColor(C_GRAY); g.setFont(fS);
            g.drawString(new StringBuffer("Takim ").append(i+1).toString(), rX+8, rY+3, Graphics.TOP|Graphics.LEFT);
            String ad = sel ? new StringBuffer(adDuzenBuffer.toString()).append(adSonTus>=0?"_":"|").toString() : takimAdlari[i];
            g.setColor(sel?C_WHITE:C_GRAY); g.setFont(fM);
            ciz(g, ad, W/2, rY+rH/2-fM.getHeight()/2+2);
        }
        g.setColor(C_LGRAY); g.setFont(fS);
        ciz(g, "2/8:Takim  #:Sil  5:Kaydet  *:Cik", W/2, H-fS.getHeight()-2);
    }

    // ── HAZIR ─────────────────────────────────────────────────────────────────
    private void paintHazir(Graphics g) {
        Font fL = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD,  Font.SIZE_LARGE);
        Font fM = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD,  Font.SIZE_MEDIUM);
        Font fS = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL);

        g.setColor(C_GOLD); g.setFont(fL);
        ciz(g, takimAdlari[aktif], W/2, H/5);
        g.setColor(C_GRAY); g.setFont(fM);
        ciz(g, new StringBuffer("Puan: ").append(puan[aktif]).toString(), W/2, H/5+fL.getHeight()+4);

        // Bilgiler
        int iy = H/2-20;
        g.setColor(C_WHITE); g.setFont(fM);
        ciz(g, new StringBuffer("Sure: ").append(turSuresi).append(" sn").toString(), W/2, iy); iy+=fM.getHeight()+4;
        String pasStr = PAS_HAKKI[pasHakkiIdx]==99 ? "Sinirsiz" : String.valueOf(PAS_HAKKI[pasHakkiIdx]);
        ciz(g, new StringBuffer("Pas Hakki: ").append(pasStr).toString(), W/2, iy); iy+=fM.getHeight()+4;
        g.setColor(C_RED);
        ciz(g, "Tabu = -3 Puan!", W/2, iy);

        int bW=W*2/3, bH=36;
        fillRnd(g, C_GREEN, (W-bW)/2, H*3/4, bW, bH);
        g.setColor(C_BG); g.setFont(fM);
        ciz(g, "BASLAT [5]", W/2, H*3/4+bH/2-fM.getHeight()/2);
        g.setColor(C_LGRAY); g.setFont(fS);
        ciz(g, "* = Ana Menu", W/2, H-fS.getHeight()-2);
    }

    // ── OYUN ──────────────────────────────────────────────────────────────────
    private void paintOyun(Graphics g) {
        Font fL  = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD,  Font.SIZE_LARGE);
        Font fM  = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD,  Font.SIZE_MEDIUM);
        Font fS  = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL);
        Font fSB = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD,  Font.SIZE_SMALL);

        // Ust bar
        int barH=26;
        fillR(g, C_DARK, 0, 0, W, barH);
        g.setFont(fM);
        if      (kalanSure<=10) g.setColor(C_RED);
        else if (kalanSure<=20) g.setColor(C_YELLOW);
        else                    g.setColor(C_GREEN);
        g.drawString(new StringBuffer().append(kalanSure).append("s").toString(), 6, 4, Graphics.TOP|Graphics.LEFT);

        // Takim adi ve skor
        g.setColor(C_WHITE); g.setFont(fSB);
        ciz(g, new StringBuffer(takimAdlari[aktif]).append("  +").append(turDogru).append(" -").append(turTabu*3).toString(), W/2, 6);

        // Pas hakki (sag)
        if (PAS_HAKKI[pasHakkiIdx] != 99) {
            g.setColor(kalanPas<=1 ? C_RED : C_ORANGE);
            String pasStr = new StringBuffer("P:").append(kalanPas).toString();
            g.setFont(fS);
            g.drawString(pasStr, W-fS.stringWidth(pasStr)-5, 7, Graphics.TOP|Graphics.LEFT);
        }

        // Kart
        int btnH=30, hintH=fS.getHeight()+4;
        int kartY=barH+4, kartH=H-kartY-btnH-hintH-8;
        int kartX=6, kartW=W-12;

        fillRnd(g, C_CARD, kartX, kartY, kartW, kartH);
        g.setColor(C_RED); g.drawRoundRect(kartX,kartY,kartW,kartH,10,10);

        int kartIdx=deste[desteIdx];
        String anaKelime = dil==0 ? TabuData.ana(kartIdx) : EngData.ana(kartIdx);
        g.setColor(C_WHITE); g.setFont(fL);
        ciz(g, anaKelime, W/2, kartY+8);

        int ayracY=kartY+8+fL.getHeight()+6;
        g.setColor(C_RED); g.fillRect(kartX+15,ayracY,kartW-30,2);
        g.setFont(fSB);
        String yasakLabel = dil==0 ? "-- YASAK KELIMELER --" : "-- FORBIDDEN WORDS --";
        ciz(g, yasakLabel, W/2, ayracY+4);

        int yasakBasY=ayracY+fSB.getHeight()+8;
        int aralik=(kartH-(yasakBasY-kartY)-4)/5;
        for (int i=0; i<5; i++) {
            int ywY=yasakBasY+i*aralik;
            fillRnd(g, C_DARK, kartX+8, ywY, kartW-16, aralik-3);
            g.setColor(C_WHITE); g.setFont(fM);
            String yasakKelime = dil==0 ? TabuData.yasak(kartIdx,i) : EngData.yasak(kartIdx,i);
            ciz(g, yasakKelime, W/2, ywY+(aralik-3)/2-fM.getHeight()/2);
        }

        // Butonlar
        int btnY=H-btnH-hintH-2, bW3=(W-12)/3;
        fillRnd(g, 0x8B0000,    4,           btnY, bW3, btnH);
        fillRnd(g, 0x1A5E1A,    4+bW3+2,     btnY, bW3, btnH);

        // Pas butonu - kirmizi ise pas hakki bitmis
        boolean pasKaldiMi = PAS_HAKKI[pasHakkiIdx]==99 || kalanPas>0;
        fillRnd(g, pasKaldiMi?0x5E4A00:0x3A3A3A, 4+(bW3+2)*2, btnY, bW3, btnH);

        g.setColor(C_WHITE); g.setFont(fSB);
        String tabuLabel = dil==0?"TABU[1]":"TABOO[1]";
        String dogruLabel= dil==0?"DOGRU[5]":"RIGHT[5]";
        String pasLabel  = dil==0?"PAS[3]":"SKIP[3]";
        ciz(g, tabuLabel,  4+bW3/2,           btnY+btnH/2-fSB.getHeight()/2);
        ciz(g, dogruLabel, 4+bW3+2+bW3/2,     btnY+btnH/2-fSB.getHeight()/2);
        g.setColor(pasKaldiMi?C_WHITE:C_GRAY);
        ciz(g, pasLabel,   4+(bW3+2)*2+bW3/2, btnY+btnH/2-fSB.getHeight()/2);

        g.setColor(C_LGRAY); g.setFont(fS);
        String hint = dil==0?"1=Tabu(-3)  5=Dogru  3=Pas  *=Dur":"1=Taboo(-3)  5=Right  3=Skip  *=Stop";
        ciz(g, hint, W/2, H-hintH);
    }

    // ── FLASH ─────────────────────────────────────────────────────────────────
    private void paintFlash(Graphics g) {
        Font fL=Font.getFont(Font.FACE_SYSTEM,Font.STYLE_BOLD,Font.SIZE_LARGE);
        Font fM=Font.getFont(Font.FACE_SYSTEM,Font.STYLE_BOLD,Font.SIZE_MEDIUM);
        fillR(g, 0xAA0000, 0, 0, W, H);
        g.setColor(C_WHITE); g.setFont(fL);
        ciz(g, dil==0?"TABU!":"TABOO!", W/2, H/2-fL.getHeight());
        g.setFont(fM);
        ciz(g, "-3 Puan", W/2, H/2+10);
    }

    // ── TUR BITTI ─────────────────────────────────────────────────────────────
    private void paintTurBit(Graphics g) {
        Font fM=Font.getFont(Font.FACE_SYSTEM,Font.STYLE_BOLD, Font.SIZE_MEDIUM);
        Font fS=Font.getFont(Font.FACE_SYSTEM,Font.STYLE_PLAIN,Font.SIZE_SMALL);

        fillR(g, C_DARK, 0, 0, W, 34);
        g.setColor(C_GOLD); g.setFont(fM);
        String turBitStr = new StringBuffer(dil==0?"TUR BITTI - ":"ROUND OVER - ").append(takimAdlari[aktif]).toString();
        ciz(g, turBitStr, W/2, 9);

        int y=44;
        g.setColor(C_WHITE); g.setFont(fM);
        ciz(g, new StringBuffer(dil==0?"Dogru: ":"Right: ").append(turDogru).append("  Tabu: ").append(turTabu).toString(), W/2, y);
        y+=fM.getHeight()+6;
        int net=turDogru-(turTabu*3);
        g.setColor(net>=0?C_GREEN:C_RED);
        ciz(g, new StringBuffer(dil==0?"Bu turden: ":"This round: ").append(net>=0?"+":"").append(net).append(" puan").toString(), W/2, y);
        y+=fM.getHeight()+12;

        g.setColor(C_GRAY); g.setFont(fS);
        ciz(g, dil==0?"- PUAN TABLOSU -":"- SCOREBOARD -", W/2, y);
        y+=fS.getHeight()+6;

        int rH=Math.max(24,H/14);
        for (int i=0; i<takimSayisi; i++) {
            boolean ben=(i==aktif);
            fillRnd(g, ben?0x1A3A1A:C_DARK, 10, y, W-20, rH);
            if (ben) { g.setColor(C_GREEN); g.drawRoundRect(10,y,W-20,rH,6,6); }
            g.setColor(ben?C_GREEN:C_WHITE); g.setFont(fM);
            ciz(g, new StringBuffer(takimAdlari[i]).append(": ").append(puan[i]).append(" p").toString(), W/2, y+rH/2-fM.getHeight()/2);
            y+=rH+4;
        }
        y+=5;
        int bW=W-20, bH=26;
        fillRnd(g, C_GREEN, 10, y, bW, bH);
        g.setColor(C_BG); g.setFont(fM);
        ciz(g, dil==0?"SONRAKI TAKIM [5]":"NEXT TEAM [5]", W/2, y+bH/2-fM.getHeight()/2);
        y+=bH+5;
        fillRnd(g, C_DARK, 10, y, bW, bH);
        g.setColor(C_WHITE);
        ciz(g, dil==0?"ANA MENU [*]":"MAIN MENU [*]", W/2, y+bH/2-fM.getHeight()/2);
    }

    // ── TUS GIRISLERI ─────────────────────────────────────────────────────────
    protected void keyPressed(int k) {
        int a=getGameAction(k);
        switch(ekran) {
            case S_MENU:     menuTus(k,a);    break;
            case S_AYAR:     ayarTus(k,a);    break;
            case S_TAKIM_AD: takimAdTus(k,a); break;
            case S_HAZIR:    hazirTus(k,a);   break;
            case S_OYUN:     oyunTus(k,a);    break;
            case S_TUR_BIT:  turBitTus(k,a);  break;
        }
        repaint();
    }

    private void menuTus(int k, int a) {
        int len = MENU_DILLER[dil].length;
        if (a==UP  ||k==K2) menuSec=(menuSec-1+len)%len;
        if (a==DOWN||k==K8) menuSec=(menuSec+1)%len;
        if (a==FIRE||k==K5) {
            switch(menuSec) {
                case 0: oyunuBaslat(); break;
                case 1: ekran=S_AYAR; ayarSec=0; break;
                case 2: takimAdAc(); break;
                case 3: midlet.exit(); break;
            }
        }
    }

    private void ayarTus(int k, int a) {
        if (k==KSTAR) { ekran=S_MENU; return; }
        if (a==UP  ||k==K2) ayarSec=(ayarSec-1+4)%4;
        if (a==DOWN||k==K8) ayarSec=(ayarSec+1)%4;
        switch(ayarSec) {
            case 0:
                if (a==LEFT ||k==K4) { if (takimSayisi>2) takimSayisi--; }
                if (a==RIGHT||k==K6) { if (takimSayisi<6) takimSayisi++; }
                break;
            case 1:
                if (a==LEFT ||k==K4) { if (sureIdx>0) { sureIdx--; turSuresi=SURELER[sureIdx]; } }
                if (a==RIGHT||k==K6) { if (sureIdx<SURELER.length-1) { sureIdx++; turSuresi=SURELER[sureIdx]; } }
                break;
            case 2:
                if (a==LEFT ||k==K4) { if (pasHakkiIdx>0) pasHakkiIdx--; }
                if (a==RIGHT||k==K6) { if (pasHakkiIdx<PAS_HAKKI.length-1) pasHakkiIdx++; }
                break;
            case 3:
                if (a==LEFT||k==K4||a==RIGHT||k==K6) dil=1-dil;
                break;
        }
        if (a==FIRE||k==K5) ekran=S_MENU;
    }

    private void takimAdTus(int k, int a) {
        if (k==KSTAR) { takimAdlari[adDuzenTakim]=adDuzenBuffer.length()>0?adDuzenBuffer.toString():(new StringBuffer("Takim ").append(adDuzenTakim+1).toString()); ekran=S_MENU; return; }
        if (a==FIRE||k==K5) {
            takimAdlari[adDuzenTakim]=adDuzenBuffer.length()>0?adDuzenBuffer.toString():(new StringBuffer("Takim ").append(adDuzenTakim+1).toString());
            if (adDuzenTakim<takimSayisi-1) { adDuzenTakim++; adDuzenBuffer=new StringBuffer(takimAdlari[adDuzenTakim]); adSonTus=-1; }
            else ekran=S_MENU;
            return;
        }
        if (a==UP  ||k==K2) { takimAdlari[adDuzenTakim]=adDuzenBuffer.length()>0?adDuzenBuffer.toString():(new StringBuffer("Takim ").append(adDuzenTakim+1).toString()); adDuzenTakim=(adDuzenTakim-1+takimSayisi)%takimSayisi; adDuzenBuffer=new StringBuffer(takimAdlari[adDuzenTakim]); adSonTus=-1; return; }
        if (a==DOWN||k==K8) { takimAdlari[adDuzenTakim]=adDuzenBuffer.length()>0?adDuzenBuffer.toString():(new StringBuffer("Takim ").append(adDuzenTakim+1).toString()); adDuzenTakim=(adDuzenTakim+1)%takimSayisi; adDuzenBuffer=new StringBuffer(takimAdlari[adDuzenTakim]); adSonTus=-1; return; }
        if (k==KHASH) { if (adDuzenBuffer.length()>0) adDuzenBuffer.deleteCharAt(adDuzenBuffer.length()-1); adSonTus=-1; return; }
        int tusNo=-1;
        if (k==K0)tusNo=0; else if(k==K1)tusNo=1; else if(k==K2)tusNo=2; else if(k==K3)tusNo=3;
        else if(k==K4)tusNo=4; else if(k==K5)tusNo=5; else if(k==K6)tusNo=6;
        else if(k==K7)tusNo=7; else if(k==K8)tusNo=8; else if(k==K9)tusNo=9;
        if (tusNo>=0) {
            String harfler=TUS_HARFLER[tusNo];
            long now=System.currentTimeMillis();
            if (adSonTus==tusNo && now-adSonZaman<1500) { if(adDuzenBuffer.length()>0) adDuzenBuffer.deleteCharAt(adDuzenBuffer.length()-1); adTusIdx=(adTusIdx+1)%harfler.length(); }
            else adTusIdx=0;
            adDuzenBuffer.append(harfler.charAt(adTusIdx));
            adSonTus=tusNo; adSonZaman=now;
        }
    }

    private void hazirTus(int k, int a) {
        if (k==KSTAR) { ekran=S_MENU; return; }
        if (a==FIRE||k==K5) turBaslat();
    }

    private void oyunTus(int k, int a) {
        if (!turAktif) return;
        if (k==KSTAR)        { turDur(); return; }
        if (a==FIRE||k==K5)  { dogruYap(); return; }
        // TABU: 1, sol ok, sol ust soft key (-6 veya -21)
        if (k==K1||a==LEFT||k==-6||k==-21)  { tabuYap(); return; }
        // PAS: 3, sag ok, sag ust soft key (-7 veya -22)
        if (k==K3||a==RIGHT||k==-7||k==K9||k==-22) { pasYap(); }
    }

    private void turBitTus(int k, int a) {
        if (k==KSTAR)                        { ekran=S_MENU; return; }
        if (a==FIRE||k==K5||a==DOWN||k==K8)  sonrakiTakim();
    }

    // ── OYUN EYLEMLERI ────────────────────────────────────────────────────────
    private void oyunuBaslat() {
        puan=new int[takimSayisi]; aktif=0;
        int toplam = dil==0 ? TabuData.TOPLAM : EngData.TOPLAM;
        deste=karistir(toplam); desteIdx=0;
        ekran=S_HAZIR;
    }

    private void takimAdAc() { adDuzenTakim=0; adDuzenBuffer=new StringBuffer(takimAdlari[0]); adSonTus=-1; ekran=S_TAKIM_AD; }

    private void turBaslat() {
        turDogru=0; turTabu=0; turPas=0;
        kalanPas = PAS_HAKKI[pasHakkiIdx];
        turStart=System.currentTimeMillis();
        turAktif=true; ekran=S_OYUN;
    }

    private void dogruYap() { turDogru++; sonrakiKart(); }

    private void tabuYap()  { turTabu++; flashStart=System.currentTimeMillis(); ekran=S_FLASH; sonrakiKart(); }

    private void pasYap() {
        if (PAS_HAKKI[pasHakkiIdx]==99) { sonrakiKart(); return; }
        if (kalanPas>0) { kalanPas--; turPas++; sonrakiKart(); }
        // pas hakki bitmisse hicbir sey yapma
    }

    private void sonrakiKart() { desteIdx=(desteIdx+1)%deste.length; }

    private void turDur() {
        turAktif=false;
        puan[aktif]+=turDogru-(turTabu*3);
        if(puan[aktif]<0) puan[aktif]=0;
        ekran=S_TUR_BIT;
    }
    private void turBit() {
        turAktif=false;
        puan[aktif]+=turDogru-(turTabu*3);
        if(puan[aktif]<0) puan[aktif]=0;
        ekran=S_TUR_BIT;
    }
    private void sonrakiTakim() { aktif=(aktif+1)%takimSayisi; ekran=S_HAZIR; }

    private int[] karistir(int n) {
        int[] a=new int[n];
        for(int i=0;i<n;i++) a[i]=i;
        Random r=new Random();
        for(int i=n-1;i>0;i--) { int j=(r.nextInt()&0x7FFFFFFF)%(i+1); int t=a[i];a[i]=a[j];a[j]=t; }
        return a;
    }
    private void fillR  (Graphics g,int c,int x,int y,int w,int h) { g.setColor(c);g.fillRect(x,y,w,h); }
    private void fillRnd(Graphics g,int c,int x,int y,int w,int h) { g.setColor(c);g.fillRoundRect(x,y,w,h,8,8); }
    private void ciz    (Graphics g,String s,int cx,int y)          { g.drawString(s,cx-g.getFont().stringWidth(s)/2,y,Graphics.TOP|Graphics.LEFT); }
    private static String cat(String a, String b)                   { return new StringBuffer(a).append(b).toString(); }
    private static String cat(String a, int b)                      { return new StringBuffer(a).append(b).toString(); }
    private static String cat(String a, int b, String c)            { return new StringBuffer(a).append(b).append(c).toString(); }
}
