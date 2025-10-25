package dto;
import java.util.Scanner;

import controller.Inhapxuat;

import java.sql.Timestamp;

public class KhachHangDTO implements Inhapxuat {
    private int maKH;
    private String soDienThoai;
    private String hoTen;
    private String diaChi;
    private Timestamp ngaySinh;

    public KhachHangDTO() {
    }

    public KhachHangDTO(int maKH, String soDienThoai, String hoTen, String diaChi, Timestamp ngaySinh) {
        this.maKH = maKH;
        this.soDienThoai = soDienThoai;
        this.hoTen = hoTen;
        this.diaChi = diaChi;
        this.ngaySinh = ngaySinh;
    }

    public int getMaKH() {
        return maKH;
    }

    public void setMaKH(int maKH) {
        this.maKH = maKH;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public Timestamp getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(Timestamp ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    @Override
    public void nhap() {
        Scanner sc = new Scanner(System.in);
        try {
            System.out.print("Nhập số điện thoại: ");
            soDienThoai = sc.nextLine();
            System.out.print("Nhập họ tên: ");
            hoTen = sc.nextLine();
            System.out.print("Nhập địa chỉ: ");
            diaChi = sc.nextLine();
            System.out.print("Nhập ngày sinh (yyyy-mm-dd): ");
            String ngaySinhStr = sc.nextLine();
            if (!ngaySinhStr.isEmpty()) {
                ngaySinh = Timestamp.valueOf(ngaySinhStr + " 10:00:00");
            }
        } finally {
            sc.close();
        }
    }

    @Override
    public void xuat() {
        System.out.printf("%-10d %-15s %-20s %-30s %-20s\n", maKH, soDienThoai, hoTen, diaChi, ngaySinh);
    }
}