package dto;
import java.time.LocalDate;

public class NhanVien {
    private int id;
    private String tenTaiKhoan;
    private String matKhau;
    private String soDienThoai;
    private LocalDate ngayVaoLam;
    private String chucVu;
    private double luong;

    public NhanVien(int id, String tenTaiKhoan, String matKhau, String soDienThoai, LocalDate ngayVaoLam, String chucVu, double luong) {
        this.id = id;
        this.tenTaiKhoan = tenTaiKhoan;
        this.matKhau = matKhau;
        this.soDienThoai = soDienThoai;
        this.ngayVaoLam = ngayVaoLam;
        this.chucVu = chucVu;
        this.luong = luong;
    }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTenTaiKhoan() { return tenTaiKhoan; }
    public void setTenTaiKhoan(String tenTaiKhoan) { this.tenTaiKhoan = tenTaiKhoan; }
    public String getMatKhau() { return matKhau; }
    public void setMatKhau(String matKhau) { this.matKhau = matKhau; }
    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }
    public LocalDate getNgayVaoLam() { return ngayVaoLam; }
    public void setNgayVaoLam(LocalDate ngayVaoLam) { this.ngayVaoLam = ngayVaoLam; }
    public String getChucVu() { return chucVu; }
    public void setChucVu(String chucVu) { this.chucVu = chucVu; }
    public double getLuong() { return luong; }
    public void setLuong(double luong) { this.luong = luong; }

    // Getter, Setter...
}