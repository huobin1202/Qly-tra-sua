package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import database.DBUtil;
import dto.NguyenLieuDTO;

public class NguyenLieuDAO {
    
    // Lấy tất cả nguyên liệu
    public List<NguyenLieuDTO> layTatCaNguyenLieu() {
        List<NguyenLieuDTO> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM nguyenlieu ORDER BY MaNL";
        
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                NguyenLieuDTO nguyenLieu = new NguyenLieuDTO();
                nguyenLieu.setMaNL(rs.getInt("MaNL"));
                nguyenLieu.setTenNL(rs.getString("TenNL"));
                nguyenLieu.setDonVi(rs.getString("DonVi"));
                danhSach.add(nguyenLieu);
            }
        } catch (SQLException e) {
        }
        return danhSach;
    }
    
    // Lấy nguyên liệu theo mã
    public NguyenLieuDTO layNguyenLieuTheoMa(int maNL) {
        String sql = "SELECT * FROM nguyenlieu WHERE MaNL = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, maNL);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    NguyenLieuDTO nguyenLieu = new NguyenLieuDTO();
                    nguyenLieu.setMaNL(rs.getInt("MaNL"));
                    nguyenLieu.setTenNL(rs.getString("TenNL"));
                    nguyenLieu.setDonVi(rs.getString("DonVi"));
                    return nguyenLieu;
                }
            }
        } catch (SQLException e) {
        }
        return null;
    }
    
    // Tìm kiếm nguyên liệu
    public List<NguyenLieuDTO> timKiemNguyenLieu(String searchType, String searchText) {
        List<NguyenLieuDTO> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM nguyenlieu WHERE ";
        PreparedStatement ps;
        
        try (Connection conn = DBUtil.getConnection()) {
            if (searchType.equals("Tất cả") || searchText.isEmpty()) {
                sql = "SELECT * FROM nguyenlieu ORDER BY MaNL";
                ps = conn.prepareStatement(sql);
            } else if (searchType.equals("ID")) {
                sql += "MaNL = ? ORDER BY MaNL";
                ps = conn.prepareStatement(sql);
                ps.setInt(1, Integer.parseInt(searchText));
            } else {
                sql += "TenNL LIKE ? ORDER BY MaNL";
                ps = conn.prepareStatement(sql);
                ps.setString(1, "%" + searchText + "%");
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    NguyenLieuDTO nguyenLieu = new NguyenLieuDTO();
                    nguyenLieu.setMaNL(rs.getInt("MaNL"));
                    nguyenLieu.setTenNL(rs.getString("TenNL"));
                    nguyenLieu.setDonVi(rs.getString("DonVi"));
                    danhSach.add(nguyenLieu);
                }
            }
        } catch (SQLException e) {
        } catch (NumberFormatException e) {
        }
        return danhSach;
    }
    
    // Thêm nguyên liệu mới
    public boolean themNguyenLieu(NguyenLieuDTO nguyenLieu) {
        String sql = "INSERT INTO nguyenlieu (TenNL, DonVi) VALUES (?, ?)";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, nguyenLieu.getTenNL());
            ps.setString(2, nguyenLieu.getDonVi());
            
            int result = ps.executeUpdate();
            
            if (result > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        nguyenLieu.setMaNL(rs.getInt(1));
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
        }
        return false;
    }
    
    // Cập nhật nguyên liệu
    public boolean capNhatNguyenLieu(NguyenLieuDTO nguyenLieu) {
        String sql = "UPDATE nguyenlieu SET TenNL = ?, DonVi = ? WHERE MaNL = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, nguyenLieu.getTenNL());
            ps.setString(2, nguyenLieu.getDonVi());
            ps.setInt(3, nguyenLieu.getMaNL());
            
            int result = ps.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
        }
        return false;
    }
    
    // Xóa nguyên liệu
    public boolean xoaNguyenLieu(int maNL) {
        String sql = "DELETE FROM nguyenlieu WHERE MaNL = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, maNL);
            int result = ps.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
        }
        return false;
    }
    
    // Kiểm tra ràng buộc trước khi xóa nguyên liệu
    public String kiemTraRangBuocXoa(int maNL) {
        try (Connection conn = DBUtil.getConnection()) {
            // Kiểm tra trong chi tiết nhập hàng
            try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM chitietnhap_nl WHERE MaNL=?")) {
                ps.setInt(1, maNL);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        return "Không thể xóa nguyên liệu này vì đã được sử dụng trong phiếu nhập hàng!";
                    }
                }
            }
            
            // Kiểm tra trong kho
            try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM khohang WHERE MaNL=?")) {
                ps.setInt(1, maNL);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        return "Không thể xóa nguyên liệu này vì đã có trong kho!";
                    }
                }
            }
            
            // Kiểm tra trong nhà cung cấp
            try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM ncc_nguyenlieu WHERE MaNL=?")) {
                ps.setInt(1, maNL);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        return "Không thể xóa nguyên liệu này vì đã được nhà cung cấp cung cấp!";
                    }
                }
            }
            
            // Kiểm tra trong mon_nguyenlieu
            try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM mon_nguyenlieu WHERE MaNL=?")) {
                ps.setInt(1, maNL);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        return "Không thể xóa nguyên liệu này vì đã được sử dụng trong món!";
                    }
                }
            }
        } catch (SQLException e) {
            return "Lỗi kiểm tra ràng buộc: " + e.getMessage();
        }
        return null; // Không có ràng buộc
    }
    
   
}

