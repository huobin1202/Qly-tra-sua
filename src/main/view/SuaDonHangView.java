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

public class SuaDonHangView extends JDialog {
    // Thông tin hóa đơn
    private JTextField maHDField;
    private JTextField nhanVienField;
    private JComboBox<String> loaiHoaDonCombo;
    private JSpinner giamGiaSpinner;
    
    // Thông tin hóa đơn hiển thị
    private JLabel trangThaiLabel;
    private JLabel tongTienLabel;
    private JLabel giamGiaAmountLabel;
    private JLabel phaiTraLabel;
    private JLabel daThanhToanLabel;
    
    // Danh mục sản phẩm
    private JButton[] categoryButtons;
    private JPanel productGridPanel;
    private List<MonDTO> currentProducts;
    
    // Danh sách món đã đặt
    private List<ChiTietDonHangDTO> orderedItems;
    
    // Nút thao tác
    private JButton quanLyShipButton;
    private JButton thanhToanButton;
    private JButton capNhatButton;
    private JButton dongButton;
    private JButton inHoaDonButton;
    private JButton huyHoaDonButton;
    
    // Dữ liệu
    private final DonHangDTO currentOrder;
    private int currentCategoryId = 1; // Sẽ được cập nhật từ database
    
    public SuaDonHangView(Window parent, int maDon) {
        super(parent, "Cập nhật hóa đơn", ModalityType.APPLICATION_MODAL);
        this.currentOrder = new DonHangDTO();
        this.currentOrder.setMaDon(maDon);
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadOrderData();
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
        
        loaiHoaDonCombo = new JComboBox<>(new String[]{"Đặt hàng", "Tại chỗ"});
        
        giamGiaSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
        giamGiaSpinner.setPreferredSize(new Dimension(80, 25));
        
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
        
        daThanhToanLabel = new JLabel("0 VND");
        daThanhToanLabel.setForeground(Color.RED);
        daThanhToanLabel.setFont(new Font("Arial", Font.BOLD, 12));
        
        // Khởi tạo danh mục sản phẩm
        currentProducts = new ArrayList<>();
        orderedItems = new ArrayList<>();
        
        // Khởi tạo danh sách món đã đặt
        
        // Khởi tạo các nút thao tác
        quanLyShipButton = new JButton("Quản lý ship");
        quanLyShipButton.setBackground(new Color(70, 130, 180));
        quanLyShipButton.setForeground(Color.BLACK);
        quanLyShipButton.setFocusPainted(false);
        
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
        
        // Loại hóa đơn
        gbc.gridx = 0; gbc.gridy = 2;
        infoPanel.add(new JLabel("Loại hóa đơn:"), gbc);
        gbc.gridx = 1;
        infoPanel.add(loaiHoaDonCombo, gbc);
        
        // Giảm giá
        gbc.gridx = 0; gbc.gridy = 3;
        infoPanel.add(new JLabel("Giảm giá(%):"), gbc);
        gbc.gridx = 1;
        infoPanel.add(giamGiaSpinner, gbc);
        
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
        
        // Đã thanh toán
        gbc.gridx = 0; gbc.gridy = 4;
        summaryPanel.add(new JLabel("Đã thanh toán:"), gbc);
        gbc.gridx = 1;
        summaryPanel.add(daThanhToanLabel, gbc);
        
        // Panel nút thao tác
        JPanel actionPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        actionPanel.setBackground(new Color(240, 248, 255));
        actionPanel.setBorder(BorderFactory.createTitledBorder("Thao tác"));
        
        actionPanel.add(quanLyShipButton);
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
            e.printStackTrace();
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
        quanLyShipButton.addActionListener(e -> manageShipping());
        inHoaDonButton.addActionListener(e -> printInvoice());
        huyHoaDonButton.addActionListener(e -> cancelOrder());
        
        // Event handler cho giảm giá
        giamGiaSpinner.addChangeListener(e -> updateOrderSummary());
    }
    
    
    private void loadOrderData() {
        try (Connection conn = DBUtil.getConnection()) {
            // Load thông tin đơn hàng
            String sql = "SELECT dh.*, nv.HoTen FROM dondathang dh " +
                        "LEFT JOIN nhanvien nv ON dh.MaNV = nv.MaNV " +
                        "WHERE dh.MaDon = ?";
            
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, currentOrder.getMaDon());
                ResultSet rs = ps.executeQuery();
                
                if (rs.next()) {
                    currentOrder.setMaDon(rs.getInt("MaDon"));
                    currentOrder.setMaNV(rs.getInt("MaNV"));
                    currentOrder.setLoai(rs.getString("Loai"));
                    currentOrder.setTrangThai(rs.getString("TrangThai"));
                    currentOrder.setNgayDat(rs.getTimestamp("NgayDat"));
                    currentOrder.setTongTien(rs.getLong("TongTien"));
                    currentOrder.setGiamGia(rs.getInt("GiamGia"));
                    
                    // Cập nhật giao diện
                    maHDField.setText(String.valueOf(currentOrder.getMaDon()));
                    nhanVienField.setText(rs.getString("HoTen") != null ? rs.getString("HoTen") : "Admin");
                    
                    // Set loại hóa đơn
                    String loai = currentOrder.getLoai();
                    if ("taiquan".equals(loai)) {
                        loaiHoaDonCombo.setSelectedItem("Tại chỗ");
                    } else if ("online".equals(loai)) {
                        loaiHoaDonCombo.setSelectedItem("Đặt hàng");
                    } else {
                        loaiHoaDonCombo.setSelectedItem("Mang đi");
                    }
                    
                    giamGiaSpinner.setValue(currentOrder.getGiamGia());
                    
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
                    product.setMoTa(rs.getString("MoTa"));
                    product.setAnh(rs.getString("Anh"));
                    product.setMaLoai(rs.getInt("MaLoai"));
                    currentProducts.add(product);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải sản phẩm: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
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
            for (ChiTietDonHangDTO item : orderedItems) {
                if (item.getMaMon() == result.maMon && 
                    item.getTenTopping() != null && 
                    item.getTenTopping().equals(result.tenTopping)) {
                    // Tăng số lượng
                    item.setSoLuong(item.getSoLuong() + result.soLuong);
                    found = true;
                    break;
                }
            }
            
            if (!found) {
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
                newItem.setGhiChu("");
                
                // Tìm mã topping
                int maTopping = findToppingId(result.tenTopping);
                newItem.setMaTopping(maTopping);
                
                orderedItems.add(newItem);
            }
            
            updateOrderedItemsTable();
            updateOrderSummary();
        }
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
                    item.setGhiChu(rs.getString("GhiChu"));
                    
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
        
        int giamGia = (Integer) giamGiaSpinner.getValue();
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
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            
            // Cập nhật thông tin đơn hàng
            String updateOrderSql = "UPDATE dondathang SET Loai = ?, GiamGia = ?, TongTien = ? WHERE MaDon = ?";
            try (PreparedStatement ps = conn.prepareStatement(updateOrderSql)) {
                String loai = (String) loaiHoaDonCombo.getSelectedItem();
                String loaiValue = "taiquan"; // Mặc định
                if ("Đặt hàng".equals(loai)) {
                    loaiValue = "online";
                } else if ("Mang đi".equals(loai)) {
                    loaiValue = "mangdi";
                }
                
                ps.setString(1, loaiValue);
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
            String insertDetailSql = "INSERT INTO chitietdonhang (MaDon, MaMon, MaTopping, SoLuong, GiaMon, GiaTopping, GhiChu) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(insertDetailSql)) {
                for (ChiTietDonHangDTO item : orderedItems) {
                    ps.setInt(1, item.getMaDon());
                    ps.setInt(2, item.getMaMon());
                    ps.setInt(3, item.getMaTopping());
                    ps.setInt(4, item.getSoLuong());
                    ps.setLong(5, item.getGiaMon());
                    ps.setLong(6, item.getGiaTopping());
                    ps.setString(7, item.getGhiChu());
                    ps.executeUpdate();
                }
            }
            
            conn.commit();
            JOptionPane.showMessageDialog(this, "Cập nhật đơn hàng thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi cập nhật đơn hàng: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void processPayment() {
        int result = JOptionPane.showConfirmDialog(this, 
            "Xác nhận thanh toán cho đơn hàng #" + currentOrder.getMaDon() + "?", 
            "Xác nhận thanh toán", JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            try (Connection conn = DBUtil.getConnection()) {
                String sql = "UPDATE dondathang SET TrangThai = ? WHERE MaDon = ?";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, "dathanhtoan");
                    ps.setInt(2, currentOrder.getMaDon());
                    ps.executeUpdate();
                }
                
                currentOrder.setTrangThai("dathanhtoan");
                trangThaiLabel.setText("Đã thanh toán");
                trangThaiLabel.setForeground(Color.GREEN);
                
                JOptionPane.showMessageDialog(this, "Thanh toán thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Lỗi thanh toán: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void manageShipping() {
        JOptionPane.showMessageDialog(this, "Chức năng quản lý ship đang được phát triển!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
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
                String sql = "UPDATE dondathang SET TrangThai = ? WHERE MaDon = ?";
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
