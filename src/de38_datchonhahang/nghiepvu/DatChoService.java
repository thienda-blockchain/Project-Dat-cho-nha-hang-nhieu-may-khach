package de38_datchonhahang.nghiepvu;

import java.util.List;

import de38_datchonhahang.luutru.LuuTruTrangThai;
import de38_datchonhahang.model.DatCho;
import de38_datchonhahang.model.KetQua;
import de38_datchonhahang.model.KhungGio;

// Phục vụ CN-06 (XEM), CN-07 (DAT), CN-08 (HUY), CN-09 (tỷ lệ lấp đầy) và CN-14 (đồng bộ); mọi phép trả KetQua.
public class DatChoService {

    private final KhoTrangThai kho;
    private final KhoaBan khoa;
    private final LuuTruTrangThai luuTru;

    public DatChoService(KhoTrangThai kho, KhoaBan khoa, LuuTruTrangThai luuTru) {
        throw new UnsupportedOperationException("CN-05: chưa cài đặt");
    }

    public KetQua<List<String>> xem(KhungGio gio) {
        throw new UnsupportedOperationException("CN-06: chưa cài đặt");
    }

    public KetQua<List<String>> xemTatCa() {
        throw new UnsupportedOperationException("CN-06: chưa cài đặt");
    }

    public KetQua<DatCho> dat(String maBan, KhungGio gio, int soKhach, String tenKhach) {
        throw new UnsupportedOperationException("CN-07: chưa cài đặt");
    }

    public KetQua<DatCho> huy(String maDat, String tenKhach) {
        throw new UnsupportedOperationException("CN-08: chưa cài đặt");
    }

    public KetQua<Double> tyLeLapDay(KhungGio gio) {
        throw new UnsupportedOperationException("CN-09: chưa cài đặt");
    }

    public KetQua<Double> tyLeLapDayCaNgay() {
        throw new UnsupportedOperationException("CN-09: chưa cài đặt");
    }
}
