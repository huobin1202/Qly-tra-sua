package view;

import java.util.Scanner;
import dao.KhachHangDAO;

public class KhachHangView {
    public static void menu(KhachHangDAO dsKhachHang, Scanner sc) {
        while (true) {
<<<<<<< refs/remotes/ChiDung/test3
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
            String chonStr = sc.nextLine();
            int chon;
            try {
                chon = Integer.parseInt(chonStr.trim());
            } catch (NumberFormatException e) {
                System.out.println("Vui lòng nhập số hợp lệ.");
                continue;
            }
=======
            System.out.println("\n╔═════════════════════════════════════════════╗");
            System.out.println("║           QUẢN LÝ KHÁCH HÀNG                ║");
            System.out.println("╠═════════════════════════════════════════════╣");
            System.out.println("║ 1. Xem danh sách khách hàng                 ║");
            System.out.println("║ 2. Thêm khách hàng                          ║");
            System.out.println("║ 3. Sửa thông tin khách hàng                 ║");
            System.out.println("║ 4. Xóa khách hàng                           ║");
            System.out.println("║ 5. Tìm kiếm khách hàng                      ║");
            System.out.println("║ 6. Quay lại                                 ║");
            System.out.println("╚═════════════════════════════════════════════╝");
            System.out.print("Chọn chức năng: ");
            int chon = sc.nextInt();
            sc.nextLine();
>>>>>>> local
            if (chon == 1) dsKhachHang.xuat();
            else if (chon == 2) dsKhachHang.them();
            else if (chon == 3) dsKhachHang.sua();
            else if (chon == 4) dsKhachHang.xoa();
            else if (chon == 5) dsKhachHang.timkiem();
            else if (chon == 0) break;
            else System.out.println("Chức năng không hợp lệ.");
        }
    }
}