package server;

import dto.Message;
import dto.MySocket;
import dto.Users;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class TestServer extends Thread {
    static ServerSocket serverSocket;
    MySocket socket;

    InputStream is;
    OutputStream os;

    Message message;

    public TestServer(Socket socket) throws IOException {
        this.socket = new MySocket();
        this.socket.setSocket(socket);

        is = socket.getInputStream();
        os = socket.getOutputStream();
    }

    @Override
    public void run() {
        try {
            byte[] buffer = new byte[Message.BUFFER_SIZE];

            while (is.read(buffer) > 0) {
                message = new Message(buffer);

                switch (message.getHead()) {
                    case "ID":
                        signIn(message.getPayload());
                        break;

                    case "GM":
                        Message msg = new Message("GR", socket.getId() + message.getPayload());
                        sendEveryone(msg.getMessage());
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
            exit();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendEveryone(byte[] msg) throws IOException {
        for (MySocket socket : Users.getInstance().getAllUser().values()) {
            if (socket.getId().equals(this.socket.getId()))
                continue;

            os = socket.getSocket().getOutputStream();
            os.write(msg);
            os.flush();
        }
    }

    private void signIn(String payload) throws IOException {
        String id = payload.substring(0, 4);
        String name = payload.substring(4);
        boolean isUR = false;

        if (isDuplicate(id)) {
            message = new Message("DR", "");
        } else {
            if (socket.isAdmin()) {
                socket.setAdmin();
            }
            socket.setId(id);
            socket.setName(name);

            Users.getInstance().putUser(id, socket);

            message = new Message("UR", getUsers());
            isUR = true;
        }

        os.write(message.getMessage());
        os.flush();

        if (isUR) {
            Message msg = new Message("JR", socket.toString());
            sendEveryone(msg.getMessage());
        }
    }

    private void exit() {
        try {
            System.out.println(socket.getId() + "의 접속이 끊겼습니다.");

            Users.getInstance().removeUser(socket.getId());

            Message message = new Message("DC", socket.getId());
            sendEveryone(message.getMessage());

            socket.close();
            is.close();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deport(String payload) throws IOException {
        Message message = new Message("WD", payload);
        sendEveryone(message.getMessage());

        Users.getInstance().removeUser(payload);
    }

    private void whisper(String payload) throws IOException {
        String receiveId = payload.substring(0, 4);
        String srPayload = socket.getId() + payload.substring(4);

        Message message = new Message("SR", srPayload);

        if (Users.getInstance().getUser(receiveId) != null) {
            os = Users.getInstance().getUser(receiveId).getSocket().getOutputStream();
            os.write(message.getMessage());
            os.flush();
        }
    }

    private String getUsers() {
        String users = "";

        for (MySocket socket : Users.getInstance().getAllUser().values()) {
            users += socket.toString() + ",";
        }
        users.substring(0, users.length()-2);

        return users;
    }

    private boolean isDuplicate(String id) {
        return Users.getInstance().getAllUser().containsKey(id);
    }

    public static void main(String[] args) {
        try {
            serverSocket = new ServerSocket(Message.PORT);
            System.out.println("서버가 열렸습니다.");

            while (true) {
                Socket socket = serverSocket.accept();
                TestServer thread = new TestServer(socket);
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
