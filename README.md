# Đ38: Đặt chỗ nhà hàng nhiều máy khách

Bài tập lớn học phần AJP201 – Lập trình Java nâng cao (Khoa CNTT, ĐH Kiến trúc Đà Nẵng).

Ứng dụng mạng TCP: một máy chủ đa luồng cho nhiều khách cùng xem, đặt và huỷ bàn theo khung giờ. Hai khách đặt cùng một bàn cùng một giờ thì chỉ một người được. Lệnh: `XEM`, `DAT`, `HUY`.

- Đề bài: đề 38 (Nhóm E) trong `de-tai-45.pdf` của học phần.
- Bảng chức năng: [docs/danh-sach-chuc-nang.md](docs/danh-sach-chuc-nang.md).
- Sơ đồ (phân rã, kiến trúc tầng, lớp, tuần tự, lưu đồ): [docs/so-do/](docs/so-do/).
- Java 21, chỉ thư viện chuẩn; thêm JUnit 5 (jar trong `lib/`), chỉ dùng cho test.

**Tình trạng:** mới có bộ khung. Các lớp đã khai báo trường và chữ ký hàm; thân hàm ném `UnsupportedOperationException("CN-xx: chưa cài đặt")`, với CN-xx là mã chức năng trong bảng.

## Cây thư mục

```
.
├── .project, .classpath, .settings/   dự án Eclipse: nguồn src/ và test/, jar JUnit trong lib/, đầu ra bin/ (test: bin-test/), JavaSE-21, mã hoá UTF-8
├── .gitignore
├── README.md
├── CLAUDE.md                   quy ước làm việc với Claude
├── data/
│   ├── ban.csv                 bàn mẫu: tiêu đề ma,sucChua và 8 bàn
│   └── khung-gio.csv           khung giờ mẫu: tiêu đề gioBatDau và 4 khung giờ
├── docs/
│   ├── danh-sach-chuc-nang.md
│   └── so-do/                  9 sơ đồ .drawio.png, mở sửa được bằng draw.io
├── lib/
│   └── junit-platform-console-standalone-1.13.4.jar   JUnit 5 (tải một lần, xem mục Chạy test)
├── src/
│   └── de38_datchonhahang/
│       ├── model/              dữ liệu dùng chung, không phụ thuộc tầng nào
│       │   ├── Ban.java            record: mã bàn, sức chứa
│       │   ├── KhungGio.java       record: giờ bắt đầu, so sánh được
│       │   ├── DatCho.java         record: mã đặt, tên khách, bàn, khung giờ, số khách
│       │   ├── TrangThaiBan.java   enum TRONG / DA_DAT
│       │   ├── MaLoi.java          enum mã lỗi nghiệp vụ
│       │   └── KetQua.java         sealed interface: kiểu kết quả duy nhất, kèm ThanhCong và ThatBai
│       ├── nghiepvu/           không import mang, giaodien; không dùng java.net
│       │   ├── DatChoService.java  xem, đặt, huỷ, tỷ lệ lấp đầy
│       │   ├── KhoTrangThai.java   Map<KhungGio, Set<Ban>> và bảng mã đặt
│       │   └── KhoaBan.java        một ReentrantLock cho mỗi bàn
│       ├── luutru/
│       │   ├── CauHinhLoader.java      đọc ban.csv, khung-gio.csv
│       │   ├── LuuTruTrangThai.java    tuần tự hoá trạng thái
│       │   └── LuuTruException.java    ngoại lệ riêng cho lỗi tệp
│       ├── mang/
│       │   ├── DatChoServer.java       ServerSocket, bể luồng, hàm main
│       │   ├── ClientHandler.java      phục vụ một kết nối
│       │   ├── BoXuLyLenh.java         phân tích lệnh, định dạng phản hồi
│       │   └── LoaiLenh.java           enum XEM / DAT / HUY
│       └── giaodien/
│           └── DatChoClient.java       máy khách console, có hàm main
└── test/                       phép thử JUnit 5, cùng cây package với src/
    └── de38_datchonhahang/model/KetQuaTest.java   test mẫu: Ban, KetQua
```

`bin/` là đầu ra khi biên dịch, không phải mã nguồn.

**Kiểu kết quả dùng chung:** mọi phép trong `DatChoService` trả `KetQua<T>`, gồm `ThanhCong<T>(giaTri)` hoặc `ThatBai<T>(MaLoi, thongBao)`. Lỗi nghiệp vụ không ném ngoại lệ. Lỗi tệp là `LuuTruException`, tầng nghiệp vụ đổi thành `ThatBai(LOI_HE_THONG)`.

## Yêu cầu

- JDK 21 trở lên. Lệnh biên dịch luôn có `--release 21`, nên máy có JDK mới hơn (như JDK 25) vẫn cho ra mã Java 21.
- Mở bằng Eclipse: File → Import → Existing Projects into Workspace. Nếu Eclipse báo thiếu `JavaSE-21`, vào Window → Preferences → Java → Installed JREs, thêm một JDK từ 21 trở lên, rồi kiểm tra ở Execution Environments.

