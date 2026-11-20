package de38_datchonhahang.giaodien;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

// Test CN-12: máy khách console, dùng một server giả chạy trong test.
@Timeout(5)
class DatChoClientTest {

    private InputStream inCu;
    private PrintStream outCu;
    private ByteArrayOutputStream ra;

    @BeforeEach
    void chuanBi() {
        inCu = System.in;
        outCu = System.out;
        ra = new ByteArrayOutputStream();
        System.setOut(new PrintStream(ra, true, UTF_8));
    }

    @AfterEach
    void khoiPhuc() {
        System.setIn(inCu);
        System.setOut(outCu);
    }

    private void banPhim(String noiDung) {
        System.setIn(new ByteArrayInputStream(noiDung.getBytes(UTF_8)));
    }

    // Server giả: nhận từng dòng, trả về phanHoi; nếu phanHoi rỗng thì đóng kết nối ngay sau dòng đầu.
    private static final class ServerGia implements AutoCloseable {
        final ServerSocket ss;
        final List<String> nhan = new CopyOnWriteArrayList<>();
        volatile IOException loi;
        private final Thread luong;

        ServerGia(List<String> phanHoi) throws IOException {
            ss = new ServerSocket(0);
            luong = new Thread(() -> {
                try (Socket s = ss.accept();
                        BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream(), UTF_8));
                        PrintWriter out = new PrintWriter(new OutputStreamWriter(s.getOutputStream(), UTF_8), true)) {
                    String dong;
                    while ((dong = in.readLine()) != null) {
                        nhan.add(dong);
                        if (phanHoi.isEmpty()) {
                            return;
                        }
                        phanHoi.forEach(out::println);
                    }
                } catch (IOException e) {
                    loi = e;
                }
            });
            luong.start();
        }

        int cong() {
            return ss.getLocalPort();
        }

        @Override
        public void close() throws IOException, InterruptedException {
            ss.close();
            luong.join(2000);
        }
    }

    @Test
    void inPhanHoiDenEndKhongInEnd() throws Exception {
        try (ServerGia server = new ServerGia(List.of("OK XEM", "dong1", "END"))) {
            banPhim("XEM\n");
            new DatChoClient("localhost", server.cong()).chay();
            assertEquals(List.of("XEM"), server.nhan);
            assertEquals(List.of("OK XEM", "dong1"), ra.toString(UTF_8).lines().toList());
            assertNull(server.loi);
        }
    }

    @Test
    void dongRongBoQuaKhongGuiLenServer() throws Exception {
        try (ServerGia server = new ServerGia(List.of("OK XEM", "END"))) {
            banPhim("\n   \nXEM\n");
            new DatChoClient("localhost", server.cong()).chay();
            assertEquals(List.of("XEM"), server.nhan);
        }
    }

    @Test
    void serverChuaChayThiBaoGonKhongNemLoi() throws Exception {
        int congKhongAiNghe;
        try (ServerSocket tam = new ServerSocket(0)) {
            congKhongAiNghe = tam.getLocalPort();
        }
        banPhim("XEM\n");
        DatChoClient client = new DatChoClient("localhost", congKhongAiNghe);
        assertDoesNotThrow(client::chay);
        assertTrue(ra.toString(UTF_8).contains("Không kết nối được"));
    }

    @Test
    void serverDongGiuaPhienThiThoatSach() throws Exception {
        try (ServerGia server = new ServerGia(List.of())) {
            banPhim("XEM\n");
            new DatChoClient("localhost", server.cong()).chay();
            assertTrue(ra.toString(UTF_8).contains("Server đã đóng"));
        }
    }
}