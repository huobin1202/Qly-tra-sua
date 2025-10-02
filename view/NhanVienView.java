package view;

import java.util.Scanner;
import dao.DSNhanVien;

public class NhanVienView {
    public static void menu(DSNhanVien dsNhanVien, Scanner sc) {
        while (true) {
            System.out.println("\n╔══════════════════════════════════════════════╗");
            System.out.println("║           QUẢN LÝ NHÂN VIÊN                 ║");
            System.out.println("╠══════════════════════════════════════════════╣");
            System.out.println("║ 1. Thêm nhân viên                           ║");
            System.out.println("║ 2. Sửa nhân viên                            ║");
            System.out.println("║ 3. Xóa nhân viên                            ║");
            System.out.println("║ 4. Xem danh sách nhân viên                  ║");
            System.out.println("║ 5. Tìm kiếm nhân viên                       ║");
            System.out.println("║ 6. Quay lại                                 ║");
            System.out.println("╚══════════════════════════════════════════════╝");
            System.out.print("Chọn chức năng: ");
            int chonNV = sc.nextInt();
            sc.nextLine();
            if (chonNV == 1) dsNhanVien.them();
            else if (chonNV == 2) dsNhanVien.sua();
            else if (chonNV == 3) dsNhanVien.xoa();
            else if (chonNV == 4) dsNhanVien.xuat();
            else if (chonNV == 5) dsNhanVien.timkiem();
            else if (chonNV == 6) break;
            else System.out.println("Chức năng không hợp lệ.");
        }
    }
}