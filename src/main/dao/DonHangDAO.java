package dao;
import java.sql.*;
import java.util.Scanner;

import database.DBUtil;
import view.ConsoleUI;

public class DonHangDAO {
    public void them() {
        Scanner sc = new Scanner(System.in);
        int maNV = database.Session.currentMaNV;
        System.out.println("=== TẠO ĐƠN HÀNG MỚI ===");
        
        // Tạo đơn hàng trống với trạng thái mặc định là "chưa thanh toán"
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO dondathang (MaNV, TrangThai, TongTien) VALUES (?, '2', 0)")) {
            ps.setInt(1, maNV);
            ps.executeUpdate();
            
            // Lấy mã đơn vừa tạo
            try (PreparedStatement ps2 = conn.prepareStatement("SELECT LAST_INSERT_ID()")) {
                ResultSet rs = ps2.executeQuery();
                if (rs.next()) {
                    int maDon = rs.getInt(1);
                    System.out.println("Đã tạo đơn hàng #" + maDon + " (trạng thái: chưa thanh toán)");
                    themChiTietDonHang(maDon, conn, sc);
                }
            }
            System.out.println("Thêm đơn đặt hàng thành công!");
        } catch (SQLException e) {
            System.out.println("Lỗi: " + e.getMessage());
        } finally {
            sc.close();
        }
    }
    
    private void themChiTietDonHang(int maDon, Connection conn, Scanner sc) throws SQLException {
        long tongTien = 0;
        
        while (true) {
            System.out.println("\n=== THÊM MÓN VÀO ĐƠN HÀNG ===");
            System.out.println("1. Thêm món");
            System.out.println("2. Hoàn thành đơn hàng");
            System.out.print("Chọn: ");
            String choice = sc.nextLine();
            
            if (choice.equals("2")) break;
            
            System.out.print("Nhập mã món: ");
            int maMon = sc.nextInt();
            sc.nextLine();

            // Không cho phép chọn món thuộc loại topping (MaLoai = 4) làm món chính
            try (PreparedStatement psCheckLoai = conn.prepareStatement(
                "SELECT MaLoai FROM mon WHERE MaMon = ?")) {
                psCheckLoai.setInt(1, maMon);
                try (ResultSet rs = psCheckLoai.executeQuery()) {
                    if (rs.next()) {
                        int maLoai = rs.getInt("MaLoai");
                        if (maLoai == 4) {
                            System.out.println("Món thuộc loại topping (MaLoai=4) không thể thêm như món chính.");
                            continue;
                        }
                    } else {
                        System.out.println("Không tìm thấy món với mã: " + maMon);
                        continue;
                    }
                }
            }
            
            System.out.print("Nhập mã topping (0 nếu không có): ");
            int maTopping = sc.nextInt();
            sc.nextLine();
            
            System.out.print("Nhập số lượng: ");
            int soLuong = sc.nextInt();
            sc.nextLine();
            
            System.out.print("Nhập ghi chú (có thể để trống): ");
            String ghiChu = sc.nextLine();
            
            // Lấy giá món và topping từ database
            long giaMon = 0;
            long giaTopping = 0;
            
            // Lấy giá món
            try (PreparedStatement psGiaMon = conn.prepareStatement(
                "SELECT Gia FROM mon WHERE MaMon = ?")) {
                psGiaMon.setInt(1, maMon);
                try (ResultSet rs = psGiaMon.executeQuery()) {
                    if (rs.next()) {
                        giaMon = rs.getLong("Gia");
                        System.out.println("Giá món: " + giaMon);
                    } else {
                        System.out.println("Không tìm thấy món với mã: " + maMon);
                        continue;
                    }
                }
            }
            
            // Xử lý topping: nếu maTopping = 0 thì dùng "No Topping" (MaMon = 1)
            if (maTopping == 0) {
                maTopping = 1; // "No Topping" có MaMon = 1
            }

            // Lấy giá topping và kiểm tra chỉ cho phép MaLoai = 4 (ngoại lệ cho MaMon = 1: không topping)
            if (maTopping == 1) {
                // Không topping
                try (PreparedStatement psGiaTopping = conn.prepareStatement(
                    "SELECT Gia FROM mon WHERE MaMon = 1")) {
                    try (ResultSet rs = psGiaTopping.executeQuery()) {
                        if (rs.next()) {
                            giaTopping = rs.getLong("Gia");
                            System.out.println("Không có topping (giá: " + giaTopping + ")");
                        } else {
                            // Nếu không tồn tại bản ghi MaMon=1 thì mặc định 0
                            giaTopping = 0;
                            System.out.println("Không có topping (giá: 0)");
                        }
                    }
                }
            } else {
                try (PreparedStatement psGiaTopping = conn.prepareStatement(
                    "SELECT Gia, MaLoai FROM mon WHERE MaMon = ?")) {
                    psGiaTopping.setInt(1, maTopping);
                    try (ResultSet rs = psGiaTopping.executeQuery()) {
                        if (rs.next()) {
                            int maLoai = rs.getInt("MaLoai");
                            if (maLoai != 4) {
                                System.out.println("Chỉ được chọn topping thuộc loại có mã = 4.");
                                continue;
                            }
                            giaTopping = rs.getLong("Gia");
                            System.out.println("Giá topping: " + giaTopping);
                        } else {
                            System.out.println("Không tìm thấy topping với mã: " + maTopping);
                            continue;
                        }
                    }
                }
            }
            
            // Thêm chi tiết đơn hàng
            try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO chitietdonhang (MaDon, MaMon, MaTopping, SoLuong, GiaMon, GiaTopping, GhiChu) VALUES (?, ?, ?, ?, ?, ?, ?)")) {
                ps.setInt(1, maDon);
                ps.setInt(2, maMon);
                ps.setInt(3, maTopping);
                ps.setInt(4, soLuong);
                ps.setLong(5, giaMon);
                ps.setLong(6, giaTopping);
                ps.setString(7, ghiChu);
                ps.executeUpdate();
                
                long thanhTien = (giaMon + giaTopping) * soLuong;
                tongTien += thanhTien;
                System.out.println("Đã thêm món vào đơn hàng! Thành tiền: " + thanhTien);
            }
        }
        
        // Cập nhật tổng tiền (cộng dồn với tổng tiền hiện tại)
        try (PreparedStatement ps = conn.prepareStatement(
            "UPDATE dondathang SET TongTien = TongTien + ? WHERE MaDon = ?")) {
            ps.setLong(1, tongTien);
            ps.setInt(2, maDon);
            ps.executeUpdate();
            System.out.println("Đã thêm " + tongTien + " vào tổng tiền đơn hàng!");
        }
    }

    public void sua() {
        Scanner sc = new Scanner(System.in);
        try {
            System.out.print("Nhập ID đơn hàng cần sửa: ");
            int id = sc.nextInt(); sc.nextLine();
            
            // Kiểm tra trạng thái thanh toán
            if (kiemTraTrangThai(id)) {
                System.out.println("Không thể sửa đơn hàng #" + id + " vì đã được thanh toán!");
                return;
            }
            
            System.out.println("=== SỬA ĐƠN HÀNG ===");
            System.out.println("1. Thêm sản phẩm vào đơn hàng");
            System.out.println("2. Cập nhật trạng thái đơn hàng");
            System.out.print("Chọn chức năng (1/2): ");
            String choice = sc.nextLine();
            
            if (choice.equals("1")) {
                // Thêm sản phẩm vào đơn hàng
                try (Connection conn = DBUtil.getConnection()) {
                    themChiTietDonHang(id, conn, sc);
                } catch (SQLException e) {
                    System.out.println("Lỗi: " + e.getMessage());
                }
            } else if (choice.equals("2")) {
                // Cập nhật trạng thái
                System.out.println("=== CẬP NHẬT TRẠNG THÁI ĐƠN HÀNG ===");
                System.out.println("1. Đã thanh toán");
                System.out.println("2. Chưa thanh toán");
                System.out.print("Chọn trạng thái mới (1/2): ");
                String trangThai = sc.nextLine();
                
                try (Connection conn = DBUtil.getConnection();
                     PreparedStatement ps = conn.prepareStatement(
                        "UPDATE dondathang SET TrangThai=? WHERE MaDon=?")) {
                    ps.setString(1, trangThai);
                    ps.setInt(2, id);
                    int rows = ps.executeUpdate();
                    if (rows > 0) System.out.println("Cập nhật trạng thái thành công!");
                    else System.out.println("Không tìm thấy đơn hàng.");
                } catch (SQLException e) {
                    System.out.println("Lỗi: " + e.getMessage());
                }
            } else {
                System.out.println("Lựa chọn không hợp lệ!");
            }
        } finally {
            sc.close();
        }
    }

    public void xoa() {
        Scanner sc = new Scanner(System.in);
        try {
            while (true) {
                System.out.print("Nhập ID đơn hàng cần xóa (0: hủy): ");
                String idStr = sc.nextLine();
                if (idStr == null) continue; idStr = idStr.trim();
                if (idStr.equals("0")) { System.out.println("Đã hủy."); break; }
                int id;
                try { id = Integer.parseInt(idStr); } catch (NumberFormatException e) { System.out.println("ID không hợp lệ."); continue; }

                if (kiemTraTrangThai(id)) {
                    System.out.println("Không thể xóa đơn hàng #" + id + " vì đã được thanh toán!");
                    continue;
                }

                try (Connection conn = DBUtil.getConnection();
                     PreparedStatement ps = conn.prepareStatement("DELETE FROM dondathang WHERE MaDon=?")) {
                    ps.setInt(1, id);
                    int rows = ps.executeUpdate();
                    if (rows > 0) System.out.println("Xóa đơn hàng thành công!");
                    else System.out.println("Không tìm thấy đơn hàng.");
                } catch (SQLException e) {
                    System.out.println("Lỗi: " + e.getMessage());
                }
            }
        } finally {
            sc.close();
        }
    }

    public void xuat() {
        ConsoleUI.printHeader("DANH SÁCH ĐƠN ĐẶT HÀNG");
        System.out.println("┌────┬────────────┬────────────┬────────────┬────────────┐");
        System.out.println("│ ID │ Người lập  │ Trạng thái │ Ngày lập   │ Tổng tiền  │");
        System.out.println("├────┼────────────┼────────────┼────────────┼────────────┤");
        boolean any = false;
        try (Connection conn = DBUtil.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(
                "SELECT d.MaDon, nv.HoTen as nguoilap, d.TrangThai, d.NgayDat, d.TongTien " +
                "FROM dondathang d JOIN nhanvien nv ON d.MaNV = nv.MaNV ORDER BY d.MaDon")) {
            while (rs.next()) {
                any = true;
                String trangThai = rs.getString("TrangThai").equals("1") ? "Đã thanh toán" : "Chưa thanh toán";
                System.out.printf("│ %-2d │ %-10s │ %-10s │ %-19s │ %-10d │\n",
                    rs.getInt("MaDon"),
                    rs.getString("nguoilap"),
                    trangThai,
                    rs.getString("NgayDat"),
                    rs.getLong("TongTien")
                );
            }
        } catch (SQLException e) {
            System.out.println("Lỗi: " + e.getMessage());
        }
        if (!any) System.out.println("│ Không có dữ liệu                                                   │");
        System.out.println("└────┴────────────┴────────────┴────────────┴────────────┘");
        ConsoleUI.printFooter();
    }
    
    public void xemChiTiet() {
        Scanner sc = new Scanner(System.in);
        try {
            System.out.print("Nhập ID đơn hàng cần xem chi tiết: ");
            int maDon = sc.nextInt();
            sc.nextLine();
            
            ConsoleUI.printHeader("CHI TIẾT ĐƠN HÀNG #" + maDon);
            
            // Hiển thị thông tin đơn hàng
            try (Connection conn = DBUtil.getConnection();
                 PreparedStatement ps = conn.prepareStatement(
                    "SELECT d.MaDon, nv.HoTen as nguoilap, d.TrangThai, d.NgayDat, d.TongTien " +
                    "FROM dondathang d JOIN nhanvien nv ON d.MaNV = nv.MaNV WHERE d.MaDon = ?")) {
                ps.setInt(1, maDon);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String trangThai = rs.getString("TrangThai").equals("1") ? "Đã thanh toán" : "Chưa thanh toán";
                        System.out.println("Người lập: " + rs.getString("nguoilap"));
                        System.out.println("Trạng thái: " + trangThai);
                        System.out.println("Ngày lập: " + rs.getString("NgayDat"));
                        System.out.println("Tổng tiền: " + rs.getLong("TongTien"));
                    } else {
                        System.out.println("Không tìm thấy đơn hàng với ID: " + maDon);
                        return;
                    }
                }
            } catch (SQLException e) {
                System.out.println("Lỗi: " + e.getMessage());
                return;
            }
            
            // Hiển thị chi tiết sản phẩm
            System.out.println("\n=== CHI TIẾT SẢN PHẨM ===");
            System.out.println("┌────────────┬────────────┬────────────┬────────────┬────────────┬────────────┐");
            System.out.println("│ Tên món    │ Topping    │ Số lượng   │ Giá món    │ Giá topping│ Thành tiền │");
            System.out.println("├────────────┼────────────┼────────────┼────────────┼────────────┼────────────┤");
            
            try (Connection conn = DBUtil.getConnection();
                 PreparedStatement ps = conn.prepareStatement(
                    "SELECT m1.TenMon as tenMon, m2.TenMon as tenTopping, ct.SoLuong, ct.GiaMon, ct.GiaTopping, ct.GhiChu " +
                    "FROM chitietdonhang ct " +
                    "JOIN mon m1 ON ct.MaMon = m1.MaMon " +
                    "JOIN mon m2 ON ct.MaTopping = m2.MaMon " +
                    "WHERE ct.MaDon = ?")) {
                ps.setInt(1, maDon);
                try (ResultSet rs = ps.executeQuery()) {
                    boolean any = false;
                    while (rs.next()) {
                        any = true;
                        long thanhTien = (rs.getLong("GiaMon") + rs.getLong("GiaTopping")) * rs.getInt("SoLuong");
                        System.out.printf("│ %-10s │ %-10s │ %-10d │ %-10d │ %-10d │ %-10d │\n",
                            rs.getString("tenMon"),
                            rs.getString("tenTopping"),
                            rs.getInt("SoLuong"),
                            rs.getLong("GiaMon"),
                            rs.getLong("GiaTopping"),
                            thanhTien
                        );
                        if (rs.getString("GhiChu") != null && !rs.getString("GhiChu").trim().isEmpty()) {
                            String ghiChu = rs.getString("GhiChu");
                            String spaces = "                                                          ";
                            System.out.println("│ Ghi chú: " + ghiChu + spaces.substring(0, Math.max(0, 50 - ghiChu.length())) + "│");
                        }
                    }
                    if (!any) {
                        System.out.println("│ Không có sản phẩm nào trong đơn hàng này                    │");
                    }
                }
            } catch (SQLException e) {
                System.out.println("Lỗi: " + e.getMessage());
            }
            System.out.println("└────────────┴────────────┴────────────┴────────────┴────────────┴────────────┘");
            ConsoleUI.printFooter();
        } finally {
            sc.close();
        }
    }

    public void timkiem() {
        Scanner sc = new Scanner(System.in);
        try {
            System.out.println("Tìm theo: 1.ID  2.Người lập/Trạng thái");
            System.out.print("Chọn: ");
            String chStr = sc.nextLine();
            int ch; try { ch = Integer.parseInt(chStr.trim()); } catch (NumberFormatException e) { System.out.println("Vui lòng nhập số hợp lệ."); return; }
            String sql;
            System.out.println("\n╔════╦════════════╦════════════╦════════════╦════════════╗");
            System.out.println("║ ID ║ Người lập  ║ Trạng thái ║ Ngày lập   ║ Tổng tiền ║");
            System.out.println("╠════╬════════════╬════════════╬════════════╬════════════╣");
            try (Connection conn = DBUtil.getConnection()) {
                if (ch == 1) {
                    System.out.print("Nhập ID: ");
                    String idStr = sc.nextLine();
                    int id; try { id = Integer.parseInt(idStr.trim()); } catch (NumberFormatException e) { System.out.println("ID không hợp lệ."); return; }
                    sql = "SELECT d.MaDon, nv.HoTen as nguoilap, d.TrangThai, d.NgayDat, d.TongTien FROM dondathang d JOIN nhanvien nv ON d.MaNV = nv.MaNV WHERE d.MaDon = ?";
                    try (PreparedStatement ps = conn.prepareStatement(sql)) {
                        ps.setInt(1, id);
                        try (ResultSet rs = ps.executeQuery()) { printDDH(rs); }
                    }
                } else {
                    System.out.print("Nhập từ khóa: ");
                    String key = sc.nextLine();
                    sql = "SELECT d.MaDon, nv.HoTen as nguoilap, d.TrangThai, d.NgayDat,  d.TongTien FROM dondathang d JOIN nhanvien nv ON d.MaNV = nv.MaNV WHERE nv.HoTen LIKE ? OR d.TrangThai LIKE ?";
                    try (PreparedStatement ps = conn.prepareStatement(sql)) {
                        ps.setString(1, "%" + key + "%");
                        ps.setString(2, "%" + key + "%");
                        try (ResultSet rs = ps.executeQuery()) { printDDH(rs); }
                    }
                }
            } catch (SQLException e) {
                System.out.println("Lỗi: " + e.getMessage());
            }
        } finally {
            sc.close();
        }
        System.out.println("╚════╩════════════╩════════════╩════════════╩════════════╩════════════╩════════════╝");
    }

    private void printDDH(ResultSet rs) throws SQLException {
        boolean any = false;
        while (rs.next()) {
            any = true;
            String trangThai = rs.getString("TrangThai").equals("1") ? "Đã thanh toán" : "Chưa thanh toán";
            System.out.printf("║ %-2d ║ %-10s ║ %-10s ║ %-19s ║ %-10d ║\n",
                rs.getInt("MaDon"),
                rs.getString("nguoilap"),
                trangThai,
                rs.getString("NgayDat"),
                rs.getLong("TongTien")
            );
        }
        if (!any) System.out.println("║ Không có kết quả                                                             ║");
    }
    
    // Kiểm tra trạng thái thanh toán của đơn hàng
    private boolean kiemTraTrangThai(int maDon) {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                "SELECT TrangThai FROM dondathang WHERE MaDon = ?")) {
            ps.setInt(1, maDon);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("TrangThai").equals("1"); // 1 = đã thanh toán
                }
            }
        } catch (SQLException e) {
            System.out.println("Lỗi kiểm tra trạng thái: " + e.getMessage());
        }
        return false;
    }
    
    // Xác nhận đã thanh toán
    public void xacNhanThanhToan() {
        Scanner sc = new Scanner(System.in);
        try {
            System.out.print("Nhập ID đơn hàng cần xác nhận thanh toán: ");
            int id = sc.nextInt(); sc.nextLine();
            
            // Kiểm tra đơn hàng có tồn tại không
            if (!kiemTraTrangThai(id)) {
                try (Connection conn = DBUtil.getConnection();
                     PreparedStatement ps = conn.prepareStatement(
                        "UPDATE dondathang SET TrangThai = '1' WHERE MaDon = ?")) {
                    ps.setInt(1, id);
                    int rows = ps.executeUpdate();
                    if (rows > 0) {
                        System.out.println("Đã xác nhận thanh toán cho đơn hàng #" + id);
                    } else {
                        System.out.println("Không tìm thấy đơn hàng với ID: " + id);
                    }
                } catch (SQLException e) {
                    System.out.println("Lỗi: " + e.getMessage());
                }
            } else {
                System.out.println("Đơn hàng #" + id + " đã được thanh toán rồi!");
            }
        } finally {
            sc.close();
        }
    }
}