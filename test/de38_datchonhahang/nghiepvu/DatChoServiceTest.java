
package de38_datchonhahang.nghiepvu;

import de38_datchonhahang.luutru.LuuTruTrangThai;
import de38_datchonhahang.model.Ban;
import de38_datchonhahang.model.KetQua;
import de38_datchonhahang.model.KhungGio;
import de38_datchonhahang.model.MaLoi;
import de38_datchonhahang.model.DatCho;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DatChoServiceTest {

    private DatChoService taoService() {
        List<Ban> dsBan = List.of(
                new Ban("B03", 6),
                new Ban("B01", 4),
                new Ban("B02", 8)
        );

        List<KhungGio> dsGio = List.of(
                new KhungGio(12),
                new KhungGio(8)
        );

        KhoTrangThai kho = new KhoTrangThai(dsBan, dsGio);

        return new DatChoService(kho, null, null);
    }

    @Test
    void xemKhungGioKhongTonTaiTraVeThatBai() {
        DatChoService service = taoService();

        KetQua<List<String>> ketQua = service.xem(new KhungGio(10));

        assertInstanceOf(KetQua.ThatBai.class, ketQua);

        KetQua.ThatBai<?> thatBai = (KetQua.ThatBai<?>) ketQua;
        assertEquals(MaLoi.GIO_KHONG_TON_TAI, thatBai.maLoi());
    }

    @Test
    void xemKhungGioTonTaiHienThiDayDuBan() {
        DatChoService service = taoService();

        KetQua<List<String>> ketQua = service.xem(new KhungGio(8));

        assertInstanceOf(KetQua.ThanhCong.class, ketQua);

        @SuppressWarnings("unchecked")
        List<String> dong = ((KetQua.ThanhCong<List<String>>) ketQua).giaTri();

        assertEquals(4, dong.size());
        assertEquals("Khung giờ: 08:00", dong.get(0));
        assertTrue(dong.stream().anyMatch(s -> s.contains("B01")));
        assertTrue(dong.stream().anyMatch(s -> s.contains("B02")));
        assertTrue(dong.stream().anyMatch(s -> s.contains("B03")));
    }

    @Test
    void xemTatCaHienThiKhungGioTheoThuTu() {
        DatChoService service = taoService();

        KetQua<List<String>> ketQua = service.xemTatCa();

        assertInstanceOf(KetQua.ThanhCong.class, ketQua);

        @SuppressWarnings("unchecked")
        List<String> dong = ((KetQua.ThanhCong<List<String>>) ketQua).giaTri();

        assertEquals("Khung giờ: 08:00", dong.get(0));
        assertEquals("Khung giờ: 12:00", dong.get(4));
        assertEquals(8, dong.size());
    }

    @Test
    void xemSapXepBanTheoMa() {
        DatChoService service = taoService();

        KetQua<List<String>> ketQua = service.xem(new KhungGio(8));

        @SuppressWarnings("unchecked")
        List<String> dong = ((KetQua.ThanhCong<List<String>>) ketQua).giaTri();

        assertTrue(dong.get(1).contains("B01"));
        assertTrue(dong.get(2).contains("B02"));
        assertTrue(dong.get(3).contains("B03"));
    }

    @Test
    void xemHienThiDungSucChuaVaTrangThaiBan() {
        DatChoService service = taoService();

        KetQua<List<String>> ketQua = service.xem(new KhungGio(8));

        @SuppressWarnings("unchecked")
        List<String> dong = ((KetQua.ThanhCong<List<String>>) ketQua).giaTri();

        assertTrue(dong.get(1).contains("4"));
        assertTrue(dong.get(1).contains("TRONG"));
        assertTrue(dong.get(2).contains("8"));
        assertTrue(dong.get(2).contains("TRONG"));
        assertTrue(dong.get(3).contains("6"));
        assertTrue(dong.get(3).contains("TRONG"));
    }

    @Test
    void xemHienThiBanDaDuocDat() {
        List<Ban> dsBan = List.of(
                new Ban("B03", 6),
                new Ban("B01", 4),
                new Ban("B02", 8)
        );

        List<KhungGio> dsGio = List.of(
                new KhungGio(8),
                new KhungGio(12)
        );

        KhoTrangThai kho = new KhoTrangThai(dsBan, dsGio);

        // Đặt bàn B02 vào khung giờ 08:00.
        kho.them(new DatCho(
                "D001",
                "Nguyen Van A",
                new Ban("B02", 8),
                new KhungGio(8),
                3
        ));

        DatChoService service = new DatChoService(kho, null, null);

        KetQua<List<String>> ketQua = service.xem(new KhungGio(8));

        assertInstanceOf(KetQua.ThanhCong.class, ketQua);

        @SuppressWarnings("unchecked")
        List<String> dong = ((KetQua.ThanhCong<List<String>>) ketQua).giaTri();

        assertTrue(dong.get(1).contains("B01"));
        assertTrue(dong.get(1).contains("TRONG"));

        assertTrue(dong.get(2).contains("B02"));
        assertTrue(dong.get(2).contains("DA_DAT"));

        assertTrue(dong.get(3).contains("B03"));
        assertTrue(dong.get(3).contains("TRONG"));
    }
}
