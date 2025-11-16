package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import database.DBUtil;
import dto.DonHangDTO;
import dto.ChiTietDonHangDTO;
import dto.MonNguyenLieuDTO;

public class DonHangDAO {
    
    // Lấy tất cả đơn hàng
    public List<DonHangDTO> layTatCaDonHang() {
        List<DonHangDTO> danhSach = new ArrayList<>();
        String sql = "SELECT dh.*, nv.HoTen AS TenNV, kh.HoTen AS TenKH, kh.SDT AS SoDienThoai " +
                    "FROM donhang dh " +
                    "LEFT JOIN nhanvien nv ON dh.MaNV = nv.MaNV " +
                    "LEFT JOIN khachhang kh ON dh.MaKH = kh.MaKH " +
                    "ORDER BY dh.MaDon";
        
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                DonHangDTO donHang = new DonHangDTO();
                donHang.setMaDon(rs.getInt("MaDon"));
                donHang.setMaNV(rs.getInt("MaNV"));
                // MaKH có thể NULL
                int maKH = rs.getInt("MaKH");
                donHang.setMaKH(rs.wasNull() ? null : maKH);
                donHang.setTrangThai(rs.getString("TrangThai"));
                donHang.setNgayDat(rs.getTimestamp("NgayDat"));
                donHang.setTongTien(rs.getLong("TongTien"));
                donHang.setGiamGia(rs.getInt("GiamGia"));
                donHang.setTenNV(rs.getString("TenNV"));
                donHang.setTenKH(rs.getString("TenKH"));
                donHang.setSoDienThoai(rs.getString("SoDienThoai"));
                danhSach.add(donHang);
            }
        } catch (SQLException e) {
        }
        return danhSach;
    }
    
    // Tìm kiếm đơn hàng với điều kiện (không có ngày)
    public List<DonHangDTO> timKiemDonHang(String searchType, String searchText, String trangThai, String ngayTim) {
        // Tham số ngayTim được giữ lại để tương thích nhưng không sử dụng
        List<DonHangDTO> danhSach = new ArrayList<>();
        String baseSql = "SELECT dh.*, nv.HoTen AS TenNV, kh.HoTen AS TenKH, kh.SDT AS SoDienThoai " +
                        "FROM donhang dh " +
                        "LEFT JOIN nhanvien nv ON dh.MaNV = nv.MaNV " +
                        "LEFT JOIN khachhang kh ON dh.MaKH = kh.MaKH ";
        
        List<String> conditions = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        
        try (Connection conn = DBUtil.getConnection()) {
            // Điều kiện tìm kiếm theo ID hoặc Tên NV
            if (searchType != null && !searchText.isEmpty()) {
                if (searchType.equals("ID")) {
                    try {
                        conditions.add("dh.MaDon = ?");
                        params.add(Integer.parseInt(searchText));
                    } catch (NumberFormatException e) {
                        // Nếu không phải số hợp lệ, bỏ qua điều kiện này
                    }
                } else if (searchType.equals("Tên NV")) {
                    conditions.add("nv.HoTen LIKE ?");
                    params.add("%" + searchText + "%");
                }
            }
            
            // Điều kiện tìm kiếm theo trạng thái
            if (trangThai != null && !trangThai.equals("Tất cả")) {
                String trangThaiDB = convertTrangThaiToDatabase(trangThai);
                conditions.add("dh.TrangThai = ?");
                params.add(trangThaiDB);
            }
            
            // Đã bỏ điều kiện tìm kiếm theo ngày
            
            // Xây dựng SQL query
            String sql = baseSql;
            if (!conditions.isEmpty()) {
                sql += "WHERE " + String.join(" AND ", conditions);
            }
            sql += " ORDER BY dh.MaDon";
            
            PreparedStatement ps = conn.prepareStatement(sql);
            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                if (param instanceof Integer) {
                    ps.setInt(i + 1, (Integer) param);
                } else {
                    ps.setString(i + 1, param.toString());
                }
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DonHangDTO donHang = new DonHangDTO();
                    donHang.setMaDon(rs.getInt("MaDon"));
                    donHang.setMaNV(rs.getInt("MaNV"));
                    // MaKH có thể NULL
                    int maKH = rs.getInt("MaKH");
                    donHang.setMaKH(rs.wasNull() ? null : maKH);
                    donHang.setTrangThai(rs.getString("TrangThai"));
                    donHang.setNgayDat(rs.getTimestamp("NgayDat"));
                    donHang.setTongTien(rs.getLong("TongTien"));
                    donHang.setGiamGia(rs.getInt("GiamGia"));
                    donHang.setTenNV(rs.getString("TenNV"));
                    donHang.setTenKH(rs.getString("TenKH"));
                    donHang.setSoDienThoai(rs.getString("SoDienThoai"));
                    danhSach.add(donHang);
                }
            }
        } catch (SQLException e) {
        }
        return danhSach;
    }
    
    // Helper method để chuyển đổi trạng thái từ UI sang database
    private String convertTrangThaiToDatabase(String trangThaiUI) {
        if ("Chưa thanh toán".equals(trangThaiUI)) {
            return "chuathanhtoan";
        } else if ("Đã thanh toán".equals(trangThaiUI)) {
            return "dathanhtoan";
        } else if ("Bị hủy".equals(trangThaiUI)) {
            return "bihuy";
        }
        return trangThaiUI; // Trả về nguyên bản nếu không khớp
    }
    
    // Lấy đơn hàng theo mã
    public DonHangDTO layDonHangTheoMa(int maDon) {
        String sql = "SELECT * FROM donhang WHERE MaDon = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, maDon);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    DonHangDTO donHang = new DonHangDTO();
                    donHang.setMaDon(rs.getInt("MaDon"));
                    donHang.setMaNV(rs.getInt("MaNV"));
                    // MaKH có thể NULL
                    int maKH = rs.getInt("MaKH");
                    donHang.setMaKH(rs.wasNull() ? null : maKH);
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
        String sql = "INSERT INTO donhang (MaNV, MaKH, TrangThai, NgayDat, TongTien, GiamGia) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setInt(1, donHang.getMaNV());
            if (donHang.getMaKH() != null) {
                ps.setInt(2, donHang.getMaKH());
            } else {
                ps.setNull(2, java.sql.Types.INTEGER);
            }
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
            String sqlDonHang = "DELETE FROM donhang WHERE MaDon = ?";
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
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            
            // Kiểm tra trạng thái hiện tại của đơn hàng
            DonHangDTO donHangHienTai = layDonHangTheoMa(maDon);
            if (donHangHienTai == null) {
                return false;
            }
            
            String trangThaiHienTai = donHangHienTai.getTrangThai();
            
            // Cập nhật trạng thái
            String sql = "UPDATE donhang SET TrangThai = ? WHERE MaDon = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, trangThai);
                ps.setInt(2, maDon);
                
                int result = ps.executeUpdate();
                if (result <= 0) {
                    conn.rollback();
                    return false;
                }
            }
            
            // Nếu chuyển sang trạng thái "dathanhtoan" và trước đó chưa thanh toán
            if ("dathanhtoan".equals(trangThai) && !"dathanhtoan".equals(trangThaiHienTai)) {
                if (!truNguyenLieuKhiThanhToan(conn, maDon)) {
                    conn.rollback();
                    return false;
                }
            }
            
            // Nếu chuyển từ "dathanhtoan" sang trạng thái khác, cần hoàn lại nguyên liệu
            if ("dathanhtoan".equals(trangThaiHienTai) && !"dathanhtoan".equals(trangThai)) {
                if (!hoanLaiNguyenLieu(conn, maDon)) {
                    conn.rollback();
                    return false;
                }
            }
            
            conn.commit();
            return true;
        } catch (SQLException e) {
        }
        return false;
    }
    
    // Trừ nguyên liệu khi thanh toán đơn hàng
    private boolean truNguyenLieuKhiThanhToan(Connection conn, int maDon) throws SQLException {
        // Lấy chi tiết đơn hàng
        List<ChiTietDonHangDTO> chiTietList = layChiTietDonHang(conn, maDon);
        
        // Map để tổng hợp nguyên liệu cần trừ (MaNL -> tổng số lượng cần trừ)
        java.util.Map<Integer, Integer> nguyenLieuCanTru = new java.util.HashMap<>();
        
        // Duyệt qua từng món trong đơn hàng
        for (ChiTietDonHangDTO chiTiet : chiTietList) {
            int maMon = chiTiet.getMaMon();
            int soLuongMon = chiTiet.getSoLuong();
            
            // Lấy danh sách nguyên liệu của món
            List<MonNguyenLieuDTO> nguyenLieuList = layNguyenLieuCuaMon(conn, maMon);
            
            // Tính tổng nguyên liệu cần trừ (nhân với số lượng món)
            for (MonNguyenLieuDTO nl : nguyenLieuList) {
                int maNL = nl.getMaNL();
                int soLuongNL = nl.getSoLuong() * soLuongMon;
                
                // Cộng dồn vào map
                nguyenLieuCanTru.put(maNL, 
                    nguyenLieuCanTru.getOrDefault(maNL, 0) + soLuongNL);
            }
        }
        
        // Trừ nguyên liệu trong kho
        for (java.util.Map.Entry<Integer, Integer> entry : nguyenLieuCanTru.entrySet()) {
            int maNL = entry.getKey();
            int soLuongCanTru = entry.getValue();
            
            // Kiểm tra tồn kho
            String checkSql = "SELECT SoLuong FROM khohang WHERE MaNL = ?";
            try (PreparedStatement ps = conn.prepareStatement(checkSql)) {
                ps.setInt(1, maNL);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int tonKho = rs.getInt("SoLuong");
                        if (tonKho < soLuongCanTru) {
                            throw new SQLException("Không đủ nguyên liệu! Nguyên liệu mã " + maNL + 
                                " chỉ còn " + tonKho + " nhưng cần " + soLuongCanTru);
                        }
                    } else {
                        throw new SQLException("Không tìm thấy nguyên liệu mã " + maNL + " trong kho");
                    }
                }
            }
            
            // Trừ số lượng
            String updateSql = "UPDATE khohang SET SoLuong = SoLuong - ? WHERE MaNL = ?";
            try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                ps.setInt(1, soLuongCanTru);
                ps.setInt(2, maNL);
                int result = ps.executeUpdate();
                if (result <= 0) {
                    throw new SQLException("Lỗi khi trừ nguyên liệu mã " + maNL);
                }
            }
        }
        
        return true;
    }
    
    // Hoàn lại nguyên liệu khi hủy/hoàn đơn đã thanh toán
    private boolean hoanLaiNguyenLieu(Connection conn, int maDon) throws SQLException {
        // Logic tương tự nhưng cộng thêm vào kho
        List<ChiTietDonHangDTO> chiTietList = layChiTietDonHang(conn, maDon);
        
        java.util.Map<Integer, Integer> nguyenLieuCanHoan = new java.util.HashMap<>();
        
        for (ChiTietDonHangDTO chiTiet : chiTietList) {
            int maMon = chiTiet.getMaMon();
            int soLuongMon = chiTiet.getSoLuong();
            
            List<MonNguyenLieuDTO> nguyenLieuList = layNguyenLieuCuaMon(conn, maMon);
            
            for (MonNguyenLieuDTO nl : nguyenLieuList) {
                int maNL = nl.getMaNL();
                int soLuongNL = nl.getSoLuong() * soLuongMon;
                
                nguyenLieuCanHoan.put(maNL, 
                    nguyenLieuCanHoan.getOrDefault(maNL, 0) + soLuongNL);
            }
        }
        
        // Hoàn lại nguyên liệu vào kho
        for (java.util.Map.Entry<Integer, Integer> entry : nguyenLieuCanHoan.entrySet()) {
            int maNL = entry.getKey();
            int soLuongCanHoan = entry.getValue();
            
            String updateSql = "UPDATE khohang SET SoLuong = SoLuong + ? WHERE MaNL = ?";
            try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                ps.setInt(1, soLuongCanHoan);
                ps.setInt(2, maNL);
                ps.executeUpdate();
            }
        }
        
        return true;
    }
    
    // Lấy nguyên liệu của món (helper method)
    private List<MonNguyenLieuDTO> layNguyenLieuCuaMon(Connection conn, int maMon) throws SQLException {
        List<MonNguyenLieuDTO> danhSach = new ArrayList<>();
        String sql = "SELECT mnl.*, nl.TenNL, nl.DonVi " +
                    "FROM mon_nguyenlieu mnl " +
                    "JOIN nguyenlieu nl ON mnl.MaNL = nl.MaNL " +
                    "WHERE mnl.MaMon = ? " +
                    "ORDER BY nl.TenNL";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maMon);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    MonNguyenLieuDTO dto = new MonNguyenLieuDTO();
                    dto.setMaMon(rs.getInt("MaMon"));
                    dto.setMaNL(rs.getInt("MaNL"));
                    dto.setTenNL(rs.getString("TenNL"));
                    dto.setSoLuong(rs.getInt("SoLuong"));
                    dto.setDonVi(rs.getString("DonVi"));
                    danhSach.add(dto);
                }
            }
        }
        return danhSach;
    }
    
    // ========== CHI TIẾT ĐƠN HÀNG ==========
    // Lấy chi tiết đơn hàng
    public List<ChiTietDonHangDTO> layChiTietDonHang(int maDon) {
        try (Connection conn = DBUtil.getConnection()) {
            return layChiTietDonHang(conn, maDon);
        } catch (SQLException e) {
            return new ArrayList<>();
        }
    }
    
    // Lấy chi tiết đơn hàng (với Connection để dùng trong transaction)
    private List<ChiTietDonHangDTO> layChiTietDonHang(Connection conn, int maDon) throws SQLException {
        List<ChiTietDonHangDTO> danhSach = new ArrayList<>();
        String sql = "SELECT ctdh.*, m1.TenMon AS TenMon, m2.TenMon AS TenTopping " +
                    "FROM chitietdonhang ctdh " +
                    "LEFT JOIN mon m1 ON ctdh.MaMon = m1.MaMon " +
                    "LEFT JOIN mon m2 ON ctdh.MaTopping = m2.MaMon " +
                    "WHERE ctdh.MaDon = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
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
                    chiTiet.setTenMon(rs.getString("TenMon"));
                    chiTiet.setTenTopping(rs.getString("TenTopping"));
                    danhSach.add(chiTiet);
                }
            }
        }
        return danhSach;
    }

    // Lấy thông tin đơn hàng với tên nhân viên và tên khách hàng
    public DonHangDTO layDonHangVoiTenNV(int maDon) {
        String sql = "SELECT dh.*, nv.HoTen AS TenNV, kh.HoTen AS TenKH, kh.SDT AS SoDienThoai " +
                    "FROM donhang dh " +
                    "LEFT JOIN nhanvien nv ON dh.MaNV = nv.MaNV " +
                    "LEFT JOIN khachhang kh ON dh.MaKH = kh.MaKH " +
                    "WHERE dh.MaDon = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, maDon);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    DonHangDTO donHang = new DonHangDTO();
                    donHang.setMaDon(rs.getInt("MaDon"));
                    donHang.setMaNV(rs.getInt("MaNV"));
                    // MaKH có thể NULL
                    int maKH = rs.getInt("MaKH");
                    donHang.setMaKH(rs.wasNull() ? null : maKH);
                    donHang.setTrangThai(rs.getString("TrangThai"));
                    donHang.setNgayDat(rs.getTimestamp("NgayDat"));
                    donHang.setTongTien(rs.getLong("TongTien"));
                    donHang.setGiamGia(rs.getInt("GiamGia"));
                    donHang.setTenNV(rs.getString("TenNV"));
                    donHang.setTenKH(rs.getString("TenKH"));
                    donHang.setSoDienThoai(rs.getString("SoDienThoai"));
                    return donHang;
                }
            }
        } catch (SQLException e) {
        }
        return null;
    }
    
    // Method để chuẩn hóa trạng thái đơn hàng trong database
    public boolean chuanHoaTrangThaiDonHang() {
        String sql1 = "UPDATE donhang SET TrangThai = 'chuathanhtoan' WHERE TrangThai = '0'";
        String sql2 = "UPDATE donhang SET TrangThai = 'dathanhtoan' WHERE TrangThai = '1'";
        String sql3 = "UPDATE donhang SET TrangThai = 'bihuy' WHERE TrangThai = '2'";
        
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
