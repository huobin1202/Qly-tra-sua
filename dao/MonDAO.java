package dao;
import java.sql.*;
import java.util.Scanner;

import db.DBUtil;
import view.ConsoleUI;

public class MonDAO {
    public void them() {
        Scanner sc = new Scanner(System.in);
        try {
            System.out.print("Tên món: ");
            String ten = sc.nextLine();
            System.out.print("Mô tả: ");
            String mota = sc.nextLine();
            System.out.print("Tên đơn vị: ");
            String tendv = sc.nextLine();
            System.out.print("Giá: ");
            int gia = sc.nextInt();
            sc.nextLine();
            System.out.print("Mã loại món (1: Đồ ăn, 2: Trà sữa, 3: Cà phê, 4: Topping): ");
            int ma_loai = sc.nextInt();
            //System.out.print("Số lượng: ");
            //int soluong = sc.nextInt();
            try (Connection conn = DBUtil.getConnection();
                 PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO mon (TenMon, MoTa, TenDonVi, Gia, MaLoai) VALUES (?, ?, ?, ?, ?)")) {
                ps.setString(1, ten);
                ps.setString(2, mota);
                ps.setString(3, tendv);
                ps.setLong(4, gia);
                ps.setInt(5, ma_loai);
                ps.executeUpdate();
                System.out.println("Đã thêm món.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } finally { }
    }

    public void sua() {
        Scanner sc = new Scanner(System.in);
        try {
            System.out.print("Nhập ID món cần sửa: ");
            int id = sc.nextInt();
            sc.nextLine();
            System.out.print("Tên món mới: ");
            String ten = sc.nextLine();
            System.out.print("Mô tả mới: ");
            String mota = sc.nextLine();
            System.out.print("Tên đơn vị mới: ");
            String tendv = sc.nextLine();
            System.out.print("Giá mới: ");
            int gia = sc.nextInt();
            sc.nextLine();
            System.out.print("Mã loại món mới: ");
            int ma_loai = sc.nextInt();
            try (Connection conn = DBUtil.getConnection();
                 PreparedStatement ps = conn.prepareStatement(
                    "UPDATE mon SET TenMon=?, MoTa=?, TenDonVi=?, Gia=?, MaLoai=? WHERE MaMon=?")) {
                ps.setString(1, ten);
                ps.setString(2, mota);
                ps.setString(3, tendv);
                ps.setLong(4, gia);
                ps.setInt(5, ma_loai);
                ps.setInt(6, id);
                int rows = ps.executeUpdate();
                if (rows > 0)
                    System.out.println("Đã sửa món.");
                else
                    System.out.println("Không tìm thấy món.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } finally { }
    }

    public void xoa() {
        Scanner sc = new Scanner(System.in);
        try {
            System.out.print("Nhập ID món cần xóa: ");
            int id = sc.nextInt();
            try (Connection conn = DBUtil.getConnection();
                 PreparedStatement ps = conn.prepareStatement("DELETE FROM mon WHERE MaMon=?")) {
                ps.setInt(1, id);
                int rows = ps.executeUpdate();
                if (rows > 0)
                    System.out.println("Đã xóa món.");
                else
                    System.out.println("Không tìm thấy món.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } finally { }
    }

    public void xuat() {
        ConsoleUI.printHeader("DANH SÁCH MÓN");
        System.out.println("┌────┬────────────────────────────┬────────────────────┬────────────┬────────────┬────────────┐");
        System.out.println("│ ID │ Tên                        │ Mô tả              │ Tên DV     │ Giá        │ Loại       │");
        System.out.println("├────┼────────────────────────────┼────────────────────┼────────────┼────────────┼────────────┤");
        boolean any = false;
        try (Connection conn = DBUtil.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(
                "SELECT mon.*, loaimon.TenLoai as ten_loai FROM mon LEFT JOIN loaimon ON mon.MaLoai = loaimon.MaLoai ORDER BY mon.MaMon")) {
            while (rs.next()) {
                any = true;
                System.out.printf("│ %-2d │ %-25s │ %-18s │ %-10s │ %-10d │ %-10s │\n",
                    rs.getInt("MaMon"), rs.getString("TenMon"), rs.getString("MoTa"),
                    rs.getString("TenDonVi"), rs.getLong("Gia"), rs.getString("ten_loai"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (!any) System.out.println("│ Không có dữ liệu                                                                               │");
        System.out.println("└────┴────────────────────┴────────────────────┴────────────┴────────────┴────────────┘");
        ConsoleUI.printFooter();
    }

    public void timkiem() {
        Scanner sc = new Scanner(System.in);
        try {
            System.out.println("Tìm theo: 1.ID  2.Tên");
            System.out.print("Chọn: ");
            String chooseStr = sc.nextLine();
            int choose;
            try {
                choose = Integer.parseInt(chooseStr.trim());
            } catch (NumberFormatException e) {
                System.out.println("Vui lòng nhập số hợp lệ.");
                return;
            }
            String sql;
            boolean byId = choose == 1;
            if (byId) {
                System.out.print("Nhập ID: ");
                String idStr = sc.nextLine();
                int id;
                try { id = Integer.parseInt(idStr.trim()); } catch (NumberFormatException e) { System.out.println("ID không hợp lệ."); return; }
                sql = "SELECT mon.*, loaimon.TenLoai as ten_loai FROM mon LEFT JOIN loaimon ON mon.MaLoai = loaimon.MaLoai WHERE mon.MaMon = ?";
                try (Connection conn = DBUtil.getConnection();
                     PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, id);
                    try (ResultSet rs = ps.executeQuery()) {
                        printMonTable(rs);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.print("Nhập tên: ");
                String ten = sc.nextLine();
                sql = "SELECT mon.*, loaimon.TenLoai as ten_loai FROM mon LEFT JOIN loaimon ON mon.MaLoai = loaimon.MaLoai WHERE mon.TenMon LIKE ?";
                try (Connection conn = DBUtil.getConnection();
                     PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, "%" + ten + "%");
                    try (ResultSet rs = ps.executeQuery()) {
                        printMonTable(rs);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } finally { }
    }

    private void printMonTable(ResultSet rs) throws SQLException {
        System.out.println("\n╔════╦════════════════════╦════════════════════╦════════════╦════════════╦════════════╗");
        System.out.println("║ ID ║ Tên               ║ Mô tả              ║ Tên DV     ║ Giá        ║ Loại       ║");
        System.out.println("╠════╬════════════════════╬════════════════════╬════════════╬════════════╬════════════╣");
        boolean any = false;
        while (rs.next()) {
            any = true;
            System.out.printf("║ %-2d ║ %-18s ║ %-18s ║ %-10s ║ %-10d ║ %-10s ║\n",
                rs.getInt("MaMon"), rs.getString("TenMon"), rs.getString("MoTa"),
                rs.getString("TenDonVi"), rs.getLong("Gia"), rs.getString("ten_loai"));
        }
        if (!any) System.out.println("║ Không có kết quả                                                             ║");
        System.out.println("╚════╩════════════════════╩════════════════════╩════════════╩════════════╩════════════╝");
    }
}