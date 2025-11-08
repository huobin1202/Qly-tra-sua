package view;

import javax.swing.*;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import dao.ThongKeDAO;
import dto.ThongKeDTO;
import utils.DateChooserComponent;

public class BaoCaoView extends JPanel {
    private final ThongKeDAO thongKeDAO;
    private JComboBox<String> yearCombo;
    private DateChooserComponent fromDatePicker;
    private DateChooserComponent toDatePicker;
    private JTextArea reportArea;
    
    public BaoCaoView() {
        thongKeDAO = new ThongKeDAO();
        initializeComponents();
        setupLayout();
        generateDefaultReport();
    }
    
    private void initializeComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 248, 255));
        
        // Initialize components
        yearCombo = new JComboBox<>();
        fromDatePicker = new DateChooserComponent();
        toDatePicker = new DateChooserComponent();
        reportArea = new JTextArea();
        
        // Populate year combo
        populateYearCombo();
        setDefaultDates();
        
        // Setup report area
        reportArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        reportArea.setEditable(false);
        reportArea.setBackground(Color.WHITE);
    }
    
    private void setupLayout() {
        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(70, 130, 180));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("📋 BÁO CÁO CHI TIẾT");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Control panel
        JPanel controlPanel = createControlPanel();
        headerPanel.add(controlPanel, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Report area
        JScrollPane scrollPane = new JScrollPane(reportArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(scrollPane, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createControlPanel() {
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        controlPanel.setOpaque(false);
        
        // Từ ngày
        controlPanel.add(new JLabel("Từ:"));
        fromDatePicker.setFont(new Font("Arial", Font.PLAIN, 12));
        controlPanel.add(fromDatePicker);
        
        // Đến ngày
        controlPanel.add(new JLabel("Đến:"));
        toDatePicker.setFont(new Font("Arial", Font.PLAIN, 12));
        controlPanel.add(toDatePicker);
        
        // Năm
        controlPanel.add(new JLabel("Năm:"));
        yearCombo.setFont(new Font("Arial", Font.PLAIN, 12));
        controlPanel.add(yearCombo);
        
        // Nút tạo báo cáo
        JButton generateButton = new JButton("📊 Tạo báo cáo");
        generateButton.setFont(new Font("Arial", Font.BOLD, 12));
        generateButton.setBackground(new Color(34, 139, 34));
        generateButton.setForeground(Color.BLACK);
        generateButton.setFocusPainted(false);
        generateButton.addActionListener(e -> generateReport());
        controlPanel.add(generateButton);
        
        return controlPanel;
    }
    
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(new Color(240, 248, 255));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JButton exportButton = new JButton("💾 Xuất file");
        exportButton.setFont(new Font("Arial", Font.BOLD, 12));
        exportButton.setBackground(new Color(255, 140, 0));
        exportButton.setForeground(Color.BLACK);
        exportButton.setFocusPainted(false);
        exportButton.addActionListener(e -> exportReport());
        
        JButton printButton = new JButton("🖨️ In báo cáo");
        printButton.setFont(new Font("Arial", Font.BOLD, 12));
        printButton.setBackground(new Color(70, 130, 180));
        printButton.setForeground(Color.BLACK);
        printButton.setFocusPainted(false);
        printButton.addActionListener(e -> printReport());
        
        JButton refreshButton = new JButton("🔄 Làm mới");
        refreshButton.setFont(new Font("Arial", Font.BOLD, 12));
        refreshButton.setBackground(new Color(34, 139, 34));
        refreshButton.setForeground(Color.BLACK);
        refreshButton.setFocusPainted(false);
        refreshButton.addActionListener(e -> generateReport());
        
        buttonPanel.add(exportButton);
        buttonPanel.add(printButton);
        buttonPanel.add(refreshButton);
        
        return buttonPanel;
    }
    
    private void populateYearCombo() {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        int currentYear = cal.get(java.util.Calendar.YEAR);
        
        for (int i = currentYear - 5; i <= currentYear + 1; i++) {
            yearCombo.addItem(String.valueOf(i));
        }
        yearCombo.setSelectedItem(String.valueOf(currentYear));
    }
    
    private void setDefaultDates() {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        
        // Đến ngày = hôm nay
        toDatePicker.setDate(cal.getTime());
        
        // Từ ngày = đầu tháng
        cal.set(java.util.Calendar.DAY_OF_MONTH, 1);
        fromDatePicker.setDate(cal.getTime());
    }
    
    private void generateDefaultReport() {
        generateReport();
    }
    
    private void generateReport() {
        String fromDate = fromDatePicker.getSelectedDateString();
        String toDate = toDatePicker.getSelectedDateString();
        String year = (String) yearCombo.getSelectedItem();
        
        // Validate dates
        if (fromDate.isEmpty() || toDate.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ ngày bắt đầu và kết thúc!", 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        StringBuilder report = new StringBuilder();
        
        // Header
        report.append("================================================================================\n");
        report.append("                    BÁO CÁO THỐNG KÊ CỬA HÀNG TRÀ SỮA\n");
        report.append("================================================================================\n");
        report.append("Ngày tạo báo cáo: ").append(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date())).append("\n");
        report.append("Khoảng thời gian: ").append(fromDate).append(" đến ").append(toDate).append("\n");
        report.append("Năm thống kê: ").append(year).append("\n");
        report.append("================================================================================\n\n");
        
        // 1. Món bán chạy nhất
        report.append("1. TOP 10 MÓN BÁN CHẠY NHẤT\n");
        report.append("--------------------------------------------------\n");
        List<ThongKeDTO> monBanChay = thongKeDAO.thongKeMonBanChay(fromDate, toDate);
        report.append(String.format("%-5s %-30s %-15s %-20s\n", "STT", "Tên món", "Số lượng bán", "Tổng tiền"));
        report.append("----------------------------------------------------------------------\n");
        
        int stt = 1;
        for (ThongKeDTO item : monBanChay) {
            report.append(String.format("%-5d %-30s %-15d %,d VNĐ\n", 
                stt++, item.getTenMon(), item.getSoLuongBan(), item.getTongTien()));
        }
        report.append("\n");
        
        // 2. Doanh thu theo loại món
        report.append("2. DOANH THU THEO LOẠI MÓN\n");
        report.append("--------------------------------------------------\n");
        List<ThongKeDTO> doanhThuLoaiMon = thongKeDAO.thongKeDoanhThuTheoLoaiMon(fromDate, toDate);
        report.append(String.format("%-5s %-25s %-15s %-20s\n", "STT", "Loại món", "Số lượng bán", "Tổng tiền"));
        report.append("-----------------------------------------------------------------\n");
        
        stt = 1;
        for (ThongKeDTO item : doanhThuLoaiMon) {
            report.append(String.format("%-5d %-25s %-15d %,d VNĐ\n", 
                stt++, item.getTenLoaiMon(), item.getSoLuongBan(), item.getTongTien()));
        }
        report.append("\n");
        
        // 3. Thống kê nhân viên
        report.append("3. THỐNG KÊ NHÂN VIÊN BÁN HÀNG\n");
        report.append("--------------------------------------------------\n");
        List<ThongKeDTO> nhanVienData = thongKeDAO.thongKeNhanVienBanHang(fromDate, toDate);
        report.append(String.format("%-5s %-25s %-15s %-20s\n", "STT", "Tên nhân viên", "Số đơn hàng", "Doanh thu"));
        report.append("-----------------------------------------------------------------\n");
        
        stt = 1;
        for (ThongKeDTO item : nhanVienData) {
            report.append(String.format("%-5d %-25s %-15d %,d VNĐ\n", 
                stt++, item.getTenNhanVien(), item.getSoDonHang(), item.getDoanhThu()));
        }
        report.append("\n");
        
        // 4. Doanh thu theo ngày
        report.append("4. DOANH THU THEO NGÀY\n");
        report.append("--------------------------------------------------\n");
        List<ThongKeDTO> doanhThuNgay = thongKeDAO.thongKeDoanhThuTheoNgay(fromDate, toDate);
        report.append(String.format("%-15s %-20s\n", "Ngày", "Doanh thu"));
        report.append("-----------------------------------\n");
        
        for (ThongKeDTO item : doanhThuNgay) {
            report.append(String.format("%-15s %,d VNĐ\n", item.getNgay(), item.getDoanhThu()));
        }
        report.append("\n");
        
        // 5. Doanh thu theo tháng
        report.append("5. DOANH THU THEO THÁNG (").append(year).append(")\n");
        report.append("--------------------------------------------------\n");
        List<ThongKeDTO> doanhThuThang = thongKeDAO.thongKeDoanhThuTheoThang(year);
        report.append(String.format("%-15s %-20s\n", "Tháng", "Doanh thu"));
        report.append("-----------------------------------\n");
        
        for (ThongKeDTO item : doanhThuThang) {
            report.append(String.format("%-15s %,d VNĐ\n", item.getThang(), item.getDoanhThu()));
        }
        report.append("\n");
        
        // 6. Đơn hàng theo trạng thái
        report.append("6. ĐƠN HÀNG THEO TRẠNG THÁI\n");
        report.append("--------------------------------------------------\n");
        List<ThongKeDTO> donHangTrangThai = thongKeDAO.thongKeDonHangTheoTrangThai();
        report.append(String.format("%-20s %-15s\n", "Trạng thái", "Số đơn hàng"));
        report.append("-----------------------------------\n");
        
        for (ThongKeDTO item : donHangTrangThai) {
            String trangThai = convertTrangThaiToUI(item.getTrangThai());
            report.append(String.format("%-20s %-15d\n", trangThai, item.getSoDonHang()));
        }
        report.append("\n");
        
        // 7. Khách hàng mới theo tháng
        report.append("7. KHÁCH HÀNG MỚI THEO THÁNG (").append(year).append(")\n");
        report.append("--------------------------------------------------\n");
        List<ThongKeDTO> khachHangMoi = thongKeDAO.thongKeKhachHangMoiTheoThang(year);
        report.append(String.format("%-15s %-20s\n", "Tháng", "Số khách hàng"));
        report.append("-----------------------------------\n");
        
        for (ThongKeDTO item : khachHangMoi) {
            report.append(String.format("%-15s %-20d\n", item.getThang(), item.getSoKhachHang()));
        }
        report.append("\n");
        
        // Footer
        report.append("================================================================================\n");
        report.append("                    KẾT THÚC BÁO CÁO\n");
        report.append("================================================================================\n");
        
        reportArea.setText(report.toString());
    }
    
    private void exportReport() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Xuất báo cáo");
        fileChooser.setSelectedFile(new java.io.File("BaoCao_" + 
            new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".txt"));
        
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try (FileWriter writer = new FileWriter(fileChooser.getSelectedFile())) {
                writer.write(reportArea.getText());
                JOptionPane.showMessageDialog(this, "Xuất báo cáo thành công!", 
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xuất file: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void printReport() {
        try {
            reportArea.print();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi in báo cáo: " + e.getMessage(), 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
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
