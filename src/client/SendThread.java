package client;

import dto.Message;
import dto.MySocket;
import test.ErrorCode;
import test.TcpServerException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;
import java.util.regex.Pattern;

public class SendThread extends Thread { // 서버에 메세지 보냄
    private MySocket socket;
    private Scanner sc;
    OutputStream os;

    public SendThread(Socket socket) throws IOException {
        this.socket = new MySocket();
        this.socket.setSocket(socket);
        sc = new Scanner(System.in);
        os = socket.getOutputStream();
    }

    @Override
    public void run() {
        try {
            signIn();

            String line;
            Message message = null;

            while (!(line = sc.nextLine()).equals("")) {
                // 0: 명령어, 1: 아이디, 2: 메세지
                String[] command = line.split(" ");

                if (line.charAt(0) == '/' && !isCorrectCommand(command)) {
                    System.out.println("[ 잘못된 명령어입니다. ]");
                    continue;
                }

                switch (command[0]) {
                    case "/e":
                        if (!isId(command[1])) {
                            System.out.println("[ 잘못된 아이디 형식입니다. ]");
                            break;
                        }
                        message = new Message("WD", command[1]);
                        break;
                    case "/w":
                        if (!isId(command[1])) {
                            System.out.println("[ 잘못된 아이디 형식입니다. ]");
                            break;
                        }
                        message = new Message("SM", command[1] + command[2]);
                        break;
                    case "/q":
                        System.out.println("[ 챗팅을 종료합니다. ]");
                        this.interrupt();
                        break;
                    default:
                        message = new Message("GM", line);
                }

                if (isInterrupted()) {
                    break;
                }

                if (message != null) {
                    os.write(message.getMessage());
                }
                os.flush();
            }
        } catch (SocketException e) {
            e.printStackTrace();
//            throw new TcpServerException(ErrorCode.SERVER_SOCKET_FAIL);
        } catch (IOException e) {
//            throw new TcpServerException(ErrorCode.MESSAGE_SEND_FAIL);
            e.printStackTrace();
        }
    }

    private boolean isId(String s) {
        return Pattern.matches("\\d{4}", s);
    }

    private boolean isCorrectCommand(String[] command) {
        return command.length == 3;
    }

    private void signIn() throws IOException {
        System.out.println("[ 아이디와 이름을 입력해주세요. ]");
        socket.setId(getCorrectId());
        socket.setName(getCorrectName());

        Message message = new Message("ID", socket.getId() + socket.getName());

        os.write(message.getMessage());
        os.flush();
    }

    private String getCorrectId() {
        String id;

        while (true) {
            System.out.print("id: ");
            id = sc.nextLine();

            if (!Pattern.matches("\\d{4}", id)) {
                System.out.println("[ 4자리의 숫자를 입력해주세요. ]");
                continue;
            }
            break;
        }

        return id;
    }

    private String getCorrectName() {
        String name;

        while (true) {
            System.out.print("이름: ");
            name = sc.nextLine();
            boolean it = !Pattern.matches("^[0-9a-zA-Zㄱ-ㅎㅏ-ㅣ가-힣]*$", name);
            if (it) {
                System.out.println("[ 이름에 특수문자는 들어갈 수 없습니다. ]");
                continue;
            } else if (name.length() == 0) {
                System.out.println("[ 이름을 입력해주세요. ]");
                continue;
            }

            break;
        }

        return name;
    }


}
