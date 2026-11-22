package de38_datchonhahang.mang;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

// Phục vụ CN-13: một luồng phục vụ một máy khách (vòng readLine, ghi phản hồi, đóng socket).
public class ClientHandler implements Runnable {

    private final Socket socket;
    private final BoXuLyLenh boXuLy;

    public ClientHandler(Socket socket, BoXuLyLenh boXuLy) {
        this.socket = socket;
        this.boXuLy = boXuLy;
    }

    @Override
    public void run() {
        try (Socket s = socket;
                BufferedReader vao = new BufferedReader(new InputStreamReader(s.getInputStream(), UTF_8));
                PrintWriter ra = new PrintWriter(new OutputStreamWriter(s.getOutputStream(), UTF_8), true)) {
            String dong;
            while ((dong = vao.readLine()) != null) {
                for (String phanHoi : boXuLy.xuLy(dong)) {
                    ra.println(phanHoi);
                }
            }
        } catch (IOException e) {
            System.err.println("Lỗi mạng với một khách: " + e.getMessage());
        } catch (RuntimeException e) { // lỗi không lường trước: chỉ đóng khách này, không làm sập bể luồng
            System.err.println("Lỗi không lường trước, đóng kết nối khách: " + e);
        }
    }
}