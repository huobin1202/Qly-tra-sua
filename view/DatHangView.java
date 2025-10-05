package view;

import java.util.Scanner;

import dao.BanDAO;
import dao.DonDatHangDAO;

public class DatHangView {
    public static void menu(BanDAO dsQuanLyBan,DonDatHangDAO dsDonDatHang, Scanner sc) {

        while (true) {
            System.out.println("\n╔══════════════════════════════════════════════╗");
            System.out.println("║             QUẢN LÝ ĐẶT HÀNG                ║");
            System.out.println("╠══════════════════════════════════════════════╣");
            System.out.println("║ 1. Quản lý bàn                              ║");
            System.out.println("║ 2. Quản lý đơn đặt hàng                     ║");
            System.out.println("║ 3. Quay lại                                 ║");
            System.out.println("╚══════════════════════════════════════════════╝");
            System.out.print("Chọn chức năng: ");
            int chonDH = sc.nextInt();
            sc.nextLine();
            if (chonDH == 1) {
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
                    if (chonBan == 1) dsQuanLyBan.them();
                    else if (chonBan == 2) dsQuanLyBan.sua();
                    else if (chonBan == 3) dsQuanLyBan.xoa();
                    else if (chonBan == 4) dsQuanLyBan.xuat();
                    else if (chonBan == 5) break;
                    else System.out.println("Chức năng không hợp lệ.");
                }
            } else if (chonDH == 2) {
                while (true) {
                    System.out.println("\n╔══════════════════════════════════════════════╗");
                    System.out.println("║        QUẢN LÝ ĐƠN ĐẶT HÀNG                 ║");
                    System.out.println("╠══════════════════════════════════════════════╣");
                    System.out.println("║ 1. Thêm đơn đặt hàng                        ║");
                    System.out.println("║ 2. Sửa đơn đặt hàng                         ║");
                    System.out.println("║ 3. Xóa đơn đặt hàng                         ║");
                    System.out.println("║ 4. Xem danh sách đơn đặt hàng               ║");
                    System.out.println("║ 5. Tìm kiếm đơn đặt hàng                    ║");
                    System.out.println("║ 6. Quay lại                                 ║");
                    System.out.println("╚══════════════════════════════════════════════╝");
                    System.out.print("Chọn chức năng: ");
                    int chonDon = sc.nextInt();
                    sc.nextLine();
                    if (chonDon == 1) dsDonDatHang.them();
                    else if (chonDon == 2) dsDonDatHang.sua();
                    else if (chonDon == 3) dsDonDatHang.xoa();
                    else if (chonDon == 4) dsDonDatHang.xuat();
                    else if (chonDon == 5) dsDonDatHang.timkiem();
                    else if (chonDon == 6) break;
                    else System.out.println("Chức năng không hợp lệ.");
                }
            } else if (chonDH == 3) {
                break;
            }
        }
    }
}
