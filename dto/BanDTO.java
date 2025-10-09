package dto;
public class BanDTO {
    private int maBan;
    private String tenBan;
    private String trangThai; // VD: "trong", "phucvu", "dattruoc"

    public BanDTO(int maBan, String tenBan, String trangThai) {
        this.maBan = maBan;
        this.tenBan = tenBan;
        this.trangThai = trangThai;
    }

    public int getMaBan() { return maBan; }
    public void setMaBan(int maBan) { this.maBan = maBan; }
    public String getTenBan() { return tenBan; }
    public void setTenBan(String tenBan) { this.tenBan = tenBan; }
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai;}
}