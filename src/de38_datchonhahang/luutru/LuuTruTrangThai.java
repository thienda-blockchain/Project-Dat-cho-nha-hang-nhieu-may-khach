package de38_datchonhahang.luutru;

import java.util.List;

import de38_datchonhahang.model.DatCho;

// Phục vụ CN-16: tuần tự hoá, lưu danh sách đặt chỗ ra tệp và nạp lại khi khởi động.
public class LuuTruTrangThai {

    private final String duongDan;

    public LuuTruTrangThai(String duongDan) {
        throw new UnsupportedOperationException("CN-16: chưa cài đặt");
    }

    public void luu(List<DatCho> danhSach) throws LuuTruException {
        throw new UnsupportedOperationException("CN-16: chưa cài đặt");
    }

    public List<DatCho> nap() throws LuuTruException {
        throw new UnsupportedOperationException("CN-16: chưa cài đặt");
    }
}
