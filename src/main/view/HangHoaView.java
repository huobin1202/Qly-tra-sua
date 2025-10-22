package view;

import java.util.Scanner;
import dao.*;

public class HangHoaView {
    private LoaiMonDAO loaiMonDAO;
    private MonDAO monDAO;
    private NguyenLieuDAO nguyenLieuDAO;
    private Scanner scanner;
    
    public HangHoaView() {
        this.loaiMonDAO = new LoaiMonDAO();
        this.monDAO = new MonDAO();
        this.nguyenLieuDAO = new NguyenLieuDAO();
        this.scanner = new Scanner(System.in);
    }
    
    public void menu() {
        while (true) {
            ConsoleUI.printHeader("QUẢN LÝ HÀNG HÓA");
            ConsoleUI.printSection("KHU VỰC QUẢN LÝ");
            System.out.println("│ 1. Loại món                                  │");
            System.out.println("│ 2. Món                                       │");
            System.out.println("│ 3. Nguyên liệu                               │");
            ConsoleUI.printSection("ĐIỀU HƯỚNG");
            System.out.println("│ 0. Quay lại                                  │");
            ConsoleUI.printFooter();
            System.out.print(ConsoleUI.promptLabel("Chọn chức năng"));
            String chonHHStr = scanner.nextLine();
            int chonHH;
            try {
                chonHH = Integer.parseInt(chonHHStr.trim());
            } catch (NumberFormatException e) {
                System.out.println("Vui lòng nhập số hợp lệ.");
                continue;
            }
            if (chonHH == 1) {
                quanLyLoaiMon();
            } else if (chonHH == 2) {
                quanLyMon();
            } else if (chonHH == 3) {
                quanLyNguyenLieu();
            }  else if (chonHH == 0) {
                break;
            }
        }
    }

    private void quanLyLoaiMon() {
        while (true) {
            ConsoleUI.printHeader("LOẠI MÓN");
            ConsoleUI.printSection("CHỨC NĂNG");
            System.out.println("│ 1. Xem danh sách loại món           │");
            System.out.println("│ 2. Thêm loại món                    │");
            System.out.println("│ 3. Sửa loại món                     │");
            System.out.println("│ 4. Xóa loại món                     │");
            ConsoleUI.printSection("ĐIỀU HƯỚNG");
            System.out.println("│ 0. Quay lại                         │");
            ConsoleUI.printFooter();
            System.out.print(ConsoleUI.promptLabel("Chọn chức năng"));
            String chonLMStr = scanner.nextLine();
            int chonLM;
            try {
                chonLM = Integer.parseInt(chonLMStr.trim());
            } catch (NumberFormatException e) {
                System.out.println("Vui lòng nhập số hợp lệ.");
                continue;
            }
            if (chonLM == 1) loaiMonDAO.xuat();
            else if (chonLM == 2) loaiMonDAO.them();
            else if (chonLM == 3) loaiMonDAO.sua();
            else if (chonLM == 4) loaiMonDAO.xoa();
            else if (chonLM == 0) break;
            else System.out.println("Chức năng không hợp lệ.");
        }
    }

    private void quanLyMon() {
        while (true) {
            ConsoleUI.printHeader("MÓN");
            ConsoleUI.printSection("CHỨC NĂNG");
            System.out.println("│ 1. Xem danh sách món                │");
            System.out.println("│ 2. Thêm món                         │");
            System.out.println("│ 3. Sửa món                          │");
            System.out.println("│ 4. Xóa món                          │");
            System.out.println("│ 5. Tìm kiếm món                     │");
            ConsoleUI.printSection("ĐIỀU HƯỚNG");
            System.out.println("│ 0. Quay lại                         │");
            ConsoleUI.printFooter();
            System.out.print(ConsoleUI.promptLabel("Chọn chức năng"));
            String chonMonStr = scanner.nextLine();
            int chonMon;
            try {
                chonMon = Integer.parseInt(chonMonStr.trim());
            } catch (NumberFormatException e) {
                System.out.println("Vui lòng nhập số hợp lệ.");
                continue;
            }
            if (chonMon == 1) monDAO.xuat();
            else if (chonMon == 2) monDAO.them();
            else if (chonMon == 3) monDAO.sua();
            else if (chonMon == 4) monDAO.xoa();
            else if (chonMon == 5) monDAO.timkiem();
            else if (chonMon == 0) break;
            else System.out.println("Chức năng không hợp lệ.");
        }
    }

    private void quanLyNguyenLieu() {
        while (true) {
            ConsoleUI.printHeader("NGUYÊN LIỆU");
            ConsoleUI.printSection("CHỨC NĂNG");
            System.out.println("│ 1. Xem danh sách nguyên liệu       │");
            System.out.println("│ 2. Thêm nguyên liệu                │");
            System.out.println("│ 3. Sửa nguyên liệu                 │");
            System.out.println("│ 4. Xóa nguyên liệu                 │");
            ConsoleUI.printSection("ĐIỀU HƯỚNG");
            System.out.println("│ 0. Quay lại                        │");
            ConsoleUI.printFooter();
            System.out.print(ConsoleUI.promptLabel("Chọn chức năng"));
            String chonNLStr = scanner.nextLine();
            int chonNL;
            try { chonNL = Integer.parseInt(chonNLStr.trim()); } catch (NumberFormatException e) { System.out.println("Vui lòng nhập số hợp lệ."); continue; }
            if (chonNL == 1) nguyenLieuDAO.xuat();
            else if (chonNL == 2) nguyenLieuDAO.them();
            else if (chonNL == 3) nguyenLieuDAO.sua();
            else if (chonNL == 4) nguyenLieuDAO.xoa();
            else if (chonNL == 0) break;
            else System.out.println("Chức năng không hợp lệ.");
        }
    }
}