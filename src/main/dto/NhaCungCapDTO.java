package dto;
import java.util.Scanner;

import controller.Inhapxuat;
public class NhaCungCapDTO implements Inhapxuat {
    private int maNCC;
    private String tenNCC;
    private String soDienThoai;
    private String diaChi;

    public NhaCungCapDTO() {
    }

    public NhaCungCapDTO(int maNCC, String tenNCC, String soDienThoai, String diaChi) {
        this.maNCC = maNCC;
        this.tenNCC = tenNCC;
        this.soDienThoai = soDienThoai;
        this.diaChi = diaChi;
    }

    public int getMaNCC() {
        return maNCC;
    }

    public void setMaNCC(int maNCC) {
        this.maNCC = maNCC;
    }

    public String getTenNCC() {
        return tenNCC;
    }

    public void setTenNCC(String tenNCC) {
        this.tenNCC = tenNCC;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
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
        maNCC = Integer.parseInt(sc.nextLine());
        System.out.print("Nhap ten nha cung cap: ");
        tenNCC = sc.nextLine();
        System.out.print("Nhap so dien thoai: ");
        soDienThoai = sc.nextLine();
        System.out.print("Nhap dia chi: ");
        diaChi = sc.nextLine();
    }

    @Override
    public void xuat() {
        System.out.printf("%-10d %-20s %-15s %-30s\n", maNCC, tenNCC, soDienThoai, diaChi);
    }
}