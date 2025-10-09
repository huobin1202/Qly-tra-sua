package view;

import java.util.Scanner;
import dao.BanDAO;

public class BanView {
    public static void menu(BanDAO dsBan, Scanner sc) {
        while (true) {
            ConsoleUI.printHeader("QUẢN LÝ BÀN");
            ConsoleUI.printSection("CHỨC NĂNG");
            System.out.println("│ 1. Thêm bàn                        │");
            System.out.println("│ 2. Sửa bàn                         │");
            System.out.println("│ 3. Xóa bàn                         │");
            System.out.println("│ 4. Xem danh sách bàn               │");
            ConsoleUI.printSection("ĐIỀU HƯỚNG");
            System.out.println("│ 5. Quay lại                        │");
            ConsoleUI.printFooter();
            System.out.print(ConsoleUI.promptLabel("Chọn chức năng"));
            String chonBanStr = sc.nextLine();
            int chonBan;
            try {
                chonBan = Integer.parseInt(chonBanStr.trim());
            } catch (NumberFormatException e) {
                System.out.println("Vui lòng nhập số hợp lệ.");
                continue;
            }
            if (chonBan == 1) dsBan.them();
            else if (chonBan == 2) dsBan.sua();
            else if (chonBan == 3) dsBan.xoa();
            else if (chonBan == 4) dsBan.xuat();
            else if (chonBan == 5) break;
            else System.out.println("Chức năng không hợp lệ.");
        }
    }
}