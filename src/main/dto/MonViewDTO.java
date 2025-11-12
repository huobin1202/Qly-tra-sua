package dto;

/**
 * DTO phục vụ việc hiển thị món trong bảng HangHoaView.
 */
public class MonViewDTO {
    private int maMon;
    private String tenMon;
    private long gia;
    private String tinhTrang;
    private String tenLoai;
    private String anh;

    public MonViewDTO() {
    }

    public MonViewDTO(int maMon, String tenMon, long gia, String tinhTrang, String tenLoai, String anh) {
        this.maMon = maMon;
        this.tenMon = tenMon;
        this.gia = gia;
        this.tinhTrang = tinhTrang;
        this.tenLoai = tenLoai;
        this.anh = anh;
    }

    public int getMaMon() {
        return maMon;
    }

    public void setMaMon(int maMon) {
        this.maMon = maMon;
    }

    public String getTenMon() {
        return tenMon;
    }

    public void setTenMon(String tenMon) {
        this.tenMon = tenMon;
    }

    public long getGia() {
        return gia;
    }

    public void setGia(long gia) {
        this.gia = gia;
    }

    public String getTinhTrang() {
        return tinhTrang;
    }

    public void setTinhTrang(String tinhTrang) {
        this.tinhTrang = tinhTrang;
    }

    public String getTenLoai() {
        return tenLoai;
    }

    public void setTenLoai(String tenLoai) {
        this.tenLoai = tenLoai;
    }

    public String getAnh() {
        return anh;
    }

    public void setAnh(String anh) {
        this.anh = anh;
    }
}


