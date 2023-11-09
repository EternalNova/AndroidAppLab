package com.example.lab1;

import java.text.SimpleDateFormat;

public class MessageEntity {
    public String text;
    public String  date;
    public int isSend;
    public MessageEntity(String text, String date, int isSend){
        this.text = text;
        this.date = date;
        this.isSend = isSend;
    }
    public MessageEntity(Message message){
        this.text = message.text;
        this.date = new SimpleDateFormat("EEE MMM d HH:mm:ss z yyyy").format(message.date);
        this.isSend = message.isSend ? 1 : 0;
    }
}
