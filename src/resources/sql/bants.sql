-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Nov 16, 2025 at 10:34 AM
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
  `GiaTopping` bigint(20) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `chitietdonhang`
--

INSERT INTO `chitietdonhang` (`MaDon`, `MaMon`, `MaTopping`, `SoLuong`, `GiaMon`, `GiaTopping`) VALUES
(2, 8, 1, 2, 43000, 0),
(2, 9, 1, 8, 10000, 0),
(2, 15, 1, 6, 40000, 0),
(5, 7, 3, 3, 50000, 10000),
(5, 8, 1, 1, 45000, 0),
(7, 13, 2, 4, 25000, 10000),
(7, 14, 1, 2, 20000, 0),
(9, 5, 2, 5, 50000, 10000),
(9, 6, 3, 3, 45000, 10000),
(9, 7, 1, 2, 50000, 0),
(11, 8, 1, 2, 45000, 0),
(11, 18, 2, 4, 45000, 10000),
(13, 7, 2, 3, 50000, 10000),
(13, 15, 1, 1, 40000, 0),
(16, 5, 1, 1, 50000, 0),
(16, 9, 1, 1, 10000, 0),
(18, 7, 3, 3, 50000, 10000),
(18, 15, 1, 1, 40000, 0),
(19, 8, 1, 2, 45000, 0),
(19, 11, 1, 2, 35000, 0),
(20, 5, 4, 4, 50000, 10000),
(20, 16, 1, 1, 50000, 0),
(22, 9, 1, 2, 10000, 0),
(22, 18, 2, 2, 45000, 10000),
(23, 7, 3, 3, 50000, 10000),
(23, 8, 1, 1, 45000, 0),
(25, 11, 1, 3, 35000, 0),
(26, 5, 2, 5, 50000, 10000),
(26, 7, 3, 2, 50000, 10000),
(27, 8, 1, 2, 45000, 0),
(27, 9, 1, 1, 10000, 0),
(28, 14, 1, 1, 20000, 0),
(28, 16, 1, 3, 50000, 0),
(30, 7, 4, 4, 50000, 10000),
(30, 18, 2, 2, 45000, 10000),
(32, 8, 3, 3, 45000, 10000),
(32, 9, 1, 2, 10000, 0),
(33, 5, 2, 3, 50000, 10000),
(33, 6, 3, 2, 45000, 10000),
(51, 7, 4, 2, 50000, 10000),
(52, 7, 4, 1, 50000, 10000),
(53, 13, 28, 1, 25000, 5000),
(54, 7, 1, 1, 50000, 0),
(55, 6, 1, 1, 45000, 0);

-- --------------------------------------------------------

--
-- Table structure for table `chitietnhap_nl`
--

