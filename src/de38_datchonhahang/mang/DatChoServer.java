package de38_datchonhahang.mang;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de38_datchonhahang.luutru.CauHinhLoader;
import de38_datchonhahang.luutru.LuuTruException;
import de38_datchonhahang.luutru.LuuTruTrangThai;
import de38_datchonhahang.model.Ban;
import de38_datchonhahang.model.KhungGio;
import de38_datchonhahang.nghiepvu.DatChoService;
import de38_datchonhahang.nghiepvu.KhoTrangThai;
import de38_datchonhahang.nghiepvu.KhoaBan;

// Phục vụ CN-11 (máy chủ một khách) và CN-13 (nhiều khách qua bể luồng); cổng và cỡ bể đặt ở một chỗ.
public class DatChoServer {

    public static final int CONG_MAC_DINH = 5000;
    public static final int SO_LUONG_LUONG = 20;

    private final int cong;
    private final ExecutorService pool;
    private final BoXuLyLenh boXuLy;

    public DatChoServer(int cong, BoXuLyLenh boXuLy) {
        this.cong = cong;
        this.boXuLy = boXuLy;
        this.pool = Executors.newFixedThreadPool(SO_LUONG_LUONG); // CN-13 sẽ dùng
    }

    // args: <tệp bàn> <tệp khung giờ> <tệp trạng thái> [cổng]
    public static void main(String[] args) {
        if (args.length < 3) {
            System.err.println("Cú pháp: DatChoServer <ban.csv> <khung-gio.csv> <trang-thai.dat> [cổng]");
            return;
        }
        int cong = CONG_MAC_DINH;
        if (args.length > 3) {
            try {
                cong = Integer.parseInt(args[3]);
            } catch (NumberFormatException e) {
                System.err.println("Cổng phải là số: " + args[3]);
                return;
            }
        }
        try {
            List<Ban> cacBan = new CauHinhLoader().docBan(args[0]);
            List<KhungGio> cacGio = new CauHinhLoader().docKhungGio(args[1]);
            KhoTrangThai kho = new KhoTrangThai(cacBan, cacGio);
            LuuTruTrangThai luuTru = new LuuTruTrangThai(args[2]);
            kho.khoiPhuc(luuTru.nap());
            DatChoService service = new DatChoService(kho, new KhoaBan(cacBan), luuTru);
            new DatChoServer(cong, new BoXuLyLenh(service)).phucVuMotKhach();
        } catch (LuuTruException e) {
            System.err.println("Lỗi đọc tệp: " + e.getMessage());
        }
    }

    // CN-11: nhận đúng một khách rồi phục vụ tới khi khách đóng kết nối. CN-13 sẽ thay bằng chay().
    public void phucVuMotKhach() {
        try (ServerSocket server = new ServerSocket(cong);
                Socket socket = server.accept();
                BufferedReader vao = new BufferedReader(new InputStreamReader(socket.getInputStream(), UTF_8));
                PrintWriter ra = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), UTF_8), true)) {
            String dong;
            while ((dong = vao.readLine()) != null) {
                for (String phanHoi : boXuLy.xuLy(dong)) {
                    ra.println(phanHoi);
                }
            }
        } catch (BindException e) {
            System.err.println("Cổng " + cong + " đang bận, hãy đóng chương trình đang dùng cổng này hoặc chọn cổng khác.");
        } catch (IOException e) {
            System.err.println("Lỗi mạng: " + e.getMessage());
        }
    }

    public void chay() {
        throw new UnsupportedOperationException("CN-13: chưa cài đặt");
    }

    public void dung() {
        throw new UnsupportedOperationException("CN-13: chưa cài đặt");
    }
}