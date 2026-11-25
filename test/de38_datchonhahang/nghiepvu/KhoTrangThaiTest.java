package de38_datchonhahang.nghiepvu;

import de38_datchonhahang.model.Ban;
import de38_datchonhahang.model.DatCho;
import de38_datchonhahang.model.KhungGio;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;

class KhoTrangThaiTest {

    private Ban ban01() {
        return new Ban("B01", 4);
    }

    private Ban ban02() {
        return new Ban("B02", 6);
    }

    private KhungGio gio18() {
        return new KhungGio(18);
    }

    private KhungGio gio19() {
        return new KhungGio(19);
    }

    private KhoTrangThai taoKho() {
        return new KhoTrangThai(
                List.of(ban01(), ban02()),
                List.of(gio19(), gio18())
        );
    }

    private DatCho taoDatCho(String maDat, Ban ban, KhungGio gio) {
        return new DatCho(maDat, "Nguyen Van A", ban, gio, 2);
    }

    @Test
    void khoiTaoThanhCongVaSapXepKhungGio() {
        KhoTrangThai kho = taoKho();

        assertEquals(List.of(gio18(), gio19()), kho.cacKhungGio());
    }

    @Test
    void khoiTaoSetRongChoMoiKhungGio() {
        KhoTrangThai kho = taoKho();

        assertTrue(kho.banBan(gio18()).isEmpty());
        assertTrue(kho.banBan(gio19()).isEmpty());
    }

    @Test
    void traVeDanhSachTatCaBan() {
        KhoTrangThai kho = taoKho();

        assertEquals(List.of(ban01(), ban02()), kho.tatCaBan());
    }

    @Test
    void timBanTheoMa() {
        KhoTrangThai kho = taoKho();

        assertEquals(ban01(), kho.timBan("B01").orElseThrow());
        assertTrue(kho.timBan("B99").isEmpty());
    }

    @Test
    void kiemTraKhungGioTonTai() {
        KhoTrangThai kho = taoKho();

        assertTrue(kho.coKhungGio(gio18()));
        assertFalse(kho.coKhungGio(new KhungGio(20)));
    }

    @Test
    void tuChoiDanhSachBanCoMaTrung() {
        assertThrows(IllegalArgumentException.class,
                () -> new KhoTrangThai(
                        List.of(ban01(), new Ban("B01", 8)),
                        List.of(gio18())
                ));
    }

    @Test
    void themDatChoVaDanhDauBanBan() {
        KhoTrangThai kho = taoKho();
        DatCho datCho = taoDatCho("DC01", ban01(), gio18());

        kho.them(datCho);

        assertEquals(datCho, kho.timDat("DC01").orElseThrow());
        assertTrue(kho.banBan(gio18()).contains(ban01()));
    }

    @Test
    void tuChoiThemDatChoTrungMa() {
        KhoTrangThai kho = taoKho();

        kho.them(taoDatCho("DC01", ban01(), gio18()));

        assertThrows(IllegalArgumentException.class,
                () -> kho.them(taoDatCho("DC01", ban02(), gio19())));
    }

    @Test
    void tuChoiDatBanKhongThuocCauHinh() {
        KhoTrangThai kho = taoKho();

        assertThrows(IllegalArgumentException.class,
                () -> kho.them(taoDatCho(
                        "DC01", new Ban("B99", 4), gio18()
                )));
    }

    @Test
    void tuChoiDatKhungGioKhongThuocCauHinh() {
        KhoTrangThai kho = taoKho();

        assertThrows(IllegalArgumentException.class,
                () -> kho.them(taoDatCho(
                        "DC01", ban01(), new KhungGio(20)
                )));
    }

    @Test
    void tuChoiDatTrungBanCungKhungGio() {
        KhoTrangThai kho = taoKho();

        kho.them(taoDatCho("DC01", ban01(), gio18()));

        assertThrows(IllegalArgumentException.class,
                () -> kho.them(taoDatCho("DC02", ban01(), gio18())));
    }

    @Test
    void choPhepCungBanKhacKhungGio() {
        KhoTrangThai kho = taoKho();

        kho.them(taoDatCho("DC01", ban01(), gio18()));
        kho.them(taoDatCho("DC02", ban01(), gio19()));

        assertEquals(1, kho.banBan(gio18()).size());
        assertEquals(1, kho.banBan(gio19()).size());
    }

    @Test
    void goDatChoVaGiaiPhongBan() {
        KhoTrangThai kho = taoKho();
        DatCho datCho = taoDatCho("DC01", ban01(), gio18());

        kho.them(datCho);

        assertEquals(datCho, kho.go("DC01").orElseThrow());
        assertTrue(kho.timDat("DC01").isEmpty());
        assertFalse(kho.banBan(gio18()).contains(ban01()));
    }

    @Test
    void goMaKhongTonTaiTraVeOptionalRong() {
        KhoTrangThai kho = taoKho();

        assertTrue(kho.go("DC99").isEmpty());
    }

    @Test
    void sinhMaDatTheoThuTu() {
        KhoTrangThai kho = taoKho();

        assertEquals("DC01", kho.sinhMaDat());
        assertEquals("DC02", kho.sinhMaDat());
        assertEquals("DC03", kho.sinhMaDat());
    }

    @Test
    void chupDatChoTraVeDanhSachHienTai() {
        KhoTrangThai kho = taoKho();
        DatCho datCho = taoDatCho("DC01", ban01(), gio18());

        kho.them(datCho);

        assertEquals(List.of(datCho), kho.chupDatCho());
    }

    @Test
    void chupDatChoTraVeBanSaoDocLap() {
        KhoTrangThai kho = taoKho();
        kho.them(taoDatCho("DC01", ban01(), gio18()));

        List<DatCho> banSao = kho.chupDatCho();
        banSao.clear();

        assertEquals(1, kho.chupDatCho().size());
    }

    @Test
    void khoiPhucDuLieuVaCapNhatBoDem() {
        KhoTrangThai kho = taoKho();

        kho.khoiPhuc(List.of(
                taoDatCho("DC03", ban01(), gio18()),
                taoDatCho("DC05", ban02(), gio19())
        ));

        assertEquals(2, kho.chupDatCho().size());
        assertEquals("DC06", kho.sinhMaDat());
    }

    @Test
    void khoiPhucDanhSachRongThiXoaTrangThaiCu() {
        KhoTrangThai kho = taoKho();
        kho.them(taoDatCho("DC01", ban01(), gio18()));

        kho.khoiPhuc(List.of());

        assertTrue(kho.chupDatCho().isEmpty());
        assertTrue(kho.banBan(gio18()).isEmpty());
    }

    @Test
    void khoiPhucDuLieuKhongHopLeThiGiuNguyenTrangThaiCu() {
        KhoTrangThai kho = taoKho();
        DatCho cu = taoDatCho("DC01", ban01(), gio18());

        kho.them(cu);

        assertThrows(IllegalArgumentException.class,
                () -> kho.khoiPhuc(List.of(
                        taoDatCho("DC02", ban02(), gio19()),
                        taoDatCho("DC02", ban01(), gio19())
                )));

        assertEquals(List.of(cu), kho.chupDatCho());
        assertTrue(kho.banBan(gio18()).contains(ban01()));
        assertTrue(kho.banBan(gio19()).isEmpty());
    }

    @Test
    void sinhMaDatKhongTrungKhiNhieuLuongCungGoi() throws Exception {
        KhoTrangThai kho = taoKho();
        ExecutorService executor = Executors.newFixedThreadPool(8);

        try {
            List<Callable<String>> congViec = new ArrayList<>();

            for (int i = 0; i < 200; i++) {
                congViec.add(kho::sinhMaDat);
            }

            List<Future<String>> ketQua = executor.invokeAll(congViec);
            Set<String> cacMa = new HashSet<>();

            for (Future<String> future : ketQua) {
                cacMa.add(future.get());
            }

            assertEquals(200, cacMa.size());
        } finally {
            executor.shutdown();
        }
    }

    @Test
    void danhSachBanTraVeKhongChoPhepSua() {
        KhoTrangThai kho = taoKho();

        assertThrows(UnsupportedOperationException.class,
                () -> kho.tatCaBan().clear());
    }

    @Test
    void danhSachKhungGioTraVeKhongChoPhepSua() {
        KhoTrangThai kho = taoKho();

        assertThrows(UnsupportedOperationException.class,
                () -> kho.cacKhungGio().clear());
    }

    @Test
    void tapBanBanTraVeKhongChoPhepSua() {
        KhoTrangThai kho = taoKho();

        assertThrows(UnsupportedOperationException.class,
                () -> kho.banBan(gio18()).add(ban01()));
    }
}
