import java.util.Scanner;

import dao.CustomerList;
import dao.DSLoaiMon;
import dao.DSMon;
import dao.DSNhanVien;
import dao.DSQuanLyBan;
import dao.DSQuanLyDonDatHang;

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

    public static void main(String[] args) {
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

        CustomerList dsKhachHang = new CustomerList();
        DSLoaiMon dsLoaiMon = new DSLoaiMon();
        DSMon dsMon = new DSMon();
        DSNhanVien dsNhanVien = new DSNhanVien();
        DSQuanLyBan dsBan = new DSQuanLyBan();
        DSQuanLyDonDatHang dsDonDatHang = new DSQuanLyDonDatHang();

        while (true) {
            System.out.println("\n╔══════════════════════════════════════════════╗");
            System.out.println("║                MENU HỆ THỐNG                ║");
            System.out.println("╠══════════════════════════════════════════════╣");
            System.out.println("║ 1. Quản lý hàng hóa                         ║");
            System.out.println("║ 2. Quản lý khách hàng                       ║");
            System.out.println("║ 3. Quản lý nhân viên                        ║");
            System.out.println("║ 4. Quản lý đặt hàng                         ║");
            System.out.println("║ 5. Thoát                                    ║");
            System.out.println("╚══════════════════════════════════════════════╝");
            System.out.print("Chọn chức năng: ");
            int chon = sc.nextInt();
            sc.nextLine();

            switch (chon) {
                case 1:
                    while (true) {
                        System.out.println("\n*** Quan ly hang hoa ***");
                        System.out.println("1. Loai mon");
                        System.out.println("2. Mon");
                        System.out.println("3. Tro ve menu chinh");
                        System.out.print("Chon chuc nang: ");
                        int chonHH = sc.nextInt();
                        sc.nextLine();
                        if (chonHH == 1) {
                            while (true) {
                                System.out.println("\n--- Loai mon ---");
                                System.out.println("1. Them loai mon");
                                System.out.println("2. Sua loai mon");
                                System.out.println("3. Xoa loai mon");
                                System.out.println("4. Xem danh sach loai mon");
                                System.out.println("5. Quay lai");
                                System.out.print("Chon chuc nang: ");
                                int chonLM = sc.nextInt();
                                sc.nextLine();
                                if (chonLM == 1) dsLoaiMon.them();
                                else if (chonLM == 2) dsLoaiMon.sua();
                                else if (chonLM == 3) dsLoaiMon.xoa();
                                else if (chonLM == 4) dsLoaiMon.xuat();
                                else if (chonLM == 5) break;
                                else System.out.println("Chuc nang khong hop le.");
                            }
                        } else if (chonHH == 2) {
                            while (true) {
                                System.out.println("\n--- Mon ---");
                                System.out.println("1. Them mon");
                                System.out.println("2. Sua mon");
                                System.out.println("3. Xoa mon");
                                System.out.println("4. Xem danh sach mon");
                                System.out.println("5. Tim kiem mon");
                                System.out.println("6. Quay lai");
                                System.out.print("Chon chuc nang: ");
                                int chonMon = sc.nextInt();
                                sc.nextLine();
                                if (chonMon == 1) dsMon.them();
                                else if (chonMon == 2) dsMon.sua();
                                else if (chonMon == 3) dsMon.xoa();
                                else if (chonMon == 4) dsMon.xuat();
                                else if (chonMon == 5) dsMon.timkiem();
                                else if (chonMon == 6) break;
                                else System.out.println("Chuc nang khong hop le.");
                            }
                        } else if (chonHH == 3) {
                            break;
                        }
                    }
                    break;
                case 2: // Quản lý khách hàng
                    int chonKhachHang;
                    do {
                        System.out.println("\n*** Quan ly khach hang *");
                        System.out.println("1. Xuat danh sach khach hang");
                        System.out.println("2. Them khach hang");
                        System.out.println("3. Sua thong tin khach hang");
                        System.out.println("4. Xoa khach hang");
                        System.out.println("5. Tim kiem khach hang");
                        System.out.println("6. Tro ve menu chinh");
                        System.out.print("Chon chuc nang: ");
                        chonKhachHang = sc.nextInt();
                        sc.nextLine();

                        switch (chonKhachHang) {
                            case 1:
                                dsKhachHang.xuat();
                                break;
                            case 2:
                                dsKhachHang.them();
                                break;
                            case 3:
                                dsKhachHang.sua();
                                break;
                            case 4:
                                dsKhachHang.xoa();
                                break;
                            case 5:
                                dsKhachHang.timkiem();
                                break;
                            case 6:
                                System.out.println("Tro ve menu chinh.");
                                break;
                            default:
                                System.out.println("Chuc nang khong hop le. Vui long chon lai.");
                        }
                    } while (chonKhachHang != 7);
                    break;

                case 3: // Quản lý nhân viên
                    while (true) {
                        System.out.println("\n╔══════════════════════════════════════════════╗");
                        System.out.println("║           QUẢN LÝ NHÂN VIÊN                 ║");
                        System.out.println("╠══════════════════════════════════════════════╣");
                        System.out.println("║ 1. Thêm nhân viên                           ║");
                        System.out.println("║ 2. Sửa nhân viên                            ║");
                        System.out.println("║ 3. Xóa nhân viên                            ║");
                        System.out.println("║ 4. Xem danh sách nhân viên                  ║");
                        System.out.println("║ 5. Tìm kiếm nhân viên                       ║");
                        System.out.println("║ 6. Quay lại                                 ║");
                        System.out.println("╚══════════════════════════════════════════════╝");
                        System.out.print("Chọn chức năng: ");
                        int chonNV = sc.nextInt();
                        sc.nextLine();
                        if (chonNV == 1) dsNhanVien.them();
                        else if (chonNV == 2) dsNhanVien.sua();
                        else if (chonNV == 3) dsNhanVien.xoa();
                        else if (chonNV == 4) dsNhanVien.xuat();
                        else if (chonNV == 5) dsNhanVien.timkiem();
                        else if (chonNV == 6) break;
                        else System.out.println("Chức năng không hợp lệ.");
                    }
                    break;
                case 4: // Quản lý đặt hàng
                    while (true) {
                        System.out.println("\n╔══════════════════════════════════════════════╗");
                        System.out.println("║             QUẢN LÝ ĐẶT HÀNG                ║");
                        System.out.println("╠══════════════════════════════════════════════╣");
                        System.out.println("║ 1. Quản lý bàn                              ║");
                        System.out.println("║ 2. Quản lý đơn đặt hàng                     ║");
                        System.out.println("║ 3. Quay lại                                 ║");
                        System.out.println("╚══════════════════════════════════════════════╝");
                        System.out.print("Chọn chức năng: ");
                        int chonDH = sc.nextInt();
                        sc.nextLine();
                        if (chonDH == 1) {
                            while (true) {
                                System.out.println("\n--- Quản lý bàn ---");
                                System.out.println("1. Thêm bàn");
                                System.out.println("2. Sửa bàn");
                                System.out.println("3. Xóa bàn");
                                System.out.println("4. Xem danh sách bàn");
                                System.out.println("5. Quay lại");
                                System.out.print("Chọn chức năng: ");
                                int chonBan = sc.nextInt();
                                sc.nextLine();
                                if (chonBan == 1) dsBan.them();
                                else if (chonBan == 2) dsBan.sua();
                                else if (chonBan == 3) dsBan.xoa();
                                else if (chonBan == 4) dsBan.xuat();
                                else if (chonBan == 5) break;
                                else System.out.println("Chức năng không hợp lệ.");
                            }
                        } else if (chonDH == 2) {
                            while (true) {
                                System.out.println("\n--- Quản lý đơn đặt hàng ---");
                                System.out.println("1. Thêm đơn đặt hàng");
                                System.out.println("2. Sửa đơn đặt hàng");
                                System.out.println("3. Xóa đơn đặt hàng");
                                System.out.println("4. Xem danh sách đơn đặt hàng");
                                System.out.println("5. Tìm kiếm đơn đặt hàng");
                                System.out.println("6. Quay lại");
                                System.out.print("Chọn chức năng: ");
                                int chonDon = sc.nextInt();
                                sc.nextLine();
                                if (chonDon == 1) dsDonDatHang.them();
                                else if (chonDon == 2) dsDonDatHang.sua();
                                else if (chonDon == 3) dsDonDatHang.xoa();
                                else if (chonDon == 4) dsDonDatHang.xuat();
                                else if (chonDon == 5) dsDonDatHang.timkiem();
                                else if (chonDon == 6) break;
                                else System.out.println("Chức năng không hợp lệ.");
                            }
                        } else if (chonDH == 3) break;
                        else System.out.println("Chức năng không hợp lệ.");
                    }
                    break;
                case 5:
                    System.out.println("Thoát chương trình.");
                    sc.close();
                    return;
                default:
                    System.out.println("Chức năng không hợp lệ. Vui lòng chọn lại.");
            }
        }
    }
}