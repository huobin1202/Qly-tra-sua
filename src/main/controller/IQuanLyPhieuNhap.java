package controller;

import java.util.List;

import dto.NhapHangDTO;

public interface IQuanLyPhieuNhap {
    // CRUD operations
    boolean taoPhieuNhap(NhapHangDTO phieuNhap);
    NhapHangDTO layPhieuNhapTheoMa(int maPN);
    List<NhapHangDTO> layTatCaPhieuNhap();
    boolean capNhatPhieuNhap(NhapHangDTO phieuNhap);
    boolean xoaPhieuNhap(int maPN);
    
    // Search operations
    List<NhapHangDTO> timPhieuNhapTheoTrangThai(String trangThai);
    List<NhapHangDTO> timPhieuNhapTheoNCC(int maNCC);
    
    // Status operations
    boolean xacNhanPhieuNhap(int maPN);
    boolean kiemTraCoTheSuaXoa(int maPN);
    
    // Display operations
    void hienThiPhieuNhap(NhapHangDTO phieu);
    void hienThiDanhSachPhieuNhap(List<NhapHangDTO> danhSach);
}
