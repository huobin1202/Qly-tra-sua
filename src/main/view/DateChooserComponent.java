package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    
    public DateChooserComponent() {
        this(new SimpleDateFormat("yyyy-MM-dd"));
    }
    
    public DateChooserComponent(SimpleDateFormat format) {
        this.dateFormat = format;
        initializeComponents();
        setupLayout();
    }
    
    public DateChooserComponent(Date initialDate) {
        this();
        setDate(initialDate);
    }
    
    private void initializeComponents() {
        // Tạo SpinnerDateModel với ngày hiện tại
        SpinnerDateModel model = new SpinnerDateModel();
        model.setCalendarField(Calendar.DAY_OF_MONTH);
        
        // Tạo JSpinner với SpinnerDateModel
        dateSpinner = new JSpinner(model);
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, dateFormat.toPattern()));
        
        // Thiết lập kích thước
        dateSpinner.setPreferredSize(new Dimension(120, 25));
        
        // Tạo nút "Hôm nay"
        todayButton = new JButton("Hôm nay");
        todayButton.setFont(new Font("Arial", Font.PLAIN, 10));
        todayButton.setPreferredSize(new Dimension(70, 25));
        todayButton.setBackground(new Color(70, 130, 180));
        todayButton.setForeground(Color.WHITE);
        todayButton.setFocusPainted(false);
        todayButton.addActionListener(e -> setCurrentDate());
        
   
    }
    
    private void setupLayout() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
        setOpaque(false);
        
        add(dateSpinner);
        add(todayButton);
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
            setCurrentDate();
            return;
        }
        dateSpinner.setValue(date);
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
        return dateFormat.format(getSelectedDate());
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
    public void setEnabled(boolean enabled) {
        dateSpinner.setEnabled(enabled);
        todayButton.setEnabled(enabled);
    }
    
    /**
     * Thiết lập font cho tất cả component
     */
    public void setFont(Font font) {
        super.setFont(font);
        if (dateSpinner != null) {
            dateSpinner.setFont(font);
            todayButton.setFont(font);
        }
    }
    
    /**
     * Lấy JSpinner để có thể tùy chỉnh thêm
     */
    public JSpinner getDateSpinner() {
        return dateSpinner;
    }
}
