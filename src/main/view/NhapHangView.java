package view;

import java.util.Scanner;
import dao.NhapHang;

public class NhapHangView {
    private NhapHang nhapHangController;
    private Scanner scanner;
    
    public NhapHangView() {
        this.nhapHangController = new NhapHang();
        this.scanner = new Scanner(System.in);
    }
    
    public void menu() {
        while (true) {
            ConsoleUI.printHeader("QUẢN LÝ PHIẾU NHẬP");
            ConsoleUI.printSection("CHỨC NĂNG");
            System.out.println("│ 1. Tạo phiếu nhập mới                        │");
            System.out.println("│ 2. Xem danh sách phiếu nhập                   │");
            System.out.println("│ 3. Tìm kiếm phiếu nhập                        │");
            System.out.println("│ 4. Sửa phiếu nhập                             │");
            System.out.println("│ 5. Xác nhận phiếu nhập                        │");
            System.out.println("│ 6. Xóa phiếu nhập                             │");
            ConsoleUI.printSection("ĐIỀU HƯỚNG");
            System.out.println("│ 0. Quay lại                                   │");
            ConsoleUI.printFooter();
            
            System.out.print(ConsoleUI.promptLabel("Chọn chức năng"));
            String choiceStr = scanner.nextLine();
            int choice;
            
            try {
                choice = Integer.parseInt(choiceStr.trim());
            } catch (NumberFormatException e) {
                System.out.println("Vui lòng nhập số hợp lệ.");
                continue;
            }
            
            switch (choice) {
                case 1:
                    taoPhieuNhap();
                    break;
                case 2:
                    xemDanhSachPhieuNhap();
                    break;
                case 3:
                    timKiemPhieuNhap();
                    break;
                case 4:
                    suaPhieuNhap();
                    break;
                case 5:
                    xacNhanPhieuNhap();
                    break;
                case 6:
                    xoaPhieuNhap();
                    break;
             
                case 0:
                    return;
                default:
                    System.out.println("Chức năng không hợp lệ.");
            }
        }
    }
    
    private void taoPhieuNhap() {
        ConsoleUI.printHeader("TẠO PHIẾU NHẬP MỚI");
        nhapHangController.taoPhieuNhap();
        ConsoleUI.pause();
    }
    
    private void xemDanhSachPhieuNhap() {
        ConsoleUI.printHeader("DANH SÁCH PHIẾU NHẬP");
        nhapHangController.hienThiTatCaPhieuNhap();
        ConsoleUI.pause();
    }
    
    private void timKiemPhieuNhap() {
        ConsoleUI.printHeader("TÌM KIẾM PHIẾU NHẬP");
        nhapHangController.menuTimKiemPhieuNhap();
        ConsoleUI.pause();
    }
    
    private void suaPhieuNhap() {
        ConsoleUI.printHeader("SỬA PHIẾU NHẬP");
        nhapHangController.suaPhieuNhap();
        ConsoleUI.pause();
    }
    
    private void xacNhanPhieuNhap() {
        ConsoleUI.printHeader("XÁC NHẬN PHIẾU NHẬP");
        nhapHangController.xacNhanPhieuNhap();
        ConsoleUI.pause();
    }
    
    private void xoaPhieuNhap() {
        ConsoleUI.printHeader("XÓA PHIẾU NHẬP");
        nhapHangController.xoaPhieuNhap();
        ConsoleUI.pause();
    }
    

}
