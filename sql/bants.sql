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
  `MaLoai` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `mon`
--

INSERT INTO `mon` (`MaMon`, `TenMon`, `MoTa`, `TenDonVi`, `Gia`, `MaLoai`) VALUES
(1, 'No Topping', '', 'Phần', 0, 4),
(2, 'Trân Châu Tuyết Sợi', NULL, 'Phần', 10000, 4),
(3, 'Trân Châu Đen', NULL, 'Phần', 10000, 4),
(4, 'Trân Châu Trắng', NULL, 'Phần', 10000, 4),
(5, 'Trà Sữa Trân Châu', '', 'Ly', 50000, 2),
(6, 'Trà Sữa Sương Sáo', NULL, 'Ly', 45000, 2),
(7, 'Trà Sữa Matcha(L)', '', 'Ly', 50000, 2),
(8, 'Sữa Tươi Trân Châu Đường Đen', NULL, 'Ly', 45000, 2),
(9, 'Bánh Flan', '', 'Cái', 10000, 2),
(10, 'Hướng dương', NULL, 'Túi', 10000, 1),
(11, 'Cafe truyền thống', NULL, 'Cốc', 35000, 3),
(12, 'Espresso', NULL, 'Cốc', 45000, 3),
(13, 'Trà Sữa Matcha(XL)', NULL, 'Ly', 25000, 2),
(14, 'Trà Sữa Ô Long', '', 'Ly', 20000, 2),
(15, 'Trà Đào', '', 'Ly', 40000, 2),
(16, 'Trà Đào(L)', '', 'Ly', 50000, 2),
(18, 'Trà Nhãn - Sen', '', 'Ly', 45000, 2);

-- --------------------------------------------------------

--
-- Table structure for table `dondathang`
--

CREATE TABLE `dondathang` (
  `MaDon` int(11) NOT NULL,
  `MaNV` int(11) NOT NULL,
  `MaBan` int(11) DEFAULT NULL,
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

INSERT INTO `dondathang` (`MaDon`, `MaNV`, `MaBan`, `Loai`, `TrangThai`, `NgayDat`, `NgayThanhToan`, `SoTienDaTra`, `TongTien`, `GiamGia`) VALUES
(1, 1, 1, 'taiquan', 'dathanhtoan', '2020-11-24 07:28:41', '2020-12-01 10:40:46', 450000, 450000, 0),
(2, 1, 1, 'online', 'dathanhtoan', '2020-11-24 08:05:08', '2020-12-23 06:33:51', 406000, 406000, 0),
(3, 1, 3, 'taiquan', 'dathanhtoan', '2020-12-16 08:55:36', '2020-12-23 06:34:01', 550000, 550000, 5);

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
-- Table structure for table `giaohang`
--

CREATE TABLE `giaohang` (
  `MaDon` int(11) NOT NULL,
  `MaKH` int(11) NOT NULL,
  `TenShipper` varchar(50) DEFAULT NULL,
  `SDTShipper` varchar(20) DEFAULT NULL,
  `PhiShip` int(11) DEFAULT NULL,
  `TrangThai` varchar(45) NOT NULL DEFAULT 'choxacnhan' COMMENT 'choxacnhan - chờ xác nhận\ncholayhang - chờ lấy hàng\ndangiao - đang giao\nhoanthanh - hoàn thành\nhuy - đã hủy',
  `ThongBao` varchar(45) DEFAULT NULL,
  `NgayBatDau` timestamp NULL DEFAULT current_timestamp(),
  `NgayKetThuc` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `giaohang`
--

INSERT INTO `giaohang` (`MaDon`, `MaKH`, `TenShipper`, `SDTShipper`, `PhiShip`, `TrangThai`, `ThongBao`, `NgayBatDau`, `NgayKetThuc`) VALUES
(2, 1, 'Nguyễn Văn B', '09421321323', 0, 'dangiao', NULL, '2020-11-23 17:00:00', NULL);

-- --------------------------------------------------------

--
-- Table structure for table `ban`
--

CREATE TABLE `ban` (
  `MaBan` int(11) NOT NULL,
  `TenBan` varchar(45) NOT NULL,
  `TrangThai` varchar(45) NOT NULL DEFAULT 'trong' COMMENT 'trong-Trống\nphucvu-Đang phục vụ\ndattruoc-Đặt trước'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `ban`
--

INSERT INTO `ban` (`MaBan`, `TenBan`, `TrangThai`) VALUES
(1, 'Bàn 1', 'trong'),
(2, 'Bàn 2', 'trong'),
(3, 'Bàn 3', 'phucvu'),
(4, 'Bàn 4', 'trong'),
(5, 'Bàn 5', 'trong'),
(6, 'Bàn 6', 'trong'),
(7, 'Bàn 7', 'trong'),
(8, 'Bàn 8', 'trong'),
(10, 'Bàn 10', 'phucvu'),
(11, 'Bàn 11', 'trong'),
(15, 'Bàn 12', 'trong'),
(16, 'Bàn 20', 'trong');

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
  ADD KEY `fk_nhanvien_don` (`MaNV`),
  ADD KEY `fk_ban_don` (`MaBan`);

--
-- Indexes for table `chitietdonhang`
--
ALTER TABLE `chitietdonhang`
  ADD PRIMARY KEY (`MaDon`,`MaMon`,`MaTopping`),
  ADD KEY `fk_don_mon` (`MaMon`),
  ADD KEY `fk_don_topping` (`MaTopping`);



--
-- Indexes for table `giaohang`
--
ALTER TABLE `giaohang`
  ADD PRIMARY KEY (`MaDon`),
  ADD KEY `fk_khachhang_giao` (`MaKH`);

--
-- Indexes for table `ban`
--
ALTER TABLE `ban`
  ADD PRIMARY KEY (`MaBan`);

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
ALTER TABLE `ban`
  MODIFY `MaBan` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=17;

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
  ADD CONSTRAINT `fk_ban_don` FOREIGN KEY (`MaBan`) REFERENCES `ban` (`MaBan`),
  ADD CONSTRAINT `fk_nhanvien_don` FOREIGN KEY (`MaNV`) REFERENCES `nhanvien` (`MaNV`);

--
-- Constraints for table `chitietdonhang`
--
ALTER TABLE `chitietdonhang`
  ADD CONSTRAINT `fk_don_chitiet` FOREIGN KEY (`MaDon`) REFERENCES `dondathang` (`MaDon`),
  ADD CONSTRAINT `fk_don_mon` FOREIGN KEY (`MaMon`) REFERENCES `mon` (`MaMon`),
  ADD CONSTRAINT `fk_don_topping` FOREIGN KEY (`MaTopping`) REFERENCES `mon` (`MaMon`);

--
-- Constraints for table `giaohang`
--
ALTER TABLE `giaohang`
  ADD CONSTRAINT `fk_don_giao` FOREIGN KEY (`MaDon`) REFERENCES `dondathang` (`MaDon`),
  ADD CONSTRAINT `fk_khachhang_giao` FOREIGN KEY (`MaKH`) REFERENCES `khachhang` (`MaKH`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
