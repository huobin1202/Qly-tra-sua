package dao;
import java.sql.*;
import java.util.Scanner;

import db.DBUtil;

public class DSQuanLyBan {
    public void them() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Tên bàn: ");
        String tenBan = sc.nextLine();
        System.out.print("Trạng thái (Trống/Đang sử dụng): ");
        String trangThai = sc.nextLine();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO ban (tenban, trangthai) VALUES (?, ?)")) {
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
                "UPDATE ban SET tenban=?, trangthai=? WHERE id=?")) {
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
             PreparedStatement ps = conn.prepareStatement("DELETE FROM ban WHERE id=?")) {
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            if (rows > 0) System.out.println("Xóa thành công!");
            else System.out.println("Không tìm thấy bàn.");
        } catch (SQLException e) {
            System.out.println("Lỗi: " + e.getMessage());
        }
    }

    public void xuat() {
        System.out.println("\n╔════╦════════════╦════════════════════╗");
        System.out.println("║ ID ║  Tên bàn   ║    Trạng thái     ║");
        System.out.println("╠════╬════════════╬════════════════════╣");
        try (Connection conn = DBUtil.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM ban")) {
            while (rs.next()) {
                System.out.printf("║ %-2d ║ %-10s ║ %-16s ║\n",
                    rs.getInt("id"),
                    rs.getString("tenban"),
                    rs.getString("trangthai")
                );
            }
        } catch (SQLException e) {
            System.out.println("Lỗi: " + e.getMessage());
        }
        System.out.println("╚════╩════════════╩════════════════════╝");
    }
}