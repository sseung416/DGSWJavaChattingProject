package dto;

import java.nio.charset.StandardCharsets;

public class Message {
    public final static int PORT = 1202;
    public final static int BUFFER_SIZE = 1024;

    private final byte[] message;

    public Message(byte[] message) {
        this.message = message;
    }

    public Message(String header, String payload) {
        String message = header + String.format("%04d", payload.length()) + payload;

        this.message = message.getBytes(StandardCharsets.UTF_8);
    }

    public byte[] getMessage() {
        return message;
    }

    public String getHead() {
        return new String(new byte[]{message[0], message[1]}, StandardCharsets.UTF_8);
    }

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

    public String getId() {
        return getPayload().substring(0, 4);
    }

    public String getMsg() {
        return getPayload().substring(4);
    }
}
