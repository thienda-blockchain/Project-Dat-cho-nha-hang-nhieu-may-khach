package de38_datchonhahang.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

// Kiểm thử CN-02: Ban (mã không null/rỗng/chỉ khoảng trắng, sức chứa > 0, equals/hashCode của record).
class BanTest {

    // ---- Dữ liệu hợp lệ ----

    @Test
    void taoBanHopLe() {
        Ban ban = new Ban("B01", 4);
        assertEquals("B01", ban.ma());
        assertEquals(4, ban.sucChua());
    }

    @Test
    void maBanKhacNhau() {
        Ban b01 = new Ban("B01", 4);
        Ban b02 = new Ban("B02", 4);
        assertEquals("B01", b01.ma());
        assertEquals("B02", b02.ma());
        assertNotEquals(b01, b02);
    }

    @Test
    void sucChuaKhacNhau() {
        Ban nho = new Ban("B01", 2);
        Ban lon = new Ban("B01", 6);
        assertEquals(2, nho.sucChua());
        assertEquals(6, lon.sucChua());
        assertNotEquals(nho, lon);
    }

    // ---- Mã bàn không hợp lệ ----

    @Test
    void maNullBiTuChoi() {
        assertThrows(IllegalArgumentException.class, () -> new Ban(null, 4));
    }

    @Test
    void maRongBiTuChoi() {
        assertThrows(IllegalArgumentException.class, () -> new Ban("", 4));
    }

    @Test
    void maChiCoKhoangTrangBiTuChoi() {
        assertThrows(IllegalArgumentException.class, () -> new Ban("   ", 4));
    }

    // ---- Sức chứa không hợp lệ ----

    @Test
    void sucChuaBang0BiTuChoi() {
        assertThrows(IllegalArgumentException.class, () -> new Ban("B01", 0));
    }

    @Test
    void sucChuaAmBiTuChoi() {
        assertThrows(IllegalArgumentException.class, () -> new Ban("B01", -1));
    }

    // ---- equals / hashCode mặc định của record ----

    @Test
    void cungDuLieuThiEqualsTrue() {
        assertEquals(new Ban("B01", 4), new Ban("B01", 4));
    }

    @Test
    void cungDuLieuThiHashCodeBangNhau() {
        assertEquals(new Ban("B01", 4).hashCode(), new Ban("B01", 4).hashCode());
    }

    @Test
    void khacDuLieuThiEqualsFalse() {
        assertNotEquals(new Ban("B01", 4), new Ban("B02", 6));
    }
}