package de38_datchonhahang.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.TreeMap;

import org.junit.jupiter.api.Test;

// Kiểm thử CN-02: KhungGio (giờ bắt đầu hợp lệ 0–23, compareTo nhất quán với equals).
class KhungGioTest {

    // ---- Giờ hợp lệ ----

    @Test
    void gioBien0HopLe() {
        assertEquals(0, new KhungGio(0).gioBatDau());
    }

    @Test
    void gioBien23HopLe() {
        assertEquals(23, new KhungGio(23).gioBatDau());
    }

    @Test
    void gioGiuaHopLe() {
        assertEquals(12, new KhungGio(12).gioBatDau());
    }

    // ---- Giờ không hợp lệ ----

    @Test
    void gioAmBiTuChoi() {
        assertThrows(IllegalArgumentException.class, () -> new KhungGio(-1));
    }

    @Test
    void gio24BiTuChoi() {
        assertThrows(IllegalArgumentException.class, () -> new KhungGio(24));
    }

    @Test
    void gioCucTriBiTuChoi() {
        assertThrows(IllegalArgumentException.class, () -> new KhungGio(Integer.MIN_VALUE));
        assertThrows(IllegalArgumentException.class, () -> new KhungGio(Integer.MAX_VALUE));
    }

    // ---- compareTo ----

    @Test
    void nhoHonThiAm() {
        assertTrue(new KhungGio(10).compareTo(new KhungGio(12)) < 0);
    }

    @Test
    void bangNhauThiKhong() {
        assertEquals(0, new KhungGio(10).compareTo(new KhungGio(10)));
    }

    @Test
    void lonHonThiDuong() {
        assertTrue(new KhungGio(12).compareTo(new KhungGio(10)) > 0);
    }

    @Test
    void bienThapVaCao() {
        KhungGio thap = new KhungGio(0);
        KhungGio cao = new KhungGio(23);
        assertTrue(thap.compareTo(cao) < 0);
        assertTrue(cao.compareTo(thap) > 0);
    }

    @Test
    void doiXung() {
        KhungGio a = new KhungGio(9);
        KhungGio b = new KhungGio(17);
        assertEquals(Integer.signum(a.compareTo(b)), -Integer.signum(b.compareTo(a)));
    }

    @Test
    void nhatQuanVoiEquals() {
        KhungGio a = new KhungGio(10);
        KhungGio b = new KhungGio(10);
        KhungGio c = new KhungGio(11);
        assertEquals(0, a.compareTo(b));
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(0, a.compareTo(c));
        assertNotEquals(a, c);
    }

    @Test
    void soSanhVoiNullNemNPE() {
        assertThrows(NullPointerException.class, () -> new KhungGio(10).compareTo(null));
    }

    // ---- Dùng làm khoá TreeMap ----

    @Test
    void treeMapSapXepTheoGio() {
        TreeMap<KhungGio, String> theoGio = new TreeMap<>();
        theoGio.put(new KhungGio(18), "toi");
        theoGio.put(new KhungGio(10), "sang");
        theoGio.put(new KhungGio(12), "trua");
        assertEquals(
                List.of(new KhungGio(10), new KhungGio(12), new KhungGio(18)),
                List.copyOf(theoGio.keySet()));
    }

    @Test
    void treeMapGopKhoaTrung() {
        TreeMap<KhungGio, String> theoGio = new TreeMap<>();
        theoGio.put(new KhungGio(10), "lan 1");
        theoGio.put(new KhungGio(10), "lan 2");
        assertEquals(1, theoGio.size());
        assertEquals("lan 2", theoGio.get(new KhungGio(10)));
    }
}
