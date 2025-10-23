package main;

import view.NhapHangMoiSwingView;
import javax.swing.*;

public class TestNhapHangMoi {
    public static void main(String[] args) {
        System.out.println("=== TEST NHẬP HÀNG MỚI ===");
        
        SwingUtilities.invokeLater(() -> {
            try {
                JFrame frame = new JFrame("Test Nhập Hàng Mới");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(1200, 800);
                frame.setLocationRelativeTo(null);
                
                NhapHangMoiSwingView nhapHangView = new NhapHangMoiSwingView();
                frame.add(nhapHangView);
                
                frame.setVisible(true);
                System.out.println("✅ Giao diện nhập hàng mới đã được tạo thành công!");
                
            } catch (Exception e) {
                System.err.println("❌ Lỗi tạo giao diện: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}
