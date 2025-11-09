package dto;

public class NhaCungCapSanPhamDTO {
    private int maNCC;
    private String tenNCC;
    private int maNL;
    private String tenNL;
    private String donVi;
    private int soLuong;
    private long donGia;

    // Constructor mặc định
    public NhaCungCapSanPhamDTO() {}

    // Constructor đầy đủ
    public NhaCungCapSanPhamDTO(int maNCC, String tenNCC, int maNL, String tenNL, String donVi, int soLuong, long donGia) {
        this.maNCC = maNCC;
        this.tenNCC = tenNCC;
        this.maNL = maNL;
        this.tenNL = tenNL;
        this.donVi = donVi;
        this.soLuong = soLuong;
        this.donGia = donGia;
    }

    // Getters và Setters
    public int getMaNCC() { return maNCC; }
    public void setMaNCC(int maNCC) { this.maNCC = maNCC; }

    public String getTenNCC() { return tenNCC; }
    public void setTenNCC(String tenNCC) { this.tenNCC = tenNCC; }

    public int getMaNL() { return maNL; }
    public void setMaNL(int maNL) { this.maNL = maNL; }

    public String getTenNL() { return tenNL; }
    public void setTenNL(String tenNL) { this.tenNL = tenNL; }

    public String getDonVi() { return donVi; }
    public void setDonVi(String donVi) { this.donVi = donVi; }

    public int getSoLuong() { return soLuong; }
    public void setSoLuong(int soLuong) { this.soLuong = soLuong; }

    public long getDonGia() { return donGia; }
    public void setDonGia(long donGia) { this.donGia = donGia; }

    @Override
    public String toString() {
        return "NhaCungCapSanPhamDTO{" +
                "maNCC=" + maNCC +
                ", tenNCC='" + tenNCC + '\'' +
                ", maNL=" + maNL +
                ", tenNL='" + tenNL + '\'' +
                ", donVi='" + donVi + '\'' +
                ", soLuong=" + soLuong +
                ", donGia=" + donGia +
                '}';
    }
}
