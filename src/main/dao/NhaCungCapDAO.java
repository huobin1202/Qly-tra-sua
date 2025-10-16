package dao;
import java.util.Scanner;

import database.DBUtil;
import view.ConsoleUI;
import dto.IQuanLy;

import java.sql.*; // Thêm import này
public class NhaCungCapDAO implements IQuanLy {
    // Thêm khách hàng vào database
    public void them() {
        Scanner rd = new Scanner(System.in);
        String ten = promptNonEmpty(rd, "Nhập tên nhà cung cấp (0: hủy)"); if (ten == null) { System.out.println("Đã hủy."); return; }
        String sdt = promptNonEmpty(rd, "Nhập số điện thoại (0: hủy)"); if (sdt == null) { System.out.println("Đã hủy."); return; }
        String diachi = promptNonEmpty(rd, "Nhập địa chỉ (0: hủy)"); if (diachi == null) { System.out.println("Đã hủy."); return; }
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO nhacungcap (TenNCC, SDT, DiaChi) VALUES (?, ?, ?)")) {
            ps.setString(1, ten);
            ps.setString(2, sdt);
            ps.setString(3, diachi);
            ps.executeUpdate();
            System.out.println("Đã thêm nhà cung cấp vào database.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Sửa thông tin khách hàng trong database
    public void sua() {
        Scanner rd = new Scanner(System.in);
        try (Connection conn = DBUtil.getConnection()) {
            Integer id = promptId(rd, "Nhập ID nhà cung cấp cần sửa (0: hủy)");
            if (id == null) { System.out.println("Đã hủy."); return; }
            while (!existsNCC(conn, id)) {
                System.out.println("Không tìm thấy nhà cung cấp, vui lòng nhập lại.");
                id = promptId(rd, "Nhập ID nhà cung cấp cần sửa (0: hủy)");
                if (id == null) { System.out.println("Đã hủy."); return; }
            }
            printNCCById(conn, id);
            System.out.println("Chọn: 1. Sửa toàn bộ, 2. Tên, 3. SĐT, 4. Địa chỉ, 0. Hủy");
            Integer ch = promptInt(rd, "Chọn", 0, 4, false);
            if (ch == null || ch == 0) { System.out.println("Đã hủy."); return; }
            switch (ch) {
                case 1: {
                    String ten = promptNonEmpty(rd, "Nhập tên nhà cung cấp mới (0: hủy)"); if (ten == null) return;
                    String sdt = promptNonEmpty(rd, "Nhập số điện thoại mới (0: hủy)"); if (sdt == null) return;
                    String diachi = promptNonEmpty(rd, "Nhập địa chỉ mới (0: hủy)"); if (diachi == null) return;
                    try (PreparedStatement ps = conn.prepareStatement("UPDATE nhacungcap SET TenNCC=?, SDT=?, DiaChi=? WHERE MaNCC=?")) {
                        ps.setString(1, ten);
                        ps.setString(2, sdt);
                        ps.setString(3, diachi);
                        ps.setInt(4, id);
                        ps.executeUpdate();
                    }
                    break;
                }
                case 2: {
                    String ten = promptNonEmpty(rd, "Nhập tên nhà cung cấp mới (0: hủy)"); if (ten == null) return;
                    updateOne(conn, "TenNCC", ten, id);
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
            }
            System.out.println("Đã sửa thông tin nhà cung cấp.");
            printNCCById(conn, id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Xóa khách hàng khỏi database
    public void xoa() {
        Scanner rd = new Scanner(System.in);
        try (Connection conn = DBUtil.getConnection()) {
            Integer id = promptId(rd, "Nhập ID nhà cung cấp cần xóa (0: hủy)");
            if (id == null) { System.out.println("Đã hủy."); return; }
            while (!existsNCC(conn, id)) {
                System.out.println("Không tìm thấy nhà cung cấp, vui lòng nhập lại.");
                id = promptId(rd, "Nhập ID nhà cung cấp cần xóa (0: hủy)");
                if (id == null) { System.out.println("Đã hủy."); return; }
            }
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM nhacungcap WHERE MaNCC=?")) {
                ps.setInt(1, id);
                ps.executeUpdate();
                System.out.println("Đã xóa nhà cung cấp.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Tìm kiếm khách hàng trong database
    public void timkiem() {
        Scanner rd = new Scanner(System.in);
        System.out.println("Tìm theo: 1.ID  2.Tên");
        System.out.print("Chọn: ");
        String chStr = rd.nextLine();
        int ch; try { ch = Integer.parseInt(chStr.trim()); } catch (NumberFormatException e) { System.out.println("Vui lòng nhập số hợp lệ."); return; }
        try (Connection conn = DBUtil.getConnection()) {
            if (ch == 1) {
                System.out.print("Nhập ID: ");
                String idStr = rd.nextLine();
                int id; try { id = Integer.parseInt(idStr.trim()); } catch (NumberFormatException e) { System.out.println("ID không hợp lệ."); return; }
                try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM nhacungcap WHERE MaNCC=?")) {
                    ps.setInt(1, id);
                    try (ResultSet rs = ps.executeQuery()) { printNCC(rs); }
                }
            } else {
                System.out.print("Nhập tên: ");
                String ten = rd.nextLine();
                try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM nhacungcap WHERE TenNCC LIKE ?")) {
                    ps.setString(1, "%" + ten + "%");
                    try (ResultSet rs = ps.executeQuery()) { printNCC(rs); }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void nhap() {
    }
    public void xuat() {
        ConsoleUI.printHeader("DANH SÁCH NHÀ CUNG CẤP");
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM nhacungcap ORDER BY MaNCC")) {
            printNCC(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ConsoleUI.printFooter();
    }

    private void printNCC(ResultSet rs) throws SQLException {
        System.out.println("┌────┬────────────────────┬──────────────┬──────────────────────────┐");
        System.out.println("│ ID │ Tên                │ SĐT          │ Địa chỉ                 │");
        System.out.println("├────┼────────────────────┼────────────┼──────────────────────────┤");
        boolean any = false;
        while (rs.next()) {
            any = true;
            System.out.printf("│ %-2d │ %-18s │ %-10s │ %-24s │\n",
                rs.getInt("MaNCC"), rs.getString("TenNCC"), rs.getString("SDT"), rs.getString("DiaChi"));
        }
        if (!any) System.out.println("│ Không có dữ liệu                                        │");
        System.out.println("└────┴────────────────────┴────────────┴──────────────────────────┘");
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

    private static boolean existsNCC(Connection conn, int id) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM nhacungcap WHERE MaNCC=?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }

    private static void updateOne(Connection conn, String column, String value, int id) throws SQLException {
        String sql = "UPDATE nhacungcap SET " + column + "=? WHERE MaNCC=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, value);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    private void printNCCById(Connection conn, int id) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM nhacungcap WHERE MaNCC=?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) { printNCC(rs); }
        }
    }
}
