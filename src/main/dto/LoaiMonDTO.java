package dto;
public class LoaiMonDTO {
    int maLoai;
    String tenLoai;

    // Constructor mặc định
    public LoaiMonDTO() {}


    
    public LoaiMonDTO(int maLoai, String tenLoai) {
        this.maLoai = maLoai;
        this.tenLoai = tenLoai;
    }
    public int getMaLoai() {
        return maLoai;
    }
    public void setMaLoai(int maLoai) {
        this.maLoai = maLoai;
    }
    public String getTenLoai() {
        return tenLoai;
    }
    public void setTenLoai(String tenLoai) {
        this.tenLoai = tenLoai;
    }


}