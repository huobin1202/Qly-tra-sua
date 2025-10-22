## Use Case List
- UC01: Quản lý hàng hóa (Loại món, Món, Nguyên liệu)
- UC02: Quản lý khách hàng
- UC03: Quản lý nhân viên
- UC04: Quản lý đơn hàng
- UC05: Quản lý giao hàng
- UC06: Quản lý nhà cung cấp
- UC07: Quản lý phiếu nhập

## UC04: Quản lý đơn hàng (Đặc tả)
- Mô tả: Nhân viên tạo, sửa, xóa, xem, tìm kiếm đơn hàng và xác nhận thanh toán.
- Tác nhân chính: Nhân viên
- Tiền điều kiện: Nhân viên đã đăng nhập.
- Hậu điều kiện: Đơn hàng được lưu/ cập nhật trạng thái.
- Luồng chính:
  1. Nhân viên chọn tạo đơn hàng mới.
  2. Hệ thống tạo đơn trống với trạng thái "chưa thanh toán".
  3. Nhân viên thêm món, topping, số lượng, ghi chú.
  4. Hệ thống tính tổng tiền và lưu chi tiết.
  5. Nhân viên xác nhận thanh toán khi khách trả tiền.
  6. Hệ thống cập nhật trạng thái đơn là "đã thanh toán".
- Luồng thay thế:
  - 3a. Mã món không tồn tại: hệ thống báo lỗi, quay lại bước 3.
  - 5a. Hủy thanh toán: giữ trạng thái "chưa thanh toán".

## UC07: Quản lý phiếu nhập (Đặc tả)
- Mô tả: Tạo/sửa/xóa/xác nhận phiếu nhập nguyên liệu từ nhà cung cấp và cập nhật tồn kho.
- Tác nhân chính: Nhân viên kho
- Tiền điều kiện: Nhân viên kho đã đăng nhập; nhà cung cấp, nhân viên tồn tại.
- Hậu điều kiện: Phiếu nhập được lưu, tồn kho và số lượng NCC được cập nhật phù hợp.
- Luồng chính:
  1. Nhân viên nhập thông tin phiếu (MaNV, MaNCC, Ngày, Ghi chú).
  2. Hệ thống kiểm tra Nhân viên/NCC tồn tại.
  3. Hệ thống tạo phiếu ở trạng thái "chưa xác nhận".
  4. Nhân viên thêm chi tiết nguyên liệu (MaNL, SL, Đơn giá, Đơn vị).
  5. Hệ thống cập nhật tồn kho, trừ số lượng từ NCC, cộng vào kho, và cộng thành tiền phiếu.
  6. Nhân viên xác nhận phiếu.
  7. Hệ thống đặt trạng thái "đã xác nhận" và chốt số liệu.
- Luồng thay thế:
  - 2a. Không tồn tại Nhân viên/NCC: thông báo lỗi, dừng.
  - 4a. SL yêu cầu > SL có ở NCC: thông báo lỗi, không thêm.
  - 6a. Xóa phiếu khi chưa xác nhận: hoàn trả số lượng về NCC, trừ kho, xóa chi tiết, xóa phiếu.

## UC06: Quản lý nhà cung cấp (Đặc tả)
- Mô tả: Thêm/sửa/xóa/xem/tìm kiếm nhà cung cấp.
- Tác nhân chính: Nhân viên kho/Admin
- Tiền điều kiện: Đã đăng nhập.
- Hậu điều kiện: Dữ liệu NCC được cập nhật.

Các UC khác tương tự: CRUD và tìm kiếm.

