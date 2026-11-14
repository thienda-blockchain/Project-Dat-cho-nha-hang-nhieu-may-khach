package de38_datchonhahang.model;

// Phục vụ CN-03, CN-06 đến CN-09: kiểu kết quả chung của mọi phép nghiệp vụ (thành công hoặc thất bại kèm mã lỗi).
public sealed interface KetQua<T> {

    record ThanhCong<T>(T giaTri) implements KetQua<T> {
    }

    record ThatBai<T>(MaLoi maLoi, String thongBao) implements KetQua<T> {
    }
}
