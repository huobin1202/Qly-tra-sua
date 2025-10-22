package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainSwingApp extends JFrame {
    private JPanel mainPanel;
    private CardLayout cardLayout;
    
    public MainSwingApp() {
        initializeComponents();
        setupLayout();
        setupEventHandlers();
    }
    
    private void initializeComponents() {
        setTitle("Hệ thống quản lý quán trà sữa");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        
        // Tạo CardLayout để chuyển đổi giữa các màn hình
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        // Tạo menu chính
        createMainMenu();
        
        // Tạo các view con
        createChildViews();
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private void createMainMenu() {
        JPanel menuPanel = new JPanel(new BorderLayout());
        menuPanel.setBackground(new Color(240, 248, 255));
        
        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(70, 130, 180));
        headerPanel.setPreferredSize(new Dimension(0, 80));
        
        JLabel titleLabel = new JLabel("HỆ THỐNG QUẢN LÝ QUÁN TRÀ SỮA");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(titleLabel);
        
        // Menu buttons
        JPanel buttonPanel = new JPanel(new GridLayout(3, 3, 20, 20));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        buttonPanel.setBackground(new Color(240, 248, 255));
        
        // Tạo các nút menu
        String[] menuItems = {
            "Quản lý hàng hóa", "Quản lý khách hàng", "Quản lý nhân viên",
            "Quản lý đặt hàng", "Quản lý nhà cung cấp", "Quản lý phiếu nhập",
            "Tồn kho", "Thống kê doanh thu", "Thoát"
        };
        
        for (String item : menuItems) {
            JButton button = createMenuButton(item);
            buttonPanel.add(button);
        }
        
        menuPanel.add(headerPanel, BorderLayout.NORTH);
        menuPanel.add(buttonPanel, BorderLayout.CENTER);
        
        mainPanel.add(menuPanel, "MAIN_MENU");
    }
    
    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setPreferredSize(new Dimension(200, 60));
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createRaisedBorder(5, 5));
        
        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(100, 149, 237));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(70, 130, 180));
            }
        });
        
        return button;
    }
    
    private void createChildViews() {
        // Tạo các view con và thêm vào mainPanel
        mainPanel.add(new NhaCungCapSwingView(this), "NHA_CUNG_CAP");
        mainPanel.add(new KhachHangSwingView(this), "KHACH_HANG");
        mainPanel.add(new NhanVienSwingView(this), "NHAN_VIEN");
        mainPanel.add(new DonHangSwingView(this), "DON_HANG");
        mainPanel.add(new HangHoaSwingView(this), "HANG_HOA");
        mainPanel.add(new NhapHangSwingView(this), "NHAP_HANG");
        mainPanel.add(new KhoHangSwingView(this), "KHO_HANG");
    }
    
    private void setupEventHandlers() {
        // Lấy tất cả các button từ main menu
        JPanel menuPanel = (JPanel) mainPanel.getComponent(0);
        JPanel buttonPanel = (JPanel) menuPanel.getComponent(1);
        
        Component[] components = buttonPanel.getComponents();
        for (int i = 0; i < components.length; i++) {
            if (components[i] instanceof JButton) {
                JButton button = (JButton) components[i];
                final int index = i;
                button.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        handleMenuSelection(index);
                    }
                });
            }
        }
    }
    
    private void handleMenuSelection(int index) {
        switch (index) {
            case 0: // Quản lý hàng hóa
                cardLayout.show(mainPanel, "HANG_HOA");
                break;
            case 1: // Quản lý khách hàng
                cardLayout.show(mainPanel, "KHACH_HANG");
                break;
            case 2: // Quản lý nhân viên
                cardLayout.show(mainPanel, "NHAN_VIEN");
                break;
            case 3: // Quản lý đặt hàng
                cardLayout.show(mainPanel, "DON_HANG");
                break;
            case 4: // Quản lý nhà cung cấp
                cardLayout.show(mainPanel, "NHA_CUNG_CAP");
                break;
            case 5: // Quản lý phiếu nhập
                cardLayout.show(mainPanel, "NHAP_HANG");
                break;
            case 6: // Tồn kho
                cardLayout.show(mainPanel, "KHO_HANG");
                break;
            case 7: // Thống kê doanh thu
                JOptionPane.showMessageDialog(this, "Chức năng đang được phát triển", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                break;
            case 8: // Thoát
                int result = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn thoát?", "Xác nhận", JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
                break;
        }
    }
    
    public void showMainMenu() {
        cardLayout.show(mainPanel, "MAIN_MENU");
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
                new MainSwingApp().setVisible(true);
            }
        });
    }
}
