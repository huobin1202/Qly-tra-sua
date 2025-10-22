package view;

import java.util.Scanner;
import dao.KhachHangDAO;

public class KhachHangView {
    private KhachHangDAO khachHangDAO;
    private Scanner scanner;
    
    public KhachHangView() {
        this.khachHangDAO = new KhachHangDAO();
        this.scanner = new Scanner(System.in);
    }
    
    public void menu() {
        while (true) {
            ConsoleUI.printHeader("QUẢN LÝ KHÁCH HÀNG");
            ConsoleUI.printSection("CHỨC NĂNG");
            System.out.println("│ 1. Xem danh sách khách hàng                  │");
            System.out.println("│ 2. Thêm khách hàng                           │");
            System.out.println("│ 3. Sửa thông tin khách hàng                  │");
            System.out.println("│ 4. Xóa khách hàng                            │");
            System.out.println("│ 5. Tìm kiếm khách hàng                       │");
            ConsoleUI.printSection("ĐIỀU HƯỚNG");
            System.out.println("│ 0. Quay lại                                  │");
            ConsoleUI.printFooter();
            System.out.print(ConsoleUI.promptLabel("Chọn chức năng"));
            String chonStr = scanner.nextLine();
            int chon;
            try {
                chon = Integer.parseInt(chonStr.trim());
            } catch (NumberFormatException e) {
                System.out.println("Vui lòng nhập số hợp lệ.");
                continue;
            }
            if (chon == 1) xemDanhSachKhachHang();
            else if (chon == 2) themKhachHang();
            else if (chon == 3) suaKhachHang();
            else if (chon == 4) xoaKhachHang();
            else if (chon == 5) timKiemKhachHang();
            else if (chon == 0) break;
            else System.out.println("Chức năng không hợp lệ.");
        }
    }
    
    private void xemDanhSachKhachHang() {
        ConsoleUI.printHeader("DANH SÁCH KHÁCH HÀNG");
        khachHangDAO.xuat();
        ConsoleUI.pause();
    }
    
    private void themKhachHang() {
        ConsoleUI.printHeader("THÊM KHÁCH HÀNG MỚI");
        khachHangDAO.them();
        ConsoleUI.pause();
    }
    
    private void suaKhachHang() {
        ConsoleUI.printHeader("SỬA THÔNG TIN KHÁCH HÀNG");
        khachHangDAO.sua();
        ConsoleUI.pause();
    }
    
    private void xoaKhachHang() {
        ConsoleUI.printHeader("XÓA KHÁCH HÀNG");
        khachHangDAO.xoa();
        ConsoleUI.pause();
    }
    
    private void timKiemKhachHang() {
        ConsoleUI.printHeader("TÌM KIẾM KHÁCH HÀNG");
        khachHangDAO.timkiem();
        ConsoleUI.pause();
    }
}