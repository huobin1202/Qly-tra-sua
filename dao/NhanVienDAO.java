package dao;
import java.sql.*;
import java.time.LocalDate;
import java.util.Scanner;

import db.DBUtil;
import view.ConsoleUI;

public class NhanVienDAO {
    public void them() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Tên tài khoản: ");
        String tenTK = sc.nextLine();
        System.out.print("Mật khẩu: ");
        String mk = sc.nextLine();
        System.out.print("Số điện thoại: ");
        String sdt = sc.nextLine();
        System.out.print("Ngày vào làm (yyyy-mm-dd): ");
        String ngay = sc.nextLine();
        System.out.print("Chức vụ (quanly/nhanvien): ");
        String chucvu = sc.nextLine();
        System.out.print("Lương: ");
        double luong = sc.nextDouble();
        sc.nextLine();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO nhanvien (tentaikhoan, matkhau, sdt, ngayvaolam, chucvu, luong) VALUES (?, ?, ?, ?, ?, ?)")) {
            ps.setString(1, tenTK);
            ps.setString(2, mk);
            ps.setString(3, sdt);
            ps.setString(4, ngay);
            ps.setString(5, chucvu);
            ps.setDouble(6, luong);
            ps.executeUpdate();
            System.out.println("Thêm nhân viên thành công!");
        } catch (SQLException e) {
            System.out.println("Lỗi: " + e.getMessage());
        }
    }

    public void sua() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Nhập ID nhân viên cần sửa: ");
        int id = sc.nextInt(); sc.nextLine();
        System.out.print("Nhập mật khẩu mới: ");
        String mk = sc.nextLine();
        System.out.print("Nhập số điện thoại mới: ");
        String sdt = sc.nextLine();
        System.out.print("Nhập chức vụ mới: ");
        String chucvu = sc.nextLine();
        System.out.print("Nhập lương mới: ");
        double luong = sc.nextDouble(); sc.nextLine();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                "UPDATE nhanvien SET matkhau=?, sdt=?, chucvu=?, luong=? WHERE id=?")) {
            ps.setString(1, mk);
            ps.setString(2, sdt);
            ps.setString(3, chucvu);
            ps.setDouble(4, luong);
            ps.setInt(5, id);
            int rows = ps.executeUpdate();
            if (rows > 0) System.out.println("Sửa thành công!");
            else System.out.println("Không tìm thấy nhân viên.");
        } catch (SQLException e) {
            System.out.println("Lỗi: " + e.getMessage());
        }
    }

    public void xoa() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Nhập ID nhân viên cần xóa: ");
        int id = sc.nextInt(); sc.nextLine();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM nhanvien WHERE id=?")) {
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            if (rows > 0) System.out.println("Xóa thành công!");
            else System.out.println("Không tìm thấy nhân viên.");
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
             ResultSet rs = st.executeQuery("SELECT * FROM nhanvien ORDER BY id")) {
            while (rs.next()) {
                any = true;
                System.out.printf("│ %-2d │ %-10s │ %-8s │ %-12s │ %-10s │ %-8s │ %-12.0f │\n",
                    rs.getInt("id"),
                    rs.getString("tentaikhoan"),
                    rs.getString("matkhau"),
                    rs.getString("sdt"),
                    rs.getString("ngayvaolam"),
                    rs.getString("chucvu"),
                    rs.getDouble("luong")
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
        System.out.println("\n╔════╦════════════╦══════════╦══════════════╦════════════╦══════════╦══════════════╗");
        System.out.println("║ ID ║ Tài khoản  ║ Mật khẩu ║  SĐT        ║ Ngày vào   ║ Chức vụ  ║    Lương     ║");
        System.out.println("╠════╬════════════╬══════════╬══════════════╬════════════╬══════════╬══════════════╣");
        try (Connection conn = DBUtil.getConnection()) {
            if (byId) {
                System.out.print("Nhập ID: ");
                String idStr = sc.nextLine();
                int id; try { id = Integer.parseInt(idStr.trim()); } catch (NumberFormatException e) { System.out.println("ID không hợp lệ."); return; }
                sql = "SELECT * FROM nhanvien WHERE id = ?";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, id);
                    try (ResultSet rs = ps.executeQuery()) { printNhanVienTable(rs); }
                }
            } else {
                System.out.print("Nhập từ khóa: ");
                String key = sc.nextLine();
                sql = "SELECT * FROM nhanvien WHERE tentaikhoan LIKE ? OR sdt LIKE ?";
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
        boolean any = false;
        while (rs.next()) {
            any = true;
            System.out.printf("║ %-2d ║ %-10s ║ %-8s ║ %-12s ║ %-10s ║ %-8s ║ %-12.0f ║\n",
                rs.getInt("id"),
                rs.getString("tentaikhoan"),
                rs.getString("matkhau"),
                rs.getString("sdt"),
                rs.getString("ngayvaolam"),
                rs.getString("chucvu"),
                rs.getDouble("luong")
            );
        }
        if (!any) System.out.println("║ Không có kết quả                                                             ║");
    }
}