package dao;
import java.sql.*;
import java.util.Scanner;

import db.DBUtil;

public class DSMon {
    public void them() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Tên món: ");
        String ten = sc.nextLine();
        System.out.print("Mô tả: ");
        String mota = sc.nextLine();
        System.out.print("Ảnh: ");
        String anh = sc.nextLine();
        System.out.print("Tên đơn vị: ");
        String tendv = sc.nextLine();
        System.out.print("Giá: ");
        int gia = sc.nextInt();
        sc.nextLine();
        System.out.print("Đơn vị: ");
        String dv = sc.nextLine();
        System.out.print("Mã loại món (1: Đồ ăn, 2: Trà sữa, 3: Cà phê, 4: Topping): ");
        int ma_loai = sc.nextInt();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO mon (ten, mota, anh, tendv, gia, dv, ma_loai) VALUES (?, ?, ?, ?, ?, ?, ?)")) {
            ps.setString(1, ten);
            ps.setString(2, mota);
            ps.setString(3, anh);
            ps.setString(4, tendv);
            ps.setInt(5, gia);
            ps.setString(6, dv);
            ps.setInt(7, ma_loai);
            ps.executeUpdate();
            System.out.println("Đã thêm món.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void sua() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Nhập ID món cần sửa: ");
        int id = sc.nextInt();
        sc.nextLine();
        System.out.print("Tên món mới: ");
        String ten = sc.nextLine();
        System.out.print("Mô tả mới: ");
        String mota = sc.nextLine();
        System.out.print("Ảnh mới: ");
        String anh = sc.nextLine();
        System.out.print("Tên đơn vị mới: ");
        String tendv = sc.nextLine();
        System.out.print("Giá mới: ");
        int gia = sc.nextInt();
        sc.nextLine();
        System.out.print("Đơn vị mới: ");
        String dv = sc.nextLine();
        System.out.print("Mã loại món mới: ");
        int ma_loai = sc.nextInt();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                "UPDATE mon SET ten=?, mota=?, anh=?, tendv=?, gia=?, dv=?, ma_loai=? WHERE id=?")) {
            ps.setString(1, ten);
            ps.setString(2, mota);
            ps.setString(3, anh);
            ps.setString(4, tendv);
            ps.setInt(5, gia);
            ps.setString(6, dv);
            ps.setInt(7, ma_loai);
            ps.setInt(8, id);
            int rows = ps.executeUpdate();
            if (rows > 0)
                System.out.println("Đã sửa món.");
            else
                System.out.println("Không tìm thấy món.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void xoa() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Nhập ID món cần xóa: ");
        int id = sc.nextInt();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM mon WHERE id=?")) {
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            if (rows > 0)
                System.out.println("Đã xóa món.");
            else
                System.out.println("Không tìm thấy món.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void xuat() {
        try (Connection conn = DBUtil.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(
                "SELECT mon.*, loaimon.ten as ten_loai FROM mon LEFT JOIN loaimon ON mon.ma_loai = loaimon.ma")) {
            System.out.println("Danh sách món:");
            while (rs.next()) {
                System.out.printf("ID: %d | Tên: %s | Mô tả: %s | Ảnh: %s | Tên DV: %s | Giá: %d | ĐV: %s | Loại: %s\n",
                    rs.getInt("id"), rs.getString("ten"), rs.getString("mota"), rs.getString("anh"),
                    rs.getString("tendv"), rs.getInt("gia"), rs.getString("dv"), rs.getString("ten_loai"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void timkiem() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Nhập tên món cần tìm: ");
        String ten = sc.nextLine();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                "SELECT mon.*, loaimon.ten as ten_loai FROM mon LEFT JOIN loaimon ON mon.ma_loai = loaimon.ma WHERE mon.ten LIKE ?")) {
            ps.setString(1, "%" + ten + "%");
            ResultSet rs = ps.executeQuery();
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.printf("ID: %d | Tên: %s | Mô tả: %s | Ảnh: %s | Tên DV: %s | Giá: %d | ĐV: %s | Loại: %s\n",
                    rs.getInt("id"), rs.getString("ten"), rs.getString("mota"), rs.getString("anh"),
                    rs.getString("tendv"), rs.getInt("gia"), rs.getString("dv"), rs.getString("ten_loai"));
            }
            if (!found) System.out.println("Không tìm thấy món.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}