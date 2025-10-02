import java.sql.*;
import java.util.Scanner;

public class DSLoaiMon {
    public void them() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Nhập tên loại món mới: ");
        String ten = sc.nextLine();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO loaimon (ten) VALUES (?)")) {
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
             PreparedStatement ps = conn.prepareStatement("UPDATE loaimon SET ten=? WHERE ma=?")) {
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
             PreparedStatement ps = conn.prepareStatement("DELETE FROM loaimon WHERE ma=?")) {
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
        try (Connection conn = DBUtil.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM loaimon")) {
            System.out.println("Danh sách loại món:");
            while (rs.next()) {
                System.out.println(rs.getInt("ma") + ". " + rs.getString("ten"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}