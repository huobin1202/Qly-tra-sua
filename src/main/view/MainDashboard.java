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
        // Kiểm tra Session trước khi khởi tạo - đảm bảo phải có đăng nhập hợp lệ
        if (!isValidSession()) {
            // Không tạo MainDashboard nếu Session không hợp lệ
            // Hiển thị thông báo và yêu cầu đăng nhập
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setVisible(false); // Ẩn frame này
            SwingUtilities.invokeLater(() -> {
                dispose(); // Dispose frame sau khi đã được tạo
                JOptionPane.showMessageDialog(null, 
                    "Phiên đăng nhập không hợp lệ. Vui lòng đăng nhập lại!", 
                    "Lỗi xác thực", 
                    JOptionPane.ERROR_MESSAGE);
                
                // Quay về màn hình đăng nhập
                LoginDialog loginDialog = new LoginDialog(null);
                loginDialog.setVisible(true);
                
                if (loginDialog.isLoginSuccessful()) {
                    loginDialog.dispose();
                    // Tạo lại MainDashboard sau khi đăng nhập thành công
                    MainDashboard newDashboard = new MainDashboard();
                    newDashboard.setVisible(true);
                } else {
                    System.exit(0);
                }
            });
            return; // Không tiếp tục khởi tạo nếu Session không hợp lệ
        }
        
        loadUserInfo(); // Load user info trước để có currentUserRole
        initializeComponents();
        setupLayout();
        setupEventHandlers();
    }
    
    // Kiểm tra Session có hợp lệ không
    private boolean isValidSession() {
        return Session.currentTaiKhoan != null && 
               !Session.currentTaiKhoan.isEmpty() &&
               Session.currentMaNV > 0 &&
               Session.currentNhanVien != null;
    }
    
    private void initializeComponents() {
        setTitle("Trang quản lý");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1400, 900);
        setLocationRelativeTo(null);
        
        // Tạo CardLayout để chuyển đổi giữa các màn hình
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        // Đảm bảo mainPanel không có padding/margin không cần thiết
        mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        
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
        
        // Kiểm tra nếu là quản lý thì mới hiển thị "Tổng quan" và "Quản lý hàng hóa"
        boolean isQuanLy = "quanly".equals(Session.currentChucVu);
        
        // Thêm menu "Tổng quan" ở đầu menu (chỉ cho quản lý)
        if (isQuanLy) {
            JButton tongQuanButton = createMenuButton("Tổng quan", "📊");
            menuPanel.add(tongQuanButton);
            menuPanel.add(Box.createVerticalStrut(5));
            
            createHangHoaDropdown(menuPanel);
        }

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
        // Sử dụng font-family với fallback để hỗ trợ cả emoji và tiếng Việt
        JButton button = new JButton("<html><div style='white-space: nowrap; overflow: hidden; text-overflow: ellipsis; width: 200px; font-family: \"Segoe UI Emoji\", \"Segoe UI\", Arial, sans-serif;'>" + icon + " " + text + "</div></html>");
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
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
        // Tạo panel chứa dropdown - sử dụng BorderLayout để kiểm soát vị trí tốt hơn
        JPanel hangHoaPanel = new JPanel(new BorderLayout());
        hangHoaPanel.setBackground(new Color(240, 240, 240));
        // Đặt preferredSize, maximumSize và minimumSize cố định khi đóng (chỉ bằng mainButton)
        // Width phải là 230px để khớp với các button menu chính
        hangHoaPanel.setPreferredSize(new Dimension(230, 50));
        hangHoaPanel.setMaximumSize(new Dimension(230, 50));
        hangHoaPanel.setMinimumSize(new Dimension(230, 50));
        hangHoaPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        // Không có border và padding để các button bên trong ngang hàng với các button menu chính
        // menuPanel có padding 10px, nên hangHoaPanel không cần padding
        hangHoaPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        // Đảm bảo hangHoaPanel không có margin hoặc padding thêm
        hangHoaPanel.setOpaque(false);
        
        // Nút chính "Quản lý hàng hóa"
        // Sử dụng font-family với fallback để hỗ trợ cả emoji và tiếng Việt
        JButton mainButton = new JButton("<html><div style='white-space: nowrap; overflow: hidden; text-overflow: ellipsis; width: 200px; font-family: \"Segoe UI Emoji\", \"Segoe UI\", Arial, sans-serif;'>📦 Quản lý hàng hóa ▼</div></html>");
        mainButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        mainButton.setPreferredSize(new Dimension(230, 50));
        mainButton.setMaximumSize(new Dimension(230, 50));
        mainButton.setBackground(Color.BLACK);
        mainButton.setForeground(Color.BLACK);
        mainButton.setFocusPainted(false);
        mainButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        mainButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainButton.setHorizontalAlignment(SwingConstants.LEFT);
        
        // Panel chứa các submenu (ẩn ban đầu)
        // Sử dụng GridBagLayout để đảm bảo căn trái hoàn toàn
        JPanel subMenuPanel = new JPanel(new GridBagLayout());
        subMenuPanel.setBackground(new Color(250, 250, 250));
        subMenuPanel.setVisible(false);
        // Khi ẩn, subMenuPanel không chiếm không gian - set cả preferredSize và maximumSize về 0
        subMenuPanel.setPreferredSize(new Dimension(0, 0));
        subMenuPanel.setMaximumSize(new Dimension(0, 0));
        subMenuPanel.setMinimumSize(new Dimension(0, 0));
        // Width phải là 230px để khớp với các button menu chính
        subMenuPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        // Không có padding để các button submenu ngang hàng với các button menu chính
        // menuPanel có padding 10px, button menu chính có border 10px = 20px từ lề menuPanel
        // menuPanel có padding 10px, hangHoaPanel không có padding, subMenuPanel không có padding, button submenu có border 10px = 20px từ lề menuPanel
        subMenuPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        // Đảm bảo subMenuPanel không có margin hoặc padding thêm
        subMenuPanel.setOpaque(false);
        
        // Các submenu items
        String[][] subMenuItems = {
            {"Quản lý món", "🍴"},
            {"Quản lý loại món", "🍽️"},
            {"Quản lý nguyên liệu", "📄"}
        };
        
        // Sử dụng GridBagLayout để đảm bảo căn trái
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 0, 0);
        
        for (int i = 0; i < subMenuItems.length; i++) {
            String[] item = subMenuItems[i];
            JButton subButton = createSubMenuButton(item[0], item[1]);
            // Ẩn các button con khi khởi tạo vì subMenuPanel đang ẩn
            subButton.setVisible(false);
            // Đảm bảo button có alignment đúng và width đầy đủ - giống như menu buttons chính
            subButton.setAlignmentX(Component.LEFT_ALIGNMENT);
            // Đảm bảo button có width đầy đủ và không bị co lại - giống như menu buttons chính
            subButton.setPreferredSize(new Dimension(230, 40));
            subButton.setMaximumSize(new Dimension(230, 40));
            subButton.setMinimumSize(new Dimension(230, 40));
            // Add vào subMenuPanel với GridBagLayout - đảm bảo căn trái
            gbc.gridy = i;
            gbc.insets = new Insets(0, 0, (i < subMenuItems.length - 1) ? 5 : 0, 0);
            subMenuPanel.add(subButton, gbc);
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
        
        // Tạo panel wrapper cho subMenuPanel để có khoảng cách giữa mainButton và subMenuPanel
        JPanel subMenuWrapper = new JPanel(new BorderLayout());
        subMenuWrapper.setOpaque(false);
        subMenuWrapper.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0)); // 2px spacing trên
        subMenuWrapper.add(subMenuPanel, BorderLayout.NORTH);
        subMenuWrapper.setVisible(false); // Ẩn ban đầu vì subMenuPanel cũng ẩn
        
        // Đảm bảo tất cả components đều căn trái trong hangHoaPanel
        mainButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        subMenuPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Sử dụng BorderLayout để đảm bảo căn trái
        hangHoaPanel.add(mainButton, BorderLayout.NORTH);
        hangHoaPanel.add(subMenuWrapper, BorderLayout.CENTER);
        
        menuPanel.add(hangHoaPanel);
        // Đặt khoảng cách cố định 5px - giống với các menu items khác để đồng nhất
        menuPanel.add(Box.createVerticalStrut(5));
        
        // Force layout update ngay sau khi tạo để đảm bảo kích thước đúng và không có khoảng trống
        SwingUtilities.invokeLater(() -> {
            // Đảm bảo hangHoaPanel chỉ có kích thước bằng mainButton khi khởi tạo
            hangHoaPanel.setPreferredSize(new Dimension(230, 50));
            hangHoaPanel.setMaximumSize(new Dimension(230, 50));
            hangHoaPanel.setMinimumSize(new Dimension(230, 50));
            hangHoaPanel.invalidate();
            hangHoaPanel.revalidate();
            menuPanel.invalidate();
            menuPanel.revalidate();
            if (leftSidebar != null) {
                leftSidebar.invalidate();
                leftSidebar.revalidate();
            }
        });
        
        // Event handler cho nút chính
        mainButton.addActionListener(e -> {
            boolean isVisible = subMenuPanel.isVisible();
            subMenuPanel.setVisible(!isVisible);
            subMenuWrapper.setVisible(!isVisible); // Ẩn/hiện wrapper cùng với subMenuPanel
            
            // Cập nhật icon mũi tên
            String arrow = isVisible ? "▼" : "▲";
            mainButton.setText("<html><div style='white-space: nowrap; overflow: hidden; text-overflow: ellipsis; width: 200px;'>📦 Quản lý hàng hóa " + arrow + "</div></html>");
            
            // Cập nhật preferredSize và maximumSize của hangHoaPanel và subMenuPanel dựa trên trạng thái mở/đóng
            if (!isVisible) {
                // Khi mở: tính toán kích thước dựa trên số lượng submenu items
                // 40px mỗi button + 5px spacing giữa các button
                int subMenuHeight = subMenuItems.length * 40 + (subMenuItems.length - 1) * 5;
                // Đảm bảo subMenuPanel có width cố định và căn trái
                subMenuPanel.setPreferredSize(new Dimension(230, subMenuHeight));
                subMenuPanel.setMaximumSize(new Dimension(230, Integer.MAX_VALUE));
                subMenuPanel.setMinimumSize(new Dimension(230, subMenuHeight));
                subMenuPanel.setSize(new Dimension(230, subMenuHeight));
                // Đảm bảo subMenuWrapper có width cố định và căn trái
                subMenuWrapper.setPreferredSize(new Dimension(230, subMenuHeight + 2)); // subMenuHeight + 2px spacing
                subMenuWrapper.setMaximumSize(new Dimension(230, subMenuHeight + 2));
                subMenuWrapper.setMinimumSize(new Dimension(230, subMenuHeight + 2));
                subMenuWrapper.setSize(new Dimension(230, subMenuHeight + 2));
                hangHoaPanel.setPreferredSize(new Dimension(230, 50 + 2 + subMenuHeight)); // 50px button + 2px spacing + subMenuHeight
                hangHoaPanel.setMaximumSize(new Dimension(230, Integer.MAX_VALUE));
                hangHoaPanel.setMinimumSize(new Dimension(230, 50 + 2 + subMenuHeight));
                // Đảm bảo alignment đúng để các button submenu ngang hàng với các button menu chính
                hangHoaPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
                // Hiện các buttons con - giống như menu buttons chính
                for (Component comp : subMenuPanel.getComponents()) {
                    if (comp instanceof JButton) {
                        JButton btn = (JButton) comp;
                        btn.setVisible(true);
                        // Đảm bảo button có width đầy đủ và căn trái - giống như menu buttons chính
                        btn.setPreferredSize(new Dimension(230, 40));
                        btn.setMaximumSize(new Dimension(230, 40));
                        btn.setMinimumSize(new Dimension(230, 40));
                        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
                        btn.setHorizontalAlignment(SwingConstants.LEFT);
                        btn.setHorizontalTextPosition(SwingConstants.LEFT);
                    }
                }
                // Force revalidate để đảm bảo layout được cập nhật
                subMenuPanel.revalidate();
                subMenuPanel.repaint();
            } else {
                // Khi đóng: subMenuPanel và strut không chiếm không gian - set cả preferredSize, maximumSize và minimumSize về 0
                subMenuPanel.setPreferredSize(new Dimension(0, 0));
                subMenuPanel.setMaximumSize(new Dimension(0, 0));
                subMenuPanel.setMinimumSize(new Dimension(0, 0));
                subMenuWrapper.setPreferredSize(new Dimension(0, 0));
                subMenuWrapper.setMaximumSize(new Dimension(0, 0));
                subMenuWrapper.setMinimumSize(new Dimension(0, 0));
                hangHoaPanel.setPreferredSize(new Dimension(230, 50)); // Chỉ bằng mainButton
                hangHoaPanel.setMaximumSize(new Dimension(230, 50));
                hangHoaPanel.setMinimumSize(new Dimension(230, 50));
                // Ẩn các buttons con - giống như menu buttons chính
                for (Component comp : subMenuPanel.getComponents()) {
                    if (comp instanceof JButton) {
                        comp.setVisible(false);
                    }
                }
            }
            
            // Force layout update - invalidate trước rồi mới validate
            hangHoaPanel.invalidate();
            hangHoaPanel.revalidate();
            hangHoaPanel.repaint();
            
            // Revalidate menuPanel và cả leftSidebar để đảm bảo layout được cập nhật hoàn toàn
            menuPanel.invalidate();
            menuPanel.revalidate();
            menuPanel.repaint();
            
            // Revalidate leftSidebar để đảm bảo toàn bộ sidebar được cập nhật
            if (leftSidebar != null) {
                leftSidebar.invalidate();
                leftSidebar.revalidate();
                leftSidebar.repaint();
            }
        });
    }
    
    private JButton createSubMenuButton(String text, String icon) {
        // Dùng HTML với style white-space: nowrap để text không xuống dòng và icon hiển thị được
        // Đảm bảo text căn trái - sử dụng font-family với fallback để hỗ trợ cả emoji và tiếng Việt
        JButton button = new JButton("<html><div style='font-family: \"Segoe UI Emoji\", \"Segoe UI\", Arial, sans-serif;'>" + icon + " " + text + "</div></html>");
        button.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        // Đảm bảo button có width đầy đủ và căn trái - width = 230px để khớp với menu chính
        button.setPreferredSize(new Dimension(230, 40));
        button.setMaximumSize(new Dimension(230, 40));
        button.setMinimumSize(new Dimension(230, 40));
        button.setBackground(new Color(250, 250, 250));
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        // Padding-left = 10px để các mục submenu ngang hàng với các mục menu chính
        // menuPanel có padding 10px, button menu chính có border 10px = 20px từ lề menuPanel
        // menuPanel có padding 10px, hangHoaPanel không có padding, subMenuPanel không có padding, button submenu có border 10px = 20px từ lề menuPanel
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        // Đảm bảo button căn trái hoàn toàn
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        // Đảm bảo button không bị căn giữa hoặc căn phải
        button.setHorizontalTextPosition(SwingConstants.LEFT);
        // Đảm bảo button không bị co lại
        button.setSize(new Dimension(230, 40));
        
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
        
        JLabel titleLabel = new JLabel("DASHBOARD TỔNG QUAN");
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
        contentPanel.add(createStatCard("TỔNG DOANH THU", 
            String.format("%,d VNĐ", tongQuan.getDoanhThu()), 
            new Color(46, 125, 50)));
        
        contentPanel.add(createStatCard("KHÁCH HÀNG", 
            String.valueOf(tongQuan.getSoKhachHang()), 
            new Color(156, 39, 176)));
        
        contentPanel.add(createStatCard("NHÂN VIÊN", 
            String.valueOf(tongQuan.getSoNhanVien()), 
            new Color(255, 87, 34)));
        
        contentPanel.add(createStatCard("MÓN ĂN", 
            String.valueOf(tongQuan.getSoMon()), 
            new Color(0, 150, 136)));
        
        contentPanel.add(createStatCard("NGUYÊN LIỆU", 
            String.valueOf(tongQuan.getSoNguyenLieu()), 
            new Color(121, 85, 72)));
        
        contentPanel.add(createStatCard("NHÀ CUNG CẤP", 
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
            // Reset Session - clear tất cả thông tin
            Session.currentMaNV = 0;
            Session.currentTaiKhoan = null;
            Session.currentChucVu = null;
            Session.currentNhanVien = null;
            
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
        // "Tổng quan" chỉ dành cho quản lý
        if ("Tổng quan".equals(menuText)) {
            if (!"quanly".equals(Session.currentChucVu)) {
                JOptionPane.showMessageDialog(this, 
                    "Bạn không có quyền truy cập chức năng này!", 
                    "Không có quyền", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
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
                // Chỉ quản lý mới có quyền xem thống kê
                if (!"quanly".equals(Session.currentChucVu)) {
                    JOptionPane.showMessageDialog(this, 
                        "Bạn không có quyền truy cập chức năng này!", 
                        "Không có quyền", 
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
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
        // Không cho phép chạy MainDashboard trực tiếp - phải qua Runner
        JOptionPane.showMessageDialog(null, 
            "Vui lòng chạy chương trình từ Runner.main() để đảm bảo đăng nhập đúng cách!", 
            "Cảnh báo", 
            JOptionPane.WARNING_MESSAGE);
        System.exit(0);
    }
}
