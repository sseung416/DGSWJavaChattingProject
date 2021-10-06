package kr.hs.dgsw.javaClass.server;

import kr.hs.dgsw.javaClass.data.Message;
import kr.hs.dgsw.javaClass.data.MySocket;
import kr.hs.dgsw.javaClass.data.Users;
import kr.hs.dgsw.javaClass.utils.*;

import java.io.*;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class TcpServer extends Thread {
    static ServerSocket serverSocket;
    MySocket socket;

    InputStream is;
    OutputStream os;

    Users users;
    CheckValid checkValid;

    public TcpServer(Socket socket) throws IOException {
        this.socket = new MySocket();
        this.socket.setSocket(socket);

        is = socket.getInputStream();
        os = socket.getOutputStream();

        users = new Users();
        checkValid = new CheckValid();
    }

    @Override
    public void run() {
        try {
            byte[] buffer = new byte[Message.BUFFER_SIZE];

            while (is.read(buffer) > 0) {
                Message message = new Message(buffer);

                switch (message.getHead()) {
                    case "ID":
                        signIn(message.getPayload());
                        break;

                    case "GM":
                        Message msg = new Message(Payload.GR.getHead(), socket.getId() + message.getPayload());
                        sendEveryone(msg.getMessage(), false);
                        break;

                    case "SM":
                        whisper(message.getPayload());
                        break;

                    case "WD":
                        deport(message.getPayload());
                        break;
                }
            }
        } catch (SocketException e){
            exit(socket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendEveryone(byte[] msg, boolean sendMe) throws IOException {
        MySocket err = null;
        try {
            for (MySocket socket : users.getAllUser().values()) {
                err = socket;

                os = socket.getSocket().getOutputStream();

                os.write(msg);
                os.flush();
            }
        } catch (SocketException e) {
            if (!err.equals(socket))
                exit(err);
        }
    }

    private void signIn(String payload) throws IOException {
        String id = payload.substring(0, 4);
        String name = payload.substring(4);
        boolean isUR = false;

        Message message;
        if (checkValid.isDuplicate(id)) {
            message = new Message(Payload.DR.getHead(), "");
        } else {
            // 방장 설정
            if (users.getAllUser().size() == 0) {
                socket.setAdmin();
            }
            socket.setId(id);
            socket.setName(name);

            users.putUser(id, socket);

            message = new Message(Payload.UR.getHead(), getUsers());
            isUR = true;
        }

        os.write(message.getMessage());
        os.flush();

        // 중복된 사용자라면(보내는 메세지가 DR이라면) JR 메세지를 보내지 않음
        if(isUR) {
            Message msg = new Message(Payload.JR.getHead(), socket.toString());
            sendEveryone(msg.getMessage(), false);
        }
    }

    private void exit(MySocket socket) {
        try {
            users.removeUser(socket.getId());

            Message message = new Message(Payload.DC.getHead(), socket.getId());
            sendEveryone(message.getMessage(), false);

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deport(String payload) throws IOException {
        if (!socket.isAdmin()) {
            return;
        }

        Message message;

        // 지목된 클라이언트 추방
        message = new Message(Payload.WR.getHead(), "");
        if (checkValid.isNull(users.getUser(payload))) {
            return;
        }

        OutputStream os = users.getUser(payload).getSocket().getOutputStream();
        os.write(message.getMessage());

        // 지목되지 않은 클라이언트에게 추방 메시지 전달
        message = new Message(Payload.WA.getHead(), payload);
        sendEveryone(message.getMessage(), true);

        users.removeUser(payload);
    }

    private void whisper(String payload) throws IOException {
        String receiveId = payload.substring(0, 4);
        String srPayload = socket.getId() + payload.substring(4);

        Message message = new Message(Payload.SR.getHead(), srPayload);

        if (!checkValid.isNull(users.getUser(receiveId))) {
            os = users.getUser(receiveId).getSocket().getOutputStream();
            os.write(message.getMessage());
            os.flush();
        }
    }

    // UR로 보낼 메세지를 반환하는 메서드 (id이름,id이름,....)
    private String getUsers() {
        StringBuilder users = new StringBuilder();

        for (MySocket socket : this.users.getAllUser().values()) {
            users.append(socket.toString()).append(",");
        }
        users.substring(0, users.length() - 2);

        return users.toString();
    }

    public static void main(String[] args) {
        try {
            serverSocket = new ServerSocket(Message.PORT);
            System.out.println(SystemMessage.OPEN_SERVER.getMessage());

            while (true) {
                Socket socket = serverSocket.accept();
                TcpServer thread = new TcpServer(socket);
                thread.start();
            }
        } catch (BindException e) {
            throw new TcpServerException(ErrorCode.PORT_ALREADY_OCCUPIED);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
