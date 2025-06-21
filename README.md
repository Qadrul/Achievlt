# 🚀 AchievLt: Aplikasi Pengelola Tujuan Personal

Aplikasi desktop **AchievLt** (Achievement Light) adalah asisten personal Anda untuk mencapai impian! Dirancang dengan antarmuka yang modern dan intuitif, AchievLt membantu Anda memecah tujuan besar menjadi langkah-langkah kecil (*road goals*), melacak progres, dan memvisualisasikan pencapaian.

---

## ✨ Fitur Unggulan

* **Autentikasi Pengguna Aman**: Login dan register untuk menjaga privasi data tujuan Anda.
* **Manajemen Tujuan Komprehensif**:
    * Buat, lihat, dan kelola tujuan utama lengkap dengan *deadline*.
    * Setiap tujuan dapat dipecah menjadi langkah-langkah detail (*road goals*).
* **Prioritas Road Goal Berbasis OOP**:
    * Tentukan prioritas setiap *road goal* dengan tiga tingkatan: **Tinggi (Merah)** 🔴, **Sedang (Kuning)** 🟡, dan **Rendah (Hijau)** 🟢.
    * Ini adalah implementasi jelas dari **Inheritance** dan **Polimorfisme** di model bisnis kami, di mana setiap prioritas adalah sub-tipe dari `RoadGoal` dengan atribut unik:
        * **Prioritas Tinggi**: Memiliki `Due Date` untuk urgensi ekstra.
        * **Prioritas Sedang**: Memiliki `Estimasi Hari` pengerjaan.
        * **Prioritas Rendah**: Dilengkapi dengan `Catatan Tambahan` opsional.
* **Pelacakan Progres Visual**: Lacak kemajuan Anda dengan *progress bar* yang diperbarui otomatis berdasarkan *road goals* yang diselesaikan.
* **Mode Edit Interaktif**: Tambah, hapus, dan perbarui *road goals* langsung di halaman detail tujuan.
* **Antarmuka JavaFX Modern**: Didesain dengan JavaFX dan FXML, menampilkan estetika pastel yang ramah mata, sudut membulat, dan transisi halaman yang mulus.
* **Penyimpanan Data Persisten**: Seluruh data Anda disimpan dan dikelola dengan aman di database **MySQL** yang di-*host* di **Aiven**.

---

## 🛠️ Cara Menjalankan Aplikasi

Ikuti langkah-langkah berikut untuk setup dan menjalankan AchievLt di lingkungan Anda.

###  Voraussetzungen

Pastikan Anda sudah menginstal:

* **Java Development Kit (JDK)**: Versi **17** atau lebih tinggi (disarankan **JDK 21/23**).
* **IntelliJ IDEA**.
* **MySQL Server & Client** (opsional, jika ingin setup database lokal).
* **MySQL Connector/J** (driver JDBC).

### ⚙️ Instalasi & Konfigurasi Database

1.  **Kloning Repositori**:
    ```bash
    git clone [URL_REPOSITORI_ANDA]
    cd achievlt
    ```

