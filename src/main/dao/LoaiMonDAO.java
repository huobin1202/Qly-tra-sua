package dao;
import java.sql.*;
import java.util.Scanner;

import datebase.DBUtil;
import view.ConsoleUI;

public class LoaiMonDAO {
    public void them() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Nhập tên loại món mới: ");
        String ten = sc.nextLine();
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
        System.out.print("Nhập mã loại món cần sửa: ");
        int ma = sc.nextInt();
        sc.nextLine();
        System.out.print("Nhập tên loại món mới: ");
        String ten = sc.nextLine();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE loaimon SET TenLoai=? WHERE MaLoai=?")) {
            ps.setString(1, ten);
            ps.setInt(2, ma);
            int rows = ps.executeUpdate();
            if (rows > 0)
                System.out.println("Đã sửa loại món.");
            else
                System.out.println("Không tìm thấy loại món.");
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
}