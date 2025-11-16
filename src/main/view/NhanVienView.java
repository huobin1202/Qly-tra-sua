package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.List;
import dto.NhanVienDTO;
import dao.NhanVienDAO;
import utils.DateChooserComponent;

public class NhanVienView extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> searchCombo;
    private MainFrameInterface parent;
    private NhanVienDAO nhanVienDAO;
    
    public NhanVienView(MainFrameInterface parent) {
        this.parent = parent;
        this.nhanVienDAO = new NhanVienDAO();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadData();
    }
    
    private void initializeComponents() {
        // Tạo table model
        String[] columns = {"ID", "Tài khoản","Mật khẩu", "Họ tên", "Số điện thoại", "Ngày vào làm", "Chức vụ", "Lương", "Trạng thái"};
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
        searchCombo = new JComboBox<>(new String[]{"ID", "Tài khoản", "Họ tên"});
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
        try {
            List<NhanVienDTO> danhSach = nhanVienDAO.layTatCaNhanVien();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            for (NhanVienDTO nv : danhSach) {
                Object[] row = {
                    nv.getMaNV(),
                    nv.getTaiKhoan(),
                    nv.getMatKhau(),
                    nv.getHoTen(),
                    nv.getSoDienThoai(),
                    nv.getNgayVaoLam() != null ? dateFormat.format(nv.getNgayVaoLam()) : "",
                    convertChucVuToUI(nv.getChucVu()),
                    String.format("%,d", nv.getLuong()) + " VNĐ",
                    convertTrangThaiToUI(nv.getTrangThai())
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
            List<NhanVienDTO> danhSach = nhanVienDAO.timKiemNhanVien(searchType, searchText);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            for (NhanVienDTO nv : danhSach) {
                Object[] row = {
                    nv.getMaNV(),
                    nv.getTaiKhoan(),
                    nv.getMatKhau(),
                    nv.getHoTen(),
                    nv.getSoDienThoai(),
                    nv.getNgayVaoLam() != null ? dateFormat.format(nv.getNgayVaoLam()) : "",
                    convertChucVuToUI(nv.getChucVu()),
                    String.format("%,d", nv.getLuong()) + " VNĐ",
                    convertTrangThaiToUI(nv.getTrangThai())
                };
                tableModel.addRow(row);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "ID phải là số!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tìm kiếm: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Method chuyển đổi chức vụ từ database sang giao diện
    private String convertChucVuToUI(String chucVuDB) {
        if ("nhanvien".equals(chucVuDB)) {
            return "Nhân viên";
        } else if ("quanly".equals(chucVuDB)) {
            return "Quản lý";
        }
        return "Nhân viên"; // Mặc định
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
        
        // Lấy dữ liệu từ DAO
        try {
            NhanVienDTO nv = nhanVienDAO.layNhanVienTheoMa(id);
            if (nv == null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy nhân viên!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            NhanVienDialog dialog = new NhanVienDialog(SwingUtilities.getWindowAncestor(this), "Sửa thông tin nhân viên", nv);
            dialog.setVisible(true);
            if (dialog.isDataChanged()) {
                loadData();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
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
        
        // Kiểm tra ràng buộc trước khi xóa
        try {
            String rangBuocMsg = nhanVienDAO.kiemTraRangBuocXoa(id);
            if (rangBuocMsg != null) {
                JOptionPane.showMessageDialog(this, rangBuocMsg, "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi kiểm tra ràng buộc: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int result = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc chắn muốn xóa nhân viên '" + hoTen + "'?", 
            "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            try {
                if (nhanVienDAO.xoaNhanVien(id)) {
                    JOptionPane.showMessageDialog(this, "Xóa thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    loadData();
                } else {
                    JOptionPane.showMessageDialog(this, "Không thể xóa nhân viên!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi xóa dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // Inner class for Add/Edit dialog
    private class NhanVienDialog extends JDialog {
        private JTextField taiKhoanField, matKhauField, hoTenField, sdtField, luongField;
        private DateChooserComponent ngayVaoLamPicker;
        private JComboBox<String> chucVuCombo;
        private JComboBox<String> trangThaiCombo; // Thêm trong NhanVienDialog
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
            ngayVaoLamPicker = new DateChooserComponent();
            chucVuCombo = new JComboBox<>(new String[]{"Nhân viên", "Quản lý"});
            chucVuCombo.setPreferredSize(new Dimension(100, 20)); // Tăng độ rộng và cao
            luongField = new JTextField(20);
            trangThaiCombo = new JComboBox<>(new String[]{"Đang làm", "Nghỉ việc"});
            
            if (nv != null) {
                // Sửa nhân viên - hiển thị thông tin hiện tại
                taiKhoanField.setText(nv.getTaiKhoan());
                matKhauField.setText(nv.getMatKhau());
                hoTenField.setText(nv.getHoTen());
                sdtField.setText(String.valueOf(nv.getSoDienThoai()));
                if (nv.getNgayVaoLam() != null) {
                    ngayVaoLamPicker.setDate(nv.getNgayVaoLam());
                }
                // Chuyển đổi chức vụ từ DB sang UI
                String chucVuUI = nv.getChucVu() != null && nv.getChucVu().equalsIgnoreCase("quanly") ? "Quản lý" : "Nhân viên";
                chucVuCombo.setSelectedItem(chucVuUI);
                luongField.setText(String.valueOf(nv.getLuong()));
                String trangThaiUi = nv.getTrangThai() != null && nv.getTrangThai().equalsIgnoreCase("nghiviec") ? "Nghỉ việc" : "Đang làm";
                trangThaiCombo.setSelectedItem(trangThaiUi);
            } else {
                // Thêm nhân viên mới - tự động set ngày hiện tại
                ngayVaoLamPicker.setCurrentDate();
                trangThaiCombo.setSelectedIndex(0);
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
            mainPanel.add(new JLabel("Ngày vào làm:"), gbc);
            gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
            mainPanel.add(ngayVaoLamPicker, gbc);
            
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
            
            // Trạng thái
            gbc.gridx = 0; gbc.gridy = 7; gbc.anchor = GridBagConstraints.EAST;
            mainPanel.add(new JLabel("Trạng thái:"), gbc);
            gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
            mainPanel.add(trangThaiCombo, gbc);
            
            // Buttons
            gbc.gridx = 0; gbc.gridy = 8; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
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
            String ngayVaoLamStr = ngayVaoLamPicker.getSelectedDateString();
            String chucVu = (String) chucVuCombo.getSelectedItem();
            String luongStr = luongField.getText().trim();
            String sdtStr = sdtField.getText().trim();
            if (taiKhoan.isEmpty() || hoTen.isEmpty() || sdtStr.isEmpty() || chucVu.isEmpty() || luongStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin bắt buộc!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            long luong;
            try {
                luong = Long.parseLong(luongStr.replaceAll("[^0-9]", ""));
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Lương phải là số!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Validation số điện thoại
            // Kiểm tra chỉ chứa số
            if (!sdtStr.matches("\\d+")) {
                JOptionPane.showMessageDialog(this, "Số điện thoại chỉ được chứa số!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Kiểm tra độ dài
            if (sdtStr.length() > 11) {
                JOptionPane.showMessageDialog(this, "Số điện thoại có tối đa 11 chữ số!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                if (nv == null) {
                    // Thêm mới
                    if (matKhau.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Vui lòng nhập mật khẩu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    // Kiểm tra tài khoản trùng
                    if (nhanVienDAO.kiemTraTaiKhoanTonTai(taiKhoan, null)) {
                        JOptionPane.showMessageDialog(this, 
                            "Tài khoản '" + taiKhoan + "' đã tồn tại! Vui lòng chọn tài khoản khác.", 
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    // Kiểm tra số điện thoại trùng
                    if (nhanVienDAO.kiemTraSDTTonTai(sdtStr, null)) {
                        JOptionPane.showMessageDialog(this, 
                            "Số điện thoại '" + sdtStr + "' đã được sử dụng! Vui lòng chọn số điện thoại khác.", 
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    // Tạo DTO
                    NhanVienDTO newNv;
                    Timestamp ngayVaoLam = null;
                    if (!ngayVaoLamStr.isEmpty()) {
                        try {
                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                            ngayVaoLam = new Timestamp(dateFormat.parse(ngayVaoLamStr).getTime());
                        } catch (Exception e) {
                            // Ignore parsing error
                        }
                    }
                    
                    String chucVuDB = convertChucVuToDatabase(chucVu);
                    if ("Quản lý".equals(chucVu)) {
                        newNv = new dto.NhanVienQuanLyDTO(0, taiKhoan, matKhau, hoTen, sdtStr, ngayVaoLam, luong, 
                            convertTrangThaiToDatabase((String)trangThaiCombo.getSelectedItem()));
                    } else {
                        newNv = new dto.NhanVienThuongDTO(0, taiKhoan, matKhau, hoTen, sdtStr, ngayVaoLam, luong, 
                            convertTrangThaiToDatabase((String)trangThaiCombo.getSelectedItem()));
                    }
                    
                    if (nhanVienDAO.themNhanVien(newNv)) {
                        JOptionPane.showMessageDialog(this, "Thêm thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "Không thể thêm nhân viên!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } else {
                    // Sửa
                    // Kiểm tra tài khoản trùng (nếu đổi tài khoản)
                    if (!taiKhoan.equals(nv.getTaiKhoan()) && nhanVienDAO.kiemTraTaiKhoanTonTai(taiKhoan, nv.getMaNV())) {
                        JOptionPane.showMessageDialog(this, 
                            "Tài khoản '" + taiKhoan + "' đã tồn tại! Vui lòng chọn tài khoản khác.", 
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    // Kiểm tra số điện thoại trùng (nếu đổi số điện thoại)
                    if (!sdtStr.equals(nv.getSoDienThoai()) && nhanVienDAO.kiemTraSDTTonTai(sdtStr, nv.getMaNV())) {
                        JOptionPane.showMessageDialog(this, 
                            "Số điện thoại '" + sdtStr + "' đã được sử dụng! Vui lòng chọn số điện thoại khác.", 
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    // Kiểm tra logic vô hiệu hóa tài khoản và đảm bảo ít nhất 1 quản lý
                    String chucVuDB = convertChucVuToDatabase(chucVu);
                    String trangThaiMoi = convertTrangThaiToDatabase((String)trangThaiCombo.getSelectedItem());
                    String chucVuCu = nv.getChucVu();
                    
                    // Kiểm tra nếu chuyển chức vụ từ Quản lý sang Nhân viên hoặc nghỉ việc
                    boolean chuyenChucVu = chucVuCu != null && chucVuCu.equalsIgnoreCase("quanly") && chucVuDB.equals("nhanvien");
                    boolean nghiViec = trangThaiMoi.equals("nghiviec");
                    
                    // Nếu chuyển chức vụ hoặc nghỉ việc, tài khoản sẽ bị vô hiệu hóa
                    if (chuyenChucVu || nghiViec) {
                        // Kiểm tra nếu nhân viên này là quản lý, cần đảm bảo còn ít nhất 1 quản lý khác
                        if (chucVuCu != null && chucVuCu.equalsIgnoreCase("quanly")) {
                            if (!nhanVienDAO.kiemTraItNhatMotQuanLy(nv.getMaNV())) {
                                JOptionPane.showMessageDialog(this, 
                                    "Không thể thực hiện thao tác này! Hệ thống cần ít nhất 1 quản lý đang làm việc.", 
                                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                        }
                        
                        // Vô hiệu hóa tài khoản (set TrangThai = "nghiviec")
                        trangThaiMoi = "nghiviec";
                    }
                    
                    // Cập nhật DTO
                    nv.setTaiKhoan(taiKhoan);
                    if (!matKhau.isEmpty()) {
                        nv.setMatKhau(matKhau);
                    }
                    nv.setHoTen(hoTen);
                    nv.setSoDienThoai(sdtStr);
                    Timestamp ngayVaoLam = null;
                    if (!ngayVaoLamStr.isEmpty()) {
                        try {
                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                            ngayVaoLam = new Timestamp(dateFormat.parse(ngayVaoLamStr).getTime());
                        } catch (Exception e) {
                            // Ignore parsing error
                        }
                    }
                    nv.setNgayVaoLam(ngayVaoLam);
                    nv.setChucVu(chucVuDB);
                    nv.setLuong(luong);
                    nv.setTrangThai(trangThaiMoi);
                    
                    // Cập nhật (có hoặc không có mật khẩu)
                    boolean capNhatMatKhau = !matKhau.isEmpty();
                    if (nhanVienDAO.capNhatNhanVienVoiMatKhau(nv, capNhatMatKhau)) {
                        JOptionPane.showMessageDialog(this, "Sửa thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "Không thể cập nhật nhân viên!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
                dataChanged = true;
                dispose();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi lưu dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
        
        public boolean isDataChanged() {
            return dataChanged;
        }
        
        // Method chuyển đổi chức vụ từ giao diện sang database
        private String convertChucVuToDatabase(String chucVuUI) {
            if ("Nhân viên".equals(chucVuUI)) {
                return "nhanvien";
            } else if ("Quản lý".equals(chucVuUI)) {
                return "quanly";
            }
            return "nhanvien"; // Mặc định
        }
    }

    // Biện dịch trạng thái DB <=> UI
    private String convertTrangThaiToUI(String trangThaiDb) {
        if ("nghiviec".equalsIgnoreCase(trangThaiDb)) return "Nghỉ việc";
        return "Đang làm";
    }
    private String convertTrangThaiToDatabase(String trangThaiUi) {
        if ("Nghỉ việc".equals(trangThaiUi)) return "nghiviec";
        return "danglam";
    }
}
