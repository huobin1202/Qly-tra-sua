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
    private DateChooserComponent fromDatePicker;
    private DateChooserComponent toDatePicker;
    private JTextArea reportArea;
    
    public BaoCaoView() {
        thongKeDAO = new ThongKeDAO();
        initializeComponents();
        setupLayout();
        // Không tự động tạo báo cáo - để người dùng chọn ngày và tạo
    }
    
    private void initializeComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 248, 255));
        
        // Initialize components
        fromDatePicker = new DateChooserComponent();
        toDatePicker = new DateChooserComponent();
        reportArea = new JTextArea();
        
        // Không set default dates - để người dùng tự chọn
        
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
        
        JLabel titleLabel = new JLabel("BÁO CÁO CHI TIẾT");
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
        
        // Nút tạo báo cáo
        JButton generateButton = new JButton("Tạo báo cáo");
        generateButton.setFont(new Font("Arial", Font.BOLD, 12));
        generateButton.setBackground(new Color(34, 139, 34));
        generateButton.setForeground(Color.BLACK);
        generateButton.setFocusPainted(false);
        generateButton.addActionListener(e -> generateReport());
        controlPanel.add(generateButton);
        
        // Nút làm mới
        JButton refreshButton = new JButton("🔄 Làm mới");
        refreshButton.setFont(new Font("Arial", Font.BOLD, 12));
        refreshButton.setBackground(new Color(34, 139, 34));
        refreshButton.setForeground(Color.BLACK);
        refreshButton.setFocusPainted(false);
        refreshButton.addActionListener(e -> {
            setDefaultDates();
            generateReport();
        });
        controlPanel.add(refreshButton);
        
        return controlPanel;
    }
    
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(new Color(240, 248, 255));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
 
        JButton printButton = new JButton("In báo cáo");
        printButton.setFont(new Font("Arial", Font.BOLD, 12));
        printButton.setBackground(new Color(70, 130, 180));
        printButton.setForeground(Color.BLACK);
        printButton.setFocusPainted(false);
        printButton.addActionListener(e -> printReport());
        
        buttonPanel.add(printButton);
        
        return buttonPanel;
    }
    
    private void setDefaultDates() {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        
        // Đến ngày = hôm nay
        toDatePicker.setDate(cal.getTime());
        
        // Từ ngày = đầu tháng
        cal.set(java.util.Calendar.DAY_OF_MONTH, 1);
        fromDatePicker.setDate(cal.getTime());
    }
    
    private void generateReport() {
        String fromDate = fromDatePicker.getSelectedDateString();
        String toDate = toDatePicker.getSelectedDateString();
        
        // Validate dates
        if (fromDate.isEmpty() || toDate.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ ngày bắt đầu và kết thúc!", 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Lấy năm từ ngày kết thúc
        String year = "";
        try {
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.setTime(toDatePicker.getSelectedDate());
            year = String.valueOf(cal.get(java.util.Calendar.YEAR));
        } catch (Exception e) {
            // Nếu không lấy được, dùng năm hiện tại
            year = String.valueOf(java.util.Calendar.getInstance().get(java.util.Calendar.YEAR));
        }
        
        StringBuilder report = new StringBuilder();
        
        // Header
        report.append("================================================================================\n");
        report.append("                    BÁO CÁO THỐNG KÊ CỬA HÀNG TRÀ SỮA\n");
        report.append("================================================================================\n");
        report.append("Ngày tạo báo cáo: ").append(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date())).append("\n");
        report.append("Khoảng thời gian: ").append(fromDate).append(" đến ").append(toDate).append("\n");
        report.append("================================================================================\n\n");
        
        // 1. Món bán chạy nhất
        report.append("1. TOP 10 MÓN BÁN CHẠY NHẤT\n");
        report.append("-------------------------------------------------------------------------------\n");
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
        report.append("-------------------------------------------------------------------------------\n");
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
        report.append("-------------------------------------------------------------------------------\n");
        List<ThongKeDTO> nhanVienData = thongKeDAO.thongKeNhanVienBanHang(fromDate, toDate);
        report.append(String.format("%-5s %-25s %-15s %-20s\n", "STT", "Tên nhân viên", "Số đơn hàng", "Doanh thu"));
        report.append("-------------------------------------------------------------------------------\n");
        
        stt = 1;
        for (ThongKeDTO item : nhanVienData) {
            report.append(String.format("%-5d %-25s %-15d %,d VNĐ\n", 
                stt++, item.getTenNhanVien(), item.getSoDonHang(), item.getDoanhThu()));
        }
        report.append("\n");
        
        // 4. Doanh thu theo ngày
        report.append("4. DOANH THU THEO NGÀY\n");
        report.append("-------------------------------------------------------------------------------\n");
        List<ThongKeDTO> doanhThuNgay = thongKeDAO.thongKeDoanhThuTheoNgay(fromDate, toDate);
        report.append(String.format("%-15s %-20s\n", "Ngày", "Doanh thu"));
        report.append("-------------------------------------------------------------------------------\n");
        
        for (ThongKeDTO item : doanhThuNgay) {
            report.append(String.format("%-15s %,d VNĐ\n", item.getNgay(), item.getDoanhThu()));
        }
        report.append("\n");
        
        // 5. Doanh thu theo tháng
        report.append("5. DOANH THU THEO THÁNG (").append(year).append(")\n");
        report.append("-------------------------------------------------------------------------------\n");
        List<ThongKeDTO> doanhThuThang = thongKeDAO.thongKeDoanhThuTheoThang(year);
        report.append(String.format("%-15s %-20s\n", "Tháng", "Doanh thu"));
        report.append("-------------------------------------------------------------------------------\n");
        
        for (ThongKeDTO item : doanhThuThang) {
            report.append(String.format("%-15s %,d VNĐ\n", item.getThang(), item.getDoanhThu()));
        }
        report.append("\n");
        
        // 6. Đơn hàng theo trạng thái
        report.append("6. ĐƠN HÀNG THEO TRẠNG THÁI\n");
        report.append("-------------------------------------------------------------------------------\n");
        List<ThongKeDTO> donHangTrangThai = thongKeDAO.thongKeDonHangTheoTrangThai();
        report.append(String.format("%-20s %-15s\n", "Trạng thái", "Số đơn hàng"));
        report.append("-------------------------------------------------------------------------------\n");
        
        for (ThongKeDTO item : donHangTrangThai) {
            String trangThai = convertTrangThaiToUI(item.getTrangThai());
            report.append(String.format("%-20s %-15d\n", trangThai, item.getSoDonHang()));
        }
        report.append("\n");
        
        // 7. Khách hàng mới theo tháng
        report.append("7. KHÁCH HÀNG MỚI THEO THÁNG (").append(year).append(")\n");
        report.append("-------------------------------------------------------------------------------\n");
        List<ThongKeDTO> khachHangMoi = thongKeDAO.thongKeKhachHangMoiTheoThang(year);
        report.append(String.format("%-15s %-20s\n", "Tháng", "Số khách hàng"));
        report.append("-------------------------------------------------------------------------------\n");
        
        for (ThongKeDTO item : khachHangMoi) {
            report.append(String.format("%-15s %-20d\n", item.getThang(), item.getSoKhachHang()));
        }
        report.append("\n");
        
        // 8. Top khách hàng VIP
        report.append("8. TOP 10 KHÁCH HÀNG VIP\n");
        report.append("-------------------------------------------------------------------------------\n");
        List<ThongKeDTO> khachHangVIP = thongKeDAO.thongKeKhachHangVIP(fromDate, toDate);
        report.append(String.format("%-5s %-30s %-15s %-20s\n", "STT", "Tên khách hàng", "Số đơn hàng", "Tổng tiền"));
        report.append("-------------------------------------------------------------------------------\n");
        
        stt = 1;
        for (ThongKeDTO item : khachHangVIP) {
            report.append(String.format("%-5d %-30s %-15d %,d VNĐ\n", 
                stt++, item.getTenKhachHang(), item.getSoDonHang(), item.getTongTien()));
        }
        report.append("\n");
        
        // 9. Chi phí nhập hàng
        report.append("9. CHI PHÍ NHẬP HÀNG\n");
        report.append("-------------------------------------------------------------------------------\n");
        List<ThongKeDTO> chiPhiNhapHang = thongKeDAO.thongKeChiPhiNhapHang(fromDate, toDate);
        report.append(String.format("%-15s %-20s\n", "Ngày", "Chi phí"));
        report.append("-------------------------------------------------------------------------------\n");
        
        long tongChiPhi = 0;
        for (ThongKeDTO item : chiPhiNhapHang) {
            report.append(String.format("%-15s %,d VNĐ\n", item.getNgay(), item.getDoanhThu()));
            tongChiPhi += item.getDoanhThu();
        }
        report.append("-------------------------------------------------------------------------------\n");
        report.append(String.format("%-15s %,d VNĐ\n", "Tổng chi phí:", tongChiPhi));
        report.append("\n");
        
        // 10. Nhà cung cấp được sử dụng nhiều nhất
        report.append("10. TOP 10 NHÀ CUNG CẤP ĐƯỢC SỬ DỤNG NHIỀU NHẤT\n");
        report.append("-------------------------------------------------------------------------------\n");
        List<ThongKeDTO> nhaCungCap = thongKeDAO.thongKeNhaCungCap(fromDate, toDate);
        report.append(String.format("%-5s %-30s %-15s %-20s\n", "STT", "Tên nhà cung cấp", "Số phiếu nhập", "Tổng chi phí"));
        report.append("-------------------------------------------------------------------------------\n");
        
        stt = 1;
        for (ThongKeDTO item : nhaCungCap) {
            report.append(String.format("%-5d %-30s %-15d %,d VNĐ\n", 
                stt++, item.getTenNhaCungCap(), item.getSoDonHang(), item.getDoanhThu()));
        }
        report.append("\n");
        
        // 11. Nguyên liệu sắp hết (ngưỡng <= 50)
        report.append("11. NGUYÊN LIỆU SẮP HẾT (≤ 50)\n");
        report.append("-------------------------------------------------------------------------------\n");
        List<ThongKeDTO> nguyenLieuSapHet = thongKeDAO.thongKeNguyenLieuSapHet(50);
        report.append(String.format("%-5s %-30s %-15s %-15s\n", "STT", "Tên nguyên liệu", "Số lượng", "Đơn vị"));
        report.append("-------------------------------------------------------------------------------\n");
        
        stt = 1;
        for (ThongKeDTO item : nguyenLieuSapHet) {
            report.append(String.format("%-5d %-30s %-15d %-15s\n", 
                stt++, item.getTenMon(), item.getSoLuongBan(), item.getTenLoaiMon()));
        }
        report.append("\n");
        
        // 12. Lợi nhuận
        report.append("12. LỢI NHUẬN\n");
        report.append("-------------------------------------------------------------------------------\n");
        ThongKeDTO loiNhuan = thongKeDAO.thongKeLoiNhuan(fromDate, toDate);
        long tongChiPhiNhapHang = loiNhuan.getDoanhThu() - loiNhuan.getTongTien(); // Chi phí = doanh thu - lợi nhuận
        report.append(String.format("%-30s %,d VNĐ\n", "Tổng doanh thu:", loiNhuan.getDoanhThu()));
        report.append(String.format("%-30s %,d VNĐ\n", "Tổng chi phí nhập hàng:", tongChiPhiNhapHang));
        report.append(String.format("%-30s %,d VNĐ\n", "Lợi nhuận:", loiNhuan.getTongTien()));
        report.append("\n");
        
        // 13. Giá trị đơn hàng trung bình
        report.append("13. GIÁ TRỊ ĐƠN HÀNG TRUNG BÌNH\n");
        report.append("-------------------------------------------------------------------------------\n");
        ThongKeDTO giaTriTrungBinh = thongKeDAO.thongKeGiaTriDonHangTrungBinh(fromDate, toDate);
        report.append(String.format("%-30s %,d VNĐ\n", "Giá trị trung bình:", giaTriTrungBinh.getTongTien()));
        report.append(String.format("%-30s %d đơn\n", "Tổng số đơn hàng:", giaTriTrungBinh.getSoDonHang()));
        report.append("\n");
        
        // 14. Tổng giá trị tồn kho
        report.append("14. TỔNG GIÁ TRỊ TỒN KHO\n");
        report.append("-------------------------------------------------------------------------------\n");
        long tongGiaTriTonKho = thongKeDAO.layTongGiaTriTonKho();
        report.append(String.format("%-30s %,d VNĐ\n", "Tổng giá trị tồn kho:", tongGiaTriTonKho));
        report.append("\n");
        
        // Footer
        report.append("================================================================================\n");
        report.append("                    KẾT THÚC BÁO CÁO\n");
        report.append("================================================================================\n");
        
        reportArea.setText(report.toString());
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
