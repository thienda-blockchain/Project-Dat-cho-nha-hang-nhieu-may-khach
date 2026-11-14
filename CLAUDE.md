# CLAUDE.md

Behavioral guidelines to reduce common LLM coding mistakes. Merge with project-specific instructions as needed.

**Tradeoff:** These guidelines bias toward caution over speed. For trivial tasks, use judgment.

## 1. Think Before Coding

**Don't assume. Don't hide confusion. Surface tradeoffs.**

Before implementing:
- State your assumptions explicitly. If uncertain, ask.
- If multiple interpretations exist, present them - don't pick silently.
- If a simpler approach exists, say so. Push back when warranted.
- If something is unclear, stop. Name what's confusing. Ask.

## 2. Simplicity First

**Minimum code that solves the problem. Nothing speculative.**

- No features beyond what was asked.
- No abstractions for single-use code.
- No "flexibility" or "configurability" that wasn't requested.
- No error handling for impossible scenarios.
- If you write 200 lines and it could be 50, rewrite it.

Ask yourself: "Would a senior engineer say this is overcomplicated?" If yes, simplify.

## 3. Surgical Changes

**Touch only what you must. Clean up only your own mess.**

When editing existing code:
- Don't "improve" adjacent code, comments, or formatting.
- Don't refactor things that aren't broken.
- Match existing style, even if you'd do it differently.
- If you notice unrelated dead code, mention it - don't delete it.

When your changes create orphans:
- Remove imports/variables/functions that YOUR changes made unused.
- Don't remove pre-existing dead code unless asked.

The test: Every changed line should trace directly to the user's request.

## 4. Goal-Driven Execution

**Define success criteria. Loop until verified.**

Transform tasks into verifiable goals:
- "Add validation" → "Write tests for invalid inputs, then make them pass"
- "Fix the bug" → "Write a test that reproduces it, then make it pass"
- "Refactor X" → "Ensure tests pass before and after"

For multi-step tasks, state a brief plan:
```
1. [Step] → verify: [check]
2. [Step] → verify: [check]
3. [Step] → verify: [check]
```

Strong success criteria let you loop independently. Weak criteria ("make it work") require constant clarification.

---

**These guidelines are working if:** fewer unnecessary changes in diffs, fewer rewrites due to overcomplication, and clarifying questions come before implementation rather than after mistakes.

## Dự án

### Đề và tài liệu
- Đề 38 "Đặt chỗ nhà hàng nhiều máy khách" (Nhóm E) trong `de-tai-45.pdf`, ở thư mục `Java Nang Cao`, là thư mục cha của dự án (`..\de-tai-45.pdf`).
- Bảng chức năng: `docs/danh-sach-chuc-nang.md` (CN-01 đến CN-19; CN-17, CN-18 là mở rộng). Cột "Trường hợp biên" là những tình huống cần có test; cột "Kỹ thuật Java (chương)" ghi kỹ thuật dùng cho từng dòng.
- Sơ đồ: `docs/so-do/` (phân rã chức năng, kiến trúc các tầng, sơ đồ lớp, 4 sơ đồ tuần tự, 2 lưu đồ). Tệp `.drawio.png` mở sửa được bằng draw.io.

### Package và các tầng (`src/de38_datchonhahang/`)
- `model`: `Ban`, `KhungGio`, `DatCho`, `TrangThaiBan`, `MaLoi`, `KetQua` (kèm `ThanhCong`, `ThatBai`). Không phụ thuộc gói nào.
- `nghiepvu`: `DatChoService` (CN-06 đến CN-09, CN-14), `KhoTrangThai` (CN-05), `KhoaBan` (CN-14).
- `luutru`: `CauHinhLoader` (CN-04), `LuuTruTrangThai` (CN-16), `LuuTruException` (CN-03).
- `mang`: `DatChoServer` (CN-11, CN-13, có hàm `main`), `ClientHandler` (CN-13), `BoXuLyLenh` (CN-10), `LoaiLenh` (CN-10).
- `giaodien`: `DatChoClient` (CN-12, có hàm `main`).
- Hướng phụ thuộc: `mang` và `giaodien` gọi `nghiepvu`; `nghiepvu` gọi `luutru`; `model` dùng chung cho mọi tầng. Tầng nghiệp vụ không phụ thuộc giao diện hay mạng: không import `java.net`, không biết có khách hay socket, nhờ vậy kiểm thử riêng được.
- Hiện các lớp là khung: thân hàm ném `UnsupportedOperationException("CN-xx: chưa cài đặt")`.
- Dữ liệu: cấu hình mẫu ở `data/ban.csv`, `data/khung-gio.csv`.

### Cách diễn đạt thất bại dùng chung
- `sealed interface KetQua<T>` với `ThanhCong<T>(T giaTri)` và `ThatBai<T>(MaLoi maLoi, String thongBao)`.
- Mọi phép trong `DatChoService` trả `KetQua<...>`. Lỗi nghiệp vụ (bàn bận, bàn không tồn tại, quá sức chứa, sai tham số...) là `ThatBai`, không ném ngoại lệ.
- Lỗi tệp là `LuuTruException` (checked); tầng nghiệp vụ đổi thành `ThatBai(LOI_HE_THONG)`.
- Thêm lỗi nghiệp vụ mới thì thêm hằng vào enum `MaLoi`.
- Đọc `KetQua` bằng switch biểu thức khớp mẫu (slide chương 6, cần Java 21).

### Luật code
- Java 21, biên dịch với `--release 21` (máy có JDK 25 vẫn cho ra mã Java 21). Không thêm thư viện nào ngoài JUnit 5 (chỉ dùng cho test, trong `test/`).
- Chỉ dùng kỹ thuật có trong slide các chương đã học: `..\chuong-01-slide.pdf` … `..\chuong-06-slide.pdf`. Cần thứ ngoài slide (ví dụ `ScheduledExecutorService`, `AtomicInteger`, `Semaphore`, `String.split`) thì hỏi trước.
- Đóng tệp, socket, luồng bằng try-with-resources. Không dùng biến toàn cục (trường `static` thay đổi được); hằng `static final` thì được.

### Cách làm việc
- Mỗi lần chỉ làm một chức năng trong bảng (một mã CN): thay đúng các `throw new UnsupportedOperationException(...)` của chức năng đó.
- Không sửa test cho xanh. Test đỏ thì tìm nguyên nhân trong code.
- Trả lời bằng tiếng Việt.

### Lệnh biên dịch và chạy (từ `README.md`, chạy ở thư mục gốc dự án; dự án mở bằng Eclipse, đầu ra ở `bin/`)

PowerShell:

```
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
javac --release 21 -encoding UTF-8 -d bin (Get-ChildItem src -Recurse -Filter *.java).FullName
java "-Dstdout.encoding=UTF-8" "-Dstderr.encoding=UTF-8" -cp bin de38_datchonhahang.mang.DatChoServer data/ban.csv data/khung-gio.csv data/trang-thai.dat 5000
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

Lệnh chạy test thêm khi có JUnit 5; khi đó cập nhật cả `README.md` và mục này.
