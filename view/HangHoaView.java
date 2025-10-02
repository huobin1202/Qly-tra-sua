package view;

import java.util.Scanner;
import dao.DSLoaiMon;
import dao.DSMon;

public class HangHoaView {
    public static void menu(DSLoaiMon dsLoaiMon, DSMon dsMon, Scanner sc) {
        while (true) {
            System.out.println("\n╔══════════════════════════════════════════════╗");
            System.out.println("║           QUẢN LÝ HÀNG HÓA                  ║");
            System.out.println("╠══════════════════════════════════════════════╣");
            System.out.println("║ 1. Loại món                                 ║");
            System.out.println("║ 2. Món                                      ║");
            System.out.println("║ 3. Quay lại                                 ║");
            System.out.println("╚══════════════════════════════════════════════╝");
            System.out.print("Chọn chức năng: ");
            int chonHH = sc.nextInt();
            sc.nextLine();
            if (chonHH == 1) {
                while (true) {
                    System.out.println("\n╔════════════════════════════════════╗");
                    System.out.println("║              LOẠI MÓN              ║");
                    System.out.println("╠════════════════════════════════════╣");
                    System.out.println("║ 1. Thêm loại món                   ║");
                    System.out.println("║ 2. Sửa loại món                    ║");
                    System.out.println("║ 3. Xóa loại món                    ║");
                    System.out.println("║ 4. Xem danh sách loại món          ║");
                    System.out.println("║ 5. Quay lại                        ║");
                    System.out.println("╚════════════════════════════════════╝");
                    System.out.print("Chọn chức năng: ");
                    int chonLM = sc.nextInt();
                    sc.nextLine();
                    if (chonLM == 1) dsLoaiMon.them();
                    else if (chonLM == 2) dsLoaiMon.sua();
                    else if (chonLM == 3) dsLoaiMon.xoa();
                    else if (chonLM == 4) dsLoaiMon.xuat();
                    else if (chonLM == 5) break;
                    else System.out.println("Chức năng không hợp lệ.");
                }
            } else if (chonHH == 2) {
                while (true) {
                    System.out.println("\n╔════════════════════════════════════╗");
                    System.out.println("║                 MÓN                ║");
                    System.out.println("╠════════════════════════════════════╣");
                    System.out.println("║ 1. Thêm món                        ║");
                    System.out.println("║ 2. Sửa món                         ║");
                    System.out.println("║ 3. Xóa món                         ║");
                    System.out.println("║ 4. Xem danh sách món               ║");
                    System.out.println("║ 5. Tìm kiếm món                    ║");
                    System.out.println("║ 6. Quay lại                        ║");
                    System.out.println("╚════════════════════════════════════╝");
                    System.out.print("Chọn chức năng: ");
                    int chonMon = sc.nextInt();
                    sc.nextLine();
                    if (chonMon == 1) dsMon.them();
                    else if (chonMon == 2) dsMon.sua();
                    else if (chonMon == 3) dsMon.xoa();
                    else if (chonMon == 4) dsMon.xuat();
                    else if (chonMon == 5) dsMon.timkiem();
                    else if (chonMon == 6) break;
                    else System.out.println("Chức năng không hợp lệ.");
                }
            } else if (chonHH == 3) {
                break;
            }
        }
    }
}