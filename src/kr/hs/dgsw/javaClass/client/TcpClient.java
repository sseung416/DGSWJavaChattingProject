package kr.hs.dgsw.javaClass.client;

import kr.hs.dgsw.javaClass.data.Message;
import kr.hs.dgsw.javaClass.utils.ErrorCode;
import kr.hs.dgsw.javaClass.utils.TcpServerException;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

public class TcpClient {
    private Socket socket;

    public TcpClient(Socket socket) throws IOException {
        this.socket = socket;
    }

    public static void main(String[] args) {
        try {
            String ip = InetAddress.getLocalHost().getHostAddress();
            TcpClient client = new TcpClient(new Socket(ip, Message.PORT));

            ReceiveThread t1 = new ReceiveThread(client.socket);
            SendThread t2 = new SendThread(client.socket);
            t1.setName("ClientReceiveThread");
            t2.setName("ClientSendThread");

            t1.setDaemon(true);
            t2.setDaemon(true);

            t1.start();
            t2.start();

            while (true) {
                if (!t1.isAlive() || !t2.isAlive()) {
                    break;
                }
            }

            client.socket.close();
        } catch (SocketException e) {
            throw new TcpServerException(ErrorCode.SERVER_SOCKET_FAIL);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
