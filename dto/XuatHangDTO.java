package dto;

public class XuatHangDTO {
    private int maPX;
    private int maNV;
    private String ngay; // iso string for simplicity
    private String ghiChu;

    public int getMaPX() { return maPX; }
    public void setMaPX(int maPX) { this.maPX = maPX; }

    public int getMaNV() { return maNV; }
    public void setMaNV(int maNV) { this.maNV = maNV; }

    public String getNgay() { return ngay; }
    public void setNgay(String ngay) { this.ngay = ngay; }

    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
}
