package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.List;
import database.DBUtil;
import dto.DonHangDTO;
import dto.ChiTietDonHangDTO;
import dao.DonHangDAO;

public class DonHangView extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> searchCombo;
    private MainFrameInterface parent;
    private DonHangDAO donHangDAO;
    
    public DonHangView(MainFrameInterface parent) {
        this.parent = parent;
        this.donHangDAO = new DonHangDAO();
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
        try {
            List<DonHangDTO> danhSach = donHangDAO.layTatCaDonHang();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            
            for (DonHangDTO donHang : danhSach) {
                Object[] row = {
                    donHang.getMaDon(),
                    donHang.getMaNV(),
                    donHang.getLoai(),
                    convertTrangThaiToUI(donHang.getTrangThai()),
                    donHang.getNgayDat() != null ? dateFormat.format(donHang.getNgayDat()) : "",
                    String.format("%,d", donHang.getTongTien()) + " VNĐ",
                    donHang.getGiamGia() + "%"
                };
                tableModel.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void performSearch() {
        String searchText = searchField.getText().trim();
        String searchType = (String) searchCombo.getSelectedItem();
        
        tableModel.setRowCount(0);
        try {
            List<DonHangDTO> danhSach = donHangDAO.timKiemDonHang(searchType, searchText);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            
            for (DonHangDTO donHang : danhSach) {
                Object[] row = {
                    donHang.getMaDon(),
                    donHang.getMaNV(),
                    donHang.getLoai(),
                    convertTrangThaiToUI(donHang.getTrangThai()),
                    donHang.getNgayDat() != null ? dateFormat.format(donHang.getNgayDat()) : "",
                    String.format("%,d", donHang.getTongTien()) + " VNĐ",
                    donHang.getGiamGia() + "%"
                };
                tableModel.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tìm kiếm: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showAddDialog() {
        ThemDonHangView dialog = new ThemDonHangView(SwingUtilities.getWindowAncestor(this));
        dialog.setVisible(true);
        // Làm mới dữ liệu sau khi đóng dialog
        loadData();
    }
    
    private void showEditDialog() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đơn hàng cần sửa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int id = (Integer) tableModel.getValueAt(selectedRow, 0);
        String trangThai = (String) tableModel.getValueAt(selectedRow, 3);
        
        // Kiểm tra trạng thái thanh toán
        if ("Đã thanh toán".equals(trangThai) || "Bị hủy".equals(trangThai)) {
            JOptionPane.showMessageDialog(this, 
                "Đơn hàng đã thanh toán, không thể chỉnh sửa!\nChỉ có thể xem chi tiết.", 
                "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Mở giao diện sửa hóa đơn mới
        SuaDonHangView editDialog = new SuaDonHangView(SwingUtilities.getWindowAncestor(this), id);
        editDialog.setVisible(true);
        
        // Làm mới dữ liệu sau khi đóng dialog
        loadData();
    }
    
    private void showDetailDialog() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đơn hàng cần xem chi tiết!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int maDon = (Integer) tableModel.getValueAt(selectedRow, 0);
        
        try {
            // Lấy thông tin đơn hàng
            DonHangDTO donHang = donHangDAO.layDonHangVoiTenNV(maDon);
            if (donHang == null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy đơn hàng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Lấy chi tiết đơn hàng
            List<ChiTietDonHangDTO> chiTietList = donHangDAO.layChiTietDonHang(maDon);
            
            StringBuilder detail = new StringBuilder();
            
            // Header
            detail.append("╔══════════════════════════════════════════════════════════════════════════════════════╗\n");
            detail.append("║                                        HÓA ĐƠN                                          ║\n");
            detail.append("║                                        #").append(String.format("%-6d", maDon)).append("                                        ║\n");
            detail.append("╠══════════════════════════════════════════════════════════════════════════════════════╣\n");
            
            // Thông tin đơn hàng
            detail.append("║ Mã đơn hàng: ").append(String.format("%-20s", donHang.getMaDon())).append(" Ngày đặt: ").append(String.format("%-20s", donHang.getNgayDat())).append(" ║\n");
            detail.append("║ Mã nhân viên: ").append(String.format("%-20s", donHang.getMaNV())).append(" Loại: ").append(String.format("%-20s", donHang.getLoai())).append(" ║\n");
            detail.append("║ Trạng thái: ").append(String.format("%-20s", convertTrangThaiToUI(donHang.getTrangThai()))).append(" Giảm giá: ").append(String.format("%-20s", donHang.getGiamGia() + "%")).append(" ║\n");
            
            detail.append("╠══════════════════════════════════════════════════════════════════════════════════════╣\n");
            detail.append("║                                    CHI TIẾT MÓN ĂN                                    ║\n");
            detail.append("╠══════════════════════════════════════════════════════════════════════════════════════╣\n");
            detail.append("║ STT │ Tên món ăn              │ Topping           │ Số lượng │ Đơn giá      │ Thành tiền    ║\n");
            detail.append("╠══════════════════════════════════════════════════════════════════════════════════════╣\n");
            
            // Chi tiết món
            if (chiTietList.isEmpty()) {
                detail.append("║ ").append("                                ").append("Không có chi tiết món").append("                                ").append(" ║\n");
            } else {
                int stt = 1;
                long tongTien = 0;
                for (ChiTietDonHangDTO chiTiet : chiTietList) {
                    long thanhTien = (chiTiet.getGiaMon() + chiTiet.getGiaTopping()) * chiTiet.getSoLuong();
                    tongTien += thanhTien;
                    
                    String tenMon = chiTiet.getTenMon();
                    if (tenMon.length() > 20) {
                        tenMon = tenMon.substring(0, 17) + "...";
                    }
                    
                    String toppingName = chiTiet.getTenTopping();
                    if (toppingName == null || toppingName.isEmpty()) {
                        toppingName = "Không";
                    } else if (toppingName.length() > 15) {
                        toppingName = toppingName.substring(0, 12) + "...";
                    }
                    
                    detail.append(String.format("║ %-3d │ %-22s │ %-17s │ %-8d │ %-12s │ %-13s ║\n",
                        stt++,
                        tenMon,
                        toppingName,
                        chiTiet.getSoLuong(),
                        String.format("%,d VNĐ", chiTiet.getGiaMon() + chiTiet.getGiaTopping()),
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
            
            JButton printButton = new JButton("🖨️ In hóa đơn");
            printButton.setBackground(new Color(70, 130, 180));
            printButton.setForeground(Color.BLACK);
            printButton.setFocusPainted(false);
            printButton.addActionListener(e -> printInvoice(detail.toString(), maDon));
            
            JButton exportButton = new JButton("💾 Xuất file");
            exportButton.setBackground(new Color(34, 139, 34));
            exportButton.setForeground(Color.BLACK);
            exportButton.setFocusPainted(false);
            exportButton.addActionListener(e -> exportInvoice(detail.toString(), maDon));
            
            buttonPanel.add(printButton);
            buttonPanel.add(exportButton);
            
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);
            
            JOptionPane.showMessageDialog(this, mainPanel, "Chi tiết đơn hàng", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (Exception e) {
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
            try {
                boolean success = donHangDAO.xoaDonHang(id);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Xóa thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    loadData();
                } else {
                    JOptionPane.showMessageDialog(this, "Không thể xóa đơn hàng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi xóa dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // Inner class for Add/Edit dialog
    private class DonHangDialog extends JDialog {
        private JTextField maNVField, loaiField, tongTienField, giamGiaField;
        private JComboBox<String> trangThaiCombo;
        private DateChooserComponent ngayDatPicker;
        private JComboBox<String> hourCombo, minuteCombo;
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
            ngayDatPicker = new DateChooserComponent();
            
            // Tạo dropdown cho giờ và phút
            hourCombo = new JComboBox<>();
            for (int i = 0; i < 24; i++) {
                hourCombo.addItem(String.format("%02d", i));
            }
            
            minuteCombo = new JComboBox<>();
            for (int i = 0; i < 60; i += 15) { // Mỗi 15 phút
                minuteCombo.addItem(String.format("%02d", i));
            }
            
            trangThaiCombo = new JComboBox<>(new String[]{"Chưa thanh toán", "Đã thanh toán", "Bị hủy"});
            
            if (dh != null) {
                maNVField.setText(String.valueOf(dh.getMaNV()));
                loaiField.setText(dh.getLoai());
                trangThaiCombo.setSelectedItem(convertTrangThaiToUI(dh.getTrangThai()));
                tongTienField.setText(String.valueOf(dh.getTongTien()));
                giamGiaField.setText(String.valueOf(dh.getGiamGia()));
                if (dh.getNgayDat() != null) {
                    ngayDatPicker.setDate(dh.getNgayDat());
                    // Thiết lập giờ và phút
                    java.util.Calendar cal = java.util.Calendar.getInstance();
                    cal.setTime(dh.getNgayDat());
                    hourCombo.setSelectedItem(String.format("%02d", cal.get(java.util.Calendar.HOUR_OF_DAY)));
                    minuteCombo.setSelectedItem(String.format("%02d", (cal.get(java.util.Calendar.MINUTE) / 15) * 15));
                }
            } else {
                // Mặc định cho đơn hàng mới
                maNVField.setText(String.valueOf(database.Session.currentMaNV));
                ngayDatPicker.setCurrentDate();
                // Thiết lập giờ hiện tại
                java.util.Calendar cal = java.util.Calendar.getInstance();
                hourCombo.setSelectedItem(String.format("%02d", cal.get(java.util.Calendar.HOUR_OF_DAY)));
                minuteCombo.setSelectedItem(String.format("%02d", (cal.get(java.util.Calendar.MINUTE) / 15) * 15));
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
            mainPanel.add(new JLabel("Ngày đặt:"), gbc);
            gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
            mainPanel.add(ngayDatPicker, gbc);
            
            // Giờ và phút
            gbc.gridx = 0; gbc.gridy = 4; gbc.anchor = GridBagConstraints.EAST;
            mainPanel.add(new JLabel("Giờ:"), gbc);
            gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
            JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
            timePanel.add(hourCombo);
            timePanel.add(new JLabel(":"));
            timePanel.add(minuteCombo);
            mainPanel.add(timePanel, gbc);
            
            // Tổng tiền
            gbc.gridx = 0; gbc.gridy = 5; gbc.anchor = GridBagConstraints.EAST;
            mainPanel.add(new JLabel("Tổng tiền (VNĐ):"), gbc);
            gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
            mainPanel.add(tongTienField, gbc);
            
            // Giảm giá
            gbc.gridx = 0; gbc.gridy = 6; gbc.anchor = GridBagConstraints.EAST;
            mainPanel.add(new JLabel("Giảm giá (%):"), gbc);
            gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
            mainPanel.add(giamGiaField, gbc);
            
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
            String maNVStr = maNVField.getText().trim();
            String loai = loaiField.getText().trim();
            String trangThai = (String) trangThaiCombo.getSelectedItem();
            String ngayDatStr = ngayDatPicker.getSelectedDateString();
            String hourStr = (String) hourCombo.getSelectedItem();
            String minuteStr = (String) minuteCombo.getSelectedItem();
            String tongTienStr = tongTienField.getText().trim();
            String giamGiaStr = giamGiaField.getText().trim();
            
            // Tạo chuỗi ngày giờ đầy đủ
            String fullDateTimeStr = ngayDatStr + " " + hourStr + ":" + minuteStr + ":00";
            
            if (maNVStr.isEmpty() || loai.isEmpty() || tongTienStr.isEmpty() || giamGiaStr.isEmpty()) {
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
                        "INSERT INTO donhang (MaNV, Loai, TrangThai, NgayDat, TongTien, GiamGia) VALUES (?, ?, ?, ?, ?, ?)");
                    ps.setInt(1, maNV);
                    ps.setString(2, loai);
                    ps.setString(3, convertTrangThaiToDatabase(trangThai));
                    ps.setString(4, fullDateTimeStr);
                    ps.setLong(5, tongTien);
                    ps.setInt(6, giamGia);
                    ps.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Thêm thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    // Sửa
                    PreparedStatement ps = conn.prepareStatement(
                        "UPDATE donhang SET MaNV=?, Loai=?, TrangThai=?, NgayDat=?, TongTien=?, GiamGia=? WHERE MaDon=?");
                    ps.setInt(1, maNV);
                    ps.setString(2, loai);
                    ps.setString(3, convertTrangThaiToDatabase(trangThai));
                    ps.setString(4, fullDateTimeStr);
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
    
    // Method chuyển đổi trạng thái từ giao diện sang database
    private String convertTrangThaiToDatabase(String trangThaiUI) {
        if ("Đã thanh toán".equals(trangThaiUI)) {
            return "dathanhtoan";
        } else if ("Chưa thanh toán".equals(trangThaiUI)) {
            return "chuathanhtoan";
        } else if ("Bị hủy".equals(trangThaiUI)) {
            return "bihuy";
        }
        return "chuathanhtoan"; // Mặc định
    }
    
    private void printInvoice(String content, int maDon) {
        try {
            // Tạo một JTextArea để in
            JTextArea printArea = new JTextArea(content);
            printArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            printArea.print();
            
            JOptionPane.showMessageDialog(this, "In hóa đơn thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi in hóa đơn: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void exportInvoice(String content, int maDon) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Xuất hóa đơn");
        fileChooser.setSelectedFile(new java.io.File("HoaDon_" + maDon + "_" + 
            new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date()) + ".txt"));
        
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try (java.io.FileWriter writer = new java.io.FileWriter(fileChooser.getSelectedFile())) {
                writer.write(content);
                JOptionPane.showMessageDialog(this, "Xuất hóa đơn thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } catch (java.io.IOException e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xuất file: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
