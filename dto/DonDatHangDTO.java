package dto;
import java.sql.Timestamp;

public class DonDatHangDTO {
    private int maDon;
    private int maNV;
    private String loai;
    private String trangThai;
    private Timestamp ngayDat;
    private Timestamp ngayThanhToan;
    private long soTienDaTra;
    private long tongTien;
    private int giamGia;

    public DonDatHangDTO(int maDon, int maNV, String loai, String trangThai, 
                         Timestamp ngayDat, Timestamp ngayThanhToan, long soTienDaTra, long tongTien, int giamGia) {
        this.maDon = maDon;
        this.maNV = maNV;
        this.loai = loai;
        this.trangThai = trangThai;
        this.ngayDat = ngayDat;
        this.ngayThanhToan = ngayThanhToan;
        this.soTienDaTra = soTienDaTra;
        this.tongTien = tongTien;
        this.giamGia = giamGia;
    }

    public int getMaDon() { return maDon; }
    public void setMaDon(int maDon) { this.maDon = maDon; }
    public int getMaNV() { return maNV; }
    public void setMaNV(int maNV) { this.maNV = maNV; }
    public String getLoai() { return loai; }
    public void setLoai(String loai) { this.loai = loai; }
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    public Timestamp getNgayDat() { return ngayDat; }
    public void setNgayDat(Timestamp ngayDat) { this.ngayDat = ngayDat; }
    public Timestamp getNgayThanhToan() { return ngayThanhToan; }
    public void setNgayThanhToan(Timestamp ngayThanhToan) { this.ngayThanhToan = ngayThanhToan; }
    public long getSoTienDaTra() { return soTienDaTra; }
    public void setSoTienDaTra(long soTienDaTra) { this.soTienDaTra = soTienDaTra; }
    public long getTongTien() { return tongTien; }
    public void setTongTien(long tongTien) { this.tongTien = tongTien; }
    public int getGiamGia() { return giamGia; }
    public void setGiamGia(int giamGia) { this.giamGia = giamGia; }
}