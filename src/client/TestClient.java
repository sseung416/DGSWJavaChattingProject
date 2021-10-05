package client;

import dto.Message;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

public class TestClient {
    Socket socket;

    public TestClient(Socket socket) throws IOException {
        this.socket = socket;
    }

    public static void main(String[] args) {
        try {
            String ip = InetAddress.getLocalHost().getHostAddress();
            TestClient client = new TestClient(new Socket(ip, Message.PORT));

            ReceiveThread t1 = new ReceiveThread(client.socket);
            SendThread t2 = new SendThread(client.socket);

            t1.start();
            t2.start();

            t1.join();
            t2.join();

            if (t1.isInterrupted() || t2.isInterrupted()) {
                t1.interrupt();
                t2.interrupt();

                client.socket.close();
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
