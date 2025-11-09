# Relational Data Model (RDM) - Hệ thống quản lý trà sữa

## Tổng quan
Mô hình dữ liệu quan hệ cho hệ thống quản lý cửa hàng trà sữa bao gồm các bảng chính và mối quan hệ giữa chúng.

## Các bảng và khóa

### 1. Bảng NHANVIEN (Nhân viên)
```sql
NHANVIEN(
    MaNV INT PRIMARY KEY AUTO_INCREMENT,
    TaiKhoan VARCHAR(50) NOT NULL UNIQUE,
    MatKhau VARCHAR(255) NOT NULL,
    HoTen VARCHAR(100) NOT NULL,
    SDT VARCHAR(15) NOT NULL,
    NgayVaoLam DATE NOT NULL,
    ChucVu VARCHAR(50) NOT NULL,
    Luong INT NOT NULL DEFAULT 0
)
```
- **Khóa chính**: MaNV
- **Khóa duy nhất**: TaiKhoan
- **Ràng buộc**: ChucVu ∈ {'Admin', 'NhanVienBanHang', 'NhanVienKho', 'QuanLy'}

### 2. Bảng KHACHHANG (Khách hàng)
```sql
KHACHHANG(
    MaKH INT PRIMARY KEY AUTO_INCREMENT,
    SDT VARCHAR(15) NOT NULL UNIQUE,
    HoTen VARCHAR(100) NOT NULL,
    DiaChi TEXT,
    NgaySinh DATE
)
```
- **Khóa chính**: MaKH
- **Khóa duy nhất**: SDT

### 3. Bảng LOAIMON (Loại món)
```sql
LOAIMON(
    MaLoai INT PRIMARY KEY AUTO_INCREMENT,
    TenLoai VARCHAR(100) NOT NULL UNIQUE
)
```
- **Khóa chính**: MaLoai
- **Khóa duy nhất**: TenLoai

### 4. Bảng MON (Món)
```sql
MON(
    MaMon INT PRIMARY KEY AUTO_INCREMENT,
    TenMon VARCHAR(100) NOT NULL,
    Gia BIGINT NOT NULL DEFAULT 0,
    MaLoai INT NOT NULL,
    MoTa TEXT,
    TrangThai VARCHAR(20) DEFAULT 'conhang',
    FOREIGN KEY (MaLoai) REFERENCES LOAIMON(MaLoai) ON DELETE RESTRICT
)
```
- **Khóa chính**: MaMon
- **Khóa ngoại**: MaLoai → LOAIMON.MaLoai
- **Ràng buộc**: TrangThai ∈ {'conhang', 'hethang', 'ngungban'}

### 5. Bảng DONDATHANG (Đơn đặt hàng)
```sql
DONDATHANG(
    MaDon INT PRIMARY KEY AUTO_INCREMENT,
    MaNV INT NOT NULL,
    Loai VARCHAR(20) NOT NULL DEFAULT 'taicho',
    TrangThai VARCHAR(20) NOT NULL DEFAULT 'chuathanhtoan',
    NgayDat DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    TongTien BIGINT NOT NULL DEFAULT 0,
    GiamGia INT NOT NULL DEFAULT 0,
    FOREIGN KEY (MaNV) REFERENCES NHANVIEN(MaNV) ON DELETE RESTRICT
)
```
- **Khóa chính**: MaDon
- **Khóa ngoại**: MaNV → NHANVIEN.MaNV
- **Ràng buộc**: 
  - Loai ∈ {'taicho', 'giaohang'}
  - TrangThai ∈ {'chuathanhtoan', 'dathanhtoan', 'dahuy'}

### 6. Bảng CHITIETDON (Chi tiết đơn hàng)
```sql
CHITIETDON(
    MaDon INT NOT NULL,
    MaMon INT NOT NULL,
    MaTopping INT NULL,
    SoLuong INT NOT NULL DEFAULT 1,
    DonGia BIGINT NOT NULL,
    GhiChu TEXT,
    PRIMARY KEY (MaDon, MaMon, MaTopping),
    FOREIGN KEY (MaDon) REFERENCES DONDATHANG(MaDon) ON DELETE CASCADE,
    FOREIGN KEY (MaMon) REFERENCES MON(MaMon) ON DELETE RESTRICT,
    FOREIGN KEY (MaTopping) REFERENCES MON(MaMon) ON DELETE SET NULL
)
```
- **Khóa chính**: (MaDon, MaMon, MaTopping)
- **Khóa ngoại**: 
  - MaDon → DONDATHANG.MaDon
  - MaMon → MON.MaMon
  - MaTopping → MON.MaMon (có thể NULL)

