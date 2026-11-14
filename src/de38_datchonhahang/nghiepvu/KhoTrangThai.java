package de38_datchonhahang.nghiepvu;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import de38_datchonhahang.model.Ban;
import de38_datchonhahang.model.DatCho;
import de38_datchonhahang.model.KhungGio;

// Phục vụ CN-05: kho trạng thái dùng chung (bàn đã bận theo khung giờ, bảng tra mã đặt, bộ sinh mã đặt).
public class KhoTrangThai {

    private final Map<KhungGio, Set<Ban>> banBan;
    private final Map<String, DatCho> datCho;
    private final List<Ban> tatCaBan;

    public KhoTrangThai(List<Ban> tatCaBan, List<KhungGio> cacKhungGio) {
        throw new UnsupportedOperationException("CN-05: chưa cài đặt");
    }

    public List<Ban> tatCaBan() {
        throw new UnsupportedOperationException("CN-05: chưa cài đặt");
    }

    public List<KhungGio> cacKhungGio() {
        throw new UnsupportedOperationException("CN-05: chưa cài đặt");
    }

    public Optional<Ban> timBan(String maBan) {
        throw new UnsupportedOperationException("CN-05: chưa cài đặt");
    }

    public boolean coKhungGio(KhungGio gio) {
        throw new UnsupportedOperationException("CN-05: chưa cài đặt");
    }

    public Set<Ban> banBan(KhungGio gio) {
        throw new UnsupportedOperationException("CN-05: chưa cài đặt");
    }

    public void them(DatCho datCho) {
        throw new UnsupportedOperationException("CN-05: chưa cài đặt");
    }

    public Optional<DatCho> timDat(String maDat) {
        throw new UnsupportedOperationException("CN-05: chưa cài đặt");
    }

    public Optional<DatCho> go(String maDat) {
        throw new UnsupportedOperationException("CN-05: chưa cài đặt");
    }

    public String sinhMaDat() {
        throw new UnsupportedOperationException("CN-05: chưa cài đặt");
    }

    public List<DatCho> chupDatCho() {
        throw new UnsupportedOperationException("CN-05: chưa cài đặt");
    }

    public void khoiPhuc(List<DatCho> danhSach) {
        throw new UnsupportedOperationException("CN-05: chưa cài đặt");
    }
}
