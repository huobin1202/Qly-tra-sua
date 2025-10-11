import java.util.Scanner;

import dao.KhachHangDAO;
import dao.LoaiMonDAO;
import dao.MonDAO;
import dao.NhanVienDAO;
import dao.DonDatHangDAO;
import dao.NhaCungCapDAO;
import dao.GiaoHangDAO;
import view.NhanVienView;
import view.HangHoaView;
import view.KhachHangView;
import view.DatHangView;
import view.NhaCungCapView;
import view.ConsoleUI;

import java.sql.*; // Thêm import nàyjavac -encoding UTF-8 Runner.java

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
                db.Session.currentMaNV = rs.getInt("MaNV");
                db.Session.currentTaiKhoan = rs.getString("TaiKhoan");
                System.out.println("Đăng nhập thành công!");
                return true;s
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
        DonDatHangDAO DonDatHang = new DonDatHangDAO();
        NhaCungCapDAO NhaCungCap = new NhaCungCapDAO();
        GiaoHangDAO GiaoHang = new GiaoHangDAO();

        

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
                    HangHoaView.menu(LoaiMon, Mon, sc);
                    break;
                case 2:
                    KhachHangView.menu(KhachHang, sc);
                    break;
                case 3:
                    NhanVienView.menu(NhanVien, sc);
                    break;
                case 4:
                    DatHangView.menu(DonDatHang, GiaoHang, sc);
                    break;
                case 5:
                    NhaCungCapView.menu(NhaCungCap, sc);
                    break;
                case 6:
                    view.KhoHangView.menu(sc); // reuse view to create and later list
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