package dao;
import java.sql.*;
import db.DBUtil;
import java.util.Scanner;

public class NhapHang {
    public void taoPhieuNhap() {
        Scanner sc = new Scanner(System.in);
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            int maNV = db.Session.currentMaNV;
            System.out.println("Nhân viên hiện tại: " + maNV);
            System.out.print("Mã nhà cung cấp: ");
            int maNCC = Integer.parseInt(sc.nextLine().trim());
            System.out.print("Ghi chú: ");
            String ghiChu = sc.nextLine();
            int maPN = 0;
            try (PreparedStatement ps = conn.prepareStatement("INSERT INTO phieunhap (MaNV, MaNCC, GhiChu) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, maNV);
                ps.setInt(2, maNCC);
                ps.setString(3, ghiChu);
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) maPN = rs.getInt(1);
                }
            }
            long tongThanhTien = 0;
            while (true) {
                System.out.print("MaMon (0 để kết thúc): ");
                int maMon = Integer.parseInt(sc.nextLine().trim());
                if (maMon == 0) break;
                System.out.print("Số lượng: ");
                int sl = Integer.parseInt(sc.nextLine().trim());
                // Validate supplier has this product and enough quantity
                int nccQty = 0; long donGia = 0;
                try (PreparedStatement ps = conn.prepareStatement("SELECT SoLuong, DonGia FROM ncc_sanpham WHERE MaNCC=? AND MaMon=? FOR UPDATE")) {
                    ps.setInt(1, maNCC);
                    ps.setInt(2, maMon);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) { nccQty = rs.getInt(1); donGia = rs.getLong(2); }
                    }
                }
                if (nccQty == 0) { System.out.println("Nhà cung cấp không có sản phẩm này."); continue; }
                if (sl > nccQty) { System.out.println("Số lượng yêu cầu vượt quá số lượng NCC (" + nccQty + ")"); continue; }
                String donVi = null;
                try (PreparedStatement ps = conn.prepareStatement("SELECT TenDonVi FROM mon WHERE MaMon=?")) {
                    ps.setInt(1, maMon);
                    try (ResultSet rs = ps.executeQuery()) { if (rs.next()) donVi = rs.getString(1); }
                }
                try (PreparedStatement ps = conn.prepareStatement("INSERT INTO chitietnhap (MaPN, MaMon, SoLuong, DonGia, DonVi) VALUES (?,?,?,?,?)")) {
                    ps.setInt(1, maPN);
                    ps.setInt(2, maMon);
                    ps.setInt(3, sl);
                    ps.setLong(4, donGia);
                    ps.setString(5, donVi);
                    ps.executeUpdate();
                }
                tongThanhTien += (long) sl * donGia;
                // upsert kho
                try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO khohang (MaMon, SoLuong) VALUES (?, ?) " +
                    "ON DUPLICATE KEY UPDATE SoLuong = SoLuong + VALUES(SoLuong)")) {
                    ps.setInt(1, maMon);
                    ps.setInt(2, sl);
                    ps.executeUpdate();
                }
                // mon table no longer tracks SoLuong
                // decrement supplier stock
                try (PreparedStatement ps = conn.prepareStatement("UPDATE ncc_sanpham SET SoLuong = SoLuong - ? WHERE MaNCC=? AND MaMon=?")) {
                    ps.setInt(1, sl);
                    ps.setInt(2, maNCC);
                    ps.setInt(3, maMon);
                    ps.executeUpdate();
                }
            }
            // update tổng tiền phiếu nhập
            try (PreparedStatement ps = conn.prepareStatement("UPDATE phieunhap SET ThanhTien=? WHERE MaPN=?")) {
                ps.setLong(1, tongThanhTien);
                ps.setInt(2, maPN);
                ps.executeUpdate();
            }
            conn.commit();
            System.out.println("Đã tạo phiếu nhập: " + maPN);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
