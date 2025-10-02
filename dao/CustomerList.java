package dao;
import java.util.Scanner;

import db.DBUtil;
import dto.IQuanLy;

import java.sql.*; // Thêm import này
public class CustomerList implements IQuanLy {
    // Thêm khách hàng vào database
    public void them() {
        Scanner rd = new Scanner(System.in);
        System.out.print("Nhập tên khách hàng: ");
        String ten = rd.nextLine();
        System.out.print("Nhập số điện thoại: ");
        String sdt = rd.nextLine();
        System.out.print("Nhập địa chỉ: ");
        String diachi = rd.nextLine(); 
        System.out.print("Nhập ngày sinh (YYYY/MM/DD): ");
        Date ngaysinh = rd.nextLine() == null ? null : Date.valueOf(rd.nextLine());

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO customers (ten, sdt, diachi, ngaysinh) VALUES (?, ?, ?, ?)")) {
            ps.setString(1, ten);
            ps.setString(2, sdt);
            ps.setString(3, diachi); // Giả sử địa chỉ rỗng
            ps.setDate(4, null); // Giả sử ngày sinh null
            ps.executeUpdate();
            System.out.println("Đã thêm khách hàng vào database.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Sửa thông tin khách hàng trong database
    public void sua() {
        Scanner rd = new Scanner(System.in);
        System.out.print("Nhập tên khách hàng cần sửa: ");
        String ten = rd.nextLine();
        System.out.print("Nhập số điện thoại mới: ");
        String sdt = rd.nextLine();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                "UPDATE customers SET sdt=? WHERE ten=? AND diachi=?" )) {
            ps.setString(1, sdt);
            ps.setString(2, ten);
            ps.setString(3, ""); // Giả sử địa chỉ rỗng
            ps.setDate(4, null); // Giả sử ngày sinh null
            int rows = ps.executeUpdate();
            if (rows > 0)
                System.out.println("Da sua thong tin khách hàng.");
            else
                System.out.println("Không tìm thay khách hàng de sua.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Xóa khách hàng khỏi database
    public void xoa() {
        Scanner rd = new Scanner(System.in);
        System.out.print("Nhập tên khách hàng cần xóa: ");
        String ten = rd.nextLine();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM customers WHERE ten=?")) {
            ps.setString(1, ten);
            int rows = ps.executeUpdate();
            if (rows > 0)
                System.out.println("Đã xóa khách hàng.");
            else
                System.out.println("Không tìm thấy khách hàng để xóa.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Tìm kiếm khách hàng trong database
    public void timkiem() {
        Scanner rd = new Scanner(System.in);
        System.out.print("Nhập tên khách hàng cần tìm: ");
        String ten = rd.nextLine();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM customers WHERE ten LIKE ?")) {
            ps.setString(1, "%" + ten + "%");
            ResultSet rs = ps.executeQuery();
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.println("Tên: " + rs.getString("ten") +
                                   ", SĐT: " + rs.getString("sdt"));
            }
            if (!found) System.out.println("Không tìm thấy khách hàng.");
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
             ResultSet rs = stmt.executeQuery("SELECT * FROM customers")) {
            while (rs.next()) {
                System.out.println("Tên: " + rs.getString("ten") +
                                   ", SĐT: " + rs.getString("sdt")+
                                   ", Địa chỉ: " + rs.getString("diachi") +
                                   ", Ngày sinh: " + rs.getDate("ngaysinh")
                                   );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    
}
