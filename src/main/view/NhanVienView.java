package view;

import java.util.Scanner;
import dao.NhanVienDAO;

public class NhanVienView {
    private NhanVienDAO nhanVienDAO;
    private Scanner scanner;
    
    public NhanVienView() {
        this.nhanVienDAO = new NhanVienDAO();
        this.scanner = new Scanner(System.in);
    }
    
    public void menu() {
        while (true) {
            ConsoleUI.printHeader("QUẢN LÝ NHÂN VIÊN");
            ConsoleUI.printSection("CHỨC NĂNG");
            System.out.println("│ 1. Xem danh sách nhân viên                   │");
            System.out.println("│ 2. Thêm nhân viên                            │");
            System.out.println("│ 3. Sửa nhân viên                             │");
            System.out.println("│ 4. Xóa nhân viên                             │");
            System.out.println("│ 5. Tìm kiếm nhân viên                        │");
            ConsoleUI.printSection("ĐIỀU HƯỚNG");
            System.out.println("│ 0. Quay lại                                  │");
            ConsoleUI.printFooter();
            System.out.print(ConsoleUI.promptLabel("Chọn chức năng"));
            String chonNVStr = scanner.nextLine();
            int chonNV;
            try {
                chonNV = Integer.parseInt(chonNVStr.trim());
            } catch (NumberFormatException e) {
                System.out.println("Vui lòng nhập số hợp lệ.");
                continue;
            }
            if (chonNV == 1) xemDanhSachNhanVien();
            else if (chonNV == 2) themNhanVien();
            else if (chonNV == 3) suaNhanVien();
            else if (chonNV == 4) xoaNhanVien();
            else if (chonNV == 5) timKiemNhanVien();
            else if (chonNV == 0) break;
            else System.out.println("Chức năng không hợp lệ.");
        }
    }
    
    private void xemDanhSachNhanVien() {
        ConsoleUI.printHeader("DANH SÁCH NHÂN VIÊN");
        nhanVienDAO.xuat();
        ConsoleUI.pause();
    }
    
    private void themNhanVien() {
        ConsoleUI.printHeader("THÊM NHÂN VIÊN MỚI");
        nhanVienDAO.them();
        ConsoleUI.pause();
    }
    
    private void suaNhanVien() {
        ConsoleUI.printHeader("SỬA NHÂN VIÊN");
        nhanVienDAO.sua();
        ConsoleUI.pause();
    }
    
    private void xoaNhanVien() {
        ConsoleUI.printHeader("XÓA NHÂN VIÊN");
        nhanVienDAO.xoa();
        ConsoleUI.pause();
    }
    
    private void timKiemNhanVien() {
        ConsoleUI.printHeader("TÌM KIẾM NHÂN VIÊN");
        nhanVienDAO.timkiem();
        ConsoleUI.pause();
    }
}