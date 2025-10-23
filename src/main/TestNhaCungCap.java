package main;

import java.sql.*;
import database.DBUtil;

public class TestNhaCungCap {
    public static void main(String[] args) {
        System.out.println("=== TEST NHÀ CUNG CẤP ===");
        
        // Test database connection
        try (Connection conn = DBUtil.getConnection()) {
            System.out.println("✅ Kết nối database thành công!");
            
            // Test insert
            System.out.println("\n1. Test thêm nhà cung cấp...");
            try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO nhacungcap (TenNCC, SDT, DiaChi) VALUES (?, ?, ?)")) {
                ps.setString(1, "Test NCC");
                ps.setString(2, "0123456789");
                ps.setString(3, "Test Address");
                int result = ps.executeUpdate();
                System.out.println("✅ Thêm thành công! Rows affected: " + result);
            } catch (SQLException e) {
                System.out.println("❌ Lỗi thêm: " + e.getMessage());
                e.printStackTrace();
            }
            
            // Test select
            System.out.println("\n2. Test lấy danh sách nhà cung cấp...");
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM nhacungcap ORDER BY MaNCC DESC LIMIT 5")) {
                
                System.out.println("Danh sách 5 nhà cung cấp mới nhất:");
                while (rs.next()) {
                    System.out.printf("ID: %d, Tên: %s, SĐT: %s, Địa chỉ: %s%n",
                        rs.getInt("MaNCC"),
                        rs.getString("TenNCC"),
                        rs.getString("SDT"),
                        rs.getString("DiaChi"));
                }
            } catch (SQLException e) {
                System.out.println("❌ Lỗi lấy danh sách: " + e.getMessage());
                e.printStackTrace();
            }
            
            // Test update
            System.out.println("\n3. Test cập nhật nhà cung cấp...");
            try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE nhacungcap SET TenNCC=?, SDT=?, DiaChi=? WHERE TenNCC=?")) {
                ps.setString(1, "Test NCC Updated");
                ps.setString(2, "0987654321");
                ps.setString(3, "Updated Address");
                ps.setString(4, "Test NCC");
                int result = ps.executeUpdate();
                System.out.println("✅ Cập nhật thành công! Rows affected: " + result);
            } catch (SQLException e) {
                System.out.println("❌ Lỗi cập nhật: " + e.getMessage());
                e.printStackTrace();
            }
            
            // Test delete
            System.out.println("\n4. Test xóa nhà cung cấp...");
            try (PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM nhacungcap WHERE TenNCC=?")) {
                ps.setString(1, "Test NCC Updated");
                int result = ps.executeUpdate();
                System.out.println("✅ Xóa thành công! Rows affected: " + result);
            } catch (SQLException e) {
                System.out.println("❌ Lỗi xóa: " + e.getMessage());
                e.printStackTrace();
            }
            
        } catch (SQLException e) {
            System.out.println("❌ Lỗi kết nối database: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("\n=== KẾT THÚC TEST ===");
    }
}
