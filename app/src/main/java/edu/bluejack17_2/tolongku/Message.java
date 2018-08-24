package edu.bluejack17_2.tolongku;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Message implements Serializable,Comparable<Message>{

    private String message,type,sender;
    private String time;
    private boolean seen;


    public Message()
    {}

    public String getSender() {
        return sender;
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

    public Timestamp getTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
        java.util.Date parsed = null;
        try {
            parsed = sdf.parse(this.time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Timestamp(parsed.getTime());

    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public Message(String message, boolean seen, String sender,String time, String type) {
        this.message = message;
        this.type = type;
        this.time = time;
        this.seen = seen;
        this.sender = sender;
    }

    @Override
    public int compareTo(@NonNull Message message) {
        return getTime().compareTo(message.getTime());
    }
}
