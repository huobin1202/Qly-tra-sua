package dto;

public class NhapHangDTO {
    private int maPN;
    private int maNV;
    private int maNCC;
    private String ngay; // iso string for simplicity
    private String ghiChu;
    private long thanhTien;
    private String trangThai;

    // Constructor mặc định
    public NhapHangDTO() {
        this.trangThai = "chuaxacnhan"; // Mặc định chưa xác nhận
    }

    // Constructor đầy đủ
    public NhapHangDTO(int maPN, int maNV, int maNCC, String ngay, String ghiChu, long thanhTien, String trangThai) {
        this.maPN = maPN;
        this.maNV = maNV;
        this.maNCC = maNCC;
        this.ngay = ngay;
        this.ghiChu = ghiChu;
        this.thanhTien = thanhTien;
        this.trangThai = trangThai;
    }


    // Getters và Setters
    public int getMaPN() { return maPN; }
    public void setMaPN(int maPN) { this.maPN = maPN; }

    public int getMaNV() { return maNV; }
    public void setMaNV(int maNV) { this.maNV = maNV; }

    public int getMaNCC() { return maNCC; }
    public void setMaNCC(int maNCC) { this.maNCC = maNCC; }

    public String getNgay() { return ngay; }
    public void setNgay(String ngay) { this.ngay = ngay; }

    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }

    public long getThanhTien() { return thanhTien; }
    public void setThanhTien(long thanhTien) { this.thanhTien = thanhTien; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    // Phương thức kiểm tra trạng thái
    public boolean isDaXacNhan() {
        return "daxacnhan".equals(this.trangThai);
    }

    public boolean isChuaXacNhan() {
        return "chuaxacnhan".equals(this.trangThai);
    }

    // Phương thức xác nhận phiếu nhập
    public void xacNhan() {
        this.trangThai = "daxacnhan";
    }

    @Override
    public String toString() {
        return "NhapHangDTO{" +
                "maPN=" + maPN +
                ", maNV=" + maNV +
                ", maNCC=" + maNCC +
                ", ngay='" + ngay + '\'' +
                ", ghiChu='" + ghiChu + '\'' +
                ", thanhTien=" + thanhTien +
                ", trangThai=" + trangThai +
                '}';
    }
}
