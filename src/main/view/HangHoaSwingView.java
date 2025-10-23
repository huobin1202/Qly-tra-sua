package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import database.DBUtil;
import dto.MonDTO;
import dto.NguyenLieuDTO;
import dto.LoaiMonDTO;

public class HangHoaSwingView extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> searchCombo, categoryCombo;
    private MainFrameInterface parent;
    private String currentView = "MON"; // MON, LOAIMON, or NGUYENLIEU
    
    public void setCurrentView(String view) {
        this.currentView = view;
        updateTableHeaders();
        loadData();
    }
    
    public String getCurrentView() {
        return currentView;
    }
    
    public HangHoaSwingView(MainFrameInterface parent) {
        this.parent = parent;
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadData();
    }
    
    private void initializeComponents() {
        // Tạo table model
        String[] columns = {"ID", "Tên", "Mô tả", "Đơn vị", "Giá", "Trạng thái", "Loại"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        
        // Tạo search components
        searchCombo = new JComboBox<>(new String[]{"Tất cả", "ID", "Tên", "Trạng thái"});
        searchField = new JTextField(20);
        categoryCombo = new JComboBox<>(new String[]{"Món", "Loại món", "Nguyên liệu"});
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 248, 255));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        
        
        // Back button
     
        
        // Control panel - ẩn dropdown và nút chuyển đổi vì đã có menu riêng biệt
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.setBackground(new Color(240, 248, 255));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Không hiển thị dropdown và nút chuyển đổi nữa
        // controlPanel.add(new JLabel("Loại:"));
        // controlPanel.add(categoryCombo);
        // JButton switchButton = new JButton("🔄 Chuyển đổi");
        // switchButton.setBackground(new Color(70, 130, 180));
        // switchButton.setForeground(Color.BLACK);
        // switchButton.setFocusPainted(false);
        // controlPanel.add(switchButton);
        
        // Top panel - chứa search và buttons trong cùng một hàng
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(240, 248, 255));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Button panel (bên trái) - Thêm/Sửa/Xóa
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(new Color(240, 248, 255));
        
        JButton addButton = new JButton("➕ Thêm mới");
        addButton.setBackground(new Color(34, 139, 34));
        addButton.setForeground(Color.BLACK);
        addButton.setFocusPainted(false);
        
        JButton editButton = new JButton("✏️ Sửa");
        editButton.setBackground(new Color(255, 140, 0));
        editButton.setForeground(Color.BLACK);
        editButton.setFocusPainted(false);
        
        JButton deleteButton = new JButton("🗑️ Xóa");
        deleteButton.setBackground(new Color(220, 20, 60));
        deleteButton.setForeground(Color.BLACK);
        deleteButton.setFocusPainted(false);
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        
        // Search panel (bên phải) - Tìm kiếm
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setBackground(new Color(240, 248, 255));
        
        searchPanel.add(new JLabel("Tìm kiếm:"));
        searchPanel.add(searchCombo);
        searchPanel.add(searchField);
        
        JButton searchButton = new JButton("🔍 Tìm");
        searchButton.setBackground(new Color(70, 130, 180));
        searchButton.setForeground(Color.BLACK);
        searchButton.setFocusPainted(false);
        searchPanel.add(searchButton);
        
        JButton refreshButton = new JButton("🔄 Làm mới");
        refreshButton.setBackground(new Color(34, 139, 34));
        refreshButton.setForeground(Color.BLACK);
        refreshButton.setFocusPainted(false);
        searchPanel.add(refreshButton);
        
        // Thêm button panel và search panel vào top panel
        topPanel.add(buttonPanel, BorderLayout.WEST);
        topPanel.add(searchPanel, BorderLayout.EAST);
        
        // Table panel
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Danh sách hàng hóa"));
        
        // Layout
        JPanel northContainer = new JPanel();
        northContainer.setLayout(new BoxLayout(northContainer, BoxLayout.Y_AXIS));
        northContainer.add(headerPanel);
        northContainer.add(controlPanel);
        northContainer.add(topPanel);
        add(northContainer, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        
        // Event handlers
        searchButton.addActionListener(e -> performSearch());
        refreshButton.addActionListener(e -> loadData());
        addButton.addActionListener(e -> showAddDialog());
        editButton.addActionListener(e -> showEditDialog());
        deleteButton.addActionListener(e -> performDelete());
    }
    
    private void setupEventHandlers() {
        // Double click to edit
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    showEditDialog();
                }
            }
        });
        
        // Enter key in search field
        searchField.addActionListener(e -> performSearch());
    }
    
    private void switchView() {
        String selected = (String) categoryCombo.getSelectedItem();
        if (selected.equals("Món")) {
            currentView = "MON";
        } else if (selected.equals("Loại món")) {
            currentView = "LOAIMON";
        } else {
            currentView = "NGUYENLIEU";
        }
        updateTableHeaders();
        loadData();
    }
    
    private void updateTableHeaders() {
        if (currentView.equals("MON")) {
            tableModel.setColumnIdentifiers(new String[]{"ID", "Tên món", "Mô tả", "Giá", "Trạng thái", "Loại"});
        } else if (currentView.equals("LOAIMON")) {
            tableModel.setColumnIdentifiers(new String[]{"ID", "Tên loại", "Slug"});
        } else {
            tableModel.setColumnIdentifiers(new String[]{"ID", "Tên nguyên liệu", "Đơn vị"});
        }
    }
    
    private void loadData() {
        tableModel.setRowCount(0);
        try (Connection conn = DBUtil.getConnection()) {
            if (currentView.equals("MON")) {
                loadMonData(conn);
            } else if (currentView.equals("LOAIMON")) {
                loadLoaiMonData(conn);
            } else {
                loadNguyenLieuData(conn);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadMonData(Connection conn) throws SQLException {
        String sql = "SELECT m.MaMon, m.TenMon, m.MoTa, m.Gia, m.TinhTrang, l.TenLoai FROM mon m LEFT JOIN loaimon l ON m.MaLoai = l.MaLoai ORDER BY m.MaMon";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("MaMon"),
                    rs.getString("TenMon"),
                    rs.getString("MoTa"),
                    String.format("%,d", rs.getLong("Gia")) + " VNĐ",
                    rs.getString("TinhTrang"),
                    rs.getString("TenLoai")
                };
                tableModel.addRow(row);
            }
        }
    }
    
    private void loadLoaiMonData(Connection conn) throws SQLException {
        String sql = "SELECT MaLoai, TenLoai, Slug FROM loaimon ORDER BY MaLoai";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("MaLoai"),
                    rs.getString("TenLoai"),
                    rs.getString("Slug")
                };
                tableModel.addRow(row);
            }
        }
    }
    
    private void loadNguyenLieuData(Connection conn) throws SQLException {
        String sql = "SELECT * FROM nguyenlieu ORDER BY MaNL";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("MaNL"),
                    rs.getString("TenNL"),
                    rs.getString("DonVi")
                };
                tableModel.addRow(row);
            }
        }
    }
    
    private void performSearch() {
        String searchText = searchField.getText().trim();
        String searchType = (String) searchCombo.getSelectedItem();
        
        tableModel.setRowCount(0);
        try (Connection conn = DBUtil.getConnection()) {
            if (currentView.equals("MON")) {
                searchMonData(conn, searchText, searchType);
            } else {
                searchNguyenLieuData(conn, searchText, searchType);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi tìm kiếm: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "ID phải là số!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void searchMonData(Connection conn, String searchText, String searchType) throws SQLException {
        String sql = "SELECT m.MaMon, m.TenMon, m.MoTa, m.Gia, m.TinhTrang, l.TenLoai FROM mon m LEFT JOIN loaimon l ON m.MaLoai = l.MaLoai WHERE ";
        PreparedStatement ps;
        
        if (searchType.equals("Tất cả") || searchText.isEmpty()) {
            sql = "SELECT m.MaMon, m.TenMon, m.MoTa, m.Gia, m.TinhTrang, l.TenLoai FROM mon m LEFT JOIN loaimon l ON m.MaLoai = l.MaLoai ORDER BY m.MaMon";
            ps = conn.prepareStatement(sql);
        } else if (searchType.equals("ID")) {
            sql += "m.MaMon = ? ORDER BY m.MaMon";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, Integer.parseInt(searchText));
        } else if (searchType.equals("Tên")) {
            sql += "m.TenMon LIKE ? ORDER BY m.MaMon";
            ps = conn.prepareStatement(sql);
            ps.setString(1, "%" + searchText + "%");
        } else {
            sql += "m.TinhTrang LIKE ? ORDER BY m.MaMon";
            ps = conn.prepareStatement(sql);
            ps.setString(1, "%" + searchText + "%");
        }
        
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Object[] row = {
                rs.getInt("MaMon"),
                rs.getString("TenMon"),
                rs.getString("MoTa"),
                String.format("%,d", rs.getLong("Gia")) + " VNĐ",
                rs.getString("TinhTrang"),
                rs.getString("TenLoai")
            };
            tableModel.addRow(row);
        }
    }
    
    private void searchNguyenLieuData(Connection conn, String searchText, String searchType) throws SQLException {
        String sql = "SELECT * FROM nguyenlieu WHERE ";
        PreparedStatement ps;
        
        if (searchType.equals("Tất cả") || searchText.isEmpty()) {
            sql = "SELECT * FROM nguyenlieu ORDER BY MaNL";
            ps = conn.prepareStatement(sql);
        } else if (searchType.equals("ID")) {
            sql += "MaNL = ? ORDER BY MaNL";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, Integer.parseInt(searchText));
        } else {
            sql += "TenNL LIKE ? ORDER BY MaNL";
            ps = conn.prepareStatement(sql);
            ps.setString(1, "%" + searchText + "%");
        }
        
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Object[] row = {
                rs.getInt("MaNL"),
                rs.getString("TenNL"),
                rs.getString("DonVi"),
                "", "", "", ""
            };
            tableModel.addRow(row);
        }
    }
    
    private void showAddDialog() {
        if (currentView.equals("MON")) {
            MonDialog dialog = new MonDialog(SwingUtilities.getWindowAncestor(this), "Thêm món mới", null);
            dialog.setVisible(true);
            if (dialog.isDataChanged()) {
                loadData();
            }
        } else if (currentView.equals("LOAIMON")) {
            LoaiMonDialog dialog = new LoaiMonDialog(SwingUtilities.getWindowAncestor(this), "Thêm loại món mới", null);
            dialog.setVisible(true);
            if (dialog.isDataChanged()) {
                loadData();
            }
        } else {
            NguyenLieuDialog dialog = new NguyenLieuDialog(SwingUtilities.getWindowAncestor(this), "Thêm nguyên liệu mới", null);
            dialog.setVisible(true);
            if (dialog.isDataChanged()) {
                loadData();
            }
        }
    }
    
    private void showEditDialog() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn mục cần sửa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (currentView.equals("MON")) {
            int id = (Integer) tableModel.getValueAt(selectedRow, 0);
            String ten = (String) tableModel.getValueAt(selectedRow, 1);
            String moTa = (String) tableModel.getValueAt(selectedRow, 2);
            String giaStr = (String) tableModel.getValueAt(selectedRow, 3);
            String tinhTrang = (String) tableModel.getValueAt(selectedRow, 4);
            String loai = (String) tableModel.getValueAt(selectedRow, 5);
            
            long gia = 0;
            if (!giaStr.isEmpty()) {
                try {
                    gia = Long.parseLong(giaStr.replaceAll("[^0-9]", ""));
                } catch (Exception e) {
                    // Ignore parsing error
                }
            }
            
            MonDTO mon = new MonDTO(id, ten, moTa, gia, tinhTrang, 0);
            MonDialog dialog = new MonDialog(SwingUtilities.getWindowAncestor(this), "Sửa thông tin món", mon);
            dialog.setVisible(true);
            if (dialog.isDataChanged()) {
                loadData();
            }
        } else if (currentView.equals("LOAIMON")) {
            int id = (Integer) tableModel.getValueAt(selectedRow, 0);
            String ten = (String) tableModel.getValueAt(selectedRow, 1);
            String slug = (String) tableModel.getValueAt(selectedRow, 2);
            
            LoaiMonDTO loaiMon = new LoaiMonDTO(id, ten, slug);
            LoaiMonDialog dialog = new LoaiMonDialog(SwingUtilities.getWindowAncestor(this), "Sửa thông tin loại món", loaiMon);
            dialog.setVisible(true);
            if (dialog.isDataChanged()) {
                loadData();
            }
        } else {
            int id = (Integer) tableModel.getValueAt(selectedRow, 0);
            String ten = (String) tableModel.getValueAt(selectedRow, 1);
            String donVi = (String) tableModel.getValueAt(selectedRow, 2);
            
            NguyenLieuDTO nl = new NguyenLieuDTO(id, ten, donVi);
            NguyenLieuDialog dialog = new NguyenLieuDialog(SwingUtilities.getWindowAncestor(this), "Sửa thông tin nguyên liệu", nl);
            dialog.setVisible(true);
            if (dialog.isDataChanged()) {
                loadData();
            }
        }
    }
    
    private void performDelete() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn mục cần xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String name = (String) tableModel.getValueAt(selectedRow, 1);
        String type;
        if (currentView.equals("MON")) {
            type = "món";
        } else if (currentView.equals("LOAIMON")) {
            type = "loại món";
        } else {
            type = "nguyên liệu";
        }
        
        int result = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc chắn muốn xóa " + type + " '" + name + "'?", 
            "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            try (Connection conn = DBUtil.getConnection()) {
                int id = (Integer) tableModel.getValueAt(selectedRow, 0);
                String tableName;
                String idColumn;
                
                if (currentView.equals("MON")) {
                    tableName = "mon";
                    idColumn = "MaMon";
                } else if (currentView.equals("LOAIMON")) {
                    tableName = "loaimon";
                    idColumn = "MaLoai";
                } else {
                    tableName = "nguyenlieu";
                    idColumn = "MaNL";
                }
                
                PreparedStatement ps = conn.prepareStatement("DELETE FROM " + tableName + " WHERE " + idColumn + "=?");
                ps.setInt(1, id);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Xóa thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Lỗi xóa dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // Inner class for Mon Add/Edit dialog
    private class MonDialog extends JDialog {
        private JTextField tenField, moTaField, donViField, giaField;
        private JComboBox<String> tinhTrangCombo, loaiCombo;
        private boolean dataChanged = false;
        private MonDTO mon;
        
        public MonDialog(Window parent, String title, MonDTO mon) {
            super(parent, title, ModalityType.APPLICATION_MODAL);
            this.mon = mon;
            initializeComponents();
            setupLayout();
            setupEventHandlers();
        }
        
        private void initializeComponents() {
            setSize(450, 400);
            setLocationRelativeTo(getParent());
            
            tenField = new JTextField(20);
            moTaField = new JTextField(20);
            donViField = new JTextField(20);
            giaField = new JTextField(20);
            tinhTrangCombo = new JComboBox<>(new String[]{"Còn hàng", "Hết hàng", "Tạm ngừng"});
            loaiCombo = new JComboBox<>();
            
            // Load loại món
            loadLoaiMon();
            
            if (mon != null) {
                tenField.setText(mon.getTenMon());
                moTaField.setText(mon.getMoTa());
                // donViField.setText(mon.getTenDonVi()); // Method not available
                giaField.setText(String.valueOf(mon.getGia()));
                tinhTrangCombo.setSelectedItem(mon.getTinhTrang());
                
                // Set loại món safely
                try {
                    if (mon.getMaLoai() > 0 && mon.getMaLoai() <= loaiCombo.getItemCount()) {
                        loaiCombo.setSelectedIndex(mon.getMaLoai() - 1);
                    }
                } catch (Exception e) {
                    // Ignore error, keep default selection
                }
            }
        }
        
        private void loadLoaiMon() {
            try (Connection conn = DBUtil.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM loaimon ORDER BY MaLoai")) {
                
                while (rs.next()) {
                    loaiCombo.addItem(rs.getString("TenLoai"));
                }
            } catch (SQLException e) {
                // Ignore error
            }
        }
        
        private void setupLayout() {
            setLayout(new BorderLayout());
            
            JPanel mainPanel = new JPanel(new GridBagLayout());
            mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            
            // Tên món
            gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST;
            mainPanel.add(new JLabel("Tên món:"), gbc);
            gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
            mainPanel.add(tenField, gbc);
            
            // Mô tả
            gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.EAST;
            mainPanel.add(new JLabel("Mô tả:"), gbc);
            gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
            mainPanel.add(moTaField, gbc);
            
            // Đơn vị
            gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.EAST;
            mainPanel.add(new JLabel("Đơn vị:"), gbc);
            gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
            mainPanel.add(donViField, gbc);
            
            // Giá
            gbc.gridx = 0; gbc.gridy = 3; gbc.anchor = GridBagConstraints.EAST;
            mainPanel.add(new JLabel("Giá (VNĐ):"), gbc);
            gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
            mainPanel.add(giaField, gbc);
            
            // Trạng thái
            gbc.gridx = 0; gbc.gridy = 4; gbc.anchor = GridBagConstraints.EAST;
            mainPanel.add(new JLabel("Trạng thái:"), gbc);
            gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
            mainPanel.add(tinhTrangCombo, gbc);
            
            // Loại
            gbc.gridx = 0; gbc.gridy = 5; gbc.anchor = GridBagConstraints.EAST;
            mainPanel.add(new JLabel("Loại:"), gbc);
            gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
            mainPanel.add(loaiCombo, gbc);
            
            // Buttons
            gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
            JPanel buttonPanel = new JPanel(new FlowLayout());
            
            JButton saveButton = new JButton("Lưu");
            saveButton.setBackground(new Color(34, 139, 34));
            saveButton.setForeground(Color.BLACK);
            saveButton.setFocusPainted(false);
            saveButton.addActionListener(e -> saveData());
            
            JButton cancelButton = new JButton("Hủy");
            cancelButton.setBackground(new Color(220, 220, 220));
            cancelButton.setFocusPainted(false);
            cancelButton.addActionListener(e -> dispose());
            
            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);
            mainPanel.add(buttonPanel, gbc);
            
            add(mainPanel, BorderLayout.CENTER);
        }
        
        private void setupEventHandlers() {
            // Event handlers are already set in setupLayout()
        }
        
        private JButton findButton(String text) {
            for (Component comp : getComponents()) {
                if (comp instanceof JPanel) {
                    JButton button = findButtonInPanel((JPanel) comp, text);
                    if (button != null) return button;
                }
            }
            return null;
        }
        
        private JButton findButtonInPanel(JPanel panel, String text) {
            for (Component comp : panel.getComponents()) {
                if (comp instanceof JButton) {
                    JButton button = (JButton) comp;
                    if (button.getText().equals(text)) {
                        return button;
                    }
                } else if (comp instanceof JPanel) {
                    JButton button = findButtonInPanel((JPanel) comp, text);
                    if (button != null) return button;
                }
            }
            return null;
        }
        
        private void saveData() {
            String ten = tenField.getText().trim();
            String moTa = moTaField.getText().trim();
            String donVi = donViField.getText().trim();
            String giaStr = giaField.getText().trim();
            String tinhTrang = (String) tinhTrangCombo.getSelectedItem();
            String loai = (String) loaiCombo.getSelectedItem();
            
            if (ten.isEmpty() || donVi.isEmpty() || giaStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin bắt buộc!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            long gia;
            try {
                gia = Long.parseLong(giaStr);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Giá phải là số!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try (Connection conn = DBUtil.getConnection()) {
                // Get MaLoai from TenLoai
                int maLoai = 1;
                try (PreparedStatement ps = conn.prepareStatement("SELECT MaLoai FROM loaimon WHERE TenLoai = ?")) {
                    ps.setString(1, loai);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        maLoai = rs.getInt("MaLoai");
                    } else {
                        JOptionPane.showMessageDialog(this, "Không tìm thấy loại món: " + loai, "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
                
                if (mon == null) {
                    // Thêm mới
                    PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO mon (TenMon, MoTa, Gia, TinhTrang, MaLoai) VALUES (?, ?, ?, ?, ?)");
                    ps.setString(1, ten);
                    ps.setString(2, moTa);
                    ps.setLong(3, gia);
                    ps.setString(4, tinhTrang);
                    ps.setInt(5, maLoai);
                    ps.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Thêm thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    // Sửa
                    PreparedStatement ps = conn.prepareStatement(
                        "UPDATE mon SET TenMon=?, MoTa=?, Gia=?, TinhTrang=?, MaLoai=? WHERE MaMon=?");
                    ps.setString(1, ten);
                    ps.setString(2, moTa);
                    ps.setLong(3, gia);
                    ps.setString(4, tinhTrang);
                    ps.setInt(5, maLoai);
                    ps.setInt(6, mon.getMaMon());
                    ps.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Sửa thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                }
                dataChanged = true;
                dispose();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Lỗi lưu dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
        
        public boolean isDataChanged() {
            return dataChanged;
        }
    }
    
    // Inner class for NguyenLieu Add/Edit dialog
    private class LoaiMonDialog extends JDialog {
        private JTextField tenField, slugField;
        private boolean dataChanged = false;
        private LoaiMonDTO loaiMon;
        
        public LoaiMonDialog(Window parent, String title, LoaiMonDTO loaiMon) {
            super(parent, title, ModalityType.APPLICATION_MODAL);
            this.loaiMon = loaiMon;
            initializeComponents();
            setupLayout();
            setupEventHandlers();
        }
        
        private void initializeComponents() {
            setSize(400, 200);
            setLocationRelativeTo(getParent());
            
            tenField = new JTextField(20);
            slugField = new JTextField(20);
            
            if (loaiMon != null) {
                tenField.setText(loaiMon.getTenLoai());
                slugField.setText(loaiMon.getSlug());
            }
        }
        
        private void setupLayout() {
            setLayout(new BorderLayout());
            
            JPanel formPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            
            gbc.gridx = 0; gbc.gridy = 0;
            formPanel.add(new JLabel("Tên loại:"), gbc);
            gbc.gridx = 1;
            formPanel.add(tenField, gbc);
            
            gbc.gridx = 0; gbc.gridy = 1;
            formPanel.add(new JLabel("Slug:"), gbc);
            gbc.gridx = 1;
            formPanel.add(slugField, gbc);
            
            JPanel buttonPanel = new JPanel(new FlowLayout());
            JButton saveButton = new JButton("Lưu");
            saveButton.setBackground(new Color(34, 139, 34));
            saveButton.setForeground(Color.BLACK);
            saveButton.setFocusPainted(false);
            saveButton.addActionListener(e -> saveData());
            
            JButton cancelButton = new JButton("Hủy");
            cancelButton.setBackground(new Color(220, 20, 60));
            cancelButton.setForeground(Color.BLACK);
            cancelButton.setFocusPainted(false);
            cancelButton.addActionListener(e -> dispose());
            
            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);
            
            add(formPanel, BorderLayout.CENTER);
            add(buttonPanel, BorderLayout.SOUTH);
        }
        
        private void setupEventHandlers() {
            // Enter key in text fields
            tenField.addActionListener(e -> saveData());
            slugField.addActionListener(e -> saveData());
        }
        
        private JButton findButton(String text) {
            for (Component comp : getComponents()) {
                if (comp instanceof JPanel) {
                    JButton button = findButtonInPanel((JPanel) comp, text);
                    if (button != null) return button;
                }
            }
            return null;
        }
        
        private JButton findButtonInPanel(JPanel panel, String text) {
            for (Component comp : panel.getComponents()) {
                if (comp instanceof JButton) {
                    JButton button = (JButton) comp;
                    if (button.getText().equals(text)) {
                        return button;
                    }
                } else if (comp instanceof JPanel) {
                    JButton button = findButtonInPanel((JPanel) comp, text);
                    if (button != null) return button;
                }
            }
            return null;
        }
        
        private void saveData() {
            String ten = tenField.getText().trim();
            String slug = slugField.getText().trim();
            
            if (ten.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập tên loại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (slug.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập slug!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try (Connection conn = DBUtil.getConnection()) {
                if (loaiMon == null) {
                    // Thêm mới
                    PreparedStatement ps = conn.prepareStatement("INSERT INTO loaimon (TenLoai, Slug) VALUES (?, ?)");
                    ps.setString(1, ten);
                    ps.setString(2, slug);
                    ps.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Thêm loại món thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    // Cập nhật
                    PreparedStatement ps = conn.prepareStatement("UPDATE loaimon SET TenLoai=?, Slug=? WHERE MaLoai=?");
                    ps.setString(1, ten);
                    ps.setString(2, slug);
                    ps.setInt(3, loaiMon.getMaLoai());
                    ps.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Cập nhật loại món thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                }
                dataChanged = true;
                dispose();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
        
        public boolean isDataChanged() {
            return dataChanged;
        }
    }

    private class NguyenLieuDialog extends JDialog {
        private JTextField tenField, donViField;
        private boolean dataChanged = false;
        private NguyenLieuDTO nl;
        
        public NguyenLieuDialog(Window parent, String title, NguyenLieuDTO nl) {
            super(parent, title, ModalityType.APPLICATION_MODAL);
            this.nl = nl;
            initializeComponents();
            setupLayout();
            setupEventHandlers();
        }
        
        private void initializeComponents() {
            setSize(350, 250);
            setLocationRelativeTo(getParent());
            
            tenField = new JTextField(20);
            donViField = new JTextField(20);
            
            if (nl != null) {
                tenField.setText(nl.getTenNL());
                donViField.setText(nl.getDonVi());
            }
        }
        
        private void setupLayout() {
            setLayout(new BorderLayout());
            
            JPanel mainPanel = new JPanel(new GridBagLayout());
            mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            
            // Tên nguyên liệu
            gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST;
            mainPanel.add(new JLabel("Tên nguyên liệu:"), gbc);
            gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
            mainPanel.add(tenField, gbc);
            
            // Đơn vị
            gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.EAST;
            mainPanel.add(new JLabel("Đơn vị:"), gbc);
            gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
            mainPanel.add(donViField, gbc);
            
            // Buttons
            gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
            JPanel buttonPanel = new JPanel(new FlowLayout());
            
            JButton saveButton = new JButton("Lưu");
            saveButton.setBackground(new Color(34, 139, 34));
            saveButton.setForeground(Color.BLACK);
            saveButton.setFocusPainted(false);
            saveButton.addActionListener(e -> saveData());
            
            JButton cancelButton = new JButton("Hủy");
            cancelButton.setBackground(new Color(220, 220, 220));
            cancelButton.setFocusPainted(false);
            cancelButton.addActionListener(e -> dispose());
            
            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);
            mainPanel.add(buttonPanel, gbc);
            
            add(mainPanel, BorderLayout.CENTER);
        }
        
        private void setupEventHandlers() {
            // Event handlers are already set in setupLayout()
        }
        
        private JButton findButton(String text) {
            for (Component comp : getComponents()) {
                if (comp instanceof JPanel) {
                    JButton button = findButtonInPanel((JPanel) comp, text);
                    if (button != null) return button;
                }
            }
            return null;
        }
        
        private JButton findButtonInPanel(JPanel panel, String text) {
            for (Component comp : panel.getComponents()) {
                if (comp instanceof JButton) {
                    JButton button = (JButton) comp;
                    if (button.getText().equals(text)) {
                        return button;
                    }
                } else if (comp instanceof JPanel) {
                    JButton button = findButtonInPanel((JPanel) comp, text);
                    if (button != null) return button;
                }
            }
            return null;
        }
        
        private void saveData() {
            String ten = tenField.getText().trim();
            String donVi = donViField.getText().trim();
            
            if (ten.isEmpty() || donVi.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try (Connection conn = DBUtil.getConnection()) {
                if (nl == null) {
                    // Thêm mới
                    PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO nguyenlieu (TenNL, DonVi) VALUES (?, ?)");
                    ps.setString(1, ten);
                    ps.setString(2, donVi);
                    ps.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Thêm thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    // Sửa
                    PreparedStatement ps = conn.prepareStatement(
                        "UPDATE nguyenlieu SET TenNL=?, DonVi=? WHERE MaNL=?");
                    ps.setString(1, ten);
                    ps.setString(2, donVi);
                    ps.setInt(3, nl.getMaNL());
                    ps.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Sửa thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                }
                dataChanged = true;
                dispose();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Lỗi lưu dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
        
        public boolean isDataChanged() {
            return dataChanged;
        }
    }
}
