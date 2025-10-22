package view;

import java.util.Scanner;
import dao.DonHangDAO;

public class KhoHangView {
    private DonHangDAO.KhoHangDAO khoHangDAO;
    private NhapHangView nhapHangView;
    private Scanner scanner;
    
    public KhoHangView() {
        this.khoHangDAO = new DonHangDAO.KhoHangDAO();
        this.nhapHangView = new NhapHangView();
        this.scanner = new Scanner(System.in);
    }
    
    public void menu() {
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
            String chonStr = scanner.nextLine();
            int chon;
            try {
                chon = Integer.parseInt(chonStr.trim());
            } catch (NumberFormatException e) {
                System.out.println("Vui lòng nhập số hợp lệ.");
                continue;
            }
            if (chon == 1) taoPhieuNhap();
            else if (chon == 2) xemTonKhoNguyenLieu();
            else if (chon == 3) xemDanhSachNCC();
            else if (chon == 4) xemNguyenLieuNCC();
            else if (chon == 0) break;
            else System.out.println("Chức năng không hợp lệ.");
        }
    }
    
    private void taoPhieuNhap() {
        ConsoleUI.printHeader("TẠO PHIẾU NHẬP");
        nhapHangView.taoPhieuNhap();
        ConsoleUI.pause();
    }
    
    private void xemTonKhoNguyenLieu() {
        ConsoleUI.printHeader("TỒN KHO NGUYÊN LIỆU");
        khoHangDAO.xemTonNguyenLieu();
        ConsoleUI.pause();
    }
    
    private void xemDanhSachNCC() {
        ConsoleUI.printHeader("DANH SÁCH NHÀ CUNG CẤP");
        khoHangDAO.xemDanhSachNCC(scanner);
        ConsoleUI.pause();
    }
    
    private void xemNguyenLieuNCC() {
        ConsoleUI.printHeader("NGUYÊN LIỆU NHÀ CUNG CẤP");
        khoHangDAO.xemNguyenLieuNCC(scanner);
        ConsoleUI.pause();
    }
}


