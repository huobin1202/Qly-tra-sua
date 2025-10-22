package view;

import java.util.Scanner;

import dao.DonHangDAO;
import dao.GiaoHangDAO;

public class DonHangView {
    private DonHangDAO donHangDAO;
    private GiaoHangDAO giaoHangDAO;
    private Scanner scanner;
    
    public DonHangView() {
        this.donHangDAO = new DonHangDAO();
        this.giaoHangDAO = new GiaoHangDAO();
        this.scanner = new Scanner(System.in);
    }
    
    public void menu() {
        while (true) {
            ConsoleUI.printHeader("QUẢN LÝ ĐẶT HÀNG");
            ConsoleUI.printSection("KHU VỰC QUẢN LÝ");
            System.out.println("│ 1. Quản lý đơn đặt hàng                      │");
            System.out.println("│ 2. Quản lý giao hàng                         │");
            ConsoleUI.printSection("ĐIỀU HƯỚNG");
            System.out.println("│ 0. Quay lại                                  │");
            ConsoleUI.printFooter();
            System.out.print(ConsoleUI.promptLabel("Chọn chức năng"));
            String chonDHStr = scanner.nextLine();
            int chonDH;
            try {
                chonDH = Integer.parseInt(chonDHStr.trim());
            } catch (NumberFormatException e) {
                System.out.println("Vui lòng nhập số hợp lệ.");
                continue;
            }
            if (chonDH == 1) {
                while (true) {
                    ConsoleUI.printHeader("QUẢN LÝ ĐƠN ĐẶT HÀNG");
                    ConsoleUI.printSection("CHỨC NĂNG");
                    System.out.println("│ 1. Xem danh sách đơn hàng                     │");
                    System.out.println("│ 2. Xem chi tiết đơn hàng                      │");
                    System.out.println("│ 3. Thêm đơn hàng                              │");
                    System.out.println("│ 4. Sửa đơn hàng                              │");
                    System.out.println("│ 5. Xóa đơn hàng                              │");
                    System.out.println("│ 6. Tìm kiếm đơn hàng                          │");
                    System.out.println("│ 7. Xác nhận đã thanh toán                    │");
                    ConsoleUI.printSection("ĐIỀU HƯỚNG");
                    System.out.println("│ 0. Quay lại                                  │");
                    ConsoleUI.printFooter();
                    System.out.print(ConsoleUI.promptLabel("Chọn chức năng"));
                    String chonDonStr = scanner.nextLine();
                    int chonDon;
                    try {
                        chonDon = Integer.parseInt(chonDonStr.trim());
                    } catch (NumberFormatException e) {
                        System.out.println("Vui lòng nhập số hợp lệ.");
                        continue;
                    }
                    if (chonDon == 1) xemDanhSachDonHang();
                    else if (chonDon == 2) xemChiTietDonHang();
                    else if (chonDon == 3) themDonHang();
                    else if (chonDon == 4) suaDonHang();
                    else if (chonDon == 5) xoaDonHang();
                    else if (chonDon == 6) timKiemDonHang();
                    else if (chonDon == 7) xacNhanThanhToan();
                    else if (chonDon == 0) break;
                    else System.out.println("Chức năng không hợp lệ.");
                }
            } else if (chonDH == 2) {
                while (true) {
                    ConsoleUI.printHeader("QUẢN LÝ GIAO HÀNG");
                    ConsoleUI.printSection("CHỨC NĂNG");
                    System.out.println("│ 1. Xem danh sách giao hàng                    │");
                    System.out.println("│ 2. Thêm giao hàng                             │");
                    System.out.println("│ 3. Sửa giao hàng                              │");
                    System.out.println("│ 4. Xóa giao hàng                              │");
                    ConsoleUI.printSection("ĐIỀU HƯỚNG");
                    System.out.println("│ 0. Quay lại                                   │");
                    ConsoleUI.printFooter();
                    System.out.print(ConsoleUI.promptLabel("Chọn chức năng"));
                    String chonGiaoStr = scanner.nextLine();
                    int chonGiao;
                    try {
                        chonGiao = Integer.parseInt(chonGiaoStr.trim());
                    } catch (NumberFormatException e) {
                        System.out.println("Vui lòng nhập số hợp lệ.");
                        continue;
                    }
                    if (chonGiao == 1) xemDanhSachGiaoHang();
                    else if (chonGiao == 2) themGiaoHang();
                    else if (chonGiao == 3) suaGiaoHang();
                    else if (chonGiao == 4) xoaGiaoHang();
                    else if (chonGiao == 0) break;
                    else System.out.println("Chức năng không hợp lệ.");
                }
            } else if (chonDH == 0) {
                break;
            }
        }
    }
    
    // Don Hang methods
    private void xemDanhSachDonHang() {
        ConsoleUI.printHeader("DANH SÁCH ĐƠN HÀNG");
        donHangDAO.xuat();
        ConsoleUI.pause();
    }
    
    private void xemChiTietDonHang() {
        ConsoleUI.printHeader("CHI TIẾT ĐƠN HÀNG");
        donHangDAO.xemChiTiet();
        ConsoleUI.pause();
    }
    
    private void themDonHang() {
        ConsoleUI.printHeader("THÊM ĐƠN HÀNG MỚI");
        donHangDAO.them();
        ConsoleUI.pause();
    }
    
    private void suaDonHang() {
        ConsoleUI.printHeader("SỬA ĐƠN HÀNG");
        donHangDAO.sua();
        ConsoleUI.pause();
    }
    
    private void xoaDonHang() {
        ConsoleUI.printHeader("XÓA ĐƠN HÀNG");
        donHangDAO.xoa();
        ConsoleUI.pause();
    }
    
    private void timKiemDonHang() {
        ConsoleUI.printHeader("TÌM KIẾM ĐƠN HÀNG");
        donHangDAO.timkiem();
        ConsoleUI.pause();
    }
    
    private void xacNhanThanhToan() {
        ConsoleUI.printHeader("XÁC NHẬN THANH TOÁN");
        donHangDAO.xacNhanThanhToan();
        ConsoleUI.pause();
    }
    
    // Giao Hang methods
    private void xemDanhSachGiaoHang() {
        ConsoleUI.printHeader("DANH SÁCH GIAO HÀNG");
        giaoHangDAO.xuat();
        ConsoleUI.pause();
    }
    
    private void themGiaoHang() {
        ConsoleUI.printHeader("THÊM GIAO HÀNG MỚI");
        giaoHangDAO.them();
        ConsoleUI.pause();
    }
    
    private void suaGiaoHang() {
        ConsoleUI.printHeader("SỬA GIAO HÀNG");
        giaoHangDAO.sua();
        ConsoleUI.pause();
    }
    
    private void xoaGiaoHang() {
        ConsoleUI.printHeader("XÓA GIAO HÀNG");
        giaoHangDAO.xoa();
        ConsoleUI.pause();
    }
}