### 7. Bảng NHACUNGCAP (Nhà cung cấp)
```sql
NHACUNGCAP(
    MaNCC INT PRIMARY KEY AUTO_INCREMENT,
    TenNCC VARCHAR(100) NOT NULL,
    DiaChi TEXT,
    SDT VARCHAR(15) NOT NULL
)
```
- **Khóa chính**: MaNCC

### 8. Bảng NGUYENLIEU (Nguyên liệu)
```sql
NGUYENLIEU(
    MaNL INT PRIMARY KEY AUTO_INCREMENT,
    TenNL VARCHAR(100) NOT NULL,
    DonVi VARCHAR(20) NOT NULL,
    Gia BIGINT NOT NULL DEFAULT 0
)
```
- **Khóa chính**: MaNL

### 9. Bảng NCC_NGUYENLIEU (Nhà cung cấp - Nguyên liệu)
```sql
NCC_NGUYENLIEU(
    MaNCC INT NOT NULL,
    MaNL INT NOT NULL,
    SoLuong INT NOT NULL DEFAULT 0,
    PRIMARY KEY (MaNCC, MaNL),
    FOREIGN KEY (MaNCC) REFERENCES NHACUNGCAP(MaNCC) ON DELETE CASCADE,
    FOREIGN KEY (MaNL) REFERENCES NGUYENLIEU(MaNL) ON DELETE CASCADE
)
```
- **Khóa chính**: (MaNCC, MaNL)
- **Khóa ngoại**: 
  - MaNCC → NHACUNGCAP.MaNCC
  - MaNL → NGUYENLIEU.MaNL

### 10. Bảng KHO_NGUYENLIEU (Kho nguyên liệu)
```sql
KHO_NGUYENLIEU(
    MaNL INT PRIMARY KEY,
    SoLuong INT NOT NULL DEFAULT 0,
    FOREIGN KEY (MaNL) REFERENCES NGUYENLIEU(MaNL) ON DELETE CASCADE
)
```
- **Khóa chính**: MaNL
- **Khóa ngoại**: MaNL → NGUYENLIEU.MaNL

### 11. Bảng PHIEUNHAP (Phiếu nhập)
```sql
PHIEUNHAP(
    MaPN INT PRIMARY KEY AUTO_INCREMENT,
    MaNV INT NOT NULL,
    MaNCC INT NOT NULL,
    Ngay DATE NOT NULL DEFAULT (CURRENT_DATE),
    GhiChu TEXT,
    ThanhTien BIGINT NOT NULL DEFAULT 0,
    TrangThai VARCHAR(20) NOT NULL DEFAULT 'chuaxacnhan',
    FOREIGN KEY (MaNV) REFERENCES NHANVIEN(MaNV) ON DELETE RESTRICT,
    FOREIGN KEY (MaNCC) REFERENCES NHACUNGCAP(MaNCC) ON DELETE RESTRICT
)
```
- **Khóa chính**: MaPN
- **Khóa ngoại**: 
  - MaNV → NHANVIEN.MaNV
  - MaNCC → NHACUNGCAP.MaNCC
- **Ràng buộc**: TrangThai ∈ {'chuaxacnhan', 'daxacnhan'}

### 12. Bảng CHITIETNHAP_NL (Chi tiết nhập nguyên liệu)
```sql
CHITIETNHAP_NL(
    MaPN INT NOT NULL,
    MaNL INT NOT NULL,
    SoLuong INT NOT NULL DEFAULT 1,
    DonGia BIGINT NOT NULL,
    DonVi VARCHAR(20) NOT NULL,
    PRIMARY KEY (MaPN, MaNL),
    FOREIGN KEY (MaPN) REFERENCES PHIEUNHAP(MaPN) ON DELETE CASCADE,
    FOREIGN KEY (MaNL) REFERENCES NGUYENLIEU(MaNL) ON DELETE RESTRICT
)
```
- **Khóa chính**: (MaPN, MaNL)
- **Khóa ngoại**: 
  - MaPN → PHIEUNHAP.MaPN
  - MaNL → NGUYENLIEU.MaNL

