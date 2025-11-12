package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import database.DBUtil;
import dto.KhoHangDTO;

public class KhoHangDAO {
    // ========== TỒN KHO ==========
    // Lấy tồn kho theo mã nguyên liệu
    public KhoHangDTO layTonKhoTheoMaNL(int maNL) {
        String sql = "SELECT kh.MaNL, nl.TenNL, nl.DonVi, kh.SoLuong " +
                    "FROM khohang kh " +
                    "JOIN nguyenlieu nl ON kh.MaNL = nl.MaNL " +
                    "WHERE kh.MaNL = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, maNL);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    KhoHangDTO khoHang = new KhoHangDTO();
                    khoHang.setMaMon(rs.getInt("MaNL"));
                    khoHang.setTenMon(rs.getString("TenNL"));
                    khoHang.setTenDonVi(rs.getString("DonVi"));
                    khoHang.setSoLuong(rs.getInt("SoLuong"));
                    return khoHang;
                }
            }
        } catch (SQLException e) {
        }
        return null;
    }
        
    // Lấy tổng giá trị tồn kho
    public long layTongGiaTriTonKho() {
        String sql = "SELECT SUM(kh.SoLuong * nccnl.DonGia) as TongGiaTri " +
                    "FROM khohang kh " +
                    "JOIN ncc_nguyenlieu nccnl ON kh.MaNL = nccnl.MaNL";
        
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getLong("TongGiaTri");
            }
        } catch (SQLException e) {
        }
        return 0;
    }
    
    // Lấy tất cả tồn kho nguyên liệu (kể cả nguyên liệu chưa có trong kho - LEFT JOIN)
    public List<KhoHangDTO> layTatCaTonKhoVoiNguyenLieu() {
        List<KhoHangDTO> danhSach = new ArrayList<>();
        String sql = "SELECT nl.MaNL, nl.TenNL, nl.DonVi, COALESCE(k.SoLuong, 0) AS SoLuong " +
                    "FROM nguyenlieu nl LEFT JOIN khohang k ON nl.MaNL = k.MaNL " +
                    "ORDER BY nl.MaNL";
        
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                KhoHangDTO khoHang = new KhoHangDTO();
                khoHang.setMaMon(rs.getInt("MaNL"));
                khoHang.setTenMon(rs.getString("TenNL"));
                khoHang.setTenDonVi(rs.getString("DonVi"));
                khoHang.setSoLuong(rs.getInt("SoLuong"));
                danhSach.add(khoHang);
            }
        } catch (SQLException e) {
        }
        return danhSach;
    }
    
    // Tìm kiếm tồn kho (LEFT JOIN để lấy cả nguyên liệu chưa có trong kho)
    public List<KhoHangDTO> timKiemTonKhoVoiNguyenLieu(String searchType, String searchText) {
        List<KhoHangDTO> danhSach = new ArrayList<>();
        String sql = "SELECT nl.MaNL, nl.TenNL, nl.DonVi, COALESCE(k.SoLuong, 0) AS SoLuong " +
                    "FROM nguyenlieu nl LEFT JOIN khohang k ON nl.MaNL = k.MaNL WHERE ";
        PreparedStatement ps;
        
        try (Connection conn = DBUtil.getConnection()) {
            if (searchText.isEmpty()) {
                sql = "SELECT nl.MaNL, nl.TenNL, nl.DonVi, COALESCE(k.SoLuong, 0) AS SoLuong " +
                      "FROM nguyenlieu nl LEFT JOIN khohang k ON nl.MaNL = k.MaNL ORDER BY nl.MaNL";
                ps = conn.prepareStatement(sql);
            } else if (searchType.equals("Mã NL")) {
                sql += "nl.MaNL = ? ORDER BY nl.MaNL";
                ps = conn.prepareStatement(sql);
                ps.setInt(1, Integer.parseInt(searchText));
            } else {
                sql += "nl.TenNL LIKE ? ORDER BY nl.MaNL";
                ps = conn.prepareStatement(sql);
                ps.setString(1, "%" + searchText + "%");
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    KhoHangDTO khoHang = new KhoHangDTO();
                    khoHang.setMaMon(rs.getInt("MaNL"));
                    khoHang.setTenMon(rs.getString("TenNL"));
                    khoHang.setTenDonVi(rs.getString("DonVi"));
                    khoHang.setSoLuong(rs.getInt("SoLuong"));
                    danhSach.add(khoHang);
                }
            }
        } catch (SQLException e) {
        } catch (NumberFormatException e) {
        }
        return danhSach;
    }
    
    // Cập nhật số lượng tồn kho (INSERT hoặc UPDATE)
    public boolean capNhatSoLuongTonKhoVoiInsert(int maNL, int soLuong) {
        try (Connection conn = DBUtil.getConnection()) {
            // Kiểm tra xem đã có record trong khohang chưa
            boolean exists = false;
            try (PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM khohang WHERE MaNL = ?")) {
                ps.setInt(1, maNL);
                ResultSet rs = ps.executeQuery();
                exists = rs.next();
            }
            
            if (exists) {
                // Cập nhật
                try (PreparedStatement ps = conn.prepareStatement("UPDATE khohang SET SoLuong = ? WHERE MaNL = ?")) {
                    ps.setInt(1, soLuong);
                    ps.setInt(2, maNL);
                    int result = ps.executeUpdate();
                    return result > 0;
                }
            } else {
                // Thêm mới
                try (PreparedStatement ps = conn.prepareStatement("INSERT INTO khohang (MaNL, SoLuong) VALUES (?, ?)")) {
                    ps.setInt(1, maNL);
                    ps.setInt(2, soLuong);
                    int result = ps.executeUpdate();
                    return result > 0;
                }
            }
        } catch (SQLException e) {
        }
        return false;
    }
    
   
    // Lấy danh sách nguyên liệu sắp hết (LEFT JOIN để lấy cả nguyên liệu chưa có trong kho)
    public List<KhoHangDTO> layNguyenLieuSapHetVoiNguyenLieu(int nguong) {
        List<KhoHangDTO> danhSach = new ArrayList<>();
        String sql = "SELECT nl.TenNL, nl.DonVi, COALESCE(k.SoLuong, 0) as SoLuong " +
                    "FROM nguyenlieu nl LEFT JOIN khohang k ON nl.MaNL = k.MaNL " +
                    "WHERE COALESCE(k.SoLuong, 0) <= ? " +
                    "ORDER BY COALESCE(k.SoLuong, 0) ASC";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, nguong);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    KhoHangDTO khoHang = new KhoHangDTO();
                    khoHang.setTenMon(rs.getString("TenNL"));
                    khoHang.setTenDonVi(rs.getString("DonVi"));
                    khoHang.setSoLuong(rs.getInt("SoLuong"));
                    danhSach.add(khoHang);
                }
            }
        } catch (SQLException e) {
        }
        return danhSach;
    }
}
