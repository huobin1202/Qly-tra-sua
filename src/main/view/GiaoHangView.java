package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import database.DBUtil;
import dto.DonHangDTO;
import dto.GiaoHangDTO;
import dao.DonHangDAO;

public class GiaoHangView extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> searchCombo;
    private DonHangDAO donHangDAO;

    public GiaoHangView() {
        donHangDAO = new DonHangDAO();
        initializeComponents();
        setupLayout();
        loadData();
    }

    private void initializeComponents() {
        setLayout(new BorderLayout());

        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        headerPanel.setBackground(new Color(240, 240, 240));

        JLabel titleLabel = new JLabel("Quản lý Giao hàng");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(new Color(240, 240, 240));

        searchCombo = new JComboBox<>(new String[]{"Tất cả", "Chưa giao", "Đang giao", "Đã giao"});
        searchField = new JTextField(20);
        JButton searchButton = new JButton("Tìm kiếm");
        JButton refreshButton = new JButton("Làm mới");

        searchPanel.add(new JLabel("Trạng thái:"));
        searchPanel.add(searchCombo);
        searchPanel.add(new JLabel("Tìm kiếm:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(refreshButton);

        headerPanel.add(searchPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(new Color(240, 240, 240));

        JButton addButton = new JButton("Thêm giao hàng");
        JButton editButton = new JButton("Cập nhật trạng thái");
        JButton deleteButton = new JButton("Xóa");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        headerPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(headerPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"Mã giao hàng", "Mã đơn hàng", "Tên khách hàng", "Địa chỉ", "SĐT", "Trạng thái", "Ngày giao", "Ghi chú"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(scrollPane, BorderLayout.CENTER);

        // Event handlers
        searchButton.addActionListener(e -> performSearch());
        refreshButton.addActionListener(e -> loadData());
        addButton.addActionListener(e -> showAddDialog());
        editButton.addActionListener(e -> showEditDialog());
        deleteButton.addActionListener(e -> performDelete());

        // Enter key search
        searchField.addActionListener(e -> performSearch());
    }

    private void setupLayout() {
        // Layout đã được setup trong initializeComponents()
    }

    private void loadData() {
        try {
            tableModel.setRowCount(0);
            
            // Lấy danh sách đơn hàng cần giao
            List<DonHangDTO> donHangs = donHangDAO.layTatCaDonHang();
            
            for (DonHangDTO donHang : donHangs) {
                // Chỉ hiển thị đơn hàng đã thanh toán
                if ("dathanhtoan".equals(donHang.getTrangThai())) {
                    Object[] row = {
                        "GH" + donHang.getMaDon(), // Mã giao hàng giả lập
                        donHang.getMaDon(),
                        "Khách hàng " + donHang.getMaDon(), // Tên khách hàng giả lập
                        "Địa chỉ giao hàng", // Địa chỉ giả lập
                        "0123456789", // SĐT giả lập
                        "Chưa giao",
                        "",
                        ""
                    };
                    tableModel.addRow(row);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void performSearch() {
        String searchText = searchField.getText().trim();
        String statusFilter = (String) searchCombo.getSelectedItem();
        
        // Implement search logic here
        loadData(); // Tạm thời reload toàn bộ dữ liệu
    }

    private void showAddDialog() {
        JOptionPane.showMessageDialog(this, "Chức năng thêm giao hàng sẽ được phát triển", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showEditDialog() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một giao hàng để cập nhật", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String[] statusOptions = {"Chưa giao", "Đang giao", "Đã giao"};
        String currentStatus = (String) tableModel.getValueAt(selectedRow, 5);
        
        String newStatus = (String) JOptionPane.showInputDialog(
            this,
            "Chọn trạng thái mới:",
            "Cập nhật trạng thái giao hàng",
            JOptionPane.QUESTION_MESSAGE,
            null,
            statusOptions,
            currentStatus
        );

        if (newStatus != null && !newStatus.equals(currentStatus)) {
            tableModel.setValueAt(newStatus, selectedRow, 5);
            if ("Đã giao".equals(newStatus)) {
                tableModel.setValueAt(new java.util.Date().toString(), selectedRow, 6);
            }
            JOptionPane.showMessageDialog(this, "Cập nhật trạng thái thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void performDelete() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một giao hàng để xóa", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int result = JOptionPane.showConfirmDialog(
            this,
            "Bạn có chắc chắn muốn xóa giao hàng này?",
            "Xác nhận xóa",
            JOptionPane.YES_NO_OPTION
        );

        if (result == JOptionPane.YES_OPTION) {
            tableModel.removeRow(selectedRow);
            JOptionPane.showMessageDialog(this, "Xóa giao hàng thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
