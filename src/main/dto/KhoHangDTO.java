package dto;

public class KhoHangDTO {
    private int maMon;
    private String tenMon;
    private String tenDonVi;
    private int soLuong;
    private int tonToiThieu;

    public int getMaMon() { return maMon; }
    public void setMaMon(int maMon) { this.maMon = maMon; }

    public String getTenMon() { return tenMon; }
    public void setTenMon(String tenMon) { this.tenMon = tenMon; }

    public String getTenDonVi() { return tenDonVi; }
    public void setTenDonVi(String tenDonVi) { this.tenDonVi = tenDonVi; }

    public int getSoLuong() { return soLuong; }
    public void setSoLuong(int soLuong) { this.soLuong = soLuong; }

    public int getTonToiThieu() { return tonToiThieu; }
    public void setTonToiThieu(int tonToiThieu) { this.tonToiThieu = tonToiThieu; }
}
