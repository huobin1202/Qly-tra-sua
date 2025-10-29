package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Component date chooser nâng cao với calendar popup
 * Tạo một popup calendar đẹp để chọn ngày
 */
public class AdvancedDateChooserComponent extends JPanel {
    private JTextField dateField;
    private JButton calendarButton;
    private JButton todayButton;
    private JDialog calendarDialog;
    private JPanel calendarPanel;
    private SimpleDateFormat dateFormat;
    private Date selectedDate;
    private Calendar calendar;
    
    public AdvancedDateChooserComponent() {
        this(new SimpleDateFormat("yyyy-MM-dd"));
    }
    
    public AdvancedDateChooserComponent(SimpleDateFormat format) {
        this.dateFormat = format;
        this.calendar = Calendar.getInstance();
        this.selectedDate = new Date();
        initializeComponents();
        setupLayout();
        createCalendarDialog();
    }
    
    public AdvancedDateChooserComponent(Date initialDate) {
        this();
        setDate(initialDate);
    }
    
    private void initializeComponents() {
        // Tạo text field hiển thị ngày
        dateField = new JTextField(12);
        dateField.setEditable(false);
        dateField.setFont(new Font("Arial", Font.PLAIN, 12));
        dateField.setHorizontalAlignment(JTextField.CENTER);
        dateField.setBackground(Color.WHITE);
        dateField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            BorderFactory.createEmptyBorder(2, 5, 2, 5)
        ));
        
        // Tạo nút mở calendar
        calendarButton = new JButton("📅");
        calendarButton.setFont(new Font("Arial", Font.PLAIN, 14));
        calendarButton.setPreferredSize(new Dimension(30, 25));
        calendarButton.setBackground(new Color(70, 130, 180));
        calendarButton.setForeground(Color.WHITE);
        calendarButton.setFocusPainted(false);
        calendarButton.addActionListener(e -> showCalendar());
        
        // Tạo nút "Hôm nay"
        todayButton = new JButton("Hôm nay");
        todayButton.setFont(new Font("Arial", Font.PLAIN, 10));
        todayButton.setPreferredSize(new Dimension(70, 25));
        todayButton.setBackground(new Color(34, 139, 34));
        todayButton.setForeground(Color.WHITE);
        todayButton.setFocusPainted(false);
        todayButton.addActionListener(e -> setCurrentDate());
        
 
        // Thiết lập ngày hiện tại
        setCurrentDate();
    }
    
    private void setupLayout() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 2, 0));
        setOpaque(false);
        
        add(dateField);
        add(calendarButton);
        add(todayButton);
    }
    
    private void createCalendarDialog() {
        calendarDialog = new JDialog((Frame) null, "Chọn ngày", true);
        calendarDialog.setSize(300, 250);
        calendarDialog.setResizable(false);
        calendarDialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
        
        calendarPanel = new JPanel(new BorderLayout());
        calendarPanel.setBackground(Color.WHITE);
        calendarPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Tạo calendar
        createCalendarGrid();
        
        calendarDialog.add(calendarPanel);
    }
    
    private void createCalendarGrid() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(70, 130, 180));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        // Nút tháng trước
        JButton prevMonthButton = new JButton("◀");
        prevMonthButton.setFont(new Font("Arial", Font.BOLD, 14));
        prevMonthButton.setBackground(new Color(70, 130, 180));
        prevMonthButton.setForeground(Color.WHITE);
        prevMonthButton.setBorderPainted(false);
        prevMonthButton.addActionListener(e -> changeMonth(-1));
        
        // Label hiển thị tháng/năm
        JLabel monthYearLabel = new JLabel("", JLabel.CENTER);
        monthYearLabel.setFont(new Font("Arial", Font.BOLD, 14));
        monthYearLabel.setForeground(Color.WHITE);
        
        // Nút tháng sau
        JButton nextMonthButton = new JButton("▶");
        nextMonthButton.setFont(new Font("Arial", Font.BOLD, 14));
        nextMonthButton.setBackground(new Color(70, 130, 180));
        nextMonthButton.setForeground(Color.WHITE);
        nextMonthButton.setBorderPainted(false);
        nextMonthButton.addActionListener(e -> changeMonth(1));
        
        headerPanel.add(prevMonthButton, BorderLayout.WEST);
        headerPanel.add(monthYearLabel, BorderLayout.CENTER);
        headerPanel.add(nextMonthButton, BorderLayout.EAST);
        
        // Tạo grid calendar
        JPanel calendarGrid = new JPanel(new GridLayout(0, 7, 2, 2));
        calendarGrid.setBackground(Color.WHITE);
        
        // Thêm header cho các ngày trong tuần
        String[] dayHeaders = {"CN", "T2", "T3", "T4", "T5", "T6", "T7"};
        for (String day : dayHeaders) {
            JLabel dayLabel = new JLabel(day, JLabel.CENTER);
            dayLabel.setFont(new Font("Arial", Font.BOLD, 12));
            dayLabel.setBackground(new Color(240, 240, 240));
            dayLabel.setOpaque(true);
            calendarGrid.add(dayLabel);
        }
        
        // Lưu reference để cập nhật sau
        this.monthYearLabel = monthYearLabel;
        this.calendarGrid = calendarGrid;
        
        // Cập nhật calendar
        updateCalendar();
        
        // Panel nút
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton okButton = new JButton("OK");
        okButton.setBackground(new Color(34, 139, 34));
        okButton.setForeground(Color.WHITE);
        okButton.addActionListener(e -> calendarDialog.setVisible(false));
        
        JButton cancelButton = new JButton("Hủy");
        cancelButton.setBackground(new Color(220, 20, 60));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.addActionListener(e -> calendarDialog.setVisible(false));
        
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        
        calendarPanel.add(headerPanel, BorderLayout.NORTH);
        calendarPanel.add(calendarGrid, BorderLayout.CENTER);
        calendarPanel.add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JLabel monthYearLabel;
    private JPanel calendarGrid;
    
    private void updateCalendar() {
        // Cập nhật header
        String[] monthNames = {"Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4", "Tháng 5", "Tháng 6",
                              "Tháng 7", "Tháng 8", "Tháng 9", "Tháng 10", "Tháng 11", "Tháng 12"};
        monthYearLabel.setText(monthNames[calendar.get(Calendar.MONTH)] + " " + calendar.get(Calendar.YEAR));
        
        // Xóa các button cũ (trừ header)
        calendarGrid.removeAll();
        
        // Thêm lại header
        String[] dayHeaders = {"CN", "T2", "T3", "T4", "T5", "T6", "T7"};
        for (String day : dayHeaders) {
            JLabel dayLabel = new JLabel(day, JLabel.CENTER);
            dayLabel.setFont(new Font("Arial", Font.BOLD, 12));
            dayLabel.setBackground(new Color(240, 240, 240));
            dayLabel.setOpaque(true);
            calendarGrid.add(dayLabel);
        }
        
        // Lấy ngày đầu tháng và số ngày trong tháng
        Calendar tempCal = (Calendar) calendar.clone();
        tempCal.set(Calendar.DAY_OF_MONTH, 1);
        int firstDayOfWeek = tempCal.get(Calendar.DAY_OF_WEEK);
        int daysInMonth = tempCal.getActualMaximum(Calendar.DAY_OF_MONTH);
        
        // Thêm các ô trống cho ngày đầu tháng
        for (int i = 1; i < firstDayOfWeek; i++) {
            calendarGrid.add(new JLabel(""));
        }
        
        // Thêm các ngày trong tháng
        for (int day = 1; day <= daysInMonth; day++) {
            JButton dayButton = new JButton(String.valueOf(day));
            dayButton.setFont(new Font("Arial", Font.PLAIN, 12));
            dayButton.setPreferredSize(new Dimension(35, 25));
            
            // Highlight ngày hiện tại
            if (day == calendar.get(Calendar.DAY_OF_MONTH)) {
                dayButton.setBackground(new Color(70, 130, 180));
                dayButton.setForeground(Color.WHITE);
            } else {
                dayButton.setBackground(Color.WHITE);
                dayButton.setForeground(Color.BLACK);
            }
            
            final int selectedDay = day;
            dayButton.addActionListener(e -> selectDate(selectedDay));
            
            calendarGrid.add(dayButton);
        }
        
        calendarGrid.revalidate();
        calendarGrid.repaint();
    }
    
    private void changeMonth(int direction) {
        calendar.add(Calendar.MONTH, direction);
        updateCalendar();
    }
    
    private void selectDate(int day) {
        calendar.set(Calendar.DAY_OF_MONTH, day);
        selectedDate = calendar.getTime();
        dateField.setText(dateFormat.format(selectedDate));
        calendarDialog.setVisible(false);
    }
    
    private void showCalendar() {
        calendarDialog.setLocationRelativeTo(this);
        calendarDialog.setVisible(true);
    }
    
    /**
     * Thiết lập ngày hiện tại
     */
    public void setCurrentDate() {
        selectedDate = new Date();
        calendar.setTime(selectedDate);
        dateField.setText(dateFormat.format(selectedDate));
    }
    
    /**
     * Thiết lập ngày cụ thể
     */
    public void setDate(Date date) {
        if (date == null) {
            setCurrentDate();
            return;
        }
        selectedDate = date;
        calendar.setTime(date);
        dateField.setText(dateFormat.format(date));
    }
    
    /**
     * Lấy ngày được chọn dưới dạng Date
     */
    public Date getSelectedDate() {
        return selectedDate;
    }
    
    /**
     * Lấy ngày được chọn dưới dạng String
     */
    public String getSelectedDateString() {
        return dateFormat.format(selectedDate);
    }
    
    /**
     * Lấy ngày được chọn dưới dạng String với format tùy chỉnh
     */
    public String getSelectedDateString(String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(selectedDate);
    }
    
    /**
     * Xóa lựa chọn (thiết lập về ngày hiện tại)
     */
  
    
    /**
     * Kiểm tra xem ngày được chọn có hợp lệ không
     */
    public boolean isValidDate() {
        return selectedDate != null;
    }
    
    /**
     * Thiết lập trạng thái enabled/disabled cho tất cả component
     */
    public void setEnabled(boolean enabled) {
        dateField.setEnabled(enabled);
        calendarButton.setEnabled(enabled);
        todayButton.setEnabled(enabled);
    }
    
    /**
     * Thiết lập font cho tất cả component
     */
    public void setFont(Font font) {
        super.setFont(font);
        if (dateField != null) {
            dateField.setFont(font);
            todayButton.setFont(font);
        }
    }
}
