package dao;
import java.util.Scanner;

import db.DBUtil;
import view.ConsoleUI;
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
            ps.setString(3, diachi);
            ps.executeUpdate();
            System.out.println("Đã thêm nhà cung cấp vào database.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Sửa thông tin khách hàng trong database
    public void sua() {
        Scanner rd = new Scanner(System.in);
        System.out.print("Nhập ID nhà cung cấp: ");
        String idStr = rd.nextLine();
        int id; try { id = Integer.parseInt(idStr.trim()); } catch (NumberFormatException e) { System.out.println("ID không hợp lệ."); return; }
        System.out.print("Nhập số điện thoại mới: ");
        String sdt = rd.nextLine();
        System.out.print("Nhập địa chỉ mới: ");
        String diachi = rd.nextLine();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                "UPDATE nhacungcap SET sdt=?, diachi=? WHERE id=?" )) {
            ps.setString(1, sdt);
            ps.setString(2, diachi);
            ps.setInt(3, id);
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
        System.out.print("Nhập ID nhà cung cấp cần xóa: ");
        String idStr = rd.nextLine();
        int id; try { id = Integer.parseInt(idStr.trim()); } catch (NumberFormatException e) { System.out.println("ID không hợp lệ."); return; }
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM nhacungcap WHERE id=?")) {
            ps.setInt(1, id);
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
        System.out.println("Tìm theo: 1.ID  2.Tên");
        System.out.print("Chọn: ");
        String chStr = rd.nextLine();
        int ch; try { ch = Integer.parseInt(chStr.trim()); } catch (NumberFormatException e) { System.out.println("Vui lòng nhập số hợp lệ."); return; }
        try (Connection conn = DBUtil.getConnection()) {
            if (ch == 1) {
                System.out.print("Nhập ID: ");
                String idStr = rd.nextLine();
                int id; try { id = Integer.parseInt(idStr.trim()); } catch (NumberFormatException e) { System.out.println("ID không hợp lệ."); return; }
                try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM nhacungcap WHERE id=?")) {
                    ps.setInt(1, id);
                    try (ResultSet rs = ps.executeQuery()) { printNCC(rs); }
                }
            } else {
                System.out.print("Nhập tên: ");
                String ten = rd.nextLine();
                try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM nhacungcap WHERE ten LIKE ?")) {
                    ps.setString(1, "%" + ten + "%");
                    try (ResultSet rs = ps.executeQuery()) { printNCC(rs); }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void nhap() {
    }
    public void xuat() {
        ConsoleUI.printHeader("DANH SÁCH NHÀ CUNG CẤP");
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM nhacungcap ORDER BY id")) {
            printNCC(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ConsoleUI.printFooter();
    }

    private void printNCC(ResultSet rs) throws SQLException {
        System.out.println("┌────┬────────────────────┬────────────┬──────────────────────────┐");
        System.out.println("│ ID │ Tên                │ SĐT        │ Địa chỉ                 │");
        System.out.println("├────┼────────────────────┼────────────┼──────────────────────────┤");
        boolean any = false;
        while (rs.next()) {
            any = true;
            System.out.printf("│ %-2d │ %-18s │ %-10s │ %-24s │\n",
                rs.getInt("id"), rs.getString("ten"), rs.getString("sdt"), rs.getString("diachi"));
        }
        if (!any) System.out.println("│ Không có dữ liệu                                        │");
        System.out.println("└────┴────────────────────┴────────────┴──────────────────────────┘");
    }
}
