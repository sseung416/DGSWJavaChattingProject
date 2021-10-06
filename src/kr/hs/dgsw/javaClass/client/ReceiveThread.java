package kr.hs.dgsw.javaClass.client;

import kr.hs.dgsw.javaClass.data.Message;
import kr.hs.dgsw.javaClass.data.MySocket;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketException;

public class ReceiveThread extends Thread { // 서버에서 보낸 메세지 읽음

    private MySocket socket;

    private InputStream is;

    public ReceiveThread(Socket socket) throws IOException {
        this.socket = new MySocket();
        this.socket.setSocket(socket);

        is = socket.getInputStream();
    }

    @Override
    public void run() {
        try {
            byte[] buffer = new byte[Message.BUFFER_SIZE];
            Message message;

            while (is.read(buffer) > 0) {
                if (isInterrupted()) {
                    return;
                }

                message = new Message(buffer);

                switch (message.getHead()) {
                    case "UR":
                        System.out.println();
                        System.out.println("[ 채팅방에 오신 걸 환영합니다! ]");
                        System.out.println("[ 명령어 ]");
                        System.out.println("[ '/e userId': 추방 ]");
                        System.out.println("[ '/w userId message': 귓속말 ]");
                        System.out.println("[ '/q': 나가기 ]");

                        showUserList(message.getPayload());
                        break;

                    case "JR":
                        System.out.println("[ " + message.getId() + "님이 입장하셨습니다. ]");
                        break;

                    case "DR":
                        System.out.println("[ 중복된 아이디이므로, 접속을 중단합니다. ]");
                        this.interrupt();
                        break;

                    case "GR":
                        System.out.println(message.getId() + "> " + message.getMsg());
                        break;

                    case "SR":
                        System.out.println("(귓속말 | " + message.getId() + "> " + message.getMsg() + ")");
                        break;

                    case "DC":
                        System.out.println("[ " + message.getId() + "님이 채팅방을 나가셨습니다. ]");
                        break;

                    case "WR":
                        System.out.println("[ 당신은 채팅방에서 추방되었습니다. ]");
                        is.close();
                        socket.close();
                        return;

                    case "WA":
                        System.out.println("[ " + message.getId() + "님이 채팅방에서 추방되었습니다. ]");
                }
            }
        } catch (SocketException e) {
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showUserList(String payload) throws IOException {
        System.out.println("[ 유저 리스트 ]");

        for (String user: payload.split(",")) {
            String id = user.substring(0, 4);
            String name = user.substring(4);

            System.out.printf("%s (%s)%n", id, name);
        }
        System.out.println();
    }
}
