package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import database.DBUtil;
import dto.LoaiMonDTO;

public class LoaiMonDAO {
    
    // Lấy tất cả loại món
    public List<LoaiMonDTO> layTatCaLoaiMon() {
        List<LoaiMonDTO> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM loaimon ORDER BY MaLoai";
        
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                LoaiMonDTO loaiMon = new LoaiMonDTO();
                loaiMon.setMaLoai(rs.getInt("MaLoai"));
                loaiMon.setTenLoai(rs.getString("TenLoai"));
                danhSach.add(loaiMon);
            }
        } catch (SQLException e) {
        }
        return danhSach;
    }
    
    // Lấy loại món theo mã
    public LoaiMonDTO layLoaiMonTheoMa(int maLoai) {
        String sql = "SELECT * FROM loaimon WHERE MaLoai = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, maLoai);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    LoaiMonDTO loaiMon = new LoaiMonDTO();
                    loaiMon.setMaLoai(rs.getInt("MaLoai"));
                    loaiMon.setTenLoai(rs.getString("TenLoai"));
                    return loaiMon;
                }
            }
        } catch (SQLException e) {
        }
        return null;
    }
    
    // Lấy mã loại món theo tên
    public int layMaLoaiMonTheoTen(String tenLoai) {
        String sql = "SELECT MaLoai FROM loaimon WHERE TenLoai = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, tenLoai);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("MaLoai");
                }
            }
        } catch (SQLException e) {
        }
        return -1;
    }
   
    // Thêm loại món mới
    public boolean themLoaiMon(LoaiMonDTO loaiMon) {
        String sql = "INSERT INTO loaimon (TenLoai) VALUES (?)";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, loaiMon.getTenLoai());
            
            int result = ps.executeUpdate();
            
            if (result > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        loaiMon.setMaLoai(rs.getInt(1));
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
        }
        return false;
    }
    
    // Cập nhật loại món
    public boolean capNhatLoaiMon(LoaiMonDTO loaiMon) {
        String sql = "UPDATE loaimon SET TenLoai = ? WHERE MaLoai = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, loaiMon.getTenLoai());
            ps.setInt(2, loaiMon.getMaLoai());
            
            int result = ps.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
        }
        return false;
    }
    
    // Xóa loại món
    public boolean xoaLoaiMon(int maLoai) {
        String sql = "DELETE FROM loaimon WHERE MaLoai = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, maLoai);
            int result = ps.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
        }
        return false;
    }
    
    // Kiểm tra loại món có được sử dụng trong bảng mon không
    public boolean kiemTraRangBuocXoa(int maLoai) {
        String sql = "SELECT COUNT(*) FROM mon WHERE MaLoai = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, maLoai);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
        }
        return false;
    }
    
    // Lấy danh sách tên loại món cho combo box
    public List<String> layDanhSachTenLoaiMon() {
        List<String> danhSach = new ArrayList<>();
        String sql = "SELECT TenLoai FROM loaimon ORDER BY MaLoai";
        
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                danhSach.add(rs.getString("TenLoai"));
            }
        } catch (SQLException e) {
        }
        return danhSach;
    }

    public List<LoaiMonDTO> timKiemLoaiMonTheoTen(String searchText) {
        List<LoaiMonDTO> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM loaimon WHERE TenLoai LIKE ? ORDER BY MaLoai";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + searchText + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LoaiMonDTO loaiMon = new LoaiMonDTO();
                    loaiMon.setMaLoai(rs.getInt("MaLoai"));
                    loaiMon.setTenLoai(rs.getString("TenLoai"));
                    danhSach.add(loaiMon);
                }
            }
        } catch (SQLException e) {
        }
        return danhSach;
    }
}

