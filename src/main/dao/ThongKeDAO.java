package dao;

import dto.ThongKeDTO;
import database.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ThongKeDAO {
    
    // Thống kê món bán chạy nhất
    public List<ThongKeDTO> thongKeMonBanChay(String tuNgay, String denNgay) {
        List<ThongKeDTO> result = new ArrayList<>();
        String sql = "SELECT m.TenMon, SUM(ct.SoLuong) as SoLuongBan, SUM(ct.SoLuong * (ct.GiaMon + ct.GiaTopping)) as TongTien " +
                    "FROM chitietdonhang ct " +
                    "JOIN mon m ON ct.MaMon = m.MaMon " +
                    "JOIN dondathang dh ON ct.MaDon = dh.MaDon " +
                    "WHERE dh.TrangThai = 'dathanhtoan' " +
                    "AND dh.NgayDat BETWEEN ? AND ? " +
                    "GROUP BY m.MaMon, m.TenMon " +
                    "ORDER BY SoLuongBan DESC " +
                    "LIMIT 10";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, tuNgay);
            ps.setString(2, denNgay);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ThongKeDTO tk = new ThongKeDTO(
                        rs.getString("TenMon"),
                        rs.getInt("SoLuongBan"),
                        rs.getLong("TongTien")
                    );
                    result.add(tk);
                }
            }
        } catch (SQLException e) {
        }
        
        return result;
    }
    
    // Thống kê doanh thu theo loại món
    public List<ThongKeDTO> thongKeDoanhThuTheoLoaiMon(String tuNgay, String denNgay) {
        List<ThongKeDTO> result = new ArrayList<>();
        String sql = "SELECT lm.TenLoai, SUM(ct.SoLuong) as SoLuongBan, SUM(ct.SoLuong * (ct.GiaMon + ct.GiaTopping)) as TongTien " +
                    "FROM chitietdonhang ct " +
                    "JOIN mon m ON ct.MaMon = m.MaMon " +
                    "JOIN loaimon lm ON m.MaLoai = lm.MaLoai " +
                    "JOIN dondathang dh ON ct.MaDon = dh.MaDon " +
                    "WHERE dh.TrangThai = 'dathanhtoan' " +
                    "AND dh.NgayDat BETWEEN ? AND ? " +
                    "GROUP BY lm.MaLoai, lm.TenLoai " +
                    "ORDER BY TongTien DESC";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, tuNgay);
            ps.setString(2, denNgay);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ThongKeDTO tk = new ThongKeDTO(
                        rs.getString("TenLoai"),
                        rs.getInt("SoLuongBan"),
                        rs.getLong("TongTien"),
                        1 // type = 1 cho loại món
                    );
                    result.add(tk);
                }
            }
        } catch (SQLException e) {
        }
        
        return result;
    }
    
    // Thống kê nhân viên bán hàng
    public List<ThongKeDTO> thongKeNhanVienBanHang(String tuNgay, String denNgay) {
        List<ThongKeDTO> result = new ArrayList<>();
        String sql = "SELECT nv.HoTen, COUNT(dh.MaDon) as SoDonHang, SUM(dh.TongTien) as DoanhThu " +
                    "FROM dondathang dh " +
                    "JOIN nhanvien nv ON dh.MaNV = nv.MaNV " +
                    "WHERE dh.TrangThai = 'dathanhtoan' " +
                    "AND dh.NgayDat BETWEEN ? AND ? " +
                    "GROUP BY nv.MaNV, nv.HoTen " +
                    "ORDER BY DoanhThu DESC";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, tuNgay);
            ps.setString(2, denNgay);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ThongKeDTO tk = new ThongKeDTO(
                        rs.getString("HoTen"),
                        rs.getInt("SoDonHang"),
                        rs.getLong("DoanhThu"),
                        "nhanvien" // type = "nhanvien"
                    );
                    result.add(tk);
                }
            }
        } catch (SQLException e) {
        }
        
        return result;
    }
    
    // Thống kê doanh thu theo ngày
    public List<ThongKeDTO> thongKeDoanhThuTheoNgay(String tuNgay, String denNgay) {
        List<ThongKeDTO> result = new ArrayList<>();
        String sql = "SELECT DATE(dh.NgayDat) as Ngay, SUM(dh.TongTien) as DoanhThu " +
                    "FROM dondathang dh " +
                    "WHERE dh.TrangThai = 'dathanhtoan' " +
                    "AND dh.NgayDat BETWEEN ? AND ? " +
                    "GROUP BY DATE(dh.NgayDat) " +
                    "ORDER BY Ngay ASC";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, tuNgay);
            ps.setString(2, denNgay);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ThongKeDTO tk = new ThongKeDTO(
                        rs.getString("Ngay"),
                        rs.getLong("DoanhThu"),
                        'd' // type = 'd' cho ngày
                    );
                    result.add(tk);
                }
            }
        } catch (SQLException e) {
        }
        
        return result;
    }
    
    // Thống kê doanh thu theo tháng
    public List<ThongKeDTO> thongKeDoanhThuTheoThang(String nam) {
        List<ThongKeDTO> result = new ArrayList<>();
        String sql = "SELECT MONTH(dh.NgayDat) as Thang, SUM(dh.TongTien) as DoanhThu " +
                    "FROM dondathang dh " +
                    "WHERE dh.TrangThai = 'dathanhtoan' " +
                    "AND YEAR(dh.NgayDat) = ? " +
                    "GROUP BY MONTH(dh.NgayDat) " +
                    "ORDER BY Thang ASC";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, nam);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ThongKeDTO tk = new ThongKeDTO(
                        "Tháng " + rs.getInt("Thang"),
                        rs.getLong("DoanhThu"),
                        (byte)1 // type = 1 cho tháng
                    );
                    result.add(tk);
                }
            }
        } catch (SQLException e) {
        }
        
        return result;
    }
    
    // Thống kê tổng quan
    public ThongKeDTO thongKeTongQuan() {
        ThongKeDTO result = new ThongKeDTO();
        
        try (Connection conn = DBUtil.getConnection()) {
            // Tổng doanh thu
            String sqlDoanhThu = "SELECT SUM(TongTien) as TongDoanhThu " +
                                "FROM dondathang " +
                                "WHERE TrangThai = 'dathanhtoan'";
            
            try (PreparedStatement ps = conn.prepareStatement(sqlDoanhThu);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    result.setDoanhThu(rs.getLong("TongDoanhThu"));
                }
            }
            
            // Số khách hàng
            String sqlKhachHang = "SELECT COUNT(*) as SoKhachHang FROM khachhang";
            try (PreparedStatement ps = conn.prepareStatement(sqlKhachHang);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    result.setSoKhachHang(rs.getInt("SoKhachHang"));
                }
            }
            
            // Số nhân viên
            String sqlNhanVien = "SELECT COUNT(*) as SoNhanVien FROM nhanvien";
            try (PreparedStatement ps = conn.prepareStatement(sqlNhanVien);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    result.setSoNhanVien(rs.getInt("SoNhanVien"));
                }
            }
            
            // Số món
            String sqlMon = "SELECT COUNT(*) as SoMon FROM mon";
            try (PreparedStatement ps = conn.prepareStatement(sqlMon);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    result.setSoMon(rs.getInt("SoMon"));
                }
            }
            
            // Số nguyên liệu
            String sqlNguyenLieu = "SELECT COUNT(*) as SoNguyenLieu FROM nguyenlieu";
            try (PreparedStatement ps = conn.prepareStatement(sqlNguyenLieu);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    result.setSoNguyenLieu(rs.getInt("SoNguyenLieu"));
                }
            }
            
            // Số nhà cung cấp
            String sqlNhaCungCap = "SELECT COUNT(*) as SoNhaCungCap FROM nhacungcap";
            try (PreparedStatement ps = conn.prepareStatement(sqlNhaCungCap);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    result.setSoNhaCungCap(rs.getInt("SoNhaCungCap"));
                }
            }
            
        } catch (SQLException e) {
        }
        
        return result;
    }
    
    // Thống kê đơn hàng theo trạng thái
    public List<ThongKeDTO> thongKeDonHangTheoTrangThai() {
        List<ThongKeDTO> result = new ArrayList<>();
        String sql = "SELECT TrangThai, COUNT(*) as SoDonHang " +
                    "FROM dondathang " +
                    "GROUP BY TrangThai";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                ThongKeDTO tk = new ThongKeDTO();
                tk.setTrangThai(rs.getString("TrangThai"));
                tk.setSoDonHang(rs.getInt("SoDonHang"));
                result.add(tk);
            }
        } catch (SQLException e) {
        }
        
        return result;
    }
    
    // Thống kê khách hàng mới theo tháng
    public List<ThongKeDTO> thongKeKhachHangMoiTheoThang(String nam) {
        List<ThongKeDTO> result = new ArrayList<>();
        String sql = "SELECT MONTH(NgaySinh) as Thang, COUNT(*) as SoKhachHang " +
                    "FROM khachhang " +
                    "WHERE YEAR(NgaySinh) = ? " +
                    "GROUP BY MONTH(NgaySinh) " +
                    "ORDER BY Thang ASC";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, nam);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ThongKeDTO tk = new ThongKeDTO();
                    tk.setThang("Tháng " + rs.getInt("Thang"));
                    tk.setSoKhachHang(rs.getInt("SoKhachHang"));
                    result.add(tk);
                }
            }
        } catch (SQLException e) {
        }
        
        return result;
    }
}
