package com.adiupd123.talkrr.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adiupd123.talkrr.Activities.ChatActivity;
import com.adiupd123.talkrr.Models.User;
import com.adiupd123.talkrr.R;
import com.adiupd123.talkrr.databinding.ConversationRowItemBinding;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UsersViewHolder>{

    Context context;
    ArrayList<User> users;

    public UsersAdapter(Context context, ArrayList<User> users){
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.conversation_row_item, parent, false);
        return new UsersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersViewHolder holder, int position) {
        User user = users.get(position);
        holder.binding.usernameTextView.setText(user.getName());
        Glide.with(context).load(user.getProfileImage())
                .placeholder(R.drawable.avatar)
                .into(holder.binding.userImageView);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chatIntent = new Intent(context, ChatActivity.class);
                chatIntent.putExtra("userName", user.getName());
                chatIntent.putExtra("userId", user.getUserId());
                context.startActivity(chatIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class UsersViewHolder extends RecyclerView.ViewHolder{

        ConversationRowItemBinding binding;
        public UsersViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ConversationRowItemBinding.bind(itemView);

        }
    }
}
