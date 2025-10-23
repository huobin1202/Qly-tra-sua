-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Oct 18, 2025 at 07:50 AM
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
(3, 5, 4, 5, 40000, 10000, ''),
(4, 5, 2, 2, 50000, 10000, 'Ít đường'),
(4, 6, 1, 1, 45000, 0, ''),
(5, 7, 3, 3, 50000, 10000, 'Nhiều đá'),
(5, 8, 1, 1, 45000, 0, ''),
(6, 11, 1, 2, 35000, 0, 'Đậm đà'),
(6, 9, 1, 2, 10000, 0, ''),
(7, 13, 2, 4, 25000, 10000, 'Ít ngọt'),
(7, 14, 1, 2, 20000, 0, ''),
(8, 15, 3, 3, 40000, 10000, 'Nhiều đá'),
(8, 16, 1, 1, 50000, 0, ''),
(9, 5, 2, 5, 50000, 10000, 'Bình thường'),
(9, 6, 3, 3, 45000, 10000, ''),
(9, 7, 1, 2, 50000, 0, ''),
(10, 12, 1, 2, 45000, 0, 'Đậm đà'),
(10, 9, 1, 1, 10000, 0, ''),
(11, 18, 2, 4, 45000, 10000, 'Ít ngọt'),
(11, 8, 1, 2, 45000, 0, ''),
(12, 5, 3, 2, 50000, 10000, 'Nhiều đá'),
(12, 6, 1, 1, 45000, 0, ''),
(12, 9, 1, 1, 10000, 0, ''),
(13, 7, 2, 3, 50000, 10000, 'Bình thường'),
(13, 15, 1, 1, 40000, 0, ''),
(14, 8, 3, 3, 45000, 10000, 'Ít đường'),
(14, 11, 1, 1, 35000, 0, ''),
(15, 5, 2, 4, 50000, 10000, 'Nhiều đá'),
(15, 6, 3, 2, 45000, 10000, ''),
(15, 7, 1, 1, 50000, 0, '');

-- --------------------------------------------------------

--
-- Table structure for table `chitietnhap_nl`
--

