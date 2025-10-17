package dao;
import java.sql.*;
import java.util.Scanner;

import database.DBUtil;

public class NhapHang {
    public void taoPhieuNhap() {
        Scanner sc = new Scanner(System.in);
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            int maNV = database.Session.currentMaNV;
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
                System.out.print("Nhập nguyên liệu (MaNL, 0: kết thúc): ");
                String idStr = sc.nextLine().trim();
                if (idStr.equals("0")) break;
                int maNL = Integer.parseInt(idStr);
                System.out.print("Số lượng: ");
                int sl = Integer.parseInt(sc.nextLine().trim());
                int nccQty = 0; long donGia = 0; String donVi = null;
                try (PreparedStatement ps = conn.prepareStatement("SELECT SoLuong, DonGia FROM ncc_nguyenlieu WHERE MaNCC=? AND MaNL=? FOR UPDATE")) {
                    ps.setInt(1, maNCC);
                    ps.setInt(2, maNL);
                    try (ResultSet rs = ps.executeQuery()) { if (rs.next()) { nccQty = rs.getInt(1); donGia = rs.getLong(2); } }
                }
                if (nccQty == 0) { System.out.println("Nhà cung cấp không có nguyên liệu này."); continue; }
                if (sl > nccQty) { System.out.println("Số lượng yêu cầu vượt quá số lượng NCC (" + nccQty + ")"); continue; }
                try (PreparedStatement ps = conn.prepareStatement("SELECT DonVi FROM nguyenlieu WHERE MaNL=?")) {
                    ps.setInt(1, maNL);
                    try (ResultSet rs = ps.executeQuery()) { if (rs.next()) donVi = rs.getString(1); }
                }
                try (PreparedStatement ps = conn.prepareStatement("INSERT INTO chitietnhap_nl (MaPN, MaNL, SoLuong, DonGia, DonVi) VALUES (?,?,?,?,?)")) {
                    ps.setInt(1, maPN);
                    ps.setInt(2, maNL);
                    ps.setInt(3, sl);
                    ps.setLong(4, donGia);
                    ps.setString(5, donVi);
                    ps.executeUpdate();
                }
                tongThanhTien += (long) sl * donGia;
                try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO kho_nguyenlieu (MaNL, SoLuong) VALUES (?, ?) ON DUPLICATE KEY UPDATE SoLuong = SoLuong + VALUES(SoLuong)")) {
                    ps.setInt(1, maNL);
                    ps.setInt(2, sl);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = conn.prepareStatement("UPDATE ncc_nguyenlieu SET SoLuong = SoLuong - ? WHERE MaNCC=? AND MaNL=?")) {
                    ps.setInt(1, sl);
                    ps.setInt(2, maNCC);
                    ps.setInt(3, maNL);
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
