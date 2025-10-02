import java.util.Scanner;

import dao.DSCustomer;
import dao.DSLoaiMon;
import dao.DSMon;
import dao.DSNhanVien;
import dao.DSBan;
import dao.DSDonDatHang;
import view.NhanVienView;
import view.HangHoaView;
import view.KhachHangView;
import view.BanView;
import view.DatHangView;

import java.sql.*; // Thêm import này
import javax.swing.SwingUtilities;
import view.gui.MainFrame;
import view.gui.LoginDialog;

public class Runner {
    public static boolean dangNhap(Scanner sc) {
        System.out.println("===== ĐĂNG NHẬP =====");
        System.out.print("Tên tài khoản: ");
        String user = sc.nextLine();
        System.out.print("Mật khẩu: ");
        String pass = sc.nextLine();

        // Kết nối CSDL kiểm tra tài khoản
        try (
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/bants", "root", ""); // sửa user/pass nếu cần
            PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM nhanvien WHERE tentaikhoan=? AND matkhau=?")
        ) {
            ps.setString(1, user);
            ps.setString(2, pass);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
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

    public static void main(String[] args) {
        // Mặc định mở GUI. Dùng --cli để chạy giao diện dòng lệnh
        if (args == null || args.length == 0 || "--gui".equalsIgnoreCase(args[0])) {
            SwingUtilities.invokeLater(() -> {
                LoginDialog login = new LoginDialog(null);
                login.setVisible(true);
                if (login.isAuthenticated()) {
                    new MainFrame().setVisible(true);
                } else {
                    System.out.println("Đăng nhập GUI thất bại. Mở giao diện dòng lệnh.");
                    new Thread(Runner::runCli).start();
                }
            });
            return;
        }
        if ("--cli".equalsIgnoreCase(args[0])) {
            runCli();
            return;
        }
        // Tham số không hợp lệ -> fallback CLI
        runCli();
    }

    private static void runCli() {
        Scanner sc = new Scanner(System.in);

        // Đăng nhập trước khi vào hệ thống
        boolean loginSuccess = false;
        for (int i = 0; i < 3; i++) {
            if (dangNhap(sc)) {
                loginSuccess = true;
                break;
            }
            System.out.println("Bạn còn " + (2 - i) + " lần thử.");
        }
        if (!loginSuccess) {
            System.out.println("Đăng nhập thất bại. Thoát chương trình.");
            sc.close();
            return;
        }

        DSCustomer dsKhachHang = new DSCustomer();
        DSLoaiMon dsLoaiMon = new DSLoaiMon();
        DSMon dsMon = new DSMon();
        DSNhanVien dsNhanVien = new DSNhanVien();
        DSBan dsBan = new DSBan();
        DSDonDatHang dsDonDatHang = new DSDonDatHang();

        while (true) {
            System.out.println("\n╔══════════════════════════════════════════════╗");
            System.out.println("║                MENU HỆ THỐNG                ║");
            System.out.println("╠══════════════════════════════════════════════╣");
            System.out.println("║ 1. Quản lý hàng hóa                         ║");
            System.out.println("║ 2. Quản lý khách hàng                       ║");
            System.out.println("║ 3. Quản lý nhân viên                        ║");
            System.out.println("║ 4. Quản lý đặt hàng                         ║");
            //System.out.println("║ 4. Quản lý nhà cung cấp                     ║");
            //System.out.println("║ 4. Quản lý kho                     ║");
            //System.out.println("║ 4. Phiếu nhập                     ║");
            //System.out.println("║ 4. Phiếu xuất                     ║");
            System.out.println("║ 5. Thống kê                                 ║");
            System.out.println("║ 6. Thoát                                    ║");
            System.out.println("╚══════════════════════════════════════════════╝");
            System.out.print("Chọn chức năng: ");
            int chon = sc.nextInt();
            sc.nextLine();

            switch (chon) {
                case 1:
                    HangHoaView.menu(dsLoaiMon, dsMon, sc);
                    break;
                case 2:
                    KhachHangView.menu(dsKhachHang, sc);
                    break;
                case 3:
                    NhanVienView.menu(dsNhanVien, sc);
                    break;
                case 4:
                    DatHangView.menu(dsBan, dsDonDatHang, sc);
                    break;
                case 5:
                    System.out.println("Thoát chương trình.");
                    sc.close();
                    return;
                case 6:
                    System.out.println("Thoát chương trình.");
                    sc.close();
                    return;
                default:
                    System.out.println("Chức năng không hợp lệ. Vui lòng chọn lại.");
            }
        }
    }
}