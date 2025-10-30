package dto;
import java.sql.Timestamp;

public class DonHangDTO {
    private int maDon;
    private int maNV;
    private Integer maKH; // nullable - có thể không có khách hàng (đơn tại quán)
    private String trangThai;
    private Timestamp ngayDat;
    private long tongTien;
    private int giamGia;
    private String tenNV;
    private String tenKH; // Tên khách hàng

    // Constructor mặc định
    public DonHangDTO() {}

    public DonHangDTO(int maDon, int maNV, String trangThai, 
                         Timestamp ngayDat, long tongTien, int giamGia) {
        this.maDon = maDon;
        this.maNV = maNV;
        this.trangThai = trangThai;
        this.ngayDat = ngayDat;
        this.tongTien = tongTien;
        this.giamGia = giamGia;
    }
    

    public int getMaDon() { return maDon; }
    public void setMaDon(int maDon) { this.maDon = maDon; }
    public int getMaNV() { return maNV; }
    public void setMaNV(int maNV) { this.maNV = maNV; }
    public Integer getMaKH() { return maKH; }
    public void setMaKH(Integer maKH) { this.maKH = maKH; }
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    public Timestamp getNgayDat() { return ngayDat; }
    public void setNgayDat(Timestamp ngayDat) { this.ngayDat = ngayDat; }
    public long getTongTien() { return tongTien; }
    public void setTongTien(long tongTien) { this.tongTien = tongTien; }
    public int getGiamGia() { return giamGia; }
    public void setGiamGia(int giamGia) { this.giamGia = giamGia; }
    public String getTenNV() { return tenNV; }
    public void setTenNV(String tenNV) { this.tenNV = tenNV; }
    public String getTenKH() { return tenKH; }
    public void setTenKH(String tenKH) { this.tenKH = tenKH; }
}