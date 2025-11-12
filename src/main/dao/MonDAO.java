package dao;

import database.DBUtil;
import dto.MonDTO;
import dto.MonViewDTO;
import dto.MonNguyenLieuDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO phục vụ cho các thao tác liên quan tới món trong giao diện quản lý hàng hóa.
 */
public class MonDAO {

    private static final String SELECT_BASE =
        "SELECT m.MaMon, m.TenMon, m.Gia, m.TinhTrang, l.TenLoai, m.Anh " +
        "FROM mon m LEFT JOIN loaimon l ON m.MaLoai = l.MaLoai";

    public List<MonViewDTO> layDanhSachMon(String tenLoaiFilter, String tinhTrangFilter) {
        List<MonViewDTO> danhSach = new ArrayList<>();
        StringBuilder sql = new StringBuilder(SELECT_BASE);
        List<Object> params = new ArrayList<>();

        List<String> where = new ArrayList<>();
        if (tenLoaiFilter != null && !tenLoaiFilter.equals("Tất cả loại")) {
            where.add("l.TenLoai = ?");
            params.add(tenLoaiFilter);
        }
        if (tinhTrangFilter != null && !tinhTrangFilter.equals("Tất cả tình trạng")) {
            where.add("m.TinhTrang = ?");
            params.add(tenTrangThaiToDb(tinhTrangFilter));
        }

        if (!where.isEmpty()) {
            sql.append(" WHERE ").append(String.join(" AND ", where));
        }
        sql.append(" ORDER BY m.MaMon");

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    danhSach.add(mapToMonView(rs));
                }
            }
        } catch (SQLException e) {
        }
        return danhSach;
    }

    public List<MonViewDTO> timKiemMon(String searchType, String searchText,
                                       String tenLoaiFilter, String tinhTrangFilter) {
        List<MonViewDTO> danhSach = new ArrayList<>();
        StringBuilder sql = new StringBuilder(SELECT_BASE).append(" WHERE ");
        List<Object> params = new ArrayList<>();
        List<String> conditions = new ArrayList<>();

        if (searchType != null && searchText != null && !searchText.isEmpty()) {
            switch (searchType) {
                case "ID" -> {
                    conditions.add("m.MaMon = ?");
                    params.add(Integer.parseInt(searchText));
                }
                case "Tên" -> {
                    conditions.add("m.TenMon LIKE ?");
                    params.add("%" + searchText + "%");
                }
                default -> {
                    conditions.add("m.TinhTrang LIKE ?");
                    params.add("%" + tenTrangThaiToDb(searchText) + "%");
                }
            }
        } else {
            conditions.add("1=1");
        }

        if (tenLoaiFilter != null && !tenLoaiFilter.equals("Tất cả loại")) {
            conditions.add("l.TenLoai = ?");
            params.add(tenLoaiFilter);
        }
        if (tinhTrangFilter != null && !tinhTrangFilter.equals("Tất cả tình trạng")) {
            conditions.add("m.TinhTrang = ?");
            params.add(tenTrangThaiToDb(tinhTrangFilter));
        }

        sql.append(String.join(" AND ", conditions));
        sql.append(" ORDER BY m.MaMon");

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

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
                    danhSach.add(mapToMonView(rs));
                }
            }
        } catch (SQLException | NumberFormatException e) {
        }
        return danhSach;
    }

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
                    }
                }
                return true;
            }
        } catch (SQLException e) {
        }
        return false;
    }

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

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
        }
        return false;
    }

    public boolean xoaMon(int maMon) {
        String sql = "DELETE FROM mon WHERE MaMon = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maMon);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
        }
        return false;
    }

    public boolean kiemTraTonTaiTrongChiTietDonHang(int maMon) {
        return demTheoSql("SELECT COUNT(*) FROM chitietdonhang WHERE MaMon = ?", maMon) > 0;
    }

    public boolean kiemTraTonTaiTrongChiTietDonHangAsTopping(int maMon) {
        return demTheoSql("SELECT COUNT(*) FROM chitietdonhang WHERE MaTopping = ?", maMon) > 0;
    }

    // Kiểm tra ràng buộc khi xóa món (trả về message lỗi nếu có)
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

    public List<Integer> layDanhSachMaNguyenLieuCuaMon(int maMon) {
        List<Integer> danhSach = new ArrayList<>();
        String sql = "SELECT MaNL FROM mon_nguyenlieu WHERE MaMon = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maMon);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    danhSach.add(rs.getInt("MaNL"));
                }
            }
        } catch (SQLException e) {
        }
        return danhSach;
    }

    public boolean xoaTatCaNguyenLieuCuaMon(int maMon) {
        String sql = "DELETE FROM mon_nguyenlieu WHERE MaMon = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maMon);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
        }
        return false;
    }

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

    public boolean chuanHoaTrangThaiMon() {
        String sqlDangBan = "UPDATE mon SET TinhTrang = 'dangban' WHERE TinhTrang IN ('Đang bán', 'Dang ban', 'dang ban')";
        String sqlNgungBan = "UPDATE mon SET TinhTrang = 'ngungban' WHERE TinhTrang IN ('Ngừng bán', 'Ngung ban', 'ngung ban')";
        
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            
            try (PreparedStatement ps = conn.prepareStatement(sqlDangBan)) {
                ps.executeUpdate();
            }
            
            try (PreparedStatement ps = conn.prepareStatement(sqlNgungBan)) {
                ps.executeUpdate();
            }
            
            conn.commit();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    private MonViewDTO mapToMonView(ResultSet rs) throws SQLException {
        MonViewDTO dto = new MonViewDTO();
        dto.setMaMon(rs.getInt("MaMon"));
        dto.setTenMon(rs.getString("TenMon"));
        dto.setGia(rs.getLong("Gia"));
        dto.setTinhTrang(rs.getString("TinhTrang"));
        dto.setTenLoai(rs.getString("TenLoai"));
        dto.setAnh(rs.getString("Anh"));
        return dto;
    }

    private long demTheoSql(String sql, int maMon) {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maMon);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
        }
        return 0;
    }

    private String tenTrangThaiToDb(String trangThaiUI) {
        if (trangThaiUI == null) return null;
        return switch (trangThaiUI.toLowerCase()) {
            case "đang bán", "dang ban", "dangban" -> "dangban";
            case "tạm ngừng", "tam ngung", "ngungban" -> "ngungban";
            default -> trangThaiUI;
        };
    }
}
