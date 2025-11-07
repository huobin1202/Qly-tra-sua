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
import utils.DateChooserComponent;

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
        searchCombo = new JComboBox<>(new String[]{"ID", "Nhân viên", "Nhà cung cấp"});
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
            
            if (searchText.isEmpty()) {
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
            } else {
                sql += "ncc.TenNCC LIKE ? ORDER BY p.MaPN";
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
        // Mở giao diện nhập hàng mới (ThemNhapHangView)
        ThemNhapHangView nhapHangMoiView = new ThemNhapHangView();
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
        ThemNhapHangView nhapHangMoiView = new ThemNhapHangView(id);
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
        try {
            // Lấy thông tin phiếu nhập với tên nhân viên và nhà cung cấp
            String tenNV = "N/A";
            String tenNCC = "N/A";
            String ngayNhap = "";
            String trangThai = "Chưa xác nhận";
            
            try (Connection conn = DBUtil.getConnection()) {
                String sql = "SELECT p.*, nv.HoTen as TenNV, ncc.TenNCC " +
                           "FROM phieunhap p " +
                           "LEFT JOIN nhanvien nv ON p.MaNV = nv.MaNV " +
                           "LEFT JOIN nhacungcap ncc ON p.MaNCC = ncc.MaNCC " +
                           "WHERE p.MaPN = ?";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, id);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            tenNV = rs.getString("TenNV") != null ? rs.getString("TenNV") : "N/A";
                            tenNCC = rs.getString("TenNCC") != null ? rs.getString("TenNCC") : "N/A";
                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                            if (rs.getDate("Ngay") != null) {
                                ngayNhap = dateFormat.format(rs.getDate("Ngay"));
                            }
                            trangThai = convertTrangThaiToUI(rs.getString("TrangThai"));
                        } else {
                            JOptionPane.showMessageDialog(this, "Không tìm thấy phiếu nhập!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    }
                }
            }
            
            // Lấy chi tiết phiếu nhập
            List<ChiTietNhapHangDTO> chiTietList = nhapHangDAO.layChiTietPhieuNhap(id);
            
            StringBuilder detail = new StringBuilder();
            
            // Header
            detail.append("╔══════════════════════════════════════════════════════════════════════════════════════╗\n");
            detail.append("║                                    PHIẾU NHẬP HÀNG                                    ║\n");
            detail.append("║                                        #").append(String.format("%-6d", id)).append("                                        ║\n");
            detail.append("╠══════════════════════════════════════════════════════════════════════════════════════╣\n");
            
            // Thông tin phiếu nhập
            detail.append("║ Mã phiếu nhập: ").append(String.format("%-20s", id)).append(" Ngày nhập: ").append(String.format("%-20s", ngayNhap)).append(" ║\n");
            detail.append("║ Nhân viên: ").append(String.format("%-20s", tenNV)).append(" ║\n");
            detail.append("║ Nhà cung cấp: ").append(String.format("%-20s", tenNCC)).append(" Trạng thái: ").append(String.format("%-20s", trangThai)).append(" ║\n");
            
            detail.append("╠══════════════════════════════════════════════════════════════════════════════════════╣\n");
            detail.append("║                                    CHI TIẾT NGUYÊN LIỆU                              ║\n");
            detail.append("╠══════════════════════════════════════════════════════════════════════════════════════╣\n");
            detail.append("║ STT │ Mã NL │ Tên nguyên liệu        │ Số lượng │ Đơn giá      │ Đơn vị │ Thành tiền    ║\n");
            detail.append("╠══════════════════════════════════════════════════════════════════════════════════════╣\n");
            
            // Chi tiết nguyên liệu
            if (chiTietList.isEmpty()) {
                detail.append("║ ").append("                                ").append("Không có chi tiết nguyên liệu").append("                                ").append(" ║\n");
            } else {
                int stt = 1;
                long tongTien = 0;
                for (ChiTietNhapHangDTO chiTiet : chiTietList) {
                    long thanhTien = chiTiet.getThanhTien();
                    tongTien += thanhTien;
                    
                    String tenNL = chiTiet.getTenNL();
                    if (tenNL.length() > 20) {
                        tenNL = tenNL.substring(0, 17) + "...";
                    }
                    
                    detail.append(String.format("║ %-3d │ %-5d │ %-22s │ %-8d │ %-12s │ %-6s │ %-13s ║\n",
                        stt++,
                        chiTiet.getMaNL(),
                        tenNL,
                        chiTiet.getSoLuong(),
                        String.format("%,d VNĐ", chiTiet.getDonGia()),
                        chiTiet.getDonVi(),
                        String.format("%,d VNĐ", thanhTien)
                    ));
                }
                
                detail.append("╠══════════════════════════════════════════════════════════════════════════════════════╣\n");
                detail.append("║ ").append("                                ").append("TỔNG TIỀN: ").append(String.format("%-20s", String.format("%,d VNĐ", tongTien))).append("                                ").append(" ║\n");
            }
            
            detail.append("╚══════════════════════════════════════════════════════════════════════════════════════╝\n");
            
            JTextArea textArea = new JTextArea(detail.toString());
            textArea.setEditable(false);
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(500, 400));
            
            // Tạo panel chứa text area và buttons
            JPanel mainPanel = new JPanel(new BorderLayout());
            mainPanel.add(scrollPane, BorderLayout.CENTER);
            
            // Panel chứa các nút
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
            
            JButton printButton = new JButton("🖨️ In phiếu nhập");
            printButton.setBackground(new Color(70, 130, 180));
            printButton.setForeground(Color.BLACK);
            printButton.setFocusPainted(false);
            printButton.addActionListener(e -> printPhieuNhap(detail.toString(), id));
            
            JButton exportButton = new JButton("💾 Xuất file");
            exportButton.setBackground(new Color(34, 139, 34));
            exportButton.setForeground(Color.BLACK);
            exportButton.setFocusPainted(false);
            exportButton.addActionListener(e -> exportPhieuNhap(detail.toString(), id));
            
            buttonPanel.add(printButton);
            buttonPanel.add(exportButton);
            
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);
            
            JOptionPane.showMessageDialog(this, mainPanel, "Chi tiết phiếu nhập", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải chi tiết: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void printPhieuNhap(String content, int maPN) {
        try {
            // Tạo một JTextArea để in
            JTextArea printArea = new JTextArea(content);
            printArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            printArea.print();
            
            JOptionPane.showMessageDialog(this, "In phiếu nhập thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi in phiếu nhập: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void exportPhieuNhap(String content, int maPN) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Xuất phiếu nhập");
        fileChooser.setSelectedFile(new java.io.File("PhieuNhap_" + maPN + "_" + 
            new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date()) + ".txt"));
        
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try (java.io.FileWriter writer = new java.io.FileWriter(fileChooser.getSelectedFile())) {
                writer.write(content);
                JOptionPane.showMessageDialog(this, "Xuất phiếu nhập thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } catch (java.io.IOException e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xuất file: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    
    // Inner class for Add/Edit dialog
    private class NhapHangDialog extends JDialog {
        private JTextField maNVField, maNCCField, thanhTienField;
        private DateChooserComponent ngayPicker;
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
            ngayPicker = new DateChooserComponent();
            thanhTienField = new JTextField(20);
            trangThaiCombo = new JComboBox<>(new String[]{"Chưa xác nhận", "Đã xác nhận"});
            
            if (nh != null) {
                maNVField.setText(String.valueOf(nh.getMaNV()));
                maNCCField.setText(String.valueOf(nh.getMaNCC()));
                ngayPicker.setDate(java.sql.Date.valueOf(nh.getNgay()));
                thanhTienField.setText(String.valueOf(nh.getThanhTien()));
                trangThaiCombo.setSelectedItem(convertTrangThaiToUI(nh.getTrangThai()));
            } else {
                // Mặc định cho phiếu nhập mới
                maNVField.setText(String.valueOf(database.Session.currentMaNV));
                ngayPicker.setCurrentDate();
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
            mainPanel.add(new JLabel("Ngày:"), gbc);
            gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
            mainPanel.add(ngayPicker, gbc);
            
      
            
            // Thành tiền
            gbc.gridx = 0; gbc.gridy = 3; gbc.anchor = GridBagConstraints.EAST;
            mainPanel.add(new JLabel("Thành tiền (VNĐ):"), gbc);
            gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
            mainPanel.add(thanhTienField, gbc);
            
            // Trạng thái
            gbc.gridx = 0; gbc.gridy = 4; gbc.anchor = GridBagConstraints.EAST;
            mainPanel.add(new JLabel("Trạng thái:"), gbc);
            gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
            mainPanel.add(trangThaiCombo, gbc);
            
            // Buttons
            gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
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
            String ngay = ngayPicker.getSelectedDateString();
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
            
            JButton printButton = new JButton("🖨️ In phiếu nhập");
            printButton.setBackground(new Color(70, 130, 180));
            printButton.setForeground(Color.BLACK);
            printButton.setFocusPainted(false);
            
            JButton exportButton = new JButton("💾 Xuất file");
            exportButton.setBackground(new Color(34, 139, 34));
            exportButton.setForeground(Color.BLACK);
            exportButton.setFocusPainted(false);
            
            JButton closeButton = new JButton("❌ Đóng");
            closeButton.setBackground(new Color(128, 128, 128));
            closeButton.setForeground(Color.BLACK);
            closeButton.setFocusPainted(false);
            
            buttonPanel.add(addChiTietButton);
            buttonPanel.add(editChiTietButton);
            buttonPanel.add(deleteChiTietButton);
            buttonPanel.add(printButton);
            buttonPanel.add(exportButton);
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
            printButton.addActionListener(e -> printPhieuNhap());
            exportButton.addActionListener(e -> exportPhieuNhap());
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
        
        private void printPhieuNhap() {
            try {
                // Tạo dialog preview trước khi in
                String content = generatePhieuNhapContent();
                
                JDialog previewDialog = new JDialog(this, "Preview - Phiếu nhập #" + maPN, ModalityType.APPLICATION_MODAL);
                previewDialog.setSize(800, 600);
                previewDialog.setLocationRelativeTo(this);
                
                JTextArea previewArea = new JTextArea(content);
                previewArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
                previewArea.setEditable(false);
                previewArea.setBackground(Color.WHITE);
                
                JScrollPane scrollPane = new JScrollPane(previewArea);
                scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                
                JPanel buttonPanel = new JPanel(new FlowLayout());
                JButton printButton = new JButton("🖨️ In");
                printButton.setBackground(new Color(70, 130, 180));
                printButton.setForeground(Color.BLACK);
                printButton.setFocusPainted(false);
                printButton.addActionListener(e -> {
                    try {
                        previewArea.print();
                        JOptionPane.showMessageDialog(previewDialog, "In phiếu nhập thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                        previewDialog.dispose();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(previewDialog, "Lỗi khi in: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                });
                
                JButton cancelButton = new JButton("Hủy");
                cancelButton.addActionListener(e -> previewDialog.dispose());
                
                buttonPanel.add(printButton);
                buttonPanel.add(cancelButton);
                
                previewDialog.setLayout(new BorderLayout());
                previewDialog.add(scrollPane, BorderLayout.CENTER);
                previewDialog.add(buttonPanel, BorderLayout.SOUTH);
                previewDialog.setVisible(true);
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi in phiếu nhập: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
        
        private String generatePhieuNhapContent() {
            StringBuilder content = new StringBuilder();
            
            // Header
            content.append("╔══════════════════════════════════════════════════════════════════════════════════════╗\n");
            content.append("║                                    PHIẾU NHẬP HÀNG                                    ║\n");
            content.append("║                                        #").append(String.format("%-6d", maPN)).append("                                        ║\n");
            content.append("╠══════════════════════════════════════════════════════════════════════════════════════╣\n");
            
            // Lấy thông tin phiếu nhập
            try (Connection conn = DBUtil.getConnection()) {
                String sql = "SELECT p.*, nv.HoTen as TenNV, ncc.TenNCC " +
                           "FROM phieunhap p " +
                           "LEFT JOIN nhanvien nv ON p.MaNV = nv.MaNV " +
                           "LEFT JOIN nhacungcap ncc ON p.MaNCC = ncc.MaNCC " +
                           "WHERE p.MaPN = ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, maPN);
                ResultSet rs = ps.executeQuery();
                
                if (rs.next()) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    String ngayNhap = rs.getDate("Ngay") != null ? dateFormat.format(rs.getDate("Ngay")) : "N/A";
                    content.append("║ Mã phiếu nhập: ").append(String.format("%-20s", rs.getInt("MaPN"))).append(" Ngày nhập: ").append(String.format("%-20s", ngayNhap)).append(" ║\n");
                    content.append("║ Nhân viên:     ").append(String.format("%-20s", rs.getString("TenNV") != null ? rs.getString("TenNV") : "N/A")).append(" Trạng thái: ").append(String.format("%-20s", convertTrangThaiToUI(rs.getString("TrangThai")))).append(" ║\n");
                    content.append("║ Nhà cung cấp: ").append(String.format("%-20s", rs.getString("TenNCC") != null ? rs.getString("TenNCC") : "N/A")).append(" ").append("                                ").append(" ║\n");
                }
                rs.close();
                ps.close();
            } catch (SQLException e) {
                throw new RuntimeException("Lỗi khi lấy thông tin phiếu nhập: " + e.getMessage(), e);
            }
            
            content.append("╠══════════════════════════════════════════════════════════════════════════════════════╣\n");
            content.append("║                                    CHI TIẾT NGUYÊN LIỆU                              ║\n");
            content.append("╠══════════════════════════════════════════════════════════════════════════════════════╣\n");
            content.append("║ STT │ Mã NL │ Tên nguyên liệu        │ Số lượng │ Đơn giá      │ Đơn vị │ Thành tiền    ║\n");
            content.append("╠══════════════════════════════════════════════════════════════════════════════════════╣\n");
            
            // Lấy chi tiết nguyên liệu
            try (Connection conn = DBUtil.getConnection()) {
                String sql = "SELECT ct.*, nl.TenNL, nl.DonVi " +
                           "FROM chitietnhap_nl ct " +
                           "JOIN nguyenlieu nl ON ct.MaNL = nl.MaNL " +
                           "WHERE ct.MaPN = ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, maPN);
                ResultSet rs = ps.executeQuery();
                
                long tongTien = 0;
                int stt = 1;
                while (rs.next()) {
                    long thanhTien = rs.getLong("SoLuong") * rs.getLong("DonGia");
                    tongTien += thanhTien;
                    
                    String tenNL = rs.getString("TenNL");
                    if (tenNL.length() > 20) {
                        tenNL = tenNL.substring(0, 17) + "...";
                    }
                    
                    content.append(String.format("║ %-3d │ %-5d │ %-22s │ %-8d │ %-12s │ %-6s │ %-13s ║\n",
                        stt++,
                        rs.getInt("MaNL"),
                        tenNL,
                        rs.getInt("SoLuong"),
                        String.format("%,d VNĐ", rs.getLong("DonGia")),
                        rs.getString("DonVi"),
                        String.format("%,d VNĐ", thanhTien)
                    ));
                }
                rs.close();
                ps.close();
                
                content.append("╠══════════════════════════════════════════════════════════════════════════════════════╣\n");
                content.append("║ ").append("                                ").append("TỔNG TIỀN: ").append(String.format("%-20s", String.format("%,d VNĐ", tongTien))).append("                                ").append(" ║\n");
                content.append("╚══════════════════════════════════════════════════════════════════════════════════════╝\n");
            } catch (SQLException e) {
                throw new RuntimeException("Lỗi khi lấy chi tiết nguyên liệu: " + e.getMessage(), e);
            }
            
            return content.toString();
        }
        
        private void exportPhieuNhap() {
            // Tạo dialog chọn định dạng xuất
            String[] formats = {"TXT (Text)", "CSV (Excel)", "HTML"};
            String selectedFormat = (String) JOptionPane.showInputDialog(
                this,
                "Chọn định dạng xuất file:",
                "Xuất phiếu nhập",
                JOptionPane.QUESTION_MESSAGE,
                null,
                formats,
                formats[0]
            );
            
            if (selectedFormat == null) {
                return;
            }
            
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Xuất phiếu nhập");
            
            String extension = ".txt";
            if (selectedFormat.contains("CSV")) {
                extension = ".csv";
            } else if (selectedFormat.contains("HTML")) {
                extension = ".html";
            }
            
            fileChooser.setSelectedFile(new java.io.File("PhieuNhap_" + maPN + "_" + 
                new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date()) + extension));
            
            int result = fileChooser.showSaveDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                try {
                    java.io.File selectedFile = fileChooser.getSelectedFile();
                    String filePath = selectedFile.getAbsolutePath();
                    
                    // Đảm bảo có extension đúng
                    if (!filePath.toLowerCase().endsWith(extension)) {
                        filePath += extension;
                        selectedFile = new java.io.File(filePath);
                    }
                    
                    if (selectedFormat.contains("CSV")) {
                        exportToCSV(selectedFile);
                    } else if (selectedFormat.contains("HTML")) {
                        exportToHTML(selectedFile);
                    } else {
                        exportToTXT(selectedFile);
                    }
                    
                    JOptionPane.showMessageDialog(this, 
                        "Xuất phiếu nhập thành công!\nFile đã được lưu tại:\n" + selectedFile.getAbsolutePath(), 
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Lỗi khi xuất file: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        
        private void exportToTXT(java.io.File file) throws Exception {
            try (java.io.FileWriter writer = new java.io.FileWriter(file)) {
                writer.write(generatePhieuNhapContent());
            }
        }
        
        private void exportToCSV(java.io.File file) throws Exception {
            try (java.io.FileWriter writer = new java.io.FileWriter(file)) {
                // Header CSV
                writer.write("PHIẾU NHẬP HÀNG #" + maPN + "\n");
                writer.write("\n");
                
                // Thông tin phiếu nhập
                try (Connection conn = DBUtil.getConnection()) {
                    String sql = "SELECT p.*, nv.HoTen as TenNV, ncc.TenNCC " +
                               "FROM phieunhap p " +
                               "LEFT JOIN nhanvien nv ON p.MaNV = nv.MaNV " +
                               "LEFT JOIN nhacungcap ncc ON p.MaNCC = ncc.MaNCC " +
                               "WHERE p.MaPN = ?";
                    PreparedStatement ps = conn.prepareStatement(sql);
                    ps.setInt(1, maPN);
                    ResultSet rs = ps.executeQuery();
                    
                    if (rs.next()) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        String ngayNhap = rs.getDate("Ngay") != null ? dateFormat.format(rs.getDate("Ngay")) : "N/A";
                        writer.write("Mã phiếu nhập," + rs.getInt("MaPN") + "\n");
                        writer.write("Ngày nhập," + ngayNhap + "\n");
                        writer.write("Nhân viên," + (rs.getString("TenNV") != null ? rs.getString("TenNV") : "N/A") + "\n");
                        writer.write("Nhà cung cấp," + (rs.getString("TenNCC") != null ? rs.getString("TenNCC") : "N/A") + "\n");
                        writer.write("Trạng thái," + convertTrangThaiToUI(rs.getString("TrangThai")) + "\n");
                    }
                    rs.close();
                    ps.close();
                }
                
                writer.write("\n");
                writer.write("CHI TIẾT NGUYÊN LIỆU\n");
                writer.write("STT,Mã NL,Tên nguyên liệu,Số lượng,Đơn giá,Đơn vị,Thành tiền\n");
                
                // Chi tiết
                try (Connection conn = DBUtil.getConnection()) {
                    String sql = "SELECT ct.*, nl.TenNL, nl.DonVi " +
                               "FROM chitietnhap_nl ct " +
                               "JOIN nguyenlieu nl ON ct.MaNL = nl.MaNL " +
                               "WHERE ct.MaPN = ?";
                    PreparedStatement ps = conn.prepareStatement(sql);
                    ps.setInt(1, maPN);
                    ResultSet rs = ps.executeQuery();
                    
                    long tongTien = 0;
                    int stt = 1;
                    while (rs.next()) {
                        long thanhTien = rs.getLong("SoLuong") * rs.getLong("DonGia");
                        tongTien += thanhTien;
                        
                        writer.write(String.format("%d,%d,\"%s\",%d,%d,\"%s\",%d\n",
                            stt++,
                            rs.getInt("MaNL"),
                            rs.getString("TenNL"),
                            rs.getInt("SoLuong"),
                            rs.getLong("DonGia"),
                            rs.getString("DonVi"),
                            thanhTien
                        ));
                    }
                    rs.close();
                    ps.close();
                    
                    writer.write("\n");
                    writer.write("TỔNG TIỀN," + tongTien + "\n");
                }
            }
        }
        
        private void exportToHTML(java.io.File file) throws Exception {
            try (java.io.FileWriter writer = new java.io.FileWriter(file)) {
                writer.write("<!DOCTYPE html>\n");
                writer.write("<html><head><meta charset='UTF-8'>\n");
                writer.write("<title>Phiếu nhập hàng #" + maPN + "</title>\n");
                writer.write("<style>\n");
                writer.write("body { font-family: Arial, sans-serif; margin: 20px; }\n");
                writer.write("h1 { text-align: center; color: #4682B4; }\n");
                writer.write("table { width: 100%; border-collapse: collapse; margin: 20px 0; }\n");
                writer.write("th, td { border: 1px solid #ddd; padding: 10px; text-align: left; }\n");
                writer.write("th { background-color: #4682B4; color: white; }\n");
                writer.write(".total { font-weight: bold; font-size: 18px; text-align: right; }\n");
                writer.write(".info { margin: 10px 0; }\n");
                writer.write("</style>\n</head><body>\n");
                
                writer.write("<h1>PHIẾU NHẬP HÀNG #" + maPN + "</h1>\n");
                
                // Thông tin phiếu nhập
                try (Connection conn = DBUtil.getConnection()) {
                    String sql = "SELECT p.*, nv.HoTen as TenNV, ncc.TenNCC " +
                               "FROM phieunhap p " +
                               "LEFT JOIN nhanvien nv ON p.MaNV = nv.MaNV " +
                               "LEFT JOIN nhacungcap ncc ON p.MaNCC = ncc.MaNCC " +
                               "WHERE p.MaPN = ?";
                    PreparedStatement ps = conn.prepareStatement(sql);
                    ps.setInt(1, maPN);
                    ResultSet rs = ps.executeQuery();
                    
                    if (rs.next()) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        String ngayNhap = rs.getDate("Ngay") != null ? dateFormat.format(rs.getDate("Ngay")) : "N/A";
                        writer.write("<div class='info'>\n");
                        writer.write("<p><strong>Mã phiếu nhập:</strong> " + rs.getInt("MaPN") + "</p>\n");
                        writer.write("<p><strong>Ngày nhập:</strong> " + ngayNhap + "</p>\n");
                        writer.write("<p><strong>Nhân viên:</strong> " + (rs.getString("TenNV") != null ? rs.getString("TenNV") : "N/A") + "</p>\n");
                        writer.write("<p><strong>Nhà cung cấp:</strong> " + (rs.getString("TenNCC") != null ? rs.getString("TenNCC") : "N/A") + "</p>\n");
                        writer.write("<p><strong>Trạng thái:</strong> " + convertTrangThaiToUI(rs.getString("TrangThai")) + "</p>\n");
                        writer.write("</div>\n");
                    }
                    rs.close();
                    ps.close();
                }
                
                writer.write("<h2>CHI TIẾT NGUYÊN LIỆU</h2>\n");
                writer.write("<table>\n");
                writer.write("<tr><th>STT</th><th>Mã NL</th><th>Tên nguyên liệu</th><th>Số lượng</th><th>Đơn giá</th><th>Đơn vị</th><th>Thành tiền</th></tr>\n");
                
                // Chi tiết
                try (Connection conn = DBUtil.getConnection()) {
                    String sql = "SELECT ct.*, nl.TenNL, nl.DonVi " +
                               "FROM chitietnhap_nl ct " +
                               "JOIN nguyenlieu nl ON ct.MaNL = nl.MaNL " +
                               "WHERE ct.MaPN = ?";
                    PreparedStatement ps = conn.prepareStatement(sql);
                    ps.setInt(1, maPN);
                    ResultSet rs = ps.executeQuery();
                    
                    long tongTien = 0;
                    int stt = 1;
                    while (rs.next()) {
                        long thanhTien = rs.getLong("SoLuong") * rs.getLong("DonGia");
                        tongTien += thanhTien;
                        
                        writer.write("<tr>");
                        writer.write("<td>" + stt++ + "</td>");
                        writer.write("<td>" + rs.getInt("MaNL") + "</td>");
                        writer.write("<td>" + rs.getString("TenNL") + "</td>");
                        writer.write("<td>" + rs.getInt("SoLuong") + "</td>");
                        writer.write("<td>" + String.format("%,d", rs.getLong("DonGia")) + " VNĐ</td>");
                        writer.write("<td>" + rs.getString("DonVi") + "</td>");
                        writer.write("<td>" + String.format("%,d", thanhTien) + " VNĐ</td>");
                        writer.write("</tr>\n");
                    }
                    rs.close();
                    ps.close();
                    
                    writer.write("<tr class='total'><td colspan='6'>TỔNG TIỀN</td><td>" + String.format("%,d", tongTien) + " VNĐ</td></tr>\n");
                }
                
                writer.write("</table>\n");
                writer.write("</body></html>");
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
