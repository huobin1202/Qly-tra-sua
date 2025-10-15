package dao;
import java.sql.*;
import java.util.Scanner;

import datebase.DBUtil;
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
                "INSERT INTO nhanvien (TaiKhoan, MatKhau, HoTen, SDT, NgayVaoLam, ChucVu, Luong) VALUES (?, ?, ?, ?, ?, ?, ?)")) {
            ps.setString(1, tenTK);
            ps.setString(2, mk);
            ps.setString(3, "Nhân viên"); // Tên mặc định
            ps.setString(4, sdt);
            ps.setTimestamp(5, java.sql.Timestamp.valueOf(ngay + " 10:00:00"));
            ps.setString(6, chucvu);
            ps.setInt(7, (int)luong);
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
                "UPDATE nhanvien SET MatKhau=?, SDT=?, ChucVu=?, Luong=? WHERE MaNV=?")) {
            ps.setString(1, mk);
            ps.setString(2, sdt);
            ps.setString(3, chucvu);
            ps.setInt(4, (int)luong);
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
             PreparedStatement ps = conn.prepareStatement("DELETE FROM nhanvien WHERE MaNV=?")) {
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
}