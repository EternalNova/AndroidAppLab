package com.example.lab1;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class MessageViewHolder extends RecyclerView.ViewHolder {
    protected TextView messageText;
    protected TextView messageDate;

    public MessageViewHolder(@NonNull View itemView) {
        super(itemView);
        messageDate = itemView.findViewById(R.id.messageDateView);
        messageText = itemView.findViewById(R.id.messageTextView);
    }
    public void bind(Message message){
        messageText.setText(message.text);
        DateFormat fmt = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");
        messageDate.setText(fmt.format(message.date));
    }
}
