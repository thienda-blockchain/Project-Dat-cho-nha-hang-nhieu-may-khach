package de38_datchonhahang.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

// Test mẫu cho CN-02 và CN-03: record Ban so sánh theo giá trị, KetQua có đúng hai kiểu con.
class KetQuaTest {

    @Test
    void banCungGiaTriThiBangNhau() {
        assertEquals(new Ban("B01", 4), new Ban("B01", 4));
        assertNotEquals(new Ban("B01", 4), new Ban("B02", 4));
    }

    @Test
    void thanhCongGiuGiaTri() {
        KetQua<Ban> kq = new KetQua.ThanhCong<>(new Ban("B03", 4));
        assertEquals("OK B03", moTa(kq));
    }

    @Test
    void thatBaiGiuMaLoi() {
        KetQua<Ban> kq = new KetQua.ThatBai<>(MaLoi.BAN_BAN, "Bàn đã có người đặt");
        assertEquals("LOI BAN_BAN", moTa(kq));
    }

    // switch phải bao phủ đủ hai kiểu con của sealed interface, không cần nhánh default
    private static String moTa(KetQua<Ban> kq) {
        return switch (kq) {
            case KetQua.ThanhCong<Ban> tc -> "OK " + tc.giaTri().ma();
            case KetQua.ThatBai<Ban> tb -> "LOI " + tb.maLoi();
        };
    }
}
