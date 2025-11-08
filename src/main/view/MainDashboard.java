package view;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.text.SimpleDateFormat;

import database.DBUtil;
import database.Session;
import dao.ThongKeDAO;
import dto.ThongKeDTO;

public class MainDashboard extends JFrame implements MainFrameInterface {
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private JPanel leftSidebar;
    private JLabel userInfoLabel;
    private JLabel roleLabel;
    private String currentUserRole; // Lưu chức vụ hiện tại
    
    // Thêm biến lưu các panel hàng hóa theo loại view
    private HangHoaView hangHoaMonPanel;
    private HangHoaView hangHoaLoaiPanel;
    private HangHoaView hangHoaNguyenLieuPanel;
    
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
        
        // Hiển thị dashboard mặc định khi đăng nhập
        cardLayout.show(mainPanel, "DEFAULT");
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
        
        // Thêm menu "Tổng quan" ở đầu menu
        JButton tongQuanButton = createMenuButton("Tổng quan", "📊");
        menuPanel.add(tongQuanButton);
        menuPanel.add(Box.createVerticalStrut(5));
        
        createHangHoaDropdown(menuPanel);

        // Lấy menu từ nghiệp vụ Nhân viên hướng đối tượng
        String[][] menuItems = database.Session.currentNhanVien != null ? database.Session.currentNhanVien.getMenuItems() : new String[0][0];
        addMenuButtonsFromList(menuPanel, menuItems);

        leftSidebar.add(menuPanel, BorderLayout.CENTER);
        
