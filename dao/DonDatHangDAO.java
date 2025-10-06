package dao;
import java.sql.*;
import java.util.Scanner;

import db.DBUtil;
import view.ConsoleUI;

public class DonDatHangDAO {
    public void them() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Người lập: ");
        String nguoiLap = sc.nextLine();
        System.out.print("ID bàn: ");
        int banId = sc.nextInt(); sc.nextLine();
        System.out.print("Trạng thái: ");
        String trangThai = sc.nextLine();
        System.out.print("Ngày lập (yyyy-mm-dd): ");
        String ngayLap = sc.nextLine();
        System.out.print("Ngày thanh toán (yyyy-mm-dd, có thể để trống): ");
        String ngayThanhToan = sc.nextLine();
        System.out.print("Đã thanh toán: ");
        double daThanhToan = sc.nextDouble();
        System.out.print("Tổng tiền: ");
        double tongTien = sc.nextDouble();
        sc.nextLine();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO dondathang (nguoilap, ban_id, trangthai, ngaylap, ngaythanhtoan, dathanhtoan, tongtien) VALUES (?, ?, ?, ?, ?, ?, ?)")) {
            ps.setString(1, nguoiLap);
            ps.setInt(2, banId);
            ps.setString(3, trangThai);
            ps.setString(4, ngayLap);
            if (ngayThanhToan.isEmpty()) ps.setNull(5, Types.DATE);
            else ps.setString(5, ngayThanhToan);
            ps.setDouble(6, daThanhToan);
            ps.setDouble(7, tongTien);
            ps.executeUpdate();
            System.out.println("Thêm đơn đặt hàng thành công!");
        } catch (SQLException e) {
            System.out.println("Lỗi: " + e.getMessage());
        }
    }

    public void sua() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Nhập ID đơn đặt hàng cần sửa: ");
        int id = sc.nextInt(); sc.nextLine();
        System.out.print("Trạng thái mới: ");
        String trangThai = sc.nextLine();
        System.out.print("Ngày thanh toán mới (yyyy-mm-dd, có thể để trống): ");
        String ngayThanhToan = sc.nextLine();
        System.out.print("Đã thanh toán mới: ");
        double daThanhToan = sc.nextDouble();
        System.out.print("Tổng tiền mới: ");
        double tongTien = sc.nextDouble();
        sc.nextLine();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                "UPDATE dondathang SET trangthai=?, ngaythanhtoan=?, dathanhtoan=?, tongtien=? WHERE id=?")) {
            ps.setString(1, trangThai);
            if (ngayThanhToan.isEmpty()) ps.setNull(2, Types.DATE);
            else ps.setString(2, ngayThanhToan);
            ps.setDouble(3, daThanhToan);
            ps.setDouble(4, tongTien);
            ps.setInt(5, id);
            int rows = ps.executeUpdate();
            if (rows > 0) System.out.println("Sửa thành công!");
            else System.out.println("Không tìm thấy đơn đặt hàng.");
        } catch (SQLException e) {
            System.out.println("Lỗi: " + e.getMessage());
        }
    }

    public void xoa() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Nhập ID đơn đặt hàng cần xóa: ");
        int id = sc.nextInt(); sc.nextLine();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM dondathang WHERE id=?")) {
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            if (rows > 0) System.out.println("Xóa thành công!");
            else System.out.println("Không tìm thấy đơn đặt hàng.");
        } catch (SQLException e) {
            System.out.println("Lỗi: " + e.getMessage());
        }
    }

    public void xuat() {
        ConsoleUI.printHeader("DANH SÁCH ĐƠN ĐẶT HÀNG");
        System.out.println("┌────┬────────────┬──────┬────────────┬────────────┬────────────┬────────────┬────────────┐");
        System.out.println("│ ID │ Người lập  │ Bàn  │ Trạng thái │ Ngày lập   │ Ngày TT    │ Đã TT      │ Tổng tiền  │");
        System.out.println("├────┼────────────┼──────┼────────────┼────────────┼────────────┼────────────┼────────────┤");
        boolean any = false;
        try (Connection conn = DBUtil.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(
                "SELECT d.id, d.nguoilap, b.tenban, d.trangthai, d.ngaylap, d.ngaythanhtoan, d.dathanhtoan, d.tongtien " +
                "FROM dondathang d JOIN ban b ON d.ban_id = b.id ORDER BY d.id")) {
            while (rs.next()) {
                any = true;
                System.out.printf("│ %-2d │ %-10s │ %-4s │ %-10s │ %-10s │ %-10s │ %-10.0f │ %-10.0f │\n",
                    rs.getInt("id"),
                    rs.getString("nguoilap"),
                    rs.getString("tenban"),
                    rs.getString("trangthai"),
                    rs.getString("ngaylap"),
                    rs.getString("ngaythanhtoan") == null ? "" : rs.getString("ngaythanhtoan"),
                    rs.getDouble("dathanhtoan"),
                    rs.getDouble("tongtien")
                );
            }
        } catch (SQLException e) {
            System.out.println("Lỗi: " + e.getMessage());
        }
        if (!any) System.out.println("│ Không có dữ liệu                                                               │");
        System.out.println("└────┴────────────┴──────┴────────────┴────────────┴────────────┴────────────┴────────────┘");
        ConsoleUI.printFooter();
    }

    public void timkiem() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Tìm theo: 1.ID  2.Người lập/Trạng thái");
        System.out.print("Chọn: ");
        String chStr = sc.nextLine();
        int ch; try { ch = Integer.parseInt(chStr.trim()); } catch (NumberFormatException e) { System.out.println("Vui lòng nhập số hợp lệ."); return; }
        String sql;
        System.out.println("\n╔════╦════════════╦══════╦════════════╦════════════╦════════════╦════════════╦════════════╗");
        System.out.println("║ ID ║ Người lập  ║ Bàn  ║ Trạng thái ║ Ngày lập   ║ Ngày TT    ║ Đã TT      ║ Tổng tiền ║");
        System.out.println("╠════╬════════════╬══════╬════════════╬════════════╬════════════╬════════════╬════════════╣");
        try (Connection conn = DBUtil.getConnection()) {
            if (ch == 1) {
                System.out.print("Nhập ID: ");
                String idStr = sc.nextLine();
                int id; try { id = Integer.parseInt(idStr.trim()); } catch (NumberFormatException e) { System.out.println("ID không hợp lệ."); return; }
                sql = "SELECT d.id, d.nguoilap, b.tenban, d.trangthai, d.ngaylap, d.ngaythanhtoan, d.dathanhtoan, d.tongtien FROM dondathang d JOIN ban b ON d.ban_id = b.id WHERE d.id = ?";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, id);
                    try (ResultSet rs = ps.executeQuery()) { printDDH(rs); }
                }
            } else {
                System.out.print("Nhập từ khóa: ");
                String key = sc.nextLine();
                sql = "SELECT d.id, d.nguoilap, b.tenban, d.trangthai, d.ngaylap, d.ngaythanhtoan, d.dathanhtoan, d.tongtien FROM dondathang d JOIN ban b ON d.ban_id = b.id WHERE d.nguoilap LIKE ? OR d.trangthai LIKE ?";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, "%" + key + "%");
                    ps.setString(2, "%" + key + "%");
                    try (ResultSet rs = ps.executeQuery()) { printDDH(rs); }
                }
            }
        } catch (SQLException e) {
            System.out.println("Lỗi: " + e.getMessage());
        }
        System.out.println("╚════╩════════════╩══════╩════════════╩════════════╩════════════╩════════════╩════════════╝");
    }

    private void printDDH(ResultSet rs) throws SQLException {
        boolean any = false;
        while (rs.next()) {
            any = true;
            System.out.printf("║ %-2d ║ %-10s ║ %-4s ║ %-10s ║ %-10s ║ %-10s ║ %-10.0f ║ %-10.0f ║\n",
                rs.getInt("id"),
                rs.getString("nguoilap"),
                rs.getString("tenban"),
                rs.getString("trangthai"),
                rs.getString("ngaylap"),
                rs.getString("ngaythanhtoan") == null ? "" : rs.getString("ngaythanhtoan"),
                rs.getDouble("dathanhtoan"),
                rs.getDouble("tongtien")
            );
        }
        if (!any) System.out.println("║ Không có kết quả                                                             ║");
    }
}