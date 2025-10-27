package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import database.DBUtil;
import dto.DonHangDTO;
import dto.ChiTietDonHangDTO;

public class DonHangDAO {
    
    // Lấy tất cả đơn hàng
    public List<DonHangDTO> layTatCaDonHang() {
        List<DonHangDTO> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM dondathang ORDER BY MaDon";
        
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                DonHangDTO donHang = new DonHangDTO();
                donHang.setMaDon(rs.getInt("MaDon"));
                donHang.setMaNV(rs.getInt("MaNV"));
                donHang.setLoai(rs.getString("Loai"));
                donHang.setTrangThai(rs.getString("TrangThai"));
                donHang.setNgayDat(rs.getTimestamp("NgayDat"));
                donHang.setTongTien(rs.getLong("TongTien"));
                donHang.setGiamGia(rs.getInt("GiamGia"));
                danhSach.add(donHang);
            }
        } catch (SQLException e) {
        }
        return danhSach;
    }
    
    // Tìm kiếm đơn hàng
    public List<DonHangDTO> timKiemDonHang(String searchType, String searchText) {
        List<DonHangDTO> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM dondathang WHERE ";
        PreparedStatement ps;
        
        try (Connection conn = DBUtil.getConnection()) {
            if (searchType.equals("Tất cả") || searchText.isEmpty()) {
                sql = "SELECT * FROM dondathang ORDER BY MaDon";
                ps = conn.prepareStatement(sql);
            } else if (searchType.equals("ID")) {
                sql += "MaDon = ? ORDER BY MaDon";
                ps = conn.prepareStatement(sql);
                ps.setInt(1, Integer.parseInt(searchText));
            } else if (searchType.equals("Mã NV")) {
                sql += "MaNV = ? ORDER BY MaDon";
                ps = conn.prepareStatement(sql);
                ps.setInt(1, Integer.parseInt(searchText));
            } else if (searchType.equals("Loại")) {
                sql += "Loai LIKE ? ORDER BY MaDon";
                ps = conn.prepareStatement(sql);
                ps.setString(1, "%" + searchText + "%");
            } else {
                sql += "TrangThai LIKE ? ORDER BY MaDon";
                ps = conn.prepareStatement(sql);
                ps.setString(1, "%" + searchText + "%");
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DonHangDTO donHang = new DonHangDTO();
                    donHang.setMaDon(rs.getInt("MaDon"));
                    donHang.setMaNV(rs.getInt("MaNV"));
                    donHang.setLoai(rs.getString("Loai"));
                    donHang.setTrangThai(rs.getString("TrangThai"));
                    donHang.setNgayDat(rs.getTimestamp("NgayDat"));
                    donHang.setTongTien(rs.getLong("TongTien"));
                    donHang.setGiamGia(rs.getInt("GiamGia"));
                    danhSach.add(donHang);
                }
            }
        } catch (SQLException e) {
        }
        return danhSach;
    }
    
    // Lấy đơn hàng theo mã
    public DonHangDTO layDonHangTheoMa(int maDon) {
        String sql = "SELECT * FROM dondathang WHERE MaDon = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, maDon);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    DonHangDTO donHang = new DonHangDTO();
                    donHang.setMaDon(rs.getInt("MaDon"));
                    donHang.setMaNV(rs.getInt("MaNV"));
                    donHang.setLoai(rs.getString("Loai"));
                    donHang.setTrangThai(rs.getString("TrangThai"));
                    donHang.setNgayDat(rs.getTimestamp("NgayDat"));
                    donHang.setTongTien(rs.getLong("TongTien"));
                    donHang.setGiamGia(rs.getInt("GiamGia"));
                    return donHang;
                }
            }
        } catch (SQLException e) {
        }
        return null;
    }
    
    // Thêm đơn hàng mới
    public boolean themDonHang(DonHangDTO donHang) {
        String sql = "INSERT INTO dondathang (MaNV, Loai, TrangThai, NgayDat, TongTien, GiamGia) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setInt(1, donHang.getMaNV());
            ps.setString(2, donHang.getLoai());
            ps.setString(3, donHang.getTrangThai());
            ps.setTimestamp(4, donHang.getNgayDat());
            ps.setLong(5, donHang.getTongTien());
            ps.setInt(6, donHang.getGiamGia());
            
            int result = ps.executeUpdate();
            
            if (result > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        donHang.setMaDon(rs.getInt(1));
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
        }
        return false;
    }
    
    // Cập nhật đơn hàng
    public boolean capNhatDonHang(DonHangDTO donHang) {
        String sql = "UPDATE dondathang SET MaNV=?, Loai=?, TrangThai=?, NgayDat=?, TongTien=?, GiamGia=? WHERE MaDon=?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, donHang.getMaNV());
            ps.setString(2, donHang.getLoai());
            ps.setString(3, donHang.getTrangThai());
            ps.setTimestamp(4, donHang.getNgayDat());
            ps.setLong(5, donHang.getTongTien());
            ps.setInt(6, donHang.getGiamGia());
            ps.setInt(7, donHang.getMaDon());
            
            int result = ps.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
        }
        return false;
    }
    
    // Xóa đơn hàng
    public boolean xoaDonHang(int maDon) {
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            
            // Xóa chi tiết đơn hàng trước
            String sqlChiTiet = "DELETE FROM chitietdonhang WHERE MaDon = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlChiTiet)) {
                ps.setInt(1, maDon);
                ps.executeUpdate();
            }
            
            // Xóa đơn hàng
            String sqlDonHang = "DELETE FROM dondathang WHERE MaDon = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlDonHang)) {
                ps.setInt(1, maDon);
                int result = ps.executeUpdate();
                
                if (result > 0) {
                    conn.commit();
                    return true;
                } else {
                    conn.rollback();
                }
            }
        } catch (SQLException e) {
        }
        return false;
    }
    
    // Cập nhật trạng thái đơn hàng
    public boolean capNhatTrangThaiDonHang(int maDon, String trangThai) {
        String sql = "UPDATE dondathang SET TrangThai = ? WHERE MaDon = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, trangThai);
            ps.setInt(2, maDon);
            
            int result = ps.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
        }
        return false;
    }
    
    // Cập nhật trạng thái đơn hàng với enum
    
    // ========== CHI TIẾT ĐƠN HÀNG ==========
    
    // Lấy chi tiết đơn hàng
    public List<ChiTietDonHangDTO> layChiTietDonHang(int maDon) {
        List<ChiTietDonHangDTO> danhSach = new ArrayList<>();
        String sql = "SELECT ctdh.*, m1.TenMon AS TenMon, m2.TenMon AS TenTopping " +
                    "FROM chitietdonhang ctdh " +
                    "LEFT JOIN mon m1 ON ctdh.MaMon = m1.MaMon " +
                    "LEFT JOIN mon m2 ON ctdh.MaTopping = m2.MaMon " +
                    "WHERE ctdh.MaDon = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, maDon);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ChiTietDonHangDTO chiTiet = new ChiTietDonHangDTO();
                    chiTiet.setMaDon(rs.getInt("MaDon"));
                    chiTiet.setMaMon(rs.getInt("MaMon"));
                    chiTiet.setMaTopping(rs.getInt("MaTopping"));
                    chiTiet.setSoLuong(rs.getInt("SoLuong"));
                    chiTiet.setGiaMon(rs.getLong("GiaMon"));
                    chiTiet.setGiaTopping(rs.getLong("GiaTopping"));
                    chiTiet.setGhiChu(rs.getString("GhiChu"));
                    chiTiet.setTenMon(rs.getString("TenMon"));
                    chiTiet.setTenTopping(rs.getString("TenTopping"));
                    danhSach.add(chiTiet);
                }
            }
        } catch (SQLException e) {
        }
        return danhSach;
    }
    
    // Thêm chi tiết đơn hàng
    public boolean themChiTietDonHang(ChiTietDonHangDTO chiTiet) {
        String sql = "INSERT INTO chitietdonhang (MaDon, MaMon, MaTopping, SoLuong, GiaMon, GiaTopping, GhiChu) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, chiTiet.getMaDon());
            ps.setInt(2, chiTiet.getMaMon());
            ps.setInt(3, chiTiet.getMaTopping());
            ps.setInt(4, chiTiet.getSoLuong());
            ps.setLong(5, chiTiet.getGiaMon());
            ps.setLong(6, chiTiet.getGiaTopping());
            ps.setString(7, chiTiet.getGhiChu());
            
            int result = ps.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
        }
        return false;
    }
    
    // Xóa tất cả chi tiết đơn hàng
    public boolean xoaTatCaChiTietDonHang(int maDon) {
        String sql = "DELETE FROM chitietdonhang WHERE MaDon = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, maDon);
            int result = ps.executeUpdate();
            return result >= 0; // >= 0 vì có thể không có chi tiết nào
        } catch (SQLException e) {
        }
        return false;
    }
    
    // Lấy thông tin đơn hàng với tên nhân viên
    public DonHangDTO layDonHangVoiTenNV(int maDon) {
        String sql = "SELECT dh.*, nv.HoTen FROM dondathang dh " +
                    "LEFT JOIN nhanvien nv ON dh.MaNV = nv.MaNV " +
                    "WHERE dh.MaDon = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, maDon);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    DonHangDTO donHang = new DonHangDTO();
                    donHang.setMaDon(rs.getInt("MaDon"));
                    donHang.setMaNV(rs.getInt("MaNV"));
                    donHang.setLoai(rs.getString("Loai"));
                    donHang.setTrangThai(rs.getString("TrangThai"));
                    donHang.setNgayDat(rs.getTimestamp("NgayDat"));
                    donHang.setTongTien(rs.getLong("TongTien"));
                    donHang.setGiamGia(rs.getInt("GiamGia"));
                    donHang.setTenNV(rs.getString("HoTen"));
                    return donHang;
                }
            }
        } catch (SQLException e) {
        }
        return null;
    }
    
    // Method để chuẩn hóa trạng thái đơn hàng trong database
    public boolean chuanHoaTrangThaiDonHang() {
        String sql1 = "UPDATE dondathang SET TrangThai = 'chuathanhtoan' WHERE TrangThai = '0'";
        String sql2 = "UPDATE dondathang SET TrangThai = 'dathanhtoan' WHERE TrangThai = '1'";
        String sql3 = "UPDATE dondathang SET TrangThai = 'bihuy' WHERE TrangThai = '2'";
        
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            
            try (PreparedStatement ps = conn.prepareStatement(sql1)) {
                int result1 = ps.executeUpdate();
            }
            
            try (PreparedStatement ps = conn.prepareStatement(sql2)) {
                int result2 = ps.executeUpdate();
            }
            
            try (PreparedStatement ps = conn.prepareStatement(sql3)) {
                int result3 = ps.executeUpdate();
            }
            
            conn.commit();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
}
