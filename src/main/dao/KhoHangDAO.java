package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import database.DBUtil;
import dto.KhoHangDTO;

public class KhoHangDAO {
    
    // Lấy tất cả tồn kho nguyên liệu
    public List<KhoHangDTO> layTatCaTonKho() {
        List<KhoHangDTO> danhSach = new ArrayList<>();
        String sql = "SELECT kh.MaNL, nl.TenNL, nl.DonVi, kh.SoLuong " +
                    "FROM khohang kh " +
                    "JOIN nguyenlieu nl ON kh.MaNL = nl.MaNL " +
                    "ORDER BY nl.TenNL";
        
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
            e.printStackTrace();
        }
        return danhSach;
    }
    
    // Tìm kiếm tồn kho
    public List<KhoHangDTO> timKiemTonKho(String searchType, String searchText) {
        List<KhoHangDTO> danhSach = new ArrayList<>();
        String sql = "SELECT kh.MaNL, nl.TenNL, nl.DonVi, kh.SoLuong " +
                    "FROM khohang kh " +
                    "JOIN nguyenlieu nl ON kh.MaNL = nl.MaNL " +
                    "WHERE ";
        PreparedStatement ps;
        
        try (Connection conn = DBUtil.getConnection()) {
            if (searchType.equals("Tất cả") || searchText.isEmpty()) {
                sql = "SELECT kh.MaNL, nl.TenNL, nl.DonVi, kh.SoLuong " +
                      "FROM khohang kh " +
                      "JOIN nguyenlieu nl ON kh.MaNL = nl.MaNL " +
                      "ORDER BY nl.TenNL";
                ps = conn.prepareStatement(sql);
            } else if (searchType.equals("Mã NL")) {
                sql += "kh.MaNL = ? ORDER BY nl.TenNL";
                ps = conn.prepareStatement(sql);
                ps.setInt(1, Integer.parseInt(searchText));
            } else {
                sql += "nl.TenNL LIKE ? ORDER BY nl.TenNL";
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
            e.printStackTrace();
        }
        return danhSach;
    }
    
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
            e.printStackTrace();
        }
        return null;
    }
    
    // Cập nhật số lượng tồn kho
    public boolean capNhatSoLuongTonKho(int maNL, int soLuong) {
        String sql = "INSERT INTO khohang (MaNL, SoLuong) VALUES (?, ?) " +
                    "ON DUPLICATE KEY UPDATE SoLuong = SoLuong + ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, maNL);
            ps.setInt(2, soLuong);
            ps.setInt(3, soLuong);
            
            int result = ps.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Giảm số lượng tồn kho
    public boolean giamSoLuongTonKho(int maNL, int soLuong) {
        String sql = "UPDATE khohang SET SoLuong = SoLuong - ? WHERE MaNL = ? AND SoLuong >= ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, soLuong);
            ps.setInt(2, maNL);
            ps.setInt(3, soLuong);
            
            int result = ps.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Kiểm tra số lượng tồn kho có đủ không
    public boolean kiemTraSoLuongTonKho(int maNL, int soLuongCan) {
        String sql = "SELECT SoLuong FROM khohang WHERE MaNL = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, maNL);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int soLuongTon = rs.getInt("SoLuong");
                    return soLuongTon >= soLuongCan;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Lấy danh sách nguyên liệu sắp hết (dưới ngưỡng)
    public List<KhoHangDTO> layNguyenLieuSapHet(int nguong) {
        List<KhoHangDTO> danhSach = new ArrayList<>();
        String sql = "SELECT kh.MaNL, nl.TenNL, nl.DonVi, kh.SoLuong " +
                    "FROM khohang kh " +
                    "JOIN nguyenlieu nl ON kh.MaNL = nl.MaNL " +
                    "WHERE kh.SoLuong <= ? " +
                    "ORDER BY kh.SoLuong ASC";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, nguong);
            
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
            e.printStackTrace();
        }
        return danhSach;
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
            e.printStackTrace();
        }
        return 0;
    }
}
