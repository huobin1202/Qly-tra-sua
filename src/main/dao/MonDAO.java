package dao;
import java.sql.*;
import java.util.Scanner;

import datebase.DBUtil;
import view.ConsoleUI;

public class MonDAO {
    public void them() {
        Scanner sc = new Scanner(System.in);
        try {
            String ten = promptString(sc, "Tên món (0: hủy)");
            if (ten == null) { System.out.println("Đã hủy thêm món."); return; }

            String mota = promptString(sc, "Mô tả (0: hủy)");
            if (mota == null) { System.out.println("Đã hủy thêm món."); return; }

            String tendv = promptString(sc, "Tên đơn vị (0: hủy)");
            if (tendv == null) { System.out.println("Đã hủy thêm món."); return; }

            Integer gia = promptInt(sc, "Giá (số nguyên, 0: hủy)", 0, Integer.MAX_VALUE, true);
            if (gia == null) { System.out.println("Đã hủy thêm món."); return; }

            String tinhTrangStr = promptTinhTrang(sc);
            if (tinhTrangStr == null) { System.out.println("Đã hủy thêm món."); return; }

            try (Connection conn = DBUtil.getConnection()) {
                Integer ma_loai = promptMaLoai(sc, conn);
                if (ma_loai == null) { System.out.println("Đã hủy thêm món."); return; }

                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO mon (TenMon, MoTa, TenDonVi, Gia, MaLoai, TinhTrang) VALUES (?, ?, ?, ?, ?, ?)")) {
                    ps.setString(1, ten);
                    ps.setString(2, mota);
                    ps.setString(3, tendv);
                    ps.setLong(4, gia);
                    ps.setInt(5, ma_loai);
                    ps.setString(6, tinhTrangStr);
                    ps.executeUpdate();
                    System.out.println("Đã thêm món.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } finally { }
    }

    public void sua() {
        Scanner sc = new Scanner(System.in);
        try (Connection conn = DBUtil.getConnection()) {
            Integer id;
            while (true) {
                id = promptInt(sc, "Nhập ID món cần sửa (0: hủy)", 1, Integer.MAX_VALUE, true);
                if (id == null) { System.out.println("Đã hủy sửa món."); return; }
                if (existsMon(conn, id)) break;
                System.out.println("Không tìm thấy món, vui lòng nhập lại.");
            }

            printMonById(conn, id);

            while (true) {
                System.out.println("Chọn cách sửa: 1. Sửa toàn bộ, 2. Tên, 3. Mô tả, 4. Tên đơn vị, 5. Giá, 6. Tình trạng, 7. Mã loại, 0. Hủy");
                Integer choice = promptInt(sc, "Chọn", 0, 7, false);
                if (choice == null) { System.out.println("Đã hủy sửa món."); return; }
                switch (choice) {
                    case 0:
                        System.out.println("Đã hủy sửa món.");
                        return;
                    case 1: {
                        String ten = promptString(sc, "Tên món mới (0: hủy)"); if (ten == null) return;
                        String mota = promptString(sc, "Mô tả mới (0: hủy)"); if (mota == null) return;
                        String tendv = promptString(sc, "Tên đơn vị mới (0: hủy)"); if (tendv == null) return;
                        Integer gia = promptInt(sc, "Giá mới (0: hủy)", 0, Integer.MAX_VALUE, true); if (gia == null) return;
                        String tinhtrang = promptTinhTrang(sc); if (tinhtrang == null) return;
                        Integer maLoai = promptMaLoai(sc, conn); if (maLoai == null) return;
                        try (PreparedStatement ps = conn.prepareStatement(
                                "UPDATE mon SET TenMon=?, MoTa=?, TenDonVi=?, Gia=?, TinhTrang=?, MaLoai=? WHERE MaMon=?")) {
                            ps.setString(1, ten);
                            ps.setString(2, mota);
                            ps.setString(3, tendv);
                            ps.setLong(4, gia);
                            ps.setString(5, tinhtrang);
                            ps.setInt(6, maLoai);
                            ps.setInt(7, id);
                            ps.executeUpdate();
                            System.out.println("Đã sửa món.");
                        }
                        printMonById(conn, id);
                        return;
                    }
                    case 2: {
                        String ten = promptString(sc, "Tên món mới (0: hủy)"); if (ten == null) return;
                        updateOne(conn, "TenMon", ten, id);
                        System.out.println("Đã sửa món.");
                        printMonById(conn, id);
                        return;
                    }
                    case 3: {
                        String mota = promptString(sc, "Mô tả mới (0: hủy)"); if (mota == null) return;
                        updateOne(conn, "MoTa", mota, id);
                        System.out.println("Đã sửa món.");
                        printMonById(conn, id);
                        return;
                    }
                    case 4: {
                        String tendv = promptString(sc, "Tên đơn vị mới (0: hủy)"); if (tendv == null) return;
                        updateOne(conn, "TenDonVi", tendv, id);
                        System.out.println("Đã sửa món.");
                        printMonById(conn, id);
                        return;
                    }
                    case 5: {
                        Integer gia = promptInt(sc, "Giá mới (0: hủy)", 0, Integer.MAX_VALUE, true); if (gia == null) return;
                        try (PreparedStatement ps = conn.prepareStatement("UPDATE mon SET Gia=? WHERE MaMon=?")) {
                            ps.setLong(1, gia);
                            ps.setInt(2, id);
                            ps.executeUpdate();
                        }
                        System.out.println("Đã sửa món.");
                        printMonById(conn, id);
                        return;
                    }
                    case 6: {
                        String tinhtrang = promptTinhTrang(sc); if (tinhtrang == null) return;
                        updateOne(conn, "TinhTrang", tinhtrang, id);
                        System.out.println("Đã sửa món.");
                        printMonById(conn, id);
                        return;
                    }
                    case 7: {
                        Integer maLoai = promptMaLoai(sc, conn); if (maLoai == null) return;
                        try (PreparedStatement ps = conn.prepareStatement("UPDATE mon SET MaLoai=? WHERE MaMon=?")) {
                            ps.setInt(1, maLoai);
                            ps.setInt(2, id);
                            ps.executeUpdate();
                        }
                        System.out.println("Đã sửa món.");
                        printMonById(conn, id);
                        return;
                    }
                    default:
                        System.out.println("Chức năng không hợp lệ.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
        System.out.println("┌────┬────────────────────────────┬────────────────────┬────────────┬────────────┬────────────┬────────────┐");
        System.out.println("│ ID │ Tên                        │ Mô tả              │ Tên DV     │ Giá        │ Loại       │ Tình trạng │");
        System.out.println("├────┼────────────────────────────┼────────────────────┼────────────┼────────────┼────────────┼────────────┤");
        boolean any = false;
        try (Connection conn = DBUtil.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(
                "SELECT mon.*, loaimon.TenLoai as ten_loai " +
                "FROM mon " +
                "LEFT JOIN loaimon ON mon.MaLoai = loaimon.MaLoai " +
                "ORDER BY mon.MaMon")) {
            while (rs.next()) {
                any = true;
                System.out.printf("│ %-2d │ %-25s │ %-18s │ %-10s │ %-10d │ %-10s │ %-10s │\n",
                    rs.getInt("MaMon"), rs.getString("TenMon"), rs.getString("MoTa"),
                    rs.getString("TenDonVi"), rs.getLong("Gia"), rs.getString("ten_loai"), rs.getString("TinhTrang"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (!any) System.out.println("│ Không có dữ liệu                                                                                               │");
        System.out.println("└────┴────────────────────┴────────────────────┴────────────┴────────────┴────────────┴────────────┘");
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
                sql = "SELECT mon.*, loaimon.TenLoai as ten_loai " +
                      "FROM mon " +
                      "LEFT JOIN loaimon ON mon.MaLoai = loaimon.MaLoai " +
                      "WHERE mon.MaMon = ?";
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
                sql = "SELECT mon.*, loaimon.TenLoai as ten_loai " +
                      "FROM mon " +
                      "LEFT JOIN loaimon ON mon.MaLoai = loaimon.MaLoai " +
                      "WHERE mon.TenMon LIKE ?";
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
        System.out.println("\n╔════╦════════════════════╦════════════════════╦════════════╦════════════╦═════════════╦════════════╗");
        System.out.println("║ ID ║ Tên               ║ Mô tả              ║ Tên DV     ║ Giá        ║ Loại       ║ Tình trạng ║");
        System.out.println("╠════╬════════════════════╬════════════════════╬════════════╬════════════╬════════════╬════════════╣");
        boolean any = false;
        while (rs.next()) {
            any = true;
            System.out.printf("║ %-2d ║ %-18s ║ %-18s ║ %-10s ║ %-10d ║ %-10s ║ %-10s ║\n",
                rs.getInt("MaMon"), rs.getString("TenMon"), rs.getString("MoTa"),
                rs.getString("TenDonVi"), rs.getLong("Gia"), rs.getString("ten_loai"), rs.getString("TinhTrang"));
        }
        if (!any) System.out.println("║ Không có kết quả                                                                           ║");
        System.out.println("╚════╩════════════════════╩════════════════════╩════════════╩════════════╩════════════╩════════════╝");
    }

    private static String promptString(Scanner sc, String label) {
        while (true) {
            System.out.print(label + ": ");
            String s = sc.nextLine();
            if (s == null) continue;
            s = s.trim();
            if (s.equals("0")) return null;
            if (!s.isEmpty()) return s;
            System.out.println("Vui lòng không để trống.");
        }
    }

    private static Integer promptInt(Scanner sc, String label, int min, int max, boolean allowZeroAsCancel) {
        while (true) {
            System.out.print(label + ": ");
            String s = sc.nextLine();
            if (s == null) continue;
            s = s.trim();
            if (allowZeroAsCancel && s.equals("0")) return null;
            try {
                int v = Integer.parseInt(s);
                if (v < min || v > max) {
                    System.out.println("Giá trị nằm ngoài phạm vi.");
                    continue;
                }
                return v;
            } catch (NumberFormatException e) {
                System.out.println("Vui lòng nhập số hợp lệ.");
            }
        }
    }

    private static String promptTinhTrang(Scanner sc) {
        while (true) {
            System.out.print("Tình trạng (1: bán, 2: không bán, 0: hủy): ");
            String s = sc.nextLine();
            if (s == null) continue;
            s = s.trim();
            if (s.equals("0")) return null;
            if (s.equals("1")) return "bán";
            if (s.equals("2")) return "không bán";
            System.out.println("Chỉ nhập 1 hoặc 2.");
        }
    }

    private static boolean existsLoai(Connection conn, int maLoai) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM loaimon WHERE MaLoai=?")) {
            ps.setInt(1, maLoai);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }

    private static boolean existsMon(Connection conn, int maMon) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM mon WHERE MaMon=?")) {
            ps.setInt(1, maMon);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }

    private static Integer promptMaLoai(Scanner sc, Connection conn) throws SQLException {
        while (true) {
            System.out.print("Mã loại món (0: hủy): ");
            String s = sc.nextLine();
            if (s == null) continue;
            s = s.trim();
            if (s.equals("0")) return null;
            try {
                int ma = Integer.parseInt(s);
                if (existsLoai(conn, ma)) return ma;
                System.out.println("Mã loại không tồn tại, vui lòng nhập lại.");
            } catch (NumberFormatException e) {
                System.out.println("Vui lòng nhập số hợp lệ.");
            }
        }
    }

    private static void updateOne(Connection conn, String column, String value, int id) throws SQLException {
        String sql = "UPDATE mon SET " + column + "=? WHERE MaMon=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, value);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    private void printMonById(Connection conn, int id) throws SQLException {
        String sql = "SELECT mon.*, loaimon.TenLoai as ten_loai FROM mon LEFT JOIN loaimon ON mon.MaLoai = loaimon.MaLoai WHERE mon.MaMon=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                printMonTable(rs);
            }
        }
    }
}