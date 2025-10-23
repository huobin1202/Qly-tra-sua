package dto;
public class MonDTO {
    int maMon;
    String tenMon;
    String moTa;
    long gia;
    String tinhTrang;
    int maLoai;


    public MonDTO(int maMon, String tenMon, String moTa, long gia, String tinhTrang, int maLoai) {
        this.maMon = maMon;
        this.tenMon = tenMon;
        this.moTa = moTa;
        this.gia = gia;
        this.tinhTrang = tinhTrang;
        this.maLoai = maLoai;
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
   
    public long getGia() {
        return gia;
    }
    public void setGia(long gia) {
        this.gia = gia;
    }
    public String getTinhTrang() {
        return tinhTrang;
    }
    public void setTinhTrang(String tinhTrang) {
        this.tinhTrang = tinhTrang;
    }
    public int getMaLoai() {
        return maLoai;
    }
    public void setMaLoai(int maLoai) {
        this.maLoai = maLoai;
    }


}