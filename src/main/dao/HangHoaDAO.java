package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import database.DBUtil;
import dto.MonDTO;
import dto.NguyenLieuDTO;
import dto.LoaiMonDTO;
import dto.MonNguyenLieuDTO;

public class HangHoaDAO {
    
    // ========== MÓN ==========
    
    // Lấy tất cả món
    public List<MonDTO> layTatCaMon() {
        List<MonDTO> danhSach = new ArrayList<>();
        String sql = "SELECT m.*, lm.TenLoai FROM mon m LEFT JOIN loaimon lm ON m.MaLoai = lm.MaLoai ORDER BY m.MaMon";
        
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                MonDTO mon = new MonDTO();
                mon.setMaMon(rs.getInt("MaMon"));
                mon.setTenMon(rs.getString("TenMon"));
                mon.setGia(rs.getLong("Gia"));
                mon.setTinhTrang(rs.getString("TinhTrang"));
                mon.setMaLoai(rs.getInt("MaLoai"));
                mon.setAnh(rs.getString("Anh"));
                danhSach.add(mon);
            }
        } catch (SQLException e) {
        }
        return danhSach;
    }
    
    // Lấy món theo loại
    public List<MonDTO> layMonTheoLoai(int maLoai) {
        List<MonDTO> danhSach = new ArrayList<>();
        String sql = "SELECT m.*, lm.TenLoai FROM mon m LEFT JOIN loaimon lm ON m.MaLoai = lm.MaLoai WHERE m.MaLoai = ? ORDER BY m.TenMon";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, maLoai);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    MonDTO mon = new MonDTO();
                    mon.setMaMon(rs.getInt("MaMon"));
                    mon.setTenMon(rs.getString("TenMon"));
                    mon.setGia(rs.getLong("Gia"));
                    mon.setTinhTrang(rs.getString("TinhTrang"));
                    mon.setMaLoai(rs.getInt("MaLoai"));
                mon.setAnh(rs.getString("Anh"));
                    danhSach.add(mon);
                }
            }
        } catch (SQLException e) {
        }
        return danhSach;
    }
    
    // Lấy món đang bán theo loại
    public List<MonDTO> layMonDangBanTheoLoai(int maLoai) {
        List<MonDTO> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM mon WHERE MaLoai = ? AND TinhTrang = 'dangban' ORDER BY TenMon";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, maLoai);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    MonDTO mon = new MonDTO();
                    mon.setMaMon(rs.getInt("MaMon"));
                    mon.setTenMon(rs.getString("TenMon"));
                    mon.setGia(rs.getLong("Gia"));
                    mon.setTinhTrang(rs.getString("TinhTrang"));
                    mon.setMaLoai(rs.getInt("MaLoai"));
                    mon.setAnh(rs.getString("Anh"));
                    danhSach.add(mon);
                }
            }
        } catch (SQLException e) {
        }
        return danhSach;
    }
    
    // Lấy topping (loại 4)
    public List<MonDTO> layTopping() {
        List<MonDTO> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM mon WHERE MaLoai = 4 AND TinhTrang = 'dangban' ORDER BY TenMon";
        
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                MonDTO mon = new MonDTO();
                mon.setMaMon(rs.getInt("MaMon"));
                mon.setTenMon(rs.getString("TenMon"));
                mon.setGia(rs.getLong("Gia"));
                mon.setTinhTrang(rs.getString("TinhTrang"));
                mon.setMaLoai(rs.getInt("MaLoai"));
                mon.setAnh(rs.getString("Anh"));
                danhSach.add(mon);
            }
        } catch (SQLException e) {
        }
        return danhSach;
    }
    
    // Tìm kiếm món
    public List<MonDTO> timKiemMon(String searchType, String searchText) {
        List<MonDTO> danhSach = new ArrayList<>();
        String sql = "SELECT m.*, lm.TenLoai FROM mon m LEFT JOIN loaimon lm ON m.MaLoai = lm.MaLoai WHERE ";
        PreparedStatement ps;
        
        try (Connection conn = DBUtil.getConnection()) {
            if (searchType.equals("Tất cả") || searchText.isEmpty()) {
                sql = "SELECT m.*, lm.TenLoai FROM mon m LEFT JOIN loaimon lm ON m.MaLoai = lm.MaLoai ORDER BY m.MaMon";
                ps = conn.prepareStatement(sql);
            } else if (searchType.equals("ID")) {
                sql += "m.MaMon = ? ORDER BY m.MaMon";
                ps = conn.prepareStatement(sql);
                ps.setInt(1, Integer.parseInt(searchText));
            } else if (searchType.equals("Tên")) {
                sql += "m.TenMon LIKE ? ORDER BY m.MaMon";
                ps = conn.prepareStatement(sql);
                ps.setString(1, "%" + searchText + "%");
            } else if (searchType.equals("Loại")) {
                sql += "lm.TenLoai LIKE ? ORDER BY m.MaMon";
                ps = conn.prepareStatement(sql);
                ps.setString(1, "%" + searchText + "%");
            } else {
                sql += "m.TinhTrang LIKE ? ORDER BY m.MaMon";
                ps = conn.prepareStatement(sql);
                ps.setString(1, "%" + searchText + "%");
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    MonDTO mon = new MonDTO();
                    mon.setMaMon(rs.getInt("MaMon"));
                    mon.setTenMon(rs.getString("TenMon"));
                    mon.setGia(rs.getLong("Gia"));
                    mon.setTinhTrang(rs.getString("TinhTrang"));
                    mon.setMaLoai(rs.getInt("MaLoai"));
                mon.setAnh(rs.getString("Anh"));
                mon.setTenLoai(rs.getString("TenLoai"));
                    danhSach.add(mon);
                }
            }
        } catch (SQLException e) {
        }
        return danhSach;
    }
    
    // Lấy món với bộ lọc theo loại và tình trạng
    public List<MonDTO> layMonTheoBoLoc(String tenLoaiSelected, String tinhTrangSelected) {
        List<MonDTO> danhSach = new ArrayList<>();
        List<String> whereConditions = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        
        StringBuilder sql = new StringBuilder(
            "SELECT m.*, lm.TenLoai FROM mon m LEFT JOIN loaimon lm ON m.MaLoai = lm.MaLoai"
        );
        
        if (tenLoaiSelected != null && !tenLoaiSelected.equals("Tất cả loại")) {
            whereConditions.add("lm.TenLoai = ?");
            params.add(tenLoaiSelected);
        }
        
        if (tinhTrangSelected != null && !tinhTrangSelected.equals("Tất cả tình trạng")) {
            whereConditions.add("m.TinhTrang = ?");
            params.add(convertTinhTrangToDatabase(tinhTrangSelected));
        }
        
        if (!whereConditions.isEmpty()) {
            sql.append(" WHERE ").append(String.join(" AND ", whereConditions));
        }
        sql.append(" ORDER BY m.MaMon");
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    MonDTO mon = new MonDTO();
                    mon.setMaMon(rs.getInt("MaMon"));
                    mon.setTenMon(rs.getString("TenMon"));
                    mon.setGia(rs.getLong("Gia"));
                    mon.setTinhTrang(rs.getString("TinhTrang"));
                    mon.setMaLoai(rs.getInt("MaLoai"));
                    mon.setAnh(rs.getString("Anh"));
                    mon.setTenLoai(rs.getString("TenLoai"));
                    danhSach.add(mon);
                }
            }
        } catch (SQLException e) {
        }
        return danhSach;
    }
    
    // Tìm kiếm món mở rộng với nhiều điều kiện
    public List<MonDTO> timKiemMonMoRong(
            String searchType,
            String searchText,
            String tenLoaiSelected,
            String tinhTrangSelected) {
        
        List<MonDTO> danhSach = new ArrayList<>();
        List<String> conditions = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        
        StringBuilder sql = new StringBuilder(
            "SELECT m.*, lm.TenLoai FROM mon m LEFT JOIN loaimon lm ON m.MaLoai = lm.MaLoai"
        );
        
        if (searchText != null && !searchText.isEmpty()) {
            if ("ID".equals(searchType)) {
                conditions.add("m.MaMon = ?");
                try {
                    params.add(Integer.parseInt(searchText));
                } catch (NumberFormatException e) {
                    // Bỏ qua nếu không phải số
                }
            } else if ("Tên".equals(searchType)) {
                conditions.add("m.TenMon LIKE ?");
                params.add("%" + searchText + "%");
            } else if ("Loại".equals(searchType)) {
                conditions.add("lm.TenLoai LIKE ?");
                params.add("%" + searchText + "%");
            } else {
                // Mặc định tìm theo trạng thái hiển thị
                conditions.add("m.TinhTrang LIKE ?");
                params.add("%" + convertTinhTrangToDatabase(searchText) + "%");
            }
        }
        
        if (tenLoaiSelected != null && !tenLoaiSelected.equals("Tất cả loại")) {
            conditions.add("lm.TenLoai = ?");
            params.add(tenLoaiSelected);
        }
        
        if (tinhTrangSelected != null && !tinhTrangSelected.equals("Tất cả tình trạng")) {
            conditions.add("m.TinhTrang = ?");
            params.add(convertTinhTrangToDatabase(tinhTrangSelected));
        }
        
        if (!conditions.isEmpty()) {
            sql.append(" WHERE ").append(String.join(" AND ", conditions));
        }
        sql.append(" ORDER BY m.MaMon");
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    MonDTO mon = new MonDTO();
                    mon.setMaMon(rs.getInt("MaMon"));
                    mon.setTenMon(rs.getString("TenMon"));
                    mon.setGia(rs.getLong("Gia"));
                    mon.setTinhTrang(rs.getString("TinhTrang"));
                    mon.setMaLoai(rs.getInt("MaLoai"));
                    mon.setAnh(rs.getString("Anh"));
                    mon.setTenLoai(rs.getString("TenLoai"));
                    danhSach.add(mon);
                }
            }
        } catch (SQLException e) {
        }
        return danhSach;
    }
    
    // Thêm món mới
    public boolean themMon(MonDTO mon) {
        String sql = "INSERT INTO mon (TenMon, Gia, TinhTrang, MaLoai, Anh) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, mon.getTenMon());
            ps.setLong(2, mon.getGia());
            ps.setString(3, mon.getTinhTrang());
            ps.setInt(4, mon.getMaLoai());
            ps.setString(5, mon.getAnh());
            
            int result = ps.executeUpdate();
            
            if (result > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        mon.setMaMon(rs.getInt(1));
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
        }
        return false;
    }
    
    // Cập nhật món
    public boolean capNhatMon(MonDTO mon) {
        String sql = "UPDATE mon SET TenMon=?, Gia=?, TinhTrang=?, MaLoai=?, Anh=? WHERE MaMon=?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, mon.getTenMon());
            ps.setLong(2, mon.getGia());
            ps.setString(3, mon.getTinhTrang());
            ps.setInt(4, mon.getMaLoai());
            ps.setString(5, mon.getAnh());
            ps.setInt(6, mon.getMaMon());
            
            int result = ps.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
        }
        return false;
    }
    
    // Xóa món
    public boolean xoaMon(int maMon) {
        String sql = "DELETE FROM mon WHERE MaMon = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, maMon);
            int result = ps.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
        }
        return false;
    }
    
    // Kiểm tra ràng buộc khi xóa món
    public String kiemTraRangBuocXoaMon(int maMon) {
        try (Connection conn = DBUtil.getConnection()) {
            // Kiểm tra món trong chi tiết đơn hàng
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT COUNT(*) FROM chitietdonhang WHERE MaMon = ?")) {
                ps.setInt(1, maMon);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        return "Không thể xóa món này vì đã được sử dụng trong đơn hàng!";
                    }
                }
            }
            
            // Kiểm tra món được dùng làm topping
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT COUNT(*) FROM chitietdonhang WHERE MaTopping = ?")) {
                ps.setInt(1, maMon);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        return "Không thể xóa món này vì đã được sử dụng làm topping trong đơn hàng!";
                    }
                }
            }
        } catch (SQLException e) {
            return "Lỗi kiểm tra ràng buộc: " + e.getMessage();
        }
        return null;
    }
    
    // Lấy mã món theo tên
    public int layMaMonTheoTen(String tenMon) {
        String sql = "SELECT MaMon FROM mon WHERE TenMon = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, tenMon);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("MaMon");
                }
            }
        } catch (SQLException e) {
        }
        return -1;
    }
    
    // ========== LOẠI MÓN ==========
    
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
    
    // Lấy loại món theo index
    public LoaiMonDTO layLoaiMonTheoIndex(int index) {
        String sql = "SELECT * FROM loaimon ORDER BY MaLoai LIMIT 1 OFFSET ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, index);
            
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
    
    // ========== NGUYÊN LIỆU ==========
    
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
    
    // ========== MÓN - NGUYÊN LIỆU ==========
    
    // Lấy danh sách nguyên liệu của món
    public List<MonNguyenLieuDTO> layNguyenLieuCuaMon(int maMon) {
        List<MonNguyenLieuDTO> danhSach = new ArrayList<>();
        String sql = "SELECT mnl.*, nl.TenNL, nl.DonVi " +
                    "FROM mon_nguyenlieu mnl " +
                    "JOIN nguyenlieu nl ON mnl.MaNL = nl.MaNL " +
                    "WHERE mnl.MaMon = ? " +
                    "ORDER BY nl.TenNL";
        
        try (Connection conn = DBUtil.getConnection()) {
            // Đảm bảo bảng tồn tại
            createMonNguyenLieuTableIfNotExists(conn);
            
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
        } catch (SQLException e) {
        }
        return danhSach;
    }
    
    // Thêm nguyên liệu vào món
    public boolean themNguyenLieuVaoMon(int maMon, int maNL, int soLuong) {
        String sql = "INSERT INTO mon_nguyenlieu (MaMon, MaNL, SoLuong) VALUES (?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE SoLuong = ?";
        
        try (Connection conn = DBUtil.getConnection()) {
            // Đảm bảo bảng tồn tại
            createMonNguyenLieuTableIfNotExists(conn);
            
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, maMon);
                ps.setInt(2, maNL);
                ps.setInt(3, soLuong);
                ps.setInt(4, soLuong);
                
                int result = ps.executeUpdate();
                return result > 0;
            }
        } catch (SQLException e) {
        }
        return false;
    }
    
    // Thêm nhiều nguyên liệu vào món cùng lúc (tối ưu hiệu suất)
    public boolean themNhieuNguyenLieuVaoMon(Connection conn, int maMon, List<MonNguyenLieuDTO> ingredientsList) throws SQLException {
        String sql = "INSERT INTO mon_nguyenlieu (MaMon, MaNL, SoLuong) VALUES (?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE SoLuong = ?";
        
        // Đảm bảo bảng tồn tại
        createMonNguyenLieuTableIfNotExists(conn);
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (MonNguyenLieuDTO dto : ingredientsList) {
                ps.setInt(1, maMon);
                ps.setInt(2, dto.getMaNL());
                ps.setInt(3, dto.getSoLuong());
                ps.setInt(4, dto.getSoLuong());
                ps.addBatch();
            }
            
            int[] results = ps.executeBatch();
            // Kiểm tra xem tất cả các lệnh có thành công không
            for (int result : results) {
                if (result < 0 && result != Statement.SUCCESS_NO_INFO) {
                    return false;
                }
            }
            return true;
        }
    }
    
    // Xóa nguyên liệu khỏi món
    public boolean xoaNguyenLieuKhoiMon(int maMon, int maNL) {
        String sql = "DELETE FROM mon_nguyenlieu WHERE MaMon = ? AND MaNL = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, maMon);
            ps.setInt(2, maNL);
            
            int result = ps.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
        }
        return false;
    }
    
    // Xóa tất cả nguyên liệu của món
    public boolean xoaTatCaNguyenLieuCuaMon(int maMon) {
        String sql = "DELETE FROM mon_nguyenlieu WHERE MaMon = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, maMon);
            int result = ps.executeUpdate();
            return result >= 0; // >= 0 vì có thể không có nguyên liệu nào
        } catch (SQLException e) {
        }
        return false;
    }
    
    // Cập nhật danh sách nguyên liệu của món (xóa cũ, thêm mới)
    public boolean capNhatNguyenLieuChoMon(int maMon, List<MonNguyenLieuDTO> ingredientsList) {
        try (Connection conn = DBUtil.getConnection()) {
            createMonNguyenLieuTableIfNotExists(conn);
            
            try (PreparedStatement deletePs = conn.prepareStatement("DELETE FROM mon_nguyenlieu WHERE MaMon = ?")) {
                deletePs.setInt(1, maMon);
                deletePs.executeUpdate();
            }
            
            if (ingredientsList == null || ingredientsList.isEmpty()) {
                return true;
            }
            
            return themNhieuNguyenLieuVaoMon(conn, maMon, ingredientsList);
        } catch (SQLException e) {
        }
        return false;
    }
    
    // Tạo bảng mon_nguyenlieu nếu chưa tồn tại
    private void createMonNguyenLieuTableIfNotExists(Connection conn) {
        String sql = "CREATE TABLE IF NOT EXISTS mon_nguyenlieu (" +
                    "MaMon INT NOT NULL, " +
                    "MaNL INT NOT NULL, " +
                    "SoLuong INT NOT NULL DEFAULT 0, " +
                    "PRIMARY KEY (MaMon, MaNL), " +
                    "FOREIGN KEY (MaMon) REFERENCES mon(MaMon) ON DELETE CASCADE, " +
                    "FOREIGN KEY (MaNL) REFERENCES nguyenlieu(MaNL) ON DELETE CASCADE" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci";
        
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            // Ignore if table already exists or other errors
        }
    }
    
    private String convertTinhTrangToDatabase(String tinhTrangUI) {
        if ("Đang bán".equalsIgnoreCase(tinhTrangUI)) {
            return "dangban";
        }
        if ("Tạm ngừng".equalsIgnoreCase(tinhTrangUI)) {
            return "ngungban";
        }
        return tinhTrangUI != null ? tinhTrangUI.toLowerCase() : "";
    }
}
