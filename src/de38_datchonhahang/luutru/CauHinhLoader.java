
package de38_datchonhahang.luutru;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import de38_datchonhahang.model.Ban;
import de38_datchonhahang.model.KhungGio;

// Phục vụ CN-04: nạp danh sách bàn và khung giờ từ tệp văn bản cấu hình.
public class CauHinhLoader {

    public List<Ban> docBan(String duongDan) throws LuuTruException {
        List<Ban> danhSach = new ArrayList<>();

        try (BufferedReader reader = moTep(duongDan)) {
            String tieuDe = reader.readLine();

            if (!"ma,sucChua".equals(tieuDe)) {
                throw new LuuTruException("Tệp bàn có tiêu đề không hợp lệ");
            }

            String dong;
            int soDong = 1;

            while ((dong = reader.readLine()) != null) {
                soDong++;

                if (dong.isBlank()) {
                    continue;
                }

                String[] cot = dong.split(",", -1);

                if (cot.length != 2) {
                    throw new LuuTruException(
                            "Tệp bàn sai số lượng cột tại dòng " + soDong);
                }

                try {
                    String ma = cot[0].trim();
                    int sucChua = Integer.parseInt(cot[1].trim());

                    danhSach.add(new Ban(ma, sucChua));
                } catch (IllegalArgumentException e) {
                    throw new LuuTruException(
                            "Dữ liệu bàn không hợp lệ tại dòng " + soDong, e);
                }
            }

            return danhSach;

        } catch (IOException e) {
            throw new LuuTruException("Không thể đọc tệp cấu hình bàn: " + duongDan, e);
        }
    }

    public List<KhungGio> docKhungGio(String duongDan) throws LuuTruException {
        List<KhungGio> danhSach = new ArrayList<>();

        try (BufferedReader reader = moTep(duongDan)) {
            String tieuDe = reader.readLine();

            if (!"gioBatDau".equals(tieuDe)) {
                throw new LuuTruException("Tệp khung giờ có tiêu đề không hợp lệ");
            }

            String dong;
            int soDong = 1;

            while ((dong = reader.readLine()) != null) {
                soDong++;

                if (dong.isBlank()) {
                    continue;
                }

                try {
                    int gioBatDau = Integer.parseInt(dong.trim());
                    danhSach.add(new KhungGio(gioBatDau));
                } catch (IllegalArgumentException e) {
                    throw new LuuTruException(
                            "Dữ liệu khung giờ không hợp lệ tại dòng " + soDong, e);
                }
            }

            return danhSach;

        } catch (IOException e) {
            throw new LuuTruException("Không thể đọc tệp cấu hình khung giờ: " + duongDan, e);
        }
    }

    private BufferedReader moTep(String duongDan) throws LuuTruException {
        if (duongDan == null || duongDan.isBlank()) {
            throw new LuuTruException("Đường dẫn tệp không được để trống");
        }

        try {
            return Files.newBufferedReader(
                    Path.of(duongDan), StandardCharsets.UTF_8);
        } catch (IOException | RuntimeException e) {
            throw new LuuTruException("Không thể mở tệp: " + duongDan, e);
        }
    }
}
