package dao;
import java.sql.*;
import java.util.Scanner;

import db.DBUtil;
import view.ConsoleUI;

public class BanDAO {
    public void them() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Tên bàn: ");
        String tenBan = sc.nextLine();
        System.out.print("Trạng thái (Trống/Đang sử dụng): ");
        String trangThai = sc.nextLine();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO ban (TenBan, TrangThai) VALUES (?, ?)")) {
            ps.setString(1, tenBan);
            ps.setString(2, trangThai);
            ps.executeUpdate();
            System.out.println("Thêm bàn thành công!");
        } catch (SQLException e) {
            System.out.println("Lỗi: " + e.getMessage());
        }
    }

    public void sua() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Nhập ID bàn cần sửa: ");
        int id = sc.nextInt(); sc.nextLine();
        System.out.print("Tên bàn mới: ");
        String tenBan = sc.nextLine();
        System.out.print("Trạng thái mới: ");
        String trangThai = sc.nextLine();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                "UPDATE ban SET TenBan=?, TrangThai=? WHERE MaBan=?")) {
            ps.setString(1, tenBan);
            ps.setString(2, trangThai);
            ps.setInt(3, id);
            int rows = ps.executeUpdate();
            if (rows > 0) System.out.println("Sửa thành công!");
            else System.out.println("Không tìm thấy bàn.");
        } catch (SQLException e) {
            System.out.println("Lỗi: " + e.getMessage());
        }
    }

    public void xoa() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Nhập ID bàn cần xóa: ");
        int id = sc.nextInt(); sc.nextLine();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM ban WHERE MaBan=?")) {
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            if (rows > 0) System.out.println("Xóa thành công!");
            else System.out.println("Không tìm thấy bàn.");
        } catch (SQLException e) {
            System.out.println("Lỗi: " + e.getMessage());
        }
    }

    public void xuat() {
        ConsoleUI.printHeader("DANH SÁCH BÀN");
        System.out.println("┌────┬────────────┬────────────────────┐");
        System.out.println("│ ID │ Tên bàn    │ Trạng thái         │");
        System.out.println("├────┼────────────┼────────────────────┤");
        boolean any = false;
        try (Connection conn = DBUtil.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM ban ORDER BY MaBan")) {
            while (rs.next()) {
                any = true;
                System.out.printf("│ %-2d │ %-10s │ %-18s │\n",
                    rs.getInt("MaBan"),
                    rs.getString("TenBan"),
                    rs.getString("TrangThai")
                );
            }
        } catch (SQLException e) {
            System.out.println("Lỗi: " + e.getMessage());
        }
        if (!any) System.out.println("│ Không có dữ liệu            │");
        System.out.println("└────┴────────────┴────────────────────┘");
        ConsoleUI.printFooter();
    }
}