package com.example.lab1;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Message {
    public String text;
    public Date date;
    public Boolean isSend;

    public Message(String text, Boolean isSend) {
        this.text = text;
        this.isSend = isSend;
        this.date = new Date();
    }
    public Message(MessageEntity messageEntity){
        this.text = messageEntity.text;
        try {
            this.date = new SimpleDateFormat("EEE MMM d HH:mm:ss z yyyy").parse(messageEntity.date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.isSend = messageEntity.isSend == 1;
    }
}
