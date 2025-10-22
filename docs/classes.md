## Lớp chính và thuộc tính

### DTO
- DonHangDTO: maDon, maNV, loai, trangThai, ngayDat, tongTien, giamGia
- ChiTietDonHangDTO: maDon, maMon, maTopping, soLuong, donGia, ghiChu
- KhachHangDTO: maKH, sdt, hoTen, diaChi, ngaySinh
- NhanVienDTO: maNV, taiKhoan, matKhau, hoTen, sdt, ngayVaoLam, chucVu, luong
- KhoHangDTO: maMon, tenMon, tenDonVi, soLuong
- MonDTO: maMon, tenMon, gia, maLoai, moTa
- LoaiMonDTO: maLoai, tenLoai
- NhaCungCapDTO: maNCC, tenNCC, diaChi, sdt
- NguyenLieuDTO: maNL, tenNL, donVi, gia
- NhapHangDTO: maPN, maNV, maNCC, ngay, ghiChu, thanhTien, trangThai

### DAO (nghiệp vụ + truy cập dữ liệu)
- DonHangDAO: them(), sua(), xoa(), xuat(), xemChiTiet(), timkiem(), xacNhanThanhToan()
- ChiTietDonHangDAO: các hàm CRUD chi tiết đơn (xem file)
- GiaoHangDAO: xuat(), them(), sua(), xoa()
- KhachHangDAO: xuat(), them(), sua(), xoa(), timkiem()
- NhanVienDAO: xuat(), them(), sua(), xoa(), timkiem()
- KhoHangDAO (inner của DonHangDAO): xemTonNguyenLieu(), xemDanhSachNCC(...), xemNguyenLieuNCC(...)
- LoaiMonDAO: xuat(), them(), sua(), xoa()
- MonDAO: xuat(), them(), sua(), xoa(), timkiem()
- NhaCungCapDAO: xuat(), them(), sua(), xoa(), timkiem()
- NguyenLieuDAO: xuat(), them(), sua(), xoa()
- NhapHangDAO: taoPhieuNhap(NhapHangDTO), capNhatPhieuNhap(NhapHangDTO), xacNhanPhieuNhap(int), xoaPhieuNhap(int), ...

### View (Console)
- ConsoleUI: printHeader, printSection, printFooter, promptLabel, pause
- HangHoaView: menu(), quanLyLoaiMon(), quanLyMon(), quanLyNguyenLieu()
- KhachHangView: menu(), ...
- NhanVienView: menu(), ...
- DonHangView: menu(), ...
- NhaCungCapView: menu(), ...
- NhapHangView: menu(), ...

