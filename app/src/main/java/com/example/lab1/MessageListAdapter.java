package com.example.lab1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MessageListAdapter extends RecyclerView.Adapter<MessageViewHolder>{
    private static final int ASSISTANT_TYPE=0;
    private static final int USER_TYPE=1;
    public ArrayList<Message> messageList = new ArrayList<Message>();
    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch(viewType){
            case USER_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_message,parent,false);
                break;
            case ASSISTANT_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.assistent_message, parent, false);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + viewType);
        }
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message msg = messageList.get(position);
        holder.bind(msg);
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }
    public int getItemViewType(int index){
        Message msg = messageList.get(index);
        if (msg.isSend)
            return USER_TYPE;
        return ASSISTANT_TYPE;
    }
}
