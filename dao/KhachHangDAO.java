package dao;
import java.util.Scanner;

import db.DBUtil;
import view.ConsoleUI;
import dto.IQuanLy;

import java.sql.*; // Thêm import này
public class KhachHangDAO implements IQuanLy {
    // Thêm khách hàng vào database
    public void them() {
        Scanner rd = new Scanner(System.in);
        System.out.print("Nhập tên khách hàng: ");
        String ten = rd.nextLine();
        System.out.print("Nhập số điện thoại: ");
        String sdt = rd.nextLine();
        System.out.print("Nhập địa chỉ: ");
        String diachi = rd.nextLine(); 
        System.out.print("Nhập ngày sinh (yyyy-mm-dd, để trống nếu không có): ");
        String ngaysinhStr = rd.nextLine();

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO khachhang (HoTen, SDT, DiaChi, NgaySinh) VALUES (?, ?, ?, ?)")) {
            ps.setString(1, ten);
            ps.setString(2, sdt);
            ps.setString(3, diachi);
            if (ngaysinhStr == null || ngaysinhStr.trim().isEmpty()) ps.setNull(4, java.sql.Types.TIMESTAMP);
            else ps.setTimestamp(4, java.sql.Timestamp.valueOf(ngaysinhStr.trim() + " 10:00:00"));
            ps.executeUpdate();
            System.out.println("Đã thêm khách hàng vào database.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Sửa thông tin khách hàng trong database
    public void sua() {
        Scanner rd = new Scanner(System.in);
        System.out.print("Nhập ID khách hàng cần sửa: ");
        String idStr = rd.nextLine();
        int id;
        try { id = Integer.parseInt(idStr.trim()); } catch (NumberFormatException e) { System.out.println("ID không hợp lệ."); 
        return; }
        System.out.print("Nhập tên khách hàng mới: ");
        String ten = rd.nextLine();
        System.out.print("Nhập số điện thoại mới: ");
        String sdt = rd.nextLine();
        System.out.print("Nhập địa chỉ mới: ");
        String diachi = rd.nextLine();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                "UPDATE khachhang SET  HoTen=?,SDT=?, DiaChi=? WHERE MaKH=?" )) {
      ps.setString(1, ten);
            ps.setString(2, sdt);
            ps.setString(3, diachi);
            ps.setInt(4, id);
            int rows = ps.executeUpdate();
            if (rows > 0)
                System.out.println("Đã sửa thông tin khách hàng.");
            else
                System.out.println("Không tìm thấy khách hàng để sửa.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Xóa khách hàng khỏi database
    public void xoa() {
        Scanner rd = new Scanner(System.in);
        System.out.print("Nhập ID khách hàng cần xóa: ");
        String idStr = rd.nextLine();
        int id; try { id = Integer.parseInt(idStr.trim()); } catch (NumberFormatException e) { System.out.println("ID không hợp lệ."); return; }
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM khachhang WHERE MaKH=?")) {
            ps.setInt(1, id);
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
        System.out.println("Tìm theo: 1.ID  2.Tên");
        System.out.print("Chọn: ");
        String chStr = rd.nextLine();
        int ch; try { ch = Integer.parseInt(chStr.trim()); } catch (NumberFormatException e) { System.out.println("Vui lòng nhập số hợp lệ."); return; }
        String sql;
        try (Connection conn = DBUtil.getConnection()) {
            if (ch == 1) {
                System.out.print("Nhập ID: ");
                String idStr = rd.nextLine();
                int id; try { id = Integer.parseInt(idStr.trim()); } catch (NumberFormatException e) { System.out.println("ID không hợp lệ."); return; }
                sql = "SELECT * FROM khachhang WHERE MaKH = ?";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, id);
                    try (ResultSet rs = ps.executeQuery()) { printCustomerTable(rs); }
                }
            } else {
                System.out.print("Nhập tên: ");
                String ten = rd.nextLine();
                sql = "SELECT * FROM khachhang WHERE HoTen LIKE ?";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, "%" + ten + "%");
                    try (ResultSet rs = ps.executeQuery()) { printCustomerTable(rs); }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void printCustomerTable(ResultSet rs) throws SQLException {
        System.out.println("\n╔════╦════════════════════╦════════════╦════════════════════════════╦════════════╗");
        System.out.println("║ ID ║ Tên               ║ SĐT        ║ Địa chỉ                   ║ Ngày sinh  ║");
        System.out.println("╠════╬════════════════════╬════════════╬════════════════════════════╬════════════╣");
        boolean any = false;
        while (rs.next()) {
            any = true;
            System.out.printf("║ %-2d ║ %-18s ║ %-10s ║ %-26s ║ %-10s ║\n",
                rs.getInt("MaKH"), rs.getString("HoTen"), rs.getString("SDT"), rs.getString("DiaChi"),
                rs.getString("NgaySinh") == null ? "" : rs.getString("NgaySinh"));
        }
        if (!any) System.out.println("║ Không có kết quả                                                       ║");
        System.out.println("╚════╩════════════════════╩════════════╩════════════════════════════╩════════════╝");
    }
    @Override
    public void nhap() {
    }
    public void xuat() {
        ConsoleUI.printHeader("DANH SÁCH KHÁCH HÀNG");
        System.out.println("┌────┬────────────────────┬────────────┬──────────────────────────┬────────────┐");
        System.out.println("│ ID │ Tên                │ SĐT        │ Địa chỉ                 │ Ngày sinh  │");
        System.out.println("├────┼────────────────────┼────────────┼──────────────────────────┼────────────┤");
        boolean any = false;
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM khachhang ORDER BY MaKH")) {
            while (rs.next()) {
                any = true;
                System.out.printf("│ %-2d │ %-18s │ %-10s │ %-24s │ %-10s │\n",
                    rs.getInt("MaKH"), rs.getString("HoTen"), rs.getString("SDT"), rs.getString("DiaChi"),
                    rs.getString("NgaySinh") == null ? "" : rs.getString("NgaySinh"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (!any) System.out.println("│ Không có dữ liệu                                                    │");
        System.out.println("└────┴────────────────────┴────────────┴──────────────────────────┴────────────┘");
        ConsoleUI.printFooter();
    }

    
}
