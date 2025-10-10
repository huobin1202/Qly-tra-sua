package dao;
import java.sql.*;
import db.DBUtil;
import java.util.Scanner;

public class XuatHang {
    public void taoPhieuXuat() {
        Scanner sc = new Scanner(System.in);
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            int maNV = db.Session.currentMaNV;
            System.out.println("Nhân viên hiện tại: " + maNV);
            System.out.print("Ghi chú: ");
            String ghiChu = sc.nextLine();
            int maPX = 0;
            try (PreparedStatement ps = conn.prepareStatement("INSERT INTO phieuxuat (MaNV, GhiChu) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, maNV);
                ps.setString(2, ghiChu);
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) maPX = rs.getInt(1);
                }
            }
            long tongThanhTien = 0;
            while (true) {
                System.out.print("MaMon (0 để kết thúc): ");
                int maMon = Integer.parseInt(sc.nextLine().trim());
                if (maMon == 0) break;
                System.out.print("Số lượng: ");
                int sl = Integer.parseInt(sc.nextLine().trim());
                System.out.print("Đơn giá: ");
                long donGia = Long.parseLong(sc.nextLine().trim());

                // check tồn kho đủ
                int ton = 0;
                try (PreparedStatement ps = conn.prepareStatement("SELECT IFNULL(SoLuong,0) FROM khohang WHERE MaMon=? FOR UPDATE")) {
                    ps.setInt(1, maMon);
                    try (ResultSet rs = ps.executeQuery()) { if (rs.next()) ton = rs.getInt(1); }
                }
                if (ton < sl) { System.out.println("Không đủ tồn kho (còn " + ton + ")"); continue; }

                String donVi = null;
                try (PreparedStatement ps = conn.prepareStatement("SELECT TenDonVi FROM mon WHERE MaMon=?")) {
                    ps.setInt(1, maMon);
                    try (ResultSet rs = ps.executeQuery()) { if (rs.next()) donVi = rs.getString(1); }
                }
                try (PreparedStatement ps = conn.prepareStatement("INSERT INTO chitietxuat (MaPX, MaMon, SoLuong, DonGia, DonVi) VALUES (?,?,?,?,?)")) {
                    ps.setInt(1, maPX);
                    ps.setInt(2, maMon);
                    ps.setInt(3, sl);
                    ps.setLong(4, donGia);
                    ps.setString(5, donVi);
                    ps.executeUpdate();
                }
                tongThanhTien += (long) sl * donGia;
                // trừ kho
                try (PreparedStatement ps = conn.prepareStatement("UPDATE khohang SET SoLuong = SoLuong - ? WHERE MaMon=?")) {
                    ps.setInt(1, sl);
                    ps.setInt(2, maMon);
                    ps.executeUpdate();
                }
                // also update mon.SoLuong
                try (PreparedStatement ps = conn.prepareStatement("UPDATE mon SET SoLuong = SoLuong - ? WHERE MaMon=?")) {
                    ps.setInt(1, sl);
                    ps.setInt(2, maMon);
                    ps.executeUpdate();
                }
            }
            // update tổng tiền phiếu xuất
            try (PreparedStatement ps = conn.prepareStatement("UPDATE phieuxuat SET ThanhTien=? WHERE MaPX=?")) {
                ps.setLong(1, tongThanhTien);
                ps.setInt(2, maPX);
                ps.executeUpdate();
            }
            conn.commit();
            System.out.println("Đã tạo phiếu xuất: " + maPX);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
