package dao;
import java.sql.*;
import java.util.Scanner;

import database.DBUtil;
import view.ConsoleUI;

public class NhanVienDAO {
    public void them() {
        Scanner sc = new Scanner(System.in);
        String tenTK = promptNonEmpty(sc, "Tên tài khoản (0: hủy)"); if (tenTK == null) { System.out.println("Đã hủy."); return; }
        String mk = promptNonEmpty(sc, "Mật khẩu (0: hủy)"); if (mk == null) { System.out.println("Đã hủy."); return; }
        String sdt = promptNonEmpty(sc, "Số điện thoại (0: hủy)"); if (sdt == null) { System.out.println("Đã hủy."); return; }
        System.out.print("Ngày vào làm (yyyy-mm-dd, 0: hủy): ");
        String ngay = sc.nextLine(); if ("0".equals(ngay != null ? ngay.trim() : "")) { System.out.println("Đã hủy."); return; }
        String chucvu = promptChucVu(sc); if (chucvu == null) { System.out.println("Đã hủy."); return; }
        Integer luong = promptInt(sc, "Lương (0: hủy)", 0, Integer.MAX_VALUE, true); if (luong == null) { System.out.println("Đã hủy."); return; }
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO nhanvien (TaiKhoan, MatKhau, HoTen, SDT, NgayVaoLam, ChucVu, Luong) VALUES (?, ?, ?, ?, ?, ?, ?)")) {
            ps.setString(1, tenTK);
            ps.setString(2, mk);
            ps.setString(3, "Nhân viên"); // Tên mặc định
            ps.setString(4, sdt);
            ps.setTimestamp(5, java.sql.Timestamp.valueOf(ngay + " 10:00:00"));
            ps.setString(6, chucvu);
            ps.setInt(7, luong);
            ps.executeUpdate();
            System.out.println("Thêm nhân viên thành công!");
        } catch (SQLException e) {
            System.out.println("Lỗi: " + e.getMessage());
        }
    }

    public void sua() {
        Scanner sc = new Scanner(System.in);
        try (Connection conn = DBUtil.getConnection()) {
            Integer id;
            while (true) {
                id = promptInt(sc, "Nhập ID nhân viên cần sửa (0: hủy)", 1, Integer.MAX_VALUE, true);
                if (id == null) { System.out.println("Đã hủy."); return; }
                if (existsNV(conn, id)) break;
                System.out.println("Không tìm thấy nhân viên, vui lòng nhập lại.");
            }
            printNVById(conn, id);
            System.out.println("Chọn: 1. Sửa toàn bộ, 2. Mật khẩu, 3. SĐT, 4. Chức vụ, 5. Lương, 0. Hủy");
            Integer ch = promptInt(sc, "Chọn", 0, 5, false);
            if (ch == null || ch == 0) { System.out.println("Đã hủy."); return; }
            switch (ch) {
                case 1: {
                    String mk = promptNonEmpty(sc, "Nhập mật khẩu mới (0: hủy)"); if (mk == null) return;
                    String sdt = promptNonEmpty(sc, "Nhập số điện thoại mới (0: hủy)"); if (sdt == null) return;
                    String chucvu = promptChucVu(sc); if (chucvu == null) return;
                    Integer luong = promptInt(sc, "Nhập lương mới (0: hủy)", 0, Integer.MAX_VALUE, true); if (luong == null) return;
                    try (PreparedStatement ps = conn.prepareStatement("UPDATE nhanvien SET MatKhau=?, SDT=?, ChucVu=?, Luong=? WHERE MaNV=?")) {
                        ps.setString(1, mk);
                        ps.setString(2, sdt);
                        ps.setString(3, chucvu);
                        ps.setInt(4, luong);
                        ps.setInt(5, id);
                        ps.executeUpdate();
                    }
                    break;
                }
                case 2: {
                    String mk = promptNonEmpty(sc, "Nhập mật khẩu mới (0: hủy)"); if (mk == null) return;
                    updateOne(conn, "MatKhau", mk, id);
                    break;
                }
                case 3: {
                    String sdt = promptNonEmpty(sc, "Nhập số điện thoại mới (0: hủy)"); if (sdt == null) return;
                    updateOne(conn, "SDT", sdt, id);
                    break;
                }
                case 4: {
                    String chucvu = promptChucVu(sc); if (chucvu == null) return;
                    updateOne(conn, "ChucVu", chucvu, id);
                    break;
                }
                case 5: {
                    Integer luong = promptInt(sc, "Nhập lương mới (0: hủy)", 0, Integer.MAX_VALUE, true); if (luong == null) return;
                    try (PreparedStatement ps = conn.prepareStatement("UPDATE nhanvien SET Luong=? WHERE MaNV=?")) {
                        ps.setInt(1, luong);
                        ps.setInt(2, id);
                        ps.executeUpdate();
                    }
                    break;
                }
            }
            System.out.println("Sửa thành công!");
            printNVById(conn, id);
        } catch (SQLException e) {
            System.out.println("Lỗi: " + e.getMessage());
        }
    }

    public void xoa() {
        Scanner sc = new Scanner(System.in);
        try (Connection conn = DBUtil.getConnection()) {
            Integer id;
            while (true) {
                id = promptInt(sc, "Nhập ID nhân viên cần xóa (0: hủy)", 1, Integer.MAX_VALUE, true);
                if (id == null) { System.out.println("Đã hủy."); return; }
                if (existsNV(conn, id)) break;
                System.out.println("Không tìm thấy nhân viên, vui lòng nhập lại.");
            }
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM nhanvien WHERE MaNV=?")) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
            System.out.println("Xóa thành công!");
        } catch (SQLException e) {
            System.out.println("Lỗi: " + e.getMessage());
        }
    }

    public void xuat() {
        ConsoleUI.printHeader("DANH SÁCH NHÂN VIÊN");
        System.out.println("┌────┬────────────┬──────────┬──────────────┬────────────┬──────────┬──────────────┐");
        System.out.println("│ ID │ Tài khoản  │ Mật khẩu │ SĐT          │ Ngày vào   │ Chức vụ  │ Lương        │");
        System.out.println("├────┼────────────┼──────────┼──────────────┼────────────┼──────────┼──────────────┤");
        boolean any = false;
        try (Connection conn = DBUtil.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM nhanvien ORDER BY MaNV")) {
            while (rs.next()) {
                any = true;
                System.out.printf("│ %-2d │ %-10s │ %-8s │ %-12s │ %-10s │ %-8s │ %-12d │\n",
                    rs.getInt("MaNV"),
                    rs.getString("TaiKhoan"),
                    rs.getString("MatKhau"),
                    rs.getString("SDT"),
                    rs.getString("NgayVaoLam"),
                    rs.getString("ChucVu"),
                    rs.getInt("Luong")
                );
            }
        } catch (SQLException e) {
            System.out.println("Lỗi: " + e.getMessage());
        }
        if (!any) System.out.println("│ Không có dữ liệu                                                              │");
        System.out.println("└────┴────────────┴──────────┴──────────────┴────────────┴──────────┴──────────────┘");
        ConsoleUI.printFooter();
    }

    public void timkiem() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Tìm theo: 1.ID  2.Tài khoản/SĐT");
        System.out.print("Chọn: ");
        String choiceStr = sc.nextLine();
        int choice;
        try { choice = Integer.parseInt(choiceStr.trim()); } catch (NumberFormatException e) { System.out.println("Vui lòng nhập số hợp lệ."); return; }
        String sql;
        boolean byId = choice == 1;
    
        try (Connection conn = DBUtil.getConnection()) {
            if (byId) {
                System.out.print("Nhập ID: ");
                String idStr = sc.nextLine();
                int id; 
                try { 
                    id = Integer.parseInt(idStr.trim()); 
                } 
                catch (NumberFormatException e) { 
                    System.out.println("ID không hợp lệ."); 
                    return; 
                }
              
                sql = "SELECT * FROM nhanvien WHERE MaNV = ?";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, id);
                    try (ResultSet rs = ps.executeQuery()) { printNhanVienTable(rs); }
                }
            } else {
                System.out.print("Nhập từ khóa: ");
                String key = sc.nextLine();
                sql = "SELECT * FROM nhanvien WHERE TaiKhoan LIKE ? OR SDT LIKE ?";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, "%" + key + "%");
                    ps.setString(2, "%" + key + "%");
                    try (ResultSet rs = ps.executeQuery()) { printNhanVienTable(rs); }
                }
            }
        } catch (SQLException e) {
            System.out.println("Lỗi: " + e.getMessage());
        }
        
        System.out.println("╚════╩════════════╩══════════╩══════════════╩════════════╩══════════╩══════════════╝");
    }

    private void printNhanVienTable(ResultSet rs) throws SQLException {
        System.out.println("\n╔════╦════════════╦══════════╦══════════════╦════════════╦══════════╦══════════════╗");
        System.out.println("║ ID ║ Tài khoản  ║ Mật khẩu ║  SĐT        ║ Ngày vào   ║ Chức vụ  ║    Lương     ║");
        System.out.println("╠════╬════════════╬══════════╬══════════════╬════════════╬══════════╬══════════════╣");
        boolean any = false;
        while (rs.next()) {
            any = true;
            System.out.printf("║ %-2d ║ %-10s ║ %-8s ║ %-12s ║ %-10s ║ %-8s ║ %-12d ║\n",
                rs.getInt("MaNV"),
                rs.getString("TaiKhoan"),
                rs.getString("MatKhau"),
                rs.getString("SDT"),
                rs.getString("NgayVaoLam"),
                rs.getString("ChucVu"),
                rs.getInt("Luong")
            );
        }
        if (!any) System.out.println("║ Không có kết quả                                                             ║");
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

    private static boolean existsNV(Connection conn, int id) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM nhanvien WHERE MaNV=?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }

    private static void updateOne(Connection conn, String column, String value, int id) throws SQLException {
        String sql = "UPDATE nhanvien SET " + column + "=? WHERE MaNV=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, value);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    private void printNVById(Connection conn, int id) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM nhanvien WHERE MaNV=?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) { printNhanVienTable(rs); }
        }
    }

    private static String promptChucVu(Scanner sc) {
        while (true) {
            System.out.print("Chức vụ (1: quanly, 2: nhanvien, 0: hủy): ");
            String s = sc.nextLine();
            if (s == null) continue;
            s = s.trim().toLowerCase();
            if (s.equals("0")) return null;
            if (s.equals("1") || s.equals("quanly")) return "quanly";
            if (s.equals("2") || s.equals("nhanvien")) return "nhanvien";
            System.out.println("Chỉ nhập 1 hoặc 2 (quanly/nhanvien).");
        }
    }
}