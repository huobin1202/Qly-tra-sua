package view;

import java.util.Scanner;

public class NhapHangView {
    private dao.NhapHangDAO nhapHangController;
    private Scanner scanner;
    
    public NhapHangView() {
        this.nhapHangController = new dao.NhapHangDAO();
        this.scanner = new Scanner(System.in);
    }
    
    public void menu() {
        while (true) {
            ConsoleUI.printHeader("QUẢN LÝ PHIẾU NHẬP");
            ConsoleUI.printSection("CHỨC NĂNG");
            System.out.println("│ 1. Tạo phiếu nhập mới                        │");
            System.out.println("│ 2. Xem danh sách phiếu nhập                   │");
            System.out.println("│ 3. Tìm kiếm phiếu nhập                        │");
            System.out.println("│ 4. Sửa phiếu nhập                             │");
            System.out.println("│ 5. Xác nhận phiếu nhập                        │");
            System.out.println("│ 6. Xóa phiếu nhập                             │");
            ConsoleUI.printSection("ĐIỀU HƯỚNG");
            System.out.println("│ 0. Quay lại                                   │");
            ConsoleUI.printFooter();
            
            System.out.print(ConsoleUI.promptLabel("Chọn chức năng"));
            String choiceStr = scanner.nextLine();
            int choice;
            
            try {
                choice = Integer.parseInt(choiceStr.trim());
            } catch (NumberFormatException e) {
                System.out.println("Vui lòng nhập số hợp lệ.");
                continue;
            }
            
            switch (choice) {
                case 1:
                    taoPhieuNhap();
                    break;
                case 2:
                    xemDanhSachPhieuNhap();
                    break;
                case 3:
                    timKiemPhieuNhap();
                    break;
                case 4:
                    suaPhieuNhap();
                    break;
                case 5:
                    xacNhanPhieuNhap();
                    break;
                case 6:
                    xoaPhieuNhap();
                    break;
             
                case 0:
                    return;
                default:
                    System.out.println("Chức năng không hợp lệ.");
            }
        }
    }
    
    void taoPhieuNhap() {
        ConsoleUI.printHeader("TẠO PHIẾU NHẬP MỚI");
        try {
            System.out.print(ConsoleUI.promptLabel("Mã nhân viên"));
            int maNV = Integer.parseInt(scanner.nextLine().trim());

            System.out.print(ConsoleUI.promptLabel("Mã nhà cung cấp"));
            int maNCC = Integer.parseInt(scanner.nextLine().trim());

            System.out.print(ConsoleUI.promptLabel("Ngày (yyyy-mm-dd)"));
            String ngay = scanner.nextLine().trim();

            System.out.print(ConsoleUI.promptLabel("Ghi chú"));
            String ghiChu = scanner.nextLine().trim();

            dto.NhapHangDTO phieu = new dto.NhapHangDTO(0, maNV, maNCC, ngay, ghiChu, 0, "chuaxacnhan");
            boolean ok = nhapHangController.taoPhieuNhap(phieu);
            System.out.println(ok ? "✅ Tạo phiếu nhập thành công!" : "❌ Tạo phiếu nhập thất bại!");
        } catch (NumberFormatException ex) {
            System.out.println("Vui lòng nhập số hợp lệ.");
        }
        ConsoleUI.pause();
    }
    
    private void xemDanhSachPhieuNhap() {
        ConsoleUI.printHeader("DANH SÁCH PHIẾU NHẬP");
        java.util.List<dto.NhapHangDTO> ds = nhapHangController.layTatCaPhieuNhap();
        nhapHangController.hienThiDanhSachPhieuNhap(ds);
        ConsoleUI.pause();
    }
    
