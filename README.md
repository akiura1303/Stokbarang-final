# Stok Barang (Android Java)

Aplikasi inventaris offline berbasis Java Native Android dan SQLite. Package: `com.example.stokbarang`.

## Fitur
- 200 master material awal dan master alat; tambah, edit, nonaktifkan, pencarian.
- Kode, nama, kategori, satuan, stok minimum, stok awal.
- Transaksi stok masuk/keluar; stok dihitung otomatis.
- Riwayat transaksi, laporan stok, filter tanggal sederhana.
- Ekspor CSV UTF-8 melalui pemilih lokasi penyimpanan Android.
- Data tersimpan lokal di SQLite; tidak membutuhkan internet.

## Build APK melalui GitHub
1. Buat repository GitHub baru bernama `StokBarang`.
2. Ekstrak seluruh isi ZIP ini, lalu upload semua file/folder ke root repository (folder `.github` harus ikut).
3. Buka tab **Actions**. Workflow **Build Android APK** akan berjalan otomatis. Jika belum, pilih workflow lalu **Run workflow**.
4. Setelah status hijau, buka hasil run → **Artifacts** → unduh `StokBarang-debug`.
5. Ekstrak ZIP artifact, lalu install `app-debug.apk` di Infinix Note 50. Jika diminta, izinkan instalasi dari sumber tersebut.

## Catatan build
Workflow memakai Gradle 8.9 + Android Gradle Plugin 8.7.3 + JDK 17. Tidak menggunakan `gradlew`, sehingga tidak akan gagal karena file `gradlew` tidak ada. Build pertama membutuhkan internet pada GitHub Actions untuk mengunduh Android SDK/Gradle dependencies; aplikasi setelah terpasang berjalan offline.

## Penyimpanan dan CSV
Database berada di penyimpanan privat aplikasi (SQLite). CSV diekspor melalui Android Storage Access Framework sehingga pengguna memilih folder/nama file. Hapus data aplikasi akan menghapus database lokal; ekspor CSV secara berkala untuk arsip.
