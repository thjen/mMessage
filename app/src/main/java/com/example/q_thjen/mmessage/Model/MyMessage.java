package com.example.q_thjen.mmessage.Model;

/**
 * Created by q-thjen on 12/14/17.
 */

public class MyMessage {

    private String message, type;
    private boolean seen;
    private long time;
    private String from;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public MyMessage(String from) {
        this.from = from;
    }

    public MyMessage() {
    }

    public MyMessage(String message, String type, boolean seen, long time) {
        this.message = message;
        this.type = type;
        this.seen = seen;
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

}
