package dto;

public class MonNguyenLieuDTO {
    private int maMon;
    private int maNL;
    private String tenNL;
    private int soLuong;
    private String donVi;

    public MonNguyenLieuDTO() {
    }

    public MonNguyenLieuDTO(int maMon, int maNL, String tenNL, int soLuong, String donVi) {
        this.maMon = maMon;
        this.maNL = maNL;
        this.tenNL = tenNL;
        this.soLuong = soLuong;
        this.donVi = donVi;
    }

    public int getMaMon() {
        return maMon;
    }

    public void setMaMon(int maMon) {
        this.maMon = maMon;
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
    }

    public String getDonVi() {
        return donVi;
    }

    public void setDonVi(String donVi) {
        this.donVi = donVi;
    }
}

