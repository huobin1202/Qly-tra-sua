package dto;

import java.sql.Timestamp;

public abstract class NhanVienDTO {

    protected int maNV;
    protected String taiKhoan;
    protected String matKhau;
    protected String hoTen;
    protected String soDienThoai;
    protected Timestamp ngayVaoLam;
    protected String chucVu;
    protected long luong; // đổi sang long
    protected String trangThai = "danglam";

    public NhanVienDTO() {
    }

    public NhanVienDTO(int maNV, String taiKhoan, String matKhau, String hoTen, String soDienThoai, Timestamp ngayVaoLam, String chucVu, long luong, String trangThai) {
        this.maNV = maNV;
        this.taiKhoan = taiKhoan;
        this.matKhau = matKhau;
        this.hoTen = hoTen;
        this.soDienThoai = soDienThoai;
        this.ngayVaoLam = ngayVaoLam;
        this.chucVu = chucVu;
        this.luong = luong;
        this.trangThai = trangThai;
    }

    public abstract double tinhLuong();

    public abstract String[][] getMenuItems();

    /**
     * Trả về submenu cho dropdown "Quản lý hàng hóa" nếu có quyền
     */
    public abstract String[][] getHangHoaSubmenu();

    public int getMaNV() {
        return maNV;
    }

    public void setMaNV(int maNV) {
        this.maNV = maNV;
    }

    public String getTaiKhoan() {
        return taiKhoan;
    }

    public void setTaiKhoan(String taiKhoan) {
        this.taiKhoan = taiKhoan;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public Timestamp getNgayVaoLam() {
        return ngayVaoLam;
    }

    public void setNgayVaoLam(Timestamp ngayVaoLam) {
        this.ngayVaoLam = ngayVaoLam;
    }

    public String getChucVu() {
        return chucVu;
    }

    public void setChucVu(String chucVu) {
        this.chucVu = chucVu;
    }

    public long getLuong() {
        return luong;
    }

    public void setLuong(long luong) {
        this.luong = luong;
    }

    public String getTrangThai() {
        return trangThai;
    }
    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
}
