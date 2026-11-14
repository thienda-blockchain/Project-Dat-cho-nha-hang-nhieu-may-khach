package de38_datchonhahang.mang;

import java.net.Socket;

// Phục vụ CN-13: một luồng phục vụ một máy khách (vòng readLine, ghi phản hồi, đóng socket).
public class ClientHandler implements Runnable {

    private final Socket socket;
    private final BoXuLyLenh boXuLy;

    public ClientHandler(Socket socket, BoXuLyLenh boXuLy) {
        throw new UnsupportedOperationException("CN-13: chưa cài đặt");
    }

    @Override
    public void run() {
        throw new UnsupportedOperationException("CN-13: chưa cài đặt");
    }
}
