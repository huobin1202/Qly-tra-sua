package dao;
import java.sql.*;
import java.util.Scanner;

import db.DBUtil;
import view.ConsoleUI;

public class DonDatHangDAO {
    public void them() {
        Scanner sc = new Scanner(System.in);
        try {
            int maNV = db.Session.currentMaNV;
            System.out.print("Trạng thái: ");
            String trangThai = sc.nextLine();
            String ngayLap = null; // now auto by DB
            String ngayThanhToan = ""; // user may set later; keep empty
            System.out.print("Đã thanh toán: ");
            double daThanhToan = sc.nextDouble();
            System.out.print("Tổng tiền: ");
            double tongTien = sc.nextDouble();
            sc.nextLine();
            try (Connection conn = DBUtil.getConnection();
                 PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO dondathang (MaNV, TrangThai, SoTienDaTra, TongTien) VALUES (?, ?, ?, ?)")) {
                ps.setInt(1, maNV);
                ps.setString(2, trangThai);
                ps.setLong(3, (long)daThanhToan);
                ps.setLong(4, (long)tongTien);
                ps.executeUpdate();
                System.out.println("Thêm đơn đặt hàng thành công!");
            } catch (SQLException e) {
                System.out.println("Lỗi: " + e.getMessage());
            }
        } finally{}
    }

