package com.example.stokbarang;

import android.app.*;
import android.os.Bundle;
import android.content.*;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.view.*;
import android.widget.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainActivity extends Activity {
    DB db;
    LinearLayout root, body, top;
    String page="home", kind="MATERIAL", txType="MASUK";
    final int BLUE=Color.rgb(15,101,218), DARK=Color.rgb(25,35,52), BG=Color.rgb(246,248,252);
    final DecimalFormat fmt=new DecimalFormat("#,##0.##");
    final int REQ_CSV=44;
    String exportContent="", exportName="laporan_stok.csv";

    @Override public void onCreate(Bundle b){super.onCreate(b); db=new DB(this); getWindow().setStatusBarColor(Color.rgb(7,87,184)); getWindow().setNavigationBarColor(Color.rgb(20,30,45)); showHome();}
    int dp(float n){return (int)(n*getResources().getDisplayMetrics().density+0.5f);}
    TextView text(String s,int size,int color,boolean bold){TextView t=new TextView(this);t.setText(s);t.setTextSize(size);t.setTextColor(color);if(bold)t.setTypeface(null,Typeface.BOLD);return t;}
    LinearLayout vertical(){LinearLayout l=new LinearLayout(this);l.setOrientation(1);return l;}
    LinearLayout horizontal(){LinearLayout l=new LinearLayout(this);l.setOrientation(0);l.setGravity(Gravity.CENTER_VERTICAL);return l;}
    void base(String title, boolean back){
        root=vertical();root.setBackgroundColor(BG);setContentView(root);
        top=horizontal();top.setPadding(dp(16),dp(12),dp(16),dp(12));top.setBackgroundColor(BLUE);
        if(back){TextView b=text("‹",34,Color.WHITE,false);b.setPadding(0,0,dp(18),0);b.setOnClickListener(v->showHome());top.addView(b);}
        TextView h=text(title,20,Color.WHITE,true);top.addView(h,new LinearLayout.LayoutParams(0,dp(44),1));root.addView(top);
        ScrollView sc=new ScrollView(this);body=vertical();body.setPadding(dp(14),dp(14),dp(14),dp(24));sc.addView(body);root.addView(sc,new LinearLayout.LayoutParams(-1,0,1));
    }
    TextView button(String label,Runnable run){TextView b=text(label,15,Color.WHITE,true);b.setGravity(Gravity.CENTER);b.setPadding(dp(14),dp(13),dp(14),dp(13));b.setBackground(round(BLUE,10));b.setOnClickListener(v->run.run());return b;}
    android.graphics.drawable.GradientDrawable round(int color,int radius){android.graphics.drawable.GradientDrawable d=new android.graphics.drawable.GradientDrawable();d.setColor(color);d.setCornerRadius(dp(radius));return d;}
    void gap(int h){View v=new View(this);body.addView(v,new LinearLayout.LayoutParams(1,dp(h)));}
    void card(View v){v.setBackground(round(Color.WHITE,14));v.setElevation(dp(1));LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);p.bottomMargin=dp(10);body.addView(v,p);}
    void showHome(){
        page="home";base("Stok Barang",false);
        TextView welcome=text("Kelola persediaan material dan alat proyek dengan mudah",15,DARK,false);welcome.setPadding(dp(4),dp(6),dp(4),dp(18));body.addView(welcome);
        String[] labels={"Transaksi\nStok Masuk / Keluar","Master Data\nMaterial & Alat","Laporan\nStok & Riwayat","Pengaturan\nEkspor CSV"};
        String[] icons={"⇄","▦","▥","⚙"};
        for(int i=0;i<4;i+=2){
            LinearLayout row=horizontal();row.setGravity(Gravity.CENTER);row.setWeightSum(2);
            for(int j=i;j<i+2;j++){final int k=j;LinearLayout tile=vertical();tile.setGravity(Gravity.CENTER);tile.setPadding(dp(8),dp(20),dp(8),dp(20));tile.setBackground(round(j==0?0xFFE5F0FF:j==1?0xFFE7F7EF:j==2?0xFFF0EAFE:0xFFFFF0E8,16));
                TextView ic=text(icons[j],36,j==1?0xFF168653:BLUE,true);ic.setGravity(Gravity.CENTER);tile.addView(ic);
                TextView name=text(labels[j],15,DARK,true);name.setGravity(Gravity.CENTER);tile.addView(name);
                LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(0,dp(145),1);p.setMargins(dp(4),dp(4),dp(4),dp(8));row.addView(tile,p);
                tile.setOnClickListener(v->{if(k==0)showTransaction();else if(k==1)showMaster("MATERIAL");else if(k==2)showReports();else showSettings();});
            }body.addView(row);
        }
        LinearLayout info=horizontal();info.setPadding(dp(16),dp(18),dp(16),dp(18));info.setBackground(round(0xFFEAF1FF,14));
        info.addView(text("▣   ",24,BLUE,true));info.addView(text("Selamat Datang,\nAdministrator",15,DARK,true));card(info);
        Cursor c=db.items("MATERIAL");int count=c.getCount();c.close();TextView countText=text("Master material aktif: "+count+" item",13,0xFF667085,false);body.addView(countText);
    }
    void showMaster(String k){
        kind=k;page="master";base("Master Data - "+(k.equals("MATERIAL")?"Material":"Alat"),true);
        LinearLayout tabs=horizontal();
        TextView m=button("Material",()->showMaster("MATERIAL"));TextView a=button("Alat",()->showMaster("ALAT"));
        tabs.addView(m,new LinearLayout.LayoutParams(0,-2,1));tabs.addView(a,new LinearLayout.LayoutParams(0,-2,1));body.addView(tabs);
        gap(10);EditText search=new EditText(this);search.setSingleLine();search.setHint("Cari kode atau nama...");card(search);
        LinearLayout list=vertical();body.addView(list);
        Runnable refresh=()->{list.removeAllViews();Cursor c=db.items(kind);String q=search.getText().toString().toLowerCase(Locale.ROOT);while(c.moveToNext()){
            String code=c.getString(c.getColumnIndexOrThrow("code")),name=c.getString(c.getColumnIndexOrThrow("name")),cat=c.getString(c.getColumnIndexOrThrow("category")),unit=c.getString(c.getColumnIndexOrThrow("unit"));double stock=c.getDouble(c.getColumnIndexOrThrow("stock"));long id=c.getLong(0);
            if(!(code+" "+name+" "+cat).toLowerCase(Locale.ROOT).contains(q))continue;
            LinearLayout row=horizontal();row.setPadding(dp(12),dp(12),dp(8),dp(12));row.addView(text("📦",22,BLUE,false));LinearLayout detail=vertical();detail.setPadding(dp(10),0,0,0);detail.addView(text(name,15,DARK,true));detail.addView(text(code+" • "+cat+" • "+fmt.format(stock)+" "+unit,12,0xFF667085,false));row.addView(detail,new LinearLayout.LayoutParams(0,-2,1));TextView edit=text("›",28,0xFF667085,false);row.addView(edit);card(row);
            row.setOnClickListener(v->itemDialog(id,code,name,cat,unit,kind));}
            c.close();};
        search.addTextChangedListener(new android.text.TextWatcher(){public void beforeTextChanged(CharSequence s,int st,int c,int a){}public void onTextChanged(CharSequence s,int st,int before,int count){refresh.run();}public void afterTextChanged(android.text.Editable e){}});
        refresh.run();gap(8);body.addView(button("+ Tambah "+(kind.equals("MATERIAL")?"Material":"Alat"),()->itemDialog(0,"","","","",kind)));
    }
    EditText field(String hint,String value){EditText e=new EditText(this);e.setSingleLine();e.setText(value);e.setHint(hint);e.setTextSize(15);return e;}
    void itemDialog(long id,String code,String name,String cat,String unit,String k){
        LinearLayout form=vertical();form.setPadding(dp(8),0,dp(8),0);
        EditText fCode=field("Kode (contoh M201)",code),fName=field("Nama item",name),fCat=field("Kategori",cat),fUnit=field("Satuan (kg, m, m3, pcs)",unit),fMin=field("Stok minimum","0"),fInitial=field("Stok awal","0");
        form.addView(fCode);form.addView(fName);form.addView(fCat);form.addView(fUnit);form.addView(fMin);form.addView(fInitial);
        if(id!=0){Cursor c=db.getReadableDatabase().rawQuery("SELECT min_stock,initial_stock FROM items WHERE id=?",new String[]{String.valueOf(id)});if(c.moveToFirst()){fMin.setText(fmt.format(c.getDouble(0)));fInitial.setText(fmt.format(c.getDouble(1)));}c.close();}
        AlertDialog dialog=new AlertDialog.Builder(this).setTitle(id==0?"Tambah Data":"Edit Data").setView(form).setNegativeButton("Batal",null).setPositiveButton("Simpan",null).create();
        dialog.setOnShowListener(x->dialog.getButton(-1).setOnClickListener(v->{try{
            String cd=fCode.getText().toString().trim(),nm=fName.getText().toString().trim(),ct=fCat.getText().toString().trim(),un=fUnit.getText().toString().trim();
            if(cd.isEmpty()||nm.isEmpty()||ct.isEmpty()||un.isEmpty())throw new Exception("Kode, nama, kategori, dan satuan wajib diisi.");
            db.saveItem(id,cd,nm,ct,un,Double.parseDouble(fMin.getText().toString().replace(",",".")),Double.parseDouble(fInitial.getText().toString().replace(",",".")),k);
            dialog.dismiss();showMaster(k);
        }catch(Exception e){msg("Gagal menyimpan",e.getMessage());}}));
        if(id!=0)dialog.setButton(AlertDialog.BUTTON_NEUTRAL,"Nonaktifkan",(d,w)->new AlertDialog.Builder(this).setMessage("Nonaktifkan item ini? Riwayat transaksi tetap tersimpan.").setNegativeButton("Batal",null).setPositiveButton("Ya", (dd,ww)->{db.deactivate(id);showMaster(k);}).show());
        dialog.show();
    }
    void showTransaction(){
        page="tx";base("Transaksi",true);
        LinearLayout tabs=horizontal();tabs.addView(button("Stok Masuk",()->{txType="MASUK";showTransaction();}),new LinearLayout.LayoutParams(0,-2,1));tabs.addView(button("Stok Keluar",()->{txType="KELUAR";showTransaction();}),new LinearLayout.LayoutParams(0,-2,1));body.addView(tabs);gap(12);
        TextView type=text(txType.equals("MASUK")?"TRANSAKSI STOK MASUK":"TRANSAKSI STOK KELUAR",16,BLUE,true);body.addView(type);gap(8);
        Cursor c=db.items("MATERIAL");ArrayList<Long> ids=new ArrayList<>();ArrayList<String> names=new ArrayList<>(),cats=new ArrayList<>(),units=new ArrayList<>();while(c.moveToNext()){ids.add(c.getLong(0));names.add(c.getString(c.getColumnIndexOrThrow("name")));cats.add(c.getString(c.getColumnIndexOrThrow("category")));units.add(c.getString(c.getColumnIndexOrThrow("unit")));}c.close();
        if(ids.isEmpty()){body.addView(text("Belum ada material. Tambahkan di Master Data.",14,DARK,false));return;}
        EditText date=field("Tanggal (dd/MM/yyyy)",new SimpleDateFormat("dd/MM/yyyy",Locale.getDefault()).format(new Date()));
        Spinner sp=new Spinner(this);sp.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,names));
        EditText qty=field("Jumlah", ""),note=field("Keterangan","");
        body.addView(text("Tanggal",14,DARK,true));card(date);body.addView(text("Material",14,DARK,true));card(sp);body.addView(text("Jumlah",14,DARK,true));card(qty);body.addView(text("Keterangan",14,DARK,true));card(note);
        body.addView(button("Simpan Transaksi",()->{try{int p=sp.getSelectedItemPosition();double q=Double.parseDouble(qty.getText().toString().replace(",","."));if(q<=0)throw new Exception("Jumlah harus lebih dari nol.");db.addTx(ids.get(p),names.get(p),cats.get(p),units.get(p),txType,q,note.getText().toString(),date.getText().toString());msg("Berhasil","Transaksi tersimpan. Stok diperbarui otomatis.");showTransaction();}catch(Exception e){msg("Tidak dapat menyimpan",e.getMessage());}}));
        gap(14);body.addView(button("Lihat Riwayat Transaksi",()->showHistory()));
    }
    void showReports(){
        page="reports";base("Laporan Stok",true);
        EditText search=field("Filter nama material (opsional)","");card(search);
        LinearLayout list=vertical();body.addView(list);
        Runnable refresh=()->{list.removeAllViews();Cursor c=db.items("MATERIAL");int n=1;while(c.moveToNext()){
            String name=c.getString(c.getColumnIndexOrThrow("name")),unit=c.getString(c.getColumnIndexOrThrow("unit"));double stock=c.getDouble(c.getColumnIndexOrThrow("stock"));String cat=c.getString(c.getColumnIndexOrThrow("category"));
            if(!name.toLowerCase(Locale.ROOT).contains(search.getText().toString().toLowerCase(Locale.ROOT)))continue;
            LinearLayout r=horizontal();r.setPadding(dp(12),dp(12),dp(12),dp(12));TextView no=text(String.valueOf(n++),13,0xFF667085,false);r.addView(no);LinearLayout d=vertical();d.setPadding(dp(12),0,0,0);d.addView(text(name,15,DARK,true));d.addView(text(cat,12,0xFF667085,false));r.addView(d,new LinearLayout.LayoutParams(0,-2,1));r.addView(text(fmt.format(stock)+" "+unit,14,BLUE,true));card(r);
        }c.close();};
        search.addTextChangedListener(new android.text.TextWatcher(){public void beforeTextChanged(CharSequence s,int st,int c,int a){}public void onTextChanged(CharSequence s,int st,int b,int count){refresh.run();}public void afterTextChanged(android.text.Editable e){}});refresh.run();
        gap(8);body.addView(button("Ekspor Laporan Stok CSV",()->exportStock()));
        body.addView(button("Riwayat Transaksi",()->showHistory()));
    }
    void showHistory(){
        page="history";base("Riwayat Transaksi",true);body.addView(button("Ekspor Riwayat CSV",()->exportHistory()));gap(8);
        Cursor c=db.transactions();while(c.moveToNext()){
            String nm=c.getString(c.getColumnIndexOrThrow("item_name")),type=c.getString(c.getColumnIndexOrThrow("type")),unit=c.getString(c.getColumnIndexOrThrow("unit")),date=c.getString(c.getColumnIndexOrThrow("date")),note=c.getString(c.getColumnIndexOrThrow("note"));double qty=c.getDouble(c.getColumnIndexOrThrow("qty"));
            LinearLayout r=vertical();r.setPadding(dp(12),dp(10),dp(12),dp(10));r.addView(text(nm,15,DARK,true));r.addView(text(date+" • "+type+" • "+fmt.format(qty)+" "+unit,13,BLUE,true));if(note!=null&&!note.isEmpty())r.addView(text(note,12,0xFF667085,false));card(r);
        }c.close();
    }
    void showSettings(){
        page="settings";base("Pengaturan",true);
        body.addView(button("Ekspor Master Material CSV",()->exportMaster("MATERIAL")));
        body.addView(button("Ekspor Master Alat CSV",()->exportMaster("ALAT")));
        body.addView(button("Ekspor Laporan Stok CSV",()->exportStock()));
        body.addView(button("Ekspor Riwayat Transaksi CSV",()->exportHistory()));
        gap(12);body.addView(text("Tentang Aplikasi",17,DARK,true));body.addView(text("Stok Barang versi 1.0.0\nJava Native Android • SQLite • Offline\nPackage: com.example.stokbarang",14,0xFF667085,false));
    }
    void exportMaster(String k){
        StringBuilder s=new StringBuilder("Kode,Nama,Kategori,Satuan,Stok Minimum,Stok Awal,Stok Saat Ini\n");Cursor c=db.items(k);while(c.moveToNext()){s.append(csv(c.getString(1))).append(",").append(csv(c.getString(2))).append(",").append(csv(c.getString(3))).append(",").append(csv(c.getString(4))).append(",").append(c.getDouble(5)).append(",").append(c.getDouble(6)).append(",").append(c.getDouble(c.getColumnIndexOrThrow("stock"))).append("\n");}c.close();saveCsv(s.toString(),k.toLowerCase(Locale.ROOT)+"_stok_barang.csv");
    }
    void exportStock(){
        StringBuilder s=new StringBuilder("Kode,Nama,Kategori,Stok,Satuan,Stok Minimum\n");Cursor c=db.items("MATERIAL");while(c.moveToNext())s.append(csv(c.getString(1))).append(",").append(csv(c.getString(2))).append(",").append(csv(c.getString(3))).append(",").append(c.getDouble(c.getColumnIndexOrThrow("stock"))).append(",").append(csv(c.getString(4))).append(",").append(c.getDouble(5)).append("\n");c.close();saveCsv(s.toString(),"laporan_stok.csv");
    }
    void exportHistory(){
        StringBuilder s=new StringBuilder("Tanggal,Nama Material,Kategori,Jenis,Jumlah,Satuan,Keterangan\n");Cursor c=db.transactions();while(c.moveToNext())s.append(csv(c.getString(c.getColumnIndexOrThrow("date")))).append(",").append(csv(c.getString(c.getColumnIndexOrThrow("item_name")))).append(",").append(csv(c.getString(c.getColumnIndexOrThrow("category")))).append(",").append(csv(c.getString(c.getColumnIndexOrThrow("type")))).append(",").append(c.getDouble(c.getColumnIndexOrThrow("qty"))).append(",").append(csv(c.getString(c.getColumnIndexOrThrow("unit")))).append(",").append(csv(c.getString(c.getColumnIndexOrThrow("note")))).append("\n");c.close();saveCsv(s.toString(),"riwayat_transaksi.csv");
    }
    String csv(String x){if(x==null)return "\"\"";return "\""+x.replace("\"","\"\"")+"\"";}
    void saveCsv(String content,String name){exportContent="\uFEFF"+content;exportName=name;Intent i=new Intent(Intent.ACTION_CREATE_DOCUMENT);i.addCategory(Intent.CATEGORY_OPENABLE);i.setType("text/csv");i.putExtra(Intent.EXTRA_TITLE,name);startActivityForResult(i,REQ_CSV);}
    @Override protected void onActivityResult(int req,int res,Intent data){super.onActivityResult(req,res,data);if(req==REQ_CSV&&res==RESULT_OK&&data!=null){try(OutputStream o=getContentResolver().openOutputStream(data.getData())){o.write(exportContent.getBytes(StandardCharsets.UTF_8));msg("Ekspor berhasil","File CSV berhasil disimpan.");}catch(Exception e){msg("Gagal ekspor",e.getMessage());}}}
    void msg(String title,String message){new AlertDialog.Builder(this).setTitle(title).setMessage(message==null?"Terjadi kesalahan.":message).setPositiveButton("OK",null).show();}
}
