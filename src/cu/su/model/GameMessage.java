package cu.su.model;


import java.io.Serializable;

public class GameMessage implements Serializable {
    private String cmd;

    private int code;
    private int timer;
    private String message;
    private Object object;
    private Object objectSecond;
    private Object objectThird;
    private Object objectFourth;
    private Object objectFifth;

    public GameMessage(String cmd) {
        this.cmd = cmd;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public Object getObjectSecond() {
        return objectSecond;
    }

    public void setObjectSecond(Object objectSecond) {
        this.objectSecond = objectSecond;
    }

    public Object getObjectThird() {
        return objectThird;
    }

    public void setObjectThird(Object objectThird) {
        this.objectThird = objectThird;
    }

    public Object getObjectFourth() {
        return objectFourth;
    }

    public void setObjectFourth(Object objectFourth) {
        this.objectFourth = objectFourth;
    }

    public Object getObjectFifth() {
        return objectFifth;
    }

    public void setObjectFifth(Object objectFifth) {
        this.objectFifth = objectFifth;
    }

    public int getTimer() {
        return timer;
    }

    public void setTimer(int timer) {
        this.timer = timer;
    }
}