CREATE TABLE `chitietnhap_nl` (
  `MaPN` int(11) NOT NULL,
  `MaNL` int(11) NOT NULL,
  `SoLuong` int(11) NOT NULL,
  `DonGia` bigint(20) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `chitietnhap_nl`
--

INSERT INTO `chitietnhap_nl` (`MaPN`, `MaNL`, `SoLuong`, `DonGia`) VALUES
(2, 1, 9899, 20),
(3, 1, 15000, 25),
(3, 2, 5000, 60),
(4, 2, 8000, 55),
(4, 3, 2000, 45),
(5, 3, 3000, 50),
(6, 1, 10000, 22),
(6, 2, 3000, 65),
(7, 1, 5000, 20),
(8, 1, 12000, 25),
(8, 2, 4000, 60),
(8, 3, 1500, 45),
(9, 1, 11000, 20),
(9, 2, 2500, 55),
(10, 1, 14000, 25),
(10, 2, 4500, 60),
(10, 3, 1800, 45),
(11, 2, 5000, 55),
(11, 3, 2500, 45),
(12, 3, 3000, 50),
(13, 1, 12500, 22),
(13, 2, 3500, 65),
(14, 1, 9500, 20),
(14, 3, 2200, 45),
(15, 1, 12000, 20),
(15, 2, 3000, 55),
(16, 1, 16800, 25),
(16, 2, 7000, 60),
(16, 3, 2000, 45),
(17, 2, 5000, 55),
(17, 3, 1800, 45),
(18, 3, 3500, 50),
(19, 3, 200, 45);

-- --------------------------------------------------------

--
-- Table structure for table `donhang`
--

CREATE TABLE `donhang` (
  `MaDon` int(11) NOT NULL,
  `MaNV` int(11) NOT NULL,
  `MaKH` int(11) DEFAULT NULL,
  `TrangThai` varchar(45) NOT NULL DEFAULT 'chuathanhtoan' COMMENT 'chuathanhtoan - chưa thanh toán\ndathanhtoan - đã thanh toán\nbihuy - bị hủy',
  `NgayDat` timestamp NOT NULL DEFAULT current_timestamp(),
  `TongTien` bigint(20) NOT NULL DEFAULT 0,
  `GiamGia` int(11) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `donhang`
--

INSERT INTO `donhang` (`MaDon`, `MaNV`, `MaKH`, `TrangThai`, `NgayDat`, `TongTien`, `GiamGia`) VALUES
(2, 1, 1, 'dathanhtoan', '2020-11-24 08:05:08', 406000, 0),
(5, 1, 3, 'dathanhtoan', '2025-10-19 14:30:00', 180000, 10),
(7, 2, 4, 'dathanhtoan', '2025-10-20 16:20:00', 220000, 5),
(9, 6, 5, 'dathanhtoan', '2025-10-21 19:15:00', 300000, 15),
(11, 1, 6, 'dathanhtoan', '2025-10-22 20:45:00', 250000, 0),
(13, 2, 7, 'dathanhtoan', '2025-10-23 18:00:00', 190000, 8),
(16, 1, 11, 'dathanhtoan', '2025-10-25 08:15:00', 95000, 0),
(18, 5, 12, 'dathanhtoan', '2025-10-25 10:45:00', 180000, 5),
(19, 6, 13, 'dathanhtoan', '2025-10-25 11:20:00', 220000, 0),
(20, 1, 14, 'dathanhtoan', '2025-10-25 12:00:00', 270000, 10),
(22, 5, 15, 'bihuy', '2025-10-25 14:30:00', 140000, 0),
(23, 6, 16, 'dathanhtoan', '2025-10-25 15:45:00', 195000, 8),
(25, 2, 17, 'dathanhtoan', '2025-10-25 17:30:00', 105000, 5),
(26, 5, 18, 'dathanhtoan', '2025-10-26 08:00:00', 320000, 12),
(27, 6, 19, 'dathanhtoan', '2025-10-26 09:15:00', 145000, 0),
(28, 1, 20, 'bihuy', '2025-10-26 10:30:00', 260000, 10),
(30, 5, 21, 'dathanhtoan', '2025-10-26 12:20:00', 310000, 8),
(32, 1, 23, 'dathanhtoan', '2025-10-26 14:15:00', 205000, 5),
(33, 2, 24, 'dathanhtoan', '2025-10-26 15:30:00', 240000, 0),
(51, 1, 41, 'dathanhtoan', '2025-11-16 09:09:01', 114000, 5),
(52, 1, 41, 'dathanhtoan', '2025-11-16 09:10:46', 60000, 0),
(53, 1, 41, 'dathanhtoan', '2025-11-16 09:16:32', 30000, 0),
(54, 1, 41, 'dathanhtoan', '2025-11-16 09:17:31', 50000, 0),
(55, 1, 41, 'bihuy', '2025-11-16 09:31:02', 45000, 0);

-- --------------------------------------------------------

--
-- Table structure for table `khachhang`
--

CREATE TABLE `khachhang` (
  `MaKH` int(11) NOT NULL,
  `SDT` varchar(15) NOT NULL,
  `HoTen` varchar(50) DEFAULT NULL,
  `DiemTichLuy` int(11) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `khachhang`
--

INSERT INTO `khachhang` (`MaKH`, `SDT`, `HoTen`, `DiemTichLuy`) VALUES
(1, '911175581', 'Cường', 150),
(2, '911175582', 'Linh', 200),
(3, '987654321', 'Nguyễn Thị Mai', 350),
(4, '912345678', 'Trần Văn Nam', 120),
(5, '965432109', 'Phạm Thị Hoa', 500),
(6, '934567890', 'Lê Minh Tuấn', 280),
(7, '976543210', 'Hoàng Thị Lan', 180),
(9, '923456789', 'Đặng Thị Nga', 95),
(10, '956789012', 'Bùi Văn Hùng', 310),
(11, '901234567', 'Nguyễn Văn A', 75),
(12, '902345678', 'Trần Thị B', 125),
(13, '903456789', 'Lê Văn C', 200),
(14, '904567890', 'Phạm Thị D', 300),
(15, '905678901', 'Hoàng Văn E', 23),
(16, '1', 'Vũ Thị F', 12),
(17, '907890123', 'Đặng Văn G', 95),
(18, '908901234', 'Bùi Thị H', 420),
(19, '909012345', 'Ngô Văn I', 150),
(20, '910123456', 'Đỗ Thị K', 12),
(21, '9112345675', 'Dương Văn L', 5),
(23, '913456789', 'Võ Văn N', 42),
(24, '914567890', 'Lý Thị O', 24),
(41, '0908447742', 'BinhHuu', 5);

-- --------------------------------------------------------

--
-- Table structure for table `khohang`
--

CREATE TABLE `khohang` (
  `MaNL` int(11) NOT NULL,
  `SoLuong` int(11) NOT NULL DEFAULT 0,
  `CapNhat` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `khohang`
--

INSERT INTO `khohang` (`MaNL`, `SoLuong`, `CapNhat`) VALUES
(1, 53354, '2025-11-16 09:17:31'),
(2, 23205, '2025-11-16 09:17:31'),
(3, 19940, '2025-11-16 09:17:31'),
(4, 14760, '2025-11-16 09:17:31'),
(5, 8000, '2025-10-29 16:00:00'),
(6, 12000, '2025-10-29 16:00:00'),
(7, 10000, '2025-10-29 16:00:00'),
(8, 1333, '2025-11-16 08:54:19'),
(9, 25000, '2025-10-29 16:00:00'),
(10, 10, '2025-11-16 09:11:40'),
(11, 18000, '2025-10-29 16:00:00'),
(12, 22000, '2025-10-29 16:00:00'),
(14, 12312, '2025-11-16 08:53:50'),
(15, 500, '2025-11-16 08:54:09');

-- --------------------------------------------------------

--
-- Table structure for table `loaimon`
--

CREATE TABLE `loaimon` (
  `MaLoai` int(11) NOT NULL,
  `TenLoai` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `loaimon`
--

INSERT INTO `loaimon` (`MaLoai`, `TenLoai`) VALUES
(3, 'Cà Phê'),
(4, 'Topping'),
(2, 'Trà Sữa');

-- --------------------------------------------------------

--
-- Table structure for table `mon`
--

CREATE TABLE `mon` (
  `MaMon` int(11) NOT NULL,
  `TenMon` varchar(50) NOT NULL,
  `Gia` bigint(20) NOT NULL,
  `TinhTrang` varchar(20) NOT NULL DEFAULT 'dangban',
  `MaLoai` int(11) NOT NULL,
  `Anh` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `mon`
--

INSERT INTO `mon` (`MaMon`, `TenMon`, `Gia`, `TinhTrang`, `MaLoai`, `Anh`) VALUES
(1, 'No Topping', 0, 'dangban', 4, 'images/tran_chau_tuyet_soi.jpg'),
(2, 'Trân Châu Tuyết Sợi', 10000, 'dangban', 4, 'images/tran_chau_tuyet_soi.jpg'),
(3, 'Trân Châu Đen', 10000, 'dangban', 4, 'images/tran_chau_tuyet_soi.jpg'),
(4, 'Trân Châu Trắng', 10000, 'dangban', 4, 'erdplus(2).png'),
(5, 'Trà Sữa Trân Châu', 50000, 'dangban', 2, 'images/tra_sua_tran_chau.jpg'),
(6, 'Trà Sữa Sương Sáo', 45000, 'dangban', 2, 'images/tra_sua_suong_sao.jpg'),
(7, 'Trà Sữa Matcha(L)', 50000, 'dangban', 2, 'images/tra_sua_matcha_l.jpg'),
(8, 'Sữa Tươi Trân Châu Đường Đen', 45000, 'dangban', 2, 'images/sua_tuoi_tran_chau_duong_den.jpg'),
(9, 'Bánh Flan', 10000, 'dangban', 2, 'images/Đang bánh_flan.jpg'),
(11, 'Cafe truyền thống', 35000, 'dangban', 3, 'images/Screenshot 2025-11-06 160004.png'),
(12, 'Espresso', 45000, 'dangban', 3, 'images/espresso.jpg'),
(13, 'Trà Sữa Matcha(XL)', 25000, 'dangban', 2, 'images/tra_sua_matcha_xl.jpg'),
(14, 'Trà Sữa Ô Long', 20000, 'dangban', 2, 'images/tra_sua_o_long.jpg'),
(15, 'Trà Đào', 40000, 'dangban', 2, 'images/tra_dao.jpg'),
(16, 'Trà Đào(L)', 50000, 'dangban', 2, 'images/tra_dao_l.jpg'),
(18, 'Trà Nhãn - Sen', 45000, 'dangban', 2, 'images/tra_nhan_sen.jpg'),
(19, 'Latte', 40000, 'dangban', 3, 'images/latte.jpg'),
(20, 'Cappuccino', 42000, 'dangban', 3, 'images/cappuccino.jpg'),
(21, 'Americano', 32000, 'dangban', 3, 'images/americano.jpg'),
(22, 'Trà Sữa Dâu', 48000, 'dangban', 2, 'images/tra_sua_dau.jpg'),
(23, 'Trà Sữa Vani', 46000, 'dangban', 2, 'images/tra_sua_vani.jpg'),
(24, 'Trà Sữa Socola', 50000, 'dangban', 2, 'images/tra_sua_socola.jpg'),
(28, 'Thạch dừa', 5000, 'dangban', 4, 'images/thach_dua.jpg'),
(30, 'Macchiato', 48000, 'dangban', 3, 'images/macchiato.jpg');

-- --------------------------------------------------------

--
-- Table structure for table `mon_nguyenlieu`
--

CREATE TABLE `mon_nguyenlieu` (
  `MaMon` int(11) NOT NULL,
  `MaNL` int(11) NOT NULL,
  `SoLuong` int(11) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `mon_nguyenlieu`
--

INSERT INTO `mon_nguyenlieu` (`MaMon`, `MaNL`, `SoLuong`) VALUES
(2, 10, 100),
(3, 14, 100),
(5, 1, 30),
(5, 2, 20),
(5, 3, 200),
(5, 10, 50),
(6, 1, 25),
(6, 2, 20),
(6, 3, 180),
(7, 1, 30),
(7, 2, 20),
(7, 3, 220),
(7, 4, 50),
(8, 1, 40),
(8, 3, 250),
(8, 10, 60),
(11, 1, 25),
(11, 3, 150),
(11, 5, 30),
(12, 3, 100),
(12, 5, 40),
(13, 1, 25),
(13, 2, 15),
(13, 3, 180),
(13, 4, 40),
(14, 1, 35),
(14, 2, 25),
(14, 3, 200),
(15, 1, 30),
(15, 2, 20),
(15, 3, 200),
(15, 12, 80),
(16, 1, 35),
(16, 2, 25),
(16, 3, 250),
(16, 12, 100),
(18, 1, 32),
(18, 2, 22),
(18, 3, 210),
(19, 1, 20),
(19, 3, 200),
(19, 5, 30),
(20, 1, 25),
(20, 3, 180),
(20, 5, 35),
(21, 3, 150),
(21, 5, 35),
(22, 1, 30),
(22, 2, 20),
(22, 3, 200),
(22, 6, 50),
(23, 1, 30),
(23, 2, 20),
(23, 3, 200),
(23, 7, 50),
(24, 1, 30),
(24, 2, 20),
(24, 3, 200),
(24, 8, 50),
(28, 11, 100),
(30, 1, 20),
(30, 3, 180),
(30, 5, 30);

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
(1, 4, 15000, 80),
(1, 9, 20000, 25),
(2, 3, 30000, 40),
(2, 4, 12000, 85),
(2, 5, 10000, 120),
(3, 1, 50000, 25),
(3, 2, 25000, 60),
(3, 3, 9800, 45),
(3, 5, 8000, 125),
(4, 2, 30000, 55),
(4, 3, 15000, 45),
(4, 6, 15000, 90),
(4, 7, 15000, 90),
(4, 8, 15000, 95),
(5, 3, 20000, 50),
(5, 9, 18000, 28),
(5, 10, 25000, 35),
(6, 1, 30000, 22),
(6, 2, 15000, 65),
(6, 10, 20000, 38),
(6, 11, 12000, 42),
(7, 1, 25000, 20),
(7, 4, 10000, 88),
(7, 12, 18000, 45);

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
(3, 'Sữa tươi', 'ml'),
(4, 'Bột trà xanh', 'gram'),
(5, 'Bột cà phê', 'gram'),
(6, 'Siro dâu', 'ml'),
(7, 'Siro vani', 'ml'),
(8, 'Siro socola', 'ml'),
(9, 'Sữa đặc', 'ml'),
(10, 'Trân châu tuyết sợi', 'gram'),
(11, 'Thạch dừa', 'gram'),
(12, 'Đào ngâm', 'gram'),
(14, 'Trân châu đen', 'gram'),
(15, 'Trân châu trắng', 'gram');

-- --------------------------------------------------------

--
-- Table structure for table `nhacungcap`
--

CREATE TABLE `nhacungcap` (
  `MaNCC` int(11) NOT NULL,
  `TenNCC` varchar(100) NOT NULL,
  `SDT` varchar(15) DEFAULT NULL,
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
  `SDT` varchar(15) DEFAULT NULL,
  `NgayVaoLam` timestamp NOT NULL DEFAULT current_timestamp(),
  `ChucVu` varchar(50) NOT NULL COMMENT 'quanly-quản lý\r\nnhanvien-nhân viên\r\nnghiviec-nghỉ việc',
  `Luong` bigint(20) NOT NULL,
  `TrangThai` varchar(20) NOT NULL DEFAULT 'danglam' COMMENT 'danglam-đang làm, nghiviec-nghỉ việc'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `nhanvien`
--

INSERT INTO `nhanvien` (`MaNV`, `TaiKhoan`, `MatKhau`, `HoTen`, `SDT`, `NgayVaoLam`, `ChucVu`, `Luong`, `TrangThai`) VALUES
(1, 'admin', 'admin', 'Super Admin', '0911175581', '2025-11-16 08:44:54', 'quanly', 10600000, 'danglam'),
(2, 'nhanvien', '3', 'Tea', '2', '2025-11-16 08:51:16', 'nhanvien', 0, 'danglam'),
(5, 'cuong', 'cuong12345', 'Trần Đức Cường', '0911175582', '2020-12-16 14:11:54', 'quanly', 0, 'danglam'),
(6, 'anh', 'anh12345', 'Đỗ Tuấn Anh', '12324123', '2020-12-16 14:21:20', 'quanly', 0, 'danglam'),
(7, 'son', 'son12345', 'Nguyễn Lam Sơn', '123', '2020-12-16 14:22:12', 'quanly', 0, 'danglam'),
(27, 'karma', '1', 'Milk', '1', '2020-12-23 16:52:39', 'nhanvien', 0, 'danglam');

-- --------------------------------------------------------

--
-- Table structure for table `phieunhap`
--

CREATE TABLE `phieunhap` (
  `MaPN` int(11) NOT NULL,
  `MaNV` int(11) NOT NULL,
  `MaNCC` int(11) NOT NULL,
  `Ngay` timestamp NOT NULL DEFAULT current_timestamp(),
  `ThanhTien` bigint(20) NOT NULL DEFAULT 0,
  `TrangThai` varchar(45) NOT NULL DEFAULT 'chuaxacnhan' COMMENT 'chuaxacnhan - chưa xác nhận\ndaxacnhan - đã xác nhận'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `phieunhap`
--

INSERT INTO `phieunhap` (`MaPN`, `MaNV`, `MaNCC`, `Ngay`, `ThanhTien`, `TrangThai`) VALUES
(2, 1, 1, '2025-10-18 05:42:10', 197980, 'chuaxacnhan'),
(3, 1, 3, '2025-10-19 08:30:00', 450000, 'chuaxacnhan'),
(4, 5, 4, '2025-10-20 14:15:00', 320000, 'chuaxacnhan'),
(5, 1, 5, '2025-10-21 09:45:00', 180000, 'chuaxacnhan'),
(6, 6, 6, '2025-10-22 16:20:00', 250000, 'daxacnhan'),
(7, 1, 7, '2025-10-23 11:10:00', 120000, 'daxacnhan'),
(8, 5, 2, '2025-10-24 13:30:00', 380000, 'chuaxacnhan'),
(9, 2, 1, '2025-10-25 08:15:00', 220000, 'daxacnhan'),
(10, 5, 3, '2025-10-25 10:30:00', 350000, 'chuaxacnhan'),
(11, 6, 4, '2025-10-26 09:00:00', 280000, 'daxacnhan'),
(12, 1, 5, '2025-10-26 14:20:00', 150000, 'chuaxacnhan'),
(13, 2, 6, '2025-10-27 11:45:00', 310000, 'daxacnhan'),
(14, 5, 7, '2025-10-27 15:30:00', 190000, 'chuaxacnhan'),
(15, 6, 1, '2025-10-28 08:00:00', 240000, 'daxacnhan'),
(16, 1, 3, '2025-10-28 13:15:00', 420000, 'chuaxacnhan'),
(17, 2, 4, '2025-10-29 09:30:00', 295000, 'daxacnhan'),
(18, 5, 5, '2025-10-29 16:00:00', 175000, 'chuaxacnhan'),
(19, 1, 3, '2025-11-16 08:55:52', 9000, 'daxacnhan');

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
-- Indexes for table `donhang`
--
ALTER TABLE `donhang`
  ADD PRIMARY KEY (`MaDon`),
  ADD KEY `fk_nhanvien_don` (`MaNV`),
  ADD KEY `fk_donhang_khachhang` (`MaKH`);

--
-- Indexes for table `khachhang`
--
ALTER TABLE `khachhang`
  ADD PRIMARY KEY (`MaKH`),
  ADD UNIQUE KEY `SDT` (`SDT`);

--
-- Indexes for table `khohang`
--
ALTER TABLE `khohang`
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
-- Indexes for table `mon_nguyenlieu`
--
ALTER TABLE `mon_nguyenlieu`
  ADD PRIMARY KEY (`MaMon`,`MaNL`),
  ADD KEY `fk_monnl_mon` (`MaMon`),
  ADD KEY `fk_monnl_nl` (`MaNL`);

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
-- AUTO_INCREMENT for table `donhang`
--
ALTER TABLE `donhang`
  MODIFY `MaDon` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=56;

--
-- AUTO_INCREMENT for table `khachhang`
--
ALTER TABLE `khachhang`
  MODIFY `MaKH` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=42;

--
-- AUTO_INCREMENT for table `loaimon`
--
ALTER TABLE `loaimon`
  MODIFY `MaLoai` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `mon`
--
ALTER TABLE `mon`
  MODIFY `MaMon` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=32;

--
-- AUTO_INCREMENT for table `nguyenlieu`
--
ALTER TABLE `nguyenlieu`
  MODIFY `MaNL` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=17;

--
-- AUTO_INCREMENT for table `nhacungcap`
--
ALTER TABLE `nhacungcap`
  MODIFY `MaNCC` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT for table `nhanvien`
--
ALTER TABLE `nhanvien`
  MODIFY `MaNV` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=29;

--
-- AUTO_INCREMENT for table `phieunhap`
--
ALTER TABLE `phieunhap`
  MODIFY `MaPN` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=20;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `chitietdonhang`
--
ALTER TABLE `chitietdonhang`
  ADD CONSTRAINT `fk_don_chitiet` FOREIGN KEY (`MaDon`) REFERENCES `donhang` (`MaDon`),
  ADD CONSTRAINT `fk_don_mon` FOREIGN KEY (`MaMon`) REFERENCES `mon` (`MaMon`),
  ADD CONSTRAINT `fk_don_topping` FOREIGN KEY (`MaTopping`) REFERENCES `mon` (`MaMon`);

--
-- Constraints for table `chitietnhap_nl`
--
ALTER TABLE `chitietnhap_nl`
  ADD CONSTRAINT `fk_ctnnl_nl` FOREIGN KEY (`MaNL`) REFERENCES `nguyenlieu` (`MaNL`) ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_ctnnl_pn` FOREIGN KEY (`MaPN`) REFERENCES `phieunhap` (`MaPN`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `donhang`
--
ALTER TABLE `donhang`
  ADD CONSTRAINT `fk_donhang_khachhang` FOREIGN KEY (`MaKH`) REFERENCES `khachhang` (`MaKH`) ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_nhanvien_don` FOREIGN KEY (`MaNV`) REFERENCES `nhanvien` (`MaNV`);

--
-- Constraints for table `khohang`
--
ALTER TABLE `khohang`
  ADD CONSTRAINT `fk_kho_nl` FOREIGN KEY (`MaNL`) REFERENCES `nguyenlieu` (`MaNL`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `mon`
--
ALTER TABLE `mon`
  ADD CONSTRAINT `fk_mon_loai` FOREIGN KEY (`MaLoai`) REFERENCES `loaimon` (`MaLoai`);

--
-- Constraints for table `mon_nguyenlieu`
--
ALTER TABLE `mon_nguyenlieu`
  ADD CONSTRAINT `fk_monnl_mon` FOREIGN KEY (`MaMon`) REFERENCES `mon` (`MaMon`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_monnl_nl` FOREIGN KEY (`MaNL`) REFERENCES `nguyenlieu` (`MaNL`) ON DELETE CASCADE ON UPDATE CASCADE;

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
