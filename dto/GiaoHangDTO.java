package dto;
import java.time.LocalDate;

public class GiaoHangDTO {
    private int id;
    private String tenKH;
    private String diaChi;
    private String tenShipper;
    private int sdtShipper;
    private double giaShip;
    private String trangThai;
    private LocalDate ngayBatDau;
    private LocalDate ngayKetThuc;

    public GiaoHangDTO(int id, String tenKH, String diaChi, String tenShipper, int sdtShipper, double giaShip, String trangThai, LocalDate ngayBatDau, LocalDate ngayKetThuc) {
        this.id = id;
        this.tenKH = tenKH;
        this.diaChi = diaChi;
        this.tenShipper = tenShipper;
        this.sdtShipper = sdtShipper;
        this.giaShip = giaShip;
        this.trangThai = trangThai;
        this.ngayBatDau = ngayBatDau;
        this.ngayKetThuc = ngayKetThuc;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTenKH() { return tenKH; }
    public void setTenKH(String tenKH) { this.tenKH = tenKH; }
    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }
    public String getTenShipper() { return tenShipper; }
    public void setTenShipper(String tenShipper) { this.tenShipper = tenShipper; }
    public int getSdtShipper() { return sdtShipper; }
    public void setSdtShipper(int sdtShipper) { this.sdtShipper = sdtShipper; }
    public double getGiaShip() { return giaShip; }
    public void setGiaShip(double giaShip) { this.giaShip = giaShip; }
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    public LocalDate getNgayBatDau() { return ngayBatDau; }
    public void setNgayBatDau(LocalDate ngayBatDau) { this.ngayBatDau = ngayBatDau; }
    public LocalDate getNgayKetThuc() { return ngayKetThuc; }

}