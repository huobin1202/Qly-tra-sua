package view;

import java.util.Scanner;

import dao.DonHangDAO;
import dao.ChiTietDonHangDAO;

public class DonHangView {
    public static void menu(DonHangDAO DonDatHang, ChiTietDonHangDAO ChiTietDon, Scanner sc) {
        ChiTietDonHangDAO chiTietDon = new ChiTietDonHangDAO();
        while (true) {
            ConsoleUI.printHeader("QUẢN LÝ ĐƠN HÀNG");
            ConsoleUI.printSection("CHỨC NĂNG");
            System.out.println("│ 1. Xem danh sách đơn hàng                │");
            System.out.println("│ 2. Thêm đơn hàng                         │");
            System.out.println("│ 3. Sửa đơn hàng                          │");
            System.out.println("│ 4. Xóa đơn hàng                          │");
            System.out.println("│ 5. Tìm kiếm đơn hàng                     │");
            System.out.println("│ 6. Quản lý chi tiết đơn hàng                 │");
            ConsoleUI.printSection("ĐIỀU HƯỚNG");
            System.out.println("│ 0. Quay lại                                  │");
            ConsoleUI.printFooter();
            System.out.print(ConsoleUI.promptLabel("Chọn chức năng"));
            String chonDonStr = sc.nextLine();
            int chonDon;
            try {
                chonDon = Integer.parseInt(chonDonStr.trim());
            } catch (NumberFormatException e) {
                System.out.println("Vui lòng nhập số hợp lệ.");
                continue;
            }
            if (chonDon == 1)
                DonDatHang.xuat();
            else if (chonDon == 2)
                DonDatHang.them();
            else if (chonDon == 3)
                DonDatHang.sua();
            else if (chonDon == 4)
                DonDatHang.xoa();
            else if (chonDon == 5)
                DonDatHang.timkiem();
            else if (chonDon == 6) {
                while (true) {
                    ConsoleUI.printHeader("CHI TIẾT ĐƠN HÀNG");
                    ConsoleUI.printSection("CHỨC NĂNG");
                    System.out.println("│ 1. Xem chi tiết theo đơn                     │");
                    System.out.println("│ 2. Thêm chi tiết vào đơn                     │");
                    System.out.println("│ 3. Sửa một dòng chi tiết                     │");
                    System.out.println("│ 4. Xóa một dòng chi tiết                     │");
                    System.out.println("│ 5. Xem danh sách tất cả các đơn              │");
                    ConsoleUI.printSection("ĐIỀU HƯỚNG");
                    System.out.println("│ 0. Quay lại                                  │");
                    ConsoleUI.printFooter();
                    System.out.print(ConsoleUI.promptLabel("Chọn chức năng"));
                    String chStr = sc.nextLine();
                    int ch;
                    try {
                        ch = Integer.parseInt(chStr.trim());
                    } catch (NumberFormatException e) {
                        System.out.println("Vui lòng nhập số hợp lệ.");
                        continue;
                    }
                    if (ch == 0)
                        return;
                    if (ch == 1)
                        chiTietDon.xemChiTietTheoDon(sc);
                    else if (ch == 2)
                        chiTietDon.them(sc);
                    else if (ch == 3)
                        chiTietDon.sua(sc);
                    else if (ch == 4)
                        chiTietDon.xoa(sc);
                    else if (ch == 5)
                        chiTietDon.xuat();
                    else
                        System.out.println("Chức năng không hợp lệ.");
                }
            } else if (chonDon == 0)
                break;
            else
                System.out.println("Chức năng không hợp lệ.");
        }
    }
}
