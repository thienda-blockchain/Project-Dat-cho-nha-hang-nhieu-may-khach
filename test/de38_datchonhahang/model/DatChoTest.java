
package de38_datchonhahang.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DatChoTest {

    private Ban taoBan() {
        return new Ban("B01", 4);
    }

    private KhungGio taoKhungGio() {
        return new KhungGio(18);
    }

    @Test
    void taoDatChoHopLe() {
        DatCho datCho = new DatCho(
                "DC01", "Nguyen Van A", taoBan(), taoKhungGio(), 3
        );

        assertEquals("DC01", datCho.maDat());
        assertEquals("Nguyen Van A", datCho.tenKhach());
        assertEquals(taoBan(), datCho.ban());
        assertEquals(taoKhungGio(), datCho.gio());
        assertEquals(3, datCho.soKhach());
    }

    @Test
    void maDatNullBiTuChoi() {
        assertThrows(IllegalArgumentException.class,
                () -> new DatCho(null, "Nguyen Van A", taoBan(), taoKhungGio(), 2));
    }

    @Test
    void maDatRongBiTuChoi() {
        assertThrows(IllegalArgumentException.class,
                () -> new DatCho("", "Nguyen Van A", taoBan(), taoKhungGio(), 2));
    }

    @Test
    void maDatChiCoKhoangTrangBiTuChoi() {
        assertThrows(IllegalArgumentException.class,
                () -> new DatCho("   ", "Nguyen Van A", taoBan(), taoKhungGio(), 2));
    }

    @Test
    void tenKhachNullBiTuChoi() {
        assertThrows(IllegalArgumentException.class,
                () -> new DatCho("DC01", null, taoBan(), taoKhungGio(), 2));
    }

    @Test
    void tenKhachRongBiTuChoi() {
        assertThrows(IllegalArgumentException.class,
                () -> new DatCho("DC01", "", taoBan(), taoKhungGio(), 2));
    }

    @Test
    void tenKhachChiCoKhoangTrangBiTuChoi() {
        assertThrows(IllegalArgumentException.class,
                () -> new DatCho("DC01", "   ", taoBan(), taoKhungGio(), 2));
    }

    @Test
    void banNullBiTuChoi() {
        assertThrows(IllegalArgumentException.class,
                () -> new DatCho("DC01", "Nguyen Van A", null, taoKhungGio(), 2));
    }

    @Test
    void khungGioNullBiTuChoi() {
        assertThrows(IllegalArgumentException.class,
                () -> new DatCho("DC01", "Nguyen Van A", taoBan(), null, 2));
    }

    @Test
    void soKhachBang0BiTuChoi() {
        assertThrows(IllegalArgumentException.class,
                () -> new DatCho("DC01", "Nguyen Van A", taoBan(), taoKhungGio(), 0));
    }

    @Test
    void soKhachAmBiTuChoi() {
        assertThrows(IllegalArgumentException.class,
                () -> new DatCho("DC01", "Nguyen Van A", taoBan(), taoKhungGio(), -1));
    }

    @Test
    void soKhachVuotSucChuaVanDuocTao() {
        DatCho datCho = new DatCho(
                "DC01", "Nguyen Van A", taoBan(), taoKhungGio(), 5
        );

        assertEquals(5, datCho.soKhach());
    }

    @Test
    void cungDuLieuThiEqualsTrue() {
        DatCho datCho1 = new DatCho(
                "DC01", "Nguyen Van A", taoBan(), taoKhungGio(), 3
        );
        DatCho datCho2 = new DatCho(
                "DC01", "Nguyen Van A", taoBan(), taoKhungGio(), 3
        );

        assertEquals(datCho1, datCho2);
    }

    @Test
    void cungDuLieuThiHashCodeBangNhau() {
        DatCho datCho1 = new DatCho(
                "DC01", "Nguyen Van A", taoBan(), taoKhungGio(), 3
        );
        DatCho datCho2 = new DatCho(
                "DC01", "Nguyen Van A", taoBan(), taoKhungGio(), 3
        );

        assertEquals(datCho1.hashCode(), datCho2.hashCode());
    }

    @Test
    void khacSoKhachThiEqualsFalse() {
        DatCho datCho1 = new DatCho(
                "DC01", "Nguyen Van A", taoBan(), taoKhungGio(), 2
        );
        DatCho datCho2 = new DatCho(
                "DC01", "Nguyen Van A", taoBan(), taoKhungGio(), 3
        );

        assertNotEquals(datCho1, datCho2);
    }
}
