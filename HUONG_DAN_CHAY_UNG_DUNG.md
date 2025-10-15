# Hướng dẫn các vị trí
```
Vị trí Runner: src->main->Runner
Vị trí thư viện: src->resources->library
Vị trí file sql: src->resources->database
```

# Hướng dẫn chạy ứng dụng Quản lý quán trà sữa bằng tiếng Việt trong terminal
### Phương pháp 1: 
```
settings ->
time & language ->
language & legion ->
administrative language settings ->
change system locate ->
Vietnamese + bật beta
```

### Phương pháp 2:
```
control panel ->
clock and region ->
region ->
administrative ->
additional settings ->
change system locate ->
Vietnamese + bật beta
```

### Phương pháp 3: Sử dụng Command Prompt với UTF-8
```cmd
chcp 65001
javac -cp "lib/mysql-connector-j-9.4.0.jar" -encoding UTF-8 *.java dao/*.java dto/*.java db/*.java view/*.java
java -cp ".;lib/mysql-connector-j-9.4.0.jar" -Dfile.encoding=UTF-8 Runner
```

### Phương pháp 4: Sử dụng PowerShell
```powershell
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
javac -cp "lib/mysql-connector-j-9.4.0.jar" -encoding UTF-8 *.java dao/*.java dto/*.java db/*.java view/*.java
java -cp ".;lib/mysql-connector-j-9.4.0.jar" -Dfile.encoding=UTF-8 Runner
```

### Phương pháp 5: Sử dụng IDE (IntelliJ IDEA, Eclipse, VS Code)
1. Mở project trong IDE
2. Đảm bảo project encoding được set thành UTF-8
3. Chạy file Runner.java

## Lưu ý quan trọng
- Đảm bảo MySQL server đang chạy trên localhost:3306
- Database tên "bants" phải tồn tại
- Username: root, Password: (để trống)

## Nếu vẫn gặp lỗi encoding
1. Kiểm tra font của terminal có hỗ trợ tiếng Việt
2. Thử chạy trong Command Prompt thay vì PowerShell
3. Đảm bảo file .java được lưu với encoding UTF-8
