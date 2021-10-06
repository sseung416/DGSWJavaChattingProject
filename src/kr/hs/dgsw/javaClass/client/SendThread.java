package kr.hs.dgsw.javaClass.client;

import kr.hs.dgsw.javaClass.data.Message;
import kr.hs.dgsw.javaClass.data.MySocket;
import kr.hs.dgsw.javaClass.utils.ErrorCode;
import kr.hs.dgsw.javaClass.utils.TcpServerException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;
import java.util.regex.Pattern;

public class SendThread extends Thread { // 서버에 메세지 보냄
    private MySocket socket;
    private Scanner sc;

    private OutputStream os;

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
            while ((line = sc.nextLine()) != null) {
                Message message = null;

                if (line.length() >= Message.BUFFER_SIZE) {
                    System.out.println("[ 9999자 이상은 입력하실 수 없습니다. ]");
                    continue;
                } else if (line.trim().equals("")) {
                    System.out.println("[ 메세지를 입력해주세요. ]");
                    continue;
                }

                // 0: 명령어, 1: 아이디, 2: 메세지
                String[] command = line.split(" ");

                switch (command[0]) {
                    // 추방
                    case "/e":
                        if (!isId(command[1])) {
                            System.out.println("[ 잘못된 아이디 형식입니다. ]");
                            break;
                        }

                        message = new Message("WD", command[1]);
                        break;

                    // 귓속말
                    case "/w":
                        if (!isId(command[1])) {
                            System.out.println("[ 잘못된 아이디 형식입니다. ]");
                            break;
                        } else if (!isCorrectCommand(command)) {
                            System.out.println("[ 잘못된 명령어 입니다. ]");
                            break;
                        }

                        message = new Message("SM", command[1] + command[2]);
                        break;

                    // 챗팅 종료
                    case "/q":
                        System.out.println("[ 챗팅을 종료합니다. ]");
                        os.close();
                        socket.close();
                        return;

                    // 일반 채팅
                    default:
                        message = new Message("GM", line);
                }

                if (message != null) {
                    os.write(message.getMessage());
                    os.flush();
                }
            }
        } catch (SocketException e) {
        } catch (IOException e) {
            throw new TcpServerException(ErrorCode.MESSAGE_SEND_FAIL);
        }
    }

    // 아이디가 4자리의 숫자인지 검사
    private boolean isId(String s) {
        return Pattern.matches("\\d{4}", s);
    }

    // 제대로된 명령어인지 검사
    private boolean isCorrectCommand(String[] command) {
        return command.length == 3;
    }

    // 회원가입
    private void signIn() throws IOException {
        System.out.println("[ 아이디와 이름을 입력해주세요. ]");
        socket.setId(getCorrectId());
        socket.setName(getCorrectName());

        Message message = new Message("ID", socket.getId() + socket.getName());

        os.write(message.getMessage());
        os.flush();
    }

    // 올바른 아이디 형식(4자리 숫자)을 입력할 때까지 아이디 값을 받음
    private String getCorrectId() {
        String id;

        while (true) {
            System.out.print("id: ");
            id = sc.nextLine();

            if (!isId(id)) {
                System.out.println("[ 4자리의 숫자를 입력해주세요. ]");
                continue;
            }
            break;
        }

        return id;
    }

    // 올바른 이름 형식을 입력할 때까지 이름 값을 받음
    private String getCorrectName() {
        String name;

        while (true) {
            System.out.print("이름: ");
            name = sc.nextLine();
            boolean it = !Pattern.matches("^[0-9a-zA-Zㄱ-ㅎ가-힣]*$", name);
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
