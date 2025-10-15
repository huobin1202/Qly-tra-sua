package dto;
import java.sql.Timestamp;

public class GiaoHangDTO {
    private int maDon;
    private int maKH;
    private String tenShipper;
    private String sdtShipper;
    private int phiShip;
    private String trangThai;
    private String thongBao;
    private Timestamp ngayBatDau;
    private Timestamp ngayKetThuc;

    public GiaoHangDTO(int maDon, int maKH, String tenShipper, String sdtShipper, 
                      int phiShip, String trangThai, String thongBao, Timestamp ngayBatDau, Timestamp ngayKetThuc) {
        this.maDon = maDon;
        this.maKH = maKH;
        this.tenShipper = tenShipper;
        this.sdtShipper = sdtShipper;
        this.phiShip = phiShip;
        this.trangThai = trangThai;
        this.thongBao = thongBao;
        this.ngayBatDau = ngayBatDau;
        this.ngayKetThuc = ngayKetThuc;
    }

    public int getMaDon() { return maDon; }
    public void setMaDon(int maDon) { this.maDon = maDon; }
    public int getMaKH() { return maKH; }
    public void setMaKH(int maKH) { this.maKH = maKH; }
    public String getTenShipper() { return tenShipper; }
    public void setTenShipper(String tenShipper) { this.tenShipper = tenShipper; }
    public String getSdtShipper() { return sdtShipper; }
    public void setSdtShipper(String sdtShipper) { this.sdtShipper = sdtShipper; }
    public int getPhiShip() { return phiShip; }
    public void setPhiShip(int phiShip) { this.phiShip = phiShip; }
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    public String getThongBao() { return thongBao; }
    public void setThongBao(String thongBao) { this.thongBao = thongBao; }
    public Timestamp getNgayBatDau() { return ngayBatDau; }
    public void setNgayBatDau(Timestamp ngayBatDau) { this.ngayBatDau = ngayBatDau; }
    public Timestamp getNgayKetThuc() { return ngayKetThuc; }
    public void setNgayKetThuc(Timestamp ngayKetThuc) { this.ngayKetThuc = ngayKetThuc; }
}