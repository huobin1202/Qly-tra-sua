package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import database.DBUtil;
import dto.NhanVienDTO;

public class NhanVienSwingView extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> searchCombo;
    private MainFrameInterface parent;
    
    public NhanVienSwingView(MainFrameInterface parent) {
        this.parent = parent;
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadData();
    }
    
    private void initializeComponents() {
        // Tạo table model
        String[] columns = {"ID", "Tài khoản","Mật khẩu", "Họ tên", "Số điện thoại", "Ngày vào làm", "Chức vụ", "Lương"};
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
        searchCombo = new JComboBox<>(new String[]{"Tất cả", "ID", "Tài khoản", "Họ tên", "Chức vụ"});
        searchField = new JTextField(20);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 248, 255));
        
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
        scrollPane.setBorder(BorderFactory.createTitledBorder("Danh sách nhân viên"));
        
        // Layout
        add(topPanel, BorderLayout.NORTH);
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
             ResultSet rs = stmt.executeQuery("SELECT * FROM nhanvien ORDER BY MaNV")) {
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("MaNV"),
                    rs.getString("TaiKhoan"),
                    rs.getString("MatKhau"),
                    rs.getString("HoTen"),
                    rs.getString("SDT"),
                    rs.getTimestamp("NgayVaoLam") != null ? dateFormat.format(rs.getTimestamp("NgayVaoLam")) : "",
                    rs.getString("ChucVu"),
                    String.format("%,d", rs.getInt("Luong")) + " VNĐ"
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
            String sql = "SELECT * FROM nhanvien WHERE ";
            PreparedStatement ps;
            
            if (searchType.equals("Tất cả") || searchText.isEmpty()) {
                sql = "SELECT * FROM nhanvien ORDER BY MaNV";
                ps = conn.prepareStatement(sql);
            } else if (searchType.equals("ID")) {
                sql += "MaNV = ? ORDER BY MaNV";
                ps = conn.prepareStatement(sql);
                ps.setInt(1, Integer.parseInt(searchText));
            } else if (searchType.equals("Tài khoản")) {
                sql += "TaiKhoan LIKE ? ORDER BY MaNV";
                ps = conn.prepareStatement(sql);
                ps.setString(1, "%" + searchText + "%");
            } else if (searchType.equals("Họ tên")) {
                sql += "HoTen LIKE ? ORDER BY MaNV";
                ps = conn.prepareStatement(sql);
                ps.setString(1, "%" + searchText + "%");
            } else {
                sql += "ChucVu LIKE ? ORDER BY MaNV";
                ps = conn.prepareStatement(sql);
                ps.setString(1, "%" + searchText + "%");
            }
            
            ResultSet rs = ps.executeQuery();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("MaNV"),
                    rs.getString("TaiKhoan"),
                    rs.getString("MatKhau"),
                    rs.getString("HoTen"),
                    rs.getString("SDT"),
                    rs.getTimestamp("NgayVaoLam") != null ? dateFormat.format(rs.getTimestamp("NgayVaoLam")) : "",
                    rs.getString("ChucVu"),
                    String.format("%,d", rs.getInt("Luong")) + " VNĐ"
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
        NhanVienDialog dialog = new NhanVienDialog(SwingUtilities.getWindowAncestor(this), "Thêm nhân viên mới", null);
        dialog.setVisible(true);
        if (dialog.isDataChanged()) {
            loadData();
        }
    }
    
    private void showEditDialog() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần sửa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int id = (Integer) tableModel.getValueAt(selectedRow, 0);
        String taiKhoan = (String) tableModel.getValueAt(selectedRow, 1);
        String matKhau = (String) tableModel.getValueAt(selectedRow, 2);
        String hoTen = (String) tableModel.getValueAt(selectedRow, 3);
        String sdt = (String) tableModel.getValueAt(selectedRow, 4);
        String ngayVaoLamStr = (String) tableModel.getValueAt(selectedRow, 5);
        String chucVu = (String) tableModel.getValueAt(selectedRow, 6);
        String luongStr = (String) tableModel.getValueAt(selectedRow, 7);
        
        Timestamp ngayVaoLam = null;
        if (!ngayVaoLamStr.isEmpty()) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                ngayVaoLam = new Timestamp(dateFormat.parse(ngayVaoLamStr).getTime());
            } catch (Exception e) {
                // Ignore parsing error
            }
        }
        
        int luong = 0;
        if (!luongStr.isEmpty()) {
            try {
                luong = Integer.parseInt(luongStr.replaceAll("[^0-9]", ""));
            } catch (Exception e) {
                // Ignore parsing error
            }
        }
        
        NhanVienDTO nv = new NhanVienDTO(id, taiKhoan, matKhau, hoTen, sdt, ngayVaoLam, chucVu, luong);
        NhanVienDialog dialog = new NhanVienDialog(SwingUtilities.getWindowAncestor(this), "Sửa thông tin nhân viên", nv);
        dialog.setVisible(true);
        if (dialog.isDataChanged()) {
            loadData();
        }
    }
    
    private void performDelete() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int id = (Integer) tableModel.getValueAt(selectedRow, 0);
        String hoTen = (String) tableModel.getValueAt(selectedRow, 3);
        
        // Kiểm tra khóa ngoại trước khi xóa
        try (Connection conn = DBUtil.getConnection()) {
            // Kiểm tra nhân viên có được sử dụng trong đơn đặt hàng không
            try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM dondathang WHERE MaNV=?")) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        JOptionPane.showMessageDialog(this, 
                            "Không thể xóa nhân viên này vì đã có đơn đặt hàng liên quan!", 
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
            }
            
            // Kiểm tra nhân viên có được sử dụng trong phiếu nhập không
            try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM phieunhap WHERE MaNV=?")) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        JOptionPane.showMessageDialog(this, 
                            "Không thể xóa nhân viên này vì đã có phiếu nhập liên quan!", 
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
            "Bạn có chắc chắn muốn xóa nhân viên '" + hoTen + "'?", 
            "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            try (Connection conn = DBUtil.getConnection();
                 PreparedStatement ps = conn.prepareStatement("DELETE FROM nhanvien WHERE MaNV=?")) {
                ps.setInt(1, id);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Xóa thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Lỗi xóa dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // Inner class for Add/Edit dialog
    private class NhanVienDialog extends JDialog {
        private JTextField taiKhoanField, matKhauField, hoTenField, sdtField, ngayVaoLamField, luongField;
        private JComboBox<String> chucVuCombo;
        private boolean dataChanged = false;
        private NhanVienDTO nv;
        
        public NhanVienDialog(Window parent, String title, NhanVienDTO nv) {
            super(parent, title, ModalityType.APPLICATION_MODAL);
            this.nv = nv;
            initializeComponents();
            setupLayout();
            setupEventHandlers();
        }
        
        private void initializeComponents() {
            setSize(450, 500);
            setLocationRelativeTo(getParent());
            
            taiKhoanField = new JTextField(20);
            matKhauField = new JTextField(20);
            hoTenField = new JTextField(20);
            sdtField = new JTextField(20);
            ngayVaoLamField = new JTextField(20);
            chucVuCombo = new JComboBox<>(new String[]{"Nhân viên", "Quản lý"});
            luongField = new JTextField(20);
            
            if (nv != null) {
                // Sửa nhân viên - hiển thị thông tin hiện tại
                taiKhoanField.setText(nv.getTaiKhoan());
                matKhauField.setText(nv.getMatKhau());
                hoTenField.setText(nv.getHoTen());
                sdtField.setText(nv.getSdt());
                if (nv.getNgayVaoLam() != null) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    ngayVaoLamField.setText(dateFormat.format(nv.getNgayVaoLam()));
                }
                chucVuCombo.setSelectedItem(nv.getChucVu());
                luongField.setText(String.valueOf(nv.getLuong()));
            } else {
                // Thêm nhân viên mới - tự động set ngày hiện tại
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                ngayVaoLamField.setText(dateFormat.format(new java.util.Date()));
            }
        }
        
        private void setupLayout() {
            setLayout(new BorderLayout());
            
            JPanel mainPanel = new JPanel(new GridBagLayout());
            mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            
            // Tài khoản
            gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST;
            mainPanel.add(new JLabel("Tài khoản:"), gbc);
            gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
            mainPanel.add(taiKhoanField, gbc);
            
            // Mật khẩu
            gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.EAST;
            mainPanel.add(new JLabel("Mật khẩu:"), gbc);
            gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
            mainPanel.add(matKhauField, gbc);
            
            // Họ tên
            gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.EAST;
            mainPanel.add(new JLabel("Họ tên:"), gbc);
            gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
            mainPanel.add(hoTenField, gbc);
            
            // Số điện thoại
            gbc.gridx = 0; gbc.gridy = 3; gbc.anchor = GridBagConstraints.EAST;
            mainPanel.add(new JLabel("Số điện thoại:"), gbc);
            gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
            mainPanel.add(sdtField, gbc);
            
            // Ngày vào làm
            gbc.gridx = 0; gbc.gridy = 4; gbc.anchor = GridBagConstraints.EAST;
            mainPanel.add(new JLabel("Ngày vào làm (yyyy-mm-dd):"), gbc);
            gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
            mainPanel.add(ngayVaoLamField, gbc);
            
            // Chức vụ
            gbc.gridx = 0; gbc.gridy = 5; gbc.anchor = GridBagConstraints.EAST;
            mainPanel.add(new JLabel("Chức vụ:"), gbc);
            gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
            mainPanel.add(chucVuCombo, gbc);
            
            // Lương
            gbc.gridx = 0; gbc.gridy = 6; gbc.anchor = GridBagConstraints.EAST;
            mainPanel.add(new JLabel("Lương (VNĐ):"), gbc);
            gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
            mainPanel.add(luongField, gbc);
            
            // Buttons
            gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
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
            String taiKhoan = taiKhoanField.getText().trim();
            String matKhau = matKhauField.getText().trim();
            String hoTen = hoTenField.getText().trim();
            String ngayVaoLamStr = ngayVaoLamField.getText().trim();
            String chucVu = (String) chucVuCombo.getSelectedItem();
            String luongStr = luongField.getText().trim();
            String sdtStr = sdtField.getText().trim();
            if (taiKhoan.isEmpty() || hoTen.isEmpty() || sdtStr.isEmpty() || chucVu.isEmpty() || luongStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin bắt buộc!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int luong;
            int sdt;
            try {
                luong = Integer.parseInt(luongStr);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Lương phải là số!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            } 
            try {
                sdt = Integer.parseInt(sdtStr);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Số điện thoại phải là số!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try (Connection conn = DBUtil.getConnection()) {
                if (nv == null) {
                    // Thêm mới
                    if (matKhau.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Vui lòng nhập mật khẩu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO nhanvien (TaiKhoan, MatKhau, HoTen, SDT, NgayVaoLam, ChucVu, Luong) VALUES (?, ?, ?, ?, ?, ?, ?)");
                    ps.setString(1, taiKhoan);
                    ps.setString(2, matKhau);
                    ps.setString(3, hoTen);
                    ps.setInt(4, sdt);
                    
                    if (!ngayVaoLamStr.isEmpty()) {
                        ps.setString(5, ngayVaoLamStr );
                    } else {
                        ps.setNull(5, Types.TIMESTAMP);
                    }
                    
                    ps.setString(6, chucVu);
                    ps.setInt(7, luong);
                    ps.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Thêm thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    // Sửa
                    PreparedStatement ps;
                    if (!matKhau.isEmpty()) {
                        ps = conn.prepareStatement(
                            "UPDATE nhanvien SET TaiKhoan=?, MatKhau=?, HoTen=?, SDT=?, NgayVaoLam=?, ChucVu=?, Luong=? WHERE MaNV=?");
                        ps.setString(1, taiKhoan);
                        ps.setString(2, matKhau);
                        ps.setString(3, hoTen);
                        ps.setInt(4, sdt);
                        
                        if (!ngayVaoLamStr.isEmpty()) {
                            ps.setString(5, ngayVaoLamStr );
                        } else {
                            ps.setNull(5, Types.TIMESTAMP);
                        }
                        
                        ps.setString(6, chucVu);
                        ps.setInt(7, luong);
                        ps.setInt(8, nv.getMaNV());
                    } else {
                        ps = conn.prepareStatement(
                            "UPDATE nhanvien SET TaiKhoan=?, HoTen=?, SDT=?, NgayVaoLam=?, ChucVu=?, Luong=? WHERE MaNV=?");
                        ps.setString(1, taiKhoan);
                        ps.setString(2, hoTen);
                        ps.setInt(3, sdt);
                        
                        if (!ngayVaoLamStr.isEmpty()) {
                            ps.setString(4, ngayVaoLamStr );
                        } else {
                            ps.setNull(4, Types.TIMESTAMP);
                        }
                        
                        ps.setString(5, chucVu);
                        ps.setInt(6, luong);
                        ps.setInt(7, nv.getMaNV());
                    }
                    
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
