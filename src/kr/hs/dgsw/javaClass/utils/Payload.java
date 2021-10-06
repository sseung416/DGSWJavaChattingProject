package kr.hs.dgsw.javaClass.utils;

public enum Payload {
    // 서버에서 보내는 메세지 목록
    DR("DR", "아이디 중복 메시지"),
    UR("UR","현재 접속된 사용자들의 정보 메세지"),
    JR("JR","새로운 사용자 접속 메세지"),
    GR("GR","클라이언트에게 받은 일반 대화 메세지"),
    SR("SR","클라이언트에게 받은 귓속말 메세지"),
    DC("DC","퇴실 메세지"),
    WR("WR","자신이 추방됐다는 메세지"),
    WA("WA","특정 클라이언트가 추방됐다는 메세지"),

    // 클라이언트에서 보내는 메세지 목록
    ID("ID","사용자 정보 메시지(id, name)"),
    GM("GM","일반 대화 메시지"),
    SM("SM","귓속말 메시지"),
    WD("WD","특정 사용자를 추방하는 메세지");

    private String head;
    private String message;

    private Payload(String head, String message) {
        this.head = head;
        this.message = message;
    }

    public String getHead() {
        return head;
    }
}
