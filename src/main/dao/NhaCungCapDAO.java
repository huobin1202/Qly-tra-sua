package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import database.DBUtil;
import dto.NhaCungCapDTO;
import dto.NhaCungCapSanPhamDTO;

public class NhaCungCapDAO {
    
    // Lấy tất cả nhà cung cấp
    public List<NhaCungCapDTO> layTatCaNhaCungCap() {
        List<NhaCungCapDTO> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM nhacungcap ORDER BY MaNCC";
        
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                NhaCungCapDTO nhaCungCap = new NhaCungCapDTO();
                nhaCungCap.setMaNCC(rs.getInt("MaNCC"));
                nhaCungCap.setTenNCC(rs.getString("TenNCC"));
                nhaCungCap.setSoDienThoai(rs.getString("SDT"));
                nhaCungCap.setDiaChi(rs.getString("DiaChi"));
                danhSach.add(nhaCungCap);
            }
        } catch (SQLException e) {
        }
        return danhSach;
    }
    
    // Tìm kiếm nhà cung cấp
    public List<NhaCungCapDTO> timKiemNhaCungCap(String searchType, String searchText) {
        List<NhaCungCapDTO> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM nhacungcap WHERE ";
        PreparedStatement ps;
        
        try (Connection conn = DBUtil.getConnection()) {
            if (searchType.equals("Tất cả") || searchText.isEmpty()) {
                sql = "SELECT * FROM nhacungcap ORDER BY MaNCC";
                ps = conn.prepareStatement(sql);
            } else if (searchType.equals("ID")) {
                sql += "MaNCC = ? ORDER BY MaNCC";
                ps = conn.prepareStatement(sql);
                ps.setInt(1, Integer.parseInt(searchText));
            } else {
                sql += "TenNCC LIKE ? ORDER BY MaNCC";
                ps = conn.prepareStatement(sql);
                ps.setString(1, "%" + searchText + "%");
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    NhaCungCapDTO nhaCungCap = new NhaCungCapDTO();
                    nhaCungCap.setMaNCC(rs.getInt("MaNCC"));
                    nhaCungCap.setTenNCC(rs.getString("TenNCC"));
                    nhaCungCap.setSoDienThoai(rs.getString("SDT"));
                    nhaCungCap.setDiaChi(rs.getString("DiaChi"));
                    danhSach.add(nhaCungCap);
                }
            }
        } catch (SQLException e) {
        }
        return danhSach;
    }
    
    // Lấy nhà cung cấp theo mã
    public NhaCungCapDTO layNhaCungCapTheoMa(int maNCC) {
        String sql = "SELECT * FROM nhacungcap WHERE MaNCC = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, maNCC);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    NhaCungCapDTO nhaCungCap = new NhaCungCapDTO();
                    nhaCungCap.setMaNCC(rs.getInt("MaNCC"));
                    nhaCungCap.setTenNCC(rs.getString("TenNCC"));
                    nhaCungCap.setSoDienThoai(rs.getString("SDT"));
                    nhaCungCap.setDiaChi(rs.getString("DiaChi"));
                    return nhaCungCap;
                }
            }
        } catch (SQLException e) {
        }
        return null;
    }
    
    // Thêm nhà cung cấp mới
    public boolean themNhaCungCap(NhaCungCapDTO nhaCungCap) {
        String sql = "INSERT INTO nhacungcap (TenNCC, SDT, DiaChi) VALUES (?, ?, ?)";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, nhaCungCap.getTenNCC());
            ps.setString(2, nhaCungCap.getSoDienThoai());
            ps.setString(3, nhaCungCap.getDiaChi());
            
            int result = ps.executeUpdate();
            
            if (result > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        nhaCungCap.setMaNCC(rs.getInt(1));
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
        }
        return false;
    }
    
    // Cập nhật nhà cung cấp
    public boolean capNhatNhaCungCap(NhaCungCapDTO nhaCungCap) {
        String sql = "UPDATE nhacungcap SET TenNCC=?, SDT=?, DiaChi=? WHERE MaNCC=?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, nhaCungCap.getTenNCC());
            ps.setString(2, nhaCungCap.getSoDienThoai());
            ps.setString(3, nhaCungCap.getDiaChi());
            ps.setInt(4, nhaCungCap.getMaNCC());
            
            int result = ps.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
        }
        return false;
    }
    
    // Xóa nhà cung cấp
    public boolean xoaNhaCungCap(int maNCC) {
        String sql = "DELETE FROM nhacungcap WHERE MaNCC = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, maNCC);
            int result = ps.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
        }
        return false;
    }
    
    // Lấy danh sách nhà cung cấp cho combo box
    public List<NhaCungCapDTO> layDanhSachNhaCungCapChoCombo() {
        List<NhaCungCapDTO> danhSach = new ArrayList<>();
        String sql = "SELECT MaNCC, TenNCC FROM nhacungcap ORDER BY TenNCC";
        
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                NhaCungCapDTO nhaCungCap = new NhaCungCapDTO();
                nhaCungCap.setMaNCC(rs.getInt("MaNCC"));
                nhaCungCap.setTenNCC(rs.getString("TenNCC"));
                danhSach.add(nhaCungCap);
            }
        } catch (SQLException e) {
        }
        return danhSach;
    }
    
    // ========== SẢN PHẨM NHÀ CUNG CẤP ==========
    
    // Lấy sản phẩm theo nhà cung cấp
    public List<NhaCungCapSanPhamDTO> laySanPhamTheoNCC(int maNCC) {
        List<NhaCungCapSanPhamDTO> danhSach = new ArrayList<>();
        String sql = "SELECT ncc.MaNCC, ncc.TenNCC, nl.MaNL, nl.TenNL, nl.DonVi, nccnl.SoLuong, nccnl.DonGia " +
                    "FROM nhacungcap ncc " +
                    "JOIN ncc_nguyenlieu nccnl ON ncc.MaNCC = nccnl.MaNCC " +
                    "JOIN nguyenlieu nl ON nccnl.MaNL = nl.MaNL " +
                    "WHERE ncc.MaNCC = ? " +
                    "ORDER BY nl.TenNL";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, maNCC);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    NhaCungCapSanPhamDTO sanPham = new NhaCungCapSanPhamDTO();
                    sanPham.setMaNCC(rs.getInt("MaNCC"));
                    sanPham.setTenNCC(rs.getString("TenNCC"));
                    sanPham.setMaNL(rs.getInt("MaNL"));
                    sanPham.setTenNL(rs.getString("TenNL"));
                    sanPham.setDonVi(rs.getString("DonVi"));
                    sanPham.setSoLuong(rs.getInt("SoLuong"));
                    sanPham.setDonGia(rs.getLong("DonGia"));
                    danhSach.add(sanPham);
                }
            }
        } catch (SQLException e) {
        }
        return danhSach;
    }
    
    // Lấy tất cả sản phẩm nhà cung cấp
    public List<NhaCungCapSanPhamDTO> layTatCaSanPhamNCC() {
        List<NhaCungCapSanPhamDTO> danhSach = new ArrayList<>();
        String sql = "SELECT ncc.MaNCC, ncc.TenNCC, nl.MaNL, nl.TenNL, nl.DonVi, nccnl.SoLuong, nccnl.DonGia " +
                    "FROM nhacungcap ncc " +
                    "JOIN ncc_nguyenlieu nccnl ON ncc.MaNCC = nccnl.MaNCC " +
                    "JOIN nguyenlieu nl ON nccnl.MaNL = nl.MaNL " +
                    "ORDER BY ncc.TenNCC, nl.TenNL";
        
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                NhaCungCapSanPhamDTO sanPham = new NhaCungCapSanPhamDTO();
                sanPham.setMaNCC(rs.getInt("MaNCC"));
                sanPham.setTenNCC(rs.getString("TenNCC"));
                sanPham.setMaNL(rs.getInt("MaNL"));
                sanPham.setTenNL(rs.getString("TenNL"));
                sanPham.setDonVi(rs.getString("DonVi"));
                sanPham.setSoLuong(rs.getInt("SoLuong"));
                sanPham.setDonGia(rs.getLong("DonGia"));
                danhSach.add(sanPham);
            }
        } catch (SQLException e) {
        }
        return danhSach;
    }
    
    // Thêm sản phẩm cho nhà cung cấp
    public boolean themSanPhamNCC(int maNCC, int maNL, int soLuong, long donGia) {
        String sql = "INSERT INTO ncc_nguyenlieu (MaNCC, MaNL, SoLuong, DonGia) VALUES (?, ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE SoLuong = SoLuong + ?, DonGia = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, maNCC);
            ps.setInt(2, maNL);
            ps.setInt(3, soLuong);
            ps.setLong(4, donGia);
            ps.setInt(5, soLuong);
            ps.setLong(6, donGia);
            
            int result = ps.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
        }
        return false;
    }
    
    // Cập nhật sản phẩm nhà cung cấp
    public boolean capNhatSanPhamNCC(int maNCC, int maNL, int soLuong, long donGia) {
        String sql = "UPDATE ncc_nguyenlieu SET SoLuong = ?, DonGia = ? WHERE MaNCC = ? AND MaNL = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, soLuong);
            ps.setLong(2, donGia);
            ps.setInt(3, maNCC);
            ps.setInt(4, maNL);
            
            int result = ps.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
        }
        return false;
    }
    
    // Xóa sản phẩm nhà cung cấp
    public boolean xoaSanPhamNCC(int maNCC, int maNL) {
        String sql = "DELETE FROM ncc_nguyenlieu WHERE MaNCC = ? AND MaNL = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, maNCC);
            ps.setInt(2, maNL);
            
            int result = ps.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
        }
        return false;
    }
    
    // Kiểm tra ràng buộc trước khi xóa nhà cung cấp
    public String kiemTraRangBuocXoa(int maNCC) {
        try (Connection conn = DBUtil.getConnection()) {
            // Kiểm tra trong bảng phieunhap
            try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM phieunhap WHERE MaNCC=?")) {
                ps.setInt(1, maNCC);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        return "Không thể xóa nhà cung cấp này vì đã có phiếu nhập liên quan!";
                    }
                }
            }
            
            // Kiểm tra trong bảng ncc_nguyenlieu
            try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM ncc_nguyenlieu WHERE MaNCC=?")) {
                ps.setInt(1, maNCC);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        return "Không thể xóa nhà cung cấp này vì đã có nguyên liệu liên quan!";
                    }
                }
            }
        } catch (SQLException e) {
            return "Lỗi kiểm tra ràng buộc: " + e.getMessage();
        }
        return null; // Không có ràng buộc
    }
    
    // Kiểm tra số điện thoại tồn tại
    public boolean kiemTraSDTTonTai(String soDienThoai, Integer maNCCExclude) {
        String sql = "SELECT COUNT(*) FROM nhacungcap WHERE SDT = ?";
        if (maNCCExclude != null) {
            sql += " AND MaNCC != ?";
        }
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, soDienThoai);
            if (maNCCExclude != null) {
                ps.setInt(2, maNCCExclude);
            }
            
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
