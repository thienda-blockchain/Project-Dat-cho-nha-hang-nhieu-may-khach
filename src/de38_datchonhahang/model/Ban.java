package de38_datchonhahang.model;

import java.io.Serializable;

// Phục vụ CN-02: mô hình dữ liệu bất biến của một bàn (mã bàn, sức chứa).
public record Ban(String ma, int sucChua) implements Serializable {

    public Ban {
        if (ma == null || ma.isBlank()) {
            throw new IllegalArgumentException("Mã bàn không được null, rỗng hoặc chỉ chứa khoảng trắng");
        }
        if (sucChua <= 0) {
            throw new IllegalArgumentException("Sức chứa phải lớn hơn 0: " + sucChua);
        }
    }
}
