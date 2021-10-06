package utils;

public class TcpServerException extends RuntimeException {
    private ErrorCode errorCode;

    public TcpServerException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
