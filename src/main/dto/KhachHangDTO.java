package dto;
import java.sql.Timestamp;

public class KhachHangDTO {
    private int maKH;
    private String soDienThoai;
    private String hoTen;
    private String diaChi;
    private Timestamp ngaySinh;

    public KhachHangDTO() {
    }

    public KhachHangDTO(int maKH, String soDienThoai, String hoTen, String diaChi, Timestamp ngaySinh) {
        this.maKH = maKH;
        this.soDienThoai = soDienThoai;
        this.hoTen = hoTen;
        this.diaChi = diaChi;
        this.ngaySinh = ngaySinh;
    }

    public int getMaKH() {
        return maKH;
    }

    public void setMaKH(int maKH) {
        this.maKH = maKH;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public Timestamp getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(Timestamp ngaySinh) {
        this.ngaySinh = ngaySinh;
    }
}