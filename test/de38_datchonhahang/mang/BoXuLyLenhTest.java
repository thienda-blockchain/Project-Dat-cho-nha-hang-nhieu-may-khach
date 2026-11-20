package de38_datchonhahang.mang;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

// Test CN-10: các đường lỗi không gọi tới nghiệp vụ nên dùng service = null.
class BoXuLyLenhTest {

    private final BoXuLyLenh boXuLy = new BoXuLyLenh(null);

    private void assertLoi(String dong) {
        List<String> r = boXuLy.xuLy(dong);
        assertEquals(2, r.size(), "dòng: " + dong);
        assertTrue(r.get(0).startsWith("LOI SAI_THAM_SO"), r.get(0));
        assertEquals("END", r.get(1));
    }

    @Test void dongRong()               { assertLoi(""); assertLoi("   "); assertLoi(null); }
    @Test void lenhLa()                 { assertLoi("BAY 1 2"); }
    @Test void datThieuThamSo()         { assertLoi("DAT B01 12 2"); }
    @Test void datSoKhachKhongPhaiSo()  { assertLoi("dat B01 12 hai An"); }
    @Test void xemThuaThamSo()          { assertLoi("XEM 12 13"); }
    @Test void xemGioKhongPhaiSo()      { assertLoi("  xem   abc "); }
    @Test void huyThieuTen()            { assertLoi("HUY D1"); }
}