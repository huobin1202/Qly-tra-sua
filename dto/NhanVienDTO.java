package dto;
import java.sql.Timestamp;

public class NhanVienDTO {
    private int maNV;
    private String taiKhoan;
    private String matKhau;
    private String hoTen;
    private String sdt;
    private Timestamp ngayVaoLam;
    private String chucVu;
    private int luong;

    public NhanVienDTO(int maNV, String taiKhoan, String matKhau, String hoTen, String sdt, Timestamp ngayVaoLam, String chucVu, int luong) {
        this.maNV = maNV;
        this.taiKhoan = taiKhoan;
        this.matKhau = matKhau;
        this.hoTen = hoTen;
        this.sdt = sdt;
        this.ngayVaoLam = ngayVaoLam;
        this.chucVu = chucVu;
        this.luong = luong;
    }

    public int getMaNV() { return maNV; }
    public void setMaNV(int maNV) { this.maNV = maNV; }
    public String getTaiKhoan() { return taiKhoan; }
    public void setTaiKhoan(String taiKhoan) { this.taiKhoan = taiKhoan; }
    public String getMatKhau() { return matKhau; }
    public void setMatKhau(String matKhau) { this.matKhau = matKhau; }
    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }
    public String getSdt() { return sdt; }
    public void setSdt(String sdt) { this.sdt = sdt; }
    public Timestamp getNgayVaoLam() { return ngayVaoLam; }
    public void setNgayVaoLam(Timestamp ngayVaoLam) { this.ngayVaoLam = ngayVaoLam; }
    public String getChucVu() { return chucVu; }
    public void setChucVu(String chucVu) { this.chucVu = chucVu; }
    public int getLuong() { return luong; }
    public void setLuong(int luong) { this.luong = luong; }
}