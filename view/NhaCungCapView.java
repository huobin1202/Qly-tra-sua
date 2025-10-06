package view;

import java.util.Scanner;
import dao.NhaCungCapDAO;

public class NhaCungCapView {
    public static void menu(NhaCungCapDAO dsNhaCungCap, Scanner sc) {
        while (true) {
            ConsoleUI.printHeader("QUẢN LÝ NHÀ CUNG CẤP");
            ConsoleUI.printSection("CHỨC NĂNG");
            System.out.println("│ 1. Xem danh sách nhà cung cấp                 │");
            System.out.println("│ 2. Thêm nhà cung cấp                          │");
            System.out.println("│ 3. Sửa thông tin nhà cung cấp                 │");
            System.out.println("│ 4. Xóa nhà cung cấp                           │");
            System.out.println("│ 5. Tìm kiếm nhà cung cấp                      │");
            ConsoleUI.printSection("ĐIỀU HƯỚNG");
            System.out.println("│ 0. Quay lại                                   │");
            ConsoleUI.printFooter();
            System.out.print(ConsoleUI.promptLabel("Chọn chức năng"));
            String chonStr = sc.nextLine();
            int chon;
            try {
                chon = Integer.parseInt(chonStr.trim());
            } catch (NumberFormatException e) {
                System.out.println("Vui lòng nhập số hợp lệ.");
                continue;
            }
            if (chon == 1) dsNhaCungCap.xuat();
            else if (chon == 2) dsNhaCungCap.them();
            else if (chon == 3) dsNhaCungCap.sua();
            else if (chon == 4) dsNhaCungCap.xoa();
            else if (chon == 5) dsNhaCungCap.timkiem();
            else if (chon == 0) break;
            else System.out.println("Chức năng không hợp lệ.");
        }
    }
}