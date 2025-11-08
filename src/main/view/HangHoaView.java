package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import database.DBUtil;
import dto.MonDTO;
import dto.NguyenLieuDTO;
import dto.LoaiMonDTO;
import dto.MonNguyenLieuDTO;
import dao.HangHoaDAO;

public class HangHoaView extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> searchCombo, categoryCombo;
    private MainFrameInterface parent;
    private String currentView = ""; // MON, LOAIMON, or NGUYENLIEU - khởi tạo rỗng để load khi setCurrentView được gọi
    private JComboBox<String> loaiMonFilterCombo;
    private JComboBox<String> tinhTrangFilterCombo;
    // Thêm biến toàn cục cho searchPanel
    private JPanel searchPanel;
    
    public void setCurrentView(String view) {
        // Chỉ load lại nếu view thay đổi
        boolean viewChanged = !this.currentView.equals(view);
        this.currentView = view;
        
        if (currentView.equals("MON")) reloadLoaiMonFilterCombo();
        updateTableHeaders();
        refreshSearchPanel();
        
        // Load dữ liệu chỉ khi view được hiển thị lần đầu hoặc view thay đổi
        if (!dataLoaded || viewChanged) {
            loadData();
            dataLoaded = true;
        }
        
        // Đảm bảo table được hiển thị
        table.setVisible(true);
        revalidate();
        repaint();
    }
    
    
    public String getCurrentView() {
        return currentView;
    }
    
    private boolean dataLoaded = false; // Flag để track xem đã load dữ liệu chưa
    
    public HangHoaView(MainFrameInterface parent) {
        this.parent = parent;
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        // Không load dữ liệu trong constructor - chỉ load khi view được hiển thị
    }
    
    private void initializeComponents() {
        // Tạo table model
        String[] columns = {"ID", "Tên", "Giá", "Trạng thái", "Loại"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(30); // Giảm height để giảm khoảng trống
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        // Tối ưu hóa hiệu năng rendering
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        
        // Tạo search components
        searchCombo = new JComboBox<>(new String[]{"ID", "Tên"});
        searchField = new JTextField(20);
        // categoryCombo = new JComboBox<>(new String[]{"Món", "Loại món", "Nguyên liệu"});
        // BỎ HOÀN TOÀN categoryCombo, không khởi tạo, không add vào panel nào nữa
        loaiMonFilterCombo = new JComboBox<>();
        loaiMonFilterCombo.addItem("Tất cả loại");
        // Tải loại món từ DB để đưa vào filter
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT TenLoai FROM loaimon ORDER BY MaLoai")) {
            while (rs.next()) {
                loaiMonFilterCombo.addItem(rs.getString("TenLoai"));
            }
        } catch (SQLException e) {
            // Nếu lỗi thôi không thêm gì
        }
        
        // Combo box lọc theo tình trạng
        tinhTrangFilterCombo = new JComboBox<>();
        tinhTrangFilterCombo.addItem("Tất cả tình trạng");
        tinhTrangFilterCombo.addItem("Đang bán");
        tinhTrangFilterCombo.addItem("Tạm ngừng");
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
        // THÊM GẮN SỰ KIỆN LẠI CHO CÁC NÚT:
        addButton.addActionListener(e -> showAddDialog());
        editButton.addActionListener(e -> showEditDialog());
        deleteButton.addActionListener(e -> performDelete());
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        
        // Search panel tạo toàn cục để dùng lại (và có thể refresh sau này)
        searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(new Color(240, 248, 255));
        // Nhớ tạo layout ngay lúc khởi tạo lần đầu
        refreshSearchPanel();
        
        // Thêm button panel và search panel vào top panel
        topPanel.add(buttonPanel, BorderLayout.WEST);
        topPanel.add(searchPanel, BorderLayout.EAST);
        
        // Table panel
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Danh sách hàng hóa"));
        
        // Layout - bỏ headerPanel và controlPanel trống để giảm khoảng trống
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        
        // Event handlers
        // Lưu ý: KHÔNG thao tác gì với searchButton, refreshButton ở setupLayout!
        // Mọi event đã gắn đúng bên trong refreshSearchPanel
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
        
        // Tự động load khi chọn loại hoặc tình trạng (chỉ khi view là MON)
        loaiMonFilterCombo.addActionListener(e -> {
            if (currentView != null && currentView.equals("MON")) {
                loadData();
            }
        });
        
        tinhTrangFilterCombo.addActionListener(e -> {
            if (currentView != null && currentView.equals("MON")) {
                loadData();
            }
        });
    }
    
    // Bỏ hoàn toàn switchView(), categoryCombo không tồn tại nữa nên không dùng mỗi khi đổi view
    
    private void updateTableHeaders() {
        if (currentView.equals("MON")) {
            tableModel.setColumnIdentifiers(new String[]{"ID", "Tên món", "Giá", "Trạng thái", "Loại", "Ảnh"});
        } else if (currentView.equals("LOAIMON")) {
            tableModel.setColumnIdentifiers(new String[]{"ID", "Tên loại"});
        } else {
            tableModel.setColumnIdentifiers(new String[]{"ID", "Tên nguyên liệu", "Đơn vị"});
        }
        
        // Set custom renderer for image column after table is updated
        SwingUtilities.invokeLater(() -> {
            if (currentView.equals("MON") && table.getColumnCount() > 5) {
                table.getColumnModel().getColumn(5).setCellRenderer(new ImageCellRenderer());
            }
        });
    }
    
    private void loadData() {
        // Clear table trước
        tableModel.setRowCount(0);
        
        // Load dữ liệu trực tiếp trên EDT để đảm bảo hiển thị ngay lập tức
        // Tối ưu bằng cách batch load và update UI một lần
        try (Connection conn = DBUtil.getConnection()) {
            List<Object[]> rows = new ArrayList<>();
            
            if (currentView.equals("MON")) {
                String tenLoaiSelected = (String) loaiMonFilterCombo.getSelectedItem();
                String tinhTrangSelected = (String) tinhTrangFilterCombo.getSelectedItem();
                rows = loadMonDataBatch(conn, tenLoaiSelected, tinhTrangSelected);
            } else if (currentView.equals("LOAIMON")) {
                rows = loadLoaiMonDataBatch(conn);
            } else {
                rows = loadNguyenLieuDataBatch(conn);
            }
            
            // Batch add rows để tối ưu hiệu suất
            for (Object[] row : rows) {
                tableModel.addRow(row);
            }
            
            // Set renderer after data is loaded
            if (currentView.equals("MON") && table.getColumnCount() > 5) {
                table.getColumnModel().getColumn(5).setCellRenderer(new ImageCellRenderer());
            }
            
            // Refresh table để hiển thị dữ liệu ngay lập tức
            table.revalidate();
            table.repaint();
        } catch (SQLException e) {
            e.printStackTrace(); // Debug
            JOptionPane.showMessageDialog(this, 
                "Lỗi tải dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Batch load methods - trả về List<Object[]> thay vì add trực tiếp vào tableModel
    private List<Object[]> loadMonDataBatch(Connection conn, String tenLoaiSelected, String tinhTrangSelected) throws SQLException {
        List<Object[]> rows = new ArrayList<>();
        String sql = "SELECT m.MaMon, m.TenMon, m.Gia, m.TinhTrang, l.TenLoai, m.Anh FROM mon m LEFT JOIN loaimon l ON m.MaLoai = l.MaLoai";
        List<String> whereConditions = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        
        if (tenLoaiSelected != null && !tenLoaiSelected.equals("Tất cả loại")) {
            whereConditions.add("l.TenLoai = ?");
            params.add(tenLoaiSelected);
        }
        
        if (tinhTrangSelected != null && !tinhTrangSelected.equals("Tất cả tình trạng")) {
            String tinhTrangDB = convertTinhTrangToDatabase(tinhTrangSelected);
            whereConditions.add("m.TinhTrang = ?");
            params.add(tinhTrangDB);
        }
        
        if (!whereConditions.isEmpty()) {
            sql += " WHERE " + String.join(" AND ", whereConditions);
        }
        sql += " ORDER BY m.MaMon";
        
        PreparedStatement ps = conn.prepareStatement(sql);
        for (int i = 0; i < params.size(); i++) {
            ps.setObject(i + 1, params.get(i));
        }
        
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Object[] row = {
                rs.getInt("MaMon"),
                rs.getString("TenMon"),
                String.format("%,d", rs.getLong("Gia")) + " VNĐ",
                convertTinhTrangToUI(rs.getString("TinhTrang")),
                rs.getString("TenLoai"),
                rs.getString("Anh")
            };
            rows.add(row);
        }
        rs.close();
        ps.close();
        return rows;
    }
    
    private List<Object[]> loadLoaiMonDataBatch(Connection conn) throws SQLException {
        List<Object[]> rows = new ArrayList<>();
        String sql = "SELECT MaLoai, TenLoai FROM loaimon ORDER BY MaLoai";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("MaLoai"),
                    rs.getString("TenLoai"),
                };
                rows.add(row);
            }
        }
        return rows;
    }
    
    private List<Object[]> loadNguyenLieuDataBatch(Connection conn) throws SQLException {
        List<Object[]> rows = new ArrayList<>();
        String sql = "SELECT * FROM nguyenlieu ORDER BY MaNL";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("MaNL"),
                    rs.getString("TenNL"),
                    rs.getString("DonVi")
                };
                rows.add(row);
            }
        }
        return rows;
    }
    
    private void performSearch() {
        String searchText = searchField.getText().trim();
        String searchType = (String) searchCombo.getSelectedItem();
        
        tableModel.setRowCount(0);
        try (Connection conn = DBUtil.getConnection()) {
            if (currentView.equals("MON")) {
                searchMonData(conn, searchText, searchType);
            } else if (currentView.equals("LOAIMON")) {
                searchLoaiMonData(conn, searchText, searchType);
            } else {
                searchNguyenLieuData(conn, searchText, searchType);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi tìm kiếm: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "ID phải là số!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
        
        // Set renderer after search is completed
        if (currentView.equals("MON") && table.getColumnCount() > 6) {
            table.getColumnModel().getColumn(6).setCellRenderer(new ImageCellRenderer());
        }
    }
    
    private void searchLoaiMonData(Connection conn, String searchText, String searchType) throws SQLException {
        String sql = "SELECT MaLoai, TenLoai FROM loaimon WHERE ";
        PreparedStatement ps;
        
        if (searchType.equals("ID")) {
            sql += "MaLoai = ? ORDER BY MaLoai";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, Integer.parseInt(searchText));
        } else {
            sql += "TenLoai LIKE ? ORDER BY MaLoai";
            ps = conn.prepareStatement(sql);
            ps.setString(1, "%" + searchText + "%");
        }
        
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Object[] row = {
                rs.getInt("MaLoai"),
                rs.getString("TenLoai")
            };
            tableModel.addRow(row);
        }
    }
    
    private void searchMonData(Connection conn, String searchText, String searchType) throws SQLException {
        String sql = "SELECT m.MaMon, m.TenMon, m.Gia, m.TinhTrang, l.TenLoai, m.Anh FROM mon m LEFT JOIN loaimon l ON m.MaLoai = l.MaLoai WHERE ";
        List<String> whereConditions = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        PreparedStatement ps;
        
        // Thêm điều kiện search
        if (searchType.equals("ID")) {
            whereConditions.add("m.MaMon = ?");
            params.add(Integer.parseInt(searchText));
        } else if (searchType.equals("Tên")) {
            whereConditions.add("m.TenMon LIKE ?");
            params.add("%" + searchText + "%");
        } else {
            // Tìm kiếm theo trạng thái: chuyển đổi từ UI sang database nếu cần
            String tinhTrangSearch = searchText;
            // Kiểm tra nếu là tiếng Việt thì chuyển sang database
            if ("Đang bán".equals(searchText) || searchText.contains("Đang bán")) {
                tinhTrangSearch = "dangban";
            } else if ("Tạm ngừng".equals(searchText) || searchText.contains("Tạm ngừng")) {
                tinhTrangSearch = "ngungban";
            }
            whereConditions.add("m.TinhTrang LIKE ?");
            params.add("%" + tinhTrangSearch + "%");
        }
        
        // Thêm điều kiện filter từ combo (nếu có)
        String tenLoaiSelected = (String) loaiMonFilterCombo.getSelectedItem();
        if (tenLoaiSelected != null && !tenLoaiSelected.equals("Tất cả loại")) {
            whereConditions.add("l.TenLoai = ?");
            params.add(tenLoaiSelected);
        }
        
        String tinhTrangSelected = (String) tinhTrangFilterCombo.getSelectedItem();
        if (tinhTrangSelected != null && !tinhTrangSelected.equals("Tất cả tình trạng")) {
            String tinhTrangDB = convertTinhTrangToDatabase(tinhTrangSelected);
            whereConditions.add("m.TinhTrang = ?");
            params.add(tinhTrangDB);
        }
        
        // Xây dựng SQL với tất cả điều kiện
        sql += String.join(" AND ", whereConditions);
        sql += " ORDER BY m.MaMon";
        
        ps = conn.prepareStatement(sql);
        for (int i = 0; i < params.size(); i++) {
            ps.setObject(i + 1, params.get(i));
        }
        
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Object[] row = {
                rs.getInt("MaMon"),
                rs.getString("TenMon"),
                String.format("%,d", rs.getLong("Gia")) + " VNĐ",
                convertTinhTrangToUI(rs.getString("TinhTrang")), // Chuyển đổi trạng thái sang tiếng Việt
                rs.getString("TenLoai"),
                rs.getString("Anh")
            };
            tableModel.addRow(row);
        }
    }
    
    private void searchNguyenLieuData(Connection conn, String searchText, String searchType) throws SQLException {
        String sql = "SELECT * FROM nguyenlieu WHERE ";
        PreparedStatement ps;
        
        if (searchType.equals("ID")) {
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
                // Nếu view món đang mở, reload loại cho bên đó luôn
                reloadLoaiMonFilterCombo();
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
            String giaStr = (String) tableModel.getValueAt(selectedRow, 2);
            String tinhTrangUI = (String) tableModel.getValueAt(selectedRow, 3); // Trạng thái hiển thị (ví dụ: "Đang bán")
            String loai = (String) tableModel.getValueAt(selectedRow, 4);
            String anh = (String) tableModel.getValueAt(selectedRow, 5);
            
            // Chuyển đổi trạng thái từ UI về database
            String tinhTrang = convertTinhTrangToDatabase(tinhTrangUI);
            
            long gia = 0;
            if (!giaStr.isEmpty()) {
                try {
                    gia = Long.parseLong(giaStr.replaceAll("[^0-9]", ""));
                } catch (Exception e) {
                    // Ignore parsing error
                }
            }
            
            // Get MaLoai from TenLoai
            int maLoai = 1;
            try (Connection conn = DBUtil.getConnection()) {
                try (PreparedStatement ps = conn.prepareStatement("SELECT MaLoai FROM loaimon WHERE TenLoai = ?")) {
                    ps.setString(1, loai);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        maLoai = rs.getInt("MaLoai");
                    }
                }
            } catch (SQLException e) {
                // Use default value
            }
            
            MonDTO mon = new MonDTO(id, ten, gia, tinhTrang, maLoai, anh);
            MonDialog dialog = new MonDialog(SwingUtilities.getWindowAncestor(this), "Sửa thông tin món", mon);
            dialog.setVisible(true);
            if (dialog.isDataChanged()) {
                loadData();
            }
        } else if (currentView.equals("LOAIMON")) {
            int id = (Integer) tableModel.getValueAt(selectedRow, 0);
            String ten = (String) tableModel.getValueAt(selectedRow, 1);
            
            LoaiMonDTO loaiMon = new LoaiMonDTO(id, ten);
            LoaiMonDialog dialog = new LoaiMonDialog(SwingUtilities.getWindowAncestor(this), "Sửa thông tin loại món", loaiMon);
            dialog.setVisible(true);
            if (dialog.isDataChanged()) {
                loadData();
                reloadLoaiMonFilterCombo();
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
        
        int id = (Integer) tableModel.getValueAt(selectedRow, 0);
        
        // Kiểm tra khóa ngoại trước khi xóa
        try (Connection conn = DBUtil.getConnection()) {
            if (currentView.equals("MON")) {
                // Kiểm tra món có được sử dụng trong chi tiết đơn hàng không
                try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM chitietdonhang WHERE MaMon=?")) {
                    ps.setInt(1, id);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next() && rs.getInt(1) > 0) {
                            JOptionPane.showMessageDialog(this, 
                                "Không thể xóa món này vì đã được sử dụng trong đơn hàng!", 
                                "Lỗi", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    }
                }
                
                // Kiểm tra món có được sử dụng làm topping trong chi tiết đơn hàng không
                try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM chitietdonhang WHERE MaTopping=?")) {
                    ps.setInt(1, id);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next() && rs.getInt(1) > 0) {
                            JOptionPane.showMessageDialog(this, 
                                "Không thể xóa món này vì đã được sử dụng làm topping trong đơn hàng!", 
                                "Lỗi", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    }
                }
            } else if (currentView.equals("LOAIMON")) {
                // Kiểm tra loại món có được sử dụng trong bảng mon không
                try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM mon WHERE MaLoai=?")) {
                    ps.setInt(1, id);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next() && rs.getInt(1) > 0) {
                            JOptionPane.showMessageDialog(this, 
                                "Không thể xóa loại món này vì đã có món sử dụng loại này!", 
                                "Lỗi", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    }
                }
            } else {
                // Kiểm tra nguyên liệu có được sử dụng trong chi tiết nhập hàng không
                try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM chitietnhap_nl WHERE MaNL=?")) {
                    ps.setInt(1, id);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next() && rs.getInt(1) > 0) {
                            JOptionPane.showMessageDialog(this, 
                                "Không thể xóa nguyên liệu này vì đã được sử dụng trong phiếu nhập hàng!", 
                                "Lỗi", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    }
                }
                
                // Kiểm tra nguyên liệu có được sử dụng trong kho không
                try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM khohang WHERE MaNL=?")) {
                    ps.setInt(1, id);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next() && rs.getInt(1) > 0) {
                            JOptionPane.showMessageDialog(this, 
                                "Không thể xóa nguyên liệu này vì đã có trong kho!", 
                                "Lỗi", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    }
                }
                
                // Kiểm tra nguyên liệu có được sử dụng trong nhà cung cấp không
                try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM ncc_nguyenlieu WHERE MaNL=?")) {
                    ps.setInt(1, id);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next() && rs.getInt(1) > 0) {
                            JOptionPane.showMessageDialog(this, 
                                "Không thể xóa nguyên liệu này vì đã được nhà cung cấp cung cấp!", 
                                "Lỗi", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi kiểm tra ràng buộc: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int result = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc chắn muốn xóa " + type + " '" + name + "'?", 
            "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            try (Connection conn = DBUtil.getConnection()) {
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
        if (currentView.equals("LOAIMON")) {
            reloadLoaiMonFilterCombo();
        }
    }
    
    // Inner class for Mon Add/Edit dialog
    private class MonDialog extends JDialog {
        private JTextField tenField, giaField, anhField;
        private JComboBox<String> loaiCombo;
        private JComboBox<String> tinhTrangCombo;
        private JButton chooseImageButton;
        private JLabel imagePreviewLabel;
        private boolean dataChanged = false;
        private MonDTO mon;
        
        // Ingredients management
        private JComboBox<NguyenLieuDTO> nguyenLieuCombo;
        private JTextField soLuongNLField, donViNLField;
        private JTable ingredientsTable;
        private DefaultTableModel ingredientsTableModel;
        private List<MonNguyenLieuDTO> ingredientsList = new ArrayList<>();
        private HangHoaDAO hangHoaDAO = new HangHoaDAO();
        
        public MonDialog(Window parent, String title, MonDTO mon) {
            super(parent, title, ModalityType.APPLICATION_MODAL);
            this.mon = mon;
            initializeComponents();
            setupLayout();
            setupEventHandlers();
        }
        
        private void initializeComponents() {
            setSize(900, 700);
            setLocationRelativeTo(getParent());
            
            // Product fields
            tenField = new JTextField(20);
            giaField = new JTextField(15);
            anhField = new JTextField(20);
            anhField.setEditable(false);
            loaiCombo = new JComboBox<>();
            tinhTrangCombo = new JComboBox<>();
            tinhTrangCombo.addItem("Đang bán");
            tinhTrangCombo.addItem("Tạm ngừng");
            
            // Image button
            chooseImageButton = new JButton("Thêm ảnh");
            chooseImageButton.setBackground(new Color(139, 90, 0)); // Brown color
            chooseImageButton.setForeground(Color.BLACK);
            chooseImageButton.setFocusPainted(false);
            chooseImageButton.addActionListener(e -> chooseImage());
            
            // Image preview
            imagePreviewLabel = new JLabel("Chưa chọn ảnh", JLabel.CENTER);
            imagePreviewLabel.setBorder(BorderFactory.createEtchedBorder());
            imagePreviewLabel.setPreferredSize(new Dimension(200, 200));
            imagePreviewLabel.setMinimumSize(new Dimension(200, 200));
            imagePreviewLabel.setMaximumSize(new Dimension(200, 200));
            imagePreviewLabel.setHorizontalAlignment(JLabel.CENTER);
            imagePreviewLabel.setVerticalAlignment(JLabel.CENTER);
            
            // Ingredients fields
            nguyenLieuCombo = new JComboBox<>();
            soLuongNLField = new JTextField(10);
            soLuongNLField.setText("0");
            donViNLField = new JTextField(10);
            donViNLField.setEditable(false);
            
            // Ingredients table
            String[] columns = {"Tên nguyên liệu", "Số lượng", "Đơn vị"};
            ingredientsTableModel = new DefaultTableModel(columns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            ingredientsTable = new JTable(ingredientsTableModel);
            ingredientsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            ingredientsTable.setRowHeight(25);
            
            // Load data
            loadLoaiMon();
            loadNguyenLieu();
            
            if (mon != null) {
                setTitle("CHỈNH SỬA SẢN PHẨM");
                tenField.setText(mon.getTenMon());
                giaField.setText(String.valueOf(mon.getGia()));
                anhField.setText(mon.getAnh() != null ? mon.getAnh() : "");
                
                // Set loại món
                try {
                    if (mon.getMaLoai() > 0 && mon.getMaLoai() <= loaiCombo.getItemCount()) {
                        loaiCombo.setSelectedIndex(mon.getMaLoai() - 1);
                    }
                } catch (Exception e) {
                }
                
                // Set tình trạng
                String tinhTrangUI = convertTinhTrangToUI(mon.getTinhTrang());
                if ("Đang bán".equals(tinhTrangUI)) {
                    tinhTrangCombo.setSelectedIndex(0);
                } else if ("Tạm ngừng".equals(tinhTrangUI)) {
                    tinhTrangCombo.setSelectedIndex(1);
                }
                
                // Load image if available
                if (mon.getAnh() != null && !mon.getAnh().trim().isEmpty()) {
                    loadImagePreview(mon.getAnh());
                }
                
                // Load ingredients
                loadIngredients();
            } else {
                setTitle("THÊM SẢN PHẨM MỚI");
                imagePreviewLabel.setText("Chưa chọn ảnh");
                imagePreviewLabel.setIcon(null);
            }
            
            // Update don vi when ingredient is selected
            nguyenLieuCombo.addActionListener(e -> {
                NguyenLieuDTO selected = (NguyenLieuDTO) nguyenLieuCombo.getSelectedItem();
                if (selected != null) {
                    donViNLField.setText(selected.getDonVi());
                }
            });
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
            // Set orange background
            Color orangeBg = new Color(255, 200, 100);
            setBackground(orangeBg);
            
            JPanel mainPanel = new JPanel(new BorderLayout());
            mainPanel.setBackground(orangeBg);
            mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            
            // Top panel: Left (form) and Right (image)
            JPanel topPanel = new JPanel(new BorderLayout(15, 0));
            topPanel.setBackground(orangeBg);
            
            // Left panel: Product info form
            JPanel leftPanel = new JPanel(new GridBagLayout());
            leftPanel.setBackground(orangeBg);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.anchor = GridBagConstraints.WEST;
            
            // Tên sản phẩm
            gbc.gridx = 0; gbc.gridy = 0;
            leftPanel.add(new JLabel("TÊN SẢN PHẨM:"), gbc);
            gbc.gridx = 1;
            tenField.setPreferredSize(new Dimension(250, 25));
            leftPanel.add(tenField, gbc);
            
            // Category
            gbc.gridx = 0; gbc.gridy = 1;
            leftPanel.add(new JLabel("LOẠI:"), gbc);
            gbc.gridx = 1;
            loaiCombo.setPreferredSize(new Dimension(250, 25));
            leftPanel.add(loaiCombo, gbc);
            
            // Giá bán
            gbc.gridx = 0; gbc.gridy = 2;
            leftPanel.add(new JLabel("GIÁ BÁN:"), gbc);
            gbc.gridx = 1;
            giaField.setPreferredSize(new Dimension(250, 25));
            leftPanel.add(giaField, gbc);
            
            // Tình trạng
            gbc.gridx = 0; gbc.gridy = 3;
            leftPanel.add(new JLabel("TÌNH TRẠNG:"), gbc);
            gbc.gridx = 1;
            tinhTrangCombo.setPreferredSize(new Dimension(250, 25));
            leftPanel.add(tinhTrangCombo, gbc);
            
            topPanel.add(leftPanel, BorderLayout.WEST);
            
            // Right panel: Image
            JPanel rightPanel = new JPanel(new BorderLayout(0, 10));
            rightPanel.setBackground(orangeBg);
            rightPanel.setPreferredSize(new Dimension(250, 300));
            
            // Button panel
            JPanel imageButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            imageButtonPanel.setBackground(orangeBg);
            imageButtonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
            imageButtonPanel.add(chooseImageButton);
            rightPanel.add(imageButtonPanel, BorderLayout.NORTH);
            
            // Image preview panel - use GridBagLayout for better control
            JPanel imagePreviewPanel = new JPanel(new GridBagLayout());
            imagePreviewPanel.setBackground(orangeBg);
            GridBagConstraints imgGbc = new GridBagConstraints();
            imgGbc.gridx = 0;
            imgGbc.gridy = 0;
            imgGbc.anchor = GridBagConstraints.CENTER;
            imagePreviewPanel.add(imagePreviewLabel, imgGbc);
            rightPanel.add(imagePreviewPanel, BorderLayout.CENTER);
            
            topPanel.add(rightPanel, BorderLayout.EAST);
            
            mainPanel.add(topPanel, BorderLayout.NORTH);
            
            // Bottom panel: Ingredients management
            JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
            bottomPanel.setBackground(orangeBg);
            bottomPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "DANH SÁCH NGUYÊN LIỆU"));
            
            // Add ingredient form
            JPanel addIngredientPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
            addIngredientPanel.setBackground(orangeBg);
            
            addIngredientPanel.add(new JLabel("TÊN NGUYÊN LIỆU:"));
            nguyenLieuCombo.setPreferredSize(new Dimension(200, 25));
            addIngredientPanel.add(nguyenLieuCombo);
            
            addIngredientPanel.add(new JLabel("SỐ LƯỢNG:"));
            soLuongNLField.setPreferredSize(new Dimension(80, 25));
            addIngredientPanel.add(soLuongNLField);
            
            addIngredientPanel.add(new JLabel("ĐƠN VỊ:"));
            donViNLField.setPreferredSize(new Dimension(80, 25));
            addIngredientPanel.add(donViNLField);
            
            JButton addIngredientButton = new JButton("Thêm");
            addIngredientButton.setBackground(new Color(139, 90, 0));
            addIngredientButton.setForeground(Color.BLACK);
            addIngredientButton.setFocusPainted(false);
            addIngredientButton.addActionListener(e -> addIngredient());
            addIngredientPanel.add(addIngredientButton);
            
            bottomPanel.add(addIngredientPanel, BorderLayout.NORTH);
            
            // Ingredients table
            JScrollPane tableScroll = new JScrollPane(ingredientsTable);
            tableScroll.setPreferredSize(new Dimension(0, 200));
            tableScroll.setBackground(orangeBg);
            bottomPanel.add(tableScroll, BorderLayout.CENTER);
            
            mainPanel.add(bottomPanel, BorderLayout.CENTER);
            
            // Buttons panel
            JPanel buttonPanel = new JPanel(new FlowLayout());
            buttonPanel.setBackground(orangeBg);
            
            JButton saveButton = new JButton("Lưu");
            saveButton.setBackground(new Color(139, 90, 0));
            saveButton.setForeground(Color.BLACK);
            saveButton.setFocusPainted(false);
            saveButton.setPreferredSize(new Dimension(100, 30));
            saveButton.addActionListener(e -> saveData());
            
            JButton cancelButton = new JButton("Hủy");
            cancelButton.setBackground(new Color(200, 200, 200));
            cancelButton.setFocusPainted(false);
            cancelButton.setPreferredSize(new Dimension(100, 30));
            cancelButton.addActionListener(e -> dispose());
            
            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);
            
            add(mainPanel);
        }
        
        private void setupEventHandlers() {
            // Double click to delete ingredient
            ingredientsTable.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    if (evt.getClickCount() == 2 && ingredientsTable.getSelectedRow() >= 0) {
                        deleteIngredient();
                    }
                }
            });
        }
        
        private void loadNguyenLieu() {
            nguyenLieuCombo.removeAllItems();
            try {
                List<NguyenLieuDTO> list = hangHoaDAO.layTatCaNguyenLieu();
                for (NguyenLieuDTO nl : list) {
                    nguyenLieuCombo.addItem(nl);
                }
                // Set renderer to show name
                nguyenLieuCombo.setRenderer(new DefaultListCellRenderer() {
                    @Override
                    public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                            boolean isSelected, boolean cellHasFocus) {
                        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                        if (value instanceof NguyenLieuDTO) {
                            setText(((NguyenLieuDTO) value).getTenNL());
                        }
                        return this;
                    }
                });
            } catch (Exception e) {
            }
        }
        
        private void loadIngredients() {
            if (mon == null) return;
            
            ingredientsList = hangHoaDAO.layNguyenLieuCuaMon(mon.getMaMon());
            refreshIngredientsTable();
        }
        
        private void refreshIngredientsTable() {
            ingredientsTableModel.setRowCount(0);
            for (MonNguyenLieuDTO dto : ingredientsList) {
                ingredientsTableModel.addRow(new Object[]{
                    dto.getTenNL(),
                    dto.getSoLuong(),
                    dto.getDonVi()
                });
            }
        }
        
        private void addIngredient() {
            NguyenLieuDTO selected = (NguyenLieuDTO) nguyenLieuCombo.getSelectedItem();
            if (selected == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn nguyên liệu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int soLuong;
            try {
                soLuong = Integer.parseInt(soLuongNLField.getText().trim());
                if (soLuong <= 0) {
                    JOptionPane.showMessageDialog(this, "Số lượng phải lớn hơn 0!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Số lượng phải là số hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Check if ingredient already exists
            for (MonNguyenLieuDTO dto : ingredientsList) {
                if (dto.getMaNL() == selected.getMaNL()) {
                    dto.setSoLuong(dto.getSoLuong() + soLuong);
                    refreshIngredientsTable();
                    soLuongNLField.setText("0");
                    return;
                }
            }
            
            // Add new ingredient
            MonNguyenLieuDTO newIngredient = new MonNguyenLieuDTO();
            newIngredient.setMaNL(selected.getMaNL());
            newIngredient.setTenNL(selected.getTenNL());
            newIngredient.setSoLuong(soLuong);
            newIngredient.setDonVi(selected.getDonVi());
            
            if (mon != null) {
                newIngredient.setMaMon(mon.getMaMon());
            }
            
            ingredientsList.add(newIngredient);
            refreshIngredientsTable();
            soLuongNLField.setText("0");
        }
        
        private void deleteIngredient() {
            int selectedRow = ingredientsTable.getSelectedRow();
            if (selectedRow < 0 || selectedRow >= ingredientsList.size()) {
                return;
            }
            
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Bạn có chắc muốn xóa nguyên liệu này?", 
                "Xác nhận", 
                JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                ingredientsList.remove(selectedRow);
                refreshIngredientsTable();
            }
        }
        
        private void saveData() {
            String ten = tenField.getText().trim();
            String giaStr = giaField.getText().trim();
            String loai = (String) loaiCombo.getSelectedItem();
            String tinhTrangUI = (String) tinhTrangCombo.getSelectedItem();
            String tinhTrang = convertTinhTrangToDatabase(tinhTrangUI);
            
            if (ten.isEmpty() || giaStr.isEmpty()) {
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
            
            Connection conn = null;
            try {
                conn = DBUtil.getConnection();
                conn.setAutoCommit(false);
                
                // Get MaLoai from TenLoai
                int maLoai = 1;
                try (PreparedStatement ps = conn.prepareStatement("SELECT MaLoai FROM loaimon WHERE TenLoai = ?")) {
                    ps.setString(1, loai);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        maLoai = rs.getInt("MaLoai");
                    } else {
                        if (conn != null) conn.rollback();
                        JOptionPane.showMessageDialog(this, "Không tìm thấy loại món: " + loai, "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
                
                String anh = anhField.getText().trim();
                int maMon;
                
                if (mon == null) {
                    // Thêm mới
                    try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO mon (TenMon, Gia, TinhTrang, MaLoai, Anh) VALUES (?, ?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS)) {
                        ps.setString(1, ten);
                        ps.setLong(2, gia);
                        ps.setString(3, tinhTrang);
                        ps.setInt(4, maLoai);
                        ps.setString(5, anh);
                        ps.executeUpdate();
                        
                        ResultSet rs = ps.getGeneratedKeys();
                        if (rs.next()) {
                            maMon = rs.getInt(1);
                        } else {
                            if (conn != null) conn.rollback();
                            JOptionPane.showMessageDialog(this, "Lỗi khi lấy mã món!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    }
                    JOptionPane.showMessageDialog(this, "Thêm thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    // Sửa
                    maMon = mon.getMaMon();
                    try (PreparedStatement ps = conn.prepareStatement(
                        "UPDATE mon SET TenMon=?, Gia=?, TinhTrang=?, MaLoai=?, Anh=? WHERE MaMon=?")) {
                        ps.setString(1, ten);
                        ps.setLong(2, gia);
                        ps.setString(3, tinhTrang);
                        ps.setInt(4, maLoai);
                        ps.setString(5, anh);
                        ps.setInt(6, maMon);
                        ps.executeUpdate();
                    }
                    JOptionPane.showMessageDialog(this, "Sửa thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                }
                
                // Save ingredients - sử dụng batch insert để tối ưu hiệu suất
                if (mon != null) {
                    // Xóa tất cả nguyên liệu cũ (sử dụng connection hiện tại)
                    try (PreparedStatement ps = conn.prepareStatement("DELETE FROM mon_nguyenlieu WHERE MaMon = ?")) {
                        ps.setInt(1, maMon);
                        ps.executeUpdate();
                    }
                }
                if (!ingredientsList.isEmpty()) {
                    hangHoaDAO.themNhieuNguyenLieuVaoMon(conn, maMon, ingredientsList);
                }
                
                conn.commit();
                dataChanged = true;
                dispose();
            } catch (SQLException e) {
                if (conn != null) {
                    try {
                        conn.rollback();
                    } catch (SQLException rollbackEx) {
                        // Ignore rollback errors
                    }
                }
                JOptionPane.showMessageDialog(this, "Lỗi lưu dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            } finally {
                if (conn != null) {
                    try {
                        conn.setAutoCommit(true);
                        conn.close();
                    } catch (SQLException e) {
                        // Ignore close errors
                    }
                }
            }
        }
        
        public boolean isDataChanged() {
            return dataChanged;
        }
        
        private void chooseImage() {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new java.io.File("src/images"));
            fileChooser.setDialogTitle("Chọn ảnh món");
            fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
                @Override
                public boolean accept(java.io.File f) {
                    return f.isDirectory() || f.getName().toLowerCase().endsWith(".jpg") || 
                           f.getName().toLowerCase().endsWith(".jpeg") || 
                           f.getName().toLowerCase().endsWith(".png") || 
                           f.getName().toLowerCase().endsWith(".gif");
                }
                
                @Override
                public String getDescription() {
                    return "Image files (*.jpg, *.jpeg, *.png, *.gif)";
                }
            });
            
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                java.io.File selectedFile = fileChooser.getSelectedFile();
                String relativePath = getRelativePath(selectedFile);
                anhField.setText(relativePath);
                loadImagePreview(relativePath);
            }
        }
        
        private String getRelativePath(java.io.File file) {
            String fullPath = file.getAbsolutePath();
            String srcPath = new java.io.File("src").getAbsolutePath();
            
            if (fullPath.startsWith(srcPath)) {
                return fullPath.substring(srcPath.length() + 1).replace("\\", "/");
            }
            return file.getName();
        }
        
        private void loadImagePreview(String imagePath) {
            if (imagePath == null || imagePath.trim().isEmpty()) {
                imagePreviewLabel.setText("Chưa chọn ảnh");
                imagePreviewLabel.setIcon(null);
                return;
            }
            
            try {
                String fullPath = "src/" + imagePath;
                java.io.File imageFile = new java.io.File(fullPath);
                
                if (imageFile.exists()) {
                    ImageIcon icon = new ImageIcon(fullPath);
                    Image image = icon.getImage();
                    
                    // Scale image to fit in preview (200x200 label)
                    int maxWidth = 190;
                    int maxHeight = 190;
                    int width = image.getWidth(null);
                    int height = image.getHeight(null);
                    
                    if (width > 0 && height > 0) {
                        double scale = Math.min((double) maxWidth / width, (double) maxHeight / height);
                        int newWidth = (int) (width * scale);
                        int newHeight = (int) (height * scale);
                        
                        Image scaledImage = image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
                        ImageIcon scaledIcon = new ImageIcon(scaledImage);
                        
                        imagePreviewLabel.setText("");
                        imagePreviewLabel.setIcon(scaledIcon);
                        imagePreviewLabel.setHorizontalAlignment(JLabel.CENTER);
                        imagePreviewLabel.setVerticalAlignment(JLabel.CENTER);
                    } else {
                        imagePreviewLabel.setText("Lỗi kích thước ảnh");
                        imagePreviewLabel.setIcon(null);
                    }
                } else {
                    imagePreviewLabel.setText("Không tìm thấy ảnh");
                    imagePreviewLabel.setIcon(null);
                }
            } catch (Exception e) {
                imagePreviewLabel.setText("Lỗi tải ảnh");
                imagePreviewLabel.setIcon(null);
            }
        }
    }
    
    // Inner class for NguyenLieu Add/Edit dialog
    private class LoaiMonDialog extends JDialog {
        private JTextField tenField;
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
            
            if (loaiMon != null) {
                tenField.setText(loaiMon.getTenLoai());
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
            
            if (ten.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập tên loại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
          
            
            try (Connection conn = DBUtil.getConnection()) {
                if (loaiMon == null) {
                    // Thêm mới
                    PreparedStatement ps = conn.prepareStatement("INSERT INTO loaimon (TenLoai) VALUES (?)");
                    ps.setString(1, ten);
                    ps.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Thêm loại món thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    // Cập nhật
                    PreparedStatement ps = conn.prepareStatement("UPDATE loaimon SET TenLoai=? WHERE MaLoai=?");
                    ps.setString(1, ten);
                    ps.setInt(2, loaiMon.getMaLoai());
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
            setSize(360, 250);
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
    
    // Custom cell renderer for image column - tối ưu hóa với cache
    private class ImageCellRenderer extends DefaultTableCellRenderer {
        private java.util.Map<String, ImageIcon> imageCache = new java.util.HashMap<>();
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = new JLabel();
            label.setOpaque(true);
            label.setHorizontalAlignment(JLabel.CENTER);
            label.setVerticalAlignment(JLabel.CENTER);
            
            // Set background color based on selection
            if (isSelected) {
                label.setBackground(table.getSelectionBackground());
                label.setForeground(table.getSelectionForeground());
            } else {
                label.setBackground(table.getBackground());
                label.setForeground(table.getForeground());
            }
            
            if (value != null && !value.toString().trim().isEmpty()) {
                String imagePath = value.toString();
                try {
                    // Kiểm tra cache trước
                    ImageIcon cachedIcon = imageCache.get(imagePath);
                    if (cachedIcon != null) {
                        label.setIcon(cachedIcon);
                        label.setText("");
                        return label;
                    }
                    
                    String fullPath = "src/" + imagePath;
                    java.io.File imageFile = new java.io.File(fullPath);
                    
                    if (imageFile.exists()) {
                        ImageIcon icon = new ImageIcon(fullPath);
                        Image image = icon.getImage();
                        
                        // Scale image to fit in cell (25x25 để phù hợp với row height 30)
                        int maxWidth = 25;
                        int maxHeight = 25;
                        int width = image.getWidth(null);
                        int height = image.getHeight(null);
                        
                        if (width > 0 && height > 0) {
                            double scale = Math.min((double) maxWidth / width, (double) maxHeight / height);
                            int newWidth = (int) (width * scale);
                            int newHeight = (int) (height * scale);
                            
                            Image scaledImage = image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
                            ImageIcon scaledIcon = new ImageIcon(scaledImage);
                            
                            // Cache icon để tăng tốc độ
                            imageCache.put(imagePath, scaledIcon);
                            
                            label.setIcon(scaledIcon);
                            label.setText("");
                        } else {
                            label.setIcon(null);
                            label.setText("No Image");
                        }
                    } else {
                        label.setIcon(null);
                        label.setText("No Image");
                    }
                } catch (Exception e) {
                    label.setIcon(null);
                    label.setText("Error");
                }
            } else {
                label.setIcon(null);
                label.setText("No Image");
            }
            
            return label;
        }
    }
    
    // Method chuyển đổi trạng thái từ giao diện sang database
    private String convertTinhTrangToDatabase(String tinhTrangUI) {
        if ("Đang bán".equals(tinhTrangUI)) {
            return "dangban";
        } else if ("Tạm ngừng".equals(tinhTrangUI)) {
            return "ngungban";
        }
        return "dangban"; // Mặc định
    }
    
    // Method chuyển đổi trạng thái từ database sang giao diện
    private String convertTinhTrangToUI(String tinhTrangDB) {
        if ("dangban".equals(tinhTrangDB)) {
            return "Đang bán";
        } else if ("ngungban".equals(tinhTrangDB)) {
            return "Tạm ngừng";
        }
        return "Đang bán"; // Mặc định
    }

    // Hàm reload lại combo loại từ DB
    private void reloadLoaiMonFilterCombo() {
        loaiMonFilterCombo.removeAllItems();
        loaiMonFilterCombo.addItem("Tất cả loại");
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT TenLoai FROM loaimon ORDER BY MaLoai")) {
            while (rs.next()) {
                loaiMonFilterCombo.addItem(rs.getString("TenLoai"));
            }
        } catch (SQLException e) {}
    }

    private void refreshSearchPanel() {
        if (searchPanel == null) return; // Nếu chưa tạo, bỏ qua
        searchPanel.removeAll();
        searchPanel.add(new JLabel("Tìm kiếm:"));
        searchPanel.add(searchCombo);
        searchPanel.add(searchField);
        if (currentView != null && currentView.equals("MON")) {
            reloadLoaiMonFilterCombo(); // chắc chắn update cho combo
            JLabel lblLoai = new JLabel("Loại:");
            searchPanel.add(lblLoai);
            searchPanel.add(loaiMonFilterCombo);
            loaiMonFilterCombo.setVisible(true);
            
            JLabel lblTinhTrang = new JLabel("Tình trạng:");
            searchPanel.add(lblTinhTrang);
            searchPanel.add(tinhTrangFilterCombo);
            tinhTrangFilterCombo.setVisible(true);
        } else {
            loaiMonFilterCombo.setVisible(false);
            tinhTrangFilterCombo.setVisible(false);
        }
        JButton searchButton = new JButton("🔍 Tìm");
        searchButton.setBackground(new Color(70, 130, 180));
        searchButton.setForeground(Color.BLACK);
        searchButton.setFocusPainted(false);
        searchButton.addActionListener(e -> performSearch());
        searchPanel.add(searchButton);
        JButton refreshButton = new JButton("🔄 Làm mới");
        refreshButton.setBackground(new Color(34, 139, 34));
        refreshButton.setForeground(Color.BLACK);
        refreshButton.setFocusPainted(false);
        refreshButton.addActionListener(e -> {
            // Reset search field
            searchField.setText("");
            // Reset combo filter về "Tất cả" (nếu đang ở view MON)
            if (currentView != null && currentView.equals("MON")) {
                loaiMonFilterCombo.setSelectedIndex(0);
                tinhTrangFilterCombo.setSelectedIndex(0);
            }
            loadData();
        });
        searchPanel.add(refreshButton);
        searchPanel.revalidate();
        searchPanel.repaint();
    }
}
