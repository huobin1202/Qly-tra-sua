package view;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import database.DBUtil;
import dto.MonDTO;

public class ThemMonDialog extends JDialog {
    private final MonDTO selectedProduct;
    private JTextField tenMonField;
    private JSpinner soLuongSpinner;
    private JTextField giaMonField;
    private JComboBox<String> toppingCombo;
    private JLabel tongTienLabel;
    private JButton themButton;
    private JButton huyButton;
    
    private int soLuong = 1;
    private long giaMon = 0;
    private long giaTopping = 0;
    private List<MonDTO> toppings;
    
    public ThemMonDialog(Window parent, MonDTO product) {
        super(parent, "Hóa đơn - Thêm món", ModalityType.APPLICATION_MODAL);
        this.selectedProduct = product;
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadToppings();
        updateTotal();
    }
    
    private void initializeComponents() {
        setSize(400, 300);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        // Tên món (chỉ đọc)
        tenMonField = new JTextField(selectedProduct.getTenMon());
        tenMonField.setEditable(false);
        tenMonField.setForeground(new Color(128, 0, 128)); // Màu tím
        
        // Số lượng
        soLuongSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 99, 1));
        soLuongSpinner.setPreferredSize(new Dimension(60, 25));
        
        // Giá món
        giaMonField = new JTextField(String.valueOf(selectedProduct.getGia()));
        giaMonField.setEditable(false);
        giaMonField.setForeground(Color.BLACK);
        
        // Topping combo
        toppingCombo = new JComboBox<>();
        toppingCombo.setPreferredSize(new Dimension(150, 25));
        
        // Tổng tiền
        tongTienLabel = new JLabel("0 VND");
        tongTienLabel.setForeground(Color.RED);
        tongTienLabel.setFont(new Font("Arial", Font.BOLD, 12));
        
        // Buttons
        themButton = new JButton("Thêm");
        themButton.setBackground(new Color(34, 139, 34));
        themButton.setForeground(Color.BLACK);
        themButton.setFocusPainted(false);
        
        huyButton = new JButton("Hủy");
        huyButton.setBackground(new Color(220, 20, 60));
        huyButton.setForeground(Color.BLACK);
        huyButton.setFocusPainted(false);
        
        toppings = new ArrayList<>();
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 248, 255));
        
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(240, 248, 255));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Tên món
        gbc.gridx = 0; gbc.gridy = 0;
        mainPanel.add(new JLabel("Tên món:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(tenMonField, gbc);
        
        // Số lượng
        gbc.gridx = 0; gbc.gridy = 1;
        mainPanel.add(new JLabel("Số Lượng:"), gbc);
        gbc.gridx = 1;
        
        JPanel quantityPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        quantityPanel.setBackground(new Color(240, 248, 255));
        
        JButton minusButton = new JButton("-");
        minusButton.setPreferredSize(new Dimension(30, 25));
        minusButton.setBackground(new Color(70, 130, 180));
        minusButton.setForeground(Color.BLACK);
        minusButton.setFocusPainted(false);
        minusButton.addActionListener(e -> {
            int current = (Integer) soLuongSpinner.getValue();
            if (current > 1) {
                soLuongSpinner.setValue(current - 1);
                updateTotal();
            }
        });
        
        JButton plusButton = new JButton("+");
        plusButton.setPreferredSize(new Dimension(30, 25));
        plusButton.setBackground(new Color(70, 130, 180));
        plusButton.setForeground(Color.BLACK);
        plusButton.setFocusPainted(false);
        plusButton.addActionListener(e -> {
            int current = (Integer) soLuongSpinner.getValue();
            if (current < 99) {
                soLuongSpinner.setValue(current + 1);
                updateTotal();
            }
        });
        
        quantityPanel.add(minusButton);
        quantityPanel.add(soLuongSpinner);
        quantityPanel.add(plusButton);
        
        mainPanel.add(quantityPanel, gbc);
        
        // Giá món
        gbc.gridx = 0; gbc.gridy = 2;
        mainPanel.add(new JLabel("Giá Món:"), gbc);
        gbc.gridx = 1;
        
        JPanel pricePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        pricePanel.setBackground(new Color(240, 248, 255));
        pricePanel.add(giaMonField);
        pricePanel.add(new JLabel("VND"));
        
        mainPanel.add(pricePanel, gbc);
        
        // Topping
        gbc.gridx = 0; gbc.gridy = 3;
        mainPanel.add(new JLabel("Topping:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(toppingCombo, gbc);
        
        // Tổng tiền
        gbc.gridx = 0; gbc.gridy = 4;
        mainPanel.add(new JLabel("Tổng Tiền:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(tongTienLabel, gbc);
        gbc.gridx = 2;
        mainPanel.add(new JLabel("VND"), gbc);
        
        // Buttons
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 3; gbc.anchor = GridBagConstraints.CENTER;
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(new Color(240, 248, 255));
        buttonPanel.add(themButton);
        buttonPanel.add(huyButton);
        mainPanel.add(buttonPanel, gbc);
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private void setupEventHandlers() {
        // Spinner change event
        soLuongSpinner.addChangeListener(e -> updateTotal());
        
        // Topping selection change
        toppingCombo.addActionListener(e -> updateTotal());
        
        // Buttons
        themButton.addActionListener(e -> addItem());
        huyButton.addActionListener(e -> dispose());
    }
    
    private void loadToppings() {
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT * FROM mon WHERE MaLoai = 4 AND TinhTrang = 'Đang bán' ORDER BY TenMon";
            
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                
                // Thêm "No Topping" đầu tiên
                toppingCombo.addItem("No Topping (0)");
                
                while (rs.next()) {
                    MonDTO topping = new MonDTO();
                    topping.setMaMon(rs.getInt("MaMon"));
                    topping.setTenMon(rs.getString("TenMon"));
                    topping.setGia(rs.getLong("Gia"));
                    toppings.add(topping);
                    
                    toppingCombo.addItem(topping.getTenMon() + " (" + String.format("%,d", topping.getGia()) + ")");
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải topping: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateTotal() {
        soLuong = (Integer) soLuongSpinner.getValue();
        giaMon = selectedProduct.getGia();
        
        // Lấy giá topping
        int selectedIndex = toppingCombo.getSelectedIndex();
        if (selectedIndex == 0) {
            giaTopping = 0; // No Topping
        } else if (selectedIndex > 0 && selectedIndex <= toppings.size()) {
            giaTopping = toppings.get(selectedIndex - 1).getGia();
        }
        
        long tongTien = (giaMon + giaTopping) * soLuong;
        tongTienLabel.setText(String.format("%,d", tongTien));
    }
    
    private void addItem() {
        // Tạo kết quả để trả về
        AddItemResult result = new AddItemResult();
        result.maMon = selectedProduct.getMaMon();
        result.tenMon = selectedProduct.getTenMon();
        result.soLuong = soLuong;
        result.giaMon = giaMon;
        result.giaTopping = giaTopping;
        result.tenTopping = getSelectedToppingName();
        
        // Lưu kết quả vào dialog
        this.result = result;
        dispose();
    }
    
    private String getSelectedToppingName() {
        int selectedIndex = toppingCombo.getSelectedIndex();
        if (selectedIndex == 0) {
            return "No Topping";
        } else if (selectedIndex > 0 && selectedIndex <= toppings.size()) {
            return toppings.get(selectedIndex - 1).getTenMon();
        }
        return "No Topping";
    }
    
    private AddItemResult result;
    
    public void setResult(AddItemResult result) {
        this.result = result;
    }
    
    public AddItemResult getResult() {
        return result;
    }
    
    // Inner class để lưu kết quả
    public static class AddItemResult {
        public int maMon;
        public String tenMon;
        public int soLuong;
        public long giaMon;
        public long giaTopping;
        public String tenTopping;
    }
}
