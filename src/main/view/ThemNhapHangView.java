package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import database.DBUtil;
import dto.NhaCungCapSanPhamDTO;
import dto.SanPhamChonNhapDTO;
import dto.NhaCungCapDTO;
import database.Session;

public class ThemNhapHangView extends JPanel {
    // Left panel components
    private JTable supplierProductsTable;
    private DefaultTableModel supplierProductsTableModel;
    private JTextField searchField;
    private JTextField quantityField;
    private JButton addButton;
    private JButton refreshButton;
    
    // Right panel components
    private JTextField receiptCodeField;
    private JComboBox<String> supplierCombo;
    private JTextField creatorField;
    private JTable selectedProductsTable;
    private DefaultTableModel selectedProductsTableModel;
    private JLabel totalAmountLabel;
    private JButton importButton;
    private JButton editQuantityButton;
    private JButton deleteProductButton;
    
    // Data
    private List<NhaCungCapSanPhamDTO> supplierProducts;
    private List<SanPhamChonNhapDTO> selectedProducts;
    private List<NhaCungCapDTO> suppliers;
    private int currentReceiptCode;
    private int currentSupplierId;
    private int editModeMaPN = -1; // -1 = thêm mới, >0 = sửa phiếu nhập
    
    public ThemNhapHangView() {
        this.editModeMaPN = -1; // Chế độ thêm mới
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadInitialData();
    }
    
    public ThemNhapHangView(int maPN) {
        this.editModeMaPN = maPN; // Chế độ sửa
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadInitialData();
    }
    
    private void initializeComponents() {
        // Left panel - Supplier products
        String[] supplierColumns = {"Mã NL", "Tên nguyên liệu", "Đơn vị", "Số lượng", "Đơn giá"};
        supplierProductsTableModel = new DefaultTableModel(supplierColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        supplierProductsTable = new JTable(supplierProductsTableModel);
        supplierProductsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        supplierProductsTable.setRowHeight(25);
        supplierProductsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        supplierProductsTable.setFont(new Font("Arial", Font.PLAIN, 12));
        
        searchField = new JTextField(20);
        quantityField = new JTextField(5);
        quantityField.setText("1");
        addButton = new JButton("➕ Thêm");
        addButton.setBackground(new Color(34, 139, 34));
        addButton.setForeground(Color.BLACK);
        addButton.setFocusPainted(false);
        refreshButton = new JButton("🔄 Làm mới");
        refreshButton.setBackground(new Color(70, 130, 180));
        refreshButton.setForeground(Color.BLACK);
        refreshButton.setFocusPainted(false);
        
        // Right panel - Import receipt details
        String[] selectedColumns = {"STT", "Mã NL", "Tên nguyên liệu", "Đơn vị", "Số lượng", "Đơn giá"};
        selectedProductsTableModel = new DefaultTableModel(selectedColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        selectedProductsTable = new JTable(selectedProductsTableModel);
        selectedProductsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        selectedProductsTable.setRowHeight(25);
        selectedProductsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        selectedProductsTable.setFont(new Font("Arial", Font.PLAIN, 12));
        
        receiptCodeField = new JTextField(10);
        receiptCodeField.setEditable(false);
        supplierCombo = new JComboBox<>();
        
        // Vô hiệu hóa nhà cung cấp khi ở chế độ sửa
        if (editModeMaPN > 0) {
            supplierCombo.setEnabled(false);
        }
        
        creatorField = new JTextField(15);
        creatorField.setEditable(false);
        creatorField.setText(Session.currentTaiKhoan);
        
        totalAmountLabel = new JLabel("Tổng tiền: 0 VNĐ");
        totalAmountLabel.setFont(new Font("Arial", Font.BOLD, 14));
        totalAmountLabel.setForeground(new Color(220, 20, 60));
        
        // Thay đổi text nút dựa trên chế độ
        if (editModeMaPN > 0) {
            importButton = new JButton("Cập nhật");
        } else {
            importButton = new JButton("Nhập hàng");
        }
        importButton.setBackground(new Color(34, 139, 34));
        importButton.setForeground(Color.BLACK);
        importButton.setFocusPainted(false);
        importButton.setFont(new Font("Arial", Font.BOLD, 14));
        
        editQuantityButton = new JButton("Sửa số lượng");
        editQuantityButton.setBackground(new Color(255, 140, 0));
        editQuantityButton.setForeground(Color.BLACK);
        editQuantityButton.setFocusPainted(false);
        
        deleteProductButton = new JButton("Xóa sản phẩm");
        deleteProductButton.setBackground(new Color(220, 20, 60));
        deleteProductButton.setForeground(Color.BLACK);
        deleteProductButton.setFocusPainted(false);
        importButton.setFont(new Font("Arial", Font.BOLD, 14));

        
        // Initialize data lists
        supplierProducts = new ArrayList<>();
        selectedProducts = new ArrayList<>();
        suppliers = new ArrayList<>();
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 248, 255));
        
        // Main container with two panels
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(500);
        splitPane.setDividerSize(5);
        splitPane.setResizeWeight(0.5);
        
        // Left panel - Supplier products
        JPanel leftPanel = createLeftPanel();
        splitPane.setLeftComponent(leftPanel);
        
        // Right panel - Import receipt details
        JPanel rightPanel = createRightPanel();
        splitPane.setRightComponent(rightPanel);
        
        add(splitPane, BorderLayout.CENTER);
    }
    
