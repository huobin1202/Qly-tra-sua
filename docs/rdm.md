## Relational Data Model (RDM)

Tables and keys:

- NHANVIEN(MaNV PK, TaiKhoan, MatKhau, HoTen, SDT, NgayVaoLam, ChucVu, Luong)
- KHACHHANG(MaKH PK, SDT, HoTen, DiaChi, NgaySinh)
- LOAIMON(MaLoai PK, TenLoai)
- MON(MaMon PK, TenMon, Gia, MaLoai FK->LOAIMON.MaLoai, MoTa)
- DONDATHANG(MaDon PK, MaNV FK->NHANVIEN.MaNV, TrangThai, NgayDat, TongTien, GiamGia)
- CHITIETDON(MaDon FK->DONDATHANG.MaDon, MaMon FK->MON.MaMon, MaTopping FK->MON.MaMon, SoLuong, DonGia, GhiChu)
  - PK đề xuất: (MaDon, MaMon, MaTopping)
- NHACUNGCAP(MaNCC PK, TenNCC, DiaChi, SDT)
- NGUYENLIEU(MaNL PK, TenNL, DonVi, Gia)
- NCC_NGUYENLIEU(MaNCC FK->NHACUNGCAP.MaNCC, MaNL FK->NGUYENLIEU.MaNL, SoLuong)
  - PK: (MaNCC, MaNL)
- KHO_NGUYENLIEU(MaNL PK/FK->NGUYENLIEU.MaNL, SoLuong)
- PHIEUNHAP(MaPN PK, MaNV FK->NHANVIEN.MaNV, MaNCC FK->NHACUNGCAP.MaNCC, Ngay, GhiChu, ThanhTien, TrangThai)
- CHITIETNHAP_NL(MaPN FK->PHIEUNHAP.MaPN, MaNL FK->NGUYENLIEU.MaNL, SoLuong, DonGia, DonVi)
  - PK: (MaPN, MaNL)
- GIAOHANG(MaGiao PK, MaDon FK->DONDATHANG.MaDon, MaNV FK->NHANVIEN.MaNV, TrangThai, ThoiGian, DiaChi)

Notes:
- MON.MaLoai NOT NULL, tham chiếu LOAIMON.
- CHITIETDON.MaTopping có thể 0/NULL nếu không có topping.
- PHIEUNHAP.TrangThai: 'chuaxacnhan' | 'daxacnhan'.

