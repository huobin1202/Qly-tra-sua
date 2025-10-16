package dao;

import java.sql.*;
import java.util.Scanner;

import datebase.DBUtil;
import view.ConsoleUI;

public class ChiTietDonHangDAO {


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

   

    public void them(Scanner sc) {
        try (Connection conn = DBUtil.getConnection()) {
            Integer maDon = promptId(sc, "Nhập mã đơn (0: hủy)"); if (maDon == null) { System.out.println("Đã hủy."); return; }
            while (!existsDon(conn, maDon)) {
                System.out.println("Không tìm thấy đơn, vui lòng nhập lại.");
                maDon = promptId(sc, "Nhập mã đơn (0: hủy)"); if (maDon == null) { System.out.println("Đã hủy."); return; }
            }
            while (true) {
                Integer maMon = promptId(sc, "Nhập mã món (0: hủy)" ); if (maMon == null) { System.out.println("Đã hủy thêm."); return; }
                if (!existsMon(conn, maMon)) { System.out.println("Món không tồn tại."); continue; }
                Integer maTopping = promptInt(sc, "Mã topping (0: không có, -1: hủy)", -1, Integer.MAX_VALUE, false);
                if (maTopping == null || maTopping == -1) { System.out.println("Đã hủy thêm."); return; }
                if (maTopping != 0 && !existsMon(conn, maTopping)) { System.out.println("Topping không tồn tại."); continue; }
                Integer soLuong = promptInt(sc, "Số lượng (>=1, 0: hủy)", 0, Integer.MAX_VALUE, true); if (soLuong == null) { System.out.println("Đã hủy thêm."); return; }

                long giaMon = getGiaMon(conn, maMon);
                long giaTop = maTopping == 0 ? 0 : getGiaMon(conn, maTopping);
                String ghiChu = promptStringAllowEmpty(sc, "Ghi chú (Enter bỏ qua, 0: hủy)"); if (ghiChu == null) { System.out.println("Đã hủy thêm."); return; }

                try (PreparedStatement ps = conn.prepareStatement("INSERT INTO chitietdonhang (MaDon, MaMon, MaTopping, SoLuong, GiaMon, GiaTopping, GhiChu) VALUES (?,?,?,?,?,?,?)")) {
                    ps.setInt(1, maDon);
                    ps.setInt(2, maMon);
                    ps.setInt(3, maTopping);
                    ps.setInt(4, soLuong);
                    ps.setLong(5, giaMon);
                    ps.setLong(6, giaTop);
                    if (ghiChu.isEmpty()) ps.setNull(7, Types.VARCHAR); else ps.setString(7, ghiChu);
                    ps.executeUpdate();
                    System.out.println("Đã thêm dòng chi tiết.");
                }

                System.out.print("Thêm tiếp? (y/n): ");
                String more = sc.nextLine();
                if (!"y".equalsIgnoreCase(more.trim())) break;
            }
            printChiTietByDon(conn, maDon);
        } catch (SQLException e) {
            System.out.println("Lỗi: " + e.getMessage());
        }
    }

    public void sua(Scanner sc) {
        try (Connection conn = DBUtil.getConnection()) {
            Integer maDon = promptId(sc, "Nhập mã đơn (0: hủy)"); if (maDon == null) { System.out.println("Đã hủy."); return; }
            if (!existsDon(conn, maDon)) { System.out.println("Không tìm thấy đơn."); return; }
            printChiTietByDon(conn, maDon);
            Integer maMon = promptId(sc, "Nhập mã món của dòng cần sửa (0: hủy)"); if (maMon == null) { System.out.println("Đã hủy."); return; }
            Integer maTopping = promptInt(sc, "Mã topping của dòng cần sửa (0: không có, -1: hủy)", -1, Integer.MAX_VALUE, false); if (maTopping == null || maTopping == -1) { System.out.println("Đã hủy."); return; }

            if (!existsChiTiet(conn, maDon, maMon, maTopping)) { System.out.println("Không tìm thấy dòng chi tiết."); return; }

            System.out.println("Chọn: 1. Sửa số lượng, 2. Sửa ghi chú, 0. Hủy");
            Integer ch = promptInt(sc, "Chọn", 0, 2, false); if (ch == null || ch == 0) { System.out.println("Đã hủy."); return; }
            if (ch == 1) {
                Integer soLuong = promptInt(sc, "Số lượng mới (>=1, 0: hủy)", 0, Integer.MAX_VALUE, true); if (soLuong == null) { System.out.println("Đã hủy."); return; }
                try (PreparedStatement ps = conn.prepareStatement("UPDATE chitietdonhang SET SoLuong=? WHERE MaDon=? AND MaMon=? AND MaTopping=?")) {
                    ps.setInt(1, soLuong);
                    ps.setInt(2, maDon);
                    ps.setInt(3, maMon);
                    ps.setInt(4, maTopping);
                    ps.executeUpdate();
                }
            } else if (ch == 2) {
                String ghiChu = promptStringAllowEmpty(sc, "Ghi chú mới (Enter để rỗng, 0: hủy)"); if (ghiChu == null) { System.out.println("Đã hủy."); return; }
                try (PreparedStatement ps = conn.prepareStatement("UPDATE chitietdonhang SET GhiChu=? WHERE MaDon=? AND MaMon=? AND MaTopping=?")) {
                    if (ghiChu.isEmpty()) ps.setNull(1, Types.VARCHAR); else ps.setString(1, ghiChu);
                    ps.setInt(2, maDon);
                    ps.setInt(3, maMon);
                    ps.setInt(4, maTopping);
                    ps.executeUpdate();
                }
            }
            System.out.println("Đã cập nhật.");
            printChiTietByDon(conn, maDon);
        } catch (SQLException e) {
            System.out.println("Lỗi: " + e.getMessage());
        }
    }

