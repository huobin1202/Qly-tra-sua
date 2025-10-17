package dao;
import java.sql.*;

import view.ConsoleUI;
import java.util.Scanner;

import database.DBUtil;
public class KhoHang {
    public void xemDanhSachNCC(Scanner sc) {
        try (java.sql.Connection conn = database.DBUtil.getConnection();
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
    public void xemSanPhamNCC(Scanner sc) {
        System.out.print(ConsoleUI.promptLabel("Nhập mã nhà cung cấp"));
        String idStr = sc.nextLine();
        int maNCC;
        try { maNCC = Integer.parseInt(idStr.trim()); } catch (NumberFormatException e) { System.out.println("Mã không hợp lệ."); return; }
        try (java.sql.Connection conn = database.DBUtil.getConnection();
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

    public void xemNguyenLieuNCC(Scanner sc) {
        System.out.print(ConsoleUI.promptLabel("Nhập mã nhà cung cấp"));
        String idStr = sc.nextLine();
        int maNCC;
        try { maNCC = Integer.parseInt(idStr.trim()); } catch (NumberFormatException e) { System.out.println("Mã không hợp lệ."); return; }
        String sql = "SELECT ncc.TenNCC, nl.MaNL, nl.TenNL, nccnl.SoLuong, nccnl.DonGia, nl.DonVi " +
                     "FROM ncc_nguyenlieu nccnl " +
                     "JOIN nhacungcap ncc ON ncc.MaNCC=nccnl.MaNCC " +
                     "JOIN nguyenlieu nl ON nl.MaNL=nccnl.MaNL " +
                     "WHERE nccnl.MaNCC=? ORDER BY nl.MaNL";
        try (java.sql.Connection conn = database.DBUtil.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maNCC);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                ConsoleUI.printHeader("NGUYÊN LIỆU THEO NHÀ CUNG CẤP");
                System.out.println("┌────┬────────────────────────────┬──────────┬──────────┬──────────┐");
                System.out.println("│ ID │ Tên nguyên liệu            │ Đơn vị   │ SL NCC   │ Đơn giá  │");
                System.out.println("├────┼────────────────────────────┼──────────┼──────────┼──────────┤");
                boolean any=false; String tenNCC=null;
                while (rs.next()) {
                    any=true; tenNCC = rs.getString(1);
                    System.out.printf("│ %-2d │ %-26s │ %-8s │ %-8d │ %-8d │\n", rs.getInt(2), rs.getString(3), rs.getString(6), rs.getInt(4), rs.getLong(5));
                }
                if (!any) System.out.println("│ Không có dữ liệu                                                    │");
                System.out.println("└────┴────────────────────────────┴──────────┴──────────┴──────────┘");
                if (tenNCC!=null) System.out.println("Nhà cung cấp: "+tenNCC);
                ConsoleUI.printFooter();
            }
        } catch (java.sql.SQLException e) { e.printStackTrace(); }
    }

    public void xemTonNguyenLieu() {
        ConsoleUI.printHeader("TỒN KHO NGUYÊN LIỆU");
        System.out.println("┌────┬────────────────────────────┬──────────┬──────────┐");
        System.out.println("│ ID │ Tên nguyên liệu            │ Đơn vị   │ Tồn      │");
        System.out.println("├────┼────────────────────────────┼──────────┼──────────┤");
        boolean any = false;
        String sql = "SELECT nl.MaNL, nl.TenNL, nl.DonVi, IFNULL(k.SoLuong,0) SoLuong " +
                     "FROM nguyenlieu nl LEFT JOIN kho_nguyenlieu k ON nl.MaNL = k.MaNL ORDER BY nl.MaNL";
        try (Connection conn = DBUtil.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                any = true;
                System.out.printf("│ %-2d │ %-26s │ %-8s │ %-8d │\n",
                    rs.getInt("MaNL"), rs.getString("TenNL"), rs.getString("DonVi"),
                    rs.getInt("SoLuong"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        if (!any) System.out.println("│ Không có dữ liệu                                                     │");
        System.out.println("└────┴────────────────────────────┴──────────┴──────────┘");
        ConsoleUI.printFooter();
    }
    public void xemTon() {
        ConsoleUI.printHeader("TỒN KHO");
        System.out.println("┌────┬────────────────────────────┬──────────┬──────────┐");
        System.out.println("│ ID │ Tên món                    │ Đơn vị   │ Tồn      │");
        System.out.println("├────┼────────────────────────────┼──────────┼──────────┤");
        boolean any = false;
        String sql = "SELECT m.MaMon, m.TenMon, m.TenDonVi, IFNULL(k.SoLuong,0) SoLuong " +
                     "FROM mon m LEFT JOIN khohang k ON m.MaMon = k.MaMon ORDER BY m.MaMon";
        try (Connection conn = DBUtil.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                any = true;
                System.out.printf("│ %-2d │ %-26s │ %-8s │ %-8d │\n",
                    rs.getInt("MaMon"), rs.getString("TenMon"), rs.getString("TenDonVi"),
                    rs.getInt("SoLuong"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        if (!any) System.out.println("│ Không có dữ liệu                                                     │");
        System.out.println("└────┴────────────────────────────┴──────────┴──────────┘");
        ConsoleUI.printFooter();
    }
}
