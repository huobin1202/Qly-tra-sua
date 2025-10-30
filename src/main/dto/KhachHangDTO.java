package dto;

public class KhachHangDTO {
    private int maKH;
    private String soDienThoai;
    private String hoTen;
    private int diemTichLuy;

    public KhachHangDTO() {
    }

    public KhachHangDTO(int maKH, String soDienThoai, String hoTen, int diemTichLuy) {
        this.maKH = maKH;
        this.soDienThoai = soDienThoai;
        this.hoTen = hoTen;
        this.diemTichLuy = diemTichLuy;
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

    public int getDiemTichLuy() {
        return diemTichLuy;
    }

    public void setDiemTichLuy(int diemTichLuy) {
        this.diemTichLuy = diemTichLuy;
    }
}