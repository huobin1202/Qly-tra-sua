package view;

import java.util.Scanner;

import dao.BanDAO;
import dao.DonDatHangDAO;
import dao.GiaoHangDAO;

public class DatHangView {
    public static void menu(BanDAO QuanLyBan,DonDatHangDAO DonDatHang,GiaoHangDAO GiaoHang, Scanner sc) {

        while (true) {
            System.out.println("\n╔══════════════════════════════════════════════╗");
            System.out.println("║             QUẢN LÝ ĐẶT HÀNG                ║");
            System.out.println("╠══════════════════════════════════════════════╣");
            System.out.println("║ 1. Quản lý bàn                              ║");
            System.out.println("║ 2. Quản lý đơn đặt hàng                     ║");
            System.out.println("║ 3. Quản lý giao hàng                        ║");
            System.out.println("║ 4. Quay lại                                 ║");
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
                    if (chonBan == 1) QuanLyBan.them();
                    else if (chonBan == 2) QuanLyBan.sua();
                    else if (chonBan == 3) QuanLyBan.xoa();
                    else if (chonBan == 4) QuanLyBan.xuat();
                    else if (chonBan == 5) break;
                    else System.out.println("Chức năng không hợp lệ.");
                }
            } else if (chonDH == 2) {
                while (true) {
                    System.out.println("\n╔══════════════════════════════════════════════╗");
                    System.out.println("║        QUẢN LÝ ĐƠN ĐẶT HÀNG                 ║");
                    System.out.println("╠══════════════════════════════════════════════╣");
                    System.out.println("║ 1. Xem danh sách đơn đặt hàng               ║");
                    System.out.println("║ 1. Thêm đơn đặt hàng                        ║");
                    System.out.println("║ 3. Sửa đơn đặt hàng                         ║");
                    System.out.println("║ 4. Xóa đơn đặt hàng                         ║");
                    System.out.println("║ 5. Tìm kiếm đơn đặt hàng                    ║");
                    System.out.println("║ 6. Quay lại                                 ║");
                    System.out.println("╚══════════════════════════════════════════════╝");
                    System.out.print("Chọn chức năng: ");
                    int chonDon = sc.nextInt();
                    sc.nextLine();
                    if (chonDon == 1) DonDatHang.xuat();
                    else if (chonDon == 2) DonDatHang.them();
                    else if (chonDon == 3) DonDatHang.sua();
                    else if (chonDon == 4) DonDatHang.xoa();
                    else if (chonDon == 5) DonDatHang.timkiem();
                    else if (chonDon == 6) break;
                    else System.out.println("Chức năng không hợp lệ.");
                }
            } else if (chonDH == 3) {
                while (true) {
                    System.out.println("\n╔══════════════════════════════════════════════╗");
                    System.out.println("║        QUẢN LÝ GIAO HÀNG                      ║");
                    System.out.println("╠══════════════════════════════════════════════╣");
                    System.out.println("║ 1. Xem danh sách giao hàng                     ║");
                    System.out.println("║ 2. Thêm giao hàng                              ║");
                    System.out.println("║ 3. Sửa giao hàng                              ║");
                    System.out.println("║ 4. Xóa giao hàng                              ║");
                    System.out.println("║ 5. Quay lại                                 ║");
                    System.out.println("╚══════════════════════════════════════════════╝");
                    System.out.print("Chọn chức năng: ");
                    int chonGiao = sc.nextInt();
                    sc.nextLine();
                    if (chonGiao == 1) GiaoHang.xuat();
                    else if (chonGiao == 2) GiaoHang.them();
                    else if (chonGiao == 3) GiaoHang.sua();
                    else if (chonGiao == 4) GiaoHang.xoa();
                    else if (chonGiao == 5) break;
                    else System.out.println("Chức năng không hợp lệ.");
                }
            } else if (chonDH == 4) {
                break;
            }
        }
    }
}
