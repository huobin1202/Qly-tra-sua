package view;

import java.util.Scanner;
import dao.LoaiMonDAO;
import dao.MonDAO;

public class HangHoaView {
    public static void menu(LoaiMonDAO dsLoaiMon, MonDAO dsMon, Scanner sc) {
        while (true) {
            ConsoleUI.printHeader("QUẢN LÝ HÀNG HÓA");
            ConsoleUI.printSection("KHU VỰC QUẢN LÝ");
            System.out.println("│ 1. Loại món                                  │");
            System.out.println("│ 2. Món                                       │");
            ConsoleUI.printSection("ĐIỀU HƯỚNG");
            System.out.println("│ 3. Quay lại                                  │");
            ConsoleUI.printFooter();
            System.out.print(ConsoleUI.promptLabel("Chọn chức năng"));
            String chonHHStr = sc.nextLine();
            int chonHH;
            try {
                chonHH = Integer.parseInt(chonHHStr.trim());
            } catch (NumberFormatException e) {
                System.out.println("Vui lòng nhập số hợp lệ.");
                continue;
            }
            if (chonHH == 1) {
                while (true) {
                    ConsoleUI.printHeader("LOẠI MÓN");
                    ConsoleUI.printSection("CHỨC NĂNG");
                    System.out.println("│ 1. Thêm loại món                    │");
                    System.out.println("│ 2. Sửa loại món                     │");
                    System.out.println("│ 3. Xóa loại món                     │");
                    System.out.println("│ 4. Xem danh sách loại món           │");
                    ConsoleUI.printSection("ĐIỀU HƯỚNG");
                    System.out.println("│ 5. Quay lại                         │");
                    ConsoleUI.printFooter();
                    System.out.print(ConsoleUI.promptLabel("Chọn chức năng"));
                    String chonLMStr = sc.nextLine();
                    int chonLM;
                    try {
                        chonLM = Integer.parseInt(chonLMStr.trim());
                    } catch (NumberFormatException e) {
                        System.out.println("Vui lòng nhập số hợp lệ.");
                        continue;
                    }
                    if (chonLM == 1) dsLoaiMon.them();
                    else if (chonLM == 2) dsLoaiMon.sua();
                    else if (chonLM == 3) dsLoaiMon.xoa();
                    else if (chonLM == 4) dsLoaiMon.xuat();
                    else if (chonLM == 5) break;
                    else System.out.println("Chức năng không hợp lệ.");
                }
            } else if (chonHH == 2) {
                while (true) {
                    ConsoleUI.printHeader("MÓN");
                    ConsoleUI.printSection("CHỨC NĂNG");
                    System.out.println("│ 1. Thêm món                         │");
                    System.out.println("│ 2. Sửa món                          │");
                    System.out.println("│ 3. Xóa món                          │");
                    System.out.println("│ 4. Xem danh sách món                │");
                    System.out.println("│ 5. Tìm kiếm món                     │");
                    ConsoleUI.printSection("ĐIỀU HƯỚNG");
                    System.out.println("│ 0. Quay lại                         │");
                    ConsoleUI.printFooter();
                    System.out.print(ConsoleUI.promptLabel("Chọn chức năng"));
                    String chonMonStr = sc.nextLine();
                    int chonMon;
                    try {
                        chonMon = Integer.parseInt(chonMonStr.trim());
                    } catch (NumberFormatException e) {
                        System.out.println("Vui lòng nhập số hợp lệ.");
                        continue;
                    }
                    if (chonMon == 1) dsMon.them();
                    else if (chonMon == 2) dsMon.sua();
                    else if (chonMon == 3) dsMon.xoa();
                    else if (chonMon == 4) dsMon.xuat();
                    else if (chonMon == 5) dsMon.timkiem();
                    else if (chonMon == 0) break;
                    else System.out.println("Chức năng không hợp lệ.");
                }
            } else if (chonHH == 3) {
                break;
            }
        }
    }
}