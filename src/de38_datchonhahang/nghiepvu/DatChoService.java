
package de38_datchonhahang.nghiepvu;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import de38_datchonhahang.luutru.LuuTruTrangThai;
import de38_datchonhahang.model.Ban;
import de38_datchonhahang.model.DatCho;
import de38_datchonhahang.model.KetQua;
import de38_datchonhahang.model.KhungGio;
import de38_datchonhahang.model.MaLoi;
import de38_datchonhahang.model.TrangThaiBan;

// Phục vụ CN-06 (XEM), CN-07 (DAT), CN-08 (HUY), CN-09 (tỷ lệ lấp đầy) và CN-14 (đồng bộ); mọi phép trả KetQua.
public class DatChoService {

    private final KhoTrangThai kho;
    private final KhoaBan khoa;
    private final LuuTruTrangThai luuTru;

    public DatChoService(KhoTrangThai kho, KhoaBan khoa, LuuTruTrangThai luuTru) {
        this.kho = Objects.requireNonNull(kho, "Kho trạng thái không được null");
        this.khoa = khoa;
        this.luuTru = luuTru;
    }

    // CN-06: xem sơ đồ bàn của một khung giờ.
    public KetQua<List<String>> xem(KhungGio gio) {
        if (gio == null) {
            return new KetQua.ThatBai<>(
                    MaLoi.SAI_THAM_SO,
                    "Khung giờ không được null"
            );
        }

        if (!kho.coKhungGio(gio)) {
            return new KetQua.ThatBai<>(
                    MaLoi.GIO_KHONG_TON_TAI,
                    "Khung giờ không tồn tại: " + gio.gioBatDau()
            );
        }

        return new KetQua.ThanhCong<>(taoDongXem(gio));
    }

    // CN-06: xem sơ đồ bàn của tất cả khung giờ theo thứ tự tăng dần.
    public KetQua<List<String>> xemTatCa() {
        List<String> ketQua = kho.cacKhungGio().stream()
                .sorted()
                .map(this::taoDongXem)
                .flatMap(List::stream)
                .toList();

        return new KetQua.ThanhCong<>(ketQua);
    }

    // Tạo các dòng hiển thị cho một khung giờ.
    private List<String> taoDongXem(KhungGio gio) {
        // Lấy bản sao danh sách bàn đã đặt để tránh duyệt trực tiếp Set đang thay đổi.
        Set<Ban> banDaDat = kho.banBan(gio);

        // Sắp xếp bàn theo mã và tạo nội dung từng dòng.
        String cacDongBan = kho.tatCaBan().stream()
                .sorted(Comparator.comparing(Ban::ma))
                .map(ban -> {
                    TrangThaiBan trangThai = banDaDat.contains(ban)
                            ? TrangThaiBan.DA_DAT
                            : TrangThaiBan.TRONG;

                    String tenTrangThai = switch (trangThai) {
                        case TRONG -> "TRONG";
                        case DA_DAT -> "DA_DAT";
                    };

                    return "Bàn " + ban.ma()
                            + " | Sức chứa: " + ban.sucChua()
                            + " | " + tenTrangThai;
                })
                .collect(Collectors.joining("\n"));

        // Dòng đầu tiên là tên khung giờ.
        List<String> ketQua = new ArrayList<>();
        ketQua.add(String.format(
                Locale.ROOT,
                "Khung giờ: %02d:00",
                gio.gioBatDau()
        ));

        // Thêm các dòng thông tin bàn nếu có bàn được cấu hình.
        if (!cacDongBan.isEmpty()) {
            ketQua.addAll(List.of(cacDongBan.split("\n")));
        }

        return List.copyOf(ketQua);
    }

    // CN-07: kiểm tra điều kiện và thực hiện đặt bàn.
    public KetQua<DatCho> dat(String maBan, KhungGio gio, int soKhach, String tenKhach) {

        // Bước 1: Kiểm tra tham số đầu vào.
        if (maBan == null || maBan.isBlank()
                || gio == null
                || soKhach <= 0
                || tenKhach == null || tenKhach.isBlank()) {
            return new KetQua.ThatBai<>(
                    MaLoi.SAI_THAM_SO,
                    "Thông tin đặt bàn không hợp lệ"
            );
        }

        // Bước 2: Kiểm tra bàn có tồn tại trong cấu hình.
        Ban ban = kho.timBan(maBan).orElse(null);

        if (ban == null) {
            return new KetQua.ThatBai<>(
                    MaLoi.BAN_KHONG_TON_TAI,
                    "Bàn không tồn tại: " + maBan
            );
        }

        // Bước 3: Kiểm tra khung giờ có tồn tại.
        if (!kho.coKhungGio(gio)) {
            return new KetQua.ThatBai<>(
                    MaLoi.GIO_KHONG_TON_TAI,
                    "Khung giờ không tồn tại: " + gio.gioBatDau()
            );
        }

        // Bước 4: Kiểm tra số khách không vượt sức chứa của bàn.
        if (soKhach > ban.sucChua()) {
            return new KetQua.ThatBai<>(
                    MaLoi.QUA_SUC_CHUA,
                    "Số khách vượt quá sức chứa của bàn " + maBan
            );
        }

        // Bước 5: Kiểm tra bàn đã được đặt trong khung giờ này chưa.
        if (kho.banBan(gio).contains(ban)) {
            return new KetQua.ThatBai<>(
                    MaLoi.BAN_BAN,
                    "Bàn đã được đặt trong khung giờ này: " + maBan
            );
        }

        // Bước 6: Sinh mã, tạo lượt đặt và lưu vào kho trạng thái.
        DatCho datCho = new DatCho(
                kho.sinhMaDat(),
                tenKhach,
                ban,
                gio,
                soKhach
        );

        kho.them(datCho);

        // Trả về kết quả đặt bàn thành công.
        return new KetQua.ThanhCong<>(datCho);
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