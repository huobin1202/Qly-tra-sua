package view;

import java.util.Scanner;
import dao.NhaCungCapDAO;

public class NhaCungCapView {
    private NhaCungCapDAO nhaCungCapDAO;
    private Scanner scanner;

    public NhaCungCapView() {
        this.nhaCungCapDAO = new NhaCungCapDAO();
        this.scanner = new Scanner(System.in);
    }

    public void menu() {
        while (true) {
            ConsoleUI.printHeader("QUẢN LÝ NHÀ CUNG CẤP");
            ConsoleUI.printSection("CHỨC NĂNG");
            System.out.println("│ 1. Xem danh sách nhà cung cấp                │");
            System.out.println("│ 2. Thêm nhà cung cấp                         │");
            System.out.println("│ 3. Sửa thông tin nhà cung cấp                │");
            System.out.println("│ 4. Xóa nhà cung cấp                          │");
            System.out.println("│ 5. Tìm kiếm nhà cung cấp                     │");
            ConsoleUI.printSection("ĐIỀU HƯỚNG");
            System.out.println("│ 0. Quay lại                                  │");
            ConsoleUI.printFooter();
            System.out.print(ConsoleUI.promptLabel("Chọn chức năng"));
            String chonStr = scanner.nextLine();
            int chon;
            try {
                chon = Integer.parseInt(chonStr.trim());
            } catch (NumberFormatException e) {
                System.out.println("Vui lòng nhập số hợp lệ.");
                continue;
            }
            if (chon == 1)
                xuat();
            else if (chon == 2)
                them();
            else if (chon == 3)
                sua();
            else if (chon == 4)
                xoa();
            else if (chon == 5)
                timkiem();
            else if (chon == 0)
                break;
            else
                System.out.println("Chức năng không hợp lệ.");
        }
    }

    private void xuat() {
        ConsoleUI.printHeader("DANH SÁCH NCC");
        nhaCungCapDAO.xuat();
        ConsoleUI.pause();
    }

    private void them() {
        ConsoleUI.printHeader("THÊM NCC");
        nhaCungCapDAO.them();
        ConsoleUI.pause();
    }

    private void sua() {
        ConsoleUI.printHeader("SỬA NCC");
        nhaCungCapDAO.sua();
        ConsoleUI.pause();
    }

    private void xoa() {
        ConsoleUI.printHeader("XÓA NCC");
        nhaCungCapDAO.xoa();
        ConsoleUI.pause();
    }

    private void timkiem() {
        ConsoleUI.printHeader("TÌM KIẾM NCC");
        nhaCungCapDAO.timkiem();
        ConsoleUI.pause();
    }
}