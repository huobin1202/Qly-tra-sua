package dao;
import java.sql.*;
import java.util.Scanner;

import database.DBUtil;
import view.ConsoleUI;

public class NguyenLieuDAO {
    public void them() {
        Scanner sc = new Scanner(System.in);
        String ten = promptString(sc, "Tên nguyên liệu (0: hủy)"); if (ten == null) { System.out.println("Đã hủy."); return; }
        String donVi = promptString(sc, "Đơn vị (0: hủy)"); if (donVi == null) { System.out.println("Đã hủy."); return; }
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO nguyenlieu (TenNL, DonVi) VALUES (?,?)")) {
            ps.setString(1, ten);
            ps.setString(2, donVi);
            ps.executeUpdate();
            System.out.println("Đã thêm nguyên liệu.");
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void sua() {
        Scanner sc = new Scanner(System.in);
        try (Connection conn = DBUtil.getConnection()) {
            Integer id = promptId(sc, "ID nguyên liệu cần sửa (0: hủy)"); if (id == null) { System.out.println("Đã hủy."); return; }
            if (!exists(conn, id)) { System.out.println("Không tìm thấy nguyên liệu."); return; }
            printOne(conn, id);
            System.out.println("Chọn: 1. Sửa tên, 2. Sửa đơn vị, 0. Hủy");
            Integer ch = promptInt(sc, "Chọn", 0, 2, false);
            if (ch == null || ch == 0) { System.out.println("Đã hủy."); return; }
            if (ch == 1) {
                String ten = promptString(sc, "Tên nguyên liệu mới (0: hủy)"); if (ten == null) return;
                updateOne(conn, "TenNL", ten, id);
            } else if (ch == 2) {
                String dv = promptString(sc, "Đơn vị mới (0: hủy)"); if (dv == null) return;
                updateOne(conn, "DonVi", dv, id);
            }
            System.out.println("Đã sửa.");
            printOne(conn, id);
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void xoa() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            Integer id = promptId(sc, "ID nguyên liệu cần xóa (0: hủy)"); if (id == null) { System.out.println("Đã hủy."); return; }
            try (Connection conn = DBUtil.getConnection();
                 PreparedStatement ps = conn.prepareStatement("DELETE FROM nguyenlieu WHERE MaNL=?")) {
                ps.setInt(1, id);
                int n = ps.executeUpdate();
                System.out.println(n > 0 ? "Đã xóa." : "Không tìm thấy.");
            } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    public void xuat() {
        ConsoleUI.printHeader("DANH SÁCH NGUYÊN LIỆU");
        System.out.println("┌────┬────────────────────────────┬──────────┐");
        System.out.println("│ ID │ Tên nguyên liệu            │ Đơn vị   │");
        System.out.println("├────┼────────────────────────────┼──────────┤");
        boolean any=false;
        try (Connection conn = DBUtil.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM nguyenlieu ORDER BY MaNL")) {
            while (rs.next()) {
                any=true;
                System.out.printf("│ %-2d │ %-26s │ %-8s │\n", rs.getInt("MaNL"), rs.getString("TenNL"), rs.getString("DonVi"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        if (!any) System.out.println("│ Không có dữ liệu                       │");
        System.out.println("└────┴────────────────────────────┴──────────┘");
        ConsoleUI.printFooter();
    }

    private static String promptString(Scanner sc, String label) {
        while (true) {
            System.out.print(label + ": ");
            String s = sc.nextLine();
            if (s == null) continue;
            s = s.trim();
            if (s.equals("0")) return null;
            if (!s.isEmpty()) return s;
            System.out.println("Vui lòng không để trống.");
        }
    }
    private static Integer promptInt(Scanner sc, String label, int min, int max, boolean allowZeroAsCancel) {
        while (true) {
            System.out.print(label + ": ");
            String s = sc.nextLine();
            if (s == null) continue;
            s = s.trim();
            if (allowZeroAsCancel && s.equals("0")) return null;
            try {
                int v = Integer.parseInt(s);
                if (v < min || v > max) { System.out.println("Giá trị nằm ngoài phạm vi."); continue; }
                return v;
            } catch (NumberFormatException e) { System.out.println("Vui lòng nhập số hợp lệ."); }
        }
    }
    private static Integer promptId(Scanner sc, String label) { return promptInt(sc, label, 1, Integer.MAX_VALUE, true); }
    private static boolean exists(Connection conn, int id) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM nguyenlieu WHERE MaNL=?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }
    private static void updateOne(Connection conn, String col, String val, int id) throws SQLException {
        String sql = "UPDATE nguyenlieu SET "+col+"=? WHERE MaNL=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, val);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }
    private static void printOne(Connection conn, int id) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM nguyenlieu WHERE MaNL=?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                System.out.println("╔════╦════════════════════╦════════════╗");
                System.out.println("║ ID ║ Tên NL            ║ Đơn vị     ║");
                System.out.println("╠════╬════════════════════╬════════════╣");
                boolean any=false;
                while (rs.next()) {
                    any=true;
                    System.out.printf("║ %-2d ║ %-18s ║ %-10s ║\n", rs.getInt("MaNL"), rs.getString("TenNL"), rs.getString("DonVi"));
                }
                if (!any) System.out.println("║ Không có dữ liệu                   ║");
                System.out.println("╚════╩════════════════════╩════════════╝");
            }
        }
    }
}


