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
                    "JOIN donhang dh ON ct.MaDon = dh.MaDon " +
                    "WHERE dh.TrangThai = 'dathanhtoan' ";
        
        if (tuNgay != null && !tuNgay.isEmpty()) {
            sql += "AND DATE(dh.NgayDat) >= DATE(?) ";
        }
        if (denNgay != null && !denNgay.isEmpty()) {
            sql += "AND DATE(dh.NgayDat) <= DATE(?) ";
        }
        
        sql += "GROUP BY m.MaMon, m.TenMon " +
               "ORDER BY SoLuongBan DESC " +
               "LIMIT 10";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            int paramIndex = 1;
            if (tuNgay != null && !tuNgay.isEmpty()) {
                ps.setString(paramIndex++, tuNgay);
            }
            if (denNgay != null && !denNgay.isEmpty()) {
                ps.setString(paramIndex++, denNgay);
            }
            
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
            e.printStackTrace(); // Log error để debug
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
                    "JOIN donhang dh ON ct.MaDon = dh.MaDon " +
                    "WHERE dh.TrangThai = 'dathanhtoan' ";
        
        if (tuNgay != null && !tuNgay.isEmpty()) {
            sql += "AND DATE(dh.NgayDat) >= DATE(?) ";
        }
        if (denNgay != null && !denNgay.isEmpty()) {
            sql += "AND DATE(dh.NgayDat) <= DATE(?) ";
        }
        
        sql += "GROUP BY lm.MaLoai, lm.TenLoai " +
               "ORDER BY TongTien DESC";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            int paramIndex = 1;
            if (tuNgay != null && !tuNgay.isEmpty()) {
                ps.setString(paramIndex++, tuNgay);
            }
            if (denNgay != null && !denNgay.isEmpty()) {
                ps.setString(paramIndex++, denNgay);
            }
            
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
            e.printStackTrace(); // Log error để debug
        }
        
        return result;
    }
    
    // Thống kê nhân viên bán hàng
    public List<ThongKeDTO> thongKeNhanVienBanHang(String tuNgay, String denNgay) {
        List<ThongKeDTO> result = new ArrayList<>();
        String sql = "SELECT nv.HoTen, COUNT(dh.MaDon) as SoDonHang, SUM(dh.TongTien) as DoanhThu " +
                    "FROM donhang dh " +
                    "JOIN nhanvien nv ON dh.MaNV = nv.MaNV " +
                    "WHERE dh.TrangThai = 'dathanhtoan' ";
        
        if (tuNgay != null && !tuNgay.isEmpty()) {
            sql += "AND DATE(dh.NgayDat) >= DATE(?) ";
        }
        if (denNgay != null && !denNgay.isEmpty()) {
            sql += "AND DATE(dh.NgayDat) <= DATE(?) ";
        }
        
        sql += "GROUP BY nv.MaNV, nv.HoTen " +
               "ORDER BY DoanhThu DESC";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            int paramIndex = 1;
            if (tuNgay != null && !tuNgay.isEmpty()) {
                ps.setString(paramIndex++, tuNgay);
            }
            if (denNgay != null && !denNgay.isEmpty()) {
                ps.setString(paramIndex++, denNgay);
            }
            
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
            e.printStackTrace(); // Log error để debug
        }
        
        return result;
    }
    
    // Thống kê doanh thu theo ngày
    public List<ThongKeDTO> thongKeDoanhThuTheoNgay(String tuNgay, String denNgay) {
        List<ThongKeDTO> result = new ArrayList<>();
        String sql = "SELECT DATE(dh.NgayDat) as Ngay, SUM(dh.TongTien) as DoanhThu " +
                    "FROM donhang dh " +
                    "WHERE dh.TrangThai = 'dathanhtoan' ";
        
        if (tuNgay != null && !tuNgay.isEmpty()) {
            sql += "AND DATE(dh.NgayDat) >= DATE(?) ";
        }
        if (denNgay != null && !denNgay.isEmpty()) {
            sql += "AND DATE(dh.NgayDat) <= DATE(?) ";
        }
        
        sql += "GROUP BY DATE(dh.NgayDat) " +
               "ORDER BY Ngay ASC";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            int paramIndex = 1;
            if (tuNgay != null && !tuNgay.isEmpty()) {
                ps.setString(paramIndex++, tuNgay);
            }
            if (denNgay != null && !denNgay.isEmpty()) {
                ps.setString(paramIndex++, denNgay);
            }
            
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
            e.printStackTrace(); // Log error để debug
        }
        
        return result;
    }
    
    // Thống kê doanh thu theo tháng
    public List<ThongKeDTO> thongKeDoanhThuTheoThang(String nam) {
        List<ThongKeDTO> result = new ArrayList<>();
        String sql = "SELECT MONTH(dh.NgayDat) as Thang, SUM(dh.TongTien) as DoanhThu " +
                    "FROM donhang dh " +
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
            String sqlDoanhThu = "SELECT COALESCE(SUM(TongTien), 0) as TongDoanhThu " +
                                "FROM donhang " +
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
            e.printStackTrace(); // Log error để debug
        }
        
        return result;
    }
    
    // Thống kê đơn hàng theo trạng thái
    public List<ThongKeDTO> thongKeDonHangTheoTrangThai() {
        List<ThongKeDTO> result = new ArrayList<>();
        String sql = "SELECT TrangThai, COUNT(*) as SoDonHang " +
                    "FROM donhang " +
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
    
    // Thống kê khách hàng mới theo tháng (dựa trên đơn hàng đầu tiên)
    public List<ThongKeDTO> thongKeKhachHangMoiTheoThang(String nam) {
        List<ThongKeDTO> result = new ArrayList<>();
        String sql = "SELECT MONTH(first_order.NgayDat) as Thang, COUNT(*) as SoKhachHang " +
                    "FROM (SELECT dh.MaKH, MIN(dh.NgayDat) as NgayDat " +
                    "      FROM donhang dh " +
                    "      GROUP BY dh.MaKH) as first_order " +
                    "WHERE YEAR(first_order.NgayDat) = ? " +
                    "GROUP BY MONTH(first_order.NgayDat) " +
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
            e.printStackTrace();
        }
        
        return result;
    }
    
    // Thống kê chi phí nhập hàng theo thời gian
    public List<ThongKeDTO> thongKeChiPhiNhapHang(String tuNgay, String denNgay) {
        List<ThongKeDTO> result = new ArrayList<>();
        String sql = "SELECT DATE(pn.Ngay) as Ngay, SUM(pn.ThanhTien) as ChiPhi " +
                    "FROM phieunhap pn " +
                    "WHERE pn.TrangThai = 'daxacnhan' ";
        
        if (tuNgay != null && !tuNgay.isEmpty()) {
            sql += "AND DATE(pn.Ngay) >= DATE(?) ";
        }
        if (denNgay != null && !denNgay.isEmpty()) {
            sql += "AND DATE(pn.Ngay) <= DATE(?) ";
        }
        
        sql += "GROUP BY DATE(pn.Ngay) " +
               "ORDER BY Ngay ASC";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            int paramIndex = 1;
            if (tuNgay != null && !tuNgay.isEmpty()) {
                ps.setString(paramIndex++, tuNgay);
            }
            if (denNgay != null && !denNgay.isEmpty()) {
                ps.setString(paramIndex++, denNgay);
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ThongKeDTO tk = new ThongKeDTO();
                    tk.setNgay(rs.getString("Ngay"));
                    tk.setDoanhThu(rs.getLong("ChiPhi")); // Dùng doanhThu để lưu chi phí
                    result.add(tk);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return result;
    }
    
    // Thống kê nhà cung cấp được sử dụng nhiều nhất
    public List<ThongKeDTO> thongKeNhaCungCap(String tuNgay, String denNgay) {
        List<ThongKeDTO> result = new ArrayList<>();
        String sql = "SELECT ncc.TenNCC, COUNT(pn.MaPN) as SoPhieuNhap, SUM(pn.ThanhTien) as TongChiPhi " +
                    "FROM phieunhap pn " +
                    "JOIN nhacungcap ncc ON pn.MaNCC = ncc.MaNCC " +
                    "WHERE pn.TrangThai = 'daxacnhan' ";
        
        if (tuNgay != null && !tuNgay.isEmpty()) {
            sql += "AND DATE(pn.Ngay) >= DATE(?) ";
        }
        if (denNgay != null && !denNgay.isEmpty()) {
            sql += "AND DATE(pn.Ngay) <= DATE(?) ";
        }
        
        sql += "GROUP BY ncc.MaNCC, ncc.TenNCC " +
               "ORDER BY TongChiPhi DESC " +
               "LIMIT 10";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            int paramIndex = 1;
            if (tuNgay != null && !tuNgay.isEmpty()) {
                ps.setString(paramIndex++, tuNgay);
            }
            if (denNgay != null && !denNgay.isEmpty()) {
                ps.setString(paramIndex++, denNgay);
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ThongKeDTO tk = new ThongKeDTO();
                    tk.setTenNhaCungCap(rs.getString("TenNCC"));
                    tk.setSoDonHang(rs.getInt("SoPhieuNhap"));
                    tk.setDoanhThu(rs.getLong("TongChiPhi"));
                    result.add(tk);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return result;
    }
    
    // Thống kê nguyên liệu sắp hết
    public List<ThongKeDTO> thongKeNguyenLieuSapHet(int nguong) {
        List<ThongKeDTO> result = new ArrayList<>();
        String sql = "SELECT nl.TenNL, nl.DonVi, kh.SoLuong " +
                    "FROM khohang kh " +
                    "JOIN nguyenlieu nl ON kh.MaNL = nl.MaNL " +
                    "WHERE kh.SoLuong <= ? " +
                    "ORDER BY kh.SoLuong ASC";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, nguong);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ThongKeDTO tk = new ThongKeDTO();
                    tk.setTenMon(rs.getString("TenNL")); // Dùng tenMon để lưu tên nguyên liệu
                    tk.setSoLuongBan(rs.getInt("SoLuong")); // Dùng soLuongBan để lưu số lượng
                    tk.setTenLoaiMon(rs.getString("DonVi")); // Dùng tenLoaiMon để lưu đơn vị
                    result.add(tk);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return result;
    }
    
    // Thống kê top khách hàng VIP
    public List<ThongKeDTO> thongKeKhachHangVIP(String tuNgay, String denNgay) {
        List<ThongKeDTO> result = new ArrayList<>();
        String sql = "SELECT kh.HoTen, COUNT(dh.MaDon) as SoDonHang, SUM(dh.TongTien) as TongTien " +
                    "FROM donhang dh " +
                    "JOIN khachhang kh ON dh.MaKH = kh.MaKH " +
                    "WHERE dh.TrangThai = 'dathanhtoan' ";
        
        if (tuNgay != null && !tuNgay.isEmpty()) {
            sql += "AND DATE(dh.NgayDat) >= DATE(?) ";
        }
        if (denNgay != null && !denNgay.isEmpty()) {
            sql += "AND DATE(dh.NgayDat) <= DATE(?) ";
        }
        
        sql += "GROUP BY kh.MaKH, kh.HoTen " +
               "ORDER BY TongTien DESC " +
               "LIMIT 10";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            int paramIndex = 1;
            if (tuNgay != null && !tuNgay.isEmpty()) {
                ps.setString(paramIndex++, tuNgay);
            }
            if (denNgay != null && !denNgay.isEmpty()) {
                ps.setString(paramIndex++, denNgay);
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ThongKeDTO tk = new ThongKeDTO();
                    tk.setTenKhachHang(rs.getString("HoTen"));
                    tk.setSoDonHang(rs.getInt("SoDonHang"));
                    tk.setTongTien(rs.getLong("TongTien"));
                    result.add(tk);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return result;
    }
    
    // Thống kê lợi nhuận (doanh thu - chi phí nhập hàng)
    public ThongKeDTO thongKeLoiNhuan(String tuNgay, String denNgay) {
        ThongKeDTO result = new ThongKeDTO();
        
        try (Connection conn = DBUtil.getConnection()) {
            // Doanh thu
            String sqlDoanhThu = "SELECT COALESCE(SUM(TongTien), 0) as TongDoanhThu " +
                                "FROM donhang " +
                                "WHERE TrangThai = 'dathanhtoan' ";
            
            if (tuNgay != null && !tuNgay.isEmpty()) {
                sqlDoanhThu += "AND DATE(NgayDat) >= DATE(?) ";
            }
            if (denNgay != null && !denNgay.isEmpty()) {
                sqlDoanhThu += "AND DATE(NgayDat) <= DATE(?) ";
            }
            
            try (PreparedStatement ps = conn.prepareStatement(sqlDoanhThu)) {
                int paramIndex = 1;
                if (tuNgay != null && !tuNgay.isEmpty()) {
                    ps.setString(paramIndex++, tuNgay);
                }
                if (denNgay != null && !denNgay.isEmpty()) {
                    ps.setString(paramIndex++, denNgay);
                }
                
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        result.setDoanhThu(rs.getLong("TongDoanhThu"));
                    }
                }
            }
            
            // Chi phí nhập hàng
            String sqlChiPhi = "SELECT COALESCE(SUM(ThanhTien), 0) as TongChiPhi " +
                              "FROM phieunhap " +
                              "WHERE TrangThai = 'daxacnhan' ";
            
            if (tuNgay != null && !tuNgay.isEmpty()) {
                sqlChiPhi += "AND DATE(Ngay) >= DATE(?) ";
            }
            if (denNgay != null && !denNgay.isEmpty()) {
                sqlChiPhi += "AND DATE(Ngay) <= DATE(?) ";
            }
            
            try (PreparedStatement ps = conn.prepareStatement(sqlChiPhi)) {
                int paramIndex = 1;
                if (tuNgay != null && !tuNgay.isEmpty()) {
                    ps.setString(paramIndex++, tuNgay);
                }
                if (denNgay != null && !denNgay.isEmpty()) {
                    ps.setString(paramIndex++, denNgay);
                }
                
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        long chiPhi = rs.getLong("TongChiPhi");
                        result.setTongTien(result.getDoanhThu() - chiPhi); // Lợi nhuận = doanh thu - chi phí
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return result;
    }
    
    // Thống kê giá trị đơn hàng trung bình
    public ThongKeDTO thongKeGiaTriDonHangTrungBinh(String tuNgay, String denNgay) {
        ThongKeDTO result = new ThongKeDTO();
        String sql = "SELECT AVG(TongTien) as GiaTriTrungBinh, COUNT(*) as SoDonHang " +
                    "FROM donhang " +
                    "WHERE TrangThai = 'dathanhtoan' ";
        
        if (tuNgay != null && !tuNgay.isEmpty()) {
            sql += "AND DATE(NgayDat) >= DATE(?) ";
        }
        if (denNgay != null && !denNgay.isEmpty()) {
            sql += "AND DATE(NgayDat) <= DATE(?) ";
        }
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            int paramIndex = 1;
            if (tuNgay != null && !tuNgay.isEmpty()) {
                ps.setString(paramIndex++, tuNgay);
            }
            if (denNgay != null && !denNgay.isEmpty()) {
                ps.setString(paramIndex++, denNgay);
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    result.setTongTien((long)rs.getDouble("GiaTriTrungBinh"));
                    result.setSoDonHang(rs.getInt("SoDonHang"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return result;
    }
    
    // Thống kê theo giờ trong ngày
    public List<ThongKeDTO> thongKeTheoGio(String tuNgay, String denNgay) {
        List<ThongKeDTO> result = new ArrayList<>();
        String sql = "SELECT HOUR(NgayDat) as Gio, COUNT(*) as SoDonHang, SUM(TongTien) as DoanhThu " +
                    "FROM donhang " +
                    "WHERE TrangThai = 'dathanhtoan' ";
        
        if (tuNgay != null && !tuNgay.isEmpty()) {
            sql += "AND DATE(NgayDat) >= DATE(?) ";
        }
        if (denNgay != null && !denNgay.isEmpty()) {
            sql += "AND DATE(NgayDat) <= DATE(?) ";
        }
        
        sql += "GROUP BY HOUR(NgayDat) " +
               "ORDER BY Gio ASC";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            int paramIndex = 1;
            if (tuNgay != null && !tuNgay.isEmpty()) {
                ps.setString(paramIndex++, tuNgay);
            }
            if (denNgay != null && !denNgay.isEmpty()) {
                ps.setString(paramIndex++, denNgay);
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ThongKeDTO tk = new ThongKeDTO();
                    tk.setNgay(rs.getInt("Gio") + ":00"); // Dùng ngay để lưu giờ
                    tk.setSoDonHang(rs.getInt("SoDonHang"));
                    tk.setDoanhThu(rs.getLong("DoanhThu"));
                    result.add(tk);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return result;
    }
    
    // Lấy tổng giá trị tồn kho
    public long layTongGiaTriTonKho() {
        String sql = "SELECT COALESCE(SUM(kh.SoLuong * nccnl.DonGia), 0) as TongGiaTri " +
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