    private JPanel createLeftPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 248, 255));
        panel.setBorder(BorderFactory.createTitledBorder("Danh sách sản phẩm nhà cung cấp"));
        
        // Top controls
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(new Color(240, 248, 255));
        topPanel.add(new JLabel("Tìm kiếm:"));
        topPanel.add(searchField);
        topPanel.add(refreshButton);
        
        // Table
        JScrollPane scrollPane = new JScrollPane(supplierProductsTable);
        scrollPane.setPreferredSize(new Dimension(480, 300));
        
        // Bottom controls
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.setBackground(new Color(240, 248, 255));
        bottomPanel.add(new JLabel("Số lượng:"));
        bottomPanel.add(quantityField);
        bottomPanel.add(addButton);
        
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createRightPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 248, 255));
        panel.setBorder(BorderFactory.createTitledBorder("Chi tiết phiếu nhập"));
        
        // Header information
        JPanel headerPanel = new JPanel(new GridBagLayout());
        headerPanel.setBackground(new Color(240, 248, 255));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Mã phiếu nhập
        gbc.gridx = 0; gbc.gridy = 0;
        headerPanel.add(new JLabel("Mã phiếu nhập:"), gbc);
        gbc.gridx = 1;
        headerPanel.add(receiptCodeField, gbc);
        
        // Nhà cung cấp
        gbc.gridx = 0; gbc.gridy = 1;
        headerPanel.add(new JLabel("Nhà cung cấp:"), gbc);
        gbc.gridx = 1;
        headerPanel.add(supplierCombo, gbc);
        
        // Người tạo phiếu
        gbc.gridx = 0; gbc.gridy = 2;
        headerPanel.add(new JLabel("Người tạo phiếu:"), gbc);
        gbc.gridx = 1;
        headerPanel.add(creatorField, gbc);
        
        // Selected products table
        JScrollPane scrollPane = new JScrollPane(selectedProductsTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Bảng sản phẩm được chọn để nhập"));
        scrollPane.setPreferredSize(new Dimension(450, 200));
        
        // Action buttons (chỉ giữ nút sửa số lượng)
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actionPanel.setBackground(new Color(240, 248, 255));
        actionPanel.add(editQuantityButton);
        
        // Bottom panel with total and import/delete buttons
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(240, 248, 255));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        totalPanel.setBackground(new Color(240, 248, 255));
        totalPanel.add(totalAmountLabel);
        
        JPanel importPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        importPanel.setBackground(new Color(240, 248, 255));
        importPanel.add(importButton);
        importPanel.add(deleteProductButton); // Thêm nút xóa kế nút nhập hàng
        
        bottomPanel.add(totalPanel, BorderLayout.WEST);
        bottomPanel.add(importPanel, BorderLayout.EAST);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(actionPanel, BorderLayout.SOUTH);
        panel.add(bottomPanel, BorderLayout.PAGE_END);
        
        return panel;
    }
    
    private void setupEventHandlers() {
        // Search functionality
        searchField.addActionListener(e -> performSearch());
        
        // Refresh button
        refreshButton.addActionListener(e -> loadSupplierProducts());
        
        // Add product to import receipt
        addButton.addActionListener(e -> addProductToReceipt());
        
        // Supplier selection change
        supplierCombo.addActionListener(e -> onSupplierChanged());
        
        // Edit quantity
        editQuantityButton.addActionListener(e -> editQuantity());
        
        // Delete product
        deleteProductButton.addActionListener(e -> deleteProduct());
        
        // Import goods
        importButton.addActionListener(e -> importGoods());
        
        // Double click to edit quantity
        selectedProductsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    editQuantity();
                }
            }
        });
    }
    
    private void loadInitialData() {
        loadSuppliers();
        if (editModeMaPN > 0) {
            // Chế độ sửa: load dữ liệu phiếu nhập hiện tại
            loadReceiptData();
        } else {
            // Chế độ thêm mới: tạo mã phiếu nhập mới
            generateNewReceiptCode();
        }
        loadSupplierProducts();
    }
    
    private void loadReceiptData() {
        try (Connection conn = DBUtil.getConnection()) {
            // Load thông tin phiếu nhập
            String sql = "SELECT p.*, nv.HoTen as TenNV, ncc.TenNCC " +
                        "FROM phieunhap p " +
                        "LEFT JOIN nhanvien nv ON p.MaNV = nv.MaNV " +
                        "LEFT JOIN nhacungcap ncc ON p.MaNCC = ncc.MaNCC " +
                        "WHERE p.MaPN = ?";
            
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, editModeMaPN);
                ResultSet rs = ps.executeQuery();
                
                if (rs.next()) {
                    receiptCodeField.setText(String.valueOf(editModeMaPN));
                    supplierCombo.setSelectedItem(rs.getString("TenNCC"));
                    creatorField.setText(rs.getString("TenNV"));
                    currentSupplierId = rs.getInt("MaNCC");
                }
            }
            
            // Load chi tiết phiếu nhập
            loadSelectedProductsFromDB();
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải thông tin phiếu nhập: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadSelectedProductsFromDB() {
        selectedProductsTableModel.setRowCount(0);
        selectedProducts.clear();
        
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT ctnh.*, nl.TenNL, nl.DonVi " +
                        "FROM chitietnhap_nl ctnh " +
                        "JOIN nguyenlieu nl ON ctnh.MaNL = nl.MaNL " +
                        "WHERE ctnh.MaPN = ?";
            
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, editModeMaPN);
                ResultSet rs = ps.executeQuery();
                
                long totalAmount = 0;
                
                while (rs.next()) {
                    SanPhamChonNhapDTO product = new SanPhamChonNhapDTO();
                    product.setMaNL(rs.getInt("MaNL"));
                    product.setTenNL(rs.getString("TenNL"));
                    product.setSoLuong(rs.getInt("SoLuong"));
                    product.setDonGia(rs.getLong("DonGia"));
                    product.setDonVi(rs.getString("DonVi"));
                    product.setThanhTien(product.getSoLuong() * product.getDonGia());
                    
                    selectedProducts.add(product);
                    totalAmount += product.getThanhTien();
                    
                    Object[] row = {
                        product.getMaNL(),
                        product.getTenNL(),
                        product.getDonVi(),
                        product.getSoLuong(),
                        String.format("%,d", product.getDonGia()) + " VNĐ"
                    };
                    selectedProductsTableModel.addRow(row);
                }
                
                totalAmountLabel.setText("Tổng tiền: " + String.format("%,d", totalAmount) + " VNĐ");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải chi tiết phiếu nhập: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadSuppliers() {
        suppliers.clear();
        supplierCombo.removeAllItems();
        
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM nhacungcap ORDER BY TenNCC")) {
            
            while (rs.next()) {
                NhaCungCapDTO supplier = new NhaCungCapDTO(
                    rs.getInt("MaNCC"),
                    rs.getString("TenNCC"),
                    rs.getString("SDT"),
                    rs.getString("DiaChi")
                );
                suppliers.add(supplier);
                supplierCombo.addItem(supplier.getTenNCC());
            }
            
            if (!suppliers.isEmpty()) {
                currentSupplierId = suppliers.get(0).getMaNCC();
                loadSupplierProducts();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải danh sách nhà cung cấp: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadSupplierProducts() {
        supplierProducts.clear();
        supplierProductsTableModel.setRowCount(0);
        
        if (currentSupplierId == 0) return;
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "SELECT ncc.MaNCC, ncc.TenNCC, nl.MaNL, nl.TenNL, nl.DonVi, nccnl.SoLuong, nccnl.DonGia " +
                 "FROM nhacungcap ncc " +
                 "JOIN ncc_nguyenlieu nccnl ON ncc.MaNCC = nccnl.MaNCC " +
                 "JOIN nguyenlieu nl ON nccnl.MaNL = nl.MaNL " +
                 "WHERE ncc.MaNCC = ? AND nccnl.SoLuong > 0 " +
                 "ORDER BY nl.TenNL")) {
            
            ps.setInt(1, currentSupplierId);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    NhaCungCapSanPhamDTO product = new NhaCungCapSanPhamDTO(
                        rs.getInt("MaNCC"),
                        rs.getString("TenNCC"),
                        rs.getInt("MaNL"),
                        rs.getString("TenNL"),
                        rs.getString("DonVi"),
                        rs.getInt("SoLuong"),
                        rs.getLong("DonGia")
                    );
                    supplierProducts.add(product);
                    
                    Object[] row = {
                        product.getMaNL(),
                        product.getTenNL(),
                        product.getDonVi(),
                        product.getSoLuong(),
                        String.format("%,d", product.getDonGia()) + " VNĐ"
                    };
                    supplierProductsTableModel.addRow(row);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải sản phẩm nhà cung cấp: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void performSearch() {
        String searchText = searchField.getText().trim().toLowerCase();
        supplierProductsTableModel.setRowCount(0);
        
        for (NhaCungCapSanPhamDTO product : supplierProducts) {
            if (searchText.isEmpty() || 
                product.getTenNL().toLowerCase().contains(searchText) ||
                String.valueOf(product.getMaNL()).contains(searchText)) {
                
                Object[] row = {
                    product.getMaNL(),
                    product.getTenNL(),
                    product.getDonVi(),
                    product.getSoLuong(),
                    String.format("%,d", product.getDonGia()) + " VNĐ"
                };
                supplierProductsTableModel.addRow(row);
            }
        }
    }
    
    private void onSupplierChanged() {
        // Kiểm tra nếu đã có sản phẩm trong phiếu nhập
        if (!selectedProducts.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Không thể thay đổi nhà cung cấp khi đã có sản phẩm trong phiếu nhập!\n" +
                "Vui lòng xóa hết sản phẩm trước khi chọn nhà cung cấp khác.", 
                "Thông báo", JOptionPane.WARNING_MESSAGE);
            
            // Khôi phục lại nhà cung cấp cũ
            for (int i = 0; i < suppliers.size(); i++) {
                if (suppliers.get(i).getMaNCC() == currentSupplierId) {
                    supplierCombo.setSelectedIndex(i);
                    break;
                }
            }
            return;
        }
        
        int selectedIndex = supplierCombo.getSelectedIndex();
        if (selectedIndex >= 0 && selectedIndex < suppliers.size()) {
            currentSupplierId = suppliers.get(selectedIndex).getMaNCC();
            loadSupplierProducts();
        }
    }
    
    private void addProductToReceipt() {
        int selectedRow = supplierProductsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm cần thêm!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String quantityStr = quantityField.getText().trim();
        if (quantityStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số lượng!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int quantity;
        try {
            quantity = Integer.parseInt(quantityStr);
            if (quantity <= 0) {
                JOptionPane.showMessageDialog(this, "Số lượng phải lớn hơn 0!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Số lượng phải là số nguyên!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Get the actual product from the filtered list
        int modelRow = supplierProductsTable.convertRowIndexToModel(selectedRow);
        NhaCungCapSanPhamDTO selectedProduct = null;
        int currentIndex = 0;
        
        for (NhaCungCapSanPhamDTO product : supplierProducts) {
            if (currentIndex == modelRow) {
                selectedProduct = product;
                break;
            }
            currentIndex++;
        }
        
        if (selectedProduct == null) return;
        
        // Check if product already exists in selected list
        for (SanPhamChonNhapDTO existing : selectedProducts) {
            if (existing.getMaNL() == selectedProduct.getMaNL()) {
                JOptionPane.showMessageDialog(this, "Sản phẩm này đã có trong phiếu nhập!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        
        // Check available quantity
        if (quantity > selectedProduct.getSoLuong()) {
            JOptionPane.showMessageDialog(this, "Số lượng yêu cầu vượt quá số lượng có sẵn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Add to selected products
        SanPhamChonNhapDTO newProduct = new SanPhamChonNhapDTO(
            selectedProducts.size() + 1,
            selectedProduct.getMaNL(),
            selectedProduct.getTenNL(),
            selectedProduct.getDonVi(),
            quantity,
            selectedProduct.getDonGia()
        );
        selectedProducts.add(newProduct);
        
        updateSelectedProductsTable();
        updateTotalAmount();
        quantityField.setText("1");
        
        // Vô hiệu hóa combo box nhà cung cấp khi có sản phẩm
        if (selectedProducts.size() == 1) {
            supplierCombo.setEnabled(false);
        }
    }
    
    private void updateSelectedProductsTable() {
        selectedProductsTableModel.setRowCount(0);
        
        for (int i = 0; i < selectedProducts.size(); i++) {
            SanPhamChonNhapDTO product = selectedProducts.get(i);
            product.setStt(i + 1);
            
            Object[] row = {
                product.getStt(),
                product.getMaNL(),
                product.getTenNL(),
                product.getDonVi(),
                product.getSoLuong(),
                String.format("%,d", product.getDonGia()) + " VNĐ"
            };
            selectedProductsTableModel.addRow(row);
        }
    }
    
    private void updateTotalAmount() {
        long total = 0;
        for (SanPhamChonNhapDTO product : selectedProducts) {
            total += product.getThanhTien();
        }
        totalAmountLabel.setText("Tổng tiền: " + String.format("%,d", total) + " VNĐ");
    }
    
    private void editQuantity() {
        int selectedRow = selectedProductsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm cần sửa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        SanPhamChonNhapDTO product = selectedProducts.get(selectedRow);
        
        String newQuantityStr = JOptionPane.showInputDialog(this, 
            "Nhập số lượng mới cho " + product.getTenNL() + ":", 
            product.getSoLuong());
        
        if (newQuantityStr == null || newQuantityStr.trim().isEmpty()) {
            return;
        }
        
        try {
            int newQuantity = Integer.parseInt(newQuantityStr.trim());
            if (newQuantity <= 0) {
                JOptionPane.showMessageDialog(this, "Số lượng phải lớn hơn 0!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Check available quantity from supplier
            for (NhaCungCapSanPhamDTO supplierProduct : supplierProducts) {
                if (supplierProduct.getMaNL() == product.getMaNL()) {
                    if (newQuantity > supplierProduct.getSoLuong()) {
                        JOptionPane.showMessageDialog(this, "Số lượng yêu cầu vượt quá số lượng có sẵn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    break;
                }
            }
            
            product.setSoLuong(newQuantity);
            updateSelectedProductsTable();
            updateTotalAmount();
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Số lượng phải là số nguyên!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteProduct() {
        int selectedRow = selectedProductsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm cần xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        SanPhamChonNhapDTO product = selectedProducts.get(selectedRow);
        
        int result = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc chắn muốn xóa sản phẩm \"" + product.getTenNL() + "\"?", 
            "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            selectedProducts.remove(selectedRow);
            updateSelectedProductsTable();
            updateTotalAmount();
            
            // Nếu xóa hết sản phẩm, cho phép thay đổi nhà cung cấp
            if (selectedProducts.isEmpty()) {
                supplierCombo.setEnabled(true);
            }
        }
    }
    
    private void generateNewReceiptCode() {
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT MAX(MaPN) FROM phieunhap")) {
            
            if (rs.next()) {
                currentReceiptCode = rs.getInt(1) + 1;
            } else {
                currentReceiptCode = 1;
            }
            
            receiptCodeField.setText("PN" + currentReceiptCode);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi tạo mã phiếu nhập: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            currentReceiptCode = 1;
            receiptCodeField.setText("PN" + currentReceiptCode);
        }
    }
    
    private void importGoods() {
        if (selectedProducts.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn ít nhất một sản phẩm!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (supplierCombo.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhà cung cấp!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String message, title;
        if (editModeMaPN > 0) {
            message = "Bạn có chắc chắn muốn cập nhật phiếu nhập #" + editModeMaPN + " với " + selectedProducts.size() + " sản phẩm?";
            title = "Xác nhận cập nhật";
        } else {
            message = "Bạn có chắc chắn muốn tạo phiếu nhập với " + selectedProducts.size() + " sản phẩm?";
            title = "Xác nhận nhập hàng";
        }
        
        int result = JOptionPane.showConfirmDialog(this, message, title, JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            try {
                if (editModeMaPN > 0) {
                    updateImportReceipt();
                    JOptionPane.showMessageDialog(this, "Cập nhật phiếu nhập thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    // Làm mới danh sách sản phẩm nhà cung cấp để hiển thị số tồn kho mới
                    loadSupplierProducts();
                } else {
                    createImportReceipt();
                    JOptionPane.showMessageDialog(this, "Tạo phiếu nhập thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    // Làm mới danh sách sản phẩm nhà cung cấp để hiển thị số tồn kho mới
                    loadSupplierProducts();
                    resetForm();
                }
            } catch (Exception e) {
                String errorMsg = editModeMaPN > 0 ? "Lỗi cập nhật phiếu nhập: " : "Lỗi tạo phiếu nhập: ";
                JOptionPane.showMessageDialog(this, errorMsg + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void updateImportReceipt() throws SQLException {
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            
            // 1. Lấy chi tiết cũ để tính toán chênh lệch
            List<SanPhamChonNhapDTO> oldProducts = getOldReceiptDetails(conn);
            
            // 2. Cập nhật số tồn kho nhà cung cấp
            updateSupplierStock(conn, oldProducts, selectedProducts);
            
            // 3. Xóa chi tiết cũ
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM chitietnhap_nl WHERE MaPN = ?")) {
                ps.setInt(1, editModeMaPN);
                ps.executeUpdate();
            }
            
            // 4. Thêm chi tiết mới
            String insertDetail = "INSERT INTO chitietnhap_nl (MaPN, MaNL, SoLuong, DonGia) VALUES (?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(insertDetail)) {
                for (SanPhamChonNhapDTO product : selectedProducts) {
                    ps.setInt(1, editModeMaPN);
                    ps.setInt(2, product.getMaNL());
                    ps.setInt(3, product.getSoLuong());
                    ps.setLong(4, product.getDonGia());
                    ps.executeUpdate();
                }
            }
            
            // 5. Cập nhật tổng tiền
            long totalAmount = selectedProducts.stream().mapToLong(SanPhamChonNhapDTO::getThanhTien).sum();
            try (PreparedStatement ps = conn.prepareStatement("UPDATE phieunhap SET ThanhTien = ? WHERE MaPN = ?")) {
                ps.setLong(1, totalAmount);
                ps.setInt(2, editModeMaPN);
                ps.executeUpdate();
            }
            
            conn.commit();
        }
    }
    
    private List<SanPhamChonNhapDTO> getOldReceiptDetails(Connection conn) throws SQLException {
        List<SanPhamChonNhapDTO> oldProducts = new ArrayList<>();
        
        String sql = "SELECT ctnh.*, nl.TenNL " +
                    "FROM chitietnhap_nl ctnh " +
                    "JOIN nguyenlieu nl ON ctnh.MaNL = nl.MaNL " +
                    "WHERE ctnh.MaPN = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, editModeMaPN);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                SanPhamChonNhapDTO product = new SanPhamChonNhapDTO();
                product.setMaNL(rs.getInt("MaNL"));
                product.setTenNL(rs.getString("TenNL"));
                product.setSoLuong(rs.getInt("SoLuong"));
                product.setDonGia(rs.getLong("DonGia"));
                product.setDonVi(rs.getString("DonVi"));
                oldProducts.add(product);
            }
        }
        
        return oldProducts;
    }
    
    private void updateSupplierStock(Connection conn, List<SanPhamChonNhapDTO> oldProducts, List<SanPhamChonNhapDTO> newProducts) throws SQLException {
        // Tạo map để dễ tìm kiếm
        Map<Integer, Integer> oldQuantities = new HashMap<>();
        Map<Integer, Integer> newQuantities = new HashMap<>();
        
        // Lưu số lượng cũ
        for (SanPhamChonNhapDTO product : oldProducts) {
            oldQuantities.put(product.getMaNL(), product.getSoLuong());
        }
        
        // Lưu số lượng mới
        for (SanPhamChonNhapDTO product : newProducts) {
            newQuantities.put(product.getMaNL(), product.getSoLuong());
        }
        
        // Tính toán và cập nhật chênh lệch
        Set<Integer> allProductIds = new HashSet<>();
        allProductIds.addAll(oldQuantities.keySet());
        allProductIds.addAll(newQuantities.keySet());
        
        for (Integer maNL : allProductIds) {
            int oldQty = oldQuantities.getOrDefault(maNL, 0);
            int newQty = newQuantities.getOrDefault(maNL, 0);
            int difference = newQty - oldQty;
            
            if (difference != 0) {
                // Cập nhật số tồn kho nhà cung cấp
                String updateStock = "UPDATE ncc_nguyenlieu SET SoLuong = SoLuong - ? WHERE MaNCC = ? AND MaNL = ?";
                try (PreparedStatement ps = conn.prepareStatement(updateStock)) {
                    ps.setInt(1, difference);
                    ps.setInt(2, currentSupplierId);
                    ps.setInt(3, maNL);
                    ps.executeUpdate();
                }
            }
        }
    }
    
    private void createImportReceipt() throws SQLException {
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            
            // 1. Create import receipt
            String insertReceipt = "INSERT INTO phieunhap (MaNV, MaNCC, Ngay, ThanhTien, TrangThai) VALUES (?, ?, NOW(), ?, 'chuaxacnhan')";
            
            try (PreparedStatement ps = conn.prepareStatement(insertReceipt, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, Session.currentMaNV);
                ps.setInt(2, currentSupplierId);
                
                long totalAmount = 0;
                for (SanPhamChonNhapDTO product : selectedProducts) {
                    totalAmount += product.getThanhTien();
                }
                ps.setLong(3, totalAmount);
                
                ps.executeUpdate();
                
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        int receiptId = rs.getInt(1);
                        
                        // 2. Create receipt details
                        String insertDetail = "INSERT INTO chitietnhap_nl (MaPN, MaNL, SoLuong, DonGia) VALUES (?, ?, ?, ?)";
                        
                        try (PreparedStatement psDetail = conn.prepareStatement(insertDetail)) {
                            for (SanPhamChonNhapDTO product : selectedProducts) {
                                psDetail.setInt(1, receiptId);
                                psDetail.setInt(2, product.getMaNL());
                                psDetail.setInt(3, product.getSoLuong());
                                psDetail.setLong(4, product.getDonGia());
                                
                                psDetail.executeUpdate();
                                
                                // 3. Update supplier inventory
                                String updateSupplier = "UPDATE ncc_nguyenlieu SET SoLuong = SoLuong - ? WHERE MaNCC = ? AND MaNL = ?";
                                try (PreparedStatement psSupplier = conn.prepareStatement(updateSupplier)) {
                                    psSupplier.setInt(1, product.getSoLuong());
                                    psSupplier.setInt(2, currentSupplierId);
                                    psSupplier.setInt(3, product.getMaNL());
                                    psSupplier.executeUpdate();
                                }
                                
                                // Lưu ý: Kho hàng chỉ được cập nhật khi phiếu nhập được xác nhận
                                // (xem method xacNhanPhieuNhap trong NhapHangDAO)
                            }
                        }
                    }
                }
            }
            
            conn.commit();
        }
    }
    
    private void resetForm() {
        selectedProducts.clear();
        updateSelectedProductsTable();
        updateTotalAmount();
        generateNewReceiptCode();
        loadSupplierProducts();
        quantityField.setText("1");
        
        // Kích hoạt lại combo box nhà cung cấp
        supplierCombo.setEnabled(true);
    }
}
