package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.List;
import database.DBUtil;
import dto.NhapHangDTO;
import dto.ChiTietNhapHangDTO;
import dao.NhapHangDAO;

public class NhapHangView extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> searchCombo;
    private final MainFrameInterface parent;
    private final NhapHangDAO nhapHangDAO;
    
    public NhapHangView(MainFrameInterface parent) {
        this.parent = parent;
        this.nhapHangDAO = new NhapHangDAO();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadData();
    }
    
    private void initializeComponents() {
        // Tạo table model
        String[] columns = {"ID", "Nhân viên", "Nhà cung cấp", "Ngày", "Thành tiền", "Trạng thái"};
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
        searchCombo = new JComboBox<>(new String[]{"Tất cả", "ID", "Nhân viên", "Nhà cung cấp", "Trạng thái"});
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
        
        JButton confirmButton = new JButton("✅ Xác nhận");
        confirmButton.setBackground(new Color(0, 128, 0));
        confirmButton.setForeground(Color.BLACK);
        confirmButton.setFocusPainted(false);
        
        JButton viewDetailsButton = new JButton("👁️ Xem chi tiết");
        viewDetailsButton.setBackground(new Color(70, 130, 180));
        viewDetailsButton.setForeground(Color.BLACK);
        viewDetailsButton.setFocusPainted(false);
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(confirmButton);
        buttonPanel.add(viewDetailsButton);
        
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
        scrollPane.setBorder(BorderFactory.createTitledBorder("Danh sách phiếu nhập"));
        
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
        confirmButton.addActionListener(e -> performConfirm());
        viewDetailsButton.addActionListener(e -> showDetailsDialog());
    }
    
    private void setupEventHandlers() {
        // Double click to edit
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
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
             ResultSet rs = stmt.executeQuery(
                "SELECT p.*, nv.HoTen as TenNV, ncc.TenNCC " +
                "FROM phieunhap p " +
                "LEFT JOIN nhanvien nv ON p.MaNV = nv.MaNV " +
                "LEFT JOIN nhacungcap ncc ON p.MaNCC = ncc.MaNCC " +
                "ORDER BY p.MaPN")) {
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("MaPN"),
                    rs.getString("TenNV") != null ? rs.getString("TenNV") : "N/A",
                    rs.getString("TenNCC") != null ? rs.getString("TenNCC") : "N/A",
                    rs.getDate("Ngay") != null ? dateFormat.format(rs.getDate("Ngay")) : "",
                    String.format("%,d", rs.getLong("ThanhTien")) + " VNĐ",
                    convertTrangThaiToUI(rs.getString("TrangThai"))
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
            String sql = "SELECT p.*, nv.HoTen as TenNV, ncc.TenNCC " +
                        "FROM phieunhap p " +
                        "LEFT JOIN nhanvien nv ON p.MaNV = nv.MaNV " +
                        "LEFT JOIN nhacungcap ncc ON p.MaNCC = ncc.MaNCC " +
                        "WHERE ";
            PreparedStatement ps;
            
            if (searchType.equals("Tất cả") || searchText.isEmpty()) {
                sql = "SELECT p.*, nv.HoTen as TenNV, ncc.TenNCC " +
                      "FROM phieunhap p " +
                      "LEFT JOIN nhanvien nv ON p.MaNV = nv.MaNV " +
                      "LEFT JOIN nhacungcap ncc ON p.MaNCC = ncc.MaNCC " +
                      "ORDER BY p.MaPN";
                ps = conn.prepareStatement(sql);
            } else if (searchType.equals("ID")) {
                sql += "p.MaPN = ? ORDER BY p.MaPN";
                ps = conn.prepareStatement(sql);
                ps.setInt(1, Integer.parseInt(searchText));
            } else if (searchType.equals("Nhân viên")) {
                sql += "nv.HoTen LIKE ? ORDER BY p.MaPN";
                ps = conn.prepareStatement(sql);
                ps.setString(1, "%" + searchText + "%");
            } else if (searchType.equals("Nhà cung cấp")) {
                sql += "ncc.TenNCC LIKE ? ORDER BY p.MaPN";
                ps = conn.prepareStatement(sql);
                ps.setString(1, "%" + searchText + "%");
            } else {
                sql += "p.TrangThai LIKE ? ORDER BY p.MaPN";
                ps = conn.prepareStatement(sql);
                ps.setString(1, "%" + searchText + "%");
            }
            
            ResultSet rs = ps.executeQuery();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("MaPN"),
                    rs.getString("TenNV") != null ? rs.getString("TenNV") : "N/A",
                    rs.getString("TenNCC") != null ? rs.getString("TenNCC") : "N/A",
                    rs.getDate("Ngay") != null ? dateFormat.format(rs.getDate("Ngay")) : "",
                    String.format("%,d", rs.getLong("ThanhTien")) + " VNĐ",
                    convertTrangThaiToUI(rs.getString("TrangThai"))
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
        // Mở giao diện nhập hàng mới (NhapHangMoiView)
        NhapHangMoiView nhapHangMoiView = new NhapHangMoiView();
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Thêm phiếu nhập mới", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(1200, 800);
        dialog.setLocationRelativeTo(this);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        // Thêm nút đóng
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeButton = new JButton("Đóng");
        closeButton.addActionListener(e -> {
            dialog.dispose();
            loadData(); // Làm mới danh sách sau khi đóng
        });
        buttonPanel.add(closeButton);
        
        // Tạo panel chính với layout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(nhapHangMoiView, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Thêm panel chính vào dialog
        dialog.setContentPane(mainPanel);
        
        dialog.setVisible(true);
    }
    
    private void showEditDialog() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn phiếu nhập cần sửa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int id = (Integer) tableModel.getValueAt(selectedRow, 0);
        String trangThai = (String) tableModel.getValueAt(selectedRow, 5);
        
        // Kiểm tra trạng thái phiếu nhập
        if ("Đã xác nhận".equalsIgnoreCase(trangThai)) {
            JOptionPane.showMessageDialog(this, 
                "Phiếu nhập đã được xác nhận, không thể chỉnh sửa!\nChỉ có thể xem chi tiết.", 
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            showDetailsDialog(id);
            return;
        }
        
        // Mở giao diện sửa phiếu nhập
        NhapHangMoiView nhapHangMoiView = new NhapHangMoiView(id);
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Sửa phiếu nhập #" + id, Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(1200, 800);
        dialog.setLocationRelativeTo(this);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        // Thêm nút đóng
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeButton = new JButton("Đóng");
        closeButton.addActionListener(e -> {
            dialog.dispose();
            loadData(); // Làm mới danh sách sau khi đóng
        });
        buttonPanel.add(closeButton);
        
        // Tạo panel chính với layout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(nhapHangMoiView, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Thêm panel chính vào dialog
        dialog.setContentPane(mainPanel);
        
        dialog.setVisible(true);
    }
    
    private void performConfirm() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn phiếu nhập cần xác nhận!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int id = (Integer) tableModel.getValueAt(selectedRow, 0);
        String trangThai = (String) tableModel.getValueAt(selectedRow, 5);
        
        if ("Đã xác nhận".equalsIgnoreCase(trangThai)) {
            JOptionPane.showMessageDialog(this, "Phiếu nhập này đã được xác nhận!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int result = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc chắn muốn xác nhận phiếu nhập #" + id + "?", 
            "Xác nhận phiếu nhập", JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            if (nhapHangDAO.xacNhanPhieuNhap(id)) {
                JOptionPane.showMessageDialog(this, "Xác nhận thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi xác nhận phiếu nhập!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void performDelete() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn phiếu nhập cần xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int id = (Integer) tableModel.getValueAt(selectedRow, 0);
        
        int result = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc chắn muốn xóa phiếu nhập #" + id + "?", 
            "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            if (nhapHangDAO.xoaPhieuNhap(id)) {
                JOptionPane.showMessageDialog(this, "Xóa thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi xóa phiếu nhập!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void showDetailsDialog() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn phiếu nhập cần xem chi tiết!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int id = (Integer) tableModel.getValueAt(selectedRow, 0);
        showDetailsDialog(id);
    }
    
    private void showDetailsDialog(int id) {
        ChiTietPhieuNhapDialog dialog = new ChiTietPhieuNhapDialog(SwingUtilities.getWindowAncestor(this), "Chi tiết phiếu nhập #" + id, id);
        dialog.setVisible(true);
        if (dialog.isDataChanged()) {
            loadData();
        }
    }
    
    
    // Inner class for Add/Edit dialog
    private class NhapHangDialog extends JDialog {
        private JTextField maNVField, maNCCField, ngayField, thanhTienField;
        private JComboBox<String> trangThaiCombo;
        private boolean dataChanged = false;
        private NhapHangDTO nh;
        
        public NhapHangDialog(Window parent, String title, NhapHangDTO nh) {
            super(parent, title, ModalityType.APPLICATION_MODAL);
            this.nh = nh;
            initializeComponents();
            setupLayout();
            setupEventHandlers();
        }
        
        private void initializeComponents() {
            setSize(450, 400);
            setLocationRelativeTo(getParent());
            
            maNVField = new JTextField(20);
            maNCCField = new JTextField(20);
            ngayField = new JTextField(20);
            thanhTienField = new JTextField(20);
            trangThaiCombo = new JComboBox<>(new String[]{"Chưa xác nhận", "Đã xác nhận"});
            
            if (nh != null) {
                maNVField.setText(String.valueOf(nh.getMaNV()));
                maNCCField.setText(String.valueOf(nh.getMaNCC()));
                ngayField.setText(nh.getNgay());
                thanhTienField.setText(String.valueOf(nh.getThanhTien()));
                trangThaiCombo.setSelectedItem(convertTrangThaiToUI(nh.getTrangThai()));
            } else {
                // Mặc định cho phiếu nhập mới
                maNVField.setText(String.valueOf(database.Session.currentMaNV));
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                ngayField.setText(dateFormat.format(new java.util.Date()));
                trangThaiCombo.setSelectedItem("Chưa xác nhận");
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
            
            // Mã NCC
            gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.EAST;
            mainPanel.add(new JLabel("Mã nhà cung cấp:"), gbc);
            gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
            mainPanel.add(maNCCField, gbc);
            
            // Ngày
            gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.EAST;
            mainPanel.add(new JLabel("Ngày (yyyy-mm-dd):"), gbc);
            gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
            mainPanel.add(ngayField, gbc);
            
      
            
            // Thành tiền
            gbc.gridx = 0; gbc.gridy = 4; gbc.anchor = GridBagConstraints.EAST;
            mainPanel.add(new JLabel("Thành tiền (VNĐ):"), gbc);
            gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
            mainPanel.add(thanhTienField, gbc);
            
            // Trạng thái
            gbc.gridx = 0; gbc.gridy = 5; gbc.anchor = GridBagConstraints.EAST;
            mainPanel.add(new JLabel("Trạng thái:"), gbc);
            gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
            mainPanel.add(trangThaiCombo, gbc);
            
            // Buttons
            gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
            JPanel buttonPanel = new JPanel(new FlowLayout());
            
            JButton saveButton = new JButton("Lưu");
            saveButton.setBackground(new Color(34, 139, 34));
            saveButton.setForeground(Color.BLACK);
            saveButton.setFocusPainted(false);
            
            JButton cancelButton = new JButton("Hủy");
            cancelButton.setBackground(new Color(220, 220, 220));
            cancelButton.setFocusPainted(false);
            
            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);
            mainPanel.add(buttonPanel, gbc);
            
            add(mainPanel, BorderLayout.CENTER);
        }
        
        private void setupEventHandlers() {
            // Find buttons using the existing findButton method
            JButton saveButton = findButton("Lưu");
            JButton cancelButton = findButton("Hủy");
            
            if (saveButton != null) {
                saveButton.addActionListener(e -> saveData());
            }
            if (cancelButton != null) {
                cancelButton.addActionListener(e -> dispose());
            }
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
            String maNCCStr = maNCCField.getText().trim();
            String ngay = ngayField.getText().trim();
            String thanhTienStr = thanhTienField.getText().trim();
            String trangThai = (String) trangThaiCombo.getSelectedItem();
            
            if (maNVStr.isEmpty() || maNCCStr.isEmpty() || ngay.isEmpty() || thanhTienStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin bắt buộc!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int maNV, maNCC;
            long thanhTien;
            try {
                maNV = Integer.parseInt(maNVStr);
                maNCC = Integer.parseInt(maNCCStr);
                thanhTien = Long.parseLong(thanhTienStr);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Mã NV, mã NCC và thành tiền phải là số!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try (Connection conn = DBUtil.getConnection()) {
                if (nh == null) {
                    // Thêm mới
                    PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO phieunhap (MaNV, MaNCC, Ngay, ThanhTien, TrangThai) VALUES (?, ?, ?, ?, ?)");
                    ps.setInt(1, maNV);
                    ps.setInt(2, maNCC);
                    ps.setString(3, ngay);
                    ps.setLong(4, thanhTien);
                    ps.setString(5, convertTrangThaiToDatabase(trangThai));
                    ps.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Thêm thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    // Sửa
                    PreparedStatement ps = conn.prepareStatement(
                        "UPDATE phieunhap SET MaNV=?, MaNCC=?, Ngay=?, ThanhTien=?, TrangThai=? WHERE MaPN=?");
                    ps.setInt(1, maNV);
                    ps.setInt(2, maNCC);
                    ps.setString(3, ngay);
                    ps.setLong(4, thanhTien);
                    ps.setString(5, convertTrangThaiToDatabase(trangThai));
                    ps.setInt(6, nh.getMaPN());
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
    
    // Inner class for Import Receipt Details dialog
    private class ChiTietPhieuNhapDialog extends JDialog {
        private JTable chiTietTable;
        private DefaultTableModel chiTietTableModel;
        private int maPN;
        private JLabel tongTienLabel;
        
        public ChiTietPhieuNhapDialog(Window parent, String title, int maPN) {
            super(parent, title, ModalityType.APPLICATION_MODAL);
            this.maPN = maPN;
            initializeComponents();
            setupLayout();
            setupEventHandlers();
            loadChiTietData();
        }
        
        private void initializeComponents() {
            setSize(800, 600);
            setLocationRelativeTo(getParent());
            
            // Tạo table model cho chi tiết
            String[] columns = {"Mã NL", "Tên nguyên liệu", "Số lượng", "Đơn giá", "Đơn vị", "Thành tiền"};
            chiTietTableModel = new DefaultTableModel(columns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false; // Chỉ đọc
                }
            };
            
            chiTietTable = new JTable(chiTietTableModel);
            chiTietTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            chiTietTable.setRowHeight(25);
            chiTietTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
            chiTietTable.setFont(new Font("Arial", Font.PLAIN, 12));
            
            tongTienLabel = new JLabel("Tổng tiền: 0 VNĐ");
            tongTienLabel.setFont(new Font("Arial", Font.BOLD, 14));
            tongTienLabel.setForeground(new Color(220, 20, 60));
        }
        
        private void setupLayout() {
            setLayout(new BorderLayout());
            
            // Header panel
            JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            headerPanel.add(new JLabel("Chi tiết phiếu nhập #" + maPN));
            
            // Table panel
            JScrollPane scrollPane = new JScrollPane(chiTietTable);
            scrollPane.setBorder(BorderFactory.createTitledBorder("Danh sách nguyên liệu"));
            
            // Button panel
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            JButton addChiTietButton = new JButton("➕ Thêm nguyên liệu");
            addChiTietButton.setBackground(new Color(34, 139, 34));
            addChiTietButton.setForeground(Color.BLACK);
            addChiTietButton.setFocusPainted(false);
            
            JButton editChiTietButton = new JButton("✏️ Sửa");
            editChiTietButton.setBackground(new Color(255, 140, 0));
            editChiTietButton.setForeground(Color.BLACK);
            editChiTietButton.setFocusPainted(false);
            
            JButton deleteChiTietButton = new JButton("🗑️ Xóa");
            deleteChiTietButton.setBackground(new Color(220, 20, 60));
            deleteChiTietButton.setForeground(Color.BLACK);
            deleteChiTietButton.setFocusPainted(false);
            
            JButton closeButton = new JButton("❌ Đóng");
            closeButton.setBackground(new Color(128, 128, 128));
            closeButton.setForeground(Color.BLACK);
            closeButton.setFocusPainted(false);
            
            buttonPanel.add(addChiTietButton);
            buttonPanel.add(editChiTietButton);
            buttonPanel.add(deleteChiTietButton);
            buttonPanel.add(closeButton);
            
            // Footer panel
            JPanel footerPanel = new JPanel(new BorderLayout());
            footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            footerPanel.add(tongTienLabel, BorderLayout.WEST);
            
            // Layout
            add(headerPanel, BorderLayout.NORTH);
            add(scrollPane, BorderLayout.CENTER);
            add(buttonPanel, BorderLayout.SOUTH);
            add(footerPanel, BorderLayout.PAGE_END);
            
            // Event handlers
            addChiTietButton.addActionListener(e -> showAddChiTietDialog());
            editChiTietButton.addActionListener(e -> showEditChiTietDialog());
            deleteChiTietButton.addActionListener(e -> performDeleteChiTiet());
            closeButton.addActionListener(e -> dispose());
        }
        
        private void setupEventHandlers() {
        // Double click to edit
        chiTietTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    showEditChiTietDialog();
                }
            }
        });
        }
        
        private void loadChiTietData() {
            chiTietTableModel.setRowCount(0);
            try {
                List<ChiTietNhapHangDTO> chiTietList = nhapHangDAO.layChiTietPhieuNhap(maPN);
                long tongTien = 0;
                
                for (ChiTietNhapHangDTO chiTiet : chiTietList) {
                    Object[] row = {
                        chiTiet.getMaNL(),
                        chiTiet.getTenNL(),
                        chiTiet.getSoLuong(),
                        String.format("%,d", chiTiet.getDonGia()) + " VNĐ",
                        chiTiet.getDonVi(),
                        String.format("%,d", chiTiet.getThanhTien()) + " VNĐ"
                    };
                    chiTietTableModel.addRow(row);
                    tongTien += chiTiet.getThanhTien();
                }
                
                tongTienLabel.setText("Tổng tiền: " + String.format("%,d", tongTien) + " VNĐ");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi tải chi tiết: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
        
        private void showAddChiTietDialog() {
            ChiTietDialog dialog = new ChiTietDialog(this, "Thêm nguyên liệu", maPN, null);
            dialog.setVisible(true);
            if (dialog.isDataChanged()) {
                loadChiTietData();
                // Refresh main table
                loadData();
            }
        }
        
        private void showEditChiTietDialog() {
            int selectedRow = chiTietTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn nguyên liệu cần sửa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int maNL = (Integer) chiTietTableModel.getValueAt(selectedRow, 0);
            ChiTietNhapHangDTO chiTiet = nhapHangDAO.layChiTietPhieuNhapTheoMa(maPN, maNL);
            
            if (chiTiet != null) {
                ChiTietDialog dialog = new ChiTietDialog(this, "Sửa nguyên liệu", maPN, chiTiet);
                dialog.setVisible(true);
                if (dialog.isDataChanged()) {
                    loadChiTietData();
                    // Refresh main table
                    loadData();
                }
            }
        }
        
        private void performDeleteChiTiet() {
            int selectedRow = chiTietTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn nguyên liệu cần xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int maNL = (Integer) chiTietTableModel.getValueAt(selectedRow, 0);
            String tenNL = (String) chiTietTableModel.getValueAt(selectedRow, 1);
            
            int result = JOptionPane.showConfirmDialog(this, 
                "Bạn có chắc chắn muốn xóa nguyên liệu \"" + tenNL + "\"?", 
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
            
            if (result == JOptionPane.YES_OPTION) {
                if (nhapHangDAO.xoaChiTietPhieuNhap(maPN, maNL)) {
                    JOptionPane.showMessageDialog(this, "Xóa thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    loadChiTietData();
                    // Refresh main table
                    loadData();
                } else {
                    JOptionPane.showMessageDialog(this, "Lỗi khi xóa nguyên liệu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        
        public boolean isDataChanged() {
            return false; // ChiTietPhieuNhapDialog không cần theo dõi thay đổi
        }
        
        // Inner class for Add/Edit ChiTiet dialog
        private class ChiTietDialog extends JDialog {
            private JTextField maNLField, soLuongField, donGiaField, donViField;
            private boolean dataChanged = false;
            private ChiTietNhapHangDTO chiTiet;
            private int maPN;
            
            public ChiTietDialog(Window parent, String title, int maPN, ChiTietNhapHangDTO chiTiet) {
                super(parent, title, ModalityType.APPLICATION_MODAL);
                this.maPN = maPN;
                this.chiTiet = chiTiet;
                initializeComponents();
                setupLayout();
                setupEventHandlers();
            }
            
            private void initializeComponents() {
                setSize(400, 300);
                setLocationRelativeTo(getParent());
                
                maNLField = new JTextField(20);
                soLuongField = new JTextField(20);
                donGiaField = new JTextField(20);
                donViField = new JTextField(20);
                
                if (chiTiet != null) {
                    maNLField.setText(String.valueOf(chiTiet.getMaNL()));
                    maNLField.setEditable(false); // Không cho sửa mã NL khi edit
                    soLuongField.setText(String.valueOf(chiTiet.getSoLuong()));
                    donGiaField.setText(String.valueOf(chiTiet.getDonGia()));
                    donViField.setText(chiTiet.getDonVi());
                }
            }
            
            private void setupLayout() {
                setLayout(new BorderLayout());
                
                JPanel mainPanel = new JPanel(new GridBagLayout());
                mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
                
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.insets = new Insets(10, 10, 10, 10);
                
                // Mã NL
                gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST;
                mainPanel.add(new JLabel("Mã nguyên liệu:"), gbc);
                gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
                mainPanel.add(maNLField, gbc);
                
                // Số lượng
                gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.EAST;
                mainPanel.add(new JLabel("Số lượng:"), gbc);
                gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
                mainPanel.add(soLuongField, gbc);
                
                // Đơn giá
                gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.EAST;
                mainPanel.add(new JLabel("Đơn giá (VNĐ):"), gbc);
                gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
                mainPanel.add(donGiaField, gbc);
                
                // Đơn vị
                gbc.gridx = 0; gbc.gridy = 3; gbc.anchor = GridBagConstraints.EAST;
                mainPanel.add(new JLabel("Đơn vị:"), gbc);
                gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
                mainPanel.add(donViField, gbc);
                
                // Buttons
                gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
                JPanel buttonPanel = new JPanel(new FlowLayout());
                
                JButton saveButton = new JButton("Lưu");
                saveButton.setBackground(new Color(34, 139, 34));
                saveButton.setForeground(Color.BLACK);
                saveButton.setFocusPainted(false);
                
                JButton cancelButton = new JButton("Hủy");
                cancelButton.setBackground(new Color(220, 220, 220));
                cancelButton.setFocusPainted(false);
                
                buttonPanel.add(saveButton);
                buttonPanel.add(cancelButton);
                mainPanel.add(buttonPanel, gbc);
                
                add(mainPanel, BorderLayout.CENTER);
                
                // Event handlers
                saveButton.addActionListener(e -> saveChiTietData());
                cancelButton.addActionListener(e -> dispose());
            }
            
            private void setupEventHandlers() {
                // Auto-calculate total when quantity or price changes
                soLuongField.addActionListener(e -> calculateTotal());
                donGiaField.addActionListener(e -> calculateTotal());
            }
            
        private void calculateTotal() {
            try {
                int soLuong = Integer.parseInt(soLuongField.getText());
                long donGia = Long.parseLong(donGiaField.getText());
                // Could show total in a label if needed
                // long thanhTien = soLuong * donGia;
            } catch (NumberFormatException e) {
                // Ignore invalid input
            }
        }
            
            private void saveChiTietData() {
                String maNLStr = maNLField.getText().trim();
                String soLuongStr = soLuongField.getText().trim();
                String donGiaStr = donGiaField.getText().trim();
                String donVi = donViField.getText().trim();
                
                if (maNLStr.isEmpty() || soLuongStr.isEmpty() || donGiaStr.isEmpty() || donVi.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                int maNL;
                int soLuong;
                long donGia;
                try {
                    maNL = Integer.parseInt(maNLStr);
                    soLuong = Integer.parseInt(soLuongStr);
                    donGia = Long.parseLong(donGiaStr);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Mã NL, số lượng và đơn giá phải là số!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (soLuong <= 0 || donGia <= 0) {
                    JOptionPane.showMessageDialog(this, "Số lượng và đơn giá phải lớn hơn 0!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                try {
                    if (chiTiet == null) {
                        // Thêm mới
                        if (nhapHangDAO.themChiTietPhieuNhap(maPN, maNL, soLuong, donGia, donVi)) {
                            JOptionPane.showMessageDialog(this, "Thêm nguyên liệu thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                            dataChanged = true;
                            dispose();
                        } else {
                            JOptionPane.showMessageDialog(this, "Lỗi khi thêm nguyên liệu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        // Sửa
                        chiTiet.setSoLuong(soLuong);
                        chiTiet.setDonGia(donGia);
                        chiTiet.setDonVi(donVi);
                        chiTiet.tinhLaiThanhTien();
                        
                        if (nhapHangDAO.capNhatChiTietPhieuNhap(chiTiet)) {
                            JOptionPane.showMessageDialog(this, "Sửa nguyên liệu thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                            dataChanged = true;
                            dispose();
                        } else {
                            JOptionPane.showMessageDialog(this, "Lỗi khi sửa nguyên liệu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
            
            public boolean isDataChanged() {
                return dataChanged;
            }
        }
    }
    
    // Method chuyển đổi trạng thái từ database sang giao diện
    private String convertTrangThaiToUI(String trangThaiDB) {
        if ("daxacnhan".equals(trangThaiDB)) {
            return "Đã xác nhận";
        } else if ("chuaxacnhan".equals(trangThaiDB)) {
            return "Chưa xác nhận";
        }
        return "Chưa xác nhận"; // Mặc định
    }
    
    // Method chuyển đổi trạng thái từ giao diện sang database
    private String convertTrangThaiToDatabase(String trangThaiUI) {
        if ("Đã xác nhận".equals(trangThaiUI)) {
            return "daxacnhan";
        } else if ("Chưa xác nhận".equals(trangThaiUI)) {
            return "chuaxacnhan";
        }
        return "chuaxacnhan"; // Mặc định
    }
}
