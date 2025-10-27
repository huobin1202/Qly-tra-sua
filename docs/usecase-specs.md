# Use Case Diagram - Hệ thống quản lý trà sữa

## Tổng quan
Biểu đồ use case mô tả các tác nhân và các chức năng chính của hệ thống quản lý cửa hàng trà sữa.

## Các tác nhân (Actors)

### 1. Admin (Quản trị viên)
- **Mô tả**: Người có quyền cao nhất trong hệ thống
- **Quyền hạn**: Quản lý toàn bộ hệ thống, bao gồm tất cả các chức năng
- **Đặc điểm**: Có thể thực hiện tất cả các use case

### 2. Nhân viên bán hàng
- **Mô tả**: Nhân viên phục vụ khách hàng tại quầy
- **Quyền hạn**: Quản lý đơn hàng, khách hàng, giao hàng
- **Đặc điểm**: Không thể quản lý nhân viên và nhập hàng

### 3. Nhân viên kho
- **Mô tả**: Nhân viên quản lý kho hàng và nhập hàng
- **Quyền hạn**: Quản lý hàng hóa, nhà cung cấp, phiếu nhập, kho hàng
- **Đặc điểm**: Không thể quản lý đơn hàng và khách hàng

### 4. Quản lý
- **Mô tả**: Người quản lý cửa hàng
- **Quyền hạn**: Xem báo cáo, thống kê, quản lý nhân viên
- **Đặc điểm**: Có quyền xem và quản lý hầu hết các chức năng

## Các Use Case chính

### UC01: Quản lý hàng hóa
- **Mô tả**: Quản lý các loại món, món ăn và nguyên liệu
- **Tác nhân chính**: Admin, Nhân viên kho
- **Tác nhân phụ**: Không
- **Tiền điều kiện**: Đã đăng nhập hệ thống
- **Hậu điều kiện**: Dữ liệu hàng hóa được cập nhật
- **Luồng chính**:
  1. Chọn loại hàng hóa cần quản lý (Loại món/Món/Nguyên liệu)
  2. Thực hiện các thao tác CRUD
  3. Hệ thống cập nhật dữ liệu
- **Luồng thay thế**:
  - 2a. Dữ liệu không hợp lệ: Hiển thị thông báo lỗi
  - 2b. Xóa món đang được sử dụng: Không cho phép xóa

### UC02: Quản lý khách hàng
- **Mô tả**: Quản lý thông tin khách hàng
- **Tác nhân chính**: Admin, Nhân viên bán hàng
- **Tác nhân phụ**: Không
- **Tiền điều kiện**: Đã đăng nhập hệ thống
- **Hậu điều kiện**: Thông tin khách hàng được cập nhật
- **Luồng chính**:
  1. Xem danh sách khách hàng
  2. Thêm/sửa/xóa thông tin khách hàng
  3. Tìm kiếm khách hàng theo tiêu chí
  4. Hệ thống lưu thông tin

### UC03: Quản lý nhân viên
- **Mô tả**: Quản lý thông tin nhân viên và phân quyền
- **Tác nhân chính**: Admin, Quản lý
- **Tác nhân phụ**: Không
- **Tiền điều kiện**: Đã đăng nhập với quyền Admin/Quản lý
- **Hậu điều kiện**: Thông tin nhân viên được cập nhật
- **Luồng chính**:
  1. Xem danh sách nhân viên
  2. Thêm nhân viên mới với tài khoản
  3. Sửa thông tin nhân viên
  4. Phân quyền cho nhân viên
  5. Hệ thống cập nhật thông tin

### UC04: Quản lý đơn hàng
- **Mô tả**: Tạo, sửa, xóa, xem và quản lý đơn hàng
- **Tác nhân chính**: Admin, Nhân viên bán hàng
- **Tác nhân phụ**: Không
- **Tiền điều kiện**: Đã đăng nhập hệ thống
- **Hậu điều kiện**: Đơn hàng được lưu/cập nhật trạng thái
- **Luồng chính**:
  1. Tạo đơn hàng mới
  2. Thêm món và topping vào đơn
  3. Tính tổng tiền
  4. Xác nhận thanh toán
  5. Hệ thống cập nhật trạng thái đơn hàng
- **Luồng thay thế**:
  - 2a. Món không tồn tại: Hiển thị thông báo lỗi
  - 4a. Hủy đơn hàng: Cập nhật trạng thái "đã hủy"

### UC05: Quản lý giao hàng
- **Mô tả**: Quản lý việc giao hàng cho khách hàng
- **Tác nhân chính**: Admin, Nhân viên bán hàng
- **Tác nhân phụ**: Không
- **Tiền điều kiện**: Có đơn hàng cần giao
- **Hậu điều kiện**: Thông tin giao hàng được cập nhật
- **Luồng chính**:
  1. Xem danh sách đơn hàng cần giao
  2. Phân công nhân viên giao hàng
  3. Cập nhật trạng thái giao hàng
  4. Hệ thống lưu thông tin giao hàng

