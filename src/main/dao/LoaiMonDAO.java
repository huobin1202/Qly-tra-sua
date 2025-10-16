package dao;
import java.sql.*;
import java.util.Scanner;

import database.DBUtil;
import view.ConsoleUI;

public class LoaiMonDAO {
    public void them() {
        Scanner sc = new Scanner(System.in);
        String ten = promptNonEmpty(sc, "Nhập tên loại món mới (0: hủy)");
        if (ten == null) { System.out.println("Đã hủy."); return; }
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO loaimon (TenLoai) VALUES (?)")) {
            ps.setString(1, ten);
            ps.executeUpdate();
            System.out.println("Đã thêm loại món.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void sua() {
        Scanner sc = new Scanner(System.in);
        try (Connection conn = DBUtil.getConnection()) {
            Integer ma = promptId(sc, "Nhập mã loại món cần sửa (0: hủy)");
            if (ma == null) { System.out.println("Đã hủy."); return; }
            while (!existsLoai(conn, ma)) {
                System.out.println("Không tìm thấy loại món, vui lòng nhập lại.");
                ma = promptId(sc, "Nhập mã loại món cần sửa (0: hủy)");
                if (ma == null) { System.out.println("Đã hủy."); return; }
            }

            printLoaiById(conn, ma);
            System.out.println("Chọn: 1. Sửa tên, 0. Hủy");
            Integer ch = promptInt(sc, "Chọn", 0, 1, false);
            if (ch == null || ch == 0) { System.out.println("Đã hủy."); return; }
            String ten = promptNonEmpty(sc, "Nhập tên loại món mới (0: hủy)");
            if (ten == null) { System.out.println("Đã hủy."); return; }
            try (PreparedStatement ps = conn.prepareStatement("UPDATE loaimon SET TenLoai=? WHERE MaLoai=?")) {
                ps.setString(1, ten);
                ps.setInt(2, ma);
                ps.executeUpdate();
                System.out.println("Đã sửa loại món.");
            }
            printLoaiById(conn, ma);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void xoa() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Nhập mã loại món cần xóa: ");
        int ma = sc.nextInt();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM loaimon WHERE MaLoai=?")) {
            ps.setInt(1, ma);
            int rows = ps.executeUpdate();
            if (rows > 0)
                System.out.println("Đã xóa loại món.");
            else
                System.out.println("Không tìm thấy loại món.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void xuat() {
        ConsoleUI.printHeader("DANH SÁCH LOẠI MÓN");
        System.out.println("┌────┬────────────────────────────┐");
        System.out.println("│ Mã │ Tên loại                   │");
        System.out.println("├────┼────────────────────────────┤");
        boolean any = false;
        try (Connection conn = DBUtil.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM loaimon ORDER BY MaLoai")) {
            while (rs.next()) {
                any = true;
                System.out.printf("│ %-2d │ %-26s │\n", rs.getInt("MaLoai"), rs.getString("TenLoai"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (!any) System.out.println("│ Không có dữ liệu                │");
        System.out.println("└────┴────────────────────────────┘");
        ConsoleUI.printFooter();
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

    private static Integer promptId(Scanner sc, String label) {
        return promptInt(sc, label, 1, Integer.MAX_VALUE, true);
    }

    private static String promptNonEmpty(Scanner sc, String label) {
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

    private static boolean existsLoai(Connection conn, int id) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM loaimon WHERE MaLoai=?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }

    private void printLoaiById(Connection conn, int id) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM loaimon WHERE MaLoai=?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                System.out.println("┌────┬────────────────────────────┐");
                System.out.println("│ Mã │ Tên loại                   │");
                System.out.println("├────┼────────────────────────────┤");
                boolean any = false;
                while (rs.next()) {
                    any = true;
                    System.out.printf("│ %-2d │ %-26s │\n", rs.getInt("MaLoai"), rs.getString("TenLoai"));
                }
                if (!any) System.out.println("│ Không có dữ liệu                │");
                System.out.println("└────┴────────────────────────────┘");
            }
        }
    }
}