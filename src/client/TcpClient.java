package client;

import dto.Message;
import utils.ErrorCode;
import utils.TcpServerException;

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

            t1.start();
            t2.start();

            t1.join();
            if (!t1.isAlive()) {
                t2.interrupt();
            }
            t2.join();

            client.socket.close();
        } catch (SocketException e) {
            throw new TcpServerException(ErrorCode.SERVER_SOCKET_FAIL);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
        }
    }


}
