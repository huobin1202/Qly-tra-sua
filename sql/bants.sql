-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Oct 06, 2025 at 06:30 PM
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
-- Table structure for table `ban`
--


--
-- Dumping data for table `ban`
--
CREATE TABLE `ban` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `tenban` VARCHAR(50) NOT NULL,
  `trangthai` VARCHAR(50) NOT NULL
);

--
INSERT INTO `ban` (`id`, `tenban`, `trangthai`) VALUES
(1, 'Bàn 1', 'Trống'),
(2, 'Bàn 2', 'Đang sử dụng');

-- --------------------------------------------------------

--
-- Table structure for table `customers`
--

CREATE TABLE `customers` (
  `id` int(11) NOT NULL,
  `ten` varchar(100) NOT NULL,
  `sdt` varchar(20) NOT NULL,
  `diachi` varchar(255) NOT NULL,
  `ngaysinh` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `customers`
--

INSERT INTO `customers` (`id`, `ten`, `sdt`, `diachi`, `ngaysinh`) VALUES
(1, 'Nguyễn Văn A', '0901234567', 'Con cu bebe', '2025-10-08'),
(2, 'Trần Thị B', '0912345678', '', NULL),
(3, '2', '6', '', NULL),
(4, 'binh', '345', '', NULL),
(5, 'binh', '1254', '', NULL),
(6, 'binh', '25', '', NULL);

-- --------------------------------------------------------

--
-- Table structure for table `dondathang`
--

CREATE TABLE `dondathang` (
  `id` int(11) NOT NULL,
  `nguoilap` varchar(50) DEFAULT NULL,
  `ban_id` int(11) DEFAULT NULL,
  `trangthai` varchar(50) DEFAULT NULL,
  `ngaylap` date DEFAULT NULL,
  `ngaythanhtoan` date DEFAULT NULL,
  `dathanhtoan` double DEFAULT NULL,
  `tongtien` double DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `dondathang`
--

INSERT INTO `dondathang` (`id`, `nguoilap`, `ban_id`, `trangthai`, `ngaylap`, `ngaythanhtoan`, `dathanhtoan`, `tongtien`) VALUES
(1, 'admin', 1, 'Đã thanh toán', '2024-10-01', '2024-10-01', 50000, 50000),
(2, 'nhanvien1', 2, 'Chưa thanh toán', '2024-10-02', NULL, 0, 30000);

-- --------------------------------------------------------

--
-- Table structure for table `hoadon`
--

CREATE TABLE `hoadon` (
  `id` int(11) NOT NULL,
  `customer_id` int(11) DEFAULT NULL,
  `drink_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `hoadon`
--

INSERT INTO `hoadon` (`id`, `customer_id`, `drink_id`) VALUES
(1, 1, 1),
(2, 2, 3);

-- --------------------------------------------------------

--
-- Table structure for table `loaimon`
--

CREATE TABLE `loaimon` (
  `ma` int(11) NOT NULL,
  `ten` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `loaimon`
--

INSERT INTO `loaimon` (`ma`, `ten`) VALUES
(1, 'Đồ ăn'),
(2, 'Trà sữa'),
(3, 'PO'),
(4, 'Topping');

-- --------------------------------------------------------

--
-- Table structure for table `mon`
--

CREATE TABLE `mon` (
  `id` int(11) NOT NULL,
  `ten` varchar(100) NOT NULL,
  `mota` text DEFAULT NULL,
  `anh` varchar(255) DEFAULT NULL,
  `tendv` varchar(50) DEFAULT NULL,
  `gia` int(11) NOT NULL,
  `dv` varchar(20) DEFAULT NULL,
  `ma_loai` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `mon`
--

INSERT INTO `mon` (`id`, `ten`, `mota`, `anh`, `tendv`, `gia`, `dv`, `ma_loai`) VALUES
(1, 'Bánh mì', 'Bánh mì thịt', 'banhmi.jpg', 'Cái', 20000, 'cai', 1),
(2, 'Trà sữa truyền thống', 'Trà sữa vị truyền thống', 'trasua.jpg', 'Ly', 25000, 'ly', 2),
(3, 'Cà phê đen', 'Cà phê đen đá', 'capheden.jpg', 'Ly', 18000, 'ly', 3),
(4, 'Trân châu', 'Topping trân châu', 'tranchau.jpg', 'Phần', 5000, 'phan', 4),
(5, '6', '6', '6', '6', 6, '6', 3);

-- --------------------------------------------------------

--
-- Table structure for table `nhanvien`
--

CREATE TABLE `nhanvien` (
  `id` int(11) NOT NULL,
  `tentaikhoan` varchar(50) NOT NULL,
  `matkhau` varchar(100) NOT NULL,
  `sdt` varchar(20) DEFAULT NULL,
  `ngayvaolam` date DEFAULT NULL,
  `chucvu` varchar(50) DEFAULT NULL,
  `luong` double DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `nhanvien`
--

INSERT INTO `nhanvien` (`id`, `tentaikhoan`, `matkhau`, `sdt`, `ngayvaolam`, `chucvu`, `luong`) VALUES
(1, 'admin', '123456', '0909999999', '2023-01-01', 'quanly', 15000000),
(2, 'nhanvien1', '123456', '0911111111', '2024-06-01', 'nhanvien', 7000000);

-- --------------------------------------------------------

-- Table structure for table `nhacungcap`

CREATE TABLE IF NOT EXISTS `nhacungcap` (
  `id` int(11) NOT NULL,
  `ten` varchar(100) NOT NULL,
  `sdt` varchar(20) DEFAULT NULL,
  `diachi` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Sample data for `nhacungcap`
INSERT INTO `nhacungcap` (`id`, `ten`, `sdt`, `diachi`) VALUES
(1, 'NCC A', '0901111111', 'HCM'),
(2, 'NCC B', '0902222222', 'HN');

-- --------------------------------------------------------

-- Table structure for table `giaohang`

CREATE TABLE IF NOT EXISTS `giaohang` (
  `id` int(11) NOT NULL,
  `dondathang_id` int(11) NOT NULL,
  `tenshipper` varchar(100) DEFAULT NULL,
  `sdtshipper` varchar(20) DEFAULT NULL,
  `phiship` double DEFAULT NULL,
  `trangthai` varchar(50) DEFAULT NULL,
  `ngaybatdau` date DEFAULT NULL,
  `ngayketthuc` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Sample data for `giaohang`
INSERT INTO `giaohang` (`id`, `dondathang_id`, `tenshipper`, `sdtshipper`, `phiship`, `trangthai`, `ngaybatdau`, `ngayketthuc`) VALUES
(1, 1, 'Shipper A', '0903333333', 15000, 'Đang giao', '2024-10-01', NULL);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `ban`
--
ALTER TABLE `ban`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `customers`
--
ALTER TABLE `customers`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `dondathang`
--
ALTER TABLE `dondathang`
  ADD PRIMARY KEY (`id`),
  ADD KEY `ban_id` (`ban_id`);

--
-- Indexes for table `hoadon`
--
ALTER TABLE `hoadon`
  ADD PRIMARY KEY (`id`),
  ADD KEY `customer_id` (`customer_id`),
  ADD KEY `drink_id` (`drink_id`);

--
-- Indexes for table `loaimon`
--
ALTER TABLE `loaimon`
  ADD PRIMARY KEY (`ma`);

--
-- Indexes for table `mon`
--
ALTER TABLE `mon`
  ADD PRIMARY KEY (`id`),
  ADD KEY `ma_loai` (`ma_loai`);

--
-- Indexes for table `nhanvien`
--
ALTER TABLE `nhanvien`
  ADD PRIMARY KEY (`id`);

-- Indexes for table `nhacungcap`
ALTER TABLE `nhacungcap`
  ADD PRIMARY KEY (`id`);

-- Indexes for table `giaohang`
ALTER TABLE `giaohang`
  ADD PRIMARY KEY (`id`),
  ADD KEY `dondathang_id` (`dondathang_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `ban`
--
ALTER TABLE `ban`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `customers`
--
ALTER TABLE `customers`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `dondathang`
--
ALTER TABLE `dondathang`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `hoadon`
--
ALTER TABLE `hoadon`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `loaimon`
--
ALTER TABLE `loaimon`
  MODIFY `ma` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `mon`
--
ALTER TABLE `mon`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `nhanvien`
--
ALTER TABLE `nhanvien`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

-- AUTO_INCREMENT for table `nhacungcap`
ALTER TABLE `nhacungcap`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

-- AUTO_INCREMENT for table `giaohang`
ALTER TABLE `giaohang`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `dondathang`
--
ALTER TABLE `dondathang`
  ADD CONSTRAINT `dondathang_ibfk_1` FOREIGN KEY (`ban_id`) REFERENCES `ban` (`id`);

--
-- Constraints for table `hoadon`
--
ALTER TABLE `hoadon`
  ADD CONSTRAINT `hoadon_ibfk_1` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`id`),
  ADD CONSTRAINT `hoadon_ibfk_2` FOREIGN KEY (`drink_id`) REFERENCES `mon` (`id`);

--
-- Constraints for table `mon`
--
ALTER TABLE `mon`
  ADD CONSTRAINT `mon_ibfk_1` FOREIGN KEY (`ma_loai`) REFERENCES `loaimon` (`ma`);

-- Constraints for table `giaohang`
ALTER TABLE `giaohang`
  ADD CONSTRAINT `giaohang_ibfk_1` FOREIGN KEY (`dondathang_id`) REFERENCES `dondathang` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
