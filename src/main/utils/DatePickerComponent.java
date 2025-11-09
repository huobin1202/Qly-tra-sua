package utils;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Component để chọn ngày/tháng/năm bằng dropdown
 * Thay thế cho việc nhập text trực tiếp
 */
public class DatePickerComponent extends JPanel {
    private JComboBox<String> dayCombo;
    private JComboBox<String> monthCombo;
    private JComboBox<String> yearCombo;
    
    public DatePickerComponent() {
        initializeComponents();
        setupLayout();
        setCurrentDate();
    }
    
    public DatePickerComponent(Date initialDate) {
        initializeComponents();
        setupLayout();
        setDate(initialDate);
    }
    
    private void initializeComponents() {
        // Khởi tạo dropdown ngày (1-31)
        dayCombo = new JComboBox<>();
        for (int i = 1; i <= 31; i++) {
            dayCombo.addItem(String.format("%02d", i));
        }
        
        // Khởi tạo dropdown tháng (1-12)
        monthCombo = new JComboBox<>();
        String[] monthNames = {
            "Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4", 
            "Tháng 5", "Tháng 6", "Tháng 7", "Tháng 8", 
            "Tháng 9", "Tháng 10", "Tháng 11", "Tháng 12"
        };
        for (String month : monthNames) {
            monthCombo.addItem(month);
        }
        
        // Khởi tạo dropdown năm (từ năm hiện tại - 100 đến năm hiện tại + 10)
        yearCombo = new JComboBox<>();
        Calendar cal = Calendar.getInstance();
        int currentYear = cal.get(Calendar.YEAR);
        for (int i = currentYear - 100; i <= currentYear + 10; i++) {
            yearCombo.addItem(String.valueOf(i));
        }
        
        // Thiết lập font và kích thước
        Font comboFont = new Font("Arial", Font.PLAIN, 12);
        dayCombo.setFont(comboFont);
        monthCombo.setFont(comboFont);
        yearCombo.setFont(comboFont);
        
        // Thiết lập kích thước dropdown
        Dimension comboSize = new Dimension(80, 25);
        dayCombo.setPreferredSize(comboSize);
        monthCombo.setPreferredSize(new Dimension(100, 25));
        yearCombo.setPreferredSize(new Dimension(80, 25));
    }
    
    private void setupLayout() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
        setOpaque(false);
        
        add(new JLabel("Ngày:"));
        add(dayCombo);
        add(new JLabel("Tháng:"));
        add(monthCombo);
        add(new JLabel("Năm:"));
        add(yearCombo);
    }
    
    /**
     * Thiết lập ngày hiện tại
     */
    public void setCurrentDate() {
        Calendar cal = Calendar.getInstance();
        setDate(cal.getTime());
    }
    
    /**
     * Thiết lập ngày cụ thể
     */
    public void setDate(Date date) {
        if (date == null) {
            setCurrentDate();
            return;
        }
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH); // 0-based
        int year = cal.get(Calendar.YEAR);
        
        dayCombo.setSelectedItem(String.format("%02d", day));
        monthCombo.setSelectedIndex(month);
        yearCombo.setSelectedItem(String.valueOf(year));
    }
    
    /**
     * Lấy ngày được chọn dưới dạng Date
     */
    public Date getSelectedDate() {
        try {
            int day = Integer.parseInt((String) dayCombo.getSelectedItem());
            int month = monthCombo.getSelectedIndex(); // 0-based
            int year = Integer.parseInt((String) yearCombo.getSelectedItem());
            
            Calendar cal = Calendar.getInstance();
            cal.set(year, month, day, 0, 0, 0);
            cal.set(Calendar.MILLISECOND, 0);
            
            return cal.getTime();
        } catch (Exception e) {
            return new Date(); // Trả về ngày hiện tại nếu có lỗi
        }
    }
    
    /**
     * Lấy ngày được chọn dưới dạng String (yyyy-MM-dd)
     */
    public String getSelectedDateString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(getSelectedDate());
    }
    
    /**
     * Lấy ngày được chọn dưới dạng String với format tùy chỉnh
     */
    public String getSelectedDateString(String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(getSelectedDate());
    }
    
    /**
     * Kiểm tra xem ngày được chọn có hợp lệ không
     */
    public boolean isValidDate() {
        try {
            int day = Integer.parseInt((String) dayCombo.getSelectedItem());
            int month = monthCombo.getSelectedIndex() + 1; // Convert to 1-based
            int year = Integer.parseInt((String) yearCombo.getSelectedItem());
            
            // Kiểm tra ngày có hợp lệ không
            Calendar cal = Calendar.getInstance();
            cal.setLenient(false);
            cal.set(year, month - 1, day); // month is 0-based in Calendar
            
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Thiết lập trạng thái enabled/disabled cho tất cả dropdown
     */
    public void setEnabled(boolean enabled) {
        dayCombo.setEnabled(enabled);
        monthCombo.setEnabled(enabled);
        yearCombo.setEnabled(enabled);
    }
    
    /**
     * Xóa lựa chọn (thiết lập về ngày hiện tại)
     */

}

