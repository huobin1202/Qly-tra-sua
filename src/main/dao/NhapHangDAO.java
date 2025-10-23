package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import controller.IQuanLyPhieuNhap;
import database.DBUtil;
import dto.NhapHangDTO;
import dto.ChiTietNhapHangDTO;

public class NhapHangDAO implements IQuanLyPhieuNhap {
    
    // Tạo phiếu nhập mới với kiểm tra ràng buộc trong Java
    @Override
    public boolean taoPhieuNhap(NhapHangDTO phieuNhap) {
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            
            // 1. Kiểm tra nhân viên tồn tại
            if (!kiemTraNhanVienTonTai(phieuNhap.getMaNV())) {
                System.out.println("❌ Nhân viên không tồn tại!");
                return false;
            }
            
            // 2. Kiểm tra nhà cung cấp tồn tại
            if (!kiemTraNhaCungCapTonTai(phieuNhap.getMaNCC())) {
                System.out.println("❌ Nhà cung cấp không tồn tại!");
                return false;
            }
            
            // 3. Tạo phiếu nhập mới
            String sql = "INSERT INTO phieunhap (MaNV, MaNCC, Ngay, GhiChu, ThanhTien, TrangThai) VALUES (?, ?, ?, ?, ?, ?)";
            
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, phieuNhap.getMaNV());
                ps.setInt(2, phieuNhap.getMaNCC());
                ps.setString(3, phieuNhap.getNgay());
                ps.setString(4, phieuNhap.getGhiChu());
                ps.setLong(5, phieuNhap.getThanhTien());
                ps.setString(6, phieuNhap.getTrangThaiString());
                
                int result = ps.executeUpdate();
                