        // Cập nhật thông tin user sau khi tạo các label
        updateUserInfo();
    }
    
    /**
     * Hàm thêm động các nút menu từ danh sách getMenuItems() vào menuPanel
     */
    private void addMenuButtonsFromList(JPanel menuPanel, String[][] menuItems) {
        for (String[] item : menuItems) {
            // Nếu là 3 menu "Quản lý món", "Quản lý loại món", "Quản lý nguyên liệu" thì bỏ qua, chỉ để chúng ở dropdown hàng hóa.
            if ("Quản lý món".equals(item[0]) || "Quản lý loại món".equals(item[0]) || "Quản lý nguyên liệu".equals(item[0])) {
                continue;
            }
            JButton menuButton = createMenuButton(item[0], item[1]);
            menuPanel.add(menuButton);
            menuPanel.add(Box.createVerticalStrut(5));
        }
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
        // Dùng HTML với style white-space: nowrap và overflow: hidden để text không xuống dòng
        JButton button = new JButton("<html><div style='white-space: nowrap; overflow: hidden; text-overflow: ellipsis; width: 200px;'>" + icon + " " + text + "</div></html>");
        button.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));  // Font hỗ trợ emoji
        Dimension buttonSize = new Dimension(230, 50);
        button.setPreferredSize(buttonSize);
        button.setMaximumSize(buttonSize);
        button.setMinimumSize(buttonSize); // Đảm bảo button không bị co lại
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
        // Đặt preferredSize cố định khi đóng (chỉ bằng mainButton), maximumSize để cho phép mở rộng khi mở dropdown
        hangHoaPanel.setPreferredSize(new Dimension(230, 50));
        hangHoaPanel.setMaximumSize(new Dimension(230, Integer.MAX_VALUE));
        hangHoaPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        // Loại bỏ border và padding để tránh khoảng cách không mong muốn
        hangHoaPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        
        // Nút chính "Quản lý hàng hóa"
        JButton mainButton = new JButton("<html><div style='white-space: nowrap; overflow: hidden; text-overflow: ellipsis; width: 200px;'>📦 Quản lý hàng hóa ▼</div></html>");
        mainButton.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
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
        // Đặt maximumSize để tránh khoảng trống lớn khi mở
        subMenuPanel.setMaximumSize(new Dimension(230, Integer.MAX_VALUE));
        // Khi ẩn, subMenuPanel không chiếm không gian
        subMenuPanel.setPreferredSize(new Dimension(0, 0));
        subMenuPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        // Loại bỏ border và padding để tránh khoảng cách không mong muốn
        subMenuPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        
        // Các submenu items
        String[][] subMenuItems = {
            {"Quản lý món", "🍴"},
            {"Quản lý loại món", "🍽️"},
            {"Quản lý nguyên liệu", "📄"}
        };
        
        for (int i = 0; i < subMenuItems.length; i++) {
            String[] item = subMenuItems[i];
            JButton subButton = createSubMenuButton(item[0], item[1]);
            subMenuPanel.add(subButton);
            // Chỉ thêm khoảng trống giữa các items, không thêm sau item cuối cùng
            if (i < subMenuItems.length - 1) {
                subMenuPanel.add(Box.createVerticalStrut(5));
            }
        }
        
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
        // Không thêm khoảng cách giữa mainButton và subMenuPanel để tránh khoảng cách thừa
        hangHoaPanel.add(subMenuPanel);
        
        menuPanel.add(hangHoaPanel);
        // Đặt khoảng cách cố định 3px - đủ gần nhưng không sát
        // Khi dropdown mở, subMenuPanel sẽ tự tạo khoảng cách hợp lý
        menuPanel.add(Box.createVerticalStrut(3));
        
        // Event handler cho nút chính
        mainButton.addActionListener(e -> {
            boolean isVisible = subMenuPanel.isVisible();
            subMenuPanel.setVisible(!isVisible);
            
            // Cập nhật icon mũi tên
            String arrow = isVisible ? "▼" : "▲";
            mainButton.setText("<html><div style='white-space: nowrap; overflow: hidden; text-overflow: ellipsis; width: 200px;'>📦 Quản lý hàng hóa " + arrow + "</div></html>");
            
            // Cập nhật preferredSize của hangHoaPanel và subMenuPanel dựa trên trạng thái mở/đóng
            if (!isVisible) {
                // Khi mở: tính toán kích thước dựa trên số lượng submenu items
                int subMenuHeight = subMenuItems.length * 40 + (subMenuItems.length - 1) * 5; // 40px mỗi button + 5px spacing
                subMenuPanel.setPreferredSize(new Dimension(230, subMenuHeight));
                hangHoaPanel.setPreferredSize(new Dimension(230, 50 + subMenuHeight));
            } else {
                // Khi đóng: subMenuPanel không chiếm không gian
                subMenuPanel.setPreferredSize(new Dimension(0, 0));
                hangHoaPanel.setPreferredSize(new Dimension(230, 50));
            }
            
            // Refresh layout - cần revalidate cả menuPanel để cập nhật layout đúng cách
            hangHoaPanel.revalidate();
            hangHoaPanel.repaint();
            // Revalidate menuPanel để đảm bảo layout được cập nhật
            menuPanel.revalidate();
            menuPanel.repaint();
        });
    }
    
    private JButton createSubMenuButton(String text, String icon) {
        // Dùng HTML với style white-space: nowrap để text không xuống dòng và icon hiển thị được
        JButton button = new JButton("<html><div style='white-space: nowrap; overflow: hidden; text-overflow: ellipsis; width: 180px; padding-left: 20px;'>" + icon + " " + text + "</div></html>");
        button.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));
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
    
   
    
    private void createChildViews() {
        // Tạo các view con và thêm vào mainPanel
        mainPanel.add(createDefaultView(), "DEFAULT");
        mainPanel.add(new NhaCungCapView(this), "NHA_CUNG_CAP");
        mainPanel.add(new KhachHangView(this), "KHACH_HANG");
        mainPanel.add(new NhanVienView(this), "NHAN_VIEN");
        mainPanel.add(new DonHangView(this), "DON_HANG");
        mainPanel.add(new KhoHangView(this), "KHO_HANG");
        mainPanel.add(new NhapHangView(this), "NHAP_HANG");
        mainPanel.add(new ThongKeView(), "THONG_KE");
        // Các view MON/LOAIMON/NGUYENLIEU lưu lại instance riêng
        mainPanel.add(createMonView(), "MON");
        mainPanel.add(createLoaiMonView(), "LOAIMON");
        mainPanel.add(createNguyenLieuView(), "NGUYENLIEU");
    }
    
    private JPanel createDefaultView() {
        JPanel dashboardPanel = new JPanel(new BorderLayout());
        dashboardPanel.setBackground(new Color(240, 248, 255));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(70, 130, 180));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        JLabel titleLabel = new JLabel("📊 DASHBOARD TỔNG QUAN");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        
        // Hiển thị ngày hiện tại
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dateFormat;
        try {
            dateFormat = new SimpleDateFormat("EEEE, dd MMMM yyyy", java.util.Locale.forLanguageTag("vi"));
        } catch (Exception e) {
            // Fallback to default locale if Vietnamese locale not available
            dateFormat = new SimpleDateFormat("EEEE, dd MMMM yyyy");
        }
        JLabel dateLabel = new JLabel(dateFormat.format(cal.getTime()));
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        dateLabel.setForeground(Color.WHITE);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(dateLabel, BorderLayout.EAST);
        
        dashboardPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Content panel với các card thống kê
        JPanel contentPanel = new JPanel(new GridLayout(2, 3, 20, 20));
        contentPanel.setBackground(new Color(240, 248, 255));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        // Load dữ liệu thống kê
        ThongKeDAO thongKeDAO = new ThongKeDAO();
        ThongKeDTO tongQuan = thongKeDAO.thongKeTongQuan();
        
        // Tạo các card thống kê
        contentPanel.add(createStatCard("💰 TỔNG DOANH THU", 
            String.format("%,d VNĐ", tongQuan.getDoanhThu()), 
            new Color(46, 125, 50)));
        
        contentPanel.add(createStatCard("👥 KHÁCH HÀNG", 
            String.valueOf(tongQuan.getSoKhachHang()), 
            new Color(156, 39, 176)));
        
        contentPanel.add(createStatCard("👨‍💼 NHÂN VIÊN", 
            String.valueOf(tongQuan.getSoNhanVien()), 
            new Color(255, 87, 34)));
        
        contentPanel.add(createStatCard("🍴 MÓN ĂN", 
            String.valueOf(tongQuan.getSoMon()), 
            new Color(0, 150, 136)));
        
        contentPanel.add(createStatCard("📦 NGUYÊN LIỆU", 
            String.valueOf(tongQuan.getSoNguyenLieu()), 
            new Color(121, 85, 72)));
        
        contentPanel.add(createStatCard("🏢 NHÀ CUNG CẤP", 
            String.valueOf(tongQuan.getSoNhaCungCap()), 
            new Color(63, 81, 181)));
        
        dashboardPanel.add(contentPanel, BorderLayout.CENTER);
        
        // Footer với thông tin thêm
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setBackground(new Color(240, 248, 255));
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel footerLabel = new JLabel("Chào mừng bạn đến với hệ thống quản lý trà sữa!");
        footerLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        footerLabel.setForeground(new Color(100, 100, 100));
        footerPanel.add(footerLabel);
        
        dashboardPanel.add(footerPanel, BorderLayout.SOUTH);
        
        return dashboardPanel;
    }
    
    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));
        
        // Title
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(new Color(100, 100, 100));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Value
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 32));
        valueLabel.setForeground(color);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        
        // Hover effect
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(color, 2),
                    BorderFactory.createEmptyBorder(29, 29, 29, 29)
                ));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                    BorderFactory.createEmptyBorder(30, 30, 30, 30)
                ));
            }
        });
        
        return card;
    }
    
    private JPanel createMonView() {
        hangHoaMonPanel = new HangHoaView(this);
        // Không gọi setCurrentView() ở đây - chỉ load khi view được hiển thị
        return hangHoaMonPanel;
    }
    
    private JPanel createLoaiMonView() {
        hangHoaLoaiPanel = new HangHoaView(this);
        // Không gọi setCurrentView() ở đây - chỉ load khi view được hiển thị
        return hangHoaLoaiPanel;
    }
    
    private JPanel createNguyenLieuView() {
        hangHoaNguyenLieuPanel = new HangHoaView(this);
        // Không gọi setCurrentView() ở đây - chỉ load khi view được hiển thị
        return hangHoaNguyenLieuPanel;
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
        if (database.Session.currentNhanVien == null) return false;
        String[][] allowedMenus = database.Session.currentNhanVien.getMenuItems();
        for (String[] menu : allowedMenus) {
            if (menu[0].equals(menuText)) return true;
        }
        // Trường hợp đặc biệt: các submenu của Quản lý hàng hóa
        if (menuText.equals("Quản lý món") || menuText.equals("Quản lý loại món") || menuText.equals("Quản lý nguyên liệu")) {
            for (String[] menu : allowedMenus) {
                if (menu[0].equals("Quản lý món")) return true; // Nếu có menu 'Quản lý món' tức là nhóm này được quyền hàng hóa
            }
        }
        return false;
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
    
    // Menu handler phải đảm bảo mỗi lần chuyển sang layout "MON", "LOAIMON", "NGUYENLIEU" đều hiển thị đúng panel con với trạng thái currentView chuẩn
    private void handleMenuSelection(String menuText) {
        // "Tổng quan" luôn có thể truy cập, không cần kiểm tra quyền
        if ("Tổng quan".equals(menuText)) {
            cardLayout.show(mainPanel, "DEFAULT");
            return;
        }
        
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
            case "Quản lý món": {
                cardLayout.show(mainPanel, "MON");
                if (hangHoaMonPanel != null) hangHoaMonPanel.setCurrentView("MON");
                break;
            }
            case "Quản lý loại món": {
                cardLayout.show(mainPanel, "LOAIMON");
                if (hangHoaLoaiPanel != null) hangHoaLoaiPanel.setCurrentView("LOAIMON");
                break;
            }
            case "Quản lý nguyên liệu": {
                cardLayout.show(mainPanel, "NGUYENLIEU");
                if (hangHoaNguyenLieuPanel != null) hangHoaNguyenLieuPanel.setCurrentView("NGUYENLIEU");
                break;
            }
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
