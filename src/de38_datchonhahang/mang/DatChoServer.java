package de38_datchonhahang.mang;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

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
    private volatile boolean dangChay = true;
    private volatile ServerSocket serverSocket;

    public DatChoServer(int cong, BoXuLyLenh boXuLy) {
        this.cong = cong;
        this.boXuLy = boXuLy;
        this.pool = Executors.newFixedThreadPool(SO_LUONG_LUONG);
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
            new DatChoServer(cong, new BoXuLyLenh(service)).chay();
        } catch (LuuTruException e) {
            System.err.println("Lỗi đọc tệp: " + e.getMessage());
        }
    }

    // CN-11: nhận đúng một khách rồi phục vụ tới khi khách đóng kết nối.
    public void phucVuMotKhach() {
        try (ServerSocket server = new ServerSocket(cong);
                Socket socket = server.accept()) {
            new ClientHandler(socket, boXuLy).run();
        } catch (BindException e) {
            System.err.println("Cổng " + cong + " đang bận, hãy đóng chương trình đang dùng cổng này hoặc chọn cổng khác.");
        } catch (IOException e) {
            System.err.println("Lỗi mạng: " + e.getMessage());
        }
    }

    // CN-13: vòng accept vô hạn, mỗi khách một ClientHandler chạy trên bể luồng.
    public void chay() {
        try (ServerSocket server = new ServerSocket(cong)) {
            serverSocket = server;
            while (dangChay) {
                Socket socket = server.accept();
                try {
                    pool.execute(new ClientHandler(socket, boXuLy));
                } catch (RejectedExecutionException e) { // bể đã đóng do dung()
                    socket.close();
                    break;
                }
            }
        } catch (BindException e) {
            System.err.println("Cổng " + cong + " đang bận, hãy đóng chương trình đang dùng cổng này hoặc chọn cổng khác.");
        } catch (IOException e) {
            if (dangChay) { // dung() đóng ServerSocket làm accept ném lỗi: đó là dừng bình thường
                System.err.println("Lỗi máy chủ: " + e.getMessage());
            }
        } finally {
            pool.shutdown();
        }
    }

    // Dừng sạch: hạ cờ, đóng ServerSocket để accept thoát, không nhận thêm việc cho bể.
    public void dung() {
        dangChay = false;
        ServerSocket server = serverSocket;
        if (server != null) {
            try {
                server.close();
            } catch (IOException e) {
                System.err.println("Lỗi khi đóng cổng: " + e.getMessage());
            }
        }
        pool.shutdown();
    }
}