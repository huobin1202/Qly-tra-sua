package view;

import java.util.Scanner;
import dao.NhanVienDAO;

public class NhanVienView {
    public static void menu(NhanVienDAO dsNhanVien, Scanner sc) {
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
            String chonNVStr = sc.nextLine();
            int chonNV;
            try {
                chonNV = Integer.parseInt(chonNVStr.trim());
            } catch (NumberFormatException e) {
                System.out.println("Vui lòng nhập số hợp lệ.");
                continue;
            }
            if (chonNV == 2) dsNhanVien.them();
            else if (chonNV == 3) dsNhanVien.sua();
            else if (chonNV == 4) dsNhanVien.xoa();
            else if (chonNV == 1) dsNhanVien.xuat();
            else if (chonNV == 5) dsNhanVien.timkiem();
            else if (chonNV == 0) break;
            else System.out.println("Chức năng không hợp lệ.");
        }
    }
}