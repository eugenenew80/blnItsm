package bln.itsm.web;

public class ControllerException extends RuntimeException {
    public String getPath() {
        return path;
    }

    private String path;

    public ControllerException(String message, String path) {
        super(message);
        this.path = path;
    }
}
