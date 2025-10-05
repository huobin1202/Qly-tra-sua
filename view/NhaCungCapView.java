package view;

import java.util.Scanner;
import dao.NhaCungCapDAO;

public class NhaCungCapView {
    public static void menu(NhaCungCapDAO dsNhaCungCap, Scanner sc) {
        while (true) {
            System.out.println("\n╔══════════════════════════════════════════════╗");
            System.out.println("║           QUẢN LÝ NHÀ CUNG CẤP                ║");
            System.out.println("╠══════════════════════════════════════════════╣");
            System.out.println("║ 1. Xem danh sách nhà cung cấp                 ║");
            System.out.println("║ 2. Thêm nhà cung cấp                          ║");
            System.out.println("║ 3. Sửa thông tin nhà cung cấp                 ║");
            System.out.println("║ 4. Xóa nhà cung cấp                           ║");
            System.out.println("║ 5. Tìm kiếm nhà cung cấp                      ║");
            System.out.println("║ 6. Quay lại                                 ║");
            System.out.println("╚══════════════════════════════════════════════╝");
            System.out.print("Chọn chức năng: ");
            int chon = sc.nextInt();
            sc.nextLine();
            if (chon == 1) dsNhaCungCap.xuat();
            else if (chon == 2) dsNhaCungCap.them();
            else if (chon == 3) dsNhaCungCap.sua();
            else if (chon == 4) dsNhaCungCap.xoa();
            else if (chon == 5) dsNhaCungCap.timkiem();
            else if (chon == 6) break;
            else System.out.println("Chức năng không hợp lệ.");
        }
    }
}