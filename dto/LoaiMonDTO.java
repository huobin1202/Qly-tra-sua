package dto;
public class LoaiMonDTO {
    int ma;
    String ten;

    public LoaiMonDTO(int ma, String ten) {
        this.ma = ma;
        this.ten = ten;
    }
    public int getMa() {
        return ma;
    }
    public void setMa(int ma) {
        this.ma = ma;
    }
    public String getTen() {
        return ten;
    }
    public void setTen(String ten) {
        this.ten = ten;
    }
}