
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


    // CN-07: Tham số không hợp lệ phải trả về SAI_THAM_SO.
    @Test
    void datThamSoKhongHopLeTraVeThatBai() {
        DatChoService service = taoService();

        KetQua<DatCho> ketQua = service.dat(
                null, new KhungGio(8), 2, "Nguyen Van A"
        );

        assertInstanceOf(KetQua.ThatBai.class, ketQua);

        KetQua.ThatBai<?> thatBai = (KetQua.ThatBai<?>) ketQua;
        assertEquals(MaLoi.SAI_THAM_SO, thatBai.maLoi());
    }

    // CN-07: Mã bàn không có trong cấu hình.
    @Test
    void datBanKhongTonTaiTraVeThatBai() {
        DatChoService service = taoService();

        KetQua<DatCho> ketQua = service.dat(
                "B99", new KhungGio(8), 2, "Nguyen Van A"
        );

        assertInstanceOf(KetQua.ThatBai.class, ketQua);

        KetQua.ThatBai<?> thatBai = (KetQua.ThatBai<?>) ketQua;
        assertEquals(MaLoi.BAN_KHONG_TON_TAI, thatBai.maLoi());
    }

    // CN-07: Khung giờ không có trong cấu hình.
    @Test
    void datKhungGioKhongTonTaiTraVeThatBai() {
        DatChoService service = taoService();

        KetQua<DatCho> ketQua = service.dat(
                "B01", new KhungGio(10), 2, "Nguyen Van A"
        );

        assertInstanceOf(KetQua.ThatBai.class, ketQua);

        KetQua.ThatBai<?> thatBai = (KetQua.ThatBai<?>) ketQua;
        assertEquals(MaLoi.GIO_KHONG_TON_TAI, thatBai.maLoi());
    }

    // CN-07: Số khách bằng 0 không hợp lệ.
    @Test
    void datSoKhachBangKhongTraVeThatBai() {
        DatChoService service = taoService();

        KetQua<DatCho> ketQua = service.dat(
                "B01", new KhungGio(8), 0, "Nguyen Van A"
        );

        assertInstanceOf(KetQua.ThatBai.class, ketQua);

        KetQua.ThatBai<?> thatBai = (KetQua.ThatBai<?>) ketQua;
        assertEquals(MaLoi.SAI_THAM_SO, thatBai.maLoi());
    }

    // CN-07: Số khách vượt sức chứa bàn.
    @Test
    void datVuotSucChuaTraVeThatBai() {
        DatChoService service = taoService();

        KetQua<DatCho> ketQua = service.dat(
                "B01", new KhungGio(8), 5, "Nguyen Van A"
        );

        assertInstanceOf(KetQua.ThatBai.class, ketQua);

        KetQua.ThatBai<?> thatBai = (KetQua.ThatBai<?>) ketQua;
        assertEquals(MaLoi.QUA_SUC_CHUA, thatBai.maLoi());
    }

    // CN-07: Không được đặt lại bàn đã bận trong cùng khung giờ.
    @Test
    void datBanDaBanTraVeThatBai() {
        List<Ban> dsBan = List.of(new Ban("B01", 4));
        List<KhungGio> dsGio = List.of(new KhungGio(8));

        KhoTrangThai kho = new KhoTrangThai(dsBan, dsGio);

        kho.them(new DatCho(
                "DC01", "Nguyen Van A",
                new Ban("B01", 4), new KhungGio(8), 2
        ));

        DatChoService service = new DatChoService(kho, null, null);

        KetQua<DatCho> ketQua = service.dat(
                "B01", new KhungGio(8), 2, "Tran Van B"
        );

        assertInstanceOf(KetQua.ThatBai.class, ketQua);

        KetQua.ThatBai<?> thatBai = (KetQua.ThatBai<?>) ketQua;
        assertEquals(MaLoi.BAN_BAN, thatBai.maLoi());
    }

    // CN-07: Đặt bàn hợp lệ phải tạo và lưu đặt chỗ.
    @Test
    void datHopLeTraVeThanhCongVaLuuDatCho() {
        List<Ban> dsBan = List.of(new Ban("B01", 4));
        List<KhungGio> dsGio = List.of(new KhungGio(8));

        KhoTrangThai kho = new KhoTrangThai(dsBan, dsGio);
        DatChoService service = new DatChoService(kho, null, null);

        KetQua<DatCho> ketQua = service.dat(
                "B01", new KhungGio(8), 3, "Nguyen Van A"
        );

        assertInstanceOf(KetQua.ThanhCong.class, ketQua);

        DatCho datCho = ((KetQua.ThanhCong<DatCho>) ketQua).giaTri();

        assertEquals("Nguyen Van A", datCho.tenKhach());
        assertEquals("B01", datCho.ban().ma());
        assertEquals(8, datCho.gio().gioBatDau());
        assertEquals(3, datCho.soKhach());

        assertTrue(kho.timDat(datCho.maDat()).isPresent());
        assertTrue(kho.banBan(new KhungGio(8)).contains(new Ban("B01", 4)));
    }

    // CN-07: Cùng một bàn có thể được đặt ở hai khung giờ khác nhau.
    @Test
    void datCungBanKhacKhungGioVanThanhCong() {
        List<Ban> dsBan = List.of(new Ban("B01", 4));
        List<KhungGio> dsGio = List.of(
                new KhungGio(8),
                new KhungGio(12)
        );

        KhoTrangThai kho = new KhoTrangThai(dsBan, dsGio);
        DatChoService service = new DatChoService(kho, null, null);

        KetQua<DatCho> ketQua1 = service.dat(
                "B01", new KhungGio(8), 2, "Nguyen Van A"
        );

        KetQua<DatCho> ketQua2 = service.dat(
                "B01", new KhungGio(12), 3, "Tran Van B"
        );

        assertInstanceOf(KetQua.ThanhCong.class, ketQua1);
        assertInstanceOf(KetQua.ThanhCong.class, ketQua2);

        assertEquals(2, kho.chupDatCho().size());
    }

    // CN-08: Mã đặt không tồn tại phải trả về DAT_KHONG_TON_TAI.
    @Test
    void huyMaDatKhongTonTaiTraVeThatBai() {
        DatChoService service = taoService();

        KetQua<DatCho> ketQua = service.huy(
                "DC999", "Nguyen Van A"
        );

        assertInstanceOf(KetQua.ThatBai.class, ketQua);

        KetQua.ThatBai<?> thatBai = (KetQua.ThatBai<?>) ketQua;
        assertEquals(MaLoi.DAT_KHONG_TON_TAI, thatBai.maLoi());
    }

    // CN-08: Khách khác không được huỷ đặt chỗ.
    @Test
    void huySaiTenKhachTraVeThatBai() {
        List<Ban> dsBan = List.of(new Ban("B01", 4));
        List<KhungGio> dsGio = List.of(new KhungGio(8));

        KhoTrangThai kho = new KhoTrangThai(dsBan, dsGio);

        kho.them(new DatCho(
                "DC01", "Nguyen Van A",
                new Ban("B01", 4), new KhungGio(8), 2
        ));

        DatChoService service = new DatChoService(kho, null, null);

        KetQua<DatCho> ketQua = service.huy(
                "DC01", "Tran Van B"
        );

        assertInstanceOf(KetQua.ThatBai.class, ketQua);

        KetQua.ThatBai<?> thatBai = (KetQua.ThatBai<?>) ketQua;
        assertEquals(MaLoi.SAI_THAM_SO, thatBai.maLoi());

        assertTrue(kho.timDat("DC01").isPresent());
        assertTrue(kho.banBan(new KhungGio(8)).contains(new Ban("B01", 4)));
    }

    // CN-08: Huỷ lần thứ hai cùng mã phải báo lỗi.
    @Test
    void huyHaiLanCungMaDatLanThuHaiThatBai() {
        List<Ban> dsBan = List.of(new Ban("B01", 4));
        List<KhungGio> dsGio = List.of(new KhungGio(8));

        KhoTrangThai kho = new KhoTrangThai(dsBan, dsGio);

        kho.them(new DatCho(
                "DC01", "Nguyen Van A",
                new Ban("B01", 4), new KhungGio(8), 2
        ));

        DatChoService service = new DatChoService(kho, null, null);

        KetQua<DatCho> lanThuNhat = service.huy(
                "DC01", "Nguyen Van A"
        );

        KetQua<DatCho> lanThuHai = service.huy(
                "DC01", "Nguyen Van A"
        );

        assertInstanceOf(KetQua.ThanhCong.class, lanThuNhat);
        assertInstanceOf(KetQua.ThatBai.class, lanThuHai);

        KetQua.ThatBai<?> thatBai = (KetQua.ThatBai<?>) lanThuHai;
        assertEquals(MaLoi.DAT_KHONG_TON_TAI, thatBai.maLoi());
    }

    // CN-08: Huỷ thành công phải xoá đặt chỗ khỏi kho.
    @Test
    void huyHopLeTraVeThanhCongVaXoaDatCho() {
        List<Ban> dsBan = List.of(new Ban("B01", 4));
        List<KhungGio> dsGio = List.of(new KhungGio(8));

        KhoTrangThai kho = new KhoTrangThai(dsBan, dsGio);

        DatCho datBanDau = new DatCho(
                "DC01", "Nguyen Van A",
                new Ban("B01", 4), new KhungGio(8), 2
        );

        kho.them(datBanDau);

        DatChoService service = new DatChoService(kho, null, null);

        KetQua<DatCho> ketQua = service.huy(
                "DC01", "Nguyen Van A"
        );

        assertInstanceOf(KetQua.ThanhCong.class, ketQua);

        DatCho datDaHuy = ((KetQua.ThanhCong<DatCho>) ketQua).giaTri();

        assertEquals(datBanDau, datDaHuy);
        assertTrue(kho.timDat("DC01").isEmpty());
        assertTrue(kho.banBan(new KhungGio(8)).isEmpty());
    }

    // CN-08: Bàn sau khi huỷ phải được đặt lại thành công.
    @Test
    void huyThanhCongCoTheDatLaiBanNgayLapTuc() {
        List<Ban> dsBan = List.of(new Ban("B01", 4));
        List<KhungGio> dsGio = List.of(new KhungGio(8));

        KhoTrangThai kho = new KhoTrangThai(dsBan, dsGio);
        DatChoService service = new DatChoService(kho, null, null);

        KetQua<DatCho> datLanDau = service.dat(
                "B01", new KhungGio(8), 2, "Nguyen Van A"
        );

        assertInstanceOf(KetQua.ThanhCong.class, datLanDau);

        DatCho datCho = ((KetQua.ThanhCong<DatCho>) datLanDau).giaTri();

        KetQua<DatCho> huy = service.huy(
                datCho.maDat(), "Nguyen Van A"
        );

        assertInstanceOf(KetQua.ThanhCong.class, huy);

        KetQua<DatCho> datLai = service.dat(
                "B01", new KhungGio(8), 3, "Tran Van B"
        );

        assertInstanceOf(KetQua.ThanhCong.class, datLai);
        assertEquals(1, kho.chupDatCho().size());
    }

    // CN-08: Mã đặt và tên khách không hợp lệ phải trả về SAI_THAM_SO.
    @Test
    void huyThamSoKhongHopLeTraVeThatBai() {
        DatChoService service = taoService();

        KetQua<DatCho> ketQua = service.huy(
                " ", "Nguyen Van A"
        );

        assertInstanceOf(KetQua.ThatBai.class, ketQua);

        KetQua.ThatBai<?> thatBai = (KetQua.ThatBai<?>) ketQua;
        assertEquals(MaLoi.SAI_THAM_SO, thatBai.maLoi());
    }

}