    public void sua() {
        Scanner sc = new Scanner(System.in);
        try {
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
                    "UPDATE dondathang SET TrangThai=?, NgayThanhToan=?, SoTienDaTra=?, TongTien=? WHERE MaDon=?")) {
                ps.setString(1, trangThai);
                if (ngayThanhToan.isEmpty()) ps.setNull(2, Types.TIMESTAMP);
                else ps.setTimestamp(2, java.sql.Timestamp.valueOf(ngayThanhToan + " 10:00:00"));
                ps.setLong(3, (long)daThanhToan);
                ps.setLong(4, (long)tongTien);
                ps.setInt(5, id);
                int rows = ps.executeUpdate();
                if (rows > 0) System.out.println("Sửa thành công!");
                else System.out.println("Không tìm thấy đơn đặt hàng.");
            } catch (SQLException e) {
                System.out.println("Lỗi: " + e.getMessage());
            }
        } finally{}
    }

    public void xoa() {
        Scanner sc = new Scanner(System.in);
        try {
            System.out.print("Nhập ID đơn đặt hàng cần xóa: ");
            int id = sc.nextInt(); sc.nextLine();
            try (Connection conn = DBUtil.getConnection();
                 PreparedStatement ps = conn.prepareStatement("DELETE FROM dondathang WHERE MaDon=?")) {
                ps.setInt(1, id);
                int rows = ps.executeUpdate();
                if (rows > 0) System.out.println("Xóa thành công!");
                else System.out.println("Không tìm thấy đơn đặt hàng.");
            } catch (SQLException e) {
                System.out.println("Lỗi: " + e.getMessage());
            }
        } finally{}
    }

    public void xuat() {
        ConsoleUI.printHeader("DANH SÁCH ĐƠN ĐẶT HÀNG");
        System.out.println("┌────┬────────────┬────────────┬────────────┬────────────┬────────────┬────────────┐");
        System.out.println("│ ID │ Người lập  │ Trạng thái │ Ngày lập   │ Ngày TT    │ Đã TT      │ Tổng tiền  │");
        System.out.println("├────┼────────────┼────────────┼────────────┼────────────┼────────────┼────────────┤");
        boolean any = false;
        try (Connection conn = DBUtil.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(
                "SELECT d.MaDon, nv.HoTen as nguoilap, d.TrangThai, d.NgayDat, d.NgayThanhToan, d.SoTienDaTra, d.TongTien " +
                "FROM dondathang d JOIN nhanvien nv ON d.MaNV = nv.MaNV ORDER BY d.MaDon")) {
            while (rs.next()) {
                any = true;
                System.out.printf("│ %-2d │ %-10s │ %-10s │ %-19s │ %-19s │ %-10d │ %-10d │\n",
                    rs.getInt("MaDon"),
                    rs.getString("nguoilap"),
                    rs.getString("TrangThai"),
                    rs.getString("NgayDat"),
                    rs.getString("NgayThanhToan") == null ? "" : rs.getString("NgayThanhToan"),
                    rs.getLong("SoTienDaTra"),
                    rs.getLong("TongTien")
                );
            }
        } catch (SQLException e) {
            System.out.println("Lỗi: " + e.getMessage());
        }
        if (!any) System.out.println("│ Không có dữ liệu                                                   │");
        System.out.println("└────┴────────────┴────────────┴────────────┴────────────┴────────────┴────────────┘");
        ConsoleUI.printFooter();
    }

    public void timkiem() {
        Scanner sc = new Scanner(System.in);
        try {
            System.out.println("Tìm theo: 1.ID  2.Người lập/Trạng thái");
            System.out.print("Chọn: ");
            String chStr = sc.nextLine();
            int ch; try { ch = Integer.parseInt(chStr.trim()); } catch (NumberFormatException e) { System.out.println("Vui lòng nhập số hợp lệ."); return; }
            String sql;
            System.out.println("\n╔════╦════════════╦════════════╦════════════╦════════════╦════════════╦════════════╗");
            System.out.println("║ ID ║ Người lập  ║ Trạng thái ║ Ngày lập   ║ Ngày TT    ║ Đã TT      ║ Tổng tiền ║");
            System.out.println("╠════╬════════════╬════════════╬════════════╬════════════╬════════════╬════════════╣");
            try (Connection conn = DBUtil.getConnection()) {
                if (ch == 1) {
                    System.out.print("Nhập ID: ");
                    String idStr = sc.nextLine();
                    int id; try { id = Integer.parseInt(idStr.trim()); } catch (NumberFormatException e) { System.out.println("ID không hợp lệ."); return; }
                    sql = "SELECT d.MaDon, nv.HoTen as nguoilap, d.TrangThai, d.NgayDat, d.NgayThanhToan, d.SoTienDaTra, d.TongTien FROM dondathang d JOIN nhanvien nv ON d.MaNV = nv.MaNV WHERE d.MaDon = ?";
                    try (PreparedStatement ps = conn.prepareStatement(sql)) {
                        ps.setInt(1, id);
                        try (ResultSet rs = ps.executeQuery()) { printDDH(rs); }
                    }
                } else {
                    System.out.print("Nhập từ khóa: ");
                    String key = sc.nextLine();
                    sql = "SELECT d.MaDon, nv.HoTen as nguoilap, d.TrangThai, d.NgayDat, d.NgayThanhToan, d.SoTienDaTra, d.TongTien FROM dondathang d JOIN nhanvien nv ON d.MaNV = nv.MaNV WHERE nv.HoTen LIKE ? OR d.TrangThai LIKE ?";
                    try (PreparedStatement ps = conn.prepareStatement(sql)) {
                        ps.setString(1, "%" + key + "%");
                        ps.setString(2, "%" + key + "%");
                        try (ResultSet rs = ps.executeQuery()) { printDDH(rs); }
                    }
                }
            } catch (SQLException e) {
                System.out.println("Lỗi: " + e.getMessage());
            }
        } finally{}
        System.out.println("╚════╩════════════╩════════════╩════════════╩════════════╩════════════╩════════════╝");
    }

    private void printDDH(ResultSet rs) throws SQLException {
        boolean any = false;
        while (rs.next()) {
            any = true;
            System.out.printf("║ %-2d ║ %-10s ║ %-10s ║ %-19s ║ %-19s ║ %-10d ║ %-10d ║\n",
                rs.getInt("MaDon"),
                rs.getString("nguoilap"),
                rs.getString("TrangThai"),
                rs.getString("NgayDat"),
                rs.getString("NgayThanhToan") == null ? "" : rs.getString("NgayThanhToan"),
                rs.getLong("SoTienDaTra"),
                rs.getLong("TongTien")
            );
        }
        if (!any) System.out.println("║ Không có kết quả                                                             ║");
    }
}