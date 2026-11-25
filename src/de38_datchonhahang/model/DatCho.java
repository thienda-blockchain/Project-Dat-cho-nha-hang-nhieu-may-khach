package de38_datchonhahang.model;

import java.io.Serializable;

// Phục vụ CN-02, CN-07: một lượt đặt chỗ (mã đặt, tên khách, bàn, khung giờ, số khách).
public record DatCho(String maDat, String tenKhach, Ban ban, KhungGio gio, int soKhach) implements Serializable {

    public DatCho {
        if (maDat == null || maDat.isBlank()) {
            throw new IllegalArgumentException("Mã đặt chỗ không được null, rỗng hoặc chỉ chứa khoảng trắng");
        }

        if (tenKhach == null || tenKhach.isBlank()) {
            throw new IllegalArgumentException("Tên khách không được null, rỗng hoặc chỉ chứa khoảng trắng");
        }

        if (ban == null) {
            throw new IllegalArgumentException("Bàn không được null");
        }

        if (gio == null) {
            throw new IllegalArgumentException("Khung giờ không được null");
        }

        if (soKhach <= 0) {
            throw new IllegalArgumentException("Số khách phải lớn hơn 0: " + soKhach);
        }
    }
}
