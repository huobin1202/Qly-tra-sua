package view.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

import db.DBUtil;

public class MonPanel extends JPanel {
    private final DefaultTableModel tableModel;
    private final JTable table;
    private final JTextField tfSearch;

    public MonPanel() {
        setLayout(new BorderLayout(8, 8));

        JPanel top = new JPanel(new BorderLayout(8, 8));
        tfSearch = new JTextField();
        String[] cols = {"ID", "Tên Món", "Mô tả", "Link ảnh", "Tên đv", "Giá đv", "Đơn vị", "Mã loại"};
        JButton btnSearch = new JButton("Tìm");
        JButton btnReload = new JButton("Sync");

        JPanel searchPanel = new JPanel(new BorderLayout(6, 6));
        searchPanel.add(new JLabel("Search"), BorderLayout.WEST);
        searchPanel.add(tfSearch, BorderLayout.CENTER);
        JPanel searchBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        searchBtns.add(btnSearch);
        searchBtns.add(btnReload);
        searchPanel.add(searchBtns, BorderLayout.EAST);

        top.add(searchPanel, BorderLayout.CENTER);
        add(top, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        JButton btnAdd = new JButton("Thêm");
        JButton btnEdit = new JButton("Sửa");
        JButton btnDelete = new JButton("Xóa");
        for (JButton b : new JButton[]{btnAdd, btnEdit, btnDelete}) {
            b.setAlignmentX(Component.CENTER_ALIGNMENT);
            b.setMaximumSize(new Dimension(120, 36));
            right.add(Box.createVerticalStrut(8));
            right.add(b);
        }
        add(right, BorderLayout.EAST);

        btnReload.addActionListener(e -> loadData(null));
        btnSearch.addActionListener(e -> loadData(tfSearch.getText().trim()));
        btnAdd.addActionListener(e -> showEditDialog(null));
        btnEdit.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                MonRecord rec = MonRecord.fromTableRow(tableModel, row);
                showEditDialog(rec);
            } else JOptionPane.showMessageDialog(this, "Chọn một dòng để sửa");
        });
        btnDelete.addActionListener(e -> deleteSelected());

        loadData(null);
    }

    private void loadData(String keyword) {
        tableModel.setRowCount(0);
        String sql = "SELECT mon.*, loaimon.ten as ten_loai FROM mon LEFT JOIN loaimon ON mon.ma_loai=loaimon.ma";
        if (keyword != null && !keyword.isEmpty()) sql += " WHERE mon.ten LIKE ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (keyword != null && !keyword.isEmpty()) ps.setString(1, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("id"), rs.getString("ten"), rs.getString("mota"), rs.getString("anh"),
                        rs.getString("tendv"), rs.getInt("gia"), rs.getString("dv"), rs.getInt("ma_loai")
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showEditDialog(MonRecord record) {
        JTextField tfTen = new JTextField(record == null ? "" : record.ten);
        JTextField tfMoTa = new JTextField(record == null ? "" : record.mota);
        JTextField tfAnh = new JTextField(record == null ? "" : record.anh);
        JTextField tfTenDv = new JTextField(record == null ? "" : record.tendv);
        JSpinner spGia = new JSpinner(new SpinnerNumberModel(record == null ? 0 : record.gia, 0, 1_000_000_000, 1000));
        JTextField tfDv = new JTextField(record == null ? "" : record.dv);
        JSpinner spMaLoai = new JSpinner(new SpinnerNumberModel(record == null ? 1 : record.maLoai, 1, 99, 1));

        JPanel form = new JPanel(new GridLayout(0, 2, 6, 6));
        form.add(new JLabel("Tên món")); form.add(tfTen);
        form.add(new JLabel("Mô tả")); form.add(tfMoTa);
        form.add(new JLabel("Link ảnh")); form.add(tfAnh);
        form.add(new JLabel("Tên đơn vị")); form.add(tfTenDv);
        form.add(new JLabel("Giá")); form.add(spGia);
        form.add(new JLabel("Đơn vị")); form.add(tfDv);
        form.add(new JLabel("Mã loại")); form.add(spMaLoai);

        int opt = JOptionPane.showConfirmDialog(this, form, record == null ? "Thêm món" : "Sửa món", JOptionPane.OK_CANCEL_OPTION);
        if (opt != JOptionPane.OK_OPTION) return;

        String sql;
        boolean isInsert = record == null;
        sql = isInsert ?
                "INSERT INTO mon (ten, mota, anh, tendv, gia, dv, ma_loai) VALUES (?, ?, ?, ?, ?, ?, ?)" :
                "UPDATE mon SET ten=?, mota=?, anh=?, tendv=?, gia=?, dv=?, ma_loai=? WHERE id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tfTen.getText().trim());
            ps.setString(2, tfMoTa.getText().trim());
            ps.setString(3, tfAnh.getText().trim());
            ps.setString(4, tfTenDv.getText().trim());
            ps.setInt(5, ((Number) spGia.getValue()).intValue());
            ps.setString(6, tfDv.getText().trim());
            ps.setInt(7, ((Number) spMaLoai.getValue()).intValue());
            if (!isInsert) ps.setInt(8, record.id);
            ps.executeUpdate();
            loadData(null);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Chọn một dòng để xóa"); return; }
        int id = (int) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Xóa món ID=" + id + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM mon WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
            loadData(null);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static class MonRecord {
        final int id; final String ten; final String mota; final String anh; final String tendv; final int gia; final String dv; final int maLoai;
        MonRecord(int id, String ten, String mota, String anh, String tendv, int gia, String dv, int maLoai) {
            this.id = id; this.ten = ten; this.mota = mota; this.anh = anh; this.tendv = tendv; this.gia = gia; this.dv = dv; this.maLoai = maLoai;
        }
        static MonRecord fromTableRow(DefaultTableModel m, int r) {
            return new MonRecord(
                    (int) m.getValueAt(r, 0), (String) m.getValueAt(r, 1), (String) m.getValueAt(r, 2), (String) m.getValueAt(r, 3),
                    (String) m.getValueAt(r, 4), (int) m.getValueAt(r, 5), (String) m.getValueAt(r, 6), (int) m.getValueAt(r, 7)
            );
        }
    }
}


