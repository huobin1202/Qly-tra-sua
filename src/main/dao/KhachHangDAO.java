package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import database.DBUtil;
import dto.KhachHangDTO;

public class KhachHangDAO {
    
    // Lấy tất cả khách hàng
    public List<KhachHangDTO> layTatCaKhachHang() {
        List<KhachHangDTO> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM khachhang ORDER BY MaKH";

        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                KhachHangDTO khachHang = new KhachHangDTO();
                khachHang.setMaKH(rs.getInt("MaKH"));
                khachHang.setSoDienThoai(rs.getString("SDT"));
                khachHang.setHoTen(rs.getString("HoTen"));
                khachHang.setDiemTichLuy(rs.getInt("DiemTichLuy"));
                danhSach.add(khachHang);
            }
        } catch (SQLException e) {
        }
        return danhSach;
    }
    
    // Tìm kiếm khách hàng
    public List<KhachHangDTO> timKiemKhachHang(String searchType, String searchText) {
        List<KhachHangDTO> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM khachhang WHERE ";
        PreparedStatement ps;
        
        try (Connection conn = DBUtil.getConnection()) {
            if (searchType.equals("Tất cả") || searchText.isEmpty()) {
                sql = "SELECT * FROM khachhang ORDER BY MaKH";
                ps = conn.prepareStatement(sql);
            } else if (searchType.equals("ID")) {
                sql += "MaKH = ? ORDER BY MaKH";
                ps = conn.prepareStatement(sql);
                ps.setInt(1, Integer.parseInt(searchText));
            } else if (searchType.equals("Số điện thoại")) {
                sql += "SoDienThoai LIKE ? ORDER BY MaKH";
                ps = conn.prepareStatement(sql);
                ps.setString(1, "%" + searchText + "%");
            } else {
                sql += "HoTen LIKE ? ORDER BY MaKH";
                ps = conn.prepareStatement(sql);
                ps.setString(1, "%" + searchText + "%");
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    KhachHangDTO khachHang = new KhachHangDTO();
                    khachHang.setMaKH(rs.getInt("MaKH"));
                    khachHang.setSoDienThoai(rs.getString("SDT"));
                    khachHang.setHoTen(rs.getString("HoTen"));
                    khachHang.setDiemTichLuy(rs.getInt("DiemTichLuy"));
                    danhSach.add(khachHang);
                }
            }
        } catch (SQLException e) {
        }
        return danhSach;
    }

    // Thêm khách hàng mới
    public boolean themKhachHang(KhachHangDTO khachHang) {
        String sql = "INSERT INTO khachhang (SDT, HoTen, DiemTichLuy) VALUES (?, ?, ?)";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
                ps.setString(1, khachHang.getSoDienThoai());
            ps.setString(2, khachHang.getHoTen());
            ps.setInt(3, khachHang.getDiemTichLuy());
            
            int result = ps.executeUpdate();
            
            if (result > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        khachHang.setMaKH(rs.getInt(1));
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
        }
        return false;
    }
    
    // Cập nhật khách hàng
    public boolean capNhatKhachHang(KhachHangDTO khachHang) {
        String sql = "UPDATE khachhang SET SDT=?, HoTen=?, DiemTichLuy=? WHERE MaKH=?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
                ps.setString(1, khachHang.getSoDienThoai());
            ps.setString(2, khachHang.getHoTen());
            ps.setInt(3, khachHang.getDiemTichLuy());
            ps.setInt(4, khachHang.getMaKH());
            
            int result = ps.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
        }
        return false;
    }
    
    // Xóa khách hàng
    public boolean xoaKhachHang(int maKH) {
        String sql = "DELETE FROM khachhang WHERE MaKH = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, maKH);
            int result = ps.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
        }
        return false;
    }
    
    // Kiểm tra số điện thoại tồn tại
    public boolean kiemTraSDTTonTai(String soDienThoai) {
        String sql = "SELECT COUNT(*) FROM khachhang WHERE SoDienThoai = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, soDienThoai);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
        }
        return false;
    }  
}
