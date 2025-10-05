package view;

import java.util.Scanner;
import dao.BanDAO;

public class BanView {
    public static void menu(BanDAO dsBan, Scanner sc) {
        while (true) {
            System.out.println("\n╔════════════════════════════════════╗");
            System.out.println("║           QUẢN LÝ BÀN             ║");
            System.out.println("╠════════════════════════════════════╣");
            System.out.println("║ 1. Thêm bàn                       ║");
            System.out.println("║ 2. Sửa bàn                        ║");
            System.out.println("║ 3. Xóa bàn                        ║");
            System.out.println("║ 4. Xem danh sách bàn              ║");
            System.out.println("║ 5. Quay lại                       ║");
            System.out.println("╚════════════════════════════════════╝");
            System.out.print("Chọn chức năng: ");
            int chonBan = sc.nextInt();
            sc.nextLine();
            if (chonBan == 1) dsBan.them();
            else if (chonBan == 2) dsBan.sua();
            else if (chonBan == 3) dsBan.xoa();
            else if (chonBan == 4) dsBan.xuat();
            else if (chonBan == 5) break;
            else System.out.println("Chức năng không hợp lệ.");
        }
    }
}