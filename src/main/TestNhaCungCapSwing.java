package main;

import view.NhaCungCapSwingView;
import view.MainFrameInterface;
import javax.swing.*;

public class TestNhaCungCapSwing {
    public static void main(String[] args) {
        System.out.println("=== TEST NHÀ CUNG CẤP SWING ===");
        
        SwingUtilities.invokeLater(() -> {
            try {
                JFrame frame = new JFrame("Test Nhà Cung Cấp");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(1000, 600);
                frame.setLocationRelativeTo(null);
                
                // Create a dummy MainFrameInterface
                MainFrameInterface dummyParent = new MainFrameInterface() {
                    // Empty implementation
                };
                
                NhaCungCapSwingView nccView = new NhaCungCapSwingView(dummyParent);
                frame.add(nccView);
                
                frame.setVisible(true);
                System.out.println("✅ Giao diện nhà cung cấp đã được tạo thành công!");
                System.out.println("Hãy thử thêm/sửa/xóa nhà cung cấp để kiểm tra...");
                
            } catch (Exception e) {
                System.err.println("❌ Lỗi tạo giao diện: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}
