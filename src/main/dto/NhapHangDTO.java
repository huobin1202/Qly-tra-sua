package dto;

import controller.TrangThaiPhieuNhap;

public class NhapHangDTO {
    private int maPN;
    private int maNV;
    private int maNCC;
    private String ngay; // iso string for simplicity
    private String ghiChu;
    private long thanhTien;
    private TrangThaiPhieuNhap trangThai;

    // Constructor mặc định
    public NhapHangDTO() {
        this.trangThai = TrangThaiPhieuNhap.CHUA_XAC_NHAN; // Mặc định chưa xác nhận
    }

    // Constructor đầy đủ
    public NhapHangDTO(int maPN, int maNV, int maNCC, String ngay, String ghiChu, long thanhTien, String trangThai) {
        this.maPN = maPN;
        this.maNV = maNV;
        this.maNCC = maNCC;
        this.ngay = ngay;
        this.ghiChu = ghiChu;
        this.thanhTien = thanhTien;
        this.trangThai = TrangThaiPhieuNhap.fromString(trangThai);
    }

    // Constructor với enum
    public NhapHangDTO(int maPN, int maNV, int maNCC, String ngay, String ghiChu, long thanhTien, TrangThaiPhieuNhap trangThai) {
        this.maPN = maPN;
        this.maNV = maNV;
        this.maNCC = maNCC;
        this.ngay = ngay;
        this.ghiChu = ghiChu;
        this.thanhTien = thanhTien;
        this.trangThai = trangThai != null ? trangThai : TrangThaiPhieuNhap.CHUA_XAC_NHAN;
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

    public TrangThaiPhieuNhap getTrangThai() { return trangThai; }
    public void setTrangThai(TrangThaiPhieuNhap trangThai) { this.trangThai = trangThai; }
    
    // Getter cho string (để tương thích với database)
    public String getTrangThaiString() { 
        return trangThai != null ? trangThai.getMaTrangThai() : TrangThaiPhieuNhap.CHUA_XAC_NHAN.getMaTrangThai(); 
    }
    
    public void setTrangThaiString(String trangThai) { 
        this.trangThai = TrangThaiPhieuNhap.fromString(trangThai); 
    }

    // Phương thức kiểm tra trạng thái
    public boolean isDaXacNhan() {
        return TrangThaiPhieuNhap.DA_XAC_NHAN.equals(this.trangThai);
    }

    public boolean isChuaXacNhan() {
        return TrangThaiPhieuNhap.CHUA_XAC_NHAN.equals(this.trangThai);
    }

    // Phương thức xác nhận phiếu nhập
    public void xacNhan() {
        this.trangThai = TrangThaiPhieuNhap.DA_XAC_NHAN;
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
                ", trangThai=" + (trangThai != null ? trangThai.getTenTrangThai() : "Chưa xác định") +
                '}';
    }
}
