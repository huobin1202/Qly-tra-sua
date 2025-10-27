import javax.swing.*;
import view.*;
import dao.NhapHangDAO;
import dao.DonHangDAO;
import dao.MonDAO;
import dao.NhanVienDAO;

import static javax.swing.UIManager.*;

public class Runner {
    public static void main(String[] args) {
        // Thiết lập Look and Feel
        try {
            setLookAndFeel(getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }
        // Chạy trên EDT (Event Dispatch Thread)
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Hiển thị màn hình đăng nhập
                LoginDialog loginDialog = new LoginDialog(null);
                loginDialog.setVisible(true);
                
                // Kiểm tra đăng nhập thành công
                if (loginDialog.isLoginSuccessful()) {
                    // Đóng dialog đăng nhập
                    loginDialog.dispose();
                    
                    // Chuẩn hóa trạng thái tất cả các bảng trước khi hiển thị ứng dụng
                    
                    DonHangDAO donHangDAO = new DonHangDAO();
                    donHangDAO.chuanHoaTrangThaiDonHang();
                    
                    MonDAO monDAO = new MonDAO();
                    monDAO.chuanHoaTrangThaiMon();
                    
                    NhanVienDAO nhanVienDAO = new NhanVienDAO();
                    nhanVienDAO.chuanHoaChucVuNhanVien();
                    
                    // Hiển thị ứng dụng chính
                    MainDashboard mainApp = new MainDashboard() ;
                    mainApp.setVisible(true);
                } else {
                    // Thoát chương trình nếu đăng nhập thất bại
                    System.exit(0);
                }
            }
        });
    }
}