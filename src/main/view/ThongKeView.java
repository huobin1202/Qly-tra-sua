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
    private boolean showAllData = false; // Flag để đánh dấu lấy tất cả dữ liệu
    
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
        createNhapHangTab();
        createKhoHangTab();
        createBaoCaoTab();
        
        // Tạo panel điều khiển
        createControlPanel();
    }
    
    private void setupLayout() {
        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(70, 130, 180));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("THỐNG KÊ & BÁO CÁO");
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
        // Thêm listener để reset flag khi người dùng thay đổi ngày
        fromDatePicker.getDateSpinner().addChangeListener(e -> showAllData = false);
        controlPanel.add(fromDatePicker);
        
        // Đến ngày
        controlPanel.add(new JLabel("Đến:"));
        toDatePicker = new DateChooserComponent();
        toDatePicker.setFont(new Font("Arial", Font.PLAIN, 12));
        // Thêm listener để reset flag khi người dùng thay đổi ngày
        toDatePicker.getDateSpinner().addChangeListener(e -> showAllData = false);
        controlPanel.add(toDatePicker);
        
        // Nút tìm kiếm
        JButton searchButton = new JButton("🔍 Tìm");
        searchButton.setFont(new Font("Arial", Font.BOLD, 12));
        searchButton.setBackground(new Color(70, 130, 180));
        searchButton.setForeground(Color.BLACK);
        searchButton.setFocusPainted(false);
        // Khi bấm, filter theo ngày đã chọn
        searchButton.addActionListener(e -> {
            showAllData = false; // Không lấy tất cả, filter theo ngày
            refreshAllTabs();
        });
        controlPanel.add(searchButton);

        // Nút làm mới (hiển thị tất cả - lấy tất cả dữ liệu không filter theo ngày)
        JButton refreshButton = new JButton("🔄 Làm mới");
        refreshButton.setFont(new Font("Arial", Font.BOLD, 12));
        refreshButton.setBackground(new Color(34, 139, 34));
        refreshButton.setForeground(Color.BLACK);
        refreshButton.setFocusPainted(false);
        refreshButton.addActionListener(e -> {
            // Set flag để lấy tất cả dữ liệu
            showAllData = true;
            refreshAllTabs();
        });
        controlPanel.add(refreshButton);
        
        // Không set default dates - để người dùng tự chọn
        
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
        
        JLabel titleLabel = new JLabel("TỔNG QUAN HỆ THỐNG");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(70, 130, 180));
        headerPanel.add(titleLabel);
        
        tongQuanPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Content panel với grid layout
        JPanel contentPanel = new JPanel(new GridLayout(2, 3, 20, 20));
        contentPanel.setBackground(new Color(240, 248, 255));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Tạo các card thống kê và lưu reference đến labels
        JPanel card1 = createStatCard("TỔNG DOANH THU", "0 VNĐ", new Color(46, 125, 50));
        doanhThuLabel = (JLabel) card1.getComponent(1);
        contentPanel.add(card1);
        
        JPanel card2 = createStatCard("KHÁCH HÀNG", "0", new Color(156, 39, 176));
        khachHangLabel = (JLabel) card2.getComponent(1);
        contentPanel.add(card2);
        
        JPanel card3 = createStatCard("NHÂN VIÊN", "0", new Color(255, 87, 34));
        nhanVienLabel = (JLabel) card3.getComponent(1);
        contentPanel.add(card3);
        
        JPanel card4 = createStatCard("MÓN ĂN", "0", new Color(0, 150, 136));
        monLabel = (JLabel) card4.getComponent(1);
        contentPanel.add(card4);
        
        JPanel card5 = createStatCard("NGUYÊN LIỆU", "0", new Color(121, 85, 72));
        nguyenLieuLabel = (JLabel) card5.getComponent(1);
        contentPanel.add(card5);
        
        JPanel card6 = createStatCard("NHÀ CUNG CẤP", "0", new Color(63, 81, 181));
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
        
        JLabel titleLabel = new JLabel("MÓN BÁN CHẠY NHẤT");
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
        
        JLabel titleLabel = new JLabel("DOANH THU");
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
        
        JLabel titleLabel = new JLabel("THỐNG KÊ NHÂN VIÊN");
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
        
        JLabel titleLabel = new JLabel("THỐNG KÊ KHÁCH HÀNG");
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
        
        // Bảng top khách hàng VIP
        JPanel vipPanel = new JPanel(new BorderLayout());
        vipPanel.setBackground(Color.WHITE);
        vipPanel.setBorder(BorderFactory.createTitledBorder("Top khách hàng VIP"));
        
        String[] vipColumns = {"STT", "Tên khách hàng", "Số đơn hàng", "Tổng tiền"};
        DefaultTableModel vipModel = new DefaultTableModel(vipColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable vipTable = new JTable(vipModel);
        vipTable.setFont(new Font("Arial", Font.PLAIN, 12));
        vipTable.setRowHeight(25);
        vipTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        vipTable.getTableHeader().setBackground(new Color(70, 130, 180));
        vipTable.getTableHeader().setForeground(Color.BLACK);
        
        JScrollPane vipScrollPane = new JScrollPane(vipTable);
        vipPanel.add(vipScrollPane, BorderLayout.CENTER);
        
        contentPanel.add(vipPanel);
        
        khachHangPanel.add(contentPanel, BorderLayout.CENTER);
        
        tabbedPane.addTab("Khách hàng", khachHangPanel);
    }
    
    private void createNhapHangTab() {
        JPanel nhapHangPanel = new JPanel(new BorderLayout());
        nhapHangPanel.setBackground(new Color(240, 248, 255));
        
        // Header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(new Color(240, 248, 255));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel("📦 THỐNG KÊ NHẬP HÀNG");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(70, 130, 180));
        headerPanel.add(titleLabel);
        
        nhapHangPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Content với 2 bảng
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        contentPanel.setBackground(new Color(240, 248, 255));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Bảng chi phí nhập hàng theo ngày
        JPanel chiPhiPanel = new JPanel(new BorderLayout());
        chiPhiPanel.setBackground(Color.WHITE);
        chiPhiPanel.setBorder(BorderFactory.createTitledBorder("Chi phí nhập hàng theo ngày"));
        
        String[] chiPhiColumns = {"Ngày", "Chi phí"};
        DefaultTableModel chiPhiModel = new DefaultTableModel(chiPhiColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable chiPhiTable = new JTable(chiPhiModel);
        chiPhiTable.setFont(new Font("Arial", Font.PLAIN, 12));
        chiPhiTable.setRowHeight(25);
        chiPhiTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        JScrollPane chiPhiScrollPane = new JScrollPane(chiPhiTable);
        chiPhiPanel.add(chiPhiScrollPane, BorderLayout.CENTER);
        
        contentPanel.add(chiPhiPanel);
        
        // Bảng nhà cung cấp
        JPanel nccPanel = new JPanel(new BorderLayout());
        nccPanel.setBackground(Color.WHITE);
        nccPanel.setBorder(BorderFactory.createTitledBorder("Top nhà cung cấp"));
        
        String[] nccColumns = {"STT", "Tên nhà cung cấp", "Số phiếu", "Tổng chi phí"};
        DefaultTableModel nccModel = new DefaultTableModel(nccColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable nccTable = new JTable(nccModel);
        nccTable.setFont(new Font("Arial", Font.PLAIN, 12));
        nccTable.setRowHeight(25);
        nccTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        nccTable.getTableHeader().setBackground(new Color(70, 130, 180));
        nccTable.getTableHeader().setForeground(Color.BLACK);
        
        JScrollPane nccScrollPane = new JScrollPane(nccTable);
        nccPanel.add(nccScrollPane, BorderLayout.CENTER);
        
        contentPanel.add(nccPanel);
        
        nhapHangPanel.add(contentPanel, BorderLayout.CENTER);
        
        tabbedPane.addTab("Nhập hàng", nhapHangPanel);
    }
    
    private void createKhoHangTab() {
        JPanel khoHangPanel = new JPanel(new BorderLayout());
        khoHangPanel.setBackground(new Color(240, 248, 255));
        
        // Header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(new Color(240, 248, 255));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel("THỐNG KÊ KHO HÀNG");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(70, 130, 180));
        headerPanel.add(titleLabel);
        
        khoHangPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"STT", "Tên nguyên liệu", "Số lượng", "Đơn vị"};
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
        
        khoHangPanel.add(scrollPane, BorderLayout.CENTER);
        
        tabbedPane.addTab("Kho hàng", khoHangPanel);
    }
    
    private void createBaoCaoTab() {
        BaoCaoView baoCaoView = new BaoCaoView();
        tabbedPane.addTab("Báo cáo", baoCaoView);
    }
    
    private void loadInitialData() {
        // Chỉ load dữ liệu không cần ngày (tổng quan, kho hàng)
        try {
            loadTongQuanData();
            loadKhoHangData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void refreshAllTabs() {
        // Load data không cần ngày (tổng quan, kho hàng)
        try {
            loadTongQuanData();
            loadKhoHangData();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        String fromDate = null;
        String toDate = null;
        
        // Nếu không phải chế độ "lấy tất cả", thì lấy ngày từ date picker
        if (!showAllData) {
            fromDate = fromDatePicker.getSelectedDateString();
            toDate = toDatePicker.getSelectedDateString();
            
            // Nếu ngày rỗng, vẫn truyền null để lấy tất cả
            if (fromDate == null || fromDate.isEmpty()) {
                fromDate = null;
            }
            if (toDate == null || toDate.isEmpty()) {
                toDate = null;
            }
        }
        // Nếu showAllData = true, fromDate và toDate đã là null, sẽ lấy tất cả
        
        // Load data for each tab (nếu ngày null thì lấy tất cả)
        try {
            loadMonBanChayData(fromDate, toDate);
            loadDoanhThuData(fromDate, toDate);
            loadNhanVienData(fromDate, toDate);
            loadKhachHangData(fromDate, toDate);
            loadNhapHangData(fromDate, toDate);
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
        // Tìm JScrollPane trong panel (nằm ở BorderLayout.CENTER)
        JScrollPane scrollPane = null;
        for (Component comp : monBanChayPanel.getComponents()) {
            if (comp instanceof JScrollPane) {
                scrollPane = (JScrollPane) comp;
                break;
            }
        }
        if (scrollPane == null) return;
        
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
        // Tìm contentPanel (nằm ở BorderLayout.CENTER)
        JPanel contentPanel = null;
        for (Component comp : doanhThuPanel.getComponents()) {
            if (comp instanceof JPanel && comp != doanhThuPanel.getComponent(0)) {
                contentPanel = (JPanel) comp;
                break;
            }
        }
        if (contentPanel == null) return;
        
        // Tìm dailyPanel (component đầu tiên trong contentPanel)
        JPanel dailyPanel = (JPanel) contentPanel.getComponent(0);
        // Tìm JScrollPane trong dailyPanel
        JScrollPane dailyScrollPane = null;
        for (Component comp : dailyPanel.getComponents()) {
            if (comp instanceof JScrollPane) {
                dailyScrollPane = (JScrollPane) comp;
                break;
            }
        }
        if (dailyScrollPane == null) return;
        
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
        // Tìm JScrollPane trong panel (nằm ở BorderLayout.CENTER)
        JScrollPane scrollPane = null;
        for (Component comp : nhanVienPanel.getComponents()) {
            if (comp instanceof JScrollPane) {
                scrollPane = (JScrollPane) comp;
                break;
            }
        }
        if (scrollPane == null) return;
        
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
        
        // Load top khách hàng VIP
        List<ThongKeDTO> vipData = thongKeDAO.thongKeKhachHangVIP(fromDate, toDate);
        
        JPanel khachHangPanel = (JPanel) tabbedPane.getComponentAt(4);
        // Tìm contentPanel (nằm ở BorderLayout.CENTER)
        JPanel contentPanel = null;
        for (Component comp : khachHangPanel.getComponents()) {
            if (comp instanceof JPanel && comp != khachHangPanel.getComponent(0)) {
                contentPanel = (JPanel) comp;
                break;
            }
        }
        if (contentPanel == null) return;
        
        // Tìm orderStatusPanel (component đầu tiên trong contentPanel)
        JPanel orderStatusPanel = (JPanel) contentPanel.getComponent(0);
        // Tìm JScrollPane trong orderStatusPanel
        JScrollPane orderStatusScrollPane = null;
        for (Component comp : orderStatusPanel.getComponents()) {
            if (comp instanceof JScrollPane) {
                orderStatusScrollPane = (JScrollPane) comp;
                break;
            }
        }
        if (orderStatusScrollPane == null) return;
        
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
        
        // Load VIP data
        JPanel vipPanel = (JPanel) contentPanel.getComponent(1);
        JScrollPane vipScrollPane = null;
        for (Component comp : vipPanel.getComponents()) {
            if (comp instanceof JScrollPane) {
                vipScrollPane = (JScrollPane) comp;
                break;
            }
        }
        if (vipScrollPane == null) return;
        
        JTable vipTable = (JTable) vipScrollPane.getViewport().getView();
        DefaultTableModel vipModel = (DefaultTableModel) vipTable.getModel();
        
        vipModel.setRowCount(0);
        int stt = 1;
        for (ThongKeDTO item : vipData) {
            Object[] row = {
                stt++,
                item.getTenKhachHang(),
                item.getSoDonHang(),
                String.format("%,d VNĐ", item.getTongTien())
            };
            vipModel.addRow(row);
        }
    }
    
    private void loadNhapHangData(String fromDate, String toDate) {
        // Load chi phí nhập hàng
        List<ThongKeDTO> chiPhiData = thongKeDAO.thongKeChiPhiNhapHang(fromDate, toDate);
        
        // Load nhà cung cấp
        List<ThongKeDTO> nccData = thongKeDAO.thongKeNhaCungCap(fromDate, toDate);
        
        JPanel nhapHangPanel = (JPanel) tabbedPane.getComponentAt(5);
        JPanel contentPanel = null;
        for (Component comp : nhapHangPanel.getComponents()) {
            if (comp instanceof JPanel && comp != nhapHangPanel.getComponent(0)) {
                contentPanel = (JPanel) comp;
                break;
            }
        }
        if (contentPanel == null) return;
        
        // Load chi phí
        JPanel chiPhiPanel = (JPanel) contentPanel.getComponent(0);
        JScrollPane chiPhiScrollPane = null;
        for (Component comp : chiPhiPanel.getComponents()) {
            if (comp instanceof JScrollPane) {
                chiPhiScrollPane = (JScrollPane) comp;
                break;
            }
        }
        if (chiPhiScrollPane == null) return;
        
        JTable chiPhiTable = (JTable) chiPhiScrollPane.getViewport().getView();
        DefaultTableModel chiPhiModel = (DefaultTableModel) chiPhiTable.getModel();
        
        chiPhiModel.setRowCount(0);
        for (ThongKeDTO item : chiPhiData) {
            Object[] row = {
                item.getNgay(),
                String.format("%,d VNĐ", item.getDoanhThu())
            };
            chiPhiModel.addRow(row);
        }
        
        // Load nhà cung cấp
        JPanel nccPanel = (JPanel) contentPanel.getComponent(1);
        JScrollPane nccScrollPane = null;
        for (Component comp : nccPanel.getComponents()) {
            if (comp instanceof JScrollPane) {
                nccScrollPane = (JScrollPane) comp;
                break;
            }
        }
        if (nccScrollPane == null) return;
        
        JTable nccTable = (JTable) nccScrollPane.getViewport().getView();
        DefaultTableModel nccModel = (DefaultTableModel) nccTable.getModel();
        
        nccModel.setRowCount(0);
        int stt = 1;
        for (ThongKeDTO item : nccData) {
            Object[] row = {
                stt++,
                item.getTenNhaCungCap(),
                item.getSoDonHang(),
                String.format("%,d VNĐ", item.getDoanhThu())
            };
            nccModel.addRow(row);
        }
    }
    
    private void loadKhoHangData() {
        // Load nguyên liệu sắp hết
        List<ThongKeDTO> nguyenLieuData = thongKeDAO.thongKeNguyenLieuSapHet(50);
        
        JPanel khoHangPanel = (JPanel) tabbedPane.getComponentAt(6);
        JScrollPane scrollPane = null;
        for (Component comp : khoHangPanel.getComponents()) {
            if (comp instanceof JScrollPane) {
                scrollPane = (JScrollPane) comp;
                break;
            }
        }
        if (scrollPane == null) return;
        
        JTable table = (JTable) scrollPane.getViewport().getView();
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        
        model.setRowCount(0);
        int stt = 1;
        for (ThongKeDTO item : nguyenLieuData) {
            Object[] row = {
                stt++,
                item.getTenMon(),
                item.getSoLuongBan(),
                item.getTenLoaiMon()
            };
            model.addRow(row);
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