    private void timKiemPhieuNhap() {
        ConsoleUI.printHeader("TÌM KIẾM PHIẾU NHẬP");
        System.out.println("1. Tìm theo trạng thái");
        System.out.println("2. Tìm theo nhà cung cấp");
        System.out.print(ConsoleUI.promptLabel("Chọn loại tìm kiếm"));
        String choiceStr = scanner.nextLine();
        try {
            int choice = Integer.parseInt(choiceStr.trim());
            switch (choice) {
                case 1:
                    System.out.print(ConsoleUI.promptLabel("Trạng thái (chuaxacnhan/daxacnhan)"));
                    String tt = scanner.nextLine().trim();
                    nhapHangController.hienThiDanhSachPhieuNhap(nhapHangController.timPhieuNhapTheoTrangThai(tt));
                    break;
                case 2:
                    System.out.print(ConsoleUI.promptLabel("Mã nhà cung cấp"));
                    int maNCC = Integer.parseInt(scanner.nextLine().trim());
                    nhapHangController.hienThiDanhSachPhieuNhap(nhapHangController.timPhieuNhapTheoNCC(maNCC));
                    break;
                default:
                    System.out.println("Chức năng không hợp lệ.");
            }
        } catch (NumberFormatException ex) {
            System.out.println("Vui lòng nhập số hợp lệ.");
        }
        ConsoleUI.pause();
    }
    
    private void suaPhieuNhap() {
        ConsoleUI.printHeader("SỬA PHIẾU NHẬP");
        try {
            System.out.print(ConsoleUI.promptLabel("Mã phiếu nhập"));
            int maPN = Integer.parseInt(scanner.nextLine().trim());
            dto.NhapHangDTO phieu = nhapHangController.layPhieuNhapTheoMa(maPN);
            if (phieu == null) {
                System.out.println("❌ Phiếu nhập không tồn tại!");
            } else if (phieu.isDaXacNhan()) {
                System.out.println("❌ Không thể sửa phiếu đã xác nhận!");
            } else {
                nhapHangController.hienThiPhieuNhap(phieu);
                System.out.print(ConsoleUI.promptLabel("Mã nhà cung cấp mới"));
                int maNCC = Integer.parseInt(scanner.nextLine().trim());
                System.out.print(ConsoleUI.promptLabel("Ghi chú mới"));
                String ghiChu = scanner.nextLine().trim();
                System.out.print(ConsoleUI.promptLabel("Thành tiền mới"));
                long thanhTien = Long.parseLong(scanner.nextLine().trim());
                phieu.setMaNCC(maNCC);
                phieu.setGhiChu(ghiChu);
                phieu.setThanhTien(thanhTien);
                boolean ok = nhapHangController.capNhatPhieuNhap(phieu);
                System.out.println(ok ? "✅ Sửa thành công!" : "❌ Sửa thất bại!");
            }
        } catch (NumberFormatException ex) {
            System.out.println("Vui lòng nhập số hợp lệ.");
        }
        ConsoleUI.pause();
    }
    
    private void xacNhanPhieuNhap() {
        ConsoleUI.printHeader("XÁC NHẬN PHIẾU NHẬP");
        try {
            System.out.print(ConsoleUI.promptLabel("Mã phiếu nhập"));
            int maPN = Integer.parseInt(scanner.nextLine().trim());
            boolean ok = nhapHangController.xacNhanPhieuNhap(maPN);
            System.out.println(ok ? "✅ Đã xác nhận!" : "❌ Xác nhận thất bại!");
        } catch (NumberFormatException ex) {
            System.out.println("Vui lòng nhập số hợp lệ.");
        }
        ConsoleUI.pause();
    }
    
    private void xoaPhieuNhap() {
        ConsoleUI.printHeader("XÓA PHIẾU NHẬP");
        try {
            System.out.print(ConsoleUI.promptLabel("Mã phiếu nhập"));
            int maPN = Integer.parseInt(scanner.nextLine().trim());
            dto.NhapHangDTO phieu = nhapHangController.layPhieuNhapTheoMa(maPN);
            if (phieu == null) {
                System.out.println("❌ Phiếu nhập không tồn tại!");
            } else if (phieu.isDaXacNhan()) {
                System.out.println("❌ Không thể xóa phiếu đã xác nhận!");
            } else {
                nhapHangController.hienThiPhieuNhap(phieu);
                System.out.print(ConsoleUI.promptLabel("Xác nhận xóa (y/n)"));
                String confirm = scanner.nextLine().trim();
                if (confirm.equalsIgnoreCase("y")) {
                    boolean ok = nhapHangController.xoaPhieuNhap(maPN);
                    System.out.println(ok ? "✅ Đã xóa!" : "❌ Xóa thất bại!");
                } else {
                    System.out.println("Đã hủy.");
                }
            }
        } catch (NumberFormatException ex) {
            System.out.println("Vui lòng nhập số hợp lệ.");
        }
        ConsoleUI.pause();
    }


}
