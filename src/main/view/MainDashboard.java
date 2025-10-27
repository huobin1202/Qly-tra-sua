package view;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import database.DBUtil;
import database.Session;

public class MainDashboard extends JFrame implements MainFrameInterface {
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private JPanel leftSidebar;
    private JLabel userInfoLabel;
    private JLabel roleLabel;
    private String currentUserRole; // Lưu chức vụ hiện tại
    
    public MainDashboard() {
        loadUserInfo(); // Load user info trước để có currentUserRole
        initializeComponents();
        setupLayout();
        setupEventHandlers();
    }
    
    private void initializeComponents() {
        setTitle("Trang quản lý");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1400, 900);
        setLocationRelativeTo(null);
        
        // Tạo CardLayout để chuyển đổi giữa các màn hình
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        // Tạo các thành phần chính
        createLeftSidebar();
        //createTopBar();
       // createRightSidebar();
        createChildViews();
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Thêm các panel vào layout
        add(leftSidebar, BorderLayout.WEST);
        //add(topBar, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
        //add(rightSidebar, BorderLayout.EAST);
    }
    
    private void createLeftSidebar() {
        leftSidebar = new JPanel(new BorderLayout());
        leftSidebar.setBackground(new Color(240, 240, 240));
        leftSidebar.setPreferredSize(new Dimension(250, 0));
        
        // Panel thông tin người dùng
        JPanel userPanel = new JPanel(new BorderLayout());
        userPanel.setBackground(new Color(76, 175, 80));
        userPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Nút Admin/Thoát
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        buttonPanel.setOpaque(false);
        
        roleLabel = new JLabel("Admin");
        roleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        roleLabel.setForeground(Color.BLACK);
        roleLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        roleLabel.setBackground(new Color(56, 142, 60));
        roleLabel.setOpaque(true);
        
        JButton exitButton = new JButton("Đăng xuất");
        exitButton.setFont(new Font("Arial", Font.BOLD, 12));
        exitButton.setForeground(Color.BLACK);
        exitButton.setBackground(new Color(76, 175, 80));
        exitButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        exitButton.setFocusPainted(false);
        exitButton.addActionListener(e -> performLogout());
        
        buttonPanel.add(roleLabel);
        buttonPanel.add(exitButton);
        
        userPanel.add(buttonPanel, BorderLayout.NORTH);
        
        // Thông tin người dùng
        userInfoLabel = new JLabel("Đang tải...");
        userInfoLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        userInfoLabel.setForeground(Color.BLACK);
        userInfoLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        userPanel.add(userInfoLabel, BorderLayout.CENTER);
        
        leftSidebar.add(userPanel, BorderLayout.NORTH);
        
        // Menu navigation
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(new Color(240, 240, 240));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        createHangHoaDropdown(menuPanel);

        // Tạo các menu items với icon dựa trên chức vụ
        String[][] menuItems;
        
        // Kiểm tra chức vụ để hiển thị menu phù hợp
        if ("quanly".equals(currentUserRole)) {
            // Quản lý có thể truy cập tất cả
            menuItems = new String[][]{
                {"Quản lý nhân viên", "👥"},
                {"Quản lý nhà cung cấp", "🛒"},
                {"Quản lý khách hàng", "👤"},
                {"Quản lý phiếu nhập", "📋"},
                {"Quản lý đơn hàng", "🛒"},
                {"Kho hàng", "🏬"},
                {"Thống kê", "📊"},
                {"Thiết lập", "⚙️"}
            };
        } else {
            // Nhân viên chỉ được truy cập một số chức năng
            menuItems = new String[][]{
                {"Quản lý khách hàng", "👤"},
                {"Quản lý đơn hàng", "🛒"},
                {"Giao hàng", "🚚"}
            };
        }
        
        for (String[] item : menuItems) {
            JButton menuButton = createMenuButton(item[0], item[1]);
            menuPanel.add(menuButton);
            menuPanel.add(Box.createVerticalStrut(10));
        }
        
        // Tạo dropdown menu cho Quản lý hàng hóa
        
        leftSidebar.add(menuPanel, BorderLayout.CENTER);
        
        // Cập nhật thông tin user sau khi tạo các label
        updateUserInfo();
    }
    
    // Method cập nhật thông tin user
    private void updateUserInfo() {
        try {
            String currentUser = Session.currentTaiKhoan;
            if (currentUser != null && !currentUser.isEmpty()) {
                Connection conn = DBUtil.getConnection();
                String sql = "SELECT HoTen FROM nhanvien WHERE TaiKhoan = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, currentUser);
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    String hoTen = rs.getString("HoTen");
                    String chucVu = Session.currentChucVu; // Sử dụng chức vụ từ Session
                    
                    userInfoLabel.setText("<html><div style='text-align: center;'>" +
                                        "<div style='font-weight: bold;'>" + hoTen + "</div>" +
                                        "<div style='font-size: 10px;'>" + chucVu + "</div></div></html>");
                    
                    // Cập nhật role label
                    if ("quanly".equals(chucVu)) {
                        roleLabel.setText("Quản lý");
                    } else {
                        roleLabel.setText("Nhân viên");
                    }
                }
                
                rs.close();
                stmt.close();
                conn.close();
            }
        } catch (Exception e) {
            userInfoLabel.setText("Không thể tải thông tin");
        }
    }
    
    private JButton createMenuButton(String text, String icon) {
        JButton button = new JButton("<html><div style='text-align: left; padding: 8px;'>" + 
                                   "<span style='font-size: 16px;'>" + icon + "</span> " + 
                                   "<span style='margin-left: 10px;'>" + text + "</span></div></html>");
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setPreferredSize(new Dimension(230, 50));
        button.setMaximumSize(new Dimension(230, 50));
        button.setBackground(Color.BLACK);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        
        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(200, 230, 255));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.BLACK);
            }
        });
        
        // Action listener
        button.addActionListener(e -> handleMenuSelection(text));
        
        return button;
    }
    
    private void createHangHoaDropdown(JPanel menuPanel) {
        // Tạo panel chứa dropdown
        JPanel hangHoaPanel = new JPanel();
        hangHoaPanel.setLayout(new BoxLayout(hangHoaPanel, BoxLayout.Y_AXIS));
        hangHoaPanel.setBackground(new Color(240, 240, 240));
        
        // Nút chính "Quản lý hàng hóa"
        JButton mainButton = new JButton("<html><div style='text-align: left; padding: 8px;'>" + 
                                        "<span style='font-size: 16px;'>📦</span> " + 
                                        "<span style='margin-left: 10px;'>Quản lý hàng hóa</span>" +
                                        "<span style='float: right; font-size: 12px;'>▼</span></div></html>");
        mainButton.setFont(new Font("Arial", Font.PLAIN, 14));
        mainButton.setPreferredSize(new Dimension(230, 50));
        mainButton.setMaximumSize(new Dimension(230, 50));
        mainButton.setBackground(Color.BLACK);
        mainButton.setForeground(Color.BLACK);
        mainButton.setFocusPainted(false);
        mainButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        mainButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainButton.setHorizontalAlignment(SwingConstants.LEFT);
        
        // Panel chứa các submenu (ẩn ban đầu)
        JPanel subMenuPanel = new JPanel();
        subMenuPanel.setLayout(new BoxLayout(subMenuPanel, BoxLayout.Y_AXIS));
        subMenuPanel.setBackground(new Color(250, 250, 250));
        subMenuPanel.setVisible(false);
        
        // Các submenu items
        String[][] subMenuItems = {
            {"Quản lý món", "🍴"},
            {"Quản lý loại món", "🍽️"},
            {"Quản lý nguyên liệu", "📄"}
        };
        
        for (String[] item : subMenuItems) {
            JButton subButton = createSubMenuButton(item[0], item[1]);
            subMenuPanel.add(subButton);
            subMenuPanel.add(Box.createVerticalStrut(5));
        }
        
        // Event handler cho nút chính
        mainButton.addActionListener(e -> {
            boolean isVisible = subMenuPanel.isVisible();
            subMenuPanel.setVisible(!isVisible);
            
            // Cập nhật icon mũi tên
            String arrow = isVisible ? "▼" : "▲";
            mainButton.setText("<html><div style='text-align: left; padding: 8px;'>" + 
                             "<span style='font-size: 16px;'>📦</span> " + 
                             "<span style='margin-left: 10px;'>Quản lý hàng hóa</span>" +
                             "<span style='float: right; font-size: 12px;'>" + arrow + "</span></div></html>");
            
            // Refresh layout
            hangHoaPanel.revalidate();
            hangHoaPanel.repaint();
        });
        
        // Hover effect cho nút chính
        mainButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                mainButton.setBackground(new Color(200, 230, 255));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                mainButton.setBackground(Color.BLACK);
            }
        });
        
        hangHoaPanel.add(mainButton);
        hangHoaPanel.add(subMenuPanel);
        hangHoaPanel.add(Box.createVerticalStrut(10));
        
        menuPanel.add(hangHoaPanel);
    }
    
    private JButton createSubMenuButton(String text, String icon) {
        JButton button = new JButton("<html><div style='text-align: left; padding: 8px; margin-left: 20px;'>" + 
                                   "<span style='font-size: 14px;'>" + icon + "</span> " + 
                                   "<span style='margin-left: 10px;'>" + text + "</span></div></html>");
        button.setFont(new Font("Arial", Font.PLAIN, 12));
        button.setPreferredSize(new Dimension(210, 40));
        button.setMaximumSize(new Dimension(210, 40));
        button.setBackground(new Color(250, 250, 250));
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        
        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(220, 240, 255));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(250, 250, 250));
            }
        });
        
        // Action listener
        button.addActionListener(e -> handleMenuSelection(text));
        
        return button;
    }
    
    /*private void createTopBar() {
        topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(173, 216, 230));
        topBar.setPreferredSize(new Dimension(0, 60));
        
        // Title
        /*JLabel titleLabel = new JLabel("Trang quản lý");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.BLACK);
        topBar.add(titleLabel, BorderLayout.WEST);*/
        
        // Search panel
        /*JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        searchPanel.setOpaque(false);
        
        JTextField searchField = new JTextField(20);
        searchField.setFont(new Font("Arial", Font.PLAIN, 12));
        
        JComboBox<String> searchCombo = new JComboBox<>(new String[]{"ID", "Tên", "Mô tả"});
        searchCombo.setFont(new Font("Arial", Font.PLAIN, 12));
        
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchCombo);
        
        topBar.add(searchPanel, BorderLayout.EAST);
    }*/
    
    /*private void createRightSidebar() {
        rightSidebar = new JPanel();
        rightSidebar.setLayout(new BoxLayout(rightSidebar, BoxLayout.Y_AXIS));
        rightSidebar.setBackground(new Color(173, 216, 230));
        rightSidebar.setPreferredSize(new Dimension(120, 0));
        rightSidebar.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        
        // Action buttons
        String[][] actionButtons = {
            {"Thêm", "➕"},
            {"Sửa", "✏️"},
            {"Xóa", "❌"},
            {"Sync", "🔄"}
        };
        
        for (String[] button : actionButtons) {
            JButton actionBtn = createActionButton(button[0], button[1]);
            rightSidebar.add(actionBtn);
            rightSidebar.add(Box.createVerticalStrut(15));
        }
    }*/
    
    /*private JButton createActionButton(String text, String icon) {
        JButton button = new JButton("<html><div style='text-align: center;'>" + 
                                   "<div style='font-size: 20px;'>" + icon + "</div>" + 
                                   "<div style='font-size: 12px; margin-top: 5px;'>" + text + "</div></div></html>");
        button.setFont(new Font("Arial", Font.PLAIN, 12));
        button.setPreferredSize(new Dimension(100, 80));
        button.setMaximumSize(new Dimension(100, 80));
        button.setBackground(Color.BLACK);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        
        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(200, 230, 255));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.BLACK);
            }
        });
        
        return button;
    }*/
    
    private void createChildViews() {
        // Tạo các view con và thêm vào mainPanel
        mainPanel.add(createDefaultView(), "DEFAULT");
        mainPanel.add(new NhaCungCapView(this), "NHA_CUNG_CAP");
        mainPanel.add(new KhachHangView(this), "KHACH_HANG");
        mainPanel.add(new NhanVienView(this), "NHAN_VIEN");
        mainPanel.add(new DonHangView(this), "DON_HANG");
        mainPanel.add(new GiaoHangView(), "GIAO_HANG");
        mainPanel.add(new KhoHangView(this), "KHO_HANG");
        mainPanel.add(new NhapHangView(this), "NHAP_HANG");
        mainPanel.add(new ThongKeView(), "THONG_KE");
        // Tạo các view riêng biệt cho từng loại hàng hóa
        mainPanel.add(createMonView(), "MON");
        mainPanel.add(createLoaiMonView(), "LOAIMON");
        mainPanel.add(createNguyenLieuView(), "NGUYENLIEU");
    }
    
    private JPanel createDefaultView() {
        JPanel defaultPanel = new JPanel(new BorderLayout());
        defaultPanel.setBackground(Color.BLACK);
        
        // Tạo bảng mẫu như trong ảnh
        String[] columnNames = {"ID", "Tên Món", "Mô tả", "Link ảnh", "Tên đv", "Giá đv", "Mã loại"};
        Object[][] data = {
            {1, "Hướng dương", "", "", "", 10000, 1},
            {2, "Bánh Flan", "", "", "", 10000, 2},
            {3, "Trà Sữa Matcha(L)", "", "", "", 50000, 2},
            {4, "Espresso", "", "", "", 45000, 3},
            {5, "No Topping", "", "", "", 0, 4},
            {6, "Trân Châu Trắng", "", "", "", 10000, 4},
            {7, "Trân Châu Tuyết Sợi", "", "", "", 10000, 4}
        };
        
        JTable table = new JTable(data, columnNames);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(173, 216, 230));
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        defaultPanel.add(scrollPane, BorderLayout.CENTER);
        
        return defaultPanel;
    }
    
    private JPanel createMonView() {
        HangHoaView hangHoaView = new HangHoaView(this);
        // Set current view to MON
        hangHoaView.setCurrentView("MON");
        return hangHoaView;
    }
    
    private JPanel createLoaiMonView() {
        HangHoaView hangHoaView = new HangHoaView(this);
        // Set current view to LOAIMON
        hangHoaView.setCurrentView("LOAIMON");
        return hangHoaView;
    }
    
    private JPanel createNguyenLieuView() {
        HangHoaView hangHoaView = new HangHoaView(this);
        // Set current view to NGUYENLIEU
        hangHoaView.setCurrentView("NGUYENLIEU");
        return hangHoaView;
    }
    
    private void setupEventHandlers() {
        // Event handlers đã được setup trong createLeftSidebar()
        
        // Xử lý sự kiện đóng cửa sổ
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                performLogout();
            }
        });
    }
    
    // Method tiện ích để thực hiện đăng xuất
    private void performLogout() {
        int result = JOptionPane.showConfirmDialog(
            this,
            "Bạn có chắc chắn muốn đăng xuất?",
            "Xác nhận đăng xuất",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (result == JOptionPane.YES_OPTION) {
            // Reset Session
            Session.currentMaNV = 0;
            Session.currentTaiKhoan = null;
            Session.currentChucVu = null;
            
            // Đóng cửa sổ hiện tại
            dispose();
            
            // Quay về màn hình đăng nhập
            SwingUtilities.invokeLater(() -> {
                LoginDialog loginDialog = new LoginDialog(null);
                loginDialog.setVisible(true);
                
                // Nếu đăng nhập thành công, mở lại MainDashboard
                if (loginDialog.isLoginSuccessful()) {
                    loginDialog.dispose();
                    MainDashboard newDashboard = new MainDashboard();
                    newDashboard.setVisible(true);
                } else {
                    // Thoát chương trình nếu đăng nhập thất bại
                    System.exit(0);
                }
            });
        }
    }
    
    // Method kiểm tra quyền truy cập
    private boolean hasPermission(String menuText) {
        // Quản lý có quyền truy cập tất cả
        if ("quanly".equals(currentUserRole)) {
            return true;
        }
        
        // Nhân viên chỉ được truy cập một số chức năng
        switch (menuText) {
            case "Quản lý khách hàng":
            case "Quản lý đơn hàng":
            case "Giao hàng":
                return true;
            default:
                return false;
        }
    }
    
    private void loadUserInfo() {
        try {
            String currentUser = Session.currentTaiKhoan;
            if (currentUser != null && !currentUser.isEmpty()) {
                // Sử dụng chức vụ đã lưu trong Session
                currentUserRole = Session.currentChucVu;
                
                // Debug: In ra thông tin để kiểm tra
                // Nếu chưa có chức vụ trong Session, query từ database
                if (currentUserRole == null || currentUserRole.isEmpty()) {
                    Connection conn = DBUtil.getConnection();
                    String sql = "SELECT HoTen, ChucVu FROM nhanvien WHERE TaiKhoan = ?";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setString(1, currentUser);
                    ResultSet rs = stmt.executeQuery();
                    
                    if (rs.next()) {
                        String chucVu = rs.getString("ChucVu");
                        currentUserRole = chucVu;
                        Session.currentChucVu = chucVu; // Cập nhật lại Session
                    }
                    
                    rs.close();
                    stmt.close();
                    conn.close();
                }
                
            }
        } catch (Exception e) {
            currentUserRole = "nhanvien"; // Mặc định là nhân viên nếu có lỗi
        }
    }
    
    private void handleMenuSelection(String menuText) {
        // Kiểm tra phân quyền trước khi xử lý
        if (!hasPermission(menuText)) {
            JOptionPane.showMessageDialog(this, 
                "Bạn không có quyền truy cập chức năng này!", 
                "Không có quyền", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        switch (menuText) {
            case "Quản lý nhân viên":
                cardLayout.show(mainPanel, "NHAN_VIEN");
                break;
            case "Quản lý nhà cung cấp":
                cardLayout.show(mainPanel, "NHA_CUNG_CAP");
                break;
            case "Quản lý khách hàng":
                cardLayout.show(mainPanel, "KHACH_HANG");
                break;
            case "Quản lý phiếu nhập":
                cardLayout.show(mainPanel, "NHAP_HANG");
                break;
            case "Quản lý đơn hàng":
                cardLayout.show(mainPanel, "DON_HANG");
                break;
            case "Giao hàng":
                cardLayout.show(mainPanel, "GIAO_HANG");
                break;
            case "Quản lý món":
                cardLayout.show(mainPanel, "MON");
                break;
            case "Quản lý loại món":
                cardLayout.show(mainPanel, "LOAIMON");
                break;
            case "Quản lý nguyên liệu":
                cardLayout.show(mainPanel, "NGUYENLIEU");
                break;
            case "Kho hàng":
                cardLayout.show(mainPanel, "KHO_HANG");
                break;
            case "Thống kê":
                cardLayout.show(mainPanel, "THONG_KE");
                break;
            case "Thiết lập":
                JOptionPane.showMessageDialog(this, "Chức năng đang được phát triển", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                break;
            default:
                cardLayout.show(mainPanel, "DEFAULT");
                break;
        }
    }
    
    public void showMainMenu() {
        cardLayout.show(mainPanel, "DEFAULT");
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
            }
            
            new MainDashboard().setVisible(true);
        });
    }
}
