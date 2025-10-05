package dao;
import java.util.Scanner;

import db.DBUtil;
import dto.IQuanLy;

import java.sql.*; // Thêm import này
public class NhaCungCapDAO implements IQuanLy {
    // Thêm khách hàng vào database
    public void them() {
        Scanner rd = new Scanner(System.in);
        System.out.print("Nhập tên nha cung cap: ");
        String ten = rd.nextLine();
        System.out.print("Nhập số điện thoại: ");
        String sdt = rd.nextLine();
        System.out.print("Nhập địa chỉ: ");
        String diachi = rd.nextLine(); 
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO nhacungcap (ten, sdt, diachi) VALUES (?, ?, ?)")) {
            ps.setString(1, ten);
            ps.setString(2, sdt);
            ps.setString(3, diachi); // Giả sử địa chỉ rỗng
            ps.executeUpdate();
            System.out.println("Đã thêm nhà cung cấp vào database.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Sửa thông tin khách hàng trong database
    public void sua() {
        Scanner rd = new Scanner(System.in);
        System.out.print("Nhập tên nhà cung cấp: ");
        String ten = rd.nextLine();
        System.out.print("Nhập số điện thoại mới: ");
        String sdt = rd.nextLine();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                "UPDATE nhacungcap SET sdt=? WHERE ten=? AND diachi=?" )) {
            ps.setString(1, sdt);
            ps.setString(2, ten);
            ps.setString(3, ""); // Giả sử địa chỉ rỗng
            int rows = ps.executeUpdate();
            if (rows > 0)
                System.out.println("Da sua thong tin nhà cung cấp.");
            else
                System.out.println("Không tìm thấy nhà cung cấp để sửa.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Xóa khách hàng khỏi database
    public void xoa() {
        Scanner rd = new Scanner(System.in);
        System.out.print("Nhập tên nhà cung cấp cần xóa: ");
        String ten = rd.nextLine();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM nhacungcap WHERE ten=?")) {
            ps.setString(1, ten);
            int rows = ps.executeUpdate();
            if (rows > 0)
                System.out.println("Đã xóa nhà cung cấp.");
            else
                System.out.println("Không tìm thấy nhà cung cấp để xóa.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Tìm kiếm khách hàng trong database
    public void timkiem() {
        Scanner rd = new Scanner(System.in);
        System.out.print("Nhập tên nhà cung cấp cần tìm: ");
        String ten = rd.nextLine();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM nhacungcap WHERE ten LIKE ?")) {
            ps.setString(1, "%" + ten + "%");
            ResultSet rs = ps.executeQuery();
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.println("Tên: " + rs.getString("ten") +
                                   ", SĐT: " + rs.getString("sdt"));
            }
            if (!found) System.out.println("Không tìm thấy nhà cung cấp.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void nhap() {
    }
    public void xuat() {
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM nhacungcap")) {
            while (rs.next()) {
                System.out.println("Tên: " + rs.getString("ten") +
                                   ", SĐT: " + rs.getString("sdt")+
                                   ", Địa chỉ: " + rs.getString("diachi") 
                                   );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    
}
