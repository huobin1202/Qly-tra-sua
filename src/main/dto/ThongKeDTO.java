package dto;

import java.sql.Timestamp;

public class ThongKeDTO {
    private String tenMon;
    private int soLuongBan;
    private long tongTien;
    private String tenLoaiMon;
    private String tenNhanVien;
    private int soDonHang;
    private String ngay;
    private String thang;
    private String nam;
    private String trangThai;
    private int maDon;
    private Timestamp ngayDat;
    private String tenKhachHang;
    private long doanhThu;
    private int soKhachHang;
    private int soNhanVien;
    private int soMon;
    private int soNguyenLieu;
    private int soNhaCungCap;

    // Constructors
    public ThongKeDTO() {}

    public ThongKeDTO(String tenMon, int soLuongBan, long tongTien) {
        this.tenMon = tenMon;
        this.soLuongBan = soLuongBan;
        this.tongTien = tongTien;
    }

    // Constructor cho loại món
    public ThongKeDTO(String tenLoaiMon, int soLuongBan, long tongTien, int type) {
        if (type == 1) { // Loại món
            this.tenLoaiMon = tenLoaiMon;
            this.soLuongBan = soLuongBan;
            this.tongTien = tongTien;
        }
    }

    // Constructor cho nhân viên
    public ThongKeDTO(String tenNhanVien, int soDonHang, long doanhThu, String type) {
        if ("nhanvien".equals(type)) {
            this.tenNhanVien = tenNhanVien;
            this.soDonHang = soDonHang;
            this.doanhThu = doanhThu;
        }
    }

    // Constructor cho ngày
    public ThongKeDTO(String ngay, long doanhThu, char type) {
        if (type == 'd') { // Day
            this.ngay = ngay;
            this.doanhThu = doanhThu;
        }
    }

    // Constructor cho tháng
    public ThongKeDTO(String thang, long doanhThu, byte type) {
        if (type == 1) { // Month
            this.thang = thang;
            this.doanhThu = doanhThu;
        }
    }

    // Constructor cho năm
    public ThongKeDTO(String nam, long doanhThu, short type) {
        if (type == 1) { // Year
            this.nam = nam;
            this.doanhThu = doanhThu;
        }
    }

    // Getters and Setters
    public String getTenMon() { return tenMon; }
    public void setTenMon(String tenMon) { this.tenMon = tenMon; }

    public int getSoLuongBan() { return soLuongBan; }
    public void setSoLuongBan(int soLuongBan) { this.soLuongBan = soLuongBan; }

    public long getTongTien() { return tongTien; }
    public void setTongTien(long tongTien) { this.tongTien = tongTien; }

    public String getTenLoaiMon() { return tenLoaiMon; }
    public void setTenLoaiMon(String tenLoaiMon) { this.tenLoaiMon = tenLoaiMon; }

    public String getTenNhanVien() { return tenNhanVien; }
    public void setTenNhanVien(String tenNhanVien) { this.tenNhanVien = tenNhanVien; }

    public int getSoDonHang() { return soDonHang; }
    public void setSoDonHang(int soDonHang) { this.soDonHang = soDonHang; }

    public String getNgay() { return ngay; }
    public void setNgay(String ngay) { this.ngay = ngay; }

    public String getThang() { return thang; }
    public void setThang(String thang) { this.thang = thang; }

    public String getNam() { return nam; }
    public void setNam(String nam) { this.nam = nam; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    public int getMaDon() { return maDon; }
    public void setMaDon(int maDon) { this.maDon = maDon; }

    public Timestamp getNgayDat() { return ngayDat; }
    public void setNgayDat(Timestamp ngayDat) { this.ngayDat = ngayDat; }

    public String getTenKhachHang() { return tenKhachHang; }
    public void setTenKhachHang(String tenKhachHang) { this.tenKhachHang = tenKhachHang; }

    public long getDoanhThu() { return doanhThu; }
    public void setDoanhThu(long doanhThu) { this.doanhThu = doanhThu; }

    public int getSoKhachHang() { return soKhachHang; }
    public void setSoKhachHang(int soKhachHang) { this.soKhachHang = soKhachHang; }

    public int getSoNhanVien() { return soNhanVien; }
    public void setSoNhanVien(int soNhanVien) { this.soNhanVien = soNhanVien; }

    public int getSoMon() { return soMon; }
    public void setSoMon(int soMon) { this.soMon = soMon; }

    public int getSoNguyenLieu() { return soNguyenLieu; }
    public void setSoNguyenLieu(int soNguyenLieu) { this.soNguyenLieu = soNguyenLieu; }

    public int getSoNhaCungCap() { return soNhaCungCap; }
    public void setSoNhaCungCap(int soNhaCungCap) { this.soNhaCungCap = soNhaCungCap; }
}
