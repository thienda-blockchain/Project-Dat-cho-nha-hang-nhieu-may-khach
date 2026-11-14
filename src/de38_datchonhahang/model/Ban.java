package de38_datchonhahang.model;

import java.io.Serializable;

// Phục vụ CN-02: mô hình dữ liệu bất biến của một bàn (mã bàn, sức chứa).
public record Ban(String ma, int sucChua) implements Serializable {
}