2.  **Siapkan Database MySQL (Aiven/Hosted)**:
    * **Buat Layanan MySQL di Aiven**: Daftar di [aiven.io](https://aiven.io/) dan buat layanan MySQL baru.
    * **Ambil Kredensial**: Catat **Hostname, Port, Nama Database, Username, dan Password** layanan database Anda.
    * **Siapkan Keystore untuk SSL**:
        * Aiven memerlukan koneksi SSL. Unduh sertifikat CA dari layanan Aiven Anda.
        * Gunakan `keytool` (dari JDK Anda) untuk membuat file **Java Key Store (.jks)**. Contoh perintah:
            ```bash
            keytool -import -file /path/to/aiven_ca.pem -alias AivenCA -keystore D:\keystore.jks -storepass qwerty
            ```
            > *Ganti `/path/to/aiven_ca.pem` dengan jalur sertifikat CA Aiven Anda, dan `D:\keystore.jks` serta `qwerty` dengan lokasi dan password yang Anda inginkan.*
    * **Jalankan SQL Schema**: Gunakan *client* MySQL (misalnya Aiven Web SQL client atau terminal `mysql`) untuk membuat tabel:
        ```sql
        CREATE DATABASE IF NOT EXISTS exploresumut;
        USE exploresumut;

        CREATE TABLE IF NOT EXISTS users (
            id INT AUTO_INCREMENT PRIMARY KEY,
            username VARCHAR(50) NOT NULL UNIQUE,
            password VARCHAR(255) NOT NULL,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        );

        CREATE TABLE IF NOT EXISTS tujuan (
            id INT AUTO_INCREMENT PRIMARY KEY,
            user_id INT NOT NULL,
            nama_tujuan VARCHAR(255) NOT NULL,
            deadline DATE,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
        );

        CREATE TABLE IF NOT EXISTS road_goals (
            id INT AUTO_INCREMENT PRIMARY KEY,
            tujuan_id INT NOT NULL,
            deskripsi VARCHAR(255) NOT NULL,
            is_completed BOOLEAN DEFAULT FALSE,
            priority_type VARCHAR(20) NOT NULL DEFAULT 'LOW', -- HIGH, MEDIUM, LOW
            due_date DATE,           -- Untuk HighPriorityRoadGoal
            estimated_days INT,      -- Untuk MediumPriorityRoadGoal
            notes TEXT,              -- Untuk LowPriorityRoadGoal
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            FOREIGN KEY (tujuan_id) REFERENCES tujuan(id) ON DELETE CASCADE
        );
        ```
    * **Perbarui `DBUtil.java`**: Buka `src/main/java/com/project/config/DBUtil.java` dan sesuaikan detail koneksi Aiven Anda:
        ```java
        private static final String JDBC_URL = "jdbc:mysql://[mysql-exploresumut-exploresumut.c.aivencloud.com:12954/exploresumut](https://mysql-exploresumut-exploresumut.c.aivencloud.com:12954/exploresumut)" +
                "?useSSL=true" +
                "&requireSSL=true" +
                "&verifyServerCertificate=true" +
                "&trustCertificateKeyStoreUrl=file:D:\\keystore.jks" + // ⚠️ SESUAIKAN JALUR INI!
                "&trustCertificateKeyStorePassword=qwerty"; // ⚠️ SESUAIKAN PASSWORD KEYSTORE!
        private static final String USER = "avnadmin"; // Username Aiven Anda
        private static final String PASSWORD = "AVNS_LXE4QR_L36QEuXPf9J_"; // Password Aiven Anda
        ```

### ▶️ Menjalankan Aplikasi

1.  **Buka Proyek di IntelliJ IDEA**: Pilih **File > Open** dan arahkan ke direktori `achievlt`.

2.  **Konfigurasi Dependensi (Maven/Gradle)**:
    * Pastikan dependensi JavaFX dan MySQL Connector/J terdaftar di file build (`pom.xml` atau `build.gradle`).
    * IntelliJ biasanya akan mengunduh dependensi secara otomatis. Jika tidak, lakukan **Maven > Reload All Maven Projects** atau **Gradle > Reload Gradle Project**.

3.  **Konfigurasi VM Options JavaFX**:
    * Di IntelliJ IDEA, buka **Run > Edit Configurations...**.
    * Pilih konfigurasi *run* untuk `Main` class Anda.
    * Di bidang **VM options**, tambahkan baris berikut. **Anda perlu mengganti `C:\Users\NAMA_USER_ANDA\.m2\repository` dengan jalur repositori Maven aktual di komputer Anda** (atau jalur ke folder `lib` di JavaFX SDK jika Anda menginstalnya secara manual).
        ```
        --module-path "C:\Users\NAMA_USER_ANDA\.m2\repository\org\openjfx\javafx-controls\21.0.2;C:\Users\NAMA_USER_ANDA\.m2\repository\org\openjfx\javafx-graphics\21.0.2;C:\Users\NAMA_USER_ANDA\.m2\repository\org\openjfx\javafx-base\21.0.2;C:\Users\NAMA_USER_ANDA\.m2\repository\org\openjfx\javafx-fxml\21.0.2" --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base
        ```

4.  **Jalankan Aplikasi**:
    * Klik tombol **Run** (▶️) di IntelliJ IDEA, atau klik kanan pada `Main.java` dan pilih **Run 'Main'**.

Aplikasi AchievLt Anda kini seharusnya berjalan dan terhubung ke database MySQL yang di-*host* di Aiven! Selamat mencoba!

---
