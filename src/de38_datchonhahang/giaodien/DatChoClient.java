package de38_datchonhahang.giaodien;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;

import de38_datchonhahang.mang.DatChoServer;

// Phục vụ CN-12: máy khách console, gửi lệnh từ bàn phím và in phản hồi tới dòng END.
public class DatChoClient {

    private final String host;
    private final int cong;

    public DatChoClient(String host, int cong) {
        this.host = host;
        this.cong = cong;
    }

    // args: [host] [cổng]
    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : "localhost";
        int cong = DatChoServer.CONG_MAC_DINH;
        if (args.length > 1) {
            try {
                cong = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.out.println("Cổng phải là số: " + args[1]);
                return;
            }
        }
        new DatChoClient(host, cong).chay();
    }

    public void chay() {
        try (Socket socket = new Socket(host, cong);
                BufferedReader tuServer = new BufferedReader(new InputStreamReader(socket.getInputStream(), UTF_8));
                PrintWriter toiServer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), UTF_8), true);
                BufferedReader banPhim = new BufferedReader(new InputStreamReader(System.in, UTF_8))) {
            String dong;
            while ((dong = banPhim.readLine()) != null) {
                if (dong.isBlank()) {
                    continue; // dòng rỗng: bỏ qua, không thoát
                }
                toiServer.println(dong);
                String p;
                while ((p = tuServer.readLine()) != null && !p.equals("END")) {
                    System.out.println(p);
                }
                if (p == null) {
                    System.out.println("Server đã đóng kết nối.");
                    return;
                }
            }
        } catch (ConnectException e) {
            System.out.println("Không kết nối được tới server " + host + ":" + cong);
        } catch (IOException e) {
            System.out.println("Lỗi mạng: " + e.getMessage());
        }
    }
}