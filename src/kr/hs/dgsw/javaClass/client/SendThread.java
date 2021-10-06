package kr.hs.dgsw.javaClass.client;

import kr.hs.dgsw.javaClass.data.Message;
import kr.hs.dgsw.javaClass.data.MySocket;
import kr.hs.dgsw.javaClass.utils.*;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

public class SendThread extends Thread { // 서버에 메세지 보냄
    private MySocket socket;
    private Scanner sc;

    private OutputStream os;

    private CheckValid checkValid;

    public SendThread(Socket socket) throws IOException {
        this.socket = new MySocket();
        this.socket.setSocket(socket);
        sc = new Scanner(System.in);
        os = socket.getOutputStream();
        checkValid = new CheckValid();
    }

    @Override
    public void run() {
        try {
            signIn();

            String line;
            while ((line = sc.nextLine()) != null) {
                Message message = null;

                if (checkValid.isBoundedBuffer(line.length())) {
                    print(SystemMessage.CANT_INPUT_9999_LENGTH.getMessage());
                    continue;
                } else if (checkValid.isBlank(line)) {
                    print(SystemMessage.INPUT_MESSAGE.getMessage());
                    continue;
                }

                // 0: 명령어, 1: 아이디, 2: 메세지
                String[] command = line.split(" ");

                switch (command[0]) {
                    // 추방
                    case "/e":
                        if (!checkValid.isCorrectId(command[1])) {
                            print(SystemMessage.WRONG_ID.getMessage());
                            break;
                        }

                        message = new Message("WD", command[1]);
                        break;

                    // 귓속말
                    case "/w":
                        if (!checkValid.isCorrectId(command[1])) {
                            print(SystemMessage.WRONG_ID.getMessage());
                            break;
                        } else if (!checkValid.isCorrectCommand(command)) {
                            print(SystemMessage.WRONG_COMMAND.getMessage());
                            break;
                        }

                        message = new Message(Payload.SM.getHead(), command[1] + command[2]);
                        break;

                    // 챗팅 종료
                    case "/q":
                        print(SystemMessage.CLOSE_CHATTING.getMessage());
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

    // 회원가입
    private void signIn() throws IOException {
        print(SystemMessage.INPUT_ID_AND_NAME.getMessage());

        socket.setId(getCorrectId());
        socket.setName(getCorrectName());

        Message message = new Message(Payload.ID.getHead(), socket.getId() + socket.getName());

        os.write(message.getMessage());
        os.flush();
    }

    // 올바른 아이디 형식(4자리 숫자)을 입력할 때까지 아이디 값을 받음
    private String getCorrectId() {
        String id;

        while (true) {
            System.out.print("id: ");
            id = sc.nextLine();

            if (!checkValid.isCorrectId(id)) {
                print(SystemMessage.INPUT_FOUR_LENGTH_NUMBER.getMessage());
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

            if (checkValid.isInSpecialSymbol(name)) {
                print(SystemMessage.CANT_INPUT_SPECIAL_SYMBOL_IN_NAME.getMessage());
                continue;
            } else if (checkValid.isBlank(name)) {
                print(SystemMessage.INPUT_NAME.getMessage());
                continue;
            }

            break;
        }

        return name;
    }

    private void print(String str) {
        System.out.println("[ " + str + "]");
    }
}
