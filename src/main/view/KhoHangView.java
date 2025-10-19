package view;

import java.util.Scanner;
import dao.KhoHangDAO;
import dao.NhapHang;

public class KhoHangView {
    public static void menu(Scanner sc) {
        KhoHangDAO kho = new KhoHangDAO();
        NhapHang nhap = new NhapHang();
        while (true) {
            ConsoleUI.printHeader("KHO NGUYÊN LIỆU / NCC");
            ConsoleUI.printSection("CHỨC NĂNG");
            System.out.println("│ 1. Tạo phiếu nhập                  │");
            System.out.println("│ 2. Xem tồn kho nguyên liệu         │");
            System.out.println("│ 3. Xem danh sách nhà cung cấp      │");
            System.out.println("│ 4. Xem nguyên liệu của nhà cung cấp│");
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
            else if (chon == 2) kho.xemTonNguyenLieu();
            else if (chon == 3) kho.xemDanhSachNCC(sc);
            else if (chon == 4) kho.xemNguyenLieuNCC(sc);
            else if (chon == 0) break;
            else System.out.println("Chức năng không hợp lệ.");
        }
    }

}