CREATE TABLE `chitietnhap_nl` (
  `MaPN` int(11) NOT NULL,
  `MaNL` int(11) NOT NULL,
  `SoLuong` int(11) NOT NULL,
  `DonGia` bigint(20) NOT NULL DEFAULT 0,
  `DonVi` varchar(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `chitietnhap_nl`
--

INSERT INTO `chitietnhap_nl` (`MaPN`, `MaNL`, `SoLuong`, `DonGia`, `DonVi`) VALUES
(2, 1, 9899, 20, 'gram'),
(3, 1, 15000, 25, 'gram'),
(3, 2, 5000, 60, 'gram'),
(4, 2, 8000, 55, 'gram'),
(4, 3, 2000, 45, 'ml'),
(5, 3, 3000, 50, 'ml'),
(6, 1, 10000, 22, 'gram'),
(6, 2, 3000, 65, 'gram'),
(7, 1, 5000, 20, 'gram'),
(8, 1, 12000, 25, 'gram'),
(8, 2, 4000, 60, 'gram'),
(8, 3, 1500, 45, 'ml');

-- --------------------------------------------------------

--
-- Table structure for table `dondathang`
--

CREATE TABLE `dondathang` (
  `MaDon` int(11) NOT NULL,
  `MaNV` int(11) NOT NULL,
  `Loai` varchar(45) NOT NULL DEFAULT 'taiquan' COMMENT 'taiquan - tại quán\nonline - đặt online',
  `TrangThai` varchar(45) NOT NULL DEFAULT '2' COMMENT '1 - đã thanh toán\n2 - chưa thanh toán',
  `NgayDat` timestamp NOT NULL DEFAULT current_timestamp(),
  `TongTien` bigint(20) NOT NULL DEFAULT 0,
  `GiamGia` int(11) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `dondathang`
--

INSERT INTO `dondathang` (`MaDon`, `MaNV`, `Loai`, `TrangThai`, `NgayDat`, `TongTien`, `GiamGia`) VALUES
(1, 1, 'taiquan', '1', '2020-11-24 07:28:41', 450000, 0),
(2, 1, 'online', '1', '2020-11-24 08:05:08', 406000, 0),
(3, 1, 'taiquan', '1', '2020-12-16 08:55:36', 550000, 5),
(4, 2, 'taiquan', '1', '2025-10-19 09:15:00', 120000, 0),
(5, 1, 'online', '1', '2025-10-19 14:30:00', 180000, 10),
(6, 5, 'taiquan', '2', '2025-10-20 11:45:00', 95000, 0),
(7, 2, 'online', '1', '2025-10-20 16:20:00', 220000, 5),
(8, 1, 'taiquan', '1', '2025-10-21 08:30:00', 150000, 0),
(9, 6, 'online', '1', '2025-10-21 19:15:00', 300000, 15),
(10, 2, 'taiquan', '2', '2025-10-22 12:00:00', 75000, 0),
(11, 1, 'online', '1', '2025-10-22 20:45:00', 250000, 0),
(12, 5, 'taiquan', '1', '2025-10-23 10:30:00', 135000, 0),
(13, 2, 'online', '1', '2025-10-23 18:00:00', 190000, 8),
(14, 1, 'taiquan', '1', '2025-10-24 13:15:00', 165000, 0),
(15, 6, 'online', '2', '2025-10-24 21:30:00', 280000, 12);

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
(2, 1, 'Nguyễn Văn B', '09421321323', 0, 'dangiao', NULL, '2020-11-23 10:00:00', NULL),
(5, 3, 'Trần Văn C', '0987654321', 15000, 'hoanthanh', 'Giao hàng thành công', '2025-10-19 15:00:00', '2025-10-19 16:30:00'),
(7, 4, 'Lê Thị D', '0912345678', 20000, 'dangiao', 'Đang giao hàng', '2025-10-20 17:00:00', NULL),
(9, 5, 'Phạm Văn E', '0965432109', 25000, 'cholayhang', 'Chờ lấy hàng', '2025-10-21 20:00:00', NULL),
(11, 6, 'Nguyễn Thị F', '0934567890', 18000, 'hoanthanh', 'Giao hàng thành công', '2025-10-22 21:00:00', '2025-10-22 22:15:00'),
(13, 7, 'Trần Văn G', '0976543210', 22000, 'dangiao', 'Đang giao hàng', '2025-10-23 19:00:00', NULL),
(15, 8, 'Lê Thị H', '0945678901', 20000, 'choxacnhan', 'Chờ xác nhận', '2025-10-24 22:00:00', NULL);

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
(2, '0911175581', 'Linh', 'Hà Nội', '2000-09-16 10:00:00'),
(3, '0987654321', 'Nguyễn Thị Mai', 'Số 12 Nguyễn Trãi, Thanh Xuân, Hà Nội', '1998-03-15 08:30:00'),
(4, '0912345678', 'Trần Văn Nam', 'Số 45 Lê Lợi, Hoàn Kiếm, Hà Nội', '1995-07-22 14:20:00'),
(5, '0965432109', 'Phạm Thị Hoa', 'Số 78 Cầu Giấy, Cầu Giấy, Hà Nội', '2001-11-08 09:15:00'),
(6, '0934567890', 'Lê Minh Tuấn', 'Số 23 Đống Đa, Đống Đa, Hà Nội', '1997-05-30 16:45:00'),
(7, '0976543210', 'Hoàng Thị Lan', 'Số 67 Hai Bà Trưng, Hai Bà Trưng, Hà Nội', '1999-12-12 11:30:00'),
(8, '0945678901', 'Vũ Đức Anh', 'Số 89 Ba Đình, Ba Đình, Hà Nội', '1996-08-25 13:10:00'),
(9, '0923456789', 'Đặng Thị Nga', 'Số 34 Tây Hồ, Tây Hồ, Hà Nội', '2000-01-18 10:25:00'),
(10, '0956789012', 'Bùi Văn Hùng', 'Số 56 Long Biên, Long Biên, Hà Nội', '1994-09-03 15:40:00');

-- --------------------------------------------------------

--
-- Table structure for table `kho_nguyenlieu`
--

CREATE TABLE `kho_nguyenlieu` (
  `MaNL` int(11) NOT NULL,
  `SoLuong` int(11) NOT NULL DEFAULT 0,
  `CapNhat` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `kho_nguyenlieu`
--

INSERT INTO `kho_nguyenlieu` (`MaNL`, `SoLuong`, `CapNhat`) VALUES
(1, 9899, '2025-10-18 05:42:15');

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
  `Gia` bigint(20) NOT NULL,
  `TinhTrang` varchar(20) NOT NULL DEFAULT 'ban',
  `MaLoai` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `mon`
--

INSERT INTO `mon` (`MaMon`, `TenMon`, `MoTa`, `Gia`, `TinhTrang`, `MaLoai`) VALUES
(1, 'No Topping', '', 0, 'ban', 4),
(2, 'Trân Châu Tuyết Sợi', '', 10000, 'ban', 4),
(3, 'Trân Châu Đen', '', 10000, 'ban', 4),
(4, 'Trân Châu Trắng', '', 10000, 'ban', 4),
(5, 'Trà Sữa Trân Châu', '', 50000, 'ban', 2),
(6, 'Trà Sữa Sương Sáo', '', 45000, 'ban', 2),
(7, 'Trà Sữa Matcha(L)', '', 50000, 'ban', 2),
(8, 'Sữa Tươi Trân Châu Đường Đen', '', 45000, 'ban', 2),
(9, 'Bánh Flan', '', 10000, 'ban', 2),
(10, 'Hướng dương', '', 10000, 'ban', 1),
(11, 'Cafe truyền thống', '', 35000, 'ban', 3),
(12, 'Espresso', '', 45000, 'ban', 3),
(13, 'Trà Sữa Matcha(XL)', '', 25000, 'ban', 2),
(14, 'Trà Sữa Ô Long', '', 20000, 'ban', 2),
(15, 'Trà Đào', '', 40000, 'ban', 2),
(16, 'Trà Đào(L)', '', 50000, 'ban', 2),
(18, 'Trà Nhãn - Sen', '', 45000, 'ban', 2);

-- --------------------------------------------------------

--
-- Table structure for table `ncc_nguyenlieu`
--

CREATE TABLE `ncc_nguyenlieu` (
  `MaNCC` int(11) NOT NULL,
  `MaNL` int(11) NOT NULL,
  `SoLuong` int(11) NOT NULL DEFAULT 0,
  `DonGia` bigint(20) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `ncc_nguyenlieu`
--

INSERT INTO `ncc_nguyenlieu` (`MaNCC`, `MaNL`, `SoLuong`, `DonGia`) VALUES
(1, 1, 40101, 20),
(1, 2, 20000, 50),
(2, 3, 30000, 40),
(3, 1, 50000, 25),
(3, 2, 25000, 60),
(3, 3, 10000, 45),
(4, 2, 30000, 55),
(4, 3, 15000, 45),
(5, 3, 20000, 50),
(6, 1, 30000, 22),
(6, 2, 15000, 65),
(7, 1, 25000, 20);

-- --------------------------------------------------------

--
-- Table structure for table `nguyenlieu`
--

CREATE TABLE `nguyenlieu` (
  `MaNL` int(11) NOT NULL,
  `TenNL` varchar(100) NOT NULL,
  `DonVi` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `nguyenlieu`
--

INSERT INTO `nguyenlieu` (`MaNL`, `TenNL`, `DonVi`) VALUES
(1, 'Đường', 'gram'),
(2, 'Trà Ô Long', 'gram'),
(3, 'Sữa tươi', 'ml');

-- --------------------------------------------------------

--
-- Table structure for table `nhacungcap`
--

CREATE TABLE `nhacungcap` (
  `MaNCC` int(11) NOT NULL,
  `TenNCC` varchar(100) NOT NULL,
  `SDT` varchar(20) DEFAULT NULL,
  `DiaChi` varchar(200) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `nhacungcap`
--

INSERT INTO `nhacungcap` (`MaNCC`, `TenNCC`, `SDT`, `DiaChi`) VALUES
(1, 'NCC A', '0900000001', 'Hà Nội'),
(2, 'NCC B', '0900000002', 'Hồ Chí Minh'),
(3, 'Công ty TNHH Nguyên liệu Thực phẩm ABC', '0241234567', 'Số 123 Đường Láng, Đống Đa, Hà Nội'),
(4, 'Nhà cung cấp Trà sữa Việt Nam', '0287654321', 'Số 456 Nguyễn Huệ, Quận 1, TP.HCM'),
(5, 'Công ty Sữa tươi Đà Lạt', '0263388888', 'Số 789 Đường 3/2, Đà Lạt, Lâm Đồng'),
(6, 'Nhà cung cấp Topping Premium', '0249999999', 'Số 321 Cầu Giấy, Hà Nội'),
(7, 'Công ty Đường mía Sóc Trăng', '0299388888', 'Số 654 Đường 30/4, Sóc Trăng');

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
-- Table structure for table `phieunhap`
--

CREATE TABLE `phieunhap` (
  `MaPN` int(11) NOT NULL,
  `MaNV` int(11) NOT NULL,
  `MaNCC` int(11) NOT NULL,
  `Ngay` timestamp NOT NULL DEFAULT current_timestamp(),
  `GhiChu` varchar(250) DEFAULT NULL,
  `ThanhTien` bigint(20) NOT NULL DEFAULT 0,
  `TrangThai` varchar(45) NOT NULL DEFAULT 'chuaxacnhan' COMMENT 'chuaxacnhan - chưa xác nhận\ndaxacnhan - đã xác nhận'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `phieunhap`
--

INSERT INTO `phieunhap` (`MaPN`, `MaNV`, `MaNCC`, `Ngay`, `GhiChu`, `ThanhTien`, `TrangThai`) VALUES
(2, 1, 1, '2025-10-18 05:42:10', '', 197980, 'daxacnhan'),
(3, 1, 3, '2025-10-19 08:30:00', 'Nhập nguyên liệu cho tháng 10', 450000, 'daxacnhan'),
(4, 5, 4, '2025-10-20 14:15:00', 'Nhập trà sữa cao cấp', 320000, 'daxacnhan'),
(5, 1, 5, '2025-10-21 09:45:00', 'Nhập sữa tươi Đà Lạt', 180000, 'chuaxacnhan'),
(6, 6, 6, '2025-10-22 16:20:00', 'Nhập topping premium', 250000, 'daxacnhan'),
(7, 1, 7, '2025-10-23 11:10:00', 'Nhập đường mía chất lượng cao', 120000, 'daxacnhan'),
(8, 5, 2, '2025-10-24 13:30:00', 'Nhập nguyên liệu dự trữ', 380000, 'chuaxacnhan');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `chitietdonhang`
--
ALTER TABLE `chitietdonhang`
  ADD PRIMARY KEY (`MaDon`,`MaMon`,`MaTopping`),
  ADD KEY `fk_don_mon` (`MaMon`),
  ADD KEY `fk_don_topping` (`MaTopping`);

--
-- Indexes for table `chitietnhap_nl`
--
ALTER TABLE `chitietnhap_nl`
  ADD PRIMARY KEY (`MaPN`,`MaNL`),
  ADD KEY `fk_ctnnl_nl` (`MaNL`);

--
-- Indexes for table `dondathang`
--
ALTER TABLE `dondathang`
  ADD PRIMARY KEY (`MaDon`),
  ADD KEY `fk_nhanvien_don` (`MaNV`);

--
-- Indexes for table `giaohang`
--
ALTER TABLE `giaohang`
  ADD PRIMARY KEY (`MaDon`),
  ADD KEY `fk_khachhang_giao` (`MaKH`);

--
-- Indexes for table `khachhang`
--
ALTER TABLE `khachhang`
  ADD PRIMARY KEY (`MaKH`);

--
-- Indexes for table `kho_nguyenlieu`
--
ALTER TABLE `kho_nguyenlieu`
  ADD PRIMARY KEY (`MaNL`);

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
-- Indexes for table `ncc_nguyenlieu`
--
ALTER TABLE `ncc_nguyenlieu`
  ADD PRIMARY KEY (`MaNCC`,`MaNL`),
  ADD KEY `fk_nccnl_nl` (`MaNL`);

--
-- Indexes for table `nguyenlieu`
--
ALTER TABLE `nguyenlieu`
  ADD PRIMARY KEY (`MaNL`),
  ADD UNIQUE KEY `uq_ten_nl` (`TenNL`);

--
-- Indexes for table `nhacungcap`
--
ALTER TABLE `nhacungcap`
  ADD PRIMARY KEY (`MaNCC`);

--
-- Indexes for table `nhanvien`
--
ALTER TABLE `nhanvien`
  ADD PRIMARY KEY (`MaNV`),
  ADD UNIQUE KEY `TaiKhoan` (`TaiKhoan`);

--
-- Indexes for table `phieunhap`
--
ALTER TABLE `phieunhap`
  ADD PRIMARY KEY (`MaPN`),
  ADD KEY `fk_pn_nv` (`MaNV`),
  ADD KEY `fk_pn_ncc` (`MaNCC`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `dondathang`
--
ALTER TABLE `dondathang`
  MODIFY `MaDon` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

--
-- AUTO_INCREMENT for table `khachhang`
--
ALTER TABLE `khachhang`
  MODIFY `MaKH` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

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
-- AUTO_INCREMENT for table `nguyenlieu`
--
ALTER TABLE `nguyenlieu`
  MODIFY `MaNL` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `nhacungcap`
--
ALTER TABLE `nhacungcap`
  MODIFY `MaNCC` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT for table `nhanvien`
--
ALTER TABLE `nhanvien`
  MODIFY `MaNV` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=28;

--
-- AUTO_INCREMENT for table `phieunhap`
--
ALTER TABLE `phieunhap`
  MODIFY `MaPN` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `chitietdonhang`
--
ALTER TABLE `chitietdonhang`
  ADD CONSTRAINT `fk_don_chitiet` FOREIGN KEY (`MaDon`) REFERENCES `dondathang` (`MaDon`),
  ADD CONSTRAINT `fk_don_mon` FOREIGN KEY (`MaMon`) REFERENCES `mon` (`MaMon`),
  ADD CONSTRAINT `fk_don_topping` FOREIGN KEY (`MaTopping`) REFERENCES `mon` (`MaMon`);

--
-- Constraints for table `chitietnhap_nl`
--
ALTER TABLE `chitietnhap_nl`
  ADD CONSTRAINT `fk_ctnnl_nl` FOREIGN KEY (`MaNL`) REFERENCES `nguyenlieu` (`MaNL`) ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_ctnnl_pn` FOREIGN KEY (`MaPN`) REFERENCES `phieunhap` (`MaPN`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `dondathang`
--
ALTER TABLE `dondathang`
  ADD CONSTRAINT `fk_nhanvien_don` FOREIGN KEY (`MaNV`) REFERENCES `nhanvien` (`MaNV`);

--
-- Constraints for table `giaohang`
--
ALTER TABLE `giaohang`
  ADD CONSTRAINT `fk_don_giao` FOREIGN KEY (`MaDon`) REFERENCES `dondathang` (`MaDon`),
  ADD CONSTRAINT `fk_khachhang_giao` FOREIGN KEY (`MaKH`) REFERENCES `khachhang` (`MaKH`);

--
-- Constraints for table `kho_nguyenlieu`
--
ALTER TABLE `kho_nguyenlieu`
  ADD CONSTRAINT `fk_kho_nl` FOREIGN KEY (`MaNL`) REFERENCES `nguyenlieu` (`MaNL`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `mon`
--
ALTER TABLE `mon`
  ADD CONSTRAINT `fk_mon_loai` FOREIGN KEY (`MaLoai`) REFERENCES `loaimon` (`MaLoai`);

--
-- Constraints for table `ncc_nguyenlieu`
--
ALTER TABLE `ncc_nguyenlieu`
  ADD CONSTRAINT `fk_nccnl_ncc` FOREIGN KEY (`MaNCC`) REFERENCES `nhacungcap` (`MaNCC`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_nccnl_nl` FOREIGN KEY (`MaNL`) REFERENCES `nguyenlieu` (`MaNL`) ON UPDATE CASCADE;

--
-- Constraints for table `phieunhap`
--
ALTER TABLE `phieunhap`
  ADD CONSTRAINT `fk_pn_ncc` FOREIGN KEY (`MaNCC`) REFERENCES `nhacungcap` (`MaNCC`) ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_pn_nv` FOREIGN KEY (`MaNV`) REFERENCES `nhanvien` (`MaNV`) ON UPDATE CASCADE;



COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
