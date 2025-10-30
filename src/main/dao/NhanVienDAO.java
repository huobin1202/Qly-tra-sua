package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import database.DBUtil;
import dto.NhanVienDTO;
import dto.NhanVienThuongDTO;
import dto.NhanVienQuanLyDTO;

public class NhanVienDAO {
    
    // Lấy tất cả nhân viên
    public List<NhanVienDTO> layTatCaNhanVien() {
        List<NhanVienDTO> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM nhanvien ORDER BY MaNV";
        
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                String chucVuDB = rs.getString("ChucVu");
                NhanVienDTO nhanVien;
                if (chucVuDB != null && chucVuDB.trim().equalsIgnoreCase("quanly")) {
                    nhanVien = new dto.NhanVienQuanLyDTO(
                        rs.getInt("MaNV"),
                        rs.getString("TaiKhoan"),
                        rs.getString("MatKhau"),
                        rs.getString("HoTen"),
                        rs.getString("SDT"),
                        rs.getTimestamp("NgayVaoLam"),
                        rs.getLong("Luong"),
                        rs.getString("TrangThai")
                    );
                } else {
                    nhanVien = new dto.NhanVienThuongDTO(
                        rs.getInt("MaNV"),
                        rs.getString("TaiKhoan"),
                        rs.getString("MatKhau"),
                        rs.getString("HoTen"),
                        rs.getString("SDT"),
                        rs.getTimestamp("NgayVaoLam"),
                        rs.getLong("Luong"),
                        rs.getString("TrangThai")
                    );
                }
                danhSach.add(nhanVien);
            }
        } catch (SQLException e) {
        }
        return danhSach;
    }
    
    // Tìm kiếm nhân viên
    public List<NhanVienDTO> timKiemNhanVien(String searchType, String searchText) {
        List<NhanVienDTO> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM nhanvien WHERE ";
        PreparedStatement ps;
        
        try (Connection conn = DBUtil.getConnection()) {
            if (searchType.equals("Tất cả") || searchText.isEmpty()) {
                sql = "SELECT * FROM nhanvien ORDER BY MaNV";
                ps = conn.prepareStatement(sql);
            } else if (searchType.equals("ID")) {
                sql += "MaNV = ? ORDER BY MaNV";
                ps = conn.prepareStatement(sql);
                ps.setInt(1, Integer.parseInt(searchText));
            } else if (searchType.equals("Tài khoản")) {
                sql += "TaiKhoan LIKE ? ORDER BY MaNV";
                ps = conn.prepareStatement(sql);
                ps.setString(1, "%" + searchText + "%");
            } else if (searchType.equals("Họ tên")) {
                sql += "HoTen LIKE ? ORDER BY MaNV";
                ps = conn.prepareStatement(sql);
                ps.setString(1, "%" + searchText + "%");
            } else {
                sql += "ChucVu LIKE ? ORDER BY MaNV";
                ps = conn.prepareStatement(sql);
                ps.setString(1, "%" + searchText + "%");
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String chucVuDB = rs.getString("ChucVu");
                    NhanVienDTO nhanVien;
                    if (chucVuDB != null && chucVuDB.trim().equalsIgnoreCase("quanly")) {
                        nhanVien = new dto.NhanVienQuanLyDTO(
                            rs.getInt("MaNV"),
                            rs.getString("TaiKhoan"),
                            rs.getString("MatKhau"),
                            rs.getString("HoTen"),
                            rs.getString("SDT"),
                            rs.getTimestamp("NgayVaoLam"),
                            rs.getLong("Luong"),
                            rs.getString("TrangThai")
                        );
                    } else {
                        nhanVien = new dto.NhanVienThuongDTO(
                            rs.getInt("MaNV"),
                            rs.getString("TaiKhoan"),
                            rs.getString("MatKhau"),
                            rs.getString("HoTen"),
                            rs.getString("SDT"),
                            rs.getTimestamp("NgayVaoLam"),
                            rs.getLong("Luong"),
                            rs.getString("TrangThai")
                        );
                    }
                    danhSach.add(nhanVien);
                }
            }
        } catch (SQLException e) {
        }
        return danhSach;
    }
    
    // Lấy nhân viên theo mã
    public NhanVienDTO layNhanVienTheoMa(int maNV) {
        String sql = "SELECT * FROM nhanvien WHERE MaNV = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, maNV);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String chucVuDB = rs.getString("ChucVu");
                    NhanVienDTO nhanVien;
                    if (chucVuDB != null && chucVuDB.trim().equalsIgnoreCase("quanly")) {
                        nhanVien = new dto.NhanVienQuanLyDTO(
                            rs.getInt("MaNV"),
                            rs.getString("TaiKhoan"),
                            rs.getString("MatKhau"),
                            rs.getString("HoTen"),
                            rs.getString("SDT"),
                            rs.getTimestamp("NgayVaoLam"),
                            rs.getLong("Luong"),
                            rs.getString("TrangThai")
                        );
                    } else {
                        nhanVien = new dto.NhanVienThuongDTO(
                            rs.getInt("MaNV"),
                            rs.getString("TaiKhoan"),
                            rs.getString("MatKhau"),
                            rs.getString("HoTen"),
                            rs.getString("SDT"),
                            rs.getTimestamp("NgayVaoLam"),
                            rs.getLong("Luong"),
                            rs.getString("TrangThai")
                        );
                    }
                    return nhanVien;
                }
            }
        } catch (SQLException e) {
        }
        return null;
    }
    
    // Xác thực đăng nhập
    public NhanVienDTO xacThucDangNhap(String taiKhoan, String matKhau) {
        String sql = "SELECT * FROM nhanvien WHERE TaiKhoan = ? AND MatKhau = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, taiKhoan);
            ps.setString(2, matKhau);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String chucVuDB = rs.getString("ChucVu");
                    NhanVienDTO nhanVien;
                    if (chucVuDB != null && chucVuDB.trim().equalsIgnoreCase("quanly")) {
                        nhanVien = new dto.NhanVienQuanLyDTO(
                            rs.getInt("MaNV"),
                            rs.getString("TaiKhoan"),
                            rs.getString("MatKhau"),
                            rs.getString("HoTen"),
                            rs.getString("SDT"),
                            rs.getTimestamp("NgayVaoLam"),
                            rs.getLong("Luong"),
                            rs.getString("TrangThai")
                        );
                    } else {
                        nhanVien = new dto.NhanVienThuongDTO(
                            rs.getInt("MaNV"),
                            rs.getString("TaiKhoan"),
                            rs.getString("MatKhau"),
                            rs.getString("HoTen"),
                            rs.getString("SDT"),
                            rs.getTimestamp("NgayVaoLam"),
                            rs.getLong("Luong"),
                            rs.getString("TrangThai")
                        );
                    }
                    return nhanVien;
                }
            }
        } catch (SQLException e) {
        }
        return null;
    }
    
    // Thêm nhân viên mới
    public boolean themNhanVien(NhanVienDTO nhanVien) {
        String sql = "INSERT INTO nhanvien (TaiKhoan, MatKhau, HoTen, SoDienThoai, NgayVaoLam, ChucVu, Luong) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, nhanVien.getTaiKhoan());
            ps.setString(2, nhanVien.getMatKhau());
            ps.setString(3, nhanVien.getHoTen());
                    ps.setString(4, nhanVien.getSoDienThoai());
            ps.setTimestamp(5, nhanVien.getNgayVaoLam());
            ps.setString(6, nhanVien.getChucVu());
            ps.setLong(7, nhanVien.getLuong());
            
            int result = ps.executeUpdate();
            
            if (result > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        nhanVien.setMaNV(rs.getInt(1));
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
        }
        return false;
    }
    
    // Cập nhật nhân viên
    public boolean capNhatNhanVien(NhanVienDTO nhanVien) {
        String sql = "UPDATE nhanvien SET TaiKhoan=?, MatKhau=?, HoTen=?, SoDienThoai=?, NgayVaoLam=?, ChucVu=?, Luong=? WHERE MaNV=?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, nhanVien.getTaiKhoan());
            ps.setString(2, nhanVien.getMatKhau());
            ps.setString(3, nhanVien.getHoTen());
                    ps.setString(4, nhanVien.getSoDienThoai());
            ps.setTimestamp(5, nhanVien.getNgayVaoLam());
            ps.setString(6, nhanVien.getChucVu());
            ps.setLong(7, nhanVien.getLuong());
            ps.setInt(8, nhanVien.getMaNV());
            
            int result = ps.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
        }
        return false;
    }
    
    // Xóa nhân viên
    public boolean xoaNhanVien(int maNV) {
        String sql = "DELETE FROM nhanvien WHERE MaNV = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, maNV);
            int result = ps.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
        }
        return false;
    }
    
    // Kiểm tra tài khoản tồn tại
    public boolean kiemTraTaiKhoanTonTai(String taiKhoan) {
        String sql = "SELECT COUNT(*) FROM nhanvien WHERE TaiKhoan = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, taiKhoan);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
        }
        return false;
    }
    
    // Lấy danh sách nhân viên cho combo box
    public List<NhanVienDTO> layDanhSachNhanVienChoCombo() {
        List<NhanVienDTO> danhSach = new ArrayList<>();
        String sql = "SELECT MaNV, HoTen FROM nhanvien ORDER BY HoTen";
        
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                NhanVienDTO nhanVien = new NhanVienThuongDTO(
                    rs.getInt("MaNV"),
                    "", // taiKhoan để trống
                    "", // matKhau để trống
                    rs.getString("HoTen"),
                    "", // sdt để trống
                    null, // ngayvaoLam null
                    0, // lương=0
                    "danglam" // trạng thái mặc định
                );
                danhSach.add(nhanVien);
            }
        } catch (SQLException e) {
        }
        return danhSach;
    }
    
    // Method để chuẩn hóa chức vụ nhân viên trong database
    public boolean chuanHoaChucVuNhanVien() {
        String sql1 = "UPDATE nhanvien SET ChucVu = 'nhanvien' WHERE ChucVu = 'Nhân viên'";
        String sql2 = "UPDATE nhanvien SET ChucVu = 'quanly' WHERE ChucVu = 'Quản lý'";
        String sql3 = "UPDATE nhanvien SET ChucVu = 'nghiviec' WHERE ChucVu = 'Nghỉ việc'";
        
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
