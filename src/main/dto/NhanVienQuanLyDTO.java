package dto;

import java.sql.Timestamp;

public class NhanVienQuanLyDTO extends NhanVienDTO {

    public NhanVienQuanLyDTO(int maNV, String taiKhoan, String matKhau, String hoTen, String soDienThoai,
            Timestamp ngayVaoLam, double luong, String trangThai) {
        super(maNV, taiKhoan, matKhau, hoTen, soDienThoai, ngayVaoLam, "quanly", luong, trangThai);
    }

    @Override
    public double tinhLuong() {
        return this.luong + 2000000; // thưởng quản lý
    }

    @Override
    public String[][] getMenuItems() {
        return new String[][]{
            {"Quản lý nhân viên", "👥"},
            {"Quản lý nhà cung cấp", "🛒"},
            {"Quản lý khách hàng", "👤"},
            {"Quản lý phiếu nhập", "📋"},
            {"Quản lý đơn hàng", "🛒"},
            {"Quản lý món", "🍴"},
            {"Quản lý loại món", "🍽️"},
            {"Quản lý nguyên liệu", "📄"},
            {"Kho hàng", "🏬"},
            {"Thống kê", "📊"},
            {"Thiết lập", "⚙️"}
        };
    }

    /**
     * Trả về submenu cho dropdown "Quản lý hàng hóa" nếu vai trò này được quyền.
     */
    public String[][] getHangHoaSubmenu() {
        return new String[][]{
            {"Quản lý món", "🍴"},
            {"Quản lý loại món", "🍽️"},
            {"Quản lý nguyên liệu", "📄"}
        };
    }
}
