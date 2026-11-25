
package de38_datchonhahang.nghiepvu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de38_datchonhahang.model.Ban;
import de38_datchonhahang.model.DatCho;
import de38_datchonhahang.model.KhungGio;

// Phục vụ CN-05: kho trạng thái dùng chung.
public class KhoTrangThai {

    private final Map<KhungGio, Set<Ban>> banBan;
    private final Map<String, DatCho> datCho;
    private final List<Ban> tatCaBan;
    private final List<KhungGio> cacKhungGio;

    private long boDemMaDat = 1;

    private static final Pattern MA_DAT_PATTERN =
            Pattern.compile("^DC(\\d+)$");

    public KhoTrangThai(List<Ban> tatCaBan, List<KhungGio> cacKhungGio) {
        if (tatCaBan == null || cacKhungGio == null) {
            throw new IllegalArgumentException(
                    "Danh sách bàn và khung giờ không được null");
        }

        List<Ban> danhSachBan = new ArrayList<>();
        Set<String> cacMaBan = new HashSet<>();

        for (Ban ban : tatCaBan) {
            if (ban == null) {
                throw new IllegalArgumentException("Danh sách không được chứa bàn null");
            }

            if (!cacMaBan.add(ban.ma())) {
                throw new IllegalArgumentException(
                        "Mã bàn bị trùng: " + ban.ma());
            }

            danhSachBan.add(ban);
        }

        Map<KhungGio, Set<Ban>> trangThaiBan = new TreeMap<>();

        for (KhungGio gio : cacKhungGio) {
            if (gio == null) {
                throw new IllegalArgumentException(
                        "Danh sách không được chứa khung giờ null");
            }

            if (trangThaiBan.containsKey(gio)) {
                throw new IllegalArgumentException(
                        "Khung giờ bị trùng: " + gio.gioBatDau());
            }

            trangThaiBan.put(gio, new HashSet<>());
        }

        this.tatCaBan = List.copyOf(danhSachBan);
        this.cacKhungGio = List.copyOf(trangThaiBan.keySet());
        this.banBan = trangThaiBan;
        this.datCho = new HashMap<>();
    }

    public List<Ban> tatCaBan() {
        return tatCaBan;
    }

    public List<KhungGio> cacKhungGio() {
        return cacKhungGio;
    }

    public Optional<Ban> timBan(String maBan) {
        if (maBan == null) {
            return Optional.empty();
        }

        return tatCaBan.stream()
                .filter(ban -> ban.ma().equals(maBan))
                .findFirst();
    }

    public boolean coKhungGio(KhungGio gio) {
        return gio != null && banBan.containsKey(gio);
    }

    public Set<Ban> banBan(KhungGio gio) {
        Set<Ban> cacBan = banBan.get(gio);

        if (cacBan == null) {
            return Set.of();
        }

        return Collections.unmodifiableSet(new HashSet<>(cacBan));
    }

    public void them(DatCho datCho) {
        if (datCho == null) {
            throw new IllegalArgumentException("Đặt chỗ không được null");
        }

        String maDat = datCho.maDat();

        if (this.datCho.containsKey(maDat)) {
            throw new IllegalArgumentException(
                    "Mã đặt chỗ đã tồn tại: " + maDat);
        }

        Ban banCauHinh = timBan(datCho.ban().ma())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Bàn không thuộc cấu hình: " + datCho.ban().ma()));

        if (!banCauHinh.equals(datCho.ban())) {
            throw new IllegalArgumentException(
                    "Thông tin bàn không khớp cấu hình: " + datCho.ban().ma());
        }

        if (!coKhungGio(datCho.gio())) {
            throw new IllegalArgumentException(
                    "Khung giờ không thuộc cấu hình: " + datCho.gio());
        }

        Set<Ban> cacBanBan = banBan.get(datCho.gio());

        if (cacBanBan.contains(banCauHinh)) {
            throw new IllegalArgumentException(
                    "Bàn đã được đặt trong khung giờ này: " + banCauHinh.ma());
        }

        this.datCho.put(maDat, datCho);
        cacBanBan.add(banCauHinh);

        capNhatBoDem(maDat);
    }

