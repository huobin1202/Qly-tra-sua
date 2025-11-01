package view;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import database.DBUtil;
import dto.DonHangDTO;
import dto.ChiTietDonHangDTO;
import dto.MonDTO;
import dto.LoaiMonDTO;
import dao.DonHangDAO;
import dao.HangHoaDAO;
import dao.KhoHangDAO;
import dto.MonNguyenLieuDTO;

public class SuaDonHangView extends JDialog {
    // Thông tin hóa đơn
    private JTextField maHDField;
    private JTextField nhanVienField;
    private JSpinner giamGiaSpinner;
    
    // Thông tin khách hàng
    private JTextField khachHangTenField;
    private JTextField khachHangSDTField;
    private JTextField khachHangDiemTichLuyField;
    private JButton timKiemKhachHangButton;
    private int selectedKhachHangId = 0;
    
    // Thông tin hóa đơn hiển thị
    private JLabel trangThaiLabel;
    private JLabel tongTienLabel;
    private JLabel giamGiaAmountLabel;
    private JLabel phaiTraLabel;
    
    // Danh mục sản phẩm
    private JButton[] categoryButtons;
    private JPanel productGridPanel;
    private List<MonDTO> currentProducts;
    
    // Danh sách món đã đặt
    private List<ChiTietDonHangDTO> orderedItems;
    
    // Nút thao tác
    private JButton thanhToanButton;
    private JButton capNhatButton;
    private JButton dongButton;
    private JButton inHoaDonButton;
    private JButton huyHoaDonButton;
    
    // Dữ liệu
    private final DonHangDTO currentOrder;
    private int currentCategoryId = 1; // Sẽ được cập nhật từ database
    private int originalKhachHangId = 0; // Lưu mã khách hàng ban đầu của đơn hàng
    
    public SuaDonHangView(Window parent, int maDon) {
        super(parent, "Cập nhật hóa đơn", ModalityType.APPLICATION_MODAL);
        this.currentOrder = new DonHangDTO();
        this.currentOrder.setMaDon(maDon);
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadOrderData(); // Load đơn hàng
        // Mặc định hiển thị tất cả sản phẩm
        currentCategoryId = 0;
        loadProducts();
        loadOrderedItems();
        updateOrderSummary();
        // Cập nhật trạng thái button danh mục sau khi đã load xong
        if (categoryButtons != null) {
            updateCategoryButtons();
        }
    }
    
