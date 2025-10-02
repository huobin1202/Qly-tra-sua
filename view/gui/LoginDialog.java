package view.gui;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class LoginDialog extends JDialog {
    private boolean authenticated = false;

    public LoginDialog(Window owner) {
        super(owner, "Đăng nhập", ModalityType.APPLICATION_MODAL);
        setSize(360, 200);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(8, 8));

        JTextField tfUser = new JTextField();
        JPasswordField pfPass = new JPasswordField();

        JPanel form = new JPanel(new GridLayout(0, 2, 6, 6));
        form.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        form.add(new JLabel("Tài khoản")); form.add(tfUser);
        form.add(new JLabel("Mật khẩu")); form.add(pfPass);
        add(form, BorderLayout.CENTER);

        JButton btnOk = new JButton("Đăng nhập");
        JButton btnCancel = new JButton("Hủy");
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.add(btnCancel); actions.add(btnOk);
        add(actions, BorderLayout.SOUTH);

        btnCancel.addActionListener(e -> dispose());
        btnOk.addActionListener(e -> {
            if (check(tfUser.getText().trim(), new String(pfPass.getPassword()))) {
                authenticated = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Sai tài khoản hoặc mật khẩu");
            }
        });
    }

    public boolean isAuthenticated() { return authenticated; }

    private boolean check(String user, String pass) {
        try (Connection conn = java.sql.DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/bants", "root", "");
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT 1 FROM nhanvien WHERE tentaikhoan=? AND matkhau=?")) {
            ps.setString(1, user);
            ps.setString(2, pass);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
}


