package de38_datchonhahang.mang;

import java.util.concurrent.ExecutorService;

// Phục vụ CN-11 (máy chủ một khách) và CN-13 (nhiều khách qua bể luồng); cổng và cỡ bể đặt ở một chỗ.
public class DatChoServer {

    public static final int CONG_MAC_DINH = 5000;
    public static final int SO_LUONG_LUONG = 20;

    private final int cong;
    private final ExecutorService pool;
    private final BoXuLyLenh boXuLy;

    public DatChoServer(int cong, BoXuLyLenh boXuLy) {
        throw new UnsupportedOperationException("CN-11: chưa cài đặt");
    }

    public static void main(String[] args) {
        throw new UnsupportedOperationException("CN-11: chưa cài đặt");
    }

    public void chay() {
        throw new UnsupportedOperationException("CN-13: chưa cài đặt");
    }

    public void dung() {
        throw new UnsupportedOperationException("CN-13: chưa cài đặt");
    }
}
