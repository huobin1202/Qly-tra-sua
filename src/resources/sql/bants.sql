-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Oct 09, 2025 at 06:23 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `bants`
--

-- --------------------------------------------------------

--
-- Table structure for table `khachhang`
--

CREATE TABLE `khachhang` (
  `MaKH` int(11) NOT NULL,
  `SDT` varchar(20) NOT NULL,
  `HoTen` varchar(50) DEFAULT NULL,
  `DiaChi` varchar(250) DEFAULT NULL,
  `NgaySinh` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `khachhang`
--

INSERT INTO `khachhang` (`MaKH`, `SDT`, `HoTen`, `DiaChi`, `NgaySinh`) VALUES
(1, '0911175581', 'Cường', 'Nghệ An', '2000-04-09 10:00:00'),
(2, '0911175581', 'Linh', 'Hà Nội', '2000-09-16 10:00:00');

-- --------------------------------------------------------

--
-- Table structure for table `nhanvien`
--

CREATE TABLE `nhanvien` (
  `MaNV` int(11) NOT NULL,
  `TaiKhoan` varchar(50) NOT NULL,
  `MatKhau` varchar(50) NOT NULL,
  `HoTen` varchar(50) NOT NULL,
  `SDT` varchar(20) DEFAULT NULL,
  `NgayVaoLam` timestamp NOT NULL DEFAULT current_timestamp(),
  `ChucVu` varchar(50) NOT NULL COMMENT 'quanly-quản lý\r\nnhanvien-nhân viên\r\nnghiviec-nghỉ việc',
  `Luong` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `nhanvien`
--

INSERT INTO `nhanvien` (`MaNV`, `TaiKhoan`, `MatKhau`, `HoTen`, `SDT`, `NgayVaoLam`, `ChucVu`, `Luong`) VALUES
(1, 'admin', 'admin', 'Admin', '0911175581', '2020-11-23 17:00:00', 'quanly', 10600000),
(2, 'nhanvien', '3', 'Tea', '0911175581', '2020-11-24 05:15:08', 'nhanvien', 0),
(5, 'cuong', 'cuong12345', 'Trần Đức Cường', '0911175582', '2020-12-16 14:11:54', 'quanly', 0),
(6, 'anh', 'anh12345', 'Đỗ Tuấn Anh', '12324123', '2020-12-16 14:21:20', 'quanly', 0),
(7, 'son', 'son12345', 'Nguyễn Lam Sơn', '123', '2020-12-16 14:22:12', 'quanly', 0),
(27, 'karma', '1', 'Milk', '1', '2020-12-23 16:52:39', 'nhanvien', 0);

-- --------------------------------------------------------

--
-- Table structure for table `loaimon`
--

CREATE TABLE `loaimon` (
  `MaLoai` int(11) NOT NULL,
  `TenLoai` varchar(50) NOT NULL,
  `Slug` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `loaimon`
--

INSERT INTO `loaimon` (`MaLoai`, `TenLoai`, `Slug`) VALUES
(1, 'Đồ Ăn', 'do-an'),
(2, 'Trà Sữa', 'tra-sua'),
(3, 'Cà Phê', 'ca-phe'),
(4, 'Topping', 'topping');

-- --------------------------------------------------------

--
-- Table structure for table `mon`
--

CREATE TABLE `mon` (
  `MaMon` int(11) NOT NULL,
  `TenMon` varchar(50) NOT NULL,
  `MoTa` varchar(500) DEFAULT NULL,
  `TenDonVi` varchar(20) NOT NULL,
  `Gia` bigint(20) NOT NULL,
  `TinhTrang` varchar(20) NOT NULL DEFAULT 'ban',
  `MaLoai` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `mon`
--

INSERT INTO `mon` (`MaMon`, `TenMon`, `MoTa`, `TenDonVi`, `Gia`, `TinhTrang`, `MaLoai`) VALUES
(1, 'No Topping', '', 'Phần', 0, 'ban', 4),
(2, 'Trân Châu Tuyết Sợi', NULL, 'Phần', 10000, 'ban', 4),
(3, 'Trân Châu Đen', NULL, 'Phần', 10000, 'ban', 4),
(4, 'Trân Châu Trắng', NULL, 'Phần', 10000, 'ban', 4),
(5, 'Trà Sữa Trân Châu', '', 'Ly', 50000, 'ban', 2),
(6, 'Trà Sữa Sương Sáo', NULL, 'Ly', 45000, 'ban', 2),
(7, 'Trà Sữa Matcha(L)', '', 'Ly', 50000, 'ban', 2),
(8, 'Sữa Tươi Trân Châu Đường Đen', NULL, 'Ly', 45000, 'ban', 2),
(9, 'Bánh Flan', '', 'Cái', 10000, 'ban', 2),
(10, 'Hướng dương', NULL, 'Túi', 10000, 'ban', 1),
(11, 'Cafe truyền thống', NULL, 'Cốc', 35000, 'ban', 3),
(12, 'Espresso', NULL, 'Cốc', 45000, 'ban', 3),
(13, 'Trà Sữa Matcha(XL)', NULL, 'Ly', 25000, 'ban', 2),
(14, 'Trà Sữa Ô Long', '', 'Ly', 20000, 'ban', 2),
(15, 'Trà Đào', '', 'Ly', 40000, 'ban', 2),
(16, 'Trà Đào(L)', '', 'Ly', 50000, 'ban', 2),
(18, 'Trà Nhãn - Sen', '', 'Ly', 45000, 'ban', 2);

-- --------------------------------------------------------

--
-- Table structure for table `dondathang`
--

CREATE TABLE `dondathang` (
  `MaDon` int(11) NOT NULL,
  `MaNV` int(11) NOT NULL,
  `Loai` varchar(45) NOT NULL DEFAULT 'taiquan' COMMENT 'taiquan - tại quán\nonline - đặt online',
  `TrangThai` varchar(45) NOT NULL DEFAULT 'chuathanhtoan' COMMENT 'chuathanhtoan - chưa thanh toán\ndathanhtoan - đã thanh toán',
  `NgayDat` timestamp NOT NULL DEFAULT current_timestamp(),
  `NgayThanhToan` timestamp NULL DEFAULT NULL,
  `SoTienDaTra` bigint(20) DEFAULT 0,
  `TongTien` bigint(20) NOT NULL DEFAULT 0,
  `GiamGia` int(11) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `dondathang`
--

INSERT INTO `dondathang` (`MaDon`, `MaNV`, `Loai`, `TrangThai`, `NgayDat`, `NgayThanhToan`, `SoTienDaTra`, `TongTien`, `GiamGia`) VALUES
(1, 1, 'taiquan', 'dathanhtoan', '2020-11-24 07:28:41', '2020-12-01 10:40:46', 450000, 450000, 0),
(2, 1, 'online', 'dathanhtoan', '2020-11-24 08:05:08', '2020-12-23 06:33:51', 406000, 406000, 0),
(3, 1, 'taiquan', 'dathanhtoan', '2020-12-16 08:55:36', '2020-12-23 06:34:01', 550000, 550000, 5);

-- --------------------------------------------------------

--
-- Table structure for table `chitietdonhang`
--

CREATE TABLE `chitietdonhang` (
  `MaDon` int(11) NOT NULL,
  `MaMon` int(11) NOT NULL,
  `MaTopping` int(11) NOT NULL DEFAULT 0,
  `SoLuong` int(11) NOT NULL DEFAULT 1,
  `GiaMon` bigint(20) NOT NULL DEFAULT 0,
  `GiaTopping` bigint(20) NOT NULL DEFAULT 0,
  `GhiChu` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `chitietdonhang`
--

INSERT INTO `chitietdonhang` (`MaDon`, `MaMon`, `MaTopping`, `SoLuong`, `GiaMon`, `GiaTopping`, `GhiChu`) VALUES
(1, 7, 1, 2, 50000, 0, NULL),
(1, 16, 1, 2, 50000, 0, ''),
(2, 8, 1, 2, 43000, 0, ''),
(2, 9, 1, 8, 10000, 0, NULL),
(2, 15, 1, 6, 40000, 0, ''),
(3, 5, 4, 5, 40000, 10000, '');

-- --------------------------------------------------------

--
-- Indexes for dumped tables
--

--
-- Indexes for table `khachhang`
--
ALTER TABLE `khachhang`
  ADD PRIMARY KEY (`MaKH`);

--
-- Indexes for table `nhanvien`
--
ALTER TABLE `nhanvien`
  ADD PRIMARY KEY (`MaNV`),
  ADD UNIQUE KEY `TaiKhoan` (`TaiKhoan`);

--
-- Indexes for table `loaimon`
--
ALTER TABLE `loaimon`
  ADD PRIMARY KEY (`MaLoai`),
  ADD UNIQUE KEY `TenLoai` (`TenLoai`);

--
-- Indexes for table `mon`
--
ALTER TABLE `mon`
  ADD PRIMARY KEY (`MaMon`),
  ADD UNIQUE KEY `TenMon` (`TenMon`),
  ADD KEY `fk_mon_loai` (`MaLoai`);

--
-- Indexes for table `dondathang`
--
ALTER TABLE `dondathang`
  ADD PRIMARY KEY (`MaDon`),
  ADD KEY `fk_nhanvien_don` (`MaNV`);

--
-- Indexes for table `chitietdonhang`
--
ALTER TABLE `chitietdonhang`
  ADD PRIMARY KEY (`MaDon`,`MaMon`,`MaTopping`),
  ADD KEY `fk_don_mon` (`MaMon`),
  ADD KEY `fk_don_topping` (`MaTopping`);



--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `khachhang`
--
ALTER TABLE `khachhang`
  MODIFY `MaKH` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `nhanvien`
--
ALTER TABLE `nhanvien`
  MODIFY `MaNV` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT for table `loaimon`
--
ALTER TABLE `loaimon`
  MODIFY `MaLoai` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `mon`
--
ALTER TABLE `mon`
  MODIFY `MaMon` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=19;

--
-- AUTO_INCREMENT for table `dondathang`
--
ALTER TABLE `dondathang`
  MODIFY `MaDon` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;


--
-- AUTO_INCREMENT for table `ban`
--
-- removed auto_increment for table `ban`

--
-- Constraints for dumped tables
--

--
-- Constraints for table `mon`
--
ALTER TABLE `mon`
  ADD CONSTRAINT `fk_mon_loai` FOREIGN KEY (`MaLoai`) REFERENCES `loaimon` (`MaLoai`);

--
-- Constraints for table `dondathang`
--
ALTER TABLE `dondathang`
  ADD CONSTRAINT `fk_nhanvien_don` FOREIGN KEY (`MaNV`) REFERENCES `nhanvien` (`MaNV`);

--
-- Constraints for table `chitietdonhang`
--
ALTER TABLE `chitietdonhang`
  ADD CONSTRAINT `fk_don_chitiet` FOREIGN KEY (`MaDon`) REFERENCES `dondathang` (`MaDon`),
  ADD CONSTRAINT `fk_don_mon` FOREIGN KEY (`MaMon`) REFERENCES `mon` (`MaMon`),
  ADD CONSTRAINT `fk_don_topping` FOREIGN KEY (`MaTopping`) REFERENCES `mon` (`MaMon`);


COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
-- --------------------------------------------------------
-- Inventory and Stock Flow Tables
-- --------------------------------------------------------


-- Table structure for table `phieunhap`
CREATE TABLE IF NOT EXISTS `phieunhap` (
  `MaPN` int(11) NOT NULL,
  `MaNV` int(11) NOT NULL,
  `MaNCC` int(11) NOT NULL,
  `Ngay` timestamp NOT NULL DEFAULT current_timestamp(),
  `GhiChu` varchar(250) DEFAULT NULL,
  `ThanhTien` bigint(20) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Supplier products catalog and on-hand stock at supplier
CREATE TABLE IF NOT EXISTS `nhacungcap` (
  `MaNCC` int(11) NOT NULL AUTO_INCREMENT,
  `TenNCC` varchar(100) NOT NULL,
  `SDT` varchar(20) DEFAULT NULL,
  `DiaChi` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`MaNCC`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS `ncc_sanpham` (
  `MaNCC` int(11) NOT NULL,
  `MaMon` int(11) NOT NULL,
  `SoLuong` int(11) NOT NULL DEFAULT 0,
  `DonGia` bigint(20) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Table structure for table `chitietnhap`
CREATE TABLE IF NOT EXISTS `chitietnhap` (
  `MaPN` int(11) NOT NULL,
  `MaMon` int(11) NOT NULL,
  `SoLuong` int(11) NOT NULL,
  `DonGia` bigint(20) NOT NULL DEFAULT 0,
  `DonVi` varchar(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- Indexes

ALTER TABLE `phieunhap`
  ADD PRIMARY KEY (`MaPN`),
  ADD KEY `fk_pn_nv` (`MaNV`),
  ADD KEY `fk_pn_ncc` (`MaNCC`);

ALTER TABLE `chitietnhap`
  ADD PRIMARY KEY (`MaPN`,`MaMon`),
  ADD KEY `fk_ctn_mon` (`MaMon`);


-- primary key declared inline for `nhacungcap`

ALTER TABLE `ncc_sanpham`
  ADD PRIMARY KEY (`MaNCC`,`MaMon`),
  ADD KEY `fk_nccsp_mon` (`MaMon`);

-- AUTO_INCREMENT
ALTER TABLE `phieunhap`
  MODIFY `MaPN` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=1;


-- Foreign keys

ALTER TABLE `phieunhap`
  ADD CONSTRAINT `fk_pn_nv` FOREIGN KEY (`MaNV`) REFERENCES `nhanvien` (`MaNV`) ON DELETE RESTRICT ON UPDATE CASCADE;

ALTER TABLE `phieunhap`
  ADD CONSTRAINT `fk_pn_ncc` FOREIGN KEY (`MaNCC`) REFERENCES `nhacungcap` (`MaNCC`) ON DELETE RESTRICT ON UPDATE CASCADE;

ALTER TABLE `chitietnhap`
  ADD CONSTRAINT `fk_ctn_pn` FOREIGN KEY (`MaPN`) REFERENCES `phieunhap` (`MaPN`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_ctn_mon` FOREIGN KEY (`MaMon`) REFERENCES `mon` (`MaMon`) ON DELETE RESTRICT ON UPDATE CASCADE;



ALTER TABLE `ncc_sanpham`
  ADD CONSTRAINT `fk_nccsp_ncc` FOREIGN KEY (`MaNCC`) REFERENCES `nhacungcap` (`MaNCC`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_nccsp_mon` FOREIGN KEY (`MaMon`) REFERENCES `mon` (`MaMon`) ON DELETE RESTRICT ON UPDATE CASCADE;

-- Seed minimal supplier data
INSERT INTO `nhacungcap` (`MaNCC`, `TenNCC`, `SDT`, `DiaChi`) VALUES
  (1, 'NCC A', '0900000001', 'Hà Nội'),
  (2, 'NCC B', '0900000002', 'Hồ Chí Minh');

INSERT INTO `ncc_sanpham` (`MaNCC`, `MaMon`, `SoLuong`, `DonGia`) VALUES
  (1, 2, 100, 9000),  -- Trân Châu Tuyết Sợi
  (1, 3, 200, 8000),  -- Trân Châu Đen
  (1, 4, 150, 8500),  -- Trân Châu Trắng
  (2, 5, 300, 40000), -- Trà Sữa Trân Châu
  (2, 8, 250, 38000); -- Sữa Tươi Trân Châu Đường Đen

-- --------------------------------------------------------
-- Ingredients domain (Nguyên liệu) and inventory changes
-- --------------------------------------------------------

-- Ingredients master table
CREATE TABLE IF NOT EXISTS `nguyenlieu` (
  `MaNL` int(11) NOT NULL AUTO_INCREMENT,
  `TenNL` varchar(100) NOT NULL,
  `DonVi` varchar(20) NOT NULL,
  PRIMARY KEY (`MaNL`),
  UNIQUE KEY `uq_ten_nl` (`TenNL`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Warehouse now tracks ingredients instead of finished goods
CREATE TABLE IF NOT EXISTS `kho_nguyenlieu` (
  `MaNL` int(11) NOT NULL,
  `SoLuong` int(11) NOT NULL DEFAULT 0,
  `CapNhat` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`MaNL`),
  CONSTRAINT `fk_kho_nl` FOREIGN KEY (`MaNL`) REFERENCES `nguyenlieu` (`MaNL`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Supplier can supply ingredients too
CREATE TABLE IF NOT EXISTS `ncc_nguyenlieu` (
  `MaNCC` int(11) NOT NULL,
  `MaNL` int(11) NOT NULL,
  `SoLuong` int(11) NOT NULL DEFAULT 0,
  `DonGia` bigint(20) NOT NULL DEFAULT 0,
  PRIMARY KEY (`MaNCC`,`MaNL`),
  KEY `fk_nccnl_nl` (`MaNL`),
  CONSTRAINT `fk_nccnl_ncc` FOREIGN KEY (`MaNCC`) REFERENCES `nhacungcap` (`MaNCC`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_nccnl_nl` FOREIGN KEY (`MaNL`) REFERENCES `nguyenlieu` (`MaNL`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Recipe: quantity of each ingredient per dish
CREATE TABLE IF NOT EXISTS `mon_nguyenlieu` (
  `MaMon` int(11) NOT NULL,
  `MaNL` int(11) NOT NULL,
  `SoLuongDung` int(11) NOT NULL,
  `DonVi` varchar(20) NOT NULL,
  PRIMARY KEY (`MaMon`,`MaNL`),
  KEY `fk_mnl_nl` (`MaNL`),
  CONSTRAINT `fk_mnl_mon` FOREIGN KEY (`MaMon`) REFERENCES `mon` (`MaMon`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_mnl_nl` FOREIGN KEY (`MaNL`) REFERENCES `nguyenlieu` (`MaNL`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Seed three example ingredients
INSERT INTO `nguyenlieu` (`MaNL`, `TenNL`, `DonVi`) VALUES
  (1, 'Đường', 'gram'),
  (2, 'Trà Ô Long', 'gram'),
  (3, 'Sữa tươi', 'ml')
ON DUPLICATE KEY UPDATE TenNL=VALUES(TenNL), DonVi=VALUES(DonVi);

-- Seed supplier ingredients
INSERT INTO `ncc_nguyenlieu` (`MaNCC`, `MaNL`, `SoLuong`, `DonGia`) VALUES
  (1, 1, 50000, 20),
  (1, 2, 20000, 50),
  (2, 3, 30000, 40)
ON DUPLICATE KEY UPDATE SoLuong=VALUES(SoLuong), DonGia=VALUES(DonGia);

-- Import details for ingredients
CREATE TABLE IF NOT EXISTS `chitietnhap_nl` (
  `MaPN` int(11) NOT NULL,
  `MaNL` int(11) NOT NULL,
  `SoLuong` int(11) NOT NULL,
  `DonGia` bigint(20) NOT NULL DEFAULT 0,
  `DonVi` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`MaPN`,`MaNL`),
  KEY `fk_ctnnl_nl` (`MaNL`),
  CONSTRAINT `fk_ctnnl_pn` FOREIGN KEY (`MaPN`) REFERENCES `phieunhap` (`MaPN`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_ctnnl_nl` FOREIGN KEY (`MaNL`) REFERENCES `nguyenlieu` (`MaNL`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