    private void initializeComponents() {
        setSize(1300, 800);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        // Khởi tạo các component thông tin hóa đơn
        maHDField = new JTextField(10);
        maHDField.setEditable(false);
        nhanVienField = new JTextField(15);
        nhanVienField.setEditable(false);
        
        giamGiaSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
        giamGiaSpinner.setPreferredSize(new Dimension(80, 25));
        
        // Khởi tạo các component thông tin khách hàng
        khachHangTenField = new JTextField(20);
        khachHangTenField.setEditable(true);
        khachHangSDTField = new JTextField(15);
        khachHangSDTField.setEditable(true);
        timKiemKhachHangButton = new JButton("🔍");
        timKiemKhachHangButton.setPreferredSize(new Dimension(60, 25));
        khachHangDiemTichLuyField = new JTextField(15);
        khachHangDiemTichLuyField.setEditable(false); // Không cho phép chỉnh sửa điểm tích lũy
        
        // Labels hiển thị thông tin
        trangThaiLabel = new JLabel("Chưa thanh toán");
        trangThaiLabel.setForeground(Color.RED);
        trangThaiLabel.setFont(new Font("Arial", Font.BOLD, 12));
        
        tongTienLabel = new JLabel("0 VND");
        tongTienLabel.setForeground(Color.RED);
        tongTienLabel.setFont(new Font("Arial", Font.BOLD, 12));
        
        giamGiaAmountLabel = new JLabel("0 %");
        giamGiaAmountLabel.setForeground(Color.RED);
        giamGiaAmountLabel.setFont(new Font("Arial", Font.BOLD, 12));
        
        phaiTraLabel = new JLabel("0 VND");
        phaiTraLabel.setForeground(Color.RED);
        phaiTraLabel.setFont(new Font("Arial", Font.BOLD, 12));
        
        // Khởi tạo danh mục sản phẩm
        currentProducts = new ArrayList<>();
        orderedItems = new ArrayList<>();
        
        // Khởi tạo danh sách món đã đặt
        
        // Khởi tạo các nút thao tác
        thanhToanButton = new JButton("Thanh toán");
        thanhToanButton.setBackground(new Color(34, 139, 34));
        thanhToanButton.setForeground(Color.BLACK);
        thanhToanButton.setFocusPainted(false);
        
        capNhatButton = new JButton("Cập nhật");
        capNhatButton.setBackground(new Color(255, 140, 0));
        capNhatButton.setForeground(Color.BLACK);
        capNhatButton.setFocusPainted(false);
        
        dongButton = new JButton("Đóng");
        dongButton.setBackground(new Color(128, 128, 128));
        dongButton.setForeground(Color.BLACK);
        dongButton.setFocusPainted(false);
        
        inHoaDonButton = new JButton("In Hóa Đơn");
        inHoaDonButton.setBackground(new Color(70, 130, 180));
        inHoaDonButton.setForeground(Color.BLACK);
        inHoaDonButton.setFocusPainted(false);
        
        huyHoaDonButton = new JButton("Hủy Hóa Đơn");
        huyHoaDonButton.setBackground(new Color(220, 20, 60));
        huyHoaDonButton.setForeground(Color.BLACK);
        huyHoaDonButton.setFocusPainted(false);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 248, 255));
        
        // Tạo 3 cột chính
        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplitPane.setDividerLocation(300);
        mainSplitPane.setDividerSize(5);
        mainSplitPane.setResizeWeight(0.25); // Cột trái chiếm 25% không gian
        mainSplitPane.setEnabled(false); // Vô hiệu hóa khả năng kéo divider
        
        // Cột trái - Thông tin hóa đơn
        JPanel leftPanel = createLeftPanel();
        mainSplitPane.setLeftComponent(leftPanel);
        
        // Cột giữa và phải
        JSplitPane rightSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        rightSplitPane.setDividerLocation(550);
        rightSplitPane.setDividerSize(5);
        rightSplitPane.setResizeWeight(0.67); // Cột giữa chiếm 67% không gian còn lại
        rightSplitPane.setEnabled(false); // Vô hiệu hóa khả năng kéo divider
        
        // Cột giữa - Danh mục và sản phẩm
        JPanel centerPanel = createCenterPanel();
        rightSplitPane.setLeftComponent(centerPanel);
        
        // Cột phải - Danh sách món đã đặt
        JPanel rightPanel = createRightPanel();
        rightSplitPane.setRightComponent(rightPanel);
        
        mainSplitPane.setRightComponent(rightSplitPane);
        add(mainSplitPane, BorderLayout.CENTER);
    }
    
    private JPanel createLeftPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 248, 255));
        panel.setBorder(BorderFactory.createTitledBorder("Chỉnh sửa thông tin"));
        
        // Panel thông tin hóa đơn
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBackground(new Color(240, 248, 255));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Mã HD
        gbc.gridx = 0; gbc.gridy = 0;
        infoPanel.add(new JLabel("Mã HD:"), gbc);
        gbc.gridx = 1;
        infoPanel.add(maHDField, gbc);
        
        // Nhân viên
        gbc.gridx = 0; gbc.gridy = 1;
        infoPanel.add(new JLabel("Nhân viên:"), gbc);
        gbc.gridx = 1;
        infoPanel.add(nhanVienField, gbc);
        
        // Giảm giá
        gbc.gridx = 0; gbc.gridy = 2;
        infoPanel.add(new JLabel("Giảm giá(%):"), gbc);
        gbc.gridx = 1;
        infoPanel.add(giamGiaSpinner, gbc);
        
        // Thông tin khách hàng
        gbc.gridx = 0; gbc.gridy = 3;
        infoPanel.add(new JLabel("SĐT:"), gbc);
        gbc.gridx = 1;
        // Panel chứa SĐT và nút tìm kiếm
        JPanel sdtPanel = new JPanel(new BorderLayout());
        sdtPanel.add(khachHangSDTField, BorderLayout.CENTER);
        sdtPanel.add(timKiemKhachHangButton, BorderLayout.EAST);
        infoPanel.add(sdtPanel, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        infoPanel.add(new JLabel("Tên:"), gbc);
        gbc.gridx = 1;
        infoPanel.add(khachHangTenField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5;
        infoPanel.add(new JLabel("Điểm tích lũy:"), gbc);
        gbc.gridx = 1;
        infoPanel.add(khachHangDiemTichLuyField, gbc);
        
        // Panel thông tin hóa đơn
        JPanel summaryPanel = new JPanel(new GridBagLayout());
        summaryPanel.setBackground(new Color(240, 248, 255));
        summaryPanel.setBorder(BorderFactory.createTitledBorder("Thông tin hóa đơn"));
        
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Trạng thái
        gbc.gridx = 0; gbc.gridy = 0;
        summaryPanel.add(new JLabel("Trạng thái:"), gbc);
        gbc.gridx = 1;
        summaryPanel.add(trangThaiLabel, gbc);
        
        // Tổng
        gbc.gridx = 0; gbc.gridy = 1;
        summaryPanel.add(new JLabel("Tổng:"), gbc);
        gbc.gridx = 1;
        summaryPanel.add(tongTienLabel, gbc);
        
        // Giảm giá
        gbc.gridx = 0; gbc.gridy = 2;
        summaryPanel.add(new JLabel("Giảm giá:"), gbc);
        gbc.gridx = 1;
        summaryPanel.add(giamGiaAmountLabel, gbc);
        
        // Phải trả
        gbc.gridx = 0; gbc.gridy = 3;
        summaryPanel.add(new JLabel("Phải trả:"), gbc);
        gbc.gridx = 1;
        summaryPanel.add(phaiTraLabel, gbc);
        
        // Panel nút thao tác
        JPanel actionPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        actionPanel.setBackground(new Color(240, 248, 255));
        actionPanel.setBorder(BorderFactory.createTitledBorder("Thao tác"));
        
        actionPanel.add(thanhToanButton);
        actionPanel.add(capNhatButton);
        actionPanel.add(dongButton);
        actionPanel.add(inHoaDonButton);
        actionPanel.add(huyHoaDonButton);
        
        // Kết hợp các panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(240, 248, 255));
        contentPanel.add(infoPanel);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(summaryPanel);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(actionPanel);
        
        panel.add(contentPanel, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 248, 255));
        
        // Panel danh mục (bên trái)
        JPanel categoryPanel = new JPanel();
        categoryPanel.setLayout(new BoxLayout(categoryPanel, BoxLayout.Y_AXIS));
        categoryPanel.setBackground(new Color(240, 248, 255));
        categoryPanel.setBorder(BorderFactory.createTitledBorder("Danh mục"));
        categoryPanel.setPreferredSize(new Dimension(120, 200));
        
        // Load danh mục từ database
        loadCategories(categoryPanel);
        
        // Panel lưới sản phẩm (bên phải)
        productGridPanel = new JPanel();
        productGridPanel.setLayout(new BoxLayout(productGridPanel, BoxLayout.Y_AXIS));
        productGridPanel.setBackground(new Color(240, 248, 255));
        productGridPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JScrollPane productScrollPane = new JScrollPane(productGridPanel);
        productScrollPane.setBorder(BorderFactory.createTitledBorder("Danh sách sản phẩm"));
        productScrollPane.setPreferredSize(new Dimension(600, 500));
        productScrollPane.setMinimumSize(new Dimension(400, 300));
        
        // Tạo split pane để chia danh mục và sản phẩm
        JSplitPane middleSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, categoryPanel, productScrollPane);
        middleSplitPane.setDividerLocation(120);
        middleSplitPane.setResizeWeight(0.0); // Danh mục không resize
        middleSplitPane.setBorder(null);
        middleSplitPane.setEnabled(false); // Vô hiệu hóa khả năng kéo divider
        
        panel.add(middleSplitPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createRightPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 248, 255));
        panel.setBorder(BorderFactory.createTitledBorder("Danh sách món đã đặt"));
        
        // Tạo panel chứa các item đã đặt
        JPanel itemsPanel = new JPanel();
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
        itemsPanel.setBackground(new Color(240, 248, 255));
        itemsPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JScrollPane scrollPane = new JScrollPane(itemsPanel);
        scrollPane.setPreferredSize(new Dimension(370, 500));
        scrollPane.setMinimumSize(new Dimension(320, 300));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Lưu reference để cập nhật sau
        this.itemsPanel = itemsPanel;
        
        return panel;
    }
    
    private JPanel itemsPanel; // Panel chứa các item đã đặt
    
    private void loadCategories(JPanel categoryPanel) {
        List<LoaiMonDTO> categories = new ArrayList<>();
        
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT * FROM loaimon ORDER BY MaLoai";
            
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ResultSet rs = ps.executeQuery();
                
                while (rs.next()) {
                    LoaiMonDTO category = new LoaiMonDTO();
                    category.setMaLoai(rs.getInt("MaLoai"));
                    category.setTenLoai(rs.getString("TenLoai"));
                    categories.add(category);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải danh mục: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
        
        
        // Tạo buttons cho từng danh mục + button "Tất cả"
        categoryButtons = new JButton[categories.size() + 1];
        Color[] categoryColors = {
            new Color(255, 140, 0),    // Cam cho "Tất cả"
            new Color(34, 139, 34),    // Xanh lá
            new Color(128, 0, 128),    // Tím
            new Color(70, 130, 180),   // Xanh dương
            new Color(107, 142, 35)    // Xanh ô liu
        };
        
        // Button "Tất cả" đầu tiên
        categoryButtons[0] = new JButton("Tất cả");
        categoryButtons[0].setBackground(categoryColors[0]);
        categoryButtons[0].setForeground(Color.BLACK);
        categoryButtons[0].setFocusPainted(false);
        categoryButtons[0].setFont(new Font("Arial", Font.BOLD, 14));
        categoryButtons[0].setPreferredSize(new Dimension(100, 35));
        categoryButtons[0].setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        categoryButtons[0].setAlignmentX(Component.CENTER_ALIGNMENT);
        categoryPanel.add(categoryButtons[0]);
        categoryPanel.add(Box.createVerticalStrut(5));
        
        // Các button danh mục
        for (int i = 0; i < categories.size(); i++) {
            LoaiMonDTO category = categories.get(i);
            categoryButtons[i + 1] = new JButton(category.getTenLoai());
            categoryButtons[i + 1].setBackground(categoryColors[(i + 1) % categoryColors.length]);
            categoryButtons[i + 1].setForeground(Color.BLACK);
            categoryButtons[i + 1].setFocusPainted(false);
            categoryButtons[i + 1].setFont(new Font("Arial", Font.BOLD, 14));
            categoryButtons[i + 1].setPreferredSize(new Dimension(100, 35));
            categoryButtons[i + 1].setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
            categoryButtons[i + 1].setAlignmentX(Component.CENTER_ALIGNMENT);
            categoryPanel.add(categoryButtons[i + 1]);
            categoryPanel.add(Box.createVerticalStrut(5));
        }
    }
    
    private void setupEventHandlers() {
        // Event handlers cho danh mục
        for (int i = 0; i < categoryButtons.length; i++) {
            final int buttonIndex = i;
            categoryButtons[i].addActionListener(e -> {
                if (buttonIndex == 0) {
                    // Button "Tất cả"
                    currentCategoryId = 0;
                    loadProductsByCategory(0);
                } else {
                    // Button danh mục cụ thể
                    currentCategoryId = getCategoryIdByIndex(buttonIndex - 1); // -1 vì button đầu tiên là "Tất cả"
                    loadProductsByCategory(currentCategoryId);
                }
                updateCategoryButtons();
            });
        }
        
        // Event handlers cho các nút thao tác
        capNhatButton.addActionListener(e -> updateOrder());
        dongButton.addActionListener(e -> dispose());
        thanhToanButton.addActionListener(e -> processPayment());
        inHoaDonButton.addActionListener(e -> printInvoice());
        huyHoaDonButton.addActionListener(e -> cancelOrder());
        
        // Event handler cho giảm giá
        giamGiaSpinner.addChangeListener(e -> updateOrderSummary());
        
        // Event handler cho nút tìm kiếm khách hàng
        timKiemKhachHangButton.addActionListener(e -> timKiemKhachHangTheoSDT());
    }
    
    
    private void loadOrderData() {
        try (Connection conn = DBUtil.getConnection()) {
            // Load thông tin đơn hàng
            String sql = "SELECT dh.*, nv.HoTen FROM donhang dh " +
                        "LEFT JOIN nhanvien nv ON dh.MaNV = nv.MaNV " +
                        "WHERE dh.MaDon = ?";
            
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, currentOrder.getMaDon());
                ResultSet rs = ps.executeQuery();
                
                if (rs.next()) {
                    currentOrder.setMaDon(rs.getInt("MaDon"));
                    currentOrder.setMaNV(rs.getInt("MaNV"));
                    currentOrder.setTrangThai(rs.getString("TrangThai"));
                    currentOrder.setNgayDat(rs.getTimestamp("NgayDat"));
                    currentOrder.setTongTien(rs.getLong("TongTien"));
                    currentOrder.setGiamGia(rs.getInt("GiamGia"));
                    
                    // Load MaKH nếu có
                    int maKH = rs.getInt("MaKH");
                    if (!rs.wasNull()) {
                        currentOrder.setMaKH(maKH);
                        selectedKhachHangId = maKH;
                        originalKhachHangId = maKH; // Lưu mã khách hàng ban đầu
                    } else {
                        currentOrder.setMaKH(null);
                        selectedKhachHangId = 0;
                        originalKhachHangId = 0;
                    }
                    
                    // Cập nhật giao diện
                    maHDField.setText(String.valueOf(currentOrder.getMaDon()));
                    nhanVienField.setText(rs.getString("HoTen") != null ? rs.getString("HoTen") : "Admin");
                    
                    giamGiaSpinner.setValue(currentOrder.getGiamGia());
                    
                    // Cập nhật khách hàng nếu có
                    if (selectedKhachHangId > 0) {
                        // Load thông tin khách hàng từ database để hiển thị
                        String khSql = "SELECT * FROM khachhang WHERE MaKH = ?";
                        try (PreparedStatement khPs = conn.prepareStatement(khSql)) {
                            khPs.setInt(1, selectedKhachHangId);
                            try (ResultSet khRs = khPs.executeQuery()) {
                                if (khRs.next()) {
                                    khachHangTenField.setText(khRs.getString("HoTen"));
                                    khachHangSDTField.setText(khRs.getString("SDT"));
                                    khachHangDiemTichLuyField.setText(String.valueOf(khRs.getInt("DiemTichLuy")));
                                }
                            }
                        }
                    }
                    
                    // Cập nhật trạng thái
                    String trangThai = currentOrder.getTrangThai();
                    if (trangThai != null) {
                        trangThaiLabel.setText(convertTrangThaiToUI(trangThai));
                        if ("dathanhtoan".equals(trangThai)) {
                            trangThaiLabel.setForeground(Color.GREEN);
                        } else if ("bihuy".equals(trangThai)) {
                            trangThaiLabel.setForeground(Color.RED);
                        } else {
                            trangThaiLabel.setForeground(Color.ORANGE);
                        }
                    } else {
                        trangThaiLabel.setText("Chưa thanh toán");
                        trangThaiLabel.setForeground(Color.ORANGE);
                    }
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải thông tin đơn hàng: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    
    private void timKiemKhachHangTheoSDT() {
        String sdt = khachHangSDTField.getText().trim();
        
        if (sdt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số điện thoại!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM khachhang WHERE SDT = ?")) {
            
            ps.setString(1, sdt);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Khách hàng đã tồn tại, tự động điền thông tin
                    int maKH = rs.getInt("MaKH");
                    String hoTen = rs.getString("HoTen");
                    int diemTichLuy = rs.getInt("DiemTichLuy");
                    
                    // Điền thông tin vào các field
                    khachHangTenField.setText(hoTen);
                    khachHangDiemTichLuyField.setText(String.valueOf(diemTichLuy));
                    
                    // Cập nhật selected customer
                    selectedKhachHangId = maKH;
                    currentOrder.setMaKH(maKH);
                    
                    JOptionPane.showMessageDialog(this, "Đã tìm thấy khách hàng!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    // Không tìm thấy khách hàng - khách hàng mới
                    JOptionPane.showMessageDialog(this, "Khách hàng mới. Vui lòng nhập đầy đủ thông tin!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    // Đặt điểm tích lũy mặc định là 0 nếu chưa có
                    if (khachHangDiemTichLuyField.getText().trim().isEmpty()) {
                        khachHangDiemTichLuyField.setText("0");
                    }
                    selectedKhachHangId = 0;
                    currentOrder.setMaKH(null);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi tìm kiếm khách hàng: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadProducts() {
        // Load tất cả sản phẩm khi khởi tạo
        loadProductsByCategory(0); // 0 = tất cả danh mục
    }
    
    private void loadProductsByCategory(int categoryId) {
        productGridPanel.removeAll();
        currentProducts.clear();
        
        try (Connection conn = DBUtil.getConnection()) {
            String sql;
            PreparedStatement ps;
            
            if (categoryId == 0) {
                // Load tất cả sản phẩm có trạng thái 'ban'
                sql = "SELECT * FROM mon WHERE TinhTrang = 'dangban' ORDER BY MaLoai, TenMon";
                ps = conn.prepareStatement(sql);
            } else {
                // Load sản phẩm theo danh mục cụ thể
                sql = "SELECT * FROM mon WHERE MaLoai = ? AND TinhTrang = 'dangban' ORDER BY TenMon";
                ps = conn.prepareStatement(sql);
                ps.setInt(1, categoryId);
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    MonDTO product = new MonDTO();
                    product.setMaMon(rs.getInt("MaMon"));
                    product.setTenMon(rs.getString("TenMon"));
                    product.setGia(rs.getLong("Gia"));
                    product.setAnh(rs.getString("Anh"));
                    product.setMaLoai(rs.getInt("MaLoai"));
                    currentProducts.add(product);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải sản phẩm: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
        
        
        // Tạo hàng sản phẩm với 2 cột cố định
        for (int i = 0; i < currentProducts.size(); i += 2) {
            JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            rowPanel.setBackground(new Color(240, 248, 255));
            rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
            rowPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            // Sản phẩm đầu tiên trong hàng
            JPanel productPanel1 = createProductPanel(currentProducts.get(i));
            rowPanel.add(productPanel1);
            
            // Sản phẩm thứ hai trong hàng (nếu có)
            if (i + 1 < currentProducts.size()) {
                JPanel productPanel2 = createProductPanel(currentProducts.get(i + 1));
                rowPanel.add(productPanel2);
            } else {
                // Thêm panel trống nếu chỉ có 1 sản phẩm trong hàng
                JPanel emptyPanel = new JPanel();
                emptyPanel.setPreferredSize(new Dimension(200, 90));
                emptyPanel.setMinimumSize(new Dimension(200, 90));
                emptyPanel.setMaximumSize(new Dimension(200, 90));
                emptyPanel.setBackground(new Color(240, 248, 255));
                rowPanel.add(emptyPanel);
            }
            
            productGridPanel.add(rowPanel);
            productGridPanel.add(Box.createVerticalStrut(10));
        }
        
        productGridPanel.revalidate();
        productGridPanel.repaint();
        
    }
    
    private JPanel createProductPanel(MonDTO product) {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(200, 90);
            }
            
            @Override
            public Dimension getMinimumSize() {
                return new Dimension(200, 90);
            }
            
            @Override
            public Dimension getMaximumSize() {
                return new Dimension(200, 90);
            }
        };
        panel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        panel.setBackground(new Color(255, 182, 193)); // Màu hồng nhạt
        
        // Hình ảnh sản phẩm
        JLabel imageLabel = new JLabel("", JLabel.CENTER);
        imageLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        imageLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        
        // Load và hiển thị ảnh sản phẩm
        if (product.getAnh() != null && !product.getAnh().trim().isEmpty()) {
            try {
                String fullPath = "src/" + product.getAnh();
                java.io.File imageFile = new java.io.File(fullPath);
                
                if (imageFile.exists()) {
                    ImageIcon icon = new ImageIcon(fullPath);
                    Image image = icon.getImage();
                    
                    // Scale image to fit in panel (80x80)
                    int maxWidth = 80;
                    int maxHeight = 80;
                    int width = image.getWidth(null);
                    int height = image.getHeight(null);
                    
                    double scale = Math.min((double) maxWidth / width, (double) maxHeight / height);
                    int newWidth = (int) (width * scale);
                    int newHeight = (int) (height * scale);
                    
                    Image scaledImage = image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
                    ImageIcon scaledIcon = new ImageIcon(scaledImage);
                    
                    imageLabel.setIcon(scaledIcon);
                    imageLabel.setText("");
                } else {
                    imageLabel.setIcon(null);
                    imageLabel.setText("🛍️");
                }
            } catch (Exception e) {
                imageLabel.setIcon(null);
                imageLabel.setText("🛍️");
            }
        } else {
            imageLabel.setIcon(null);
            imageLabel.setText("🛍️");
        }
        
        // Tên sản phẩm
        JLabel nameLabel = new JLabel(product.getTenMon(), JLabel.CENTER);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 12));
        nameLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        
        // Giá
        JLabel priceLabel = new JLabel(String.format("%,dVND", product.getGia()), JLabel.CENTER);
        priceLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        priceLabel.setForeground(Color.RED);
        priceLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.add(nameLabel, BorderLayout.NORTH);
        infoPanel.add(priceLabel, BorderLayout.CENTER);
        
        panel.add(imageLabel, BorderLayout.CENTER);
        panel.add(infoPanel, BorderLayout.SOUTH);
        
        // Click event để thêm sản phẩm
        panel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                addProductToOrder(product);
            }
        });
        
        return panel;
    }
    
    private void addProductToOrder(MonDTO product) {
        // Mở dialog thêm món chi tiết
        ThemMonView dialog = new ThemMonView(this, product);
        dialog.setVisible(true);
        
        ThemMonView.AddItemResult result = dialog.getResult();
        if (result != null) {
            // Kiểm tra xem sản phẩm với topping tương tự đã có chưa
            boolean found = false;
            int soLuongCu = 0;
            for (ChiTietDonHangDTO item : orderedItems) {
                if (item.getMaMon() == result.maMon && 
                    item.getTenTopping() != null && 
                    item.getTenTopping().equals(result.tenTopping)) {
                    soLuongCu = item.getSoLuong();
                    found = true;
                    break;
                }
            }
            
            // Kiểm tra nguyên liệu trước khi thêm
            int soLuongMoi = found ? (soLuongCu + result.soLuong) : result.soLuong;
            String errorMessage = kiemTraNguyenLieu(result.maMon, soLuongMoi, result.maMon, result.tenTopping);
            if (errorMessage != null) {
                JOptionPane.showMessageDialog(this, errorMessage, "Không đủ nguyên liệu", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (found) {
                // Tăng số lượng cho món đã có
                for (ChiTietDonHangDTO item : orderedItems) {
                    if (item.getMaMon() == result.maMon && 
                        item.getTenTopping() != null && 
                        item.getTenTopping().equals(result.tenTopping)) {
                        item.setSoLuong(soLuongMoi);
                        break;
                    }
                }
            } else {
                // Thêm sản phẩm mới
                ChiTietDonHangDTO newItem = new ChiTietDonHangDTO();
                newItem.setMaDon(currentOrder.getMaDon());
                newItem.setMaMon(result.maMon);
                newItem.setTenMon(result.tenMon);
                newItem.setAnh(product.getAnh()); // Lưu ảnh từ sản phẩm
                newItem.setTenTopping(result.tenTopping);
                newItem.setSoLuong(result.soLuong);
                newItem.setGiaMon(result.giaMon);
                newItem.setGiaTopping(result.giaTopping);
                
                // Tìm mã topping
                int maTopping = findToppingId(result.tenTopping);
                newItem.setMaTopping(maTopping);
                
                orderedItems.add(newItem);
            }
            
            updateOrderedItemsTable();
            updateOrderSummary();
        }
    }
    
    // Kiểm tra xem có đủ nguyên liệu cho món với số lượng cho trước không
    private String kiemTraNguyenLieu(int maMon, int soLuong, int maMonCheck, String tenTopping) {
        HangHoaDAO hangHoaDAO = new HangHoaDAO();
        KhoHangDAO khoHangDAO = new KhoHangDAO();
        
        // Lấy danh sách nguyên liệu cần cho món
        List<MonNguyenLieuDTO> nguyenLieuList = hangHoaDAO.layNguyenLieuCuaMon(maMon);
        
        if (nguyenLieuList == null || nguyenLieuList.isEmpty()) {
            // Món không có nguyên liệu định nghĩa, cho phép thêm
            return null;
        }
        
        // Tính tổng nguyên liệu cần cho tất cả món trong đơn hàng (bao gồm món mới)
        java.util.Map<Integer, Integer> tongNguyenLieuCan = new java.util.HashMap<>();
        
        // Đếm nguyên liệu từ các món đã có trong đơn hàng
        for (ChiTietDonHangDTO item : orderedItems) {
            if (item.getMaMon() == maMonCheck && 
                item.getTenTopping() != null && 
                item.getTenTopping().equals(tenTopping)) {
                // Bỏ qua món này vì sẽ được thay thế bằng số lượng mới
                continue;
            }
            
            List<MonNguyenLieuDTO> nguyenLieuItem = hangHoaDAO.layNguyenLieuCuaMon(item.getMaMon());
            if (nguyenLieuItem != null) {
                for (MonNguyenLieuDTO nl : nguyenLieuItem) {
                    int tongSoLuong = nl.getSoLuong() * item.getSoLuong();
                    tongNguyenLieuCan.put(nl.getMaNL(), 
                        tongNguyenLieuCan.getOrDefault(nl.getMaNL(), 0) + tongSoLuong);
                }
            }
        }
        
        // Cộng thêm nguyên liệu của món mới
        for (MonNguyenLieuDTO nl : nguyenLieuList) {
            int tongSoLuong = nl.getSoLuong() * soLuong;
            tongNguyenLieuCan.put(nl.getMaNL(), 
                tongNguyenLieuCan.getOrDefault(nl.getMaNL(), 0) + tongSoLuong);
        }
        
        // Kiểm tra từng nguyên liệu có đủ trong kho không
        for (java.util.Map.Entry<Integer, Integer> entry : tongNguyenLieuCan.entrySet()) {
            int maNL = entry.getKey();
            int soLuongCan = entry.getValue();
            
            // Lấy tồn kho hiện tại
            dto.KhoHangDTO tonKho = khoHangDAO.layTonKhoTheoMaNL(maNL);
            if (tonKho == null || tonKho.getSoLuong() < soLuongCan) {
                // Tìm tên nguyên liệu để hiển thị thông báo
                String tenNL = "";
                for (MonNguyenLieuDTO nl : nguyenLieuList) {
                    if (nl.getMaNL() == maNL) {
                        tenNL = nl.getTenNL();
                        break;
                    }
                }
                if (tenNL.isEmpty()) {
                    // Tìm trong các nguyên liệu khác
                    for (ChiTietDonHangDTO item : orderedItems) {
                        List<MonNguyenLieuDTO> nlList = hangHoaDAO.layNguyenLieuCuaMon(item.getMaMon());
                        if (nlList != null) {
                            for (MonNguyenLieuDTO nl : nlList) {
                                if (nl.getMaNL() == maNL) {
                                    tenNL = nl.getTenNL();
                                    break;
                                }
                            }
                            if (!tenNL.isEmpty()) break;
                        }
                    }
                }
                
                int tonKhoHienTai = (tonKho != null) ? tonKho.getSoLuong() : 0;
                return "Không đủ nguyên liệu!\n" +
                       "Nguyên liệu: " + tenNL + "\n" +
                       "Cần: " + soLuongCan + " " + (tonKho != null ? tonKho.getTenDonVi() : "") + "\n" +
                       "Hiện có trong kho: " + tonKhoHienTai + " " + (tonKho != null ? tonKho.getTenDonVi() : "");
            }
        }
        
        return null; // Đủ nguyên liệu
    }
    
    // Kiểm tra các món trong đơn hàng có bị tạm ngưng không
    private List<String> kiemTraMonTamNgung() {
        List<String> danhSachMonTamNgung = new ArrayList<>();
        
        try (Connection conn = DBUtil.getConnection()) {
            for (ChiTietDonHangDTO item : orderedItems) {
                String sql = "SELECT TenMon, TinhTrang FROM mon WHERE MaMon = ?";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, item.getMaMon());
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            String tinhTrang = rs.getString("TinhTrang");
                            if ("ngungban".equals(tinhTrang)) {
                                String tenMon = rs.getString("TenMon");
                                danhSachMonTamNgung.add(tenMon);
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            // Ignore error, return empty list
        }
        
        return danhSachMonTamNgung;
    }
    
    private int findToppingId(String tenTopping) {
        if ("No Topping".equals(tenTopping)) {
            return 1; // Mã của "No Topping"
        }
        
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT MaMon FROM mon WHERE TenMon = ? AND MaLoai = 4";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, tenTopping);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    return rs.getInt("MaMon");
                }
            }
        } catch (SQLException e) {
            // Ignore error, return default
        }
        
        return 1; // Default to "No Topping"
    }
    
    private void loadOrderedItems() {
        orderedItems.clear();
        
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT ctdh.*, m1.TenMon AS TenMon, m1.Anh AS Anh, m2.TenMon AS TenTopping " +
                        "FROM chitietdonhang ctdh " +
                        "LEFT JOIN mon m1 ON ctdh.MaMon = m1.MaMon " +
                        "LEFT JOIN mon m2 ON ctdh.MaTopping = m2.MaMon " +
                        "WHERE ctdh.MaDon = ?";
            
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, currentOrder.getMaDon());
                ResultSet rs = ps.executeQuery();
                
                while (rs.next()) {
                    ChiTietDonHangDTO item = new ChiTietDonHangDTO();
                    item.setMaDon(rs.getInt("MaDon"));
                    item.setMaMon(rs.getInt("MaMon"));
                    item.setMaTopping(rs.getInt("MaTopping"));
                    item.setSoLuong(rs.getInt("SoLuong"));
                    item.setGiaMon(rs.getLong("GiaMon"));
                    item.setGiaTopping(rs.getLong("GiaTopping"));
                    
                    // Lưu tên sản phẩm, ảnh và topping để hiển thị
                    item.setTenMon(rs.getString("TenMon"));
                    item.setAnh(rs.getString("Anh"));
                    item.setTenTopping(rs.getString("TenTopping"));
                    
                    orderedItems.add(item);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải chi tiết đơn hàng: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
        
        updateOrderedItemsTable();
    }
    
    private void updateOrderedItemsTable() {
        if (itemsPanel == null) return;
        
        itemsPanel.removeAll();
        
        for (int i = 0; i < orderedItems.size(); i++) {
            ChiTietDonHangDTO item = orderedItems.get(i);
            JPanel itemPanel = createOrderedItemPanel(item, i);
            itemsPanel.add(itemPanel);
            itemsPanel.add(Box.createVerticalStrut(5));
        }
        
        itemsPanel.revalidate();
        itemsPanel.repaint();
    }
    
    private JPanel createOrderedItemPanel(ChiTietDonHangDTO item, int index) {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(350, 70);
            }
            
            @Override
            public Dimension getMinimumSize() {
                return new Dimension(350, 70);
            }
            
            @Override
            public Dimension getMaximumSize() {
                return new Dimension(350, 70);
            }
        };
        panel.setBackground(new Color(144, 238, 144)); // Màu xanh lá nhạt
        panel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        
        // Panel ảnh sản phẩm bên trái
        JLabel imageLabel = new JLabel("", JLabel.CENTER);
        imageLabel.setPreferredSize(new Dimension(60, 60));
        imageLabel.setMinimumSize(new Dimension(60, 60));
        imageLabel.setMaximumSize(new Dimension(60, 60));
        imageLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        imageLabel.setBackground(Color.BLACK);
        imageLabel.setOpaque(true);
        
        // Load và hiển thị ảnh sản phẩm
        if (item.getAnh() != null && !item.getAnh().trim().isEmpty()) {
            try {
                String fullPath = "src/" + item.getAnh();
                java.io.File imageFile = new java.io.File(fullPath);
                
                if (imageFile.exists()) {
                    ImageIcon icon = new ImageIcon(fullPath);
                    Image image = icon.getImage();
                    
                    // Scale image to fit in panel (60x60)
                    int maxWidth = 60;
                    int maxHeight = 60;
                    int width = image.getWidth(null);
                    int height = image.getHeight(null);
                    
                    double scale = Math.min((double) maxWidth / width, (double) maxHeight / height);
                    int newWidth = (int) (width * scale);
                    int newHeight = (int) (height * scale);
                    
                    Image scaledImage = image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
                    ImageIcon scaledIcon = new ImageIcon(scaledImage);
                    
                    imageLabel.setIcon(scaledIcon);
                    imageLabel.setText("");
                } else {
                    imageLabel.setIcon(null);
                    imageLabel.setText("🛍️");
                }
            } catch (Exception e) {
                imageLabel.setIcon(null);
                imageLabel.setText("🛍️");
            }
        } else {
            imageLabel.setIcon(null);
            imageLabel.setText("🛍️");
        }
        
        // Panel thông tin sản phẩm
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(new Color(144, 238, 144));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
        
        // Tên món
        JLabel nameLabel = new JLabel(item.getTenMon());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 12));
        nameLabel.setBackground(new Color(144, 238, 144));
        
        // Topping
        JLabel toppingLabel = new JLabel("No Topping");
        toppingLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        toppingLabel.setForeground(Color.GRAY);
        if (item.getTenTopping() != null && !item.getTenTopping().equals("No Topping")) {
            toppingLabel.setText(item.getTenTopping());
        }
        
        // Giá
        long donGia = item.getGiaMon() + item.getGiaTopping();
        JLabel priceLabel = new JLabel(String.format("%,d VND", donGia));
        priceLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        priceLabel.setForeground(Color.RED);
        
        // Panel thông tin bên trái
        JPanel leftInfoPanel = new JPanel();
        leftInfoPanel.setLayout(new BoxLayout(leftInfoPanel, BoxLayout.Y_AXIS));
        leftInfoPanel.setBackground(new Color(144, 238, 144));
        leftInfoPanel.add(nameLabel);
        leftInfoPanel.add(toppingLabel);
        leftInfoPanel.add(priceLabel);
        
        // Panel điều khiển số lượng bên phải
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        controlPanel.setBackground(new Color(144, 238, 144));
        
        JButton minusButton = new JButton("-");
        minusButton.setPreferredSize(new Dimension(25, 25));
        minusButton.setBackground(new Color(70, 130, 180));
        minusButton.setForeground(Color.BLACK);
        minusButton.setFocusPainted(false);
        minusButton.setFont(new Font("Arial", Font.BOLD, 12));
        minusButton.addActionListener(e -> {
            if (item.getSoLuong() > 1) {
                item.setSoLuong(item.getSoLuong() - 1);
                updateOrderedItemsTable();
                updateOrderSummary();
            } else {
                // Xóa item nếu số lượng = 0
                orderedItems.remove(index);
                updateOrderedItemsTable();
                updateOrderSummary();
            }
        });
        
        JLabel quantityLabel = new JLabel(String.valueOf(item.getSoLuong()));
        quantityLabel.setFont(new Font("Arial", Font.BOLD, 12));
        quantityLabel.setPreferredSize(new Dimension(30, 25));
        quantityLabel.setHorizontalAlignment(JLabel.CENTER);
        quantityLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        
        JButton plusButton = new JButton("+");
        plusButton.setPreferredSize(new Dimension(25, 25));
        plusButton.setBackground(new Color(70, 130, 180));
        plusButton.setForeground(Color.BLACK);
        plusButton.setFocusPainted(false);
        plusButton.setFont(new Font("Arial", Font.BOLD, 12));
        plusButton.addActionListener(e -> {
            item.setSoLuong(item.getSoLuong() + 1);
            updateOrderedItemsTable();
            updateOrderSummary();
        });
        
        controlPanel.add(minusButton);
        controlPanel.add(quantityLabel);
        controlPanel.add(plusButton);
        
        infoPanel.add(leftInfoPanel, BorderLayout.WEST);
        infoPanel.add(controlPanel, BorderLayout.EAST);
        
        // Thêm ảnh vào bên trái và thông tin vào giữa
        panel.add(imageLabel, BorderLayout.WEST);
        panel.add(infoPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void updateOrderSummary() {
        long tongTien = 0;
        for (ChiTietDonHangDTO item : orderedItems) {
            tongTien += (item.getGiaMon() + item.getGiaTopping()) * item.getSoLuong();
        }
        
        // Tự động tính giảm giá theo điểm tích lũy hiện có của khách hàng
        int giamGia = (Integer) giamGiaSpinner.getValue();
        try {
            int availablePoints = 0;
            if (khachHangDiemTichLuyField != null && !khachHangDiemTichLuyField.getText().trim().isEmpty()) {
                availablePoints = Integer.parseInt(khachHangDiemTichLuyField.getText().trim());
            }
            int autoDiscount = 0;
            if (availablePoints >= 500) {
                autoDiscount = 20;
            } else if (availablePoints >= 200) {
                autoDiscount = 10;
            } else if (availablePoints >= 100) {
                autoDiscount = 5;
            }
            if (autoDiscount != giamGia) {
                giamGia = autoDiscount;
                giamGiaSpinner.setValue(giamGia);
            }
        } catch (NumberFormatException ignore) {
            // Bỏ qua nếu không parse được điểm
        }
        
        long giamGiaAmount = tongTien * giamGia / 100;
        long phaiTra = tongTien - giamGiaAmount;
        
        tongTienLabel.setText(String.format("%,d", tongTien) + " VND");
        giamGiaAmountLabel.setText(giamGia + " %");
        phaiTraLabel.setText(String.format("%,d", phaiTra) + " VND");
        
        // Cập nhật tổng tiền trong đối tượng đơn hàng
        currentOrder.setTongTien(tongTien);
        currentOrder.setGiamGia(giamGia);
    }
    
    private int getCategoryIdByIndex(int index) {
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT MaLoai FROM loaimon ORDER BY MaLoai";
            
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ResultSet rs = ps.executeQuery();
                
                int currentIndex = 0;
                while (rs.next()) {
                    if (currentIndex == index) {
                        return rs.getInt("MaLoai");
                    }
                    currentIndex++;
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi lấy danh mục: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
        return 1; // Mặc định trả về 1 nếu có lỗi
    }
    
    private void updateCategoryButtons() {
        for (int i = 0; i < categoryButtons.length; i++) {
            if (i == 0) {
                // Button "Tất cả"
                if (currentCategoryId == 0) {
                    categoryButtons[i].setBorder(BorderFactory.createLineBorder(Color.YELLOW, 3));
                } else {
                    categoryButtons[i].setBorder(BorderFactory.createEmptyBorder());
                }
            } else {
                // Button danh mục cụ thể
                int categoryId = getCategoryIdByIndex(i - 1); // -1 vì button đầu tiên là "Tất cả"
                if (categoryId == currentCategoryId) {
                    categoryButtons[i].setBorder(BorderFactory.createLineBorder(Color.YELLOW, 3));
                } else {
                    categoryButtons[i].setBorder(BorderFactory.createEmptyBorder());
                }
            }
        }
    }
    
    private void updateOrder() {
        // Kiểm tra xem có sản phẩm nào trong đơn hàng không
        if (orderedItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng thêm ít nhất một sản phẩm vào đơn hàng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Kiểm tra thông tin khách hàng nếu có nhập
        String sdt = khachHangSDTField.getText().trim();
        String ten = khachHangTenField.getText().trim();
        
        if (!sdt.isEmpty() && ten.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên khách hàng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (sdt.isEmpty() && !ten.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số điện thoại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            
            int maKH = selectedKhachHangId;
            boolean createdNewCustomer = false;
            
            // Nếu có nhập thông tin khách hàng nhưng chưa có MaKH (khách hàng mới)
            // Chỉ tạo khách hàng mới nếu đơn hàng chưa có khách hàng ban đầu
            if (originalKhachHangId == 0 && selectedKhachHangId == 0 && !sdt.isEmpty() && !ten.isEmpty()) {
                // Kiểm tra số điện thoại đã tồn tại chưa
                try (PreparedStatement checkPs = conn.prepareStatement("SELECT MaKH FROM khachhang WHERE SDT = ?")) {
                    checkPs.setString(1, sdt);
                    try (ResultSet rs = checkPs.executeQuery()) {
                        if (rs.next()) {
                            conn.rollback();
                            JOptionPane.showMessageDialog(this, "Số điện thoại này đã được sử dụng bởi khách hàng khác!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    }
                }
                
                // Tạo khách hàng mới
                String insertKhSql = "INSERT INTO khachhang (SDT, HoTen, DiemTichLuy) VALUES (?, ?, 0)";
                try (PreparedStatement ps = conn.prepareStatement(insertKhSql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, sdt);
                    ps.setString(2, ten);
                    ps.executeUpdate();
                    
                    // Lấy MaKH vừa tạo
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) {
                            maKH = rs.getInt(1);
                            selectedKhachHangId = maKH;
                            currentOrder.setMaKH(maKH);
                            createdNewCustomer = true;
                            // Cập nhật điểm tích lũy về 0
                            khachHangDiemTichLuyField.setText("0");
                        }
                    }
                }
            } else if (originalKhachHangId > 0) {
                // Nếu đã có khách hàng ban đầu, giữ nguyên
                maKH = originalKhachHangId;
            }
            
            // Cập nhật thông tin đơn hàng
            String updateOrderSql = "UPDATE donhang SET MaKH = ?, GiamGia = ?, TongTien = ? WHERE MaDon = ?";
            try (PreparedStatement ps = conn.prepareStatement(updateOrderSql)) {
                // Cập nhật MaKH
                if (maKH > 0) {
                    ps.setInt(1, maKH);
                } else {
                    ps.setNull(1, java.sql.Types.INTEGER);
                }
                
                ps.setInt(2, (Integer) giamGiaSpinner.getValue());
                ps.setLong(3, currentOrder.getTongTien());
                ps.setInt(4, currentOrder.getMaDon());
                ps.executeUpdate();
            }
            
            // Xóa chi tiết cũ
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM chitietdonhang WHERE MaDon = ?")) {
                ps.setInt(1, currentOrder.getMaDon());
                ps.executeUpdate();
            }
            
            // Thêm chi tiết mới
            String insertDetailSql = "INSERT INTO chitietdonhang (MaDon, MaMon, MaTopping, SoLuong, GiaMon, GiaTopping) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(insertDetailSql)) {
                for (ChiTietDonHangDTO item : orderedItems) {
                    ps.setInt(1, item.getMaDon());
                    ps.setInt(2, item.getMaMon());
                    ps.setInt(3, item.getMaTopping());
                    ps.setInt(4, item.getSoLuong());
                    ps.setLong(5, item.getGiaMon());
                    ps.setLong(6, item.getGiaTopping());
                    ps.executeUpdate();
                }
            }
            
            conn.commit();
            
            String message = "Cập nhật đơn hàng thành công!";
            if (createdNewCustomer) {
                message += "\nĐã tạo khách hàng mới với số điện thoại: " + sdt;
            }
            JOptionPane.showMessageDialog(this, message, "Thành công", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (SQLException e) {
            try {
                Connection rollbackConn = DBUtil.getConnection();
                rollbackConn.rollback();
            } catch (SQLException rollbackEx) {
                // Ignore rollback error
            }
            JOptionPane.showMessageDialog(this, "Lỗi cập nhật đơn hàng: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void processPayment() {
        // Kiểm tra xem có sản phẩm nào trong đơn hàng không
        if (orderedItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không thể thanh toán đơn hàng trống! Vui lòng thêm sản phẩm vào đơn hàng.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Kiểm tra món tạm ngưng
        List<String> monTamNgung = kiemTraMonTamNgung();
        if (!monTamNgung.isEmpty()) {
            StringBuilder message = new StringBuilder();
            message.append("⚠️ Các món sau đang tạm ngưng phục vụ:\n\n");
            for (String tenMon : monTamNgung) {
                message.append("• ").append(tenMon).append("\n");
            }
            message.append("\nBạn có muốn tiếp tục thanh toán không?");
            
            int confirmTamNgung = JOptionPane.showConfirmDialog(this, 
                message.toString(), 
                "Cảnh báo món tạm ngưng", 
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            
            if (confirmTamNgung != JOptionPane.YES_OPTION) {
                return; // Người dùng không muốn tiếp tục
            }
        }
        
        int result = JOptionPane.showConfirmDialog(this, 
            "Xác nhận thanh toán cho đơn hàng #" + currentOrder.getMaDon() + "?\n\n" +
            "Lưu ý: Nguyên liệu trong kho sẽ tự động được trừ sau khi thanh toán.", 
            "Xác nhận thanh toán", JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            DonHangDAO donHangDAO = new DonHangDAO();
            if (donHangDAO.capNhatTrangThaiDonHang(currentOrder.getMaDon(), "dathanhtoan")) {
                currentOrder.setTrangThai("dathanhtoan");
                trangThaiLabel.setText("Đã thanh toán");
                trangThaiLabel.setForeground(Color.GREEN);

                // Cập nhật điểm tích lũy của khách hàng (nếu có)
                try (Connection conn = DBUtil.getConnection()) {
                    Integer maKH = currentOrder.getMaKH();
                    if (maKH != null && maKH > 0) {
                        long tongTien = currentOrder.getTongTien();
                        int giamGia = currentOrder.getGiamGia();
                        long phaiTra = tongTien - (tongTien * giamGia / 100);

                        int pointsUsed = 0;
                        if (giamGia >= 20) pointsUsed = 500;
                        else if (giamGia >= 10) pointsUsed = 200;
                        else if (giamGia >= 5) pointsUsed = 100;

                        int earnedPoints = (int)(phaiTra / 10000);

                        int currentPoints = 0;
                        try (PreparedStatement ps = conn.prepareStatement("SELECT DiemTichLuy FROM khachhang WHERE MaKH=?")) {
                            ps.setInt(1, maKH);
                            try (ResultSet rs = ps.executeQuery()) {
                                if (rs.next()) currentPoints = rs.getInt(1);
                            }
                        }

                        int newPoints = Math.max(0, currentPoints - pointsUsed) + earnedPoints;
                        try (PreparedStatement ps = conn.prepareStatement("UPDATE khachhang SET DiemTichLuy=? WHERE MaKH=?")) {
                            ps.setInt(1, newPoints);
                            ps.setInt(2, maKH);
                            ps.executeUpdate();
                        }

                        // Cập nhật UI điểm
                        if (khachHangDiemTichLuyField != null) {
                            khachHangDiemTichLuyField.setText(String.valueOf(newPoints));
                        }
                    }
                } catch (SQLException ignore) {
                }

                JOptionPane.showMessageDialog(this, "Thanh toán thành công!\nNguyên liệu trong kho đã được cập nhật.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi thanh toán! Vui lòng kiểm tra lại số lượng nguyên liệu trong kho.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void printInvoice() {
        JOptionPane.showMessageDialog(this, "Chức năng in hóa đơn đang được phát triển!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void cancelOrder() {
        int result = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc chắn muốn hủy đơn hàng #" + currentOrder.getMaDon() + "?", 
            "Xác nhận hủy đơn hàng", JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            try (Connection conn = DBUtil.getConnection()) {
                // Cập nhật trạng thái đơn hàng thành "Bị hủy"
                String sql = "UPDATE donhang SET TrangThai = ? WHERE MaDon = ?";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, "bihuy");
                    ps.setInt(2, currentOrder.getMaDon());
                    ps.executeUpdate();
                }
                
                currentOrder.setTrangThai("bihuy");
                trangThaiLabel.setText("Bị hủy");
                trangThaiLabel.setForeground(Color.RED);
                
                JOptionPane.showMessageDialog(this, "Hủy đơn hàng thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Lỗi hủy đơn hàng: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // Method chuyển đổi trạng thái từ database sang giao diện
    private String convertTrangThaiToUI(String trangThaiDB) {
        if ("dathanhtoan".equals(trangThaiDB)) {
            return "Đã thanh toán";
        } else if ("chuathanhtoan".equals(trangThaiDB)) {
            return "Chưa thanh toán";
        } else if ("bihuy".equals(trangThaiDB)) {
            return "Bị hủy";
        }
        return "Chưa thanh toán"; // Mặc định
    }
}
