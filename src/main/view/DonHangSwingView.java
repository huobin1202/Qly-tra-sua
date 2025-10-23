package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import database.DBUtil;
import dto.DonHangDTO;

public class DonHangSwingView extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> searchCombo;
    private MainFrameInterface parent;
    
    public DonHangSwingView(MainFrameInterface parent) {
        this.parent = parent;
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadData();
    }
    
    private void initializeComponents() {
        // Tạo table model
        String[] columns = {"ID", "Mã NV", "Loại", "Trạng thái", "Ngày đặt", "Tổng tiền", "Giảm giá"};
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
        searchCombo = new JComboBox<>(new String[]{"Tất cả", "ID", "Mã NV", "Loại", "Trạng thái"});
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
        
        JButton detailButton = new JButton("📋 Chi tiết");
        detailButton.setBackground(new Color(70, 130, 180));
        detailButton.setForeground(Color.BLACK);
        detailButton.setFocusPainted(false);
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(detailButton);
        
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
        scrollPane.setBorder(BorderFactory.createTitledBorder("Danh sách đơn hàng"));
        
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
        detailButton.addActionListener(e -> showDetailDialog());
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
             ResultSet rs = stmt.executeQuery("SELECT * FROM dondathang ORDER BY MaDon")) {
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("MaDon"),
                    rs.getInt("MaNV"),
                    rs.getString("Loai"),
                    rs.getString("TrangThai"),
                    rs.getTimestamp("NgayDat") != null ? dateFormat.format(rs.getTimestamp("NgayDat")) : "",
                    String.format("%,d", rs.getLong("TongTien")) + " VNĐ",
                    rs.getInt("GiamGia") + "%"
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
            String sql = "SELECT * FROM dondathang WHERE ";
            PreparedStatement ps;
            
            if (searchType.equals("Tất cả") || searchText.isEmpty()) {
                sql = "SELECT * FROM dondathang ORDER BY MaDon";
                ps = conn.prepareStatement(sql);
            } else if (searchType.equals("ID")) {
                sql += "MaDon = ? ORDER BY MaDon";
                ps = conn.prepareStatement(sql);
                ps.setInt(1, Integer.parseInt(searchText));
            } else if (searchType.equals("Mã NV")) {
                sql += "MaNV = ? ORDER BY MaDon";
                ps = conn.prepareStatement(sql);
                ps.setInt(1, Integer.parseInt(searchText));
            } else if (searchType.equals("Loại")) {
                sql += "Loai LIKE ? ORDER BY MaDon";
                ps = conn.prepareStatement(sql);
                ps.setString(1, "%" + searchText + "%");
            } else {
                sql += "TrangThai LIKE ? ORDER BY MaDon";
                ps = conn.prepareStatement(sql);
                ps.setString(1, "%" + searchText + "%");
            }
            
            ResultSet rs = ps.executeQuery();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("MaDon"),
                    rs.getInt("MaNV"),
                    rs.getString("Loai"),
                    rs.getString("TrangThai"),
                    rs.getTimestamp("NgayDat") != null ? dateFormat.format(rs.getTimestamp("NgayDat")) : "",
                    String.format("%,d", rs.getLong("TongTien")) + " VNĐ",
                    rs.getInt("GiamGia") + "%"
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
        DonHangDialog dialog = new DonHangDialog(SwingUtilities.getWindowAncestor(this), "Thêm đơn hàng mới", null);
        dialog.setVisible(true);
        if (dialog.isDataChanged()) {
            loadData();
        }
    }
    
    private void showEditDialog() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đơn hàng cần sửa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int id = (Integer) tableModel.getValueAt(selectedRow, 0);
        int maNV = (Integer) tableModel.getValueAt(selectedRow, 1);
        String loai = (String) tableModel.getValueAt(selectedRow, 2);
        String trangThai = (String) tableModel.getValueAt(selectedRow, 3);
        String ngayDatStr = (String) tableModel.getValueAt(selectedRow, 4);
        String tongTienStr = (String) tableModel.getValueAt(selectedRow, 5);
        String giamGiaStr = (String) tableModel.getValueAt(selectedRow, 6);
        
        Timestamp ngayDat = null;
        if (!ngayDatStr.isEmpty()) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                ngayDat = new Timestamp(dateFormat.parse(ngayDatStr).getTime());
            } catch (Exception e) {
                // Ignore parsing error
            }
        }
        
        long tongTien = 0;
        if (!tongTienStr.isEmpty()) {
            try {
                tongTien = Long.parseLong(tongTienStr.replaceAll("[^0-9]", ""));
            } catch (Exception e) {
                // Ignore parsing error
            }
        }
        
        int giamGia = 0;
        if (!giamGiaStr.isEmpty()) {
            try {
                giamGia = Integer.parseInt(giamGiaStr.replaceAll("[^0-9]", ""));
            } catch (Exception e) {
                // Ignore parsing error
            }
        }
        
        DonHangDTO dh = new DonHangDTO(id, maNV, loai, trangThai, ngayDat, tongTien, giamGia);
        DonHangDialog dialog = new DonHangDialog(SwingUtilities.getWindowAncestor(this), "Sửa thông tin đơn hàng", dh);
        dialog.setVisible(true);
        if (dialog.isDataChanged()) {
            loadData();
        }
    }
    
    private void showDetailDialog() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đơn hàng cần xem chi tiết!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int maDon = (Integer) tableModel.getValueAt(selectedRow, 0);
        
        // Hiển thị chi tiết đơn hàng
        try (Connection conn = DBUtil.getConnection()) {
            StringBuilder detail = new StringBuilder();
            detail.append("CHI TIẾT ĐƠN HÀNG #").append(maDon).append("\n\n");
            
            // Thông tin đơn hàng
            try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM dondathang WHERE MaDon = ?")) {
                ps.setInt(1, maDon);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    detail.append("Mã đơn: ").append(rs.getInt("MaDon")).append("\n");
                    detail.append("Mã NV: ").append(rs.getInt("MaNV")).append("\n");
                    detail.append("Loại: ").append(rs.getString("Loai")).append("\n");
                    detail.append("Trạng thái: ").append(rs.getString("TrangThai")).append("\n");
                    detail.append("Ngày đặt: ").append(rs.getTimestamp("NgayDat")).append("\n");
                    detail.append("Tổng tiền: ").append(String.format("%,d", rs.getLong("TongTien"))).append(" VNĐ\n");
                    detail.append("Giảm giá: ").append(rs.getInt("GiamGia")).append("%\n\n");
                }
            }
            
            // Chi tiết món
            detail.append("CHI TIẾT MÓN:\n");
            try (PreparedStatement ps = conn.prepareStatement(
                "SELECT ctdh.*, m1.TenMon AS TenMon, m2.TenMon AS TenTopping FROM chitietdonhang ctdh " +
                "LEFT JOIN mon m1 ON ctdh.MaMon = m1.MaMon " +
                "LEFT JOIN mon m2 ON ctdh.MaTopping = m2.MaMon WHERE ctdh.MaDon = ?")) {
                ps.setInt(1, maDon);
                ResultSet rs = ps.executeQuery();
                
                if (!rs.next()) {
                    detail.append("Không có chi tiết món.\n");
                } else {
                    do {
                        long giaMon = rs.getLong("GiaMon");
                        long giaTopping = rs.getLong("GiaTopping");
                        int soLuong = rs.getInt("SoLuong");
                        long thanhTien = (giaMon + giaTopping) * soLuong;
                        String toppingName = rs.getString("TenTopping");
                        detail.append("- ")
                              .append(rs.getString("TenMon"))
                              .append(toppingName != null && !toppingName.isEmpty() ? " + " + toppingName : "")
                              .append(" x").append(soLuong)
                              .append(" = ").append(String.format("%,d", thanhTien)).append(" VNĐ\n");
                    } while (rs.next());
                }
            }
            
            JTextArea textArea = new JTextArea(detail.toString());
            textArea.setEditable(false);
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(500, 400));
            
            JOptionPane.showMessageDialog(this, scrollPane, "Chi tiết đơn hàng", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải chi tiết: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void performDelete() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đơn hàng cần xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int id = (Integer) tableModel.getValueAt(selectedRow, 0);
        
        int result = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc chắn muốn xóa đơn hàng #" + id + "?", 
            "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            try (Connection conn = DBUtil.getConnection()) {
                // Xóa chi tiết đơn hàng trước
                try (PreparedStatement ps = conn.prepareStatement("DELETE FROM chitietdonhang WHERE MaDon = ?")) {
                    ps.setInt(1, id);
                    ps.executeUpdate();
                }
                
                // Xóa đơn hàng
                try (PreparedStatement ps = conn.prepareStatement("DELETE FROM dondathang WHERE MaDon = ?")) {
                    ps.setInt(1, id);
                    ps.executeUpdate();
                }
                
                JOptionPane.showMessageDialog(this, "Xóa thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Lỗi xóa dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // Inner class for Add/Edit dialog
    private class DonHangDialog extends JDialog {
        private JTextField maNVField, loaiField, tongTienField, giamGiaField;
        private JComboBox<String> trangThaiCombo;
        private JTextField ngayDatField;
        private boolean dataChanged = false;
        private DonHangDTO dh;
        
        public DonHangDialog(Window parent, String title, DonHangDTO dh) {
            super(parent, title, ModalityType.APPLICATION_MODAL);
            this.dh = dh;
            initializeComponents();
            setupLayout();
            setupEventHandlers();
        }
        
        private void initializeComponents() {
            setSize(450, 400);
            setLocationRelativeTo(getParent());
            
            maNVField = new JTextField(20);
            loaiField = new JTextField(20);
            tongTienField = new JTextField(20);
            giamGiaField = new JTextField(20);
            ngayDatField = new JTextField(20);
            trangThaiCombo = new JComboBox<>(new String[]{"Chờ xử lý", "Đang chuẩn bị", "Đang giao", "Đã giao", "Đã hủy"});
            
            if (dh != null) {
                maNVField.setText(String.valueOf(dh.getMaNV()));
                loaiField.setText(dh.getLoai());
                trangThaiCombo.setSelectedItem(dh.getTrangThai());
                tongTienField.setText(String.valueOf(dh.getTongTien()));
                giamGiaField.setText(String.valueOf(dh.getGiamGia()));
                if (dh.getNgayDat() != null) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    ngayDatField.setText(dateFormat.format(dh.getNgayDat()));
                }
            } else {
                // Mặc định cho đơn hàng mới
                maNVField.setText(String.valueOf(database.Session.currentMaNV));
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                ngayDatField.setText(dateFormat.format(new java.util.Date()));
                giamGiaField.setText("0");
            }
        }
        
        private void setupLayout() {
            setLayout(new BorderLayout());
            
            JPanel mainPanel = new JPanel(new GridBagLayout());
            mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            
            // Mã NV
            gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST;
            mainPanel.add(new JLabel("Mã nhân viên:"), gbc);
            gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
            mainPanel.add(maNVField, gbc);
            
            // Loại
            gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.EAST;
            mainPanel.add(new JLabel("Loại:"), gbc);
            gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
            mainPanel.add(loaiField, gbc);
            
            // Trạng thái
            gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.EAST;
            mainPanel.add(new JLabel("Trạng thái:"), gbc);
            gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
            mainPanel.add(trangThaiCombo, gbc);
            
            // Ngày đặt
            gbc.gridx = 0; gbc.gridy = 3; gbc.anchor = GridBagConstraints.EAST;
            mainPanel.add(new JLabel("Ngày đặt (yyyy-mm-dd hh:mm):"), gbc);
            gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
            mainPanel.add(ngayDatField, gbc);
            
            // Tổng tiền
            gbc.gridx = 0; gbc.gridy = 4; gbc.anchor = GridBagConstraints.EAST;
            mainPanel.add(new JLabel("Tổng tiền (VNĐ):"), gbc);
            gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
            mainPanel.add(tongTienField, gbc);
            
            // Giảm giá
            gbc.gridx = 0; gbc.gridy = 5; gbc.anchor = GridBagConstraints.EAST;
            mainPanel.add(new JLabel("Giảm giá (%):"), gbc);
            gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
            mainPanel.add(giamGiaField, gbc);
            
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
            String maNVStr = maNVField.getText().trim();
            String loai = loaiField.getText().trim();
            String trangThai = (String) trangThaiCombo.getSelectedItem();
            String ngayDatStr = ngayDatField.getText().trim();
            String tongTienStr = tongTienField.getText().trim();
            String giamGiaStr = giamGiaField.getText().trim();
            
            if (maNVStr.isEmpty() || loai.isEmpty() || ngayDatStr.isEmpty() || tongTienStr.isEmpty() || giamGiaStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin bắt buộc!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int maNV, giamGia;
            long tongTien;
            try {
                maNV = Integer.parseInt(maNVStr);
                tongTien = Long.parseLong(tongTienStr);
                giamGia = Integer.parseInt(giamGiaStr);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Mã NV, tổng tiền và giảm giá phải là số!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try (Connection conn = DBUtil.getConnection()) {
                if (dh == null) {
                    // Thêm mới
                    PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO dondathang (MaNV, Loai, TrangThai, NgayDat, TongTien, GiamGia) VALUES (?, ?, ?, ?, ?, ?)");
                    ps.setInt(1, maNV);
                    ps.setString(2, loai);
                    ps.setString(3, trangThai);
                    ps.setString(4, ngayDatStr);
                    ps.setLong(5, tongTien);
                    ps.setInt(6, giamGia);
                    ps.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Thêm thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    // Sửa
                    PreparedStatement ps = conn.prepareStatement(
                        "UPDATE dondathang SET MaNV=?, Loai=?, TrangThai=?, NgayDat=?, TongTien=?, GiamGia=? WHERE MaDon=?");
                    ps.setInt(1, maNV);
                    ps.setString(2, loai);
                    ps.setString(3, trangThai);
                    ps.setString(4, ngayDatStr);
                    ps.setLong(5, tongTien);
                    ps.setInt(6, giamGia);
                    ps.setInt(7, dh.getMaDon());
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
