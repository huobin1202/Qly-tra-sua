package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import database.DBUtil;

public class KhoHangView extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> searchCombo;
    private MainFrameInterface parent;
    
    public KhoHangView(MainFrameInterface parent) {
        this.parent = parent;
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadData();
    }
    
    private void initializeComponents() {
        // Tạo table model (hiển thị tồn kho nguyên liệu vì không có bảng kho sản phẩm)
        String[] columns = {"Mã NL", "Tên nguyên liệu", "Đơn vị", "Số lượng tồn"};
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
        searchCombo = new JComboBox<>(new String[]{"Tất cả", "Mã NL", "Tên NL"});
        searchField = new JTextField(20);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 248, 255));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
       
        
  
        
        // Top panel - chứa search và buttons trong cùng một hàng
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(240, 248, 255));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Button panel (bên trái) - Cập nhật/Hàng sắp hết
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(new Color(240, 248, 255));
        
        JButton editButton = new JButton("✏️ Sửa số lượng");
        editButton.setBackground(new Color(70, 130, 180));
        editButton.setForeground(Color.BLACK);
        editButton.setFocusPainted(false);
        
        JButton lowStockButton = new JButton("⚠️ Hàng sắp hết");
        lowStockButton.setBackground(new Color(255, 69, 0));
        lowStockButton.setForeground(Color.BLACK);
        lowStockButton.setFocusPainted(false);
        
        buttonPanel.add(editButton);
        buttonPanel.add(lowStockButton);
        
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
        scrollPane.setBorder(BorderFactory.createTitledBorder("Danh sách tồn kho"));
        
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
        editButton.addActionListener(e -> showUpdateDialog());
        lowStockButton.addActionListener(e -> showLowStockDialog());
    }
    
    private void setupEventHandlers() {
        // Double click to update
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    showUpdateDialog();
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
             ResultSet rs = stmt.executeQuery(
                 "SELECT nl.MaNL, nl.TenNL, nl.DonVi, COALESCE(k.SoLuong, 0) AS SoLuong " +
                 "FROM nguyenlieu nl LEFT JOIN khohang k ON nl.MaNL = k.MaNL " +
                 "ORDER BY nl.MaNL")) {

            while (rs.next()) {
                Object[] row = {
                    rs.getInt("MaNL"),
                    rs.getString("TenNL"),
                    rs.getString("DonVi"),
                    rs.getInt("SoLuong")
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
            String sql = "SELECT nl.MaNL, nl.TenNL, nl.DonVi, COALESCE(k.SoLuong, 0) AS SoLuong " +
                        "FROM nguyenlieu nl LEFT JOIN khohang k ON nl.MaNL = k.MaNL WHERE ";
            PreparedStatement ps;

            if (searchType.equals("Tất cả") || searchText.isEmpty()) {
                sql = "SELECT nl.MaNL, nl.TenNL, nl.DonVi, COALESCE(k.SoLuong, 0) AS SoLuong " +
                      "FROM nguyenlieu nl LEFT JOIN khohang k ON nl.MaNL = k.MaNL ORDER BY nl.MaNL";
                ps = conn.prepareStatement(sql);
            } else if (searchType.equals("Mã NL")) {
                sql += "nl.MaNL = ? ORDER BY nl.MaNL";
                ps = conn.prepareStatement(sql);
                ps.setInt(1, Integer.parseInt(searchText));
            } else {
                sql += "nl.TenNL LIKE ? ORDER BY nl.MaNL";
                ps = conn.prepareStatement(sql);
                ps.setString(1, "%" + searchText + "%");
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("MaNL"),
                    rs.getString("TenNL"),
                    rs.getString("DonVi"),
                    rs.getInt("SoLuong")
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi tìm kiếm: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Mã NL phải là số!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showUpdateDialog() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nguyên liệu cần cập nhật!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            int maNL = (Integer) tableModel.getValueAt(selectedRow, 0);
            String tenNL = (String) tableModel.getValueAt(selectedRow, 1);
            int soLuongHienTai = (Integer) tableModel.getValueAt(selectedRow, 3);

            UpdateStockDialog dialog = new UpdateStockDialog(SwingUtilities.getWindowAncestor(this), 
                "Cập nhật số lượng tồn kho", maNL, tenNL, soLuongHienTai);
            dialog.setVisible(true);
            if (dialog.isDataChanged()) {
                loadData();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi mở dialog sửa: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showLowStockDialog() {
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                 "SELECT nl.TenNL, nl.DonVi, COALESCE(k.SoLuong, 0) as SoLuong " +
                 "FROM nguyenlieu nl LEFT JOIN khohang k ON nl.MaNL = k.MaNL " +
                 "WHERE COALESCE(k.SoLuong, 0) <= 1000 " +
                 "ORDER BY COALESCE(k.SoLuong, 0) ASC")) {
            
            StringBuilder lowStock = new StringBuilder();
            lowStock.append("DANH SÁCH HÀNG SẮP HẾT (≤ 1000):\n\n");
            
            boolean hasLowStock = false;
            while (rs.next()) {
                hasLowStock = true;
                lowStock.append("• ").append(rs.getString("TenNL"))
                       .append(" - Còn: ").append(rs.getInt("SoLuong"))
                       .append(" ").append(rs.getString("DonVi")).append("\n");
            }
            
            if (!hasLowStock) {
                lowStock.append("Không có hàng nào sắp hết!");
            }
            
            JTextArea textArea = new JTextArea(lowStock.toString());
            textArea.setEditable(false);
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(400, 300));
            
            JOptionPane.showMessageDialog(this, scrollPane, "Hàng sắp hết", JOptionPane.WARNING_MESSAGE);
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Inner class for Update Stock dialog
    private class UpdateStockDialog extends JDialog {
        private JTextField soLuongField;
        private boolean dataChanged = false;
        private int maNL;
        private String tenNL;
        private int soLuongHienTai;
        private JButton saveButton;
        private JButton cancelButton;
        
        public UpdateStockDialog(Window parent, String title, int maNL, String tenNL, int soLuongHienTai) {
            super(parent, title, ModalityType.APPLICATION_MODAL);
            this.maNL = maNL;
            this.tenNL = tenNL;
            this.soLuongHienTai = soLuongHienTai;
            initializeComponents();
            setupLayout(); // sẽ gắn luôn listener ở đây
        }
        
        private void initializeComponents() {
            setSize(350, 200);
            setLocationRelativeTo(getParent());
            soLuongField = new JTextField(20);
            soLuongField.setText(String.valueOf(soLuongHienTai));
        }
        
        private void setupLayout() {
            setLayout(new BorderLayout());
            JPanel mainPanel = new JPanel(new GridBagLayout());
            mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
            JLabel infoLabel = new JLabel("Nguyên liệu: " + tenNL + " (Mã: " + maNL + ")");
            infoLabel.setFont(new Font("Arial", Font.BOLD, 14));
            mainPanel.add(infoLabel, gbc);
            gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
            JLabel currentLabel = new JLabel("Số lượng hiện tại: " + soLuongHienTai);
            mainPanel.add(currentLabel, gbc);
            gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.EAST;
            mainPanel.add(new JLabel("Số lượng mới:"), gbc);
            gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
            mainPanel.add(soLuongField, gbc);
            gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
            JPanel buttonPanel = new JPanel(new FlowLayout());
            saveButton = new JButton("Lưu");
            saveButton.setBackground(new Color(34, 139, 34));
            saveButton.setForeground(Color.BLACK);
            saveButton.setFocusPainted(false);
            cancelButton = new JButton("Hủy");
            cancelButton.setBackground(new Color(220, 220, 220));
            cancelButton.setFocusPainted(false);
            // Gắn action listener TRỰC TIẾP
            saveButton.addActionListener(e -> saveData());
            cancelButton.addActionListener(e -> dispose());
            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);
            mainPanel.add(buttonPanel, gbc);
            add(mainPanel, BorderLayout.CENTER);
        }
        
        private void saveData() {
            String soLuongStr = soLuongField.getText().trim();
            
            if (soLuongStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập số lượng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int soLuongMoi;
            try {
                soLuongMoi = Integer.parseInt(soLuongStr);
                if (soLuongMoi < 0) {
                    JOptionPane.showMessageDialog(this, "Số lượng không được âm!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Số lượng phải là số nguyên!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try (Connection conn = DBUtil.getConnection()) {
                System.out.println("Kết nối database thành công");
                
                // Kiểm tra xem đã có record trong khohang chưa
                boolean exists = false;
                try (PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM khohang WHERE MaNL = ?")) {
                    ps.setInt(1, maNL);
                    ResultSet rs = ps.executeQuery();
                    exists = rs.next();
                }
                
                if (exists) {
                    // Cập nhật
                    try (PreparedStatement ps = conn.prepareStatement("UPDATE khohang SET SoLuong = ? WHERE MaNL = ?")) {
                        ps.setInt(1, soLuongMoi);
                        ps.setInt(2, maNL);
                        int result = ps.executeUpdate();
                    }
                } else {
                    // Thêm mới
                    try (PreparedStatement ps = conn.prepareStatement("INSERT INTO khohang (MaNL, SoLuong) VALUES (?, ?)")) {
                        ps.setInt(1, maNL);
                        ps.setInt(2, soLuongMoi);
                        int result = ps.executeUpdate();
                    }
                }
                
                JOptionPane.showMessageDialog(this, "Cập nhật thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                dataChanged = true;
                dispose();
            } catch (SQLException e) {
                System.err.println("Lỗi kết nối database: " + e.getMessage());
                JOptionPane.showMessageDialog(this, "Lỗi cập nhật dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi không xác định: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
        
        public boolean isDataChanged() {
            return dataChanged;
        }
    }
}
