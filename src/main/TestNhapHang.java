package main;

import dao.NhapHangDAO;
import dto.NhapHangDTO;
import dto.ChiTietNhapHangDTO;
import controller.TrangThaiPhieuNhap;
import java.util.List;

public class TestNhapHang {
    public static void main(String[] args) {
        System.out.println("=== TEST NHẬP HÀNG VÀ CHI TIẾT PHIẾU NHẬP ===");
        
        NhapHangDAO nhapHangDAO = new NhapHangDAO();
        
        // Test 1: Tạo phiếu nhập mới
        System.out.println("\n1. Tạo phiếu nhập mới...");
        NhapHangDTO phieuNhap = new NhapHangDTO();
        phieuNhap.setMaNV(1); // Admin
        phieuNhap.setMaNCC(1); // NCC A
        phieuNhap.setNgay("2025-01-15");
        phieuNhap.setGhiChu("Test phiếu nhập");
        phieuNhap.setThanhTien(0);
        phieuNhap.setTrangThai(TrangThaiPhieuNhap.CHUA_XAC_NHAN);
        
        if (nhapHangDAO.taoPhieuNhap(phieuNhap)) {
            System.out.println("✅ Tạo phiếu nhập thành công! Mã: " + phieuNhap.getMaPN());
            
            // Test 2: Thêm chi tiết phiếu nhập
            System.out.println("\n2. Thêm chi tiết phiếu nhập...");
            if (nhapHangDAO.themChiTietPhieuNhap(phieuNhap.getMaPN(), 1, 1000, 20, "gram")) {
                System.out.println("✅ Thêm chi tiết thành công!");
            } else {
                System.out.println("❌ Thêm chi tiết thất bại!");
            }
            
            // Test 3: Lấy chi tiết phiếu nhập
            System.out.println("\n3. Lấy chi tiết phiếu nhập...");
            List<ChiTietNhapHangDTO> chiTietList = nhapHangDAO.layChiTietPhieuNhap(phieuNhap.getMaPN());
            System.out.println("Số lượng chi tiết: " + chiTietList.size());
            
            for (ChiTietNhapHangDTO chiTiet : chiTietList) {
                System.out.println("- " + chiTiet.getTenNL() + ": " + chiTiet.getSoLuong() + " " + chiTiet.getDonVi() + 
                                 " x " + chiTiet.getDonGia() + " = " + chiTiet.getThanhTien());
            }
            
            // Test 4: Hiển thị chi tiết
            System.out.println("\n4. Hiển thị chi tiết phiếu nhập:");
            nhapHangDAO.hienThiChiTietPhieuNhap(phieuNhap.getMaPN());
            
            // Test 5: Lấy thông tin phiếu nhập
            System.out.println("\n5. Thông tin phiếu nhập:");
            NhapHangDTO phieuInfo = nhapHangDAO.layPhieuNhapTheoMa(phieuNhap.getMaPN());
            if (phieuInfo != null) {
                nhapHangDAO.hienThiPhieuNhap(phieuInfo);
            }
            
        } else {
            System.out.println("❌ Tạo phiếu nhập thất bại!");
        }
        
        System.out.println("\n=== KẾT THÚC TEST ===");
    }
}