### 13. Bảng GIAOHANG (Giao hàng)
```sql
GIAOHANG(
    MaGiao INT PRIMARY KEY AUTO_INCREMENT,
    MaDon INT NOT NULL,
    MaNV INT NOT NULL,
    TrangThai VARCHAR(20) NOT NULL DEFAULT 'chogiao',
    ThoiGian DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    DiaChi TEXT NOT NULL,
    FOREIGN KEY (MaDon) REFERENCES DONDATHANG(MaDon) ON DELETE CASCADE,
    FOREIGN KEY (MaNV) REFERENCES NHANVIEN(MaNV) ON DELETE RESTRICT
)
```
- **Khóa chính**: MaGiao
- **Khóa ngoại**: 
  - MaDon → DONDATHANG.MaDon
  - MaNV → NHANVIEN.MaNV
- **Ràng buộc**: TrangThai ∈ {'chogiao', 'danggiao', 'dagiao', 'huy'}

## Mối quan hệ giữa các bảng

### Quan hệ 1-N (Một-Nhiều)
1. **NHANVIEN** → **DONDATHANG**: Một nhân viên có thể tạo nhiều đơn hàng
2. **NHANVIEN** → **PHIEUNHAP**: Một nhân viên có thể tạo nhiều phiếu nhập
3. **NHANVIEN** → **GIAOHANG**: Một nhân viên có thể giao nhiều đơn hàng
4. **LOAIMON** → **MON**: Một loại món có nhiều món
5. **DONDATHANG** → **CHITIETDON**: Một đơn hàng có nhiều chi tiết
6. **MON** → **CHITIETDON**: Một món có thể xuất hiện trong nhiều chi tiết đơn
7. **NHACUNGCAP** → **PHIEUNHAP**: Một nhà cung cấp có nhiều phiếu nhập
8. **PHIEUNHAP** → **CHITIETNHAP_NL**: Một phiếu nhập có nhiều chi tiết
9. **NGUYENLIEU** → **CHITIETNHAP_NL**: Một nguyên liệu có thể xuất hiện trong nhiều chi tiết nhập
10. **DONDATHANG** → **GIAOHANG**: Một đơn hàng có thể có nhiều lần giao hàng

### Quan hệ N-N (Nhiều-Nhiều)
1. **NHACUNGCAP** ↔ **NGUYENLIEU**: Quan hệ nhiều-nhiều qua bảng **NCC_NGUYENLIEU**

### Quan hệ 1-1 (Một-Một)
1. **NGUYENLIEU** ↔ **KHO_NGUYENLIEU**: Một nguyên liệu có một bản ghi tồn kho

## Ràng buộc toàn vẹn

### Ràng buộc miền giá trị
- **NHANVIEN.ChucVu**: {'Admin', 'NhanVienBanHang', 'NhanVienKho', 'QuanLy'}
- **MON.TrangThai**: {'conhang', 'hethang', 'ngungban'}
- **DONDATHANG.Loai**: {'taicho', 'giaohang'}
- **DONDATHANG.TrangThai**: {'chuathanhtoan', 'dathanhtoan', 'dahuy'}
- **PHIEUNHAP.TrangThai**: {'chuaxacnhan', 'daxacnhan'}
- **GIAOHANG.TrangThai**: {'chogiao', 'danggiao', 'dagiao', 'huy'}

### Ràng buộc NOT NULL
- Tất cả các khóa chính và khóa ngoại không được NULL
- Các trường thông tin cơ bản như tên, giá không được NULL

### Ràng buộc UNIQUE
- **NHANVIEN.TaiKhoan**: Tài khoản phải duy nhất
- **KHACHHANG.SDT**: Số điện thoại khách hàng phải duy nhất
- **LOAIMON.TenLoai**: Tên loại món phải duy nhất

## Ghi chú đặc biệt

1. **CHITIETDON.MaTopping** có thể NULL nếu không có topping
2. **PHIEUNHAP.TrangThai** quản lý trạng thái xác nhận phiếu nhập
3. **KHO_NGUYENLIEU** tự động cập nhật khi có phiếu nhập được xác nhận
4. **NCC_NGUYENLIEU** quản lý số lượng nguyên liệu mà nhà cung cấp có thể cung cấp
5. Tất cả các bảng đều có khóa chính tự tăng (AUTO_INCREMENT) để đảm bảo tính duy nhất