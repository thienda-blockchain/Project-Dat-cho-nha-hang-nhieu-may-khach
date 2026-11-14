package de38_datchonhahang.model;

import java.io.Serializable;

// Phục vụ CN-02: khung giờ đặt chỗ, sắp xếp được để làm khoá TreeMap.
public record KhungGio(int gioBatDau) implements Serializable, Comparable<KhungGio> {

    @Override
    public int compareTo(KhungGio khac) {
        throw new UnsupportedOperationException("CN-02: chưa cài đặt");
    }
}