                if (result > 0) {
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) {
                            phieuNhap.setMaPN(rs.getInt(1));
                            conn.commit();
                            System.out.println("✅ Tạo phiếu nhập thành công! Mã phiếu: " + phieuNhap.getMaPN());
                            return true;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ Lỗi khi tạo phiếu nhập: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    // Kiểm tra nhân viên tồn tại
    private boolean kiemTraNhanVienTonTai(int maNV) {
        String sql = "SELECT COUNT(*) FROM nhanvien WHERE MaNV = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maNV);
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
    
    // Kiểm tra nhà cung cấp tồn tại
    private boolean kiemTraNhaCungCapTonTai(int maNCC) {
        String sql = "SELECT COUNT(*) FROM nhacungcap WHERE MaNCC = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maNCC);
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
    
    // Lấy phiếu nhập theo mã
    @Override
    public NhapHangDTO layPhieuNhapTheoMa(int maPN) {
        String sql = "SELECT * FROM phieunhap WHERE MaPN = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, maPN);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new NhapHangDTO(
                        rs.getInt("MaPN"),
                        rs.getInt("MaNV"),
                        rs.getInt("MaNCC"),
                        rs.getString("Ngay"),
                        rs.getString("GhiChu"),
                        rs.getLong("ThanhTien"),
                        rs.getString("TrangThai")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // Lấy tất cả phiếu nhập
    @Override
    public List<NhapHangDTO> layTatCaPhieuNhap() {
        List<NhapHangDTO> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM phieunhap ORDER BY Ngay DESC";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                danhSach.add(new NhapHangDTO(
                    rs.getInt("MaPN"),
                    rs.getInt("MaNV"),
                    rs.getInt("MaNCC"),
                    rs.getString("Ngay"),
                    rs.getString("GhiChu"),
                    rs.getLong("ThanhTien"),
                    rs.getString("TrangThai")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSach;
    }
    
    // Tìm kiếm phiếu nhập theo trạng thái
    @Override
    public List<NhapHangDTO> timPhieuNhapTheoTrangThai(String trangThai) {
        List<NhapHangDTO> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM phieunhap WHERE TrangThai = ? ORDER BY Ngay DESC";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, trangThai);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    danhSach.add(new NhapHangDTO(
                        rs.getInt("MaPN"),
                        rs.getInt("MaNV"),
                        rs.getInt("MaNCC"),
                        rs.getString("Ngay"),
                        rs.getString("GhiChu"),
                        rs.getLong("ThanhTien"),
                        rs.getString("TrangThai")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSach;
    }
    
    // Tìm kiếm phiếu nhập theo nhà cung cấp
    @Override
    public List<NhapHangDTO> timPhieuNhapTheoNCC(int maNCC) {
        List<NhapHangDTO> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM phieunhap WHERE MaNCC = ? ORDER BY Ngay DESC";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, maNCC);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    danhSach.add(new NhapHangDTO(
                        rs.getInt("MaPN"),
                        rs.getInt("MaNV"),
                        rs.getInt("MaNCC"),
                        rs.getString("Ngay"),
                        rs.getString("GhiChu"),
                        rs.getLong("ThanhTien"),
                        rs.getString("TrangThai")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSach;
    }
    
    // Cập nhật phiếu nhập (chỉ khi chưa xác nhận)
    @Override
    public boolean capNhatPhieuNhap(NhapHangDTO phieuNhap) {
        // Kiểm tra trạng thái trước khi cập nhật
        NhapHangDTO phieuHienTai = layPhieuNhapTheoMa(phieuNhap.getMaPN());
        if (phieuHienTai != null && phieuHienTai.isDaXacNhan()) {
            System.out.println("Không thể sửa phiếu nhập đã được xác nhận!");
            return false;
        }
        
        String sql = "UPDATE phieunhap SET MaNCC = ?, GhiChu = ?, ThanhTien = ? WHERE MaPN = ? AND TrangThai = 'chuaxacnhan'";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, phieuNhap.getMaNCC());
            ps.setString(2, phieuNhap.getGhiChu());
            ps.setLong(3, phieuNhap.getThanhTien());
            ps.setInt(4, phieuNhap.getMaPN());
            
            int result = ps.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Xác nhận phiếu nhập với kiểm tra ràng buộc trong Java
    @Override
    public boolean xacNhanPhieuNhap(int maPN) {
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            
            // 1. Kiểm tra phiếu nhập tồn tại
            NhapHangDTO phieuHienTai = layPhieuNhapTheoMa(maPN);
            if (phieuHienTai == null) {
                System.out.println("❌ Phiếu nhập không tồn tại!");
                return false;
            }
            
            // 2. Kiểm tra trạng thái hiện tại
            if (phieuHienTai.isDaXacNhan()) {
                System.out.println("❌ Phiếu nhập đã được xác nhận rồi!");
                return false;
            }
            
            // 3. Xác nhận phiếu nhập
            String sql = "UPDATE phieunhap SET TrangThai = 'daxacnhan' WHERE MaPN = ? AND TrangThai = 'chuaxacnhan'";
            
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, maPN);
                
                int result = ps.executeUpdate();
                
                if (result > 0) {
                    conn.commit();
                    System.out.println("✅ Xác nhận phiếu nhập thành công!");
                    return true;
                } else {
                    System.out.println("❌ Không thể xác nhận phiếu nhập!");
                    conn.rollback();
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ Lỗi khi xác nhận phiếu nhập: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    // Xóa phiếu nhập với kiểm tra ràng buộc trong Java
    @Override
    public boolean xoaPhieuNhap(int maPN) {
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            
            // 1. Kiểm tra phiếu nhập tồn tại
            NhapHangDTO phieuHienTai = layPhieuNhapTheoMa(maPN);
            if (phieuHienTai == null) {
                System.out.println("❌ Phiếu nhập không tồn tại!");
                return false;
            }
            
            // 2. Kiểm tra trạng thái - chỉ cho phép xóa khi chưa xác nhận
            if (phieuHienTai.isDaXacNhan()) {
                System.out.println("❌ Không thể xóa phiếu nhập đã được xác nhận!");
                return false;
            }
            
            // 3. Hoàn trả nguyên liệu về nhà cung cấp
            if (!hoanTraNguyenLieu(maPN, phieuHienTai.getMaNCC())) {
                System.out.println("❌ Lỗi khi hoàn trả nguyên liệu!");
                conn.rollback();
                return false;
            }
            
            // 4. Xóa chi tiết phiếu nhập trước
            String sqlChiTiet = "DELETE FROM chitietnhap_nl WHERE MaPN = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlChiTiet)) {
                ps.setInt(1, maPN);
                ps.executeUpdate();
            }
            
            // 5. Xóa phiếu nhập
            String sqlPhieu = "DELETE FROM phieunhap WHERE MaPN = ? AND TrangThai = 'chuaxacnhan'";
            try (PreparedStatement ps = conn.prepareStatement(sqlPhieu)) {
                ps.setInt(1, maPN);
                int result = ps.executeUpdate();
                
                if (result > 0) {
                    conn.commit();
                    System.out.println("✅ Xóa phiếu nhập thành công!");
                    return true;
                } else {
                    System.out.println("❌ Không thể xóa phiếu nhập!");
                    conn.rollback();
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ Lỗi khi xóa phiếu nhập: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    // Hoàn trả nguyên liệu về nhà cung cấp
    private boolean hoanTraNguyenLieu(int maPN, int maNCC) {
        String sql = "SELECT MaNL, SoLuong FROM chitietnhap_nl WHERE MaPN = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maPN);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int maNL = rs.getInt("MaNL");
                    int soLuong = rs.getInt("SoLuong");
                    
                    // Hoàn trả về nhà cung cấp
                    String sqlNCC = "UPDATE ncc_nguyenlieu SET SoLuong = SoLuong + ? WHERE MaNCC = ? AND MaNL = ?";
                    try (PreparedStatement psNCC = conn.prepareStatement(sqlNCC)) {
                        psNCC.setInt(1, soLuong);
                        psNCC.setInt(2, maNCC);
                        psNCC.setInt(3, maNL);
                        psNCC.executeUpdate();
                    }
                    
                    // Trừ số lượng trong kho
                    String sqlKho = "UPDATE kho_nguyenlieu SET SoLuong = SoLuong - ? WHERE MaNL = ?";
                    try (PreparedStatement psKho = conn.prepareStatement(sqlKho)) {
                        psKho.setInt(1, soLuong);
                        psKho.setInt(2, maNL);
                        psKho.executeUpdate();
                    }
                }
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Kiểm tra phiếu nhập có tồn tại và chưa xác nhận không
    @Override
    public boolean kiemTraCoTheSuaXoa(int maPN) {
        NhapHangDTO phieu = layPhieuNhapTheoMa(maPN);
        return phieu != null && phieu.isChuaXacNhan();
    }
    
    // Hiển thị thông tin phiếu nhập
    @Override
    public void hienThiPhieuNhap(NhapHangDTO phieu) {
        if (phieu != null) {
            System.out.println("=== THÔNG TIN PHIẾU NHẬP ===");
            System.out.println("Mã phiếu nhập: " + phieu.getMaPN());
            System.out.println("Mã nhân viên: " + phieu.getMaNV());
            System.out.println("Mã nhà cung cấp: " + phieu.getMaNCC());
            System.out.println("Ngày: " + phieu.getNgay());
            System.out.println("Ghi chú: " + phieu.getGhiChu());
            System.out.println("Thành tiền: " + phieu.getThanhTien());
            System.out.println("Trạng thái: " + (phieu.getTrangThai() != null ? phieu.getTrangThai().getTenTrangThai() : "Chưa xác định"));
            System.out.println("==========================");
        }
    }
    
    // Hiển thị danh sách phiếu nhập
    @Override
    public void hienThiDanhSachPhieuNhap(List<NhapHangDTO> danhSach) {
        if (danhSach.isEmpty()) {
            System.out.println("Không có phiếu nhập nào!");
            return;
        }
        
        System.out.println("=== DANH SÁCH PHIẾU NHẬP ===");
        System.out.printf("%-8s %-8s %-8s %-20s %-15s %-12s%n", 
                         "MaPN", "MaNV", "MaNCC", "Ngày", "Thành tiền", "Trạng thái");
        System.out.println("------------------------------------------------------------");
        
        for (NhapHangDTO phieu : danhSach) {
            System.out.printf("%-8d %-8d %-8d %-20s %-15d %-12s%n",
                             phieu.getMaPN(),
                             phieu.getMaNV(),
                             phieu.getMaNCC(),
                             phieu.getNgay(),
                             phieu.getThanhTien(),
                             phieu.getTrangThai() != null ? phieu.getTrangThai().getTenTrangThai() : "Chưa xác định");
        }
        System.out.println("=============================");
    }
    
    // Thêm chi tiết phiếu nhập với kiểm tra ràng buộc trong Java
    @Override
    public boolean themChiTietPhieuNhap(int maPN, int maNL, int soLuong, long donGia, String donVi) {
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            
            // 1. Kiểm tra phiếu nhập tồn tại và chưa xác nhận
            NhapHangDTO phieuHienTai = layPhieuNhapTheoMa(maPN);
            if (phieuHienTai == null) {
                System.out.println("❌ Phiếu nhập không tồn tại!");
                return false;
            }
            
            if (phieuHienTai.isDaXacNhan()) {
                System.out.println("❌ Không thể thêm chi tiết vào phiếu nhập đã xác nhận!");
                return false;
            }
            
            // 2. Kiểm tra số lượng có sẵn từ nhà cung cấp
            if (!kiemTraSoLuongNCC(phieuHienTai.getMaNCC(), maNL, soLuong)) {
                System.out.println("❌ Số lượng yêu cầu vượt quá số lượng có sẵn từ nhà cung cấp!");
                return false;
            }
            
            // 3. Thêm chi tiết phiếu nhập
            String sql = "INSERT INTO chitietnhap_nl (MaPN, MaNL, SoLuong, DonGia, DonVi) VALUES (?, ?, ?, ?, ?)";
            
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, maPN);
                ps.setInt(2, maNL);
                ps.setInt(3, soLuong);
                ps.setLong(4, donGia);
                ps.setString(5, donVi);
                
                int result = ps.executeUpdate();
                
                if (result > 0) {
                    // 4. Cập nhật số lượng nhà cung cấp
                    capNhatSoLuongNCC(phieuHienTai.getMaNCC(), maNL, -soLuong);
                    
                    // 5. Cập nhật kho nguyên liệu
                    capNhatKhoNguyenLieu(maNL, soLuong);
                    
                    // 6. Cập nhật tổng tiền phiếu nhập
                    capNhatTongTienPhieuNhap(maPN, soLuong * donGia);
                    
                    conn.commit();
                    System.out.println("✅ Thêm chi tiết phiếu nhập thành công!");
                    return true;
                } else {
                    System.out.println("❌ Không thể thêm chi tiết phiếu nhập!");
                    conn.rollback();
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ Lỗi khi thêm chi tiết phiếu nhập: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    // Kiểm tra số lượng có sẵn từ nhà cung cấp
    private boolean kiemTraSoLuongNCC(int maNCC, int maNL, int soLuongYeuCau) {
        String sql = "SELECT SoLuong FROM ncc_nguyenlieu WHERE MaNCC = ? AND MaNL = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maNCC);
            ps.setInt(2, maNL);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int soLuongCoSan = rs.getInt("SoLuong");
                    return soLuongCoSan >= soLuongYeuCau;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Cập nhật số lượng nhà cung cấp
    private void capNhatSoLuongNCC(int maNCC, int maNL, int soLuong) {
        String sql = "UPDATE ncc_nguyenlieu SET SoLuong = SoLuong + ? WHERE MaNCC = ? AND MaNL = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, soLuong);
            ps.setInt(2, maNCC);
            ps.setInt(3, maNL);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Cập nhật kho nguyên liệu
    private void capNhatKhoNguyenLieu(int maNL, int soLuong) {
        String sql = "INSERT INTO kho_nguyenlieu (MaNL, SoLuong) VALUES (?, ?) ON DUPLICATE KEY UPDATE SoLuong = SoLuong + ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maNL);
            ps.setInt(2, soLuong);
            ps.setInt(3, soLuong);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Cập nhật tổng tiền phiếu nhập
    private void capNhatTongTienPhieuNhap(int maPN, long thanhTien) {
        String sql = "UPDATE phieunhap SET ThanhTien = ThanhTien + ? WHERE MaPN = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, thanhTien);
            ps.setInt(2, maPN);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // ========== CHI TIẾT PHIẾU NHẬP ==========
    
    // Lấy tất cả chi tiết phiếu nhập theo mã phiếu
    @Override
    public List<ChiTietNhapHangDTO> layChiTietPhieuNhap(int maPN) {
        List<ChiTietNhapHangDTO> danhSach = new ArrayList<>();
        String sql = "SELECT ctn.MaPN, ctn.MaNL, nl.TenNL, ctn.SoLuong, ctn.DonGia, ctn.DonVi " +
                    "FROM chitietnhap_nl ctn " +
                    "JOIN nguyenlieu nl ON ctn.MaNL = nl.MaNL " +
                    "WHERE ctn.MaPN = ? " +
                    "ORDER BY ctn.MaNL";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maPN);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ChiTietNhapHangDTO chiTiet = new ChiTietNhapHangDTO(
                        rs.getInt("MaPN"),
                        rs.getInt("MaNL"),
                        rs.getString("TenNL"),
                        rs.getInt("SoLuong"),
                        rs.getLong("DonGia"),
                        rs.getString("DonVi")
                    );
                    danhSach.add(chiTiet);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSach;
    }
    
    // Cập nhật chi tiết phiếu nhập
    @Override
    public boolean capNhatChiTietPhieuNhap(ChiTietNhapHangDTO chiTiet) {
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            
            // 1. Kiểm tra phiếu nhập tồn tại và chưa xác nhận
            NhapHangDTO phieuHienTai = layPhieuNhapTheoMa(chiTiet.getMaPN());
            if (phieuHienTai == null) {
                System.out.println("❌ Phiếu nhập không tồn tại!");
                return false;
            }
            
            if (phieuHienTai.isDaXacNhan()) {
                System.out.println("❌ Không thể sửa chi tiết phiếu nhập đã xác nhận!");
                return false;
            }
            
            // 2. Lấy thông tin chi tiết cũ để tính toán chênh lệch
            ChiTietNhapHangDTO chiTietCu = layChiTietPhieuNhapTheoMa(chiTiet.getMaPN(), chiTiet.getMaNL());
            if (chiTietCu == null) {
                System.out.println("❌ Chi tiết phiếu nhập không tồn tại!");
                return false;
            }
            
            // 3. Tính chênh lệch số lượng và tiền
            int chenhLechSoLuong = chiTiet.getSoLuong() - chiTietCu.getSoLuong();
            long chenhLechTien = chiTiet.getThanhTien() - chiTietCu.getThanhTien();
            
            // 4. Kiểm tra số lượng có sẵn từ nhà cung cấp (nếu tăng số lượng)
            if (chenhLechSoLuong > 0) {
                if (!kiemTraSoLuongNCC(phieuHienTai.getMaNCC(), chiTiet.getMaNL(), chenhLechSoLuong)) {
                    System.out.println("❌ Số lượng yêu cầu vượt quá số lượng có sẵn từ nhà cung cấp!");
                    return false;
                }
            }
            
            // 5. Cập nhật chi tiết phiếu nhập
            String sql = "UPDATE chitietnhap_nl SET SoLuong = ?, DonGia = ?, DonVi = ? WHERE MaPN = ? AND MaNL = ?";
            
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, chiTiet.getSoLuong());
                ps.setLong(2, chiTiet.getDonGia());
                ps.setString(3, chiTiet.getDonVi());
                ps.setInt(4, chiTiet.getMaPN());
                ps.setInt(5, chiTiet.getMaNL());
                
                int result = ps.executeUpdate();
                
                if (result > 0) {
                    // 6. Cập nhật số lượng nhà cung cấp
                    capNhatSoLuongNCC(phieuHienTai.getMaNCC(), chiTiet.getMaNL(), -chenhLechSoLuong);
                    
                    // 7. Cập nhật kho nguyên liệu
                    capNhatKhoNguyenLieu(chiTiet.getMaNL(), chenhLechSoLuong);
                    
                    // 8. Cập nhật tổng tiền phiếu nhập
                    capNhatTongTienPhieuNhap(chiTiet.getMaPN(), chenhLechTien);
                    
                    conn.commit();
                    System.out.println("✅ Cập nhật chi tiết phiếu nhập thành công!");
                    return true;
                } else {
                    System.out.println("❌ Không thể cập nhật chi tiết phiếu nhập!");
                    conn.rollback();
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ Lỗi khi cập nhật chi tiết phiếu nhập: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    // Xóa chi tiết phiếu nhập
    @Override
    public boolean xoaChiTietPhieuNhap(int maPN, int maNL) {
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            
            // 1. Kiểm tra phiếu nhập tồn tại và chưa xác nhận
            NhapHangDTO phieuHienTai = layPhieuNhapTheoMa(maPN);
            if (phieuHienTai == null) {
                System.out.println("❌ Phiếu nhập không tồn tại!");
                return false;
            }
            
            if (phieuHienTai.isDaXacNhan()) {
                System.out.println("❌ Không thể xóa chi tiết phiếu nhập đã xác nhận!");
                return false;
            }
            
            // 2. Lấy thông tin chi tiết để tính toán
            ChiTietNhapHangDTO chiTiet = layChiTietPhieuNhapTheoMa(maPN, maNL);
            if (chiTiet == null) {
                System.out.println("❌ Chi tiết phiếu nhập không tồn tại!");
                return false;
            }
            
            // 3. Xóa chi tiết phiếu nhập
            String sql = "DELETE FROM chitietnhap_nl WHERE MaPN = ? AND MaNL = ?";
            
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, maPN);
                ps.setInt(2, maNL);
                
                int result = ps.executeUpdate();
                
                if (result > 0) {
                    // 4. Hoàn trả số lượng về nhà cung cấp
                    capNhatSoLuongNCC(phieuHienTai.getMaNCC(), maNL, chiTiet.getSoLuong());
                    
                    // 5. Trừ số lượng trong kho
                    capNhatKhoNguyenLieu(maNL, -chiTiet.getSoLuong());
                    
                    // 6. Cập nhật tổng tiền phiếu nhập
                    capNhatTongTienPhieuNhap(maPN, -chiTiet.getThanhTien());
                    
                    conn.commit();
                    System.out.println("✅ Xóa chi tiết phiếu nhập thành công!");
                    return true;
                } else {
                    System.out.println("❌ Không thể xóa chi tiết phiếu nhập!");
                    conn.rollback();
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ Lỗi khi xóa chi tiết phiếu nhập: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    // Lấy chi tiết phiếu nhập theo mã phiếu và mã nguyên liệu
    @Override
    public ChiTietNhapHangDTO layChiTietPhieuNhapTheoMa(int maPN, int maNL) {
        String sql = "SELECT ctn.MaPN, ctn.MaNL, nl.TenNL, ctn.SoLuong, ctn.DonGia, ctn.DonVi " +
                    "FROM chitietnhap_nl ctn " +
                    "JOIN nguyenlieu nl ON ctn.MaNL = nl.MaNL " +
                    "WHERE ctn.MaPN = ? AND ctn.MaNL = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maPN);
            ps.setInt(2, maNL);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new ChiTietNhapHangDTO(
                        rs.getInt("MaPN"),
                        rs.getInt("MaNL"),
                        rs.getString("TenNL"),
                        rs.getInt("SoLuong"),
                        rs.getLong("DonGia"),
                        rs.getString("DonVi")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // Hiển thị chi tiết phiếu nhập
    @Override
    public void hienThiChiTietPhieuNhap(int maPN) {
        List<ChiTietNhapHangDTO> chiTietList = layChiTietPhieuNhap(maPN);
        
        if (chiTietList.isEmpty()) {
            System.out.println("Phiếu nhập #" + maPN + " chưa có chi tiết nào!");
            return;
        }
        
        System.out.println("=== CHI TIẾT PHIẾU NHẬP #" + maPN + " ===");
        System.out.printf("%-8s %-20s %-10s %-15s %-10s %-15s%n", 
                         "Mã NL", "Tên nguyên liệu", "Số lượng", "Đơn giá", "Đơn vị", "Thành tiền");
        System.out.println("----------------------------------------------------------------------------");
        
        long tongTien = 0;
        for (ChiTietNhapHangDTO chiTiet : chiTietList) {
            System.out.printf("%-8d %-20s %-10d %-15d %-10s %-15d%n",
                             chiTiet.getMaNL(),
                             chiTiet.getTenNL(),
                             chiTiet.getSoLuong(),
                             chiTiet.getDonGia(),
                             chiTiet.getDonVi(),
                             chiTiet.getThanhTien());
            tongTien += chiTiet.getThanhTien();
        }
        
        System.out.println("----------------------------------------------------------------------------");
        System.out.printf("%-60s %-15d%n", "TỔNG TIỀN:", tongTien);
        System.out.println("================================================");
    }
    
}
