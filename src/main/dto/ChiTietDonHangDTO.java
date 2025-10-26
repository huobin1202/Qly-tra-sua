package dto;

public class ChiTietDonHangDTO {
    private int maDon;
    private int maMon;
    private int maTopping;
    private int soLuong;
    private long giaMon;
    private long giaTopping;
    private String ghiChu;
    private String tenMon; // Để hiển thị tên món
    private String tenTopping; // Để hiển thị tên topping
    private String anh; // Để hiển thị ảnh món

    // Constructor mặc định
    public ChiTietDonHangDTO() {}

    public ChiTietDonHangDTO(int maDon, int maMon, int maTopping, int soLuong, long giaMon, long giaTopping, String ghiChu) {
        this.maDon = maDon;
        this.maMon = maMon;
        this.maTopping = maTopping;
        this.soLuong = soLuong;
        this.giaMon = giaMon;
        this.giaTopping = giaTopping;
        this.ghiChu = ghiChu;
    }

    public int getMaDon() { return maDon; }
    public void setMaDon(int maDon) { this.maDon = maDon; }
    public int getMaMon() { return maMon; }
    public void setMaMon(int maMon) { this.maMon = maMon; }
    public int getMaTopping() { return maTopping; }
    public void setMaTopping(int maTopping) { this.maTopping = maTopping; }
    public int getSoLuong() { return soLuong; }
    public void setSoLuong(int soLuong) { this.soLuong = soLuong; }
    public long getGiaMon() { return giaMon; }
    public void setGiaMon(long giaMon) { this.giaMon = giaMon; }
    public long getGiaTopping() { return giaTopping; }
    public void setGiaTopping(long giaTopping) { this.giaTopping = giaTopping; }
    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
    
    public String getTenMon() { return tenMon; }
    public void setTenMon(String tenMon) { this.tenMon = tenMon; }
    
    public String getTenTopping() { return tenTopping; }
    public void setTenTopping(String tenTopping) { this.tenTopping = tenTopping; }
    
    public String getAnh() { return anh; }
    public void setAnh(String anh) { this.anh = anh; }
}
