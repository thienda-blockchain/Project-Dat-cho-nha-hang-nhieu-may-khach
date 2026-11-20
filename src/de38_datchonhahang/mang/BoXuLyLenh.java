package de38_datchonhahang.mang;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de38_datchonhahang.model.DatCho;
import de38_datchonhahang.model.KetQua;
import de38_datchonhahang.model.KhungGio;
import de38_datchonhahang.model.MaLoi;
import de38_datchonhahang.nghiepvu.DatChoService;

// Phục vụ CN-10: phân tích dòng lệnh, gọi nghiệp vụ, định dạng phản hồi OK / LOI / END.
public class BoXuLyLenh {

    private final DatChoService service;

    public BoXuLyLenh(DatChoService service) {
        this.service = service;
    }

    public List<String> xuLy(String dong) {
        try {
            String s = dong == null ? "" : dong.strip();
            if (s.isEmpty()) {
                return loi(MaLoi.SAI_THAM_SO, "Dòng lệnh rỗng");
            }
            String[] t = s.split("\\s+");
            LoaiLenh lenh;
            try {
                lenh = LoaiLenh.valueOf(t[0].toUpperCase());
            } catch (IllegalArgumentException e) {
                return loi(MaLoi.SAI_THAM_SO, "Lệnh không hợp lệ: " + t[0]);
            }
            return switch (lenh) {
                case XEM -> xem(t);
                case DAT -> dat(t);
                case HUY -> huy(t);
            };
        } catch (RuntimeException e) { // một lệnh hỏng không được làm sập luồng phục vụ
            return loi(MaLoi.LOI_HE_THONG, "Lỗi hệ thống");
        }
    }

    private List<String> xem(String[] t) {
        if (t.length > 2) {
            return loi(MaLoi.SAI_THAM_SO, "XEM nhận tối đa một tham số");
        }
        KetQua<List<String>> kq;
        if (t.length == 1) {
            kq = service.xemTatCa();
        } else {
            Integer gio = soNguyen(t[1]);
            if (gio == null) {
                return loi(MaLoi.SAI_THAM_SO, "Khung giờ phải là số");
            }
            kq = service.xem(new KhungGio(gio));
        }
        if (kq instanceof KetQua.ThanhCong<List<String>> tc) {
            List<String> r = new ArrayList<>();
            r.add("OK XEM");
            r.addAll(tc.giaTri());
            r.add("END");
            return r;
        }
        return loiTu(kq);
    }

    private List<String> dat(String[] t) {
        if (t.length < 5) {
            return loi(MaLoi.SAI_THAM_SO, "Cú pháp: DAT <maBan> <gio> <soKhach> <tenKhach>");
        }
        Integer gio = soNguyen(t[2]);
        Integer soKhach = soNguyen(t[3]);
        if (gio == null || soKhach == null) {
            return loi(MaLoi.SAI_THAM_SO, "Giờ và số khách phải là số");
        }
        String ten = String.join(" ", Arrays.copyOfRange(t, 4, t.length));
        KetQua<DatCho> kq = service.dat(t[1], new KhungGio(gio), soKhach, ten);
        if (kq instanceof KetQua.ThanhCong<DatCho> tc) {
            return List.of("OK DAT " + tc.giaTri().maDat(), "END");
        }
        return loiTu(kq);
    }

    private List<String> huy(String[] t) {
        if (t.length < 3) {
            return loi(MaLoi.SAI_THAM_SO, "Cú pháp: HUY <maDat> <tenKhach>");
        }
        String ten = String.join(" ", Arrays.copyOfRange(t, 2, t.length));
        KetQua<DatCho> kq = service.huy(t[1], ten);
        if (kq instanceof KetQua.ThanhCong<DatCho> tc) {
            return List.of("OK HUY " + tc.giaTri().maDat(), "END");
        }
        return loiTu(kq);
    }

    private static Integer soNguyen(String s) {
        try {
            return Integer.valueOf(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static List<String> loiTu(KetQua<?> kq) {
        KetQua.ThatBai<?> tb = (KetQua.ThatBai<?>) kq;
        return loi(tb.maLoi(), tb.thongBao());
    }

    private static List<String> loi(MaLoi ma, String thongBao) {
        return List.of("LOI " + ma + " " + thongBao, "END");
    }
}