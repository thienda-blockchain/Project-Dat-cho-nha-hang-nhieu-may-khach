package de38_datchonhahang.model;

import java.io.Serializable;

// Phục vụ CN-02, CN-07: một lượt đặt chỗ (mã đặt, tên khách, bàn, khung giờ, số khách).
public record DatCho(String maDat, String tenKhach, Ban ban, KhungGio gio, int soKhach) implements Serializable {
}