## Biên dịch và chạy (từ thư mục gốc dự án)

PowerShell:

```
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
javac --release 21 -encoding UTF-8 -d bin (Get-ChildItem src -Recurse -Filter *.java).FullName
# máy chủ: tệp bàn, tệp khung giờ, tệp trạng thái, cổng
java "-Dstdout.encoding=UTF-8" "-Dstderr.encoding=UTF-8" -cp bin de38_datchonhahang.mang.DatChoServer data/ban.csv data/khung-gio.csv data/trang-thai.dat 5000
# máy khách (cửa sổ khác hoặc máy khác): IP máy chủ, cổng
java "-Dstdout.encoding=UTF-8" "-Dstderr.encoding=UTF-8" -cp bin de38_datchonhahang.giaodien.DatChoClient localhost 5000
```

cmd:

```
chcp 65001
dir /s /b src\*.java > sources.txt
javac --release 21 -encoding UTF-8 -d bin @sources.txt
java -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -cp bin de38_datchonhahang.mang.DatChoServer data/ban.csv data/khung-gio.csv data/trang-thai.dat 5000
java -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -cp bin de38_datchonhahang.giaodien.DatChoClient localhost 5000
```

Linux / macOS:

```
find src -name '*.java' > sources.txt
javac --release 21 -encoding UTF-8 -d bin @sources.txt
java -cp bin de38_datchonhahang.mang.DatChoServer data/ban.csv data/khung-gio.csv data/trang-thai.dat 5000
java -cp bin de38_datchonhahang.giaodien.DatChoClient localhost 5000
```

Dòng đầu và hai tham số `-D...encoding=UTF-8` giúp console Windows in đúng tiếng Việt có dấu. Trong PowerShell phải giữ ngoặc kép quanh `-D...`, nếu không PowerShell tách sai tham số. Khi chạy trên hai máy, thay `localhost` bằng IP của máy chủ trong mạng nội bộ.

Các tham số của máy chủ (tệp bàn, khung giờ, trạng thái, cổng) là dự kiến, chốt khi làm CN-04, CN-11, CN-16. Hiện cả hai lệnh `java` đều dừng với `UnsupportedOperationException` (CN-11 và CN-12 chưa làm).

## Đóng gói jar

```
jar --create --file de38.jar --main-class de38_datchonhahang.mang.DatChoServer -C bin .
java -jar de38.jar data/ban.csv data/khung-gio.csv data/trang-thai.dat 5000
```

## Chạy test

JUnit 5 dùng bản đóng gói sẵn `junit-platform-console-standalone` (dòng 1.x tương ứng JUnit 5.13), đặt trong `lib/`. Làm theo thứ tự: tải jar (một lần), biên dịch mã chính vào `bin/` (mục Biên dịch ở trên), biên dịch test vào `bin-test/`, rồi chạy.

Tải jar một lần. PowerShell:

```
Invoke-WebRequest -Uri https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.13.4/junit-platform-console-standalone-1.13.4.jar -OutFile lib/junit-platform-console-standalone-1.13.4.jar
```

cmd, Linux, macOS (`curl` có sẵn trên Windows 10 trở lên):

```
curl -L -o lib/junit-platform-console-standalone-1.13.4.jar https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.13.4/junit-platform-console-standalone-1.13.4.jar
```

Biên dịch và chạy test. PowerShell:

```
javac --release 21 -encoding UTF-8 -cp "bin;lib/junit-platform-console-standalone-1.13.4.jar" -d bin-test (Get-ChildItem test -Recurse -Filter *.java).FullName
java "-Dstdout.encoding=UTF-8" -jar lib/junit-platform-console-standalone-1.13.4.jar execute --class-path "bin;bin-test" --scan-class-path
```

cmd:

```
dir /s /b test\*.java > tests.txt
javac --release 21 -encoding UTF-8 -cp "bin;lib/junit-platform-console-standalone-1.13.4.jar" -d bin-test @tests.txt
java -Dstdout.encoding=UTF-8 -jar lib/junit-platform-console-standalone-1.13.4.jar execute --class-path "bin;bin-test" --scan-class-path
```

Linux / macOS (dấu phân cách classpath là `:`):

```
find test -name '*.java' > tests.txt
javac --release 21 -encoding UTF-8 -cp "bin:lib/junit-platform-console-standalone-1.13.4.jar" -d bin-test @tests.txt
java -jar lib/junit-platform-console-standalone-1.13.4.jar execute --class-path "bin:bin-test" --scan-class-path
```

Kết quả mong đợi với test mẫu: 3 test đạt, 0 test lỗi. Test mẫu chỉ kiểm tra `Ban` và `KetQua`; các chức năng khác còn là khung nên chưa có test.

Trong Eclipse: chuột phải `KetQuaTest.java` → Run As → JUnit Test. Cần tải jar vào `lib/` trước, nếu không Eclipse báo thiếu thư viện.

Muốn dùng bản JUnit mới hơn thì đổi số phiên bản trong tên tệp jar, trong `.classpath` và trong các lệnh trên (dòng 1.x là JUnit 5; dòng 6.x là JUnit 6).
