import java.util.Scanner;

import view.*;
import java.sql.*; // Thêm import này

public class Runner {
    public static boolean dangNhap(Scanner sc) {
        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.println("║                                                  ║");
        System.out.println("║            CHÀO MỪNG ĐẾN VỚI HỆ THỐNG            ║");
        System.out.println("║               QUẢN LÝ QUÁN TRÀ SỮA               ║");
        System.out.println("║                                                  ║");
        System.out.println("╚══════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.println("║                ĐĂNG NHẬP HỆ THỐNG                ║");
        System.out.println("╠══════════════════════════════════════════════════╣");
        System.out.print ("║ 👤 Tên tài khoản: ");
        String user = sc.nextLine();
        System.out.print ("║ 🔒 Mật khẩu: ");
        String pass = sc.nextLine();
        System.out.println("╚══════════════════════════════════════════════════╝");
        System.out.println("⏳ Đang kiểm tra thông tin đăng nhập...");
        


        // Kết nối CSDL kiểm tra tài khoản
        try (
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/bants", "root", ""); // sửa user/pass nếu cần
            PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM nhanvien WHERE TaiKhoan=? AND MatKhau=?")
        ) {
            ps.setString(1, user);
            ps.setString(2, pass);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                database.Session.currentMaNV = rs.getInt("MaNV");
                database.Session.currentTaiKhoan = rs.getString("TaiKhoan");
                System.out.println("Đăng nhập thành công!");
                return true;
            } else {
                System.out.println("Sai tài khoản hoặc mật khẩu!");
                return false;
            }
        } catch (Exception e) {
            System.out.println("Lỗi kết nối CSDL: " + e.getMessage());
            return false;
        }
    }

    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        // Đăng nhập trước khi vào hệ thống
        boolean loginSuccess = false;
        for (int i = 0; i < 100; i++) {
            if (dangNhap(sc)) {
                loginSuccess = true;
                break;
            }
        }
        if (!loginSuccess) {
            System.out.println("Đăng nhập thất bại. Thoát chương trình.");
            sc.close();
            return;
        }

        // DAOs và Views sẽ được khởi tạo khi người dùng chọn menu tương ứng
        NhapHangView NhapHang = new NhapHangView();
        

        while (true) {
            view.ConsoleUI.printHeader("MENU HỆ THỐNG");
            view.ConsoleUI.printSection("KHU VỰC QUẢN LÝ");
            System.out.println("│ 1. Quản lý hàng hóa                          │");
            System.out.println("│ 2. Quản lý khách hàng                        │");
            System.out.println("│ 3. Quản lý nhân viên                         │");
            System.out.println("│ 4. Quản lý đặt hàng                          │");
            System.out.println("│ 5. Quản lý nhà cung cấp                      │");
            System.out.println("│ 6. Quản lý phiếu nhập                        │");
            System.out.println("│ 7. Tồn kho                                   │");
            view.ConsoleUI.printSection("THỐNG KÊ (đang cập nhật)");
            System.out.println("│ 9. Thống kê doanh thu                        │");
            view.ConsoleUI.printSection("HỆ THỐNG");
            System.out.println("│ 0. Thoát                                     │");
            view.ConsoleUI.printFooter();
            System.out.print(view.ConsoleUI.promptLabel("Chọn chức năng"));
            String chonStr = sc.nextLine();
            int chon;
            try {
                chon = Integer.parseInt(chonStr.trim());
            } catch (NumberFormatException e) {
                System.out.println("Vui lòng nhập số hợp lệ.");
                continue;
            }

            switch (chon) {
                case 1:
                    HangHoaView hangHoaView = new HangHoaView();
                    hangHoaView.menu();
                    break;
                case 2:
                    KhachHangView khachHangView = new KhachHangView();
                    khachHangView.menu();
                    break;
                case 3:
                    NhanVienView nhanVienView = new NhanVienView();
                    nhanVienView.menu();
                    break;
                case 4:
                    DonHangView donHangView = new DonHangView();
                    donHangView.menu();
                    break;
                case 5:
                    NhaCungCapView nhaCungCapView = new NhaCungCapView();
                    nhaCungCapView.menu();
                    break;
                case 6:
                    NhapHang.menu();
                    break;
                case 7:
                    KhoHangView khoHangView = new KhoHangView();
                    khoHangView.menu();
                    break;
                case 0:
                    System.out.println("Thoát chương trình.");
                    sc.close();
                    return;
                default:
                    System.out.println("Chức năng không hợp lệ. Vui lòng chọn lại.");
            }
        }
    }
}