package dto;
public class LoaiMonDTO {
    int maLoai;
    String tenLoai;
    String slug;

    public LoaiMonDTO(int maLoai, String tenLoai, String slug) {
        this.maLoai = maLoai;
        this.tenLoai = tenLoai;
        this.slug = slug;
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
    public String getSlug() {
        return slug;
    }
    public void setSlug(String slug) {
        this.slug = slug;
    }
}