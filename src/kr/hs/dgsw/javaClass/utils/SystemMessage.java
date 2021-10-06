package kr.hs.dgsw.javaClass.utils;

public enum SystemMessage {
    INPUT_MESSAGE("메세지를 입력해주세요."),
    INPUT_NAME("이름을 입력해주세요."),
    INPUT_ID_AND_NAME("아이디와 이름을 입력해주세요."),
    INPUT_FOUR_LENGTH_NUMBER("4자리의 숫자를 입력해주세요."),
    CANT_INPUT_SPECIAL_SYMBOL_IN_NAME("이름에 특수문자는 들어갈 수 없습니다."),
    CANT_INPUT_9999_LENGTH("9999자 이상은 입력하실 수 없습니다."),
    DUPLICATE_ID("중복된 아이디이므로, 접속을 중단합니다."),
    WRONG_ID("잘못된 아이디 형식입니다."),
    WRONG_COMMAND("잘못된 명령어입니다."),
    YOU_EXILE("당신은 채팅방에서 추방되었습니다."),
    CLOSE_CHATTING("챗팅을 종료합니다."),
    OPEN_SERVER("서버가 열렸습니다."),
    WELCOME_CHATTING("채팅방에 오신 걸 환영합니다!");

    private String message;

    private SystemMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void print(SystemMessage systemMessage) {

        System.out.println();
    }
}
