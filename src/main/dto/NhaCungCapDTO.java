package dto;
import java.util.Scanner;
public class NhaCungCapDTO implements Inhapxuat {
    private String maNCC;
    private String tenNCC;
    private String sdt;
    private String diaChi;

    public NhaCungCapDTO() {
    }

    public NhaCungCapDTO(String maNCC, String tenNCC, String sdt, String diaChi) {
        this.maNCC = maNCC;
        this.tenNCC = tenNCC;
        this.sdt = sdt;
        this.diaChi = diaChi;
    }

    public String getMaNCC() {
        return maNCC;
    }

    public void setMaNCC(String maNCC) {
        this.maNCC = maNCC;
    }

    public String getTenNCC() {
        return tenNCC;
    }

    public void setTenNCC(String tenNCC) {
        this.tenNCC = tenNCC;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    @Override
    public void nhap() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Nhap ma nha cung cap: ");
        maNCC = sc.nextLine();
        System.out.print("Nhap ten nha cung cap: ");
        tenNCC = sc.nextLine();
        System.out.print("Nhap so dien thoai: ");
        sdt = sc.nextLine();
        System.out.print("Nhap dia chi: ");
        diaChi = sc.nextLine();
    }

    @Override
    public void xuat() {
        System.out.printf("%-10s %-20s %-15s %-30s\n", maNCC, tenNCC, sdt, diaChi);
    }
}