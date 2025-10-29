package utils;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Component để chọn ngày bằng date chooser
 * Sử dụng JSpinner với SpinnerDateModel để tạo date chooser
 */
public class DateChooserComponent extends JPanel {
    private JSpinner dateSpinner;
    private JButton todayButton;
    private SimpleDateFormat dateFormat;
    private boolean showTodayButton;
    private Date maxDate; // Ngày tối đa có thể chọn
    
    public DateChooserComponent() {
        this(new SimpleDateFormat("yyyy-MM-dd"), true, new Date()); // Mặc định giới hạn đến hôm nay
    }
    
    public DateChooserComponent(SimpleDateFormat format) {
        this(format, true, new Date()); // Mặc định giới hạn đến hôm nay
    }
    
    public DateChooserComponent(SimpleDateFormat format, boolean showTodayButton) {
        this(format, showTodayButton, new Date()); // Mặc định giới hạn đến hôm nay
    }
    
    public DateChooserComponent(SimpleDateFormat format, boolean showTodayButton, Date maxDate) {
        this.dateFormat = format;
        this.showTodayButton = showTodayButton;
        this.maxDate = maxDate;
        initializeComponents();
        setupLayout();
    }
    
    public DateChooserComponent(Date initialDate) {
        this();
        setDate(initialDate);
    }
    
    /**
     * Constructor cho ngày sinh: ẩn nút "Hôm nay" và giới hạn ngày tối đa là hôm nay
     */
    public DateChooserComponent(boolean showTodayButton, Date maxDate) {
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        this.showTodayButton = showTodayButton;
        this.maxDate = maxDate;
        initializeComponents();
        setupLayout();
    }
    
    private void initializeComponents() {
        // Chuẩn hóa maxDate để chỉ chứa ngày tháng năm (không có giờ phút giây)
        Date normalizedMaxDate = null;
        if (maxDate != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(maxDate);
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            cal.set(Calendar.MILLISECOND, 999);
            normalizedMaxDate = cal.getTime();
        }
        
        // Tạo SpinnerDateModel với ngày hiện tại
        Date minDate = null; // Ngày tối thiểu
        Date startDate = new Date(); // Ngày bắt đầu
        Date endDate = normalizedMaxDate; // Ngày tối đa
        
        SpinnerDateModel model = new SpinnerDateModel(startDate, minDate, endDate, Calendar.DAY_OF_MONTH);
        
        // Tạo JSpinner với SpinnerDateModel
        dateSpinner = new JSpinner(model);
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, dateFormat.toPattern()));
        
        // Thiết lập kích thước
        dateSpinner.setPreferredSize(new Dimension(120, 25));
        
        // Tạo nút "Hôm nay" chỉ khi showTodayButton = true
        if (showTodayButton) {
            todayButton = new JButton("Hôm nay");
            todayButton.setFont(new Font("Arial", Font.PLAIN, 10));
            todayButton.setPreferredSize(new Dimension(70, 25));
            todayButton.setBackground(new Color(70, 130, 180));
            todayButton.setForeground(Color.WHITE);
            todayButton.setFocusPainted(false);
            todayButton.addActionListener(e -> setCurrentDate());
        }
    }
    
    private void setupLayout() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
        setOpaque(false);
        
        add(dateSpinner);
        if (showTodayButton && todayButton != null) {
            add(todayButton);
        }
    }
    
    /**
     * Thiết lập ngày hiện tại
     */
    public void setCurrentDate() {
        dateSpinner.setValue(new Date());
    }
    
    /**
     * Thiết lập ngày cụ thể
     */
    public void setDate(Date date) {
        if (date == null) {
            if (maxDate != null) {
                // Nếu có giới hạn ngày tối đa và không có ngày ban đầu, để trống
                dateSpinner.setValue(null);
            } else {
                setCurrentDate();
            }
            return;
        }
        // Kiểm tra nếu ngày vượt quá maxDate
        if (maxDate != null && date.after(maxDate)) {
            dateSpinner.setValue(maxDate);
        } else {
            dateSpinner.setValue(date);
        }
    }
    
    /**
     * Lấy ngày được chọn dưới dạng Date
     */
    public Date getSelectedDate() {
        return (Date) dateSpinner.getValue();
    }
    
    /**
     * Lấy ngày được chọn dưới dạng String
     */
    public String getSelectedDateString() {
        try {
            Date selectedDate = getSelectedDate();
            if (selectedDate == null) {
                return "";
            }
            return dateFormat.format(selectedDate);
        } catch (Exception e) {
            return "";
        }
    }
    
    /**
     * Lấy ngày được chọn dưới dạng String với format tùy chỉnh
     */
    public String getSelectedDateString(String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(getSelectedDate());
    }
    
    /**
     * Xóa lựa chọn (thiết lập về ngày hiện tại)
     */
  
    
    /**
     * Kiểm tra xem ngày được chọn có hợp lệ không
     */
    public boolean isValidDate() {
        try {
            Date selectedDate = getSelectedDate();
            return selectedDate != null;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Thiết lập trạng thái enabled/disabled cho tất cả component
     */
    @Override
    public void setEnabled(boolean enabled) {
        dateSpinner.setEnabled(enabled);
        if (todayButton != null) {
            todayButton.setEnabled(enabled);
        }
    }
    
    /**
     * Thiết lập font cho tất cả component
     */
    @Override
    public void setFont(Font font) {
        super.setFont(font);
        if (dateSpinner != null) {
            dateSpinner.setFont(font);
            if (todayButton != null) {
                todayButton.setFont(font);
            }
        }
    }
    
    /**
     * Lấy JSpinner để có thể tùy chỉnh thêm
     */
    public JSpinner getDateSpinner() {
        return dateSpinner;
    }
}

