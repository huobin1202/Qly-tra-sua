package dto;

import java.sql.Timestamp;

public class NhanVienThuongDTO extends NhanVienDTO {

    public NhanVienThuongDTO(int maNV, String taiKhoan, String matKhau, String hoTen, String soDienThoai,
            java.sql.Timestamp ngayVaoLam, long luong, String trangThai) {
        super(maNV, taiKhoan, matKhau, hoTen, soDienThoai, ngayVaoLam, "nhanvien", luong, trangThai);
    }


    @Override
    public double tinhLuong() {
        return this.luong;
    }

    @Override
    public String[][] getMenuItems() {
        return new String[][]{
            {"Quản lý khách hàng", "👤"},
            {"Quản lý đơn hàng", "🛒"},
            {"Kho hàng", "🏬"},
            {"Thiết lập", "⚙️"}

        };
    }

    @Override
    public String[][] getHangHoaSubmenu() {
        return new String[0][0];
    }
}
