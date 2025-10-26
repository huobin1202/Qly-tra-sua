package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import dao.ThongKeDAO;
import dto.ThongKeDTO;

public class ThongKeView extends JPanel {
    private JTabbedPane tabbedPane;
    private final ThongKeDAO thongKeDAO;
    private JComboBox<String> yearCombo;
    private JTextField fromDateField;
    private JTextField toDateField;
    
    public ThongKeView() {
        thongKeDAO = new ThongKeDAO();
        initializeComponents();
        setupLayout();
        loadInitialData();
    }
    
    private void initializeComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 248, 255));
        
        // Tạo tabbed pane
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));
        
        // Tạo các tab
        createTongQuanTab();
        createMonBanChayTab();
        createDoanhThuTab();
        createNhanVienTab();
        createKhachHangTab();
        createBaoCaoTab();
        
        // Tạo panel điều khiển
        createControlPanel();
    }
    
    private void setupLayout() {
        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(70, 130, 180));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("📊 THỐNG KÊ & BÁO CÁO");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Control panel
        JPanel controlPanel = createControlPanel();
        headerPanel.add(controlPanel, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    private JPanel createControlPanel() {
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        controlPanel.setOpaque(false);
        
        // Từ ngày
        controlPanel.add(new JLabel("Từ:"));
        fromDateField = new JTextField(10);
        fromDateField.setFont(new Font("Arial", Font.PLAIN, 12));
        controlPanel.add(fromDateField);
        
        // Đến ngày
        controlPanel.add(new JLabel("Đến:"));
        toDateField = new JTextField(10);
        toDateField.setFont(new Font("Arial", Font.PLAIN, 12));
        controlPanel.add(toDateField);
        
        // Năm
        controlPanel.add(new JLabel("Năm:"));
        yearCombo = new JComboBox<>();
        yearCombo.setFont(new Font("Arial", Font.PLAIN, 12));
        populateYearCombo();
        controlPanel.add(yearCombo);
        
        // Nút làm mới
        JButton refreshButton = new JButton("🔄 Làm mới");
        refreshButton.setFont(new Font("Arial", Font.BOLD, 12));
        refreshButton.setBackground(new Color(34, 139, 34));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.addActionListener(e -> refreshAllTabs());
        controlPanel.add(refreshButton);
        
        // Set default dates
        setDefaultDates();
        
        return controlPanel;
    }
    
    private void populateYearCombo() {
        Calendar cal = Calendar.getInstance();
        int currentYear = cal.get(Calendar.YEAR);
        
        for (int i = currentYear - 5; i <= currentYear + 1; i++) {
            yearCombo.addItem(String.valueOf(i));
        }
        yearCombo.setSelectedItem(String.valueOf(currentYear));
    }
    
    private void setDefaultDates() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        
        // Đến ngày = hôm nay
        toDateField.setText(sdf.format(cal.getTime()));
        
        // Từ ngày = đầu tháng
        cal.set(Calendar.DAY_OF_MONTH, 1);
        fromDateField.setText(sdf.format(cal.getTime()));
    }
    
    private void createTongQuanTab() {
        JPanel tongQuanPanel = new JPanel(new BorderLayout());
        tongQuanPanel.setBackground(new Color(240, 248, 255));
        
        // Header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(new Color(240, 248, 255));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel("📈 TỔNG QUAN HỆ THỐNG");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(70, 130, 180));
        headerPanel.add(titleLabel);
        
        tongQuanPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Content panel với grid layout
        JPanel contentPanel = new JPanel(new GridLayout(2, 3, 20, 20));
        contentPanel.setBackground(new Color(240, 248, 255));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Tạo các card thống kê
        contentPanel.add(createStatCard("💰 TỔNG DOANH THU", "0 VNĐ", new Color(46, 125, 50)));
        contentPanel.add(createStatCard("👥 KHÁCH HÀNG", "0", new Color(156, 39, 176)));
        contentPanel.add(createStatCard("👨‍💼 NHÂN VIÊN", "0", new Color(255, 87, 34)));
        contentPanel.add(createStatCard("🍴 MÓN ĂN", "0", new Color(0, 150, 136)));
        contentPanel.add(createStatCard("📦 NGUYÊN LIỆU", "0", new Color(121, 85, 72)));
        contentPanel.add(createStatCard("🏢 NHÀ CUNG CẤP", "0", new Color(63, 81, 181)));
        
        tongQuanPanel.add(contentPanel, BorderLayout.CENTER);
        
        tabbedPane.addTab("Tổng quan", tongQuanPanel);
    }
    
    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(new Color(100, 100, 100));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 24));
        valueLabel.setForeground(color);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        
        return card;
    }
    
    private void createMonBanChayTab() {
        JPanel monBanChayPanel = new JPanel(new BorderLayout());
        monBanChayPanel.setBackground(new Color(240, 248, 255));
        
        // Header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(new Color(240, 248, 255));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel("🏆 MÓN BÁN CHẠY NHẤT");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(70, 130, 180));
        headerPanel.add(titleLabel);
        
        monBanChayPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"STT", "Tên món", "Số lượng bán", "Tổng tiền"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable table = new JTable(tableModel);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(70, 130, 180));
        table.getTableHeader().setForeground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        monBanChayPanel.add(scrollPane, BorderLayout.CENTER);
        
        tabbedPane.addTab("Món bán chạy", monBanChayPanel);
    }
    
    private void createDoanhThuTab() {
        JPanel doanhThuPanel = new JPanel(new BorderLayout());
        doanhThuPanel.setBackground(new Color(240, 248, 255));
        
        // Header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(new Color(240, 248, 255));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel("📊 DOANH THU");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(70, 130, 180));
        headerPanel.add(titleLabel);
        
        doanhThuPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Content với 2 bảng
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        contentPanel.setBackground(new Color(240, 248, 255));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Bảng doanh thu theo ngày
        JPanel dailyPanel = new JPanel(new BorderLayout());
        dailyPanel.setBackground(Color.WHITE);
        dailyPanel.setBorder(BorderFactory.createTitledBorder("Doanh thu theo ngày"));
        
        String[] dailyColumns = {"Ngày", "Doanh thu"};
        DefaultTableModel dailyModel = new DefaultTableModel(dailyColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable dailyTable = new JTable(dailyModel);
        dailyTable.setFont(new Font("Arial", Font.PLAIN, 12));
        dailyTable.setRowHeight(25);
        dailyTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        JScrollPane dailyScrollPane = new JScrollPane(dailyTable);
        dailyPanel.add(dailyScrollPane, BorderLayout.CENTER);
        
        // Bảng doanh thu theo tháng
        JPanel monthlyPanel = new JPanel(new BorderLayout());
        monthlyPanel.setBackground(Color.WHITE);
        monthlyPanel.setBorder(BorderFactory.createTitledBorder("Doanh thu theo tháng"));
        
        String[] monthlyColumns = {"Tháng", "Doanh thu"};
        DefaultTableModel monthlyModel = new DefaultTableModel(monthlyColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable monthlyTable = new JTable(monthlyModel);
        monthlyTable.setFont(new Font("Arial", Font.PLAIN, 12));
        monthlyTable.setRowHeight(25);
        monthlyTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        JScrollPane monthlyScrollPane = new JScrollPane(monthlyTable);
        monthlyPanel.add(monthlyScrollPane, BorderLayout.CENTER);
        
        contentPanel.add(dailyPanel);
        contentPanel.add(monthlyPanel);
        
        doanhThuPanel.add(contentPanel, BorderLayout.CENTER);
        
        tabbedPane.addTab("Doanh thu", doanhThuPanel);
    }
    
    private void createNhanVienTab() {
        JPanel nhanVienPanel = new JPanel(new BorderLayout());
        nhanVienPanel.setBackground(new Color(240, 248, 255));
        
        // Header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(new Color(240, 248, 255));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel("👨‍💼 THỐNG KÊ NHÂN VIÊN");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(70, 130, 180));
        headerPanel.add(titleLabel);
        
        nhanVienPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"STT", "Tên nhân viên", "Số đơn hàng", "Doanh thu"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable table = new JTable(tableModel);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(70, 130, 180));
        table.getTableHeader().setForeground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        nhanVienPanel.add(scrollPane, BorderLayout.CENTER);
        
        tabbedPane.addTab("Nhân viên", nhanVienPanel);
    }
    
    private void createKhachHangTab() {
        JPanel khachHangPanel = new JPanel(new BorderLayout());
        khachHangPanel.setBackground(new Color(240, 248, 255));
        
        // Header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(new Color(240, 248, 255));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel("👥 THỐNG KÊ KHÁCH HÀNG");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(70, 130, 180));
        headerPanel.add(titleLabel);
        
        khachHangPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Content với 2 bảng
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        contentPanel.setBackground(new Color(240, 248, 255));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Bảng khách hàng mới theo tháng
        JPanel newCustomerPanel = new JPanel(new BorderLayout());
        newCustomerPanel.setBackground(Color.WHITE);
        newCustomerPanel.setBorder(BorderFactory.createTitledBorder("Khách hàng mới theo tháng"));
        
        String[] newCustomerColumns = {"Tháng", "Số khách hàng"};
        DefaultTableModel newCustomerModel = new DefaultTableModel(newCustomerColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable newCustomerTable = new JTable(newCustomerModel);
        newCustomerTable.setFont(new Font("Arial", Font.PLAIN, 12));
        newCustomerTable.setRowHeight(25);
        newCustomerTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        JScrollPane newCustomerScrollPane = new JScrollPane(newCustomerTable);
        newCustomerPanel.add(newCustomerScrollPane, BorderLayout.CENTER);
        
        // Bảng đơn hàng theo trạng thái
        JPanel orderStatusPanel = new JPanel(new BorderLayout());
        orderStatusPanel.setBackground(Color.WHITE);
        orderStatusPanel.setBorder(BorderFactory.createTitledBorder("Đơn hàng theo trạng thái"));
        
        String[] orderStatusColumns = {"Trạng thái", "Số đơn hàng"};
        DefaultTableModel orderStatusModel = new DefaultTableModel(orderStatusColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable orderStatusTable = new JTable(orderStatusModel);
        orderStatusTable.setFont(new Font("Arial", Font.PLAIN, 12));
        orderStatusTable.setRowHeight(25);
        orderStatusTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        JScrollPane orderStatusScrollPane = new JScrollPane(orderStatusTable);
        orderStatusPanel.add(orderStatusScrollPane, BorderLayout.CENTER);
        
        contentPanel.add(newCustomerPanel);
        contentPanel.add(orderStatusPanel);
        
        khachHangPanel.add(contentPanel, BorderLayout.CENTER);
        
        tabbedPane.addTab("Khách hàng", khachHangPanel);
    }
    
    private void createBaoCaoTab() {
        BaoCaoView baoCaoView = new BaoCaoView();
        tabbedPane.addTab("Báo cáo", baoCaoView);
    }
    
    private void loadInitialData() {
        refreshAllTabs();
    }
    
    private void refreshAllTabs() {
        String fromDate = fromDateField.getText().trim();
        String toDate = toDateField.getText().trim();
        String year = (String) yearCombo.getSelectedItem();
        
        // Validate dates
        if (fromDate.isEmpty() || toDate.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ ngày bắt đầu và kết thúc!", 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Load data for each tab
        loadTongQuanData();
        loadMonBanChayData(fromDate, toDate);
        loadDoanhThuData(fromDate, toDate, year);
        loadNhanVienData(fromDate, toDate);
        loadKhachHangData(year);
    }
    
    private void loadTongQuanData() {
        ThongKeDTO tongQuan = thongKeDAO.thongKeTongQuan();
        
        // Update stat cards
        JPanel tongQuanPanel = (JPanel) tabbedPane.getComponentAt(0);
        JPanel contentPanel = (JPanel) tongQuanPanel.getComponent(1);
        
        Component[] cards = contentPanel.getComponents();
        
        // Update values
        ((JLabel) ((JPanel) cards[0]).getComponent(1)).setText(String.format("%,d VNĐ", tongQuan.getDoanhThu()));
        ((JLabel) ((JPanel) cards[1]).getComponent(1)).setText(String.valueOf(tongQuan.getSoKhachHang()));
        ((JLabel) ((JPanel) cards[2]).getComponent(1)).setText(String.valueOf(tongQuan.getSoNhanVien()));
        ((JLabel) ((JPanel) cards[3]).getComponent(1)).setText(String.valueOf(tongQuan.getSoMon()));
        ((JLabel) ((JPanel) cards[4]).getComponent(1)).setText(String.valueOf(tongQuan.getSoNguyenLieu()));
        ((JLabel) ((JPanel) cards[5]).getComponent(1)).setText(String.valueOf(tongQuan.getSoNhaCungCap()));
    }
    
    private void loadMonBanChayData(String fromDate, String toDate) {
        List<ThongKeDTO> data = thongKeDAO.thongKeMonBanChay(fromDate, toDate);
        
        JPanel monBanChayPanel = (JPanel) tabbedPane.getComponentAt(1);
        JScrollPane scrollPane = (JScrollPane) monBanChayPanel.getComponent(1);
        JTable table = (JTable) scrollPane.getViewport().getView();
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        
        model.setRowCount(0);
        
        int stt = 1;
        for (ThongKeDTO item : data) {
            Object[] row = {
                stt++,
                item.getTenMon(),
                item.getSoLuongBan(),
                String.format("%,d VNĐ", item.getTongTien())
            };
            model.addRow(row);
        }
    }
    
    private void loadDoanhThuData(String fromDate, String toDate, String year) {
        // Load daily revenue
        List<ThongKeDTO> dailyData = thongKeDAO.thongKeDoanhThuTheoNgay(fromDate, toDate);
        
        JPanel doanhThuPanel = (JPanel) tabbedPane.getComponentAt(2);
        JPanel contentPanel = (JPanel) doanhThuPanel.getComponent(1);
        JPanel dailyPanel = (JPanel) contentPanel.getComponent(0);
        JScrollPane dailyScrollPane = (JScrollPane) dailyPanel.getComponent(0);
        JTable dailyTable = (JTable) dailyScrollPane.getViewport().getView();
        DefaultTableModel dailyModel = (DefaultTableModel) dailyTable.getModel();
        
        dailyModel.setRowCount(0);
        for (ThongKeDTO item : dailyData) {
            Object[] row = {
                item.getNgay(),
                String.format("%,d VNĐ", item.getDoanhThu())
            };
            dailyModel.addRow(row);
        }
        
        // Load monthly revenue
        List<ThongKeDTO> monthlyData = thongKeDAO.thongKeDoanhThuTheoThang(year);
        
        JPanel monthlyPanel = (JPanel) contentPanel.getComponent(1);
        JScrollPane monthlyScrollPane = (JScrollPane) monthlyPanel.getComponent(0);
        JTable monthlyTable = (JTable) monthlyScrollPane.getViewport().getView();
        DefaultTableModel monthlyModel = (DefaultTableModel) monthlyTable.getModel();
        
        monthlyModel.setRowCount(0);
        for (ThongKeDTO item : monthlyData) {
            Object[] row = {
                item.getThang(),
                String.format("%,d VNĐ", item.getDoanhThu())
            };
            monthlyModel.addRow(row);
        }
    }
    
    private void loadNhanVienData(String fromDate, String toDate) {
        List<ThongKeDTO> data = thongKeDAO.thongKeNhanVienBanHang(fromDate, toDate);
        
        JPanel nhanVienPanel = (JPanel) tabbedPane.getComponentAt(3);
        JScrollPane scrollPane = (JScrollPane) nhanVienPanel.getComponent(1);
        JTable table = (JTable) scrollPane.getViewport().getView();
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        
        model.setRowCount(0);
        
        int stt = 1;
        for (ThongKeDTO item : data) {
            Object[] row = {
                stt++,
                item.getTenNhanVien(),
                item.getSoDonHang(),
                String.format("%,d VNĐ", item.getDoanhThu())
            };
            model.addRow(row);
        }
    }
    
    private void loadKhachHangData(String year) {
        // Load new customers by month
        List<ThongKeDTO> newCustomerData = thongKeDAO.thongKeKhachHangMoiTheoThang(year);
        
        JPanel khachHangPanel = (JPanel) tabbedPane.getComponentAt(4);
        JPanel contentPanel = (JPanel) khachHangPanel.getComponent(1);
        JPanel newCustomerPanel = (JPanel) contentPanel.getComponent(0);
        JScrollPane newCustomerScrollPane = (JScrollPane) newCustomerPanel.getComponent(0);
        JTable newCustomerTable = (JTable) newCustomerScrollPane.getViewport().getView();
        DefaultTableModel newCustomerModel = (DefaultTableModel) newCustomerTable.getModel();
        
        newCustomerModel.setRowCount(0);
        for (ThongKeDTO item : newCustomerData) {
            Object[] row = {
                item.getThang(),
                item.getSoKhachHang()
            };
            newCustomerModel.addRow(row);
        }
        
        // Load order status
        List<ThongKeDTO> orderStatusData = thongKeDAO.thongKeDonHangTheoTrangThai();
        
        JPanel orderStatusPanel = (JPanel) contentPanel.getComponent(1);
        JScrollPane orderStatusScrollPane = (JScrollPane) orderStatusPanel.getComponent(0);
        JTable orderStatusTable = (JTable) orderStatusScrollPane.getViewport().getView();
        DefaultTableModel orderStatusModel = (DefaultTableModel) orderStatusTable.getModel();
        
        orderStatusModel.setRowCount(0);
        for (ThongKeDTO item : orderStatusData) {
            String trangThai = convertTrangThaiToUI(item.getTrangThai());
            Object[] row = {
                trangThai,
                item.getSoDonHang()
            };
            orderStatusModel.addRow(row);
        }
    }
    
    private String convertTrangThaiToUI(String trangThaiDB) {
        switch (trangThaiDB) {
            case "dathanhtoan": return "Đã thanh toán";
            case "chuathanhtoan": return "Chưa thanh toán";
            case "bihuy": return "Bị hủy";
            default: return trangThaiDB;
        }
    }
}
