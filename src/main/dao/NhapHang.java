package dao;
import java.sql.*;
import java.util.Scanner;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import database.DBUtil;
import dto.NhapHangDTO;

public class NhapHang {
    private NhapHangDAO nhapHangDAO;
    private Scanner scanner;
    
    public NhapHang() {
        this.nhapHangDAO = new NhapHangDAO();
        this.scanner = new Scanner(System.in);
    }
    
 
    
    // Tạo phiếu nhập mới
    public void taoPhieuNhap() {
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            
            int maNV = database.Session.currentMaNV;
            System.out.println("Nhân viên hiện tại: " + maNV);
            
            System.out.print("Mã nhà cung cấp: ");
            int maNCC = Integer.parseInt(scanner.nextLine().trim());
            
            System.out.print("Ghi chú: ");
            String ghiChu = scanner.nextLine();
            
            // Tạo phiếu nhập mới với trạng thái chưa xác nhận
            NhapHangDTO phieuNhap = new NhapHangDTO();
            phieuNhap.setMaNV(maNV);
            phieuNhap.setMaNCC(maNCC);
            phieuNhap.setGhiChu(ghiChu);
            phieuNhap.setNgay(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            phieuNhap.setThanhTien(0);
            
            if (!nhapHangDAO.taoPhieuNhap(phieuNhap)) {
                System.out.println("Lỗi khi tạo phiếu nhập!");
                return;
            }
            
            int maPN = phieuNhap.getMaPN();
            long tongThanhTien = 0;
            
            // Nhập chi tiết nguyên liệu
            while (true) {
                System.out.print("Nhập nguyên liệu (MaNL, 0: kết thúc): ");
                String idStr = scanner.nextLine().trim();
                if (idStr.equals("0")) break;
                
                int maNL = Integer.parseInt(idStr);
                System.out.print("Số lượng: ");
                int sl = Integer.parseInt(scanner.nextLine().trim());
                
                // Kiểm tra nguyên liệu có sẵn từ nhà cung cấp
                int nccQty = 0; 
                long donGia = 0; 
                String donVi = null;
                
                try (PreparedStatement ps = conn.prepareStatement("SELECT SoLuong, DonGia FROM ncc_nguyenlieu WHERE MaNCC=? AND MaNL=? FOR UPDATE")) {
                    ps.setInt(1, maNCC);
                    ps.setInt(2, maNL);
                    try (ResultSet rs = ps.executeQuery()) { 
                        if (rs.next()) { 
                            nccQty = rs.getInt(1); 
                            donGia = rs.getLong(2); 
                        } 
                    }
                }
                
                if (nccQty == 0) { 
                    System.out.println("Nhà cung cấp không có nguyên liệu này."); 
                    continue; 
                }
                if (sl > nccQty) { 
                    System.out.println("Số lượng yêu cầu vượt quá số lượng NCC (" + nccQty + ")"); 
                    continue; 
                }
                
                // Lấy đơn vị nguyên liệu
                try (PreparedStatement ps = conn.prepareStatement("SELECT DonVi FROM nguyenlieu WHERE MaNL=?")) {
                    ps.setInt(1, maNL);
                    try (ResultSet rs = ps.executeQuery()) { 
                        if (rs.next()) donVi = rs.getString(1); 
                    }
                }
                
                // Thêm chi tiết phiếu nhập
                try (PreparedStatement ps = conn.prepareStatement("INSERT INTO chitietnhap_nl (MaPN, MaNL, SoLuong, DonGia, DonVi) VALUES (?,?,?,?,?)")) {
                    ps.setInt(1, maPN);
                    ps.setInt(2, maNL);
                    ps.setInt(3, sl);
                    ps.setLong(4, donGia);
                    ps.setString(5, donVi);
                    ps.executeUpdate();
                }
                
                tongThanhTien += (long) sl * donGia;
                
                // Cập nhật kho nguyên liệu
                try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO kho_nguyenlieu (MaNL, SoLuong) VALUES (?, ?) ON DUPLICATE KEY UPDATE SoLuong = SoLuong + VALUES(SoLuong)")) {
                    ps.setInt(1, maNL);
                    ps.setInt(2, sl);
                    ps.executeUpdate();
                }
                
                // Cập nhật số lượng nhà cung cấp
                try (PreparedStatement ps = conn.prepareStatement("UPDATE ncc_nguyenlieu SET SoLuong = SoLuong - ? WHERE MaNCC=? AND MaNL=?")) {
                    ps.setInt(1, sl);
                    ps.setInt(2, maNCC);
                    ps.setInt(3, maNL);
                    ps.executeUpdate();
                }
            }
            
            // Cập nhật tổng tiền phiếu nhập
            phieuNhap.setThanhTien(tongThanhTien);
            nhapHangDAO.capNhatPhieuNhap(phieuNhap);
            
            conn.commit();
            System.out.println("Đã tạo phiếu nhập: " + maPN + " (Trạng thái: Chưa xác nhận)");
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Hiển thị tất cả phiếu nhập
    public void hienThiTatCaPhieuNhap() {
        List<NhapHangDTO> danhSach = nhapHangDAO.layTatCaPhieuNhap();
        nhapHangDAO.hienThiDanhSachPhieuNhap(danhSach);
    }
    
    // Menu tìm kiếm phiếu nhập
    public void menuTimKiemPhieuNhap() {
        System.out.println("\n=== TÌM KIẾM PHIẾU NHẬP ===");
        System.out.println("1. Tìm theo trạng thái");
        System.out.println("2. Tìm theo nhà cung cấp");
        System.out.println("3. Tìm theo mã phiếu nhập");
        System.out.println("0. Quay lại");
        System.out.print("Chọn: ");
        
        int choice = Integer.parseInt(scanner.nextLine().trim());
        
        switch (choice) {
            case 1:
                System.out.print("Nhập trạng thái (chuaxacnhan/daxacnhan): ");
                String trangThai = scanner.nextLine().trim();
                List<NhapHangDTO> danhSach1 = nhapHangDAO.timPhieuNhapTheoTrangThai(trangThai);
                nhapHangDAO.hienThiDanhSachPhieuNhap(danhSach1);
                break;
            case 2:
                System.out.print("Nhập mã nhà cung cấp: ");
                int maNCC = Integer.parseInt(scanner.nextLine().trim());
                List<NhapHangDTO> danhSach2 = nhapHangDAO.timPhieuNhapTheoNCC(maNCC);
                nhapHangDAO.hienThiDanhSachPhieuNhap(danhSach2);
                break;
            case 3:
                System.out.print("Nhập mã phiếu nhập: ");
                int maPN = Integer.parseInt(scanner.nextLine().trim());
                NhapHangDTO phieu = nhapHangDAO.layPhieuNhapTheoMa(maPN);
                nhapHangDAO.hienThiPhieuNhap(phieu);
                break;
            case 0:
                return;
            default:
                System.out.println("Lựa chọn không hợp lệ!");
        }
    }
    
    // Sửa phiếu nhập
    public void suaPhieuNhap() {
        System.out.print("Nhập mã phiếu nhập cần sửa: ");
        int maPN = Integer.parseInt(scanner.nextLine().trim());
        
        if (!nhapHangDAO.kiemTraCoTheSuaXoa(maPN)) {
            System.out.println("Không thể sửa phiếu nhập này!");
            return;
        }
        
        NhapHangDTO phieu = nhapHangDAO.layPhieuNhapTheoMa(maPN);
        if (phieu == null) {
            System.out.println("Không tìm thấy phiếu nhập!");
            return;
        }
        
        System.out.println("Thông tin hiện tại:");
        nhapHangDAO.hienThiPhieuNhap(phieu);
        
        System.out.print("Mã nhà cung cấp mới (Enter để giữ nguyên): ");
        String nccStr = scanner.nextLine().trim();
        if (!nccStr.isEmpty()) {
            phieu.setMaNCC(Integer.parseInt(nccStr));
        }
        
        System.out.print("Ghi chú mới (Enter để giữ nguyên): ");
        String ghiChu = scanner.nextLine().trim();
        if (!ghiChu.isEmpty()) {
            phieu.setGhiChu(ghiChu);
        }
        
        if (nhapHangDAO.capNhatPhieuNhap(phieu)) {
            System.out.println("Cập nhật thành công!");
        } else {
            System.out.println("Cập nhật thất bại!");
        }
    }
    
    // Xác nhận phiếu nhập
    public void xacNhanPhieuNhap() {
        System.out.print("Nhập mã phiếu nhập cần xác nhận: ");
        int maPN = Integer.parseInt(scanner.nextLine().trim());
        
        NhapHangDTO phieu = nhapHangDAO.layPhieuNhapTheoMa(maPN);
        if (phieu == null) {
            System.out.println("Không tìm thấy phiếu nhập!");
            return;
        }
        
        if (phieu.isDaXacNhan()) {
            System.out.println("Phiếu nhập đã được xác nhận rồi!");
            return;
        }
        
        if (nhapHangDAO.xacNhanPhieuNhap(maPN)) {
            System.out.println("Xác nhận phiếu nhập thành công!");
        } else {
            System.out.println("Xác nhận phiếu nhập thất bại!");
        }
    }
    
    // Xóa phiếu nhập
    public void xoaPhieuNhap() {
        System.out.print("Nhập mã phiếu nhập cần xóa: ");
        int maPN = Integer.parseInt(scanner.nextLine().trim());
        
        if (!nhapHangDAO.kiemTraCoTheSuaXoa(maPN)) {
            System.out.println("Không thể xóa phiếu nhập này!");
            return;
        }
        
        System.out.print("Bạn có chắc chắn muốn xóa? (y/n): ");
        String confirm = scanner.nextLine().trim().toLowerCase();
        
        if (confirm.equals("y") || confirm.equals("yes")) {
            if (nhapHangDAO.xoaPhieuNhap(maPN)) {
                System.out.println("Xóa phiếu nhập thành công!");
            } else {
                System.out.println("Xóa phiếu nhập thất bại!");
            }
        } else {
            System.out.println("Hủy bỏ xóa phiếu nhập.");
        }
    }
}
