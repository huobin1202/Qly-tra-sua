<!-- author: hgbaodev -->
# Quản lý quán trà sữa
Đồ án môn Phân tích thiết kế hệ thống thông tin
# Thành viên trong nhóm
| Họ Tên  | MSSV | % Công việc |
| ------------- | ------------- | ------------- | 
| Hồ Phạm Hữu Bình  | 3123411033  | |
| Nguyễn Văn Phát  | 3123411220  | |
| Nguyễn Minh  | 3123411---  | |
| Võ Lê Chí Dũng  | 3123411---  | |

## Getting Started

1. Tải source code về:

   ```bash
   git clone https://github.com/huobin1202/Qly-tra-sua.git
   ```
2. Mở xampp và vào trang http://localhost/phpmyadmin/ tạo 1 database mới có tên là bants và import cơ sở dữ liệu trong folder db trong source code.

### Chạy giao diện GUI (Swing)

- Yêu cầu Java 8+ và MySQL đang chạy, cấu hình trong `db/DBUtil.java`.
- Chạy ứng dụng với tham số `--gui` để mở giao diện giống ảnh minh họa.

Ví dụ (VS Code/Terminal):

```bash
java Runner --gui
```

- Đăng nhập bằng tài khoản trong bảng `nhanvien`.
- Màn hình `Quản lý hàng hóa` hỗ trợ: tìm kiếm, thêm, sửa, xóa, đồng bộ danh sách từ DB.

### Xây dựng hệ thống quản lý quán trà sữa, hỗ trợ:

    Quản lý hàng hóa: món, loại món
    Quản lý đặt hàng: quản lý bàn, đơn đặt hàng, quản lý giao hàng
    Quản lý nhập/xuất hàng, bán hàng
    Quản lý nhà cung cấp
    Quản lý khách hàng, nhân viên
    Quản lý tài khoản (login, phân quyền) 
    Thống kê từng mục