    public void xoa(Scanner sc) {
        try (Connection conn = DBUtil.getConnection()) {
            Integer maDon = promptId(sc, "Nhập mã đơn (0: hủy)"); if (maDon == null) { System.out.println("Đã hủy."); return; }
            if (!existsDon(conn, maDon)) { System.out.println("Không tìm thấy đơn."); return; }
            printChiTietByDon(conn, maDon);
            Integer maMon = promptId(sc, "Nhập mã món của dòng cần xóa (0: hủy)"); if (maMon == null) { System.out.println("Đã hủy."); return; }
            Integer maTopping = promptInt(sc, "Mã topping của dòng cần xóa (0: không có, -1: hủy)", -1, Integer.MAX_VALUE, false); if (maTopping == null || maTopping == -1) { System.out.println("Đã hủy."); return; }
            if (!existsChiTiet(conn, maDon, maMon, maTopping)) { System.out.println("Không tìm thấy dòng chi tiết."); return; }
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM chitietdonhang WHERE MaDon=? AND MaMon=? AND MaTopping=?")) {
                ps.setInt(1, maDon);
                ps.setInt(2, maMon);
                ps.setInt(3, maTopping);
                ps.executeUpdate();
            }
            System.out.println("Đã xóa.");
            printChiTietByDon(conn, maDon);
        } catch (SQLException e) {
            System.out.println("Lỗi: " + e.getMessage());
        }
    }
    
    public void xemChiTietTheoDon(Scanner sc) {
        try (Connection conn = DBUtil.getConnection()) {
            Integer maDon = promptId(sc, "Nhập mã đơn (0: hủy)"); if (maDon == null) { System.out.println("Đã hủy."); return; }
            if (!existsDon(conn, maDon)) { System.out.println("Không tìm thấy đơn."); return; }
            printChiTietByDon(conn, maDon);
        } catch (SQLException e) {
            System.out.println("Lỗi: " + e.getMessage());
        }
    }
    public void printChiTietByDon(Connection conn, int maDon) throws SQLException {
        System.out.println("┌────┬──────┬────────────────────┬───────────┬──────────┬────────────┬────────────┬──────────┐");
        System.out.println("│ Đơn│ Món  │ Tên món            │ Topping   │ Số lượng │ Giá món    │ Giá topping│ Ghi chú  │");
        System.out.println("├────┼──────┼────────────────────┼───────────┼──────────┼────────────┼────────────┼──────────┤");
        boolean any = false;
        String sql = "SELECT ct.*, m1.TenMon as tenMon, m2.TenMon as tenTop FROM chitietdonhang ct " +
                "LEFT JOIN mon m1 ON ct.MaMon = m1.MaMon " +
                "LEFT JOIN mon m2 ON ct.MaTopping = m2.MaMon " +
                "WHERE ct.MaDon=? ORDER BY ct.MaMon, ct.MaTopping";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maDon);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    any = true;
                    System.out.printf("│ %-2d │ %-4d │ %-20s │ %-9s │ %-8d │ %-10d │ %-10d │ %-8s │\n",
                        rs.getInt("MaDon"),
                        rs.getInt("MaMon"),
                        rs.getString("tenMon") == null ? "" : rs.getString("tenMon"),
                        rs.getString("tenTop") == null ? "" : rs.getString("tenTop"),
                        rs.getInt("SoLuong"),
                        rs.getLong("GiaMon"),
                        rs.getLong("GiaTopping"),
                        rs.getString("GhiChu") == null ? "" : rs.getString("GhiChu")
                    );
                }
            }
        }
        if (!any) System.out.println("│ Không có dữ liệu                                                                   │");
        System.out.println("└────┴──────┴────────────────────┴───────────┴──────────┴────────────┴────────────┴──────────┘");
    }

    private static Integer promptId(Scanner sc, String label) {
        return promptInt(sc, label, 1, Integer.MAX_VALUE, true);
    }

    private static Integer promptInt(Scanner sc, String label, int min, int max, boolean allowZeroAsCancel) {
        while (true) {
            System.out.print(label + ": ");
            String s = sc.nextLine();
            if (s == null) continue;
            s = s.trim();
            if (allowZeroAsCancel && s.equals("0")) return null;
            try {
                int v = Integer.parseInt(s);
                if (v < min || v > max) { System.out.println("Giá trị nằm ngoài phạm vi."); continue; }
                return v;
            } catch (NumberFormatException e) { System.out.println("Vui lòng nhập số hợp lệ."); }
        }
    }

    private static String promptStringAllowEmpty(Scanner sc, String label) {
        System.out.print(label + ": ");
        String s = sc.nextLine();
        if (s == null) return "";
        s = s.trim();
        if (s.equals("0")) return null;
        return s;
    }

    private static boolean existsDon(Connection conn, int maDon) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM dondathang WHERE MaDon=?")) {
            ps.setInt(1, maDon);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }

    private static boolean existsMon(Connection conn, int maMon) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM mon WHERE MaMon=?")) {
            ps.setInt(1, maMon);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }

    private static boolean existsChiTiet(Connection conn, int maDon, int maMon, int maTopping) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM chitietdonhang WHERE MaDon=? AND MaMon=? AND MaTopping=?")) {
            ps.setInt(1, maDon); ps.setInt(2, maMon); ps.setInt(3, maTopping);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }

    private static long getGiaMon(Connection conn, int maMon) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT Gia FROM mon WHERE MaMon=?")) {
            ps.setInt(1, maMon);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return rs.getLong(1); }
        }
        return 0;
    }
}


