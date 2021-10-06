package dto;

import java.nio.charset.StandardCharsets;

// 메세지 관련 클래스
public class Message {
    public final static int PORT = 1100;
    public final static int BUFFER_SIZE = 2048;

    private byte[] message;

    public Message(byte[] message) {
        this.message = message;
    }

    public Message(String header, String payload) {
        // 한글 깨짐을 방지해 byte 크기 전달
        byte[] payloadByte = payload.getBytes(StandardCharsets.UTF_8);

        String message = header + String.format("%04d", payloadByte.length) + payload;

        this.message = message.getBytes(StandardCharsets.UTF_8);
    }

    public byte[] getMessage() {
        return message;
    }

    // 메세지의 헤드(종류)를 반환
    public String getHead() {
        return new String(new byte[]{message[0], message[1]}, StandardCharsets.UTF_8);
    }

    // 메세지의 길이를 반환
    public int getLength() {
        byte[] length = new byte[4];
        System.arraycopy(message, 2, length, 0, 4);
        return Integer.parseInt(new String(length));
    }

    public String getPayload() {
        byte[] payload = new byte[getLength()];
        System.arraycopy(message, 6, payload, 0, payload.length);
        return new String(payload, StandardCharsets.UTF_8);
    }

    // 페이로드에서 아이디를 추출해 반환
    public String getId() {
        return getPayload().substring(0, 4);
    }

    // 페이로드에서 메세지를 추출해 반환
    public String getMsg() {
        return getPayload().substring(4);
    }

//    public String sibalHangeul() {
//
//    }
}
