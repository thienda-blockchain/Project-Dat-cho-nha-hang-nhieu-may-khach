package de38_datchonhahang.mang;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

// Test CN-11: máy chủ phục vụ một khách, dùng socket thật trên localhost.
@Timeout(10)
class DatChoServerTest {

    private static int congTrong() throws IOException {
        try (ServerSocket tam = new ServerSocket(0)) {
            return tam.getLocalPort();
        }
    }

    // Chạy server ở luồng nền, trả về luồng để chờ nó kết thúc.
    private static Thread chayNen(DatChoServer server) {
        Thread t = new Thread(server::phucVuMotKhach);
        t.start();
        return t;
    }

    // Server khởi động chậm hơn client một chút: thử nối lại tới 3 giây.
    private static Socket noiToi(int cong) throws Exception {
        for (int i = 0; i < 60; i++) {
            try {
                return new Socket("localhost", cong);
            } catch (IOException e) {
                Thread.sleep(50);
            }
        }
        throw new IOException("Không nối được tới server thử nghiệm");
    }

    private static BoXuLyLenh boXuLyGia() {
        return new BoXuLyLenh(null) {
            @Override
            public List<String> xuLy(String dong) {
                return List.of("OK " + dong, "dong phu", "END");
            }
        };
    }

    @Test
    void traLoiTungLenhDenEnd() throws Exception {
        int cong = congTrong();
        Thread server = chayNen(new DatChoServer(cong, boXuLyGia()));
        try (Socket s = noiToi(cong);
                BufferedReader vao = new BufferedReader(new InputStreamReader(s.getInputStream(), UTF_8));
                PrintWriter ra = new PrintWriter(new OutputStreamWriter(s.getOutputStream(), UTF_8), true)) {
            ra.println("XEM");
            assertEquals("OK XEM", vao.readLine());
            assertEquals("dong phu", vao.readLine());
            assertEquals("END", vao.readLine());
            ra.println("HUY D1 An"); // lệnh thứ hai trên cùng kết nối
            assertEquals("OK HUY D1 An", vao.readLine());
        }
        server.join(3000);
        assertFalse(server.isAlive(), "Khách đóng kết nối thì server phải kết thúc gọn");
    }

    @Test
    void khachDongDotNgotKhongLamServerNemLoi() throws Exception {
        int cong = congTrong();
        Thread server = chayNen(new DatChoServer(cong, boXuLyGia()));
        noiToi(cong).close(); // nối rồi đóng ngay, chưa gửi gì
        server.join(3000);
        assertFalse(server.isAlive());
    }

    @Test
    void ghepVoiBoXuLyLenhThat() throws Exception {
        int cong = congTrong();
        Thread server = chayNen(new DatChoServer(cong, new BoXuLyLenh(null)));
        try (Socket s = noiToi(cong);
                BufferedReader vao = new BufferedReader(new InputStreamReader(s.getInputStream(), UTF_8));
                PrintWriter ra = new PrintWriter(new OutputStreamWriter(s.getOutputStream(), UTF_8), true)) {
            ra.println("BAY 1 2");
            assertTrue(vao.readLine().startsWith("LOI SAI_THAM_SO"));
            assertEquals("END", vao.readLine());
        }
        server.join(3000);
    }

    @Test
    void congDangBanThiBaoRoVaKhongNemLoi() throws Exception {
        PrintStream errCu = System.err;
        ByteArrayOutputStream ra = new ByteArrayOutputStream();
        System.setErr(new PrintStream(ra, true, UTF_8));
        try (ServerSocket chiem = new ServerSocket(0)) {
            DatChoServer server = new DatChoServer(chiem.getLocalPort(), boXuLyGia());
            assertDoesNotThrow(server::phucVuMotKhach);
        } finally {
            System.setErr(errCu);
        }
        assertTrue(ra.toString(UTF_8).contains("đang bận"));
    }
}