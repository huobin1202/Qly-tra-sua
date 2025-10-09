package view;

import java.util.Scanner;
import dao.KhoHang;
import dao.NhapHang;
import dao.XuatHang;

public class KhoHangView {
    public static void menu(Scanner sc) {
        KhoHang kho = new KhoHang();
        NhapHang nhap = new NhapHang();
        XuatHang xuat = new XuatHang();
        while (true) {
            ConsoleUI.printHeader("TỒN KHO / NHẬP / XUẤT");
            ConsoleUI.printSection("CHỨC NĂNG");
            System.out.println("│ 1. Xem tồn kho                        │");
            System.out.println("│ 2. Tạo phiếu nhập                     │");
            System.out.println("│ 3. Tạo phiếu xuất                     │");
            System.out.println("│ 4. Xem danh sách nhà cung cấp                │");
            System.out.println("│ 5. Xem sản phẩm của nhà cung cấp      │");
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
            if (chon == 1) kho.xemTon();
            else if (chon == 2) nhap.taoPhieuNhap();
            else if (chon == 3) xuat.taoPhieuXuat();
            else if (chon == 4) xemDanhSachNCC(sc);
            else if (chon == 5) xemSanPhamNCC(sc);
            else if (chon == 0) break;
            else System.out.println("Chức năng không hợp lệ.");
        }
    }
    private static void xemDanhSachNCC(Scanner sc) {
        try (java.sql.Connection conn = db.DBUtil.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement("SELECT MaNCC, TenNCC FROM nhacungcap")) {
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                ConsoleUI.printHeader("DANH SÁCH NHÀ CUNG CẤP");
                System.out.println("┌────┬────────────────────────────┐");
                System.out.println("│ ID │ Tên nhà cung cấp            │");
                System.out.println("├────┼────────────────────────────┤");
                while (rs.next()) {
                    System.out.printf("│ %-2d │ %-26s │\n", rs.getInt(1), rs.getString(2));
                }
                System.out.println("└────┴────────────────────────────┘");
                ConsoleUI.printFooter();
            }
        } catch (java.sql.SQLException e) { e.printStackTrace(); }
    }
    private static void xemSanPhamNCC(Scanner sc) {
        System.out.print(ConsoleUI.promptLabel("Nhập mã nhà cung cấp"));
        String idStr = sc.nextLine();
        int maNCC;
        try { maNCC = Integer.parseInt(idStr.trim()); } catch (NumberFormatException e) { System.out.println("Mã không hợp lệ."); return; }
        try (java.sql.Connection conn = db.DBUtil.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(
                "SELECT ncc.TenNCC, sp.MaMon, m.TenMon, sp.SoLuong, sp.DonGia FROM ncc_sanpham sp " +
                "JOIN nhacungcap ncc ON ncc.MaNCC=sp.MaNCC JOIN mon m ON m.MaMon=sp.MaMon WHERE sp.MaNCC=? ORDER BY sp.MaMon")) {
            ps.setInt(1, maNCC);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                ConsoleUI.printHeader("SẢN PHẨM THEO NHÀ CUNG CẤP");
                System.out.println("┌────┬────────────────────────────┬──────────┬──────────┐");
                System.out.println("│ ID │ Tên món                    │ SL NCC   │ Đơn giá  │");
                System.out.println("├────┼────────────────────────────┼──────────┼──────────┤");
                boolean any=false; String tenNCC=null;
                while (rs.next()) {
                    any=true; tenNCC = rs.getString(1);
                    System.out.printf("│ %-2d │ %-26s │ %-8d │ %-8d │\n", rs.getInt(2), rs.getString(3), rs.getInt(4), rs.getLong(5));
                }
                if (!any) System.out.println("│ Không có dữ liệu                                       │");
                System.out.println("└────┴────────────────────────────┴──────────┴──────────┘");
                if (tenNCC!=null) System.out.println("Nhà cung cấp: "+tenNCC);
                ConsoleUI.printFooter();
            }
        } catch (java.sql.SQLException e) { e.printStackTrace(); }
    }
}


