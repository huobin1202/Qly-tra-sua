package controller;

public enum TrangThaiPhieuNhap {
    CHUA_XAC_NHAN("chuaxacnhan", "Chưa xác nhận"),
    DA_XAC_NHAN("daxacnhan", "Đã xác nhận");
    
    private final String maTrangThai;
    private final String tenTrangThai;
    
    TrangThaiPhieuNhap(String maTrangThai, String tenTrangThai) {
        this.maTrangThai = maTrangThai;
        this.tenTrangThai = tenTrangThai;
    }
    
    public String getMaTrangThai() {
        return maTrangThai;
    }
    
    public String getTenTrangThai() {
        return tenTrangThai;
    }
    
    public static TrangThaiPhieuNhap fromString(String maTrangThai) {
        for (TrangThaiPhieuNhap trangThai : values()) {
            if (trangThai.maTrangThai.equals(maTrangThai)) {
                return trangThai;
            }
        }
        return CHUA_XAC_NHAN; // Mặc định
    }
    
    @Override
    public String toString() {
        return tenTrangThai;
    }
}
