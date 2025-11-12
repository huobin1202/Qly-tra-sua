package dto;

public class MonDTO {
int maMon;
String tenMon;
long gia;
String tinhTrang;
int maLoai;
String anh;
String tenLoai;

    // Constructor mặc định
    public MonDTO() {}

    public MonDTO(int maMon, String tenMon, long gia, String tinhTrang, int maLoai) {
        this.maMon = maMon;
        this.tenMon = tenMon;
        this.gia = gia;
        this.tinhTrang = tinhTrang;
        this.maLoai = maLoai;
        this.anh = "";
        this.tenLoai = "";
    }

    public MonDTO(int maMon, String tenMon, long gia, String tinhTrang, int maLoai, String anh) {
        this.maMon = maMon;
        this.tenMon = tenMon;
        this.gia = gia;
        this.tinhTrang = tinhTrang;
        this.maLoai = maLoai;
        this.anh = anh;
        this.tenLoai = "";
    }
    
    public MonDTO(int maMon, String tenMon, long gia, String tinhTrang, int maLoai, String anh, String tenLoai) {
        this.maMon = maMon;
        this.tenMon = tenMon;
        this.gia = gia;
        this.tinhTrang = tinhTrang;
        this.maLoai = maLoai;
        this.anh = anh;
        this.tenLoai = tenLoai;
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
    public String getAnh() {
        return anh;
    }
    public void setAnh(String anh) {
        this.anh = anh;
    }
    public String getTenLoai() {
        return tenLoai;
    }
    public void setTenLoai(String tenLoai) {
        this.tenLoai = tenLoai;
    }
}