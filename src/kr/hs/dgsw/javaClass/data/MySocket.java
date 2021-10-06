package kr.hs.dgsw.javaClass.data;

import java.net.Socket;

public class MySocket extends Socket {
    private Socket socket;

    private String id;

    private String name;

    private boolean isAdmin = false;

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAdmin() {
        isAdmin = true;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    @Override
    public String toString() {
        return id + name;
    }
}
