package view.gui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private final JPanel contentPanel;

    public MainFrame() {
        setTitle("Trang quản lý");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 700);
        setLocationRelativeTo(null);

        JPanel sidebar = new JPanel(new GridLayout(0, 1, 8, 8));
        sidebar.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JButton btnHangHoa = new JButton("Quản lý hàng hóa");
        JButton btnKhachHang = new JButton("Quản lý khách hàng");
        JButton btnNhanVien = new JButton("Quản lý nhân viên");
        JButton btnDatHang = new JButton("Quản lý đặt hàng");
        JButton btnThongKe = new JButton("Thống kê");
        JButton btnThoat = new JButton("Thoát");

        sidebar.add(btnHangHoa);
        sidebar.add(btnKhachHang);
        sidebar.add(btnNhanVien);
        sidebar.add(btnDatHang);
        sidebar.add(btnThongKe);
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(btnThoat);

        contentPanel = new JPanel(new BorderLayout());

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sidebar, contentPanel);
        split.setDividerLocation(220);
        split.setResizeWeight(0);
        add(split, BorderLayout.CENTER);

        btnHangHoa.addActionListener(e -> setContent(new MonPanel()));
        btnKhachHang.addActionListener(e -> setContent(new PlaceholderPanel("Khách hàng")));
        btnNhanVien.addActionListener(e -> setContent(new PlaceholderPanel("Nhân viên")));
        btnDatHang.addActionListener(e -> setContent(new PlaceholderPanel("Đặt hàng")));
        btnThongKe.addActionListener(e -> setContent(new PlaceholderPanel("Thống kê")));
        btnThoat.addActionListener(e -> System.exit(0));

        setContent(new MonPanel());
    }

    private void setContent(JComponent comp) {
        contentPanel.removeAll();
        contentPanel.add(comp, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private static class PlaceholderPanel extends JPanel {
        PlaceholderPanel(String name) {
            setLayout(new BorderLayout());
            add(new JLabel("Chức năng '" + name + "' sẽ sớm có trong GUI. Hãy dùng CLI tạm thời.", SwingConstants.CENTER), BorderLayout.CENTER);
        }
    }
}


