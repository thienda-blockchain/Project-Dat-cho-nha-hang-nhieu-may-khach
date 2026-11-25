package de38_datchonhahang.model;

import java.io.Serializable;

// Phục vụ CN-02: khung giờ đặt chỗ, sắp xếp được để làm khoá TreeMap.
public record KhungGio(int gioBatDau) implements Serializable, Comparable<KhungGio> {

    public KhungGio {
        if (gioBatDau < 0 || gioBatDau > 23) {
            throw new IllegalArgumentException("Giờ bắt đầu phải trong khoảng 0 đến 23: " + gioBatDau);
        }
    }

    @Override
    public int compareTo(KhungGio khac) {
        // khac == null thì truy cập khac.gioBatDau ném NullPointerException, đúng hợp đồng Comparable.
        return Integer.compare(gioBatDau, khac.gioBatDau);
    }
}