### UC06: Quản lý nhà cung cấp
- **Mô tả**: Quản lý thông tin nhà cung cấp
- **Tác nhân chính**: Admin, Nhân viên kho
- **Tác nhân phụ**: Không
- **Tiền điều kiện**: Đã đăng nhập hệ thống
- **Hậu điều kiện**: Thông tin nhà cung cấp được cập nhật
- **Luồng chính**:
  1. Xem danh sách nhà cung cấp
  2. Thêm/sửa/xóa thông tin nhà cung cấp
  3. Quản lý nguyên liệu của nhà cung cấp
  4. Hệ thống lưu thông tin

### UC07: Quản lý phiếu nhập
- **Mô tả**: Tạo và quản lý phiếu nhập nguyên liệu
- **Tác nhân chính**: Admin, Nhân viên kho
- **Tác nhân phụ**: Không
- **Tiền điều kiện**: Đã đăng nhập với quyền quản lý kho
- **Hậu điều kiện**: Phiếu nhập được tạo và tồn kho được cập nhật
- **Luồng chính**:
  1. Tạo phiếu nhập mới
  2. Chọn nhà cung cấp
  3. Thêm chi tiết nguyên liệu
  4. Xác nhận phiếu nhập
  5. Hệ thống cập nhật tồn kho
- **Luồng thay thế**:
  - 3a. Nguyên liệu không đủ: Hiển thị cảnh báo
  - 4a. Hủy phiếu nhập: Hoàn trả số lượng về nhà cung cấp

### UC08: Quản lý kho hàng
- **Mô tả**: Xem và quản lý tồn kho nguyên liệu
- **Tác nhân chính**: Admin, Nhân viên kho
- **Tác nhân phụ**: Không
- **Tiền điều kiện**: Đã đăng nhập hệ thống
- **Hậu điều kiện**: Thông tin tồn kho được hiển thị
- **Luồng chính**:
  1. Xem tồn kho nguyên liệu
  2. Kiểm tra số lượng tồn kho
  3. Cảnh báo khi hết hàng
  4. Cập nhật tồn kho khi có phiếu nhập

### UC09: Thống kê và báo cáo
- **Mô tả**: Xem các báo cáo thống kê về doanh thu, sản phẩm
- **Tác nhân chính**: Admin, Quản lý
- **Tác nhân phụ**: Không
- **Tiền điều kiện**: Đã đăng nhập với quyền xem báo cáo
- **Hậu điều kiện**: Báo cáo được hiển thị
- **Luồng chính**:
  1. Chọn loại báo cáo
  2. Chọn khoảng thời gian
  3. Hệ thống tạo báo cáo
  4. Hiển thị kết quả thống kê

### UC10: Đăng nhập hệ thống
- **Mô tả**: Xác thực người dùng vào hệ thống
- **Tác nhân chính**: Tất cả người dùng
- **Tác nhân phụ**: Không
- **Tiền điều kiện**: Có tài khoản hợp lệ
- **Hậu điều kiện**: Người dùng được xác thực và phân quyền
- **Luồng chính**:
  1. Nhập tài khoản và mật khẩu
  2. Hệ thống xác thực thông tin
  3. Phân quyền theo chức vụ
  4. Hiển thị giao diện phù hợp
- **Luồng thay thế**:
  - 2a. Thông tin không đúng: Hiển thị thông báo lỗi

## Mối quan hệ giữa các Use Case

### Include (Bao gồm)
- **UC04** include **UC10**: Quản lý đơn hàng bao gồm đăng nhập
- **UC07** include **UC10**: Quản lý phiếu nhập bao gồm đăng nhập
- **UC09** include **UC10**: Thống kê bao gồm đăng nhập

### Extend (Mở rộng)
- **UC08** extend **UC07**: Quản lý kho hàng mở rộng từ phiếu nhập
- **UC05** extend **UC04**: Quản lý giao hàng mở rộng từ đơn hàng

### Generalization (Kế thừa)
- **UC01** ← **UC01a** (Quản lý loại món)
- **UC01** ← **UC01b** (Quản lý món)
- **UC01** ← **UC01c** (Quản lý nguyên liệu)

## Ma trận phân quyền

| Use Case | Admin | Quản lý | Nhân viên bán hàng | Nhân viên kho |
|----------|-------|---------|-------------------|---------------|
| UC01: Quản lý hàng hóa | ✓ | ✓ | ✗ | ✓ |
| UC02: Quản lý khách hàng | ✓ | ✓ | ✓ | ✗ |
| UC03: Quản lý nhân viên | ✓ | ✓ | ✗ | ✗ |
| UC04: Quản lý đơn hàng | ✓ | ✓ | ✓ | ✗ |
| UC05: Quản lý giao hàng | ✓ | ✓ | ✓ | ✗ |
| UC06: Quản lý nhà cung cấp | ✓ | ✓ | ✗ | ✓ |
| UC07: Quản lý phiếu nhập | ✓ | ✓ | ✗ | ✓ |
| UC08: Quản lý kho hàng | ✓ | ✓ | ✗ | ✓ |
| UC09: Thống kê và báo cáo | ✓ | ✓ | ✗ | ✗ |
| UC10: Đăng nhập hệ thống | ✓ | ✓ | ✓ | ✓ |

**Chú thích**: ✓ = Có quyền, ✗ = Không có quyền