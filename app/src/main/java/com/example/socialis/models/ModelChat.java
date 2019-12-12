package com.example.socialis.models;

public class ModelChat {

    String message , reciever , sender, time;
    boolean isSeen;

    public ModelChat(String message, String reciever, String sender, String time, boolean isSeen) {
        this.message = message;
        this.reciever = reciever;
        this.sender = sender;
        this.time = time;
        this.isSeen = isSeen;
    }

    public ModelChat() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReciever() {
        return reciever;
    }

    public void setReciever(String reciever) {
        this.reciever = reciever;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isSeen() {
        return isSeen;
    }

    public void setISeen(boolean seen) {
        isSeen = seen;
    }
}
