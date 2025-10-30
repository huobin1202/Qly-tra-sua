package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import dto.NhanVienDTO;
import dto.NhanVienQuanLyDTO;
import dto.NhanVienThuongDTO;

public class LoginDialog extends JDialog {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private boolean loginSuccessful = false;
    
    public LoginDialog(Frame parent) {
        super(parent, "Đăng nhập hệ thống", true);
        initializeComponents();
        setupLayout();
        setupEventHandlers();
    }
    
    private void initializeComponents() {
        setSize(700, 500);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(76, 175, 80)); // green
        headerPanel.setPreferredSize(new Dimension(0, 80));
        JLabel titleLabel = new JLabel("Đăng Nhập");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.BLACK);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(titleLabel);
        
        // Main content
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(240, 248, 255));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Username
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST;
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        mainPanel.add(userLabel, gbc);
        
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        mainPanel.add(usernameField, gbc);
        
        // Password
        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.EAST;
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        mainPanel.add(passLabel, gbc);
        
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        mainPanel.add(passwordField, gbc);

        // Forgot password link (right aligned under password)
        gbc.gridx = 1; gbc.gridy = 2; gbc.anchor = GridBagConstraints.EAST;
        JLabel forgotLabel = new JLabel("<html><a href=''>Quên mật khẩu</a></html>");
        forgotLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        forgotLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        forgotLabel.setForeground(new Color(33, 150, 243));
        mainPanel.add(forgotLabel, gbc);
        
        // Buttons
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(new Color(240, 248, 255));
        
        loginButton = new JButton("Đăng Nhập");
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setPreferredSize(new Dimension(150, 35));
        loginButton.setBackground(new Color(76, 175, 80));
        loginButton.setForeground(Color.BLACK);
        loginButton.setFocusPainted(false);
        
        // Hover effect
        loginButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                loginButton.setBackground(new Color(56, 142, 60));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                loginButton.setBackground(new Color(76, 175, 80));
            }
        });
        
        buttonPanel.add(loginButton);
        mainPanel.add(buttonPanel, gbc);

        // Sign up link under button
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        JLabel signupLabel = new JLabel("<html><a href=''>Chưa có tài khoản?</a></html>");
        signupLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        signupLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        mainPanel.add(signupLabel, gbc);
        
        add(headerPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);

        // Set default button to allow Enter key to trigger login anywhere
        getRootPane().setDefaultButton(loginButton);
    }
    
    private void setupEventHandlers() {
        // Login button
        if (loginButton != null) {
            loginButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    performLogin();
                }
            });
        }

        // Optional links
        // Show placeholder actions for forgot password and signup
        for (Component comp : getContentPane().getComponents()) {
            if (comp instanceof JPanel) {
                for (Component c2 : ((JPanel) comp).getComponents()) {
                    if (c2 instanceof JPanel) {
                        for (Component c3 : ((JPanel) c2).getComponents()) {
                            if (c3 instanceof JLabel) {
                                JLabel l = (JLabel) c3;
                                if (l.getText().contains("Quên mật khẩu")) {
                                    l.addMouseListener(new java.awt.event.MouseAdapter() {
                                        public void mouseClicked(java.awt.event.MouseEvent e) {
                                            JOptionPane.showMessageDialog(LoginDialog.this, "Vui lòng liên hệ quản trị để đặt lại mật khẩu.");
                                        }
                                    });
                                } else if (l.getText().contains("Chưa có tài khoản")) {
                                    l.addMouseListener(new java.awt.event.MouseAdapter() {
                                        public void mouseClicked(java.awt.event.MouseEvent e) {
                                            JOptionPane.showMessageDialog(LoginDialog.this, "Tính năng đăng ký sẽ được bổ sung sau.");
                                        }
                                    });
                                }
                            }
                        }
                    }
                }
            }
        }
        
        // Enter key on password field
        passwordField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });
    }
    
    private JButton findButton(String text) {
        for (Component comp : getComponents()) {
            if (comp instanceof JPanel) {
                JButton button = findButtonInPanel((JPanel) comp, text);
                if (button != null) return button;
            }
        }
        return null;
    }
    
    private JButton findButtonInPanel(JPanel panel, String text) {
        for (Component comp : panel.getComponents()) {
            if (comp instanceof JButton) {
                JButton button = (JButton) comp;
                if (button.getText().equals(text)) {
                    return button;
                }
            } else if (comp instanceof JPanel) {
                JButton button = findButtonInPanel((JPanel) comp, text);
                if (button != null) return button;
            }
        }
        return null;
    }
    
    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Kiểm tra đăng nhập với database
        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/bants", "root", "")) {
            
            PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM nhanvien WHERE TaiKhoan=? AND MatKhau=?");
            ps.setString(1, username);
            ps.setString(2, password);
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                // Lưu thông tin session
                database.Session.currentMaNV = rs.getInt("MaNV");
                database.Session.currentTaiKhoan = rs.getString("TaiKhoan");
                database.Session.currentChucVu = rs.getString("ChucVu");

                // Lưu object nhân viên thực tế vào Session
                String trangThai = rs.getString("TrangThai");
                if (trangThai != null && trangThai.equalsIgnoreCase("nghiviec")) {
                    JOptionPane.showMessageDialog(this, "Tài khoản này đã nghỉ việc, không thể đăng nhập!", "Bị từ chối", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String chucVuDB = rs.getString("ChucVu");
                NhanVienDTO nhanVienObj;
                if (chucVuDB != null && chucVuDB.trim().equalsIgnoreCase("quanly")) {
                    nhanVienObj = new NhanVienQuanLyDTO(
                        rs.getInt("MaNV"),
                        rs.getString("TaiKhoan"),
                        rs.getString("MatKhau"),
                        rs.getString("HoTen"),
                        rs.getString("SDT"),
                        rs.getTimestamp("NgayVaoLam"),
                        rs.getLong("Luong"),
                        trangThai
                    );
                } else {
                    nhanVienObj = new NhanVienThuongDTO(
                        rs.getInt("MaNV"),
                        rs.getString("TaiKhoan"),
                        rs.getString("MatKhau"),
                        rs.getString("HoTen"),
                        rs.getString("SDT"),
                        rs.getTimestamp("NgayVaoLam"),
                        rs.getLong("Luong"),
                        trangThai
                    );
                }
                database.Session.currentNhanVien = nhanVienObj;

                // Debug: In ra thông tin để kiểm tra
                loginSuccessful = true;
                JOptionPane.showMessageDialog(this, "Đăng nhập thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Sai tài khoản hoặc mật khẩu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                passwordField.setText("");
                passwordField.requestFocus();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi kết nối database: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public boolean isLoginSuccessful() {
        return loginSuccessful;
    }
}
