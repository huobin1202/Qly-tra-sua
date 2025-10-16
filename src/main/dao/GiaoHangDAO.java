package dao;
import java.sql.*;
import java.util.Scanner;

import database.DBUtil;
import view.ConsoleUI;

public class GiaoHangDAO {
    public void them() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Đơn hàng ID: ");
        String ddhIdStr = sc.nextLine();
        int ddhId; try { ddhId = Integer.parseInt(ddhIdStr.trim()); } catch (NumberFormatException e) { System.out.println("ID đơn hàng không hợp lệ."); return; }
        System.out.print("Tên shipper: ");
        String tenShipper = sc.nextLine();
        System.out.print("SĐT shipper: ");
        String sdtShipper = sc.nextLine();
        System.out.print("Phí ship: ");
        String phiShipStr = sc.nextLine();
        double phiShip; try { phiShip = Double.parseDouble(phiShipStr.trim()); } catch (NumberFormatException e) { System.out.println("Phí ship không hợp lệ."); return; }
        System.out.print("Trạng thái: ");
        String trangThai = sc.nextLine();
        System.out.print("Ngày bắt đầu (yyyy-mm-dd, có thể để trống): ");
        String ngayBatDau = sc.nextLine();
        System.out.print("Ngày kết thúc (yyyy-mm-dd, có thể để trống): ");
        String ngayKetThuc = sc.nextLine();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO giaohang (dondathang_id, tenshipper, sdtshipper, phiship, trangthai, ngaybatdau, ngayketthuc) VALUES (?, ?, ?, ?, ?, ?, ?)")) {
            ps.setInt(1, ddhId);
            ps.setString(2, tenShipper);
            ps.setString(3, sdtShipper);
            ps.setDouble(4, phiShip);
            ps.setString(5, trangThai);
            if (ngayBatDau == null || ngayBatDau.trim().isEmpty()) ps.setNull(6, Types.DATE); else ps.setString(6, ngayBatDau.trim());
            if (ngayKetThuc == null || ngayKetThuc.trim().isEmpty()) ps.setNull(7, Types.DATE); else ps.setString(7, ngayKetThuc.trim());
            ps.executeUpdate();
            System.out.println("Thêm lịch giao hàng thành công!");
        } catch (SQLException e) {
            System.out.println("Lỗi: " + e.getMessage());
        }
    }

    public void sua() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Nhập ID giao hàng cần sửa: ");
        String idStr = sc.nextLine();
        int id; try { id = Integer.parseInt(idStr.trim()); } catch (NumberFormatException e) { System.out.println("ID không hợp lệ."); return; }
        System.out.print("Trạng thái mới: ");
        String trangThai = sc.nextLine();
        System.out.print("Ngày kết thúc (yyyy-mm-dd, có thể để trống): ");
        String ngayKetThuc = sc.nextLine();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                "UPDATE giaohang SET trangthai=?, ngayketthuc=? WHERE id=?")) {
            ps.setString(1, trangThai);
            if (ngayKetThuc == null || ngayKetThuc.trim().isEmpty()) ps.setNull(2, Types.DATE); else ps.setString(2, ngayKetThuc.trim());
            ps.setInt(3, id);
            int rows = ps.executeUpdate();
            if (rows > 0) System.out.println("Sửa thành công!");
            else System.out.println("Không tìm thấy giao hàng.");
        } catch (SQLException e) {
            System.out.println("Lỗi: " + e.getMessage());
        }
    }

    public void xoa() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Nhập ID giao hàng cần xóa: ");
        String idStr = sc.nextLine();
        int id; try { id = Integer.parseInt(idStr.trim()); } catch (NumberFormatException e) { System.out.println("ID không hợp lệ."); return; }
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM giaohang WHERE id=?")) {
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            if (rows > 0) System.out.println("Xóa thành công!");
            else System.out.println("Không tìm thấy giao hàng.");
        } catch (SQLException e) {
            System.out.println("Lỗi: " + e.getMessage());
        }
    }

    public void xuat() {
        ConsoleUI.printHeader("DANH SÁCH GIAO HÀNG");
        System.out.println("┌────┬────────────┬──────────────┬──────────────┬────────────┬────────────┬────────────┐");
        System.out.println("│ ID │ Đơn hàng  │ Tên shipper  │ SĐT shipper  │ Phí ship   │ Bắt đầu   │ Kết thúc  │");
        System.out.println("├────┼────────────┼──────────────┼──────────────┼────────────┼────────────┼────────────┤");
        try (Connection conn = DBUtil.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(
                "SELECT g.id, g.dondathang_id, g.tenshipper, g.sdtshipper, g.phiship, g.ngaybatdau, g.ngayketthuc FROM giaohang g ORDER BY g.id")) {
            boolean any = false;
            while (rs.next()) {
                any = true;
                System.out.printf("│ %-2d │ %-10d │ %-12s │ %-12s │ %-10.0f │ %-10s │ %-10s │\n",
                    rs.getInt("id"), rs.getInt("dondathang_id"), rs.getString("tenshipper"), rs.getString("sdtshipper"),
                    rs.getDouble("phiship"), rs.getString("ngaybatdau"), rs.getString("ngayketthuc") == null ? "" : rs.getString("ngayketthuc"));
            }
            if (!any) System.out.println("│ Không có dữ liệu                                                    │");
        } catch (SQLException e) {
            System.out.println("Lỗi: " + e.getMessage());
        }
        System.out.println("└────┴────────────┴──────────────┴──────────────┴────────────┴────────────┴────────────┘");
        ConsoleUI.printFooter();
    }

    public void timkiem() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Tìm theo: 1.ID giao hàng  2.ID đơn hàng  3.Tên shipper  4.Trạng thái");
        System.out.print("Chọn: ");
        String chStr = sc.nextLine();
        int ch; try { ch = Integer.parseInt(chStr.trim()); } catch (NumberFormatException e) { System.out.println("Vui lòng nhập số hợp lệ."); return; }
        String sql;
        try (Connection conn = DBUtil.getConnection()) {
            switch (ch) {
                case 1: {
                    System.out.print("Nhập ID giao hàng: ");
                    String idStr = sc.nextLine();
                    int id; try { id = Integer.parseInt(idStr.trim()); } catch (NumberFormatException e) { System.out.println("ID không hợp lệ."); return; }
                    sql = "SELECT * FROM giaohang WHERE id=?";
                    try (PreparedStatement ps = conn.prepareStatement(sql)) { ps.setInt(1, id); try (ResultSet rs = ps.executeQuery()) { printGH(rs); } }
                    break;
                }
                case 2: {
                    System.out.print("Nhập ID đơn hàng: ");
                    String idStr = sc.nextLine();
                    int id; try { id = Integer.parseInt(idStr.trim()); } catch (NumberFormatException e) { System.out.println("ID không hợp lệ."); return; }
                    sql = "SELECT * FROM giaohang WHERE dondathang_id=?";
                    try (PreparedStatement ps = conn.prepareStatement(sql)) { ps.setInt(1, id); try (ResultSet rs = ps.executeQuery()) { printGH(rs); } }
                    break;
                }
                case 3: {
                    System.out.print("Nhập tên shipper: ");
                    String key = sc.nextLine();
                    sql = "SELECT * FROM giaohang WHERE tenshipper LIKE ?";
                    try (PreparedStatement ps = conn.prepareStatement(sql)) { ps.setString(1, "%" + key + "%"); try (ResultSet rs = ps.executeQuery()) { printGH(rs); } }
                    break;
                }
                case 4: {
                    System.out.print("Nhập trạng thái: ");
                    String key = sc.nextLine();
                    sql = "SELECT * FROM giaohang WHERE trangthai LIKE ?";
                    try (PreparedStatement ps = conn.prepareStatement(sql)) { ps.setString(1, "%" + key + "%"); try (ResultSet rs = ps.executeQuery()) { printGH(rs); } }
                    break;
                }
                default:
                    System.out.println("Lựa chọn không hợp lệ.");
            }
        } catch (SQLException e) {
            System.out.println("Lỗi: " + e.getMessage());
        }
    }

    private void printGH(ResultSet rs) throws SQLException {
        System.out.println("\n╔════╦════════════╦══════════════╦══════════════╦════════════╦════════════╦════════════╗");
        System.out.println("║ ID ║ Đơn hàng  ║ Tên shipper  ║ SĐT shipper  ║ Phí ship   ║ Bắt đầu   ║ Kết thúc  ║");
        System.out.println("╠════╬════════════╬══════════════╬══════════════╬════════════╬════════════╬════════════╣");
        boolean any = false;
        while (rs.next()) {
            any = true;
            System.out.printf("║ %-2d ║ %-10d ║ %-12s ║ %-12s ║ %-10.0f ║ %-10s ║ %-10s ║\n",
                rs.getInt("id"), rs.getInt("dondathang_id"), rs.getString("tenshipper"), rs.getString("sdtshipper"),
                rs.getDouble("phiship"), rs.getString("ngaybatdau"), rs.getString("ngayketthuc") == null ? "" : rs.getString("ngayketthuc"));
        }
        if (!any) System.out.println("║ Không có kết quả                                                    ║");
        System.out.println("╚════╩════════════╩══════════════╩══════════════╩════════════╩════════════╩════════════╝");
    }
}