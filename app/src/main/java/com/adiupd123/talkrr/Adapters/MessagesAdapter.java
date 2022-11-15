package com.adiupd123.talkrr.Adapters;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adiupd123.talkrr.Models.Message;
import com.adiupd123.talkrr.R;
import com.adiupd123.talkrr.databinding.RecievedItemBinding;
import com.adiupd123.talkrr.databinding.SentItemBinding;
import com.github.pgreze.reactions.ReactionPopup;
import com.github.pgreze.reactions.ReactionsConfig;
import com.github.pgreze.reactions.ReactionsConfigBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MessagesAdapter extends RecyclerView.Adapter{

    Context context;
    ArrayList<Message> messages;
    String senderRoom, recieverRoom;
    final int ITEM_SENT = 1;
    final int ITEM_RECIEVED = 2;


    public MessagesAdapter(Context context, ArrayList<Message> messages, String senderRoom, String recieverRoom){
        this.context = context;
        this.messages = messages;
        this.senderRoom = senderRoom;
        this.recieverRoom = recieverRoom;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == ITEM_SENT){
            View view = LayoutInflater.from(context).inflate(R.layout.sent_item, parent, false);
            return new SentViewHolder(view);
        }
        else{
            View view = LayoutInflater.from(context).inflate(R.layout.recieved_item, parent, false);
            return new RecievedViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        if(message.getSenderId().equals(FirebaseAuth.getInstance().getUid())){
            return ITEM_SENT;
        }
        else{
            return ITEM_RECIEVED;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);
        int[] reactions = new int[]{
                R.drawable.ic_fb_like,
                R.drawable.ic_fb_love,
                R.drawable.ic_fb_laugh,
                R.drawable.ic_fb_wow,
                R.drawable.ic_fb_sad,
                R.drawable.ic_fb_angry
        };
        ReactionsConfig config = new ReactionsConfigBuilder(context)
                .withReactions(reactions)
                .build();

        ReactionPopup popup = new ReactionPopup(context, config, (pos) -> {
            if(holder.getClass() == SentViewHolder.class){
                SentViewHolder viewHolder = (SentViewHolder) holder;
                viewHolder.sBinding.feelingsToSentImageView.setImageResource(reactions[pos]);
                viewHolder.sBinding.feelingsToSentImageView.setVisibility(View.VISIBLE);
            }
            else{
                RecievedViewHolder viewHolder = (RecievedViewHolder) holder;
                viewHolder.rBinding.feelingsToRecievedImageView.setImageResource(reactions[pos]);
                viewHolder.rBinding.feelingsToRecievedImageView.setVisibility(View.VISIBLE);
            }

            message.setFeeling(pos);

            FirebaseDatabase.getInstance().getReference()
                    .child("chats")
                    .child(senderRoom)
                    .child("messages")
                    .child(message.getMessageId()).setValue(message);

            FirebaseDatabase.getInstance().getReference()
                    .child("chats")
                    .child(recieverRoom)
                    .child("messages")
                    .child(message.getMessageId()).setValue(message);

            return true; // true is closing popup, false is requesting a new selection
        });

        if(holder.getClass() == SentViewHolder.class) {
            SentViewHolder viewHolder = (SentViewHolder) holder;
            if(message.getFeeling()>=0){
                viewHolder.sBinding.feelingsToSentImageView.setImageResource(reactions[message.getFeeling()]);
                viewHolder.sBinding.feelingsToSentImageView.setVisibility(View.VISIBLE);
            }
            else{
                viewHolder.sBinding.feelingsToSentImageView.setVisibility(View.GONE);
            }
            viewHolder.sBinding.msgTextView.setText(message.getMessage());
            viewHolder.sBinding.msgTextView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    popup.onTouch(view, motionEvent);
                    return false;
                }
            });
        }
        else {
            RecievedViewHolder viewHolder = (RecievedViewHolder) holder;
            if(message.getFeeling()>=0){
                viewHolder.rBinding.feelingsToRecievedImageView.setImageResource(reactions[message.getFeeling()]);
                viewHolder.rBinding.feelingsToRecievedImageView.setVisibility(View.VISIBLE);
            }
            else{
                viewHolder.rBinding.feelingsToRecievedImageView.setVisibility(View.GONE);
            }
            viewHolder.rBinding.msgTextView.setText(message.getMessage());
            viewHolder.rBinding.msgTextView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    popup.onTouch(view, motionEvent);
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class SentViewHolder extends RecyclerView.ViewHolder{

        SentItemBinding sBinding;
        public SentViewHolder(@NonNull View itemView) {
            super(itemView);
            sBinding = SentItemBinding.bind(itemView);
        }
    }


    public class RecievedViewHolder extends RecyclerView.ViewHolder{

        RecievedItemBinding rBinding;
        public RecievedViewHolder(@NonNull View itemView) {
            super(itemView);
            rBinding = RecievedItemBinding.bind(itemView);
        }
    }
}
