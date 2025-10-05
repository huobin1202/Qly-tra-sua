package dto;
public class BanDTO {
    private int id;
    private String tenBan;
    private String trangThai; // VD: "Trống", "Đang sử dụng"

    public BanDTO(int id, String tenBan, String trangThai) {
        this.id = id;
        this.tenBan = tenBan;
        this.trangThai = trangThai;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTenBan() { return tenBan; }
    public void setTenBan(String tenBan) { this.tenBan = tenBan; }
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai;}
}