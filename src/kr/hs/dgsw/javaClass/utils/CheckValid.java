package kr.hs.dgsw.javaClass.utils;

import kr.hs.dgsw.javaClass.data.Message;
import kr.hs.dgsw.javaClass.data.Users;

import java.util.regex.Pattern;

public class CheckValid {
    Users users = new Users();

    public boolean isDuplicate(String id) {
        return users.getAllUser().containsKey(id);
    }

    public boolean isNull(Object o) {
        return o == null;
    }

    public boolean isBoundedBuffer(int length) {
        return length >= Message.BUFFER_SIZE;
    }

    public boolean isBlank(String str) {
        return str.trim().equals("");
    }

    public boolean isCorrectId(String s) {
        return Pattern.matches("\\d{4}", s);
    }

    public boolean isCorrectCommand(String[] command) {
        return command.length == 3;
    }

    public boolean isInSpecialSymbol(String str) {
        return !Pattern.matches("^[0-9a-zA-Zㄱ-ㅎ가-힣]*$", str);
    }
}