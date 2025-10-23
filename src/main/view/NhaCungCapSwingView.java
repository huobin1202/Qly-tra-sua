package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import database.DBUtil;
import dto.NhaCungCapDTO;

public class NhaCungCapSwingView extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> searchCombo;
    private MainFrameInterface parent;
    
    public NhaCungCapSwingView(MainFrameInterface parent) {
        this.parent = parent;
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadData();
    }
    
    private void initializeComponents() {
        // Tạo table model
        String[] columns = {"ID", "Tên nhà cung cấp", "Số điện thoại", "Địa chỉ"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Không cho phép chỉnh sửa trực tiếp
            }
        };
        
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        
        // Tạo search components
        searchCombo = new JComboBox<>(new String[]{"Tất cả", "ID", "Tên nhà cung cấp"});
        searchField = new JTextField(20);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 248, 255));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        
        
        // Back button
    
        
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
        scrollPane.setBorder(BorderFactory.createTitledBorder("Danh sách nhà cung cấp"));
        
        // Layout
        JPanel northContainer = new JPanel();
        northContainer.setLayout(new BoxLayout(northContainer, BoxLayout.Y_AXIS));
        northContainer.add(headerPanel);
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
    
    private void loadData() {
        tableModel.setRowCount(0);
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM nhacungcap ORDER BY MaNCC")) {
            
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("MaNCC"),
                    rs.getString("TenNCC"),
                    rs.getString("SDT"),
                    rs.getString("DiaChi")
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void performSearch() {
        String searchText = searchField.getText().trim();
        String searchType = (String) searchCombo.getSelectedItem();
        
        tableModel.setRowCount(0);
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT * FROM nhacungcap WHERE ";
            PreparedStatement ps;
            
            if (searchType.equals("Tất cả") || searchText.isEmpty()) {
                sql = "SELECT * FROM nhacungcap ORDER BY MaNCC";
                ps = conn.prepareStatement(sql);
            } else if (searchType.equals("ID")) {
                sql += "MaNCC = ? ORDER BY MaNCC";
                ps = conn.prepareStatement(sql);
                ps.setInt(1, Integer.parseInt(searchText));
            } else {
                sql += "TenNCC LIKE ? ORDER BY MaNCC";
                ps = conn.prepareStatement(sql);
                ps.setString(1, "%" + searchText + "%");
            }
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("MaNCC"),
                    rs.getString("TenNCC"),
                    rs.getString("SDT"),
                    rs.getString("DiaChi")
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi tìm kiếm: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "ID phải là số!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showAddDialog() {
        System.out.println("Mở dialog thêm nhà cung cấp...");
        NhaCungCapDialog dialog = new NhaCungCapDialog(SwingUtilities.getWindowAncestor(this), "Thêm nhà cung cấp mới", null);
        dialog.setVisible(true);
        if (dialog.isDataChanged()) {
            System.out.println("Dữ liệu đã thay đổi, tải lại...");
            loadData();
        }
    }
    
    private void showEditDialog() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhà cung cấp cần sửa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int id = (Integer) tableModel.getValueAt(selectedRow, 0);
        String ten = (String) tableModel.getValueAt(selectedRow, 1);
        String sdt = (String) tableModel.getValueAt(selectedRow, 2);
        String diaChi = (String) tableModel.getValueAt(selectedRow, 3);
        
        System.out.println("Mở dialog sửa nhà cung cấp - ID: " + id + ", Tên: " + ten);
        
        NhaCungCapDTO ncc = new NhaCungCapDTO(String.valueOf(id), ten, sdt, diaChi);
        NhaCungCapDialog dialog = new NhaCungCapDialog(SwingUtilities.getWindowAncestor(this), "Sửa thông tin nhà cung cấp", ncc);
        dialog.setVisible(true);
        if (dialog.isDataChanged()) {
            System.out.println("Dữ liệu đã thay đổi, tải lại...");
            loadData();
        }
    }
    
    private void performDelete() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhà cung cấp cần xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int id = (Integer) tableModel.getValueAt(selectedRow, 0);
        String ten = (String) tableModel.getValueAt(selectedRow, 1);
        
        // Kiểm tra xem nhà cung cấp có đang được sử dụng không
        try (Connection conn = DBUtil.getConnection()) {
            // Kiểm tra trong bảng phieunhap
            try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM phieunhap WHERE MaNCC=?")) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        JOptionPane.showMessageDialog(this, 
                            "Không thể xóa nhà cung cấp này vì đã có phiếu nhập liên quan!", 
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
            }
            
            // Kiểm tra trong bảng ncc_nguyenlieu
            try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM ncc_nguyenlieu WHERE MaNCC=?")) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        JOptionPane.showMessageDialog(this, 
                            "Không thể xóa nhà cung cấp này vì đã có nguyên liệu liên quan!", 
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi kiểm tra ràng buộc: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int result = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc chắn muốn xóa nhà cung cấp '" + ten + "'?", 
            "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            try (Connection conn = DBUtil.getConnection();
                 PreparedStatement ps = conn.prepareStatement("DELETE FROM nhacungcap WHERE MaNCC=?")) {
                ps.setInt(1, id);
                int rowsAffected = ps.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Xóa thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    loadData();
                } else {
                    JOptionPane.showMessageDialog(this, "Không tìm thấy nhà cung cấp để xóa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                System.err.println("Lỗi xóa nhà cung cấp: " + e.getMessage());
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi xóa dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // Inner class for Add/Edit dialog
    private class NhaCungCapDialog extends JDialog {
        private JTextField tenField, sdtField, diaChiField;
        private boolean dataChanged = false;
        private NhaCungCapDTO ncc;
        
        public NhaCungCapDialog(Window parent, String title, NhaCungCapDTO ncc) {
            super(parent, title, ModalityType.APPLICATION_MODAL);
            this.ncc = ncc;
            System.out.println("Tạo NhaCungCapDialog với title: " + title);
            initializeComponents();
            setupLayout();
            setupEventHandlers();
            System.out.println("Dialog đã được khởi tạo hoàn tất");
        }
        
        private void initializeComponents() {
            setSize(400, 300);
            setLocationRelativeTo(getParent());
            
            tenField = new JTextField(20);
            sdtField = new JTextField(20);
            diaChiField = new JTextField(20);
            
            if (ncc != null) {
                tenField.setText(ncc.getTenNCC() != null ? ncc.getTenNCC() : "");
                sdtField.setText(ncc.getSdt() != null ? ncc.getSdt() : "");
                diaChiField.setText(ncc.getDiaChi() != null ? ncc.getDiaChi() : "");
                System.out.println("Khởi tạo dialog sửa với dữ liệu:");
                System.out.println("Tên: " + ncc.getTenNCC());
                System.out.println("SĐT: " + ncc.getSdt());
                System.out.println("Địa chỉ: " + ncc.getDiaChi());
            } else {
                System.out.println("Khởi tạo dialog thêm mới");
            }
        }
        
        private void setupLayout() {
            setLayout(new BorderLayout());
            
            JPanel mainPanel = new JPanel(new GridBagLayout());
            mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            
            // Tên nhà cung cấp
            gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST;
            mainPanel.add(new JLabel("Tên nhà cung cấp:"), gbc);
            gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
            mainPanel.add(tenField, gbc);
            
            // Số điện thoại
            gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.EAST;
            mainPanel.add(new JLabel("Số điện thoại:"), gbc);
            gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
            mainPanel.add(sdtField, gbc);
            
            // Địa chỉ
            gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.EAST;
            mainPanel.add(new JLabel("Địa chỉ:"), gbc);
            gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
            mainPanel.add(diaChiField, gbc);
            
            // Buttons
            gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
            JPanel buttonPanel = new JPanel(new FlowLayout());
            
            JButton saveButton = new JButton("Lưu");
            saveButton.setBackground(new Color(34, 139, 34));
            saveButton.setForeground(Color.BLACK);
            saveButton.setFocusPainted(false);
            
            JButton cancelButton = new JButton("Hủy");
            cancelButton.setBackground(new Color(220, 220, 220));
            cancelButton.setFocusPainted(false);
            
            // Thêm event handlers trực tiếp
            saveButton.addActionListener(e -> {
                try {
                    saveData();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            });
            
            cancelButton.addActionListener(e -> dispose());
            
            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);
            mainPanel.add(buttonPanel, gbc);
            
            add(mainPanel, BorderLayout.CENTER);
        }
        
        private void setupEventHandlers() {
            // Event handlers đã được thêm trực tiếp trong setupLayout()
            System.out.println("Event handlers đã được thiết lập trực tiếp trong setupLayout()");
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
            System.out.println("=== BẮT ĐẦU saveData() ===");
            
            String ten = tenField.getText().trim();
            String sdt = sdtField.getText().trim();
            String diaChi = diaChiField.getText().trim();
            
            System.out.println("Dữ liệu từ form:");
            System.out.println("Tên: '" + ten + "'");
            System.out.println("SĐT: '" + sdt + "'");
            System.out.println("Địa chỉ: '" + diaChi + "'");
            
            if (ten.isEmpty() || sdt.isEmpty() || diaChi.isEmpty()) {
                System.out.println("Lỗi: Thiếu thông tin bắt buộc");
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            System.out.println("Đang lưu dữ liệu nhà cung cấp...");
            System.out.println("Tên: " + ten);
            System.out.println("SĐT: " + sdt);
            System.out.println("Địa chỉ: " + diaChi);
            System.out.println("Mode: " + (ncc == null ? "Thêm mới" : "Sửa"));
            
            try (Connection conn = DBUtil.getConnection()) {
                System.out.println("Kết nối database thành công!");
                
                if (ncc == null) {
                    // Thêm mới
                    System.out.println("Thực hiện INSERT...");
                    try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO nhacungcap (TenNCC, SDT, DiaChi) VALUES (?, ?, ?)")) {
                        ps.setString(1, ten);
                        ps.setString(2, sdt);
                        ps.setString(3, diaChi);
                        int result = ps.executeUpdate();
                        System.out.println("INSERT thành công! Rows affected: " + result);
                        JOptionPane.showMessageDialog(this, "Thêm thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    // Sửa
                    System.out.println("Thực hiện UPDATE...");
                    System.out.println("MaNCC: " + ncc.getMaNCC());
                    try (PreparedStatement ps = conn.prepareStatement(
                        "UPDATE nhacungcap SET TenNCC=?, SDT=?, DiaChi=? WHERE MaNCC=?")) {
                        ps.setString(1, ten);
                        ps.setString(2, sdt);
                        ps.setString(3, diaChi);
                        ps.setInt(4, Integer.parseInt(ncc.getMaNCC()));
                        int result = ps.executeUpdate();
                        System.out.println("UPDATE thành công! Rows affected: " + result);
                        JOptionPane.showMessageDialog(this, "Sửa thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
                dataChanged = true;
                dispose();
            } catch (SQLException e) {
                System.err.println("Lỗi SQL: " + e.getMessage());
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi lưu dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                System.err.println("Lỗi khác: " + e.getMessage());
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
        
        public boolean isDataChanged() {
            return dataChanged;
        }
    }
}
