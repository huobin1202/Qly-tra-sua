package controller;

import java.util.List;

import dto.NhapHangDTO;
import dto.ChiTietNhapHangDTO;

public interface IQuanLyPhieuNhap {
    // CRUD operations
    boolean taoPhieuNhap(NhapHangDTO phieuNhap);

    NhapHangDTO layPhieuNhapTheoMa(int maPN);

    List<NhapHangDTO> layTatCaPhieuNhap();

    boolean capNhatPhieuNhap(NhapHangDTO phieuNhap);

    boolean xoaPhieuNhap(int maPN);

    // Search operations


    // Status operations
    boolean xacNhanPhieuNhap(int maPN);
    // Display operations
    // Chi tiết phiếu nhập operations
    boolean themChiTietPhieuNhap(int maPN, int maNL, int soLuong, long donGia, String donVi);

    List<ChiTietNhapHangDTO> layChiTietPhieuNhap(int maPN);

    ChiTietNhapHangDTO layChiTietPhieuNhapTheoMa(int maPN, int maNL);

    boolean capNhatChiTietPhieuNhap(ChiTietNhapHangDTO chiTiet);

    boolean xoaChiTietPhieuNhap(int maPN, int maNL);

}
