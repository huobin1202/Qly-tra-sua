package dao;

import java.sql.*;
import database.DBUtil;

public class MonDAO {
    
    // Method để chuẩn hóa trạng thái món trong database
    public boolean chuanHoaTrangThaiMon() {
        String sql1 = "UPDATE mon SET TinhTrang = 'dangban' WHERE TinhTrang = 'Đang bán'";
        String sql2 = "UPDATE mon SET TinhTrang = 'ngungban' WHERE TinhTrang = 'Ngừng bán'";
        
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            
            try (PreparedStatement ps = conn.prepareStatement(sql1)) {
                int result1 = ps.executeUpdate();
            }
            
            try (PreparedStatement ps = conn.prepareStatement(sql2)) {
                int result2 = ps.executeUpdate();
            }
            
            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