    public Optional<DatCho> timDat(String maDat) {
        return Optional.ofNullable(datCho.get(maDat));
    }

    public Optional<DatCho> go(String maDat) {
        DatCho datDaGo = datCho.remove(maDat);

        if (datDaGo == null) {
            return Optional.empty();
        }

        Set<Ban> cacBanBan = banBan.get(datDaGo.gio());

        if (cacBanBan != null) {
            cacBanBan.remove(datDaGo.ban());
        }

        return Optional.of(datDaGo);
    }

    public synchronized String sinhMaDat() {
        String maDat;

        do {
            maDat = String.format(
                    Locale.ROOT, "DC%02d", boDemMaDat++);
        } while (datCho.containsKey(maDat));

        return maDat;
    }

    public List<DatCho> chupDatCho() {
        return new ArrayList<>(datCho.values());
    }

    public void khoiPhuc(List<DatCho> danhSach) {
        if (danhSach == null) {
            throw new IllegalArgumentException(
                    "Danh sách đặt chỗ không được null");
        }

        Map<String, DatCho> datChoMoi = new HashMap<>();

        Map<KhungGio, Set<Ban>> banBanMoi = new TreeMap<>();

        for (KhungGio gio : cacKhungGio) {
            banBanMoi.put(gio, new HashSet<>());
        }

        long boDemMoi = boDemMaDat;

        // Kiểm tra và xây dựng trạng thái tạm.
        // Chỉ cập nhật trạng thái chính sau khi toàn bộ dữ liệu hợp lệ.
        for (DatCho dat : danhSach) {
            if (dat == null) {
                throw new IllegalArgumentException(
                        "Danh sách không được chứa đặt chỗ null");
            }

            if (datChoMoi.containsKey(dat.maDat())) {
                throw new IllegalArgumentException(
                        "Mã đặt chỗ bị trùng: " + dat.maDat());
            }

            Ban banCauHinh = timBan(dat.ban().ma())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Bàn không thuộc cấu hình: " + dat.ban().ma()));

            if (!banCauHinh.equals(dat.ban())) {
                throw new IllegalArgumentException(
                        "Thông tin bàn không khớp cấu hình: " + dat.ban().ma());
            }

            Set<Ban> cacBanBan = banBanMoi.get(dat.gio());

            if (cacBanBan == null) {
                throw new IllegalArgumentException(
                        "Khung giờ không thuộc cấu hình: " + dat.gio());
            }

            if (!cacBanBan.add(banCauHinh)) {
                throw new IllegalArgumentException(
                        "Một bàn bị đặt nhiều lần trong cùng khung giờ: "
                                + banCauHinh.ma());
            }

            datChoMoi.put(dat.maDat(), dat);

            Matcher matcher = MA_DAT_PATTERN.matcher(dat.maDat());

            if (matcher.matches()) {
                try {
                    long soThuTu = Long.parseLong(matcher.group(1));

                    if (soThuTu >= boDemMoi) {
                        boDemMoi = soThuTu + 1;
                    }
                } catch (NumberFormatException ignored) {
                    // Bỏ qua mã có phần số vượt giới hạn long.
                }
            }
        }

        // Tất cả dữ liệu hợp lệ: thay thế trạng thái cũ.
        datCho.clear();
        datCho.putAll(datChoMoi);

        banBan.clear();

        for (Map.Entry<KhungGio, Set<Ban>> entry : banBanMoi.entrySet()) {
            banBan.put(entry.getKey(), new HashSet<>(entry.getValue()));
        }

        boDemMaDat = boDemMoi;
    }

    private void capNhatBoDem(String maDat) {
        Matcher matcher = MA_DAT_PATTERN.matcher(maDat);

        if (matcher.matches()) {
            try {
                long soThuTu = Long.parseLong(matcher.group(1));

                if (soThuTu >= boDemMaDat) {
                    boDemMaDat = soThuTu + 1;
                }
            } catch (NumberFormatException ignored) {
                // Bỏ qua mã có phần số vượt giới hạn long.
            }
        }
    }
}
