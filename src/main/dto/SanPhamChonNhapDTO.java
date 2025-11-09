package dto;

public class SanPhamChonNhapDTO {
    private int stt;
    private int maNL;
    private String tenNL;
    private String donVi;
    private int soLuong;
    private long donGia;
    private long thanhTien;

    // Constructor mặc định
    public SanPhamChonNhapDTO() {}

    // Constructor đầy đủ
    public SanPhamChonNhapDTO(int stt, int maNL, String tenNL, String donVi, int soLuong, long donGia) {
        this.stt = stt;
        this.maNL = maNL;
        this.tenNL = tenNL;
        this.donVi = donVi;
        this.soLuong = soLuong;
        this.donGia = donGia;
        this.thanhTien = soLuong * donGia;
    }

    // Getters và Setters
    public int getStt() { return stt; }
    public void setStt(int stt) { this.stt = stt; }

    public int getMaNL() { return maNL; }
    public void setMaNL(int maNL) { this.maNL = maNL; }

    public String getTenNL() { return tenNL; }
    public void setTenNL(String tenNL) { this.tenNL = tenNL; }

    public String getDonVi() { return donVi; }
    public void setDonVi(String donVi) { this.donVi = donVi; }

    public int getSoLuong() { return soLuong; }
    public void setSoLuong(int soLuong) { 
        this.soLuong = soLuong;
        this.thanhTien = this.soLuong * this.donGia; // Tự động tính lại thành tiền
    }

    public long getDonGia() { return donGia; }
    public void setDonGia(long donGia) { 
        this.donGia = donGia;
        this.thanhTien = this.soLuong * this.donGia; // Tự động tính lại thành tiền
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
        return "SanPhamChonNhapDTO{" +
                "stt=" + stt +
                ", maNL=" + maNL +
                ", tenNL='" + tenNL + '\'' +
                ", donVi='" + donVi + '\'' +
                ", soLuong=" + soLuong +
                ", donGia=" + donGia +
                ", thanhTien=" + thanhTien +
                '}';
    }
}
