
package de38_datchonhahang.luutru;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import de38_datchonhahang.model.Ban;
import de38_datchonhahang.model.KhungGio;

class CauHinhLoaderTest {

    @TempDir
    Path thuMucTam;

    private final CauHinhLoader loader = new CauHinhLoader();

    private Path taoTep(String tenTep, String noiDung) throws IOException {
        Path tep = thuMucTam.resolve(tenTep);
        Files.writeString(tep, noiDung, StandardCharsets.UTF_8);
        return tep;
    }

    @Test
    void docBan_DocDuLieuHopLe() throws IOException, LuuTruException {
        Path tep = taoTep("ban.csv",
                "ma,sucChua\n" +
                "B01,2\n" +
                "B02,4\n" +
                "B03,6\n");

        List<Ban> ketQua = loader.docBan(tep.toString());

        assertEquals(3, ketQua.size());
        assertEquals(new Ban("B01", 2), ketQua.get(0));
        assertEquals(new Ban("B02", 4), ketQua.get(1));
        assertEquals(new Ban("B03", 6), ketQua.get(2));
    }

    @Test
    void docBan_BoQuaDongTrong() throws IOException, LuuTruException {
        Path tep = taoTep("ban.csv",
                "ma,sucChua\n" +
                "B01,2\n" +
                "\n" +
                "B02,4\n");

        List<Ban> ketQua = loader.docBan(tep.toString());

        assertEquals(2, ketQua.size());
        assertEquals("B02", ketQua.get(1).ma());
    }

    @Test
    void docBan_TepChiCoTieuDe_TraVeDanhSachRong()
            throws IOException, LuuTruException {
        Path tep = taoTep("ban.csv", "ma,sucChua\n");

        List<Ban> ketQua = loader.docBan(tep.toString());

        assertNotNull(ketQua);
        assertTrue(ketQua.isEmpty());
    }

    @Test
    void docBan_SaiTieuDe_NemLuuTruException() throws IOException {
        Path tep = taoTep("ban.csv",
                "maBan,sucChua\n" +
                "B01,2\n");

        assertThrows(LuuTruException.class,
                () -> loader.docBan(tep.toString()));
    }

    @Test
    void docBan_SaiSoLuongCot_NemLuuTruException()
            throws IOException {
        Path tep = taoTep("ban.csv",
                "ma,sucChua\n" +
                "B01,2,ghiChu\n");

        assertThrows(LuuTruException.class,
                () -> loader.docBan(tep.toString()));
    }

    @Test
    void docBan_SucChuaKhongPhaiSoNguyen_NemLuuTruException()
            throws IOException {
        Path tep = taoTep("ban.csv",
                "ma,sucChua\n" +
                "B01,abc\n");

        assertThrows(LuuTruException.class,
                () -> loader.docBan(tep.toString()));
    }

    @Test
    void docBan_SucChuaKhongHopLe_NemLuuTruException()
            throws IOException {
        Path tep = taoTep("ban.csv",
                "ma,sucChua\n" +
                "B01,0\n");

        assertThrows(LuuTruException.class,
                () -> loader.docBan(tep.toString()));
    }

    @Test
    void docBan_TepKhongTonTai_NemLuuTruException() {
        Path tep = thuMucTam.resolve("khong-ton-tai.csv");

        assertThrows(LuuTruException.class,
                () -> loader.docBan(tep.toString()));
    }

    @Test
    void docKhungGio_DocDuLieuHopLe()
            throws IOException, LuuTruException {
        Path tep = taoTep("khung-gio.csv",
                "gioBatDau\n" +
                "11\n" +
                "12\n" +
                "18\n" +
                "19\n");

        List<KhungGio> ketQua = loader.docKhungGio(tep.toString());

        assertEquals(4, ketQua.size());
        assertEquals(new KhungGio(11), ketQua.get(0));
        assertEquals(new KhungGio(12), ketQua.get(1));
        assertEquals(new KhungGio(18), ketQua.get(2));
        assertEquals(new KhungGio(19), ketQua.get(3));
    }

    @Test
    void docKhungGio_TepChiCoTieuDe_TraVeDanhSachRong()
            throws IOException, LuuTruException {
        Path tep = taoTep("khung-gio.csv", "gioBatDau\n");

        List<KhungGio> ketQua = loader.docKhungGio(tep.toString());

        assertNotNull(ketQua);
        assertTrue(ketQua.isEmpty());
    }

    @Test
    void docKhungGio_SaiTieuDe_NemLuuTruException()
            throws IOException {
        Path tep = taoTep("khung-gio.csv",
                "gio\n" +
                "11\n");

        assertThrows(LuuTruException.class,
                () -> loader.docKhungGio(tep.toString()));
    }

    @Test
    void docKhungGio_DuLieuKhongPhaiSoNguyen_NemLuuTruException()
            throws IOException {
        Path tep = taoTep("khung-gio.csv",
                "gioBatDau\n" +
                "abc\n");

        assertThrows(LuuTruException.class,
                () -> loader.docKhungGio(tep.toString()));
    }

    @Test
    void docKhungGio_GioNgoaiKhoang_NemLuuTruException()
            throws IOException {
        Path tep = taoTep("khung-gio.csv",
                "gioBatDau\n" +
                "24\n");

        assertThrows(LuuTruException.class,
                () -> loader.docKhungGio(tep.toString()));
    }

    @Test
    void docKhungGio_SaiSoLuongCot_NemLuuTruException()
            throws IOException {
        Path tep = taoTep("khung-gio.csv",
                "gioBatDau,ghiChu\n" +
                "11,buoi sang\n");

        assertThrows(LuuTruException.class,
                () -> loader.docKhungGio(tep.toString()));
    }

    @Test
    void docKhungGio_TepKhongTonTai_NemLuuTruException() {
        Path tep = thuMucTam.resolve("khong-ton-tai.csv");

        assertThrows(LuuTruException.class,
                () -> loader.docKhungGio(tep.toString()));
    }
}
