package de38_datchonhahang.nghiepvu;

import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import de38_datchonhahang.model.Ban;

// Phục vụ CN-14: mỗi bàn một ReentrantLock, tạo một lần lúc khởi động.
public class KhoaBan {

    private final Map<String, ReentrantLock> khoa;

    public KhoaBan(List<Ban> tatCaBan) {
        throw new UnsupportedOperationException("CN-14: chưa cài đặt");
    }

    public ReentrantLock lay(String maBan) {
        throw new UnsupportedOperationException("CN-14: chưa cài đặt");
    }
}
