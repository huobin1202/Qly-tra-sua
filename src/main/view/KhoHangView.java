package view;

import java.util.Scanner;
import dao.KhoHang;
import dao.NhapHang;

public class KhoHangView {
    public static void menu(Scanner sc) {
        KhoHang kho = new KhoHang();
        NhapHang nhap = new NhapHang();
        while (true) {
            ConsoleUI.printHeader("NHẬP → KHO → XUẤT");
            ConsoleUI.printSection("CHỨC NĂNG");
            System.out.println("│ 1. Tạo phiếu nhập                     │");
            System.out.println("│ 2. Xem tồn kho                        │");
            System.out.println("│ 3. Xem danh sách nhà cung cấp                │");
            System.out.println("│ 4. Xem sản phẩm của nhà cung cấp      │");
            ConsoleUI.printSection("ĐIỀU HƯỚNG");
            System.out.println("│ 0. Quay lại                           │");
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
            if (chon == 1) nhap.taoPhieuNhap();
            else if (chon == 2) kho.xemTon();
            else if (chon == 3) kho.xemDanhSachNCC(sc);
            else if (chon == 4) kho.xemSanPhamNCC(sc);
            else if (chon == 0) break;
            else System.out.println("Chức năng không hợp lệ.");
        }
    }

}


