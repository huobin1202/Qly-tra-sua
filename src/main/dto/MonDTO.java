package dto;
public class MonDTO {
    int maMon;
    String tenMon;
    String moTa;
    String tenDonVi;
    long gia;
    int maLoai;
    int soLuong;

    public MonDTO(int maMon, String tenMon, String moTa, String tenDonVi, long gia, int maLoai, int soLuong) {
        this.maMon = maMon;
        this.tenMon = tenMon;
        this.moTa = moTa;
        this.tenDonVi = tenDonVi;
        this.gia = gia;
        this.maLoai = maLoai;
        this.soLuong = soLuong;
    }
    public int getMaMon() {
        return maMon;
    }
    public void setMaMon(int maMon) {
        this.maMon = maMon;
    }
    public String getTenMon() {
        return tenMon;
    }
    public void setTenMon(String tenMon) {
        this.tenMon = tenMon;
    }
    public String getMoTa() {
        return moTa;
    }
    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }
   
    public String getTenDonVi() {
        return tenDonVi;
    }
    public void setTenDonVi(String tenDonVi) {
        this.tenDonVi = tenDonVi;
    }
    public long getGia() {
        return gia;
    }
    public void setGia(long gia) {
        this.gia = gia;
    }
    public int getMaLoai() {
        return maLoai;
    }
    public void setMaLoai(int maLoai) {
        this.maLoai = maLoai;
    }
    public int getSoLuong() {
        return soLuong;
    }
    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }
}