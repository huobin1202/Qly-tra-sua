import java.time.LocalDate;

public class DonDatHang {
    private int id;
    private String nguoiLap;
    private Ban ban;
    private String trangThai;
    private LocalDate ngayLap;
    private LocalDate ngayThanhToan;
    private double daThanhToan; // số tiền đã trả
    private double tongTien;    // số tiền phải trả

    public DonDatHang(int id, String nguoiLap, Ban ban, String trangThai, LocalDate ngayLap, LocalDate ngayThanhToan, double daThanhToan, double tongTien) {
        this.id = id;
        this.nguoiLap = nguoiLap;
        this.ban = ban;
        this.trangThai = trangThai;
        this.ngayLap = ngayLap;
        this.ngayThanhToan = ngayThanhToan;
        this.daThanhToan = daThanhToan;
        this.tongTien = tongTien;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNguoiLap() { return nguoiLap; }
    public void setNguoiLap(String nguoiLap) { this.nguoiLap = nguoiLap; }
    public Ban getBan() { return ban; }
    public void setBan(Ban ban) { this.ban = ban; }
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    public LocalDate getNgayLap() { return ngayLap; }
    public void setNgayLap(LocalDate ngayLap) { this.ngayLap = ngayLap        ; }
    public LocalDate getNgayThanhToan() { return ngayThanhToan; }
    public void setNgayThanhToan(LocalDate ngayThanhToan) { this.ngayThanhToan = ngayThanhToan; }
    public double getDaThanhToan() { return daThanhToan; }
    public void setDaThanhToan(double daThanhToan) { this.daThanhToan = daThanhToan; }
    public double getTongTien() { return tongTien; }
}