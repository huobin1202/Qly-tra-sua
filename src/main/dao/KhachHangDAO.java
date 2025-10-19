package dao;
import java.util.Scanner;

import controller.IQuanLy;
import database.DBUtil;
import view.ConsoleUI;

import java.sql.*; // Thêm import này
public class KhachHangDAO implements IQuanLy {
    // Thêm khách hàng vào database
    public void them() {
        Scanner rd = new Scanner(System.in);
        try {
        String ten = promptNonEmpty(rd, "Nhập tên khách hàng (0: hủy)"); if (ten == null) { System.out.println("Đã hủy."); return; }
        String sdt = promptNonEmpty(rd, "Nhập số điện thoại (0: hủy)"); if (sdt == null) { System.out.println("Đã hủy."); return; }
        String diachi = promptNonEmpty(rd, "Nhập địa chỉ (0: hủy)"); if (diachi == null) { System.out.println("Đã hủy."); return; }
        System.out.print("Nhập ngày sinh (yyyy-mm-dd, để trống nếu không có, 0: hủy): ");
        String ngaysinhStr = rd.nextLine(); if ("0".equals(ngaysinhStr != null ? ngaysinhStr.trim() : "")) { System.out.println("Đã hủy."); return; }

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO khachhang (HoTen, SDT, DiaChi, NgaySinh) VALUES (?, ?, ?, ?)")) {
            ps.setString(1, ten);
            ps.setString(2, sdt);
            ps.setString(3, diachi);
            if (ngaysinhStr == null || ngaysinhStr.trim().isEmpty()) ps.setNull(4, java.sql.Types.TIMESTAMP);
            else ps.setTimestamp(4, java.sql.Timestamp.valueOf(ngaysinhStr.trim() + " 10:00:00"));
            ps.executeUpdate();
            System.out.println("Đã thêm khách hàng vào database.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        } finally { rd.close(); }
    }

    // Sửa thông tin khách hàng trong database
    public void sua() {
        Scanner rd = new Scanner(System.in);
        try (Connection conn = DBUtil.getConnection()) {
            Integer id = promptId(rd, "Nhập ID khách hàng cần sửa (0: hủy)");
            if (id == null) { System.out.println("Đã hủy."); return; }
            while (!existsKH(conn, id)) {
                System.out.println("Không tìm thấy khách hàng, vui lòng nhập lại.");
                id = promptId(rd, "Nhập ID khách hàng cần sửa (0: hủy)");
                if (id == null) { System.out.println("Đã hủy."); return; }
            }
            printCustomerById(conn, id);
            System.out.println("Chọn: 1. Sửa toàn bộ, 2. Tên, 3. SĐT, 4. Địa chỉ, 5. Ngày sinh, 0. Hủy");
            Integer ch = promptInt(rd, "Chọn", 0, 5, false);
            if (ch == null || ch == 0) { System.out.println("Đã hủy."); return; }
            switch (ch) {
                case 1: {
                    String ten = promptNonEmpty(rd, "Nhập tên khách hàng mới (0: hủy)"); if (ten == null) return;
                    String sdt = promptNonEmpty(rd, "Nhập số điện thoại mới (0: hủy)"); if (sdt == null) return;
                    String diachi = promptNonEmpty(rd, "Nhập địa chỉ mới (0: hủy)"); if (diachi == null) return;
                    String ngay = promptDateOrEmpty(rd, "Nhập ngày sinh mới (yyyy-mm-dd, rỗng bỏ qua, 0: hủy)"); if (ngay == null) return;
                    try (PreparedStatement ps = conn.prepareStatement("UPDATE khachhang SET HoTen=?, SDT=?, DiaChi=?, NgaySinh=? WHERE MaKH=?")) {
                        ps.setString(1, ten);
                        ps.setString(2, sdt);
                        ps.setString(3, diachi);
                        if (ngay.isEmpty()) ps.setNull(4, java.sql.Types.TIMESTAMP); else ps.setTimestamp(4, java.sql.Timestamp.valueOf(ngay + " 10:00:00"));
                        ps.setInt(5, id);
                        ps.executeUpdate();
                    }
                    break;
                }
                case 2: {
                    String ten = promptNonEmpty(rd, "Nhập tên khách hàng mới (0: hủy)"); if (ten == null) return;
                    updateOne(conn, "HoTen", ten, id);
                    break;
                }
                case 3: {
                    String sdt = promptNonEmpty(rd, "Nhập số điện thoại mới (0: hủy)"); if (sdt == null) return;
                    updateOne(conn, "SDT", sdt, id);
                    break;
                }
                case 4: {
                    String diachi = promptNonEmpty(rd, "Nhập địa chỉ mới (0: hủy)"); if (diachi == null) return;
                    updateOne(conn, "DiaChi", diachi, id);
                    break;
                }
                case 5: {
                    String ngay = promptDateOrEmpty(rd, "Nhập ngày sinh mới (yyyy-mm-dd, rỗng bỏ qua, 0: hủy)"); if (ngay == null) return;
                    try (PreparedStatement ps = conn.prepareStatement("UPDATE khachhang SET NgaySinh=? WHERE MaKH=?")) {
                        if (ngay.isEmpty()) ps.setNull(1, java.sql.Types.TIMESTAMP); else ps.setTimestamp(1, java.sql.Timestamp.valueOf(ngay + " 10:00:00"));
                        ps.setInt(2, id);
                        ps.executeUpdate();
                    }
                    break;
                }
            }
            System.out.println("Đã sửa thông tin khách hàng.");
            printCustomerById(conn, id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Xóa khách hàng khỏi database
    public void xoa() {
        Scanner rd = new Scanner(System.in);
        try (Connection conn = DBUtil.getConnection()) {
            while (true) {
                Integer id = promptId(rd, "Nhập ID khách hàng cần xóa (0: hủy)");
                if (id == null) { System.out.println("Đã hủy."); return; }
                if (!existsKH(conn, id)) { System.out.println("Không tìm thấy khách hàng, vui lòng nhập lại."); continue; }
                try (PreparedStatement ps = conn.prepareStatement("DELETE FROM khachhang WHERE MaKH=?")) {
                    ps.setInt(1, id);
                    ps.executeUpdate();
                    System.out.println("Đã xóa khách hàng.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally { rd.close(); }
    }

    // Tìm kiếm khách hàng trong database
    public void timkiem() {
        Scanner rd = new Scanner(System.in);
        System.out.println("Tìm theo: 1.ID  2.Tên");
        System.out.print("Chọn: ");
        String chStr = rd.nextLine();
        int ch; try { ch = Integer.parseInt(chStr.trim()); } catch (NumberFormatException e) { System.out.println("Vui lòng nhập số hợp lệ."); return; }
        String sql;
        try (Connection conn = DBUtil.getConnection()) {
            if (ch == 1) {
                System.out.print("Nhập ID: ");
                String idStr = rd.nextLine();
                int id; try { id = Integer.parseInt(idStr.trim()); } catch (NumberFormatException e) { System.out.println("ID không hợp lệ."); return; }
                sql = "SELECT * FROM khachhang WHERE MaKH = ?";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, id);
                    try (ResultSet rs = ps.executeQuery()) { printCustomerTable(rs); }
                }
            } else {
                System.out.print("Nhập tên: ");
                String ten = rd.nextLine();
                sql = "SELECT * FROM khachhang WHERE HoTen LIKE ?";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, "%" + ten + "%");
                    try (ResultSet rs = ps.executeQuery()) { printCustomerTable(rs); }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally { rd.close(); }
    }

    private void printCustomerTable(ResultSet rs) throws SQLException {
        System.out.println("\n╔════╦════════════════════╦════════════╦════════════════════════════╦════════════╗");
        System.out.println("║ ID ║ Tên               ║ SĐT        ║ Địa chỉ                   ║ Ngày sinh  ║");
        System.out.println("╠════╬════════════════════╬════════════╬════════════════════════════╬════════════╣");
        boolean any = false;
        while (rs.next()) {
            any = true;
            System.out.printf("║ %-2d ║ %-18s ║ %-10s ║ %-26s ║ %-10s ║\n",
                rs.getInt("MaKH"), rs.getString("HoTen"), rs.getString("SDT"), rs.getString("DiaChi"),
                rs.getString("NgaySinh") == null ? "" : rs.getString("NgaySinh"));
        }
        if (!any) System.out.println("║ Không có kết quả                                                       ║");
        System.out.println("╚════╩════════════════════╩════════════╩════════════════════════════╩════════════╝");
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

    private static boolean existsKH(Connection conn, int id) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM khachhang WHERE MaKH=?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }

    private static void updateOne(Connection conn, String column, String value, int id) throws SQLException {
        String sql = "UPDATE khachhang SET " + column + "=? WHERE MaKH=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, value);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    private static String promptDateOrEmpty(Scanner sc, String label) {
        System.out.print(label + ": ");
        String s = sc.nextLine();
        if (s == null) return "";
        s = s.trim();
        if (s.equals("0")) return null;
        return s; // simple passthrough, DBUtil cast with 10:00:00 like existing
    }

    private void printCustomerById(Connection conn, int id) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM khachhang WHERE MaKH=?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) { printCustomerTable(rs); }
        }
    }
    @Override
    public void nhap() {
    }
    public void xuat() {
        ConsoleUI.printHeader("DANH SÁCH KHÁCH HÀNG");
        System.out.println("┌────┬────────────────────┬────────────┬──────────────────────────┬────────────┐");
        System.out.println("│ ID │ Tên                │ SĐT        │ Địa chỉ                 │ Ngày sinh  │");
        System.out.println("├────┼────────────────────┼────────────┼──────────────────────────┼────────────┤");
        boolean any = false;
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM khachhang ORDER BY MaKH")) {
            while (rs.next()) {
                any = true;
                System.out.printf("│ %-2d │ %-18s │ %-10s │ %-24s │ %-10s │\n",
                    rs.getInt("MaKH"), rs.getString("HoTen"), rs.getString("SDT"), rs.getString("DiaChi"),
                    rs.getString("NgaySinh") == null ? "" : rs.getString("NgaySinh"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (!any) System.out.println("│ Không có dữ liệu                                                    │");
        System.out.println("└────┴────────────────────┴────────────┴──────────────────────────┴────────────┘");
        ConsoleUI.printFooter();
    }

    
}
