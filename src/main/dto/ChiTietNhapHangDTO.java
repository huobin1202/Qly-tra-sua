package dto;

public class ChiTietNhapHangDTO {
    private int maPN;
    private int maNL;
    private String tenNL;
    private int soLuong;
    private long donGia;
    private String donVi;
    private long thanhTien;

    // Constructor mặc định
    public ChiTietNhapHangDTO() {
    }

    // Constructor đầy đủ
    public ChiTietNhapHangDTO(int maPN, int maNL, String tenNL, int soLuong, long donGia, String donVi) {
        this.maPN = maPN;
        this.maNL = maNL;
        this.tenNL = tenNL;
        this.soLuong = soLuong;
        this.donGia = donGia;
        this.donVi = donVi;
        this.thanhTien = soLuong * donGia;
    }

    // Constructor không có tên nguyên liệu (để tương thích với database)
    public ChiTietNhapHangDTO(int maPN, int maNL, int soLuong, long donGia, String donVi) {
        this.maPN = maPN;
        this.maNL = maNL;
        this.soLuong = soLuong;
        this.donGia = donGia;
        this.donVi = donVi;
        this.thanhTien = soLuong * donGia;
    }

    // Getters và Setters
    public int getMaPN() {
        return maPN;
    }

    public void setMaPN(int maPN) {
        this.maPN = maPN;
    }

    public int getMaNL() {
        return maNL;
    }

    public void setMaNL(int maNL) {
        this.maNL = maNL;
    }

    public String getTenNL() {
        return tenNL;
    }

    public void setTenNL(String tenNL) {
        this.tenNL = tenNL;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
        this.thanhTien = this.soLuong * this.donGia; // Tự động tính lại thành tiền
    }

    public long getDonGia() {
        return donGia;
    }

    public void setDonGia(long donGia) {
        this.donGia = donGia;
        this.thanhTien = this.soLuong * this.donGia; // Tự động tính lại thành tiền
    }

    public String getDonVi() {
        return donVi;
    }

    public void setDonVi(String donVi) {
        this.donVi = donVi;
    }

    public long getThanhTien() {
        return thanhTien;
    }

    public void setThanhTien(long thanhTien) {
        this.thanhTien = thanhTien;
    }

    // Phương thức tính lại thành tiền
    public void tinhLaiThanhTien() {
        this.thanhTien = this.soLuong * this.donGia;
    }

    @Override
    public String toString() {
        return "ChiTietNhapHangDTO{" +
                "maPN=" + maPN +
                ", maNL=" + maNL +
                ", tenNL='" + tenNL + '\'' +
                ", soLuong=" + soLuong +
                ", donGia=" + donGia +
                ", donVi='" + donVi + '\'' +
                ", thanhTien=" + thanhTien +
                '}';
    }
}
