package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Calendar;
import java.util.List;

import dao.ThongKeDAO;
import dto.ThongKeDTO;
import utils.DateChooserComponent;

public class ThongKeView extends JPanel {
    private JTabbedPane tabbedPane;
    private final ThongKeDAO thongKeDAO;
    private DateChooserComponent fromDatePicker;
    private DateChooserComponent toDatePicker;
    
    // Lưu reference đến các label trong tab tổng quan để cập nhật dễ dàng hơn
    private JLabel doanhThuLabel, khachHangLabel, nhanVienLabel, monLabel, nguyenLieuLabel, nhaCungCapLabel;
    
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
        fromDatePicker = new DateChooserComponent();
        fromDatePicker.setFont(new Font("Arial", Font.PLAIN, 12));
        controlPanel.add(fromDatePicker);
        
        // Đến ngày
        controlPanel.add(new JLabel("Đến:"));
        toDatePicker = new DateChooserComponent();
        toDatePicker.setFont(new Font("Arial", Font.PLAIN, 12));
        controlPanel.add(toDatePicker);
        
        // Nút tìm kiếm
        JButton searchButton = new JButton("\uD83D\uDD0D Tìm");
        searchButton.setFont(new Font("Arial", Font.BOLD, 12));
        searchButton.setBackground(new Color(70, 130, 180));
        searchButton.setForeground(Color.BLACK);
        searchButton.setFocusPainted(false);
        // Khi bấm, chỉ filter theo from-to và năm hiện chọn, không reset về mặc định
        searchButton.addActionListener(e -> refreshAllTabs());
        controlPanel.add(searchButton);

        // Nút làm mới (hiển thị tất cả)
        JButton refreshButton = new JButton("\u21BB Làm mới");
        refreshButton.setFont(new Font("Arial", Font.BOLD, 12));
        refreshButton.setBackground(new Color(34, 139, 34));
        refreshButton.setForeground(Color.BLACK);
        refreshButton.setFocusPainted(false);
        refreshButton.addActionListener(e -> {
            setDefaultDates();
            refreshAllTabs();
        });
        controlPanel.add(refreshButton);
        
        // Set default dates
        setDefaultDates();
        
        return controlPanel;
    }
    
    private void setDefaultDates() {
        Calendar cal = Calendar.getInstance();
        
        // Đến ngày = hôm nay
        toDatePicker.setDate(cal.getTime());
        
        // Từ ngày = đầu tháng
        cal.set(Calendar.DAY_OF_MONTH, 1);
        fromDatePicker.setDate(cal.getTime());
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
        
        // Tạo các card thống kê và lưu reference đến labels
        JPanel card1 = createStatCard("💰 TỔNG DOANH THU", "0 VNĐ", new Color(46, 125, 50));
        doanhThuLabel = (JLabel) card1.getComponent(1);
        contentPanel.add(card1);
        
        JPanel card2 = createStatCard("👥 KHÁCH HÀNG", "0", new Color(156, 39, 176));
        khachHangLabel = (JLabel) card2.getComponent(1);
        contentPanel.add(card2);
        
        JPanel card3 = createStatCard("👨‍💼 NHÂN VIÊN", "0", new Color(255, 87, 34));
        nhanVienLabel = (JLabel) card3.getComponent(1);
        contentPanel.add(card3);
        
        JPanel card4 = createStatCard("🍴 MÓN ĂN", "0", new Color(0, 150, 136));
        monLabel = (JLabel) card4.getComponent(1);
        contentPanel.add(card4);
        
        JPanel card5 = createStatCard("📦 NGUYÊN LIỆU", "0", new Color(121, 85, 72));
        nguyenLieuLabel = (JLabel) card5.getComponent(1);
        contentPanel.add(card5);
        
        JPanel card6 = createStatCard("🏢 NHÀ CUNG CẤP", "0", new Color(63, 81, 181));
        nhaCungCapLabel = (JLabel) card6.getComponent(1);
        contentPanel.add(card6);
        
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
        table.getTableHeader().setForeground(Color.BLACK);
        
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
        
        contentPanel.add(dailyPanel);
        
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
        table.getTableHeader().setForeground(Color.BLACK);
        
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
        String fromDate = fromDatePicker.getSelectedDateString();
        String toDate = toDatePicker.getSelectedDateString();
        
        // Validate dates
        if (fromDate == null || fromDate.isEmpty() || toDate == null || toDate.isEmpty()) {
            // Nếu ngày rỗng, set lại ngày mặc định và thử lại
            setDefaultDates();
            fromDate = fromDatePicker.getSelectedDateString();
            toDate = toDatePicker.getSelectedDateString();
            
            if (fromDate == null || fromDate.isEmpty() || toDate == null || toDate.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ ngày bắt đầu và kết thúc!", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        
        // Load data for each tab
        try {
            loadTongQuanData();
            loadMonBanChayData(fromDate, toDate);
            loadDoanhThuData(fromDate, toDate);
            loadNhanVienData(fromDate, toDate);
            loadKhachHangData(fromDate, toDate);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Lỗi khi tải dữ liệu thống kê: " + e.getMessage(), 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void loadTongQuanData() {
        try {
            ThongKeDTO tongQuan = thongKeDAO.thongKeTongQuan();
            
            // Update values trực tiếp vào các label đã lưu
            if (doanhThuLabel != null) {
                doanhThuLabel.setText(String.format("%,d VNĐ", tongQuan.getDoanhThu()));
            }
            if (khachHangLabel != null) {
                khachHangLabel.setText(String.valueOf(tongQuan.getSoKhachHang()));
            }
            if (nhanVienLabel != null) {
                nhanVienLabel.setText(String.valueOf(tongQuan.getSoNhanVien()));
            }
            if (monLabel != null) {
                monLabel.setText(String.valueOf(tongQuan.getSoMon()));
            }
            if (nguyenLieuLabel != null) {
                nguyenLieuLabel.setText(String.valueOf(tongQuan.getSoNguyenLieu()));
            }
            if (nhaCungCapLabel != null) {
                nhaCungCapLabel.setText(String.valueOf(tongQuan.getSoNhaCungCap()));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Lỗi khi tải dữ liệu tổng quan: " + e.getMessage(), 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
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
    
    private void loadDoanhThuData(String fromDate, String toDate) {
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
    
    private void loadKhachHangData(String fromDate, String toDate) {
        // Load order status
        List<ThongKeDTO> orderStatusData = thongKeDAO.thongKeDonHangTheoTrangThai();
        
        JPanel khachHangPanel = (JPanel) tabbedPane.getComponentAt(4);
        JPanel contentPanel = (JPanel) khachHangPanel.getComponent(1);
        JPanel orderStatusPanel = (JPanel) contentPanel.getComponent(0);
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
