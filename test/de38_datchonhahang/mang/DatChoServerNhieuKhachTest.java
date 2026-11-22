package de38_datchonhahang.mang;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

// Test CN-13: máy chủ phục vụ nhiều khách cùng lúc bằng bể luồng, dùng socket thật trên localhost.
@Timeout(20)
class DatChoServerNhieuKhachTest {

    private int cong;
    private DatChoServer server;
    private Thread luongServer;

    // Một khách thử nghiệm: gửi một dòng, đọc phản hồi tới END. Quá 3 giây không có phản hồi thì lỗi.
    private static final class Khach implements AutoCloseable {
        final Socket socket;
        final BufferedReader vao;
        final PrintWriter ra;

        Khach(int cong) throws Exception {
            Socket s = null;
            for (int i = 0; i < 60 && s == null; i++) {
                try {
                    s = new Socket("localhost", cong);
                } catch (IOException e) {
                    Thread.sleep(50); // server khởi động chậm hơn client một chút
                }
            }
            if (s == null) {
                throw new IOException("Không nối được tới server thử nghiệm");
            }
            socket = s;
            socket.setSoTimeout(3000);
            vao = new BufferedReader(new InputStreamReader(socket.getInputStream(), UTF_8));
            ra = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), UTF_8), true);
        }

        List<String> gui(String dong) throws IOException {
            ra.println(dong);
            List<String> kq = new ArrayList<>();
            String p;
            while ((p = vao.readLine()) != null && !p.equals("END")) {
                kq.add(p);
            }
            return kq;
        }

        boolean biServerDong() {
            try {
                return vao.readLine() == null;
            } catch (IOException e) {
                return true; // bị reset cũng là đã đóng
            }
        }

        @Override
        public void close() throws IOException {
            socket.close();
        }
    }

    private static BoXuLyLenh boXuLyGia() {
        return new BoXuLyLenh(null) {
            @Override
            public List<String> xuLy(String dong) {
                if (dong.equals("NO")) {
                    throw new IllegalStateException("lỗi giả lập trong xử lý lệnh");
                }
                return List.of("OK " + dong, "END");
            }
        };
    }

    @BeforeEach
    void batServer() throws IOException {
        try (ServerSocket tam = new ServerSocket(0)) {
            cong = tam.getLocalPort();
        }
        server = new DatChoServer(cong, boXuLyGia());
        luongServer = new Thread(server::chay);
        luongServer.start();
    }

    @AfterEach
    void tatServer() throws InterruptedException {
        server.dung();
        luongServer.join(3000);
    }

    @Test
    void khachDangNhanRoiVanPhucVuDuocKhachKhac() throws Exception {
        try (Khach a = new Khach(cong); Khach b = new Khach(cong)) { // a nối rồi để yên
            assertEquals(List.of("OK ping"), b.gui("ping"));
            assertEquals(List.of("OK xin chao"), a.gui("xin chao"));
        }
    }

    @Test
    void muoiKhachGuiDongThoiMoiKhachNhanDungPhanHoiCuaMinh() throws Exception {
        int soKhach = 10;
        ExecutorService khach = Executors.newFixedThreadPool(soKhach);
        try {
            List<Future<?>> viec = new ArrayList<>();
            for (int id = 0; id < soKhach; id++) {
                int k = id;
                viec.add(khach.submit(() -> {
                    try (Khach x = new Khach(cong)) {
                        for (int j = 0; j < 20; j++) {
                            String lenh = "k" + k + "-" + j;
                            assertEquals(List.of("OK " + lenh), x.gui(lenh));
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }));
            }
            for (Future<?> f : viec) {
                f.get(); // có khách nào sai thì ném lỗi ở đây
            }
        } finally {
            khach.shutdownNow();
        }
    }

    @Test
    void loiTrongXuLyLenhChiDongKhachDoKhongSapServer() throws Exception {
        try (Khach hong = new Khach(cong)) {
            hong.ra.println("NO");
            assertTrue(hong.biServerDong(), "Khách gây lỗi bị đóng kết nối");
        }
        try (Khach tot = new Khach(cong)) {
            assertEquals(List.of("OK con song"), tot.gui("con song"));
        }
    }

    @Test
    void dungThiVongAcceptKetThucGon() throws Exception {
        new Khach(cong).close(); // chắc chắn server đã mở cổng
        server.dung();
        luongServer.join(3000);
        assertFalse(luongServer.isAlive(), "dung() phải làm chay() thoát");
    }
}