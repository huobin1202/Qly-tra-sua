package dto;

public class NhapHangDTO {
    private int maPN;
    private int maNV;
    private String ngay; // iso string for simplicity
    private String ghiChu;

    public int getMaPN() { return maPN; }
    public void setMaPN(int maPN) { this.maPN = maPN; }

    public int getMaNV() { return maNV; }
    public void setMaNV(int maNV) { this.maNV = maNV; }

    public String getNgay() { return ngay; }
    public void setNgay(String ngay) { this.ngay = ngay; }

    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
}
