package dao;
import java.sql.*;
import datebase.DBUtil;
import view.ConsoleUI;

public class KhoHang {
    public void xemTon() {
        ConsoleUI.printHeader("TỒN KHO");
        System.out.println("┌────┬────────────────────────────┬──────────┬──────────┐");
        System.out.println("│ ID │ Tên món                    │ Đơn vị   │ Tồn      │");
        System.out.println("├────┼────────────────────────────┼──────────┼──────────┤");
        boolean any = false;
        String sql = "SELECT m.MaMon, m.TenMon, m.TenDonVi, IFNULL(k.SoLuong,0) SoLuong " +
                     "FROM mon m LEFT JOIN khohang k ON m.MaMon = k.MaMon ORDER BY m.MaMon";
        try (Connection conn = DBUtil.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                any = true;
                System.out.printf("│ %-2d │ %-26s │ %-8s │ %-8d │\n",
                    rs.getInt("MaMon"), rs.getString("TenMon"), rs.getString("TenDonVi"),
                    rs.getInt("SoLuong"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        if (!any) System.out.println("│ Không có dữ liệu                                                     │");
        System.out.println("└────┴────────────────────────────┴──────────┴──────────┘");
        ConsoleUI.printFooter();
    }
}
