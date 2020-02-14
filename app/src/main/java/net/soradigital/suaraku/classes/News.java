package net.soradigital.suaraku.classes;

public class News {
    private int id_berita;
    private int id_kategori;
    private String nama_lengkap;
    private String judul;
    private String tanggal;
    private String gambar;

    public int getId_berita() {
        return id_berita;
    }

    public void setId_berita(int id_berita) {
        this.id_berita = id_berita;
    }

    public int getId_kategori() {
        return id_kategori;
    }

    public void setId_kategori(int id_kategori) {
        this.id_kategori = id_kategori;
    }

    public String getNama_lengkap() {
        return nama_lengkap;
    }

    public void setNama_lengkap(String nama_lengkap) {
        this.nama_lengkap = nama_lengkap;
    }

    public String getJudul() {
        return judul;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getGambar() {
        return gambar;
    }

    public void setGambar(String gambar) {
        this.gambar = gambar;
    }
}
