
package de38_datchonhahang.nghiepvu;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.TreeMap;

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
        List<String> ketQua = new ArrayList<>(
                kho.cacKhungGio().stream()
                        .sorted()
                        .map(this::taoDongXem)
                        .flatMap(List::stream)
                        .toList()
        );

        // Tính tỷ lệ lấp đầy tổng thể trong cả ngày.
        KetQua<Double> ketQuaTyLeCaNgay = tyLeLapDayCaNgay();

        if (ketQuaTyLeCaNgay instanceof KetQua.ThanhCong<?> thanhCong) {
            double tyLeCaNgay = (Double) thanhCong.giaTri();

            ketQua.add(String.format(
                    Locale.ROOT,
                    "Tỷ lệ lấp đầy cả ngày: %.2f%%",
                    tyLeCaNgay
            ));
        }

        return new KetQua.ThanhCong<>(List.copyOf(ketQua));
    }

    
    // Tạo các dòng hiển thị cho một khung giờ.
    private List<String> taoDongXem(KhungGio gio) {
        // Lấy bản sao danh sách bàn đã đặt.
        Set<Ban> banDaDat = kho.banBan(gio);

        // Lấy danh sách bàn đã cấu hình.
        List<Ban> tatCaBan = kho.tatCaBan();

        // Tính tỷ lệ lấp đầy từ cùng bản sao trạng thái bàn.
        double tyLeLapDay = tatCaBan.isEmpty()
                ? 0.0
                : banDaDat.size() * 100.0 / tatCaBan.size();

        // Sắp xếp bàn theo mã và tạo nội dung từng dòng.
        String cacDongBan = tatCaBan.stream()
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

        List<String> ketQua = new ArrayList<>();

        // Dòng đầu tiên là tên khung giờ.
        ketQua.add(String.format(
                Locale.ROOT,
                "Khung giờ: %02d:00",
                gio.gioBatDau()
        ));

        // Thêm thông tin từng bàn.
        if (!cacDongBan.isEmpty()) {
            ketQua.addAll(List.of(cacDongBan.split("\n")));
        }

        // Thêm tỷ lệ lấp đầy.
        ketQua.add(String.format(
                Locale.ROOT,
                "Tỷ lệ lấp đầy: %.2f%%",
                tyLeLapDay
        ));

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


    // CN-08: huỷ đặt chỗ theo mã đặt và xác minh tên khách.
    public KetQua<DatCho> huy(String maDat, String tenKhach) {

        // Bước 1: Kiểm tra tham số đầu vào.
        if (maDat == null || maDat.isBlank()
                || tenKhach == null || tenKhach.isBlank()) {
            return new KetQua.ThatBai<>(
                    MaLoi.SAI_THAM_SO,
                    "Mã đặt chỗ và tên khách không được để trống"
            );
        }

        // Bước 2: Tìm đặt chỗ theo mã.
        DatCho datCho = kho.timDat(maDat).orElse(null);

        if (datCho == null) {
            return new KetQua.ThatBai<>(
                    MaLoi.DAT_KHONG_TON_TAI,
                    "Đặt chỗ không tồn tại: " + maDat
            );
        }

        // Bước 3: Xác minh tên khách.
        if (!datCho.tenKhach().equals(tenKhach)) {
            return new KetQua.ThatBai<>(
                    MaLoi.SAI_THAM_SO,
                    "Tên khách không khớp với thông tin đặt chỗ"
            );
        }

        // Bước 4: Huỷ đặt chỗ và giải phóng bàn.
        DatCho datDaHuy = kho.go(maDat).orElse(null);

        if (datDaHuy == null) {
            return new KetQua.ThatBai<>(
                    MaLoi.DAT_KHONG_TON_TAI,
                    "Đặt chỗ không còn tồn tại: " + maDat
            );
        }

        // Bước 5: Trả về đặt chỗ đã huỷ.
        return new KetQua.ThanhCong<>(datDaHuy);
    }

    public KetQua<Double> tyLeLapDay(KhungGio gio) {
        if (gio == null) {
            return new KetQua.ThatBai<>(
                    MaLoi.SAI_THAM_SO,
                    "Khung giờ không được null"
            );
        }

        if (!kho.coKhungGio(gio)) {
            return new KetQua.ThatBai<>(
                    MaLoi.GIO_KHONG_TON_TAI,
                    "Khung giờ không tồn tại"
            );
        }

        int tongSoBan = kho.tatCaBan().size();

        if (tongSoBan == 0) {
            return new KetQua.ThanhCong<>(0.0);
        }

        // Lấy bản sao trạng thái bàn đã đặt.
        Set<Ban> banDaDat = kho.banBan(gio);

        // Dùng Stream filter và count để đếm bàn đã đặt.
        long soBanDaDat = kho.tatCaBan().stream()
                .filter(banDaDat::contains)
                .count();

        double tyLe = soBanDaDat * 100.0 / tongSoBan;

        return new KetQua.ThanhCong<>(tyLe);
    }

    public KetQua<Double> tyLeLapDayCaNgay() {
        int tongSoBan = kho.tatCaBan().size();
        int tongSoKhungGio = kho.cacKhungGio().size();

        if (tongSoBan == 0 || tongSoKhungGio == 0) {
            return new KetQua.ThanhCong<>(0.0);
        }

        // Chụp danh sách đặt chỗ để thống kê.
        List<DatCho> danhSachDat = kho.chupDatCho();

        // Thống kê số bàn được đặt theo từng khung giờ.
        Map<KhungGio, Long> soBanDaDatTheoGio = danhSachDat.stream()
                .collect(Collectors.groupingBy(
                        DatCho::gio,
                        TreeMap::new,
                        Collectors.counting()
                ));

        // Tổng số lượt bàn đã được đặt trong cả ngày.
        long tongSoLuotBanDaDat = soBanDaDatTheoGio.values().stream()
                .mapToLong(Long::longValue)
                .sum();

        // Tổng số lượt bàn có thể được đặt trong cả ngày.
        double tongSoLuotBanToiDa = (double) tongSoBan * tongSoKhungGio;

        double tyLe = tongSoLuotBanDaDat * 100.0 / tongSoLuotBanToiDa;

        return new KetQua.ThanhCong<>(tyLe);
    }
}