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
                khachHang.setSoDienThoai(rs.getLong("SDT"));
                khachHang.setHoTen(rs.getString("HoTen"));
                khachHang.setDiaChi(rs.getString("DiaChi"));
                khachHang.setNgaySinh(rs.getTimestamp("NgaySinh"));
                danhSach.add(khachHang);
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
                    khachHang.setSoDienThoai(rs.getLong("SDT"));
                    khachHang.setHoTen(rs.getString("HoTen"));
                    khachHang.setDiaChi(rs.getString("DiaChi"));
                    khachHang.setNgaySinh(rs.getTimestamp("NgaySinh"));
                    danhSach.add(khachHang);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSach;
    }
    
    // Lấy khách hàng theo mã
    public KhachHangDTO layKhachHangTheoMa(int maKH) {
        String sql = "SELECT * FROM khachhang WHERE MaKH = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, maKH);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    KhachHangDTO khachHang = new KhachHangDTO();
                    khachHang.setMaKH(rs.getInt("MaKH"));
                    khachHang.setSoDienThoai(rs.getLong("SDT"));
                    khachHang.setHoTen(rs.getString("HoTen"));
                    khachHang.setDiaChi(rs.getString("DiaChi"));
                    khachHang.setNgaySinh(rs.getTimestamp("NgaySinh"));
                    return khachHang;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // Lấy khách hàng theo số điện thoại
    public KhachHangDTO layKhachHangTheoSDT(String soDienThoai) {
        String sql = "SELECT * FROM khachhang WHERE SoDienThoai = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, soDienThoai);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    KhachHangDTO khachHang = new KhachHangDTO();
                    khachHang.setMaKH(rs.getInt("MaKH"));
                    khachHang.setSoDienThoai(rs.getLong("SDT"));
                    khachHang.setHoTen(rs.getString("HoTen"));
                    khachHang.setDiaChi(rs.getString("DiaChi"));
                    khachHang.setNgaySinh(rs.getTimestamp("NgaySinh"));
                    return khachHang;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // Thêm khách hàng mới
    public boolean themKhachHang(KhachHangDTO khachHang) {
        String sql = "INSERT INTO khachhang (SDT, HoTen, DiaChi, NgaySinh) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setLong(1, khachHang.getSoDienThoai());
            ps.setString(2, khachHang.getHoTen());
            ps.setString(3, khachHang.getDiaChi());
            ps.setTimestamp(4, khachHang.getNgaySinh());
            
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
            e.printStackTrace();
        }
        return false;
    }
    
    // Cập nhật khách hàng
    public boolean capNhatKhachHang(KhachHangDTO khachHang) {
        String sql = "UPDATE khachhang SET SDT=?, HoTen=?, DiaChi=?, NgaySinh=? WHERE MaKH=?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setLong(1, khachHang.getSoDienThoai());
            ps.setString(2, khachHang.getHoTen());
            ps.setString(3, khachHang.getDiaChi());
            ps.setTimestamp(4, khachHang.getNgaySinh());
            ps.setInt(5, khachHang.getMaKH());
            
            int result = ps.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
        }
        return false;
    }
    
    // Lấy danh sách khách hàng cho combo box
    public List<KhachHangDTO> layDanhSachKhachHangChoCombo() {
        List<KhachHangDTO> danhSach = new ArrayList<>();
        String sql = "SELECT MaKH, HoTen, SoDienThoai FROM khachhang ORDER BY HoTen";
        
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                KhachHangDTO khachHang = new KhachHangDTO();
                khachHang.setMaKH(rs.getInt("MaKH"));
                khachHang.setHoTen(rs.getString("HoTen"));
                khachHang.setSoDienThoai(rs.getLong("SDT"));
                danhSach.add(khachHang);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSach;
    }
}
