package kr.hs.dgsw.javaClass.utils;

public enum ErrorCode {
    PORT_ALREADY_OCCUPIED("이미 사용 중인 port 번호입니다."),
    SERVER_SOCKET_FAIL("서버 접속이 끊겼습니다."),
    MESSAGE_SEND_FAIL("메시지 전송에 실패하였습니다.");

    private String message;

    private ErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
