import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import dao.KhachHangDAO;
import dao.LoaiMonDAO;
import dao.MonDAO;
import dao.NhanVienDAO;
import dao.BanDAO;
import dao.DonDatHangDAO;
import dao.NhaCungCapDAO;
import dao.GiaoHangDAO;
import view.NhanVienView;
import view.HangHoaView;
import view.KhachHangView;
import view.BanView;
import view.DatHangView;
import view.NhaCungCapView;

import java.sql.*; // Thêm import này

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

    public static void main(String[] args) throws Exception {
        // Ép input luôn UTF-8
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

        KhachHangDAO KhachHang = new KhachHangDAO();
        LoaiMonDAO LoaiMon = new LoaiMonDAO();
        MonDAO Mon = new MonDAO();
        NhanVienDAO NhanVien = new NhanVienDAO();
        BanDAO Ban = new BanDAO();
        DonDatHangDAO DonDatHang = new DonDatHangDAO();
        NhaCungCapDAO NhaCungCap = new NhaCungCapDAO();
        GiaoHangDAO GiaoHang = new GiaoHangDAO();

        while (true) {
            System.out.println("\n╔══════════════════════════════════════════════╗");
            System.out.println("║                MENU HỆ THỐNG                ║");
            System.out.println("╠══════════════════════════════════════════════╣");
            System.out.println("║ 1. Quản lý hàng hóa                         ║");
            System.out.println("║ 2. Quản lý khách hàng                       ║");
            System.out.println("║ 3. Quản lý nhân viên                        ║");
            System.out.println("║ 4. Quản lý đặt hàng                         ║");
            System.out.println("║ 5. Quản lý nhà cung cấp                     ║");
            //System.out.println("║ 7. Nhập hàng                                ║");
            //System.out.println("║ 8. Quản lý kho                              ║");
            //System.out.println("║ 9. Xuất hàng                                ║");
            //System.out.println("║ 10. Thống kê                                 ║");
            //System.out.println("║ 0. Thoát                                    ║");
            System.out.println("╚══════════════════════════════════════════════╝");
            System.out.print("Chọn chức năng: ");
            int chon = sc.nextInt();
            sc.nextLine();

            switch (chon) {
                case 1:
                    HangHoaView.menu(LoaiMon, Mon, sc);
                    break;
                case 2:
                    KhachHangView.menu(KhachHang, sc);
                    break;
                case 3:
                    NhanVienView.menu(NhanVien, sc);
                    break;
                case 4:
                    DatHangView.menu(Ban, DonDatHang, GiaoHang, sc);
                    break;
                case 5:
                    NhaCungCapView.menu(NhaCungCap, sc);
                    return;
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