package dto;
public class MonDTO {
    int id;
    String ten;
    String mota;
    String anh;
    String tendv;
    int gia;
    String dv;
    int ma_loai;

    public MonDTO(int id, String ten, String mota, String anh, String tendv, int gia, String dv, int ma_loai) {
        this.id = id;
        this.ten = ten;
        this.mota = mota;
        this.anh = anh;
        this.tendv = tendv;
        this.gia = gia;
        this.dv = dv;
        this.ma_loai = ma_loai;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getTen() {
        return ten;
    }
    public void setTen(String ten) {
        this.ten = ten;
    }
    public String getMota() {
        return mota;
    }
    public void setMota(String mota) {
        this.mota = mota;
    }
    public String getAnh() {
        return anh;
    }
    public void setAnh(String anh) {
        this.anh = anh;
    }
    public String getTendv() {
        return tendv;
    }
    public void setTendv(String tendv) {
        this.tendv = tendv;
    }
    public int getGia() {
        return gia;
    }
    public void setGia(int gia) {
        this.gia = gia;
    }
    public String getDv() {
        return dv;
    }
    public void setDv(String dv) {
        this.dv = dv;
    }
    public int getMa_loai() {
        return ma_loai;
    }
    public void setMa_loai(int ma_loai) {
        this.ma_loai = ma_loai;
    }

}