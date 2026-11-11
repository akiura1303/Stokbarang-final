package com.example.stokbarang;

import android.content.*;
import android.database.Cursor;
import android.database.sqlite.*;
import java.util.*;

public class DB extends SQLiteOpenHelper {
    public DB(Context c) { super(c, "stok_barang.db", null, 1); }

    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE items(id INTEGER PRIMARY KEY AUTOINCREMENT, code TEXT UNIQUE, name TEXT, category TEXT, unit TEXT, min_stock REAL DEFAULT 0, initial_stock REAL DEFAULT 0, kind TEXT, active INTEGER DEFAULT 1)");
        db.execSQL("CREATE TABLE tx(id INTEGER PRIMARY KEY AUTOINCREMENT, item_id INTEGER, item_name TEXT, category TEXT, unit TEXT, type TEXT, qty REAL, note TEXT, date TEXT, created_at INTEGER)");
        seed(db);
    }
    @Override public void onUpgrade(SQLiteDatabase db,int oldV,int newV) {}
    private void seed(SQLiteDatabase db) {
        String[][] rows = {
            {"Besi Beton","Besi Beton","kg"},{"Besi 10 mm","Besi Beton","kg"},{"Besi 12 mm","Besi Beton","kg"},
            {"Besi  D8 mm","Besi Beton","kg"},{"Besi D13 mm","Besi Beton","kg"},{"Besi D16 mm","Besi Beton","kg"},
            {"Besi D19 mm","Besi Beton","kg"},{"Besi D22 mm","Besi Beton","kg"},{"Besi D25 mm","Besi Beton","kg"},
            {"Wiremesh M6","Besi Beton","lembar"},{"Wiremesh M8","Besi Beton","lembar"},{"Kawat Bendrat","Besi Beton","kg"},
            {"Semen Portland","Beton & Semen","zak"},{"Semen PCC","Beton & Semen","zak"},{"Ready mix K-175","Beton & Semen","m3"},
            {"Ready mix K-200","Beton & Semen","m3"},{"Ready mix K-225","Beton & Semen","m3"},{"Ready mix K-250","Beton & Semen","m3"},
            {"Ready mix K-300","Beton & Semen","m3"},{"Ready mix K-350","Beton & Semen","m3"},{"Beton instan","Beton & Semen","zak"},
            {"Pasir beton","Agregat","m3"},{"Pasir pasang","Agregat","m3"},{"Batu split 1/2","Agregat","m3"},
            {"Batu split 2/3","Agregat","m3"},{"Batu screening","Agregat","m3"},{"Sirtu","Agregat","m3"},
            {"Batu kali","Agregat","m3"},{"Tanah urug","Agregat","m3"},{"Base course","Agregat","m3"},
            {"Pipa PVC 1/2 inch","Pipa","batang"},{"Pipa PVC 3/4 inch","Pipa","batang"},{"Pipa PVC 1 inch","Pipa","batang"},
            {"Pipa PVC 2 inch","Pipa","batang"},{"Pipa PVC 3 inch","Pipa","batang"},{"Pipa PVC 4 inch","Pipa","batang"},
            {"Pipa HDPE 1 inch","Pipa","meter"},{"Pipa HDPE 2 inch","Pipa","meter"},{"Pipa galvanis 1 inch","Pipa","batang"},
            {"Pipa galvanis 2 inch","Pipa","batang"},{"Pipa conduit","Pipa","batang"},{"Fitting elbow PVC","Pipa","pcs"},
            {"Fitting tee PVC","Pipa","pcs"},{"Lem PVC","Pipa","kaleng"},{"Waterstop","Pipa","meter"},
            {"Multipleks 9 mm","Bekisting","lembar"},{"Multipleks 12 mm","Bekisting","lembar"},{"Kayu kaso 4/6","Bekisting","batang"},
            {"Kayu papan 2/20","Bekisting","batang"},{"Paku 5 cm","Bekisting","kg"},{"Paku 7 cm","Bekisting","kg"},
            {"Paku 10 cm","Bekisting","kg"},{"Minyak bekisting","Bekisting","liter"},{"Tie rod","Bekisting","pcs"},
            {"Wing nut","Bekisting","pcs"},{"Form tie","Bekisting","pcs"},{"Scaffolding frame","Bekisting","set"},
            {"Cat tembok","Finishing","pail"},{"Cat besi","Finishing","kg"},{"Cat dasar","Finishing","kg"},
            {"Thinner","Finishing","liter"},{"Plamir","Finishing","kg"},{"Acian instan","Finishing","zak"},
            {"Keramik 30x30","Finishing","dus"},{"Keramik 40x40","Finishing","dus"},{"Keramik 60x60","Finishing","dus"},
            {"Granit 60x60","Finishing","dus"},{"Nat keramik","Finishing","kg"},{"Perekat keramik","Finishing","zak"},
            {"Gypsum 9 mm","Finishing","lembar"},{"Hollow galvanis","Finishing","batang"},{"Rangka plafon","Finishing","batang"},
            {"Saklar tunggal","Elektrikal","pcs"},{"Saklar ganda","Elektrikal","pcs"},{"Stop kontak","Elektrikal","pcs"},
            {"Kabel NYA 1.5 mm","Elektrikal","roll"},{"Kabel NYM 2x1.5","Elektrikal","roll"},{"Kabel NYM 3x2.5","Elektrikal","roll"},
            {"Kabel NYY 4x4","Elektrikal","meter"},{"Pipa conduit listrik","Elektrikal","batang"},{"MCB 1 phase","Elektrikal","pcs"},
            {"MCB 3 phase","Elektrikal","pcs"},{"Panel listrik","Elektrikal","unit"},{"Lampu LED 12W","Elektrikal","pcs"},
            {"Lampu LED 18W","Elektrikal","pcs"},{"Downlight","Elektrikal","pcs"},{"Junction box","Elektrikal","pcs"},
            {"Grounding rod","Elektrikal","batang"},{"Cable tray","Elektrikal","meter"},{"Klem kabel","Elektrikal","pack"},
            {"Gerinda tangan","Alat Kerja","unit"},{"Bor tangan","Alat Kerja","unit"},{"Mesin las","Alat Kerja","unit"},
            {"Concrete vibrator","Alat Kerja","unit"},{"Molen beton","Alat Kerja","unit"},{"Waterpass","Alat Kerja","unit"},
            {"Meteran 5 m","Alat Kerja","pcs"},{"Meteran 50 m","Alat Kerja","pcs"},{"Cangkul","Alat Kerja","pcs"},
            {"Sekop","Alat Kerja","pcs"},{"Palu","Alat Kerja","pcs"},{"Tang","Alat Kerja","pcs"},
            {"Kunci inggris","Alat Kerja","pcs"},{"Kunci pipa","Alat Kerja","pcs"},{"Mesin potong besi","Alat Kerja","unit"},
            {"Mesin potong keramik","Alat Kerja","unit"},{"Jack hammer","Alat Kerja","unit"},{"Genset","Alat Kerja","unit"},
            {"Pompa air","Alat Kerja","unit"},{"Selang air","Alat Kerja","meter"},{"Tangga aluminium","Alat Kerja","unit"},
            {"Helm proyek","APD","pcs"},{"Rompi safety","APD","pcs"},{"Sepatu safety","APD","pasang"},
            {"Sarung tangan","APD","pasang"},{"Kacamata safety","APD","pcs"},{"Masker respirator","APD","pcs"},
            {"Full body harness","APD","set"},{"Ear plug","APD","pasang"},{"Rambu keselamatan","APD","pcs"},
            {"Safety line","APD","roll"},{"Jaring pengaman","APD","roll"},{"APAR","APD","unit"}
        };
        int n=0;
        for(String[] r: rows) { if(n>=200) break; addSeed(db, ++n, r[0], r[1], r[2], "MATERIAL"); }
        String[][] tools = {
            {"Palu","Alat tangan","pcs"},{"Meteran 5 m","Alat ukur","pcs"},{"Waterpass","Alat ukur","unit"},
            {"Bor listrik","Mesin","unit"},{"Gerinda tangan","Mesin","unit"},{"Mesin las","Mesin","unit"},
            {"Concrete vibrator","Mesin","unit"},{"Molen beton","Mesin","unit"},{"Genset","Mesin","unit"},
            {"Pompa air","Mesin","unit"},{"Tangga aluminium","Akses kerja","unit"},{"Cangkul","Alat tangan","pcs"},
            {"Sekop","Alat tangan","pcs"},{"Linggis","Alat tangan","pcs"},{"Kunci inggris","Alat tangan","pcs"},
            {"Tang kombinasi","Alat tangan","pcs"},{"Obeng set","Alat tangan","set"},{"Kunci pipa","Alat tangan","pcs"},
            {"Mesin potong besi","Mesin","unit"},{"Jack hammer","Mesin","unit"}
        };
        int toolNo=0;
        for(String[] r: tools) addSeed(db, ++toolNo, r[0], r[1], r[2], "ALAT");
        // Lengkapi master material menjadi tepat 200 data awal dengan variasi item proyek yang umum.
        String[] extras = {"Besi siku","Besi UNP","Besi CNP","Besi hollow","Plat besi","Baut dan mur","Dynabolt","Kawat harmonika","Papan cor","Balok kayu","Bambu","Plywood film face","Pipa beton","Paving block","Kansteen","U ditch","Box culvert","Geotextile","Geogrid","Aspal hotmix","Prime coat","Tack coat","Batu bata merah","Batako","Hebel","Mortar perekat","Mortar plester","Mortar acian","Pasir urug","Batu pondasi","Batu makadam","Kerikil","Semen grouting","Non shrink grout","Waterproofing coating","Membran bakar","Sealant","Silicone","Kaca 5 mm","Kaca tempered","Aluminium frame","Kusen pintu","Daun pintu","Engsel pintu","Handle pintu","Kunci pintu","Closet duduk","Wastafel","Kran air","Floor drain","Roof drain","Talang air","Genteng metal","Spandek","Nok atap","Baja ringan","Reng baja ringan","Bondek","Wiremesh M5","Wiremesh M7","Kawat las","Elektroda las","Batu gerinda","Mata bor","Mata potong","Kabel ties","Isolasi listrik","Pipa flexible","Lampu emergency","Panel MDP","Panel SDP","Grounding cable","Terminal listrik","Stop kontak industrial","Cat marka jalan","Thinner A","Amplas","Kuas cat","Roll cat","Seal tape","Klem pipa","Flange","Valve gate","Valve ball","Check valve","Flexible joint","Manhole cover","Grating steel","Besi beton polos 8 mm","Besi beton polos 10 mm","Besi beton ulir 13 mm","Besi beton ulir 16 mm","Besi beton ulir 19 mm","Semen putih","Semen mortar","Pasir silika","Batu alam","Keramik dinding","Keramik lantai","Granit tile","Plafon PVC","Gypsum compound","List gypsum","Hollow plafon","Pintu PVC","Pintu aluminium","Kaca nako","Kabel NYA 2.5 mm","Kabel NYM 2x2.5","Kabel NYM 3x1.5","Kabel NYY 4x6","MCB  beh","Box panel","Pipa PPR","Pipa PEX","Pipa stainless","Pipa besi hitam","Pipa besi galvanis","Sambungan reducer","Sambungan socket","Sambungan union","Lem epoxy","Waterproofing integral","Plastik cor","Terpal","Karung","Bahan bakar solar","Oli mesin","Grease","Air bersih","Air curing","Kayu dolken","Besi wiremesh","Besi strip","Besi plat bordes","Plat galvanis","Paku beton","Sekrup gypsum","Sekrup roofing","Fischer plug","Besi angkur","Chemical anchor","Grout semen","Batu sikat","Grass block","Paving conblock","Pipa drainase","Pipa HDPE 4 inch","Pipa PVC AW","Pipa PVC D","Pipa PVC C","Pipa kabel","Kabel grounding","Lampu sorot","Lampu jalan","Exhaust fan","Kipas ventilasi","Kaca film","Plafon akustik","Panel ACP","Aluminium composite panel","Batu koral","Batu split 3/5","Sirtu pilihan","Tanah merah","Urugan pilihan","Aspal cair","Cat waterproof","Cat epoxy","Floor hardener","Expansion joint","Dowel bar","Tie bar","Geobag","Bronjong","Pagar proyek","Pintu pagar","Kawat duri","Jaring proyek","Papan nama proyek","Bendera keselamatan","Rambu proyek","APD tambahan"};
        int idx=0;
        for(String name: extras) {
            if(n>=200) break;
            String cat = name.toLowerCase().contains("besi") || name.toLowerCase().contains("baut") ? "Besi Beton" :
                name.toLowerCase().contains("pipa") || name.toLowerCase().contains("valve") ? "Pipa" :
                name.toLowerCase().contains("kabel") || name.toLowerCase().contains("lampu") || name.toLowerCase().contains("panel") ? "Elektrikal" :
                name.toLowerCase().contains("cat") || name.toLowerCase().contains("keramik") || name.toLowerCase().contains("pintu") ? "Finishing" : "Material Proyek";
            String unit = name.toLowerCase().contains("pipa") || name.toLowerCase().contains("kabel") ? "meter" :
                name.toLowerCase().contains("semen") || name.toLowerCase().contains("mortar") ? "zak" :
                name.toLowerCase().contains("pasir") || name.toLowerCase().contains("batu") || name.toLowerCase().contains("tanah") ? "m3" : "pcs";
            addSeed(db, ++n, name, cat, unit, "MATERIAL");
        }
    }
    private void addSeed(SQLiteDatabase db,int no,String name,String cat,String unit,String kind) {
        ContentValues v=new ContentValues(); v.put("code", (kind.equals("ALAT")?"A":"M")+String.format(Locale.US,"%03d",no));
        v.put("name",name); v.put("category",cat); v.put("unit",unit); v.put("min_stock",0); v.put("initial_stock",0); v.put("kind",kind); v.put("active",1);
        try { db.insertOrThrow("items",null,v); } catch(Exception ignored) {}
    }
    public Cursor items(String kind) {
        return getReadableDatabase().rawQuery("SELECT i.*, COALESCE(i.initial_stock,0)+COALESCE((SELECT SUM(CASE WHEN type='MASUK' THEN qty ELSE -qty END) FROM tx WHERE item_id=i.id),0) AS stock FROM items i WHERE active=1 AND kind=? ORDER BY name",new String[]{kind});
    }
    public Cursor transactions() { return getReadableDatabase().rawQuery("SELECT * FROM tx ORDER BY id DESC",null); }
    public double stock(long id) {
        Cursor c=getReadableDatabase().rawQuery("SELECT initial_stock+COALESCE((SELECT SUM(CASE WHEN type='MASUK' THEN qty ELSE -qty END) FROM tx WHERE item_id=items.id),0) FROM items WHERE id=?",new String[]{String.valueOf(id)});
        double v=0; if(c.moveToFirst()) v=c.getDouble(0); c.close(); return v;
    }
    public long saveItem(long id,String code,String name,String cat,String unit,double min,double initial,String kind) {
        ContentValues v=new ContentValues(); v.put("code",code);v.put("name",name);v.put("category",cat);v.put("unit",unit);v.put("min_stock",min);v.put("initial_stock",initial);v.put("kind",kind);v.put("active",1);
        if(id==0) return getWritableDatabase().insertOrThrow("items",null,v);
        getWritableDatabase().update("items",v,"id=?",new String[]{String.valueOf(id)}); return id;
    }
    public void deactivate(long id) { ContentValues v=new ContentValues();v.put("active",0);getWritableDatabase().update("items",v,"id=?",new String[]{String.valueOf(id)}); }
    public void addTx(long id,String name,String cat,String unit,String type,double qty,String note,String date) {
        if(type.equals("KELUAR") && qty>stock(id)) throw new IllegalArgumentException("Stok tidak mencukupi. Stok tersedia: "+stock(id));
        ContentValues v=new ContentValues();v.put("item_id",id);v.put("item_name",name);v.put("category",cat);v.put("unit",unit);v.put("type",type);v.put("qty",qty);v.put("note",note);v.put("date",date);v.put("created_at",System.currentTimeMillis());getWritableDatabase().insert("tx",null,v);
    }
}
