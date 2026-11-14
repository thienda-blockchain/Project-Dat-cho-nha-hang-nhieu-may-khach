package de38_datchonhahang.luutru;

// Phục vụ CN-03, CN-16: ngoại lệ tự định nghĩa (checked) cho lỗi đọc ghi tệp cấu hình và trạng thái.
public class LuuTruException extends Exception {

    private static final long serialVersionUID = 1L;

    public LuuTruException(String thongBao) {
        super(thongBao);
    }

    public LuuTruException(String thongBao, Throwable nguyenNhan) {
        super(thongBao, nguyenNhan);
    }
}
