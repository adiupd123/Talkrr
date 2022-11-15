package com.adiupd123.talkrr.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;

import com.adiupd123.talkrr.Adapters.MessagesAdapter;
import com.adiupd123.talkrr.Models.Message;
import com.adiupd123.talkrr.databinding.ActivityChatBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

public class ChatActivity extends AppCompatActivity {

    FirebaseDatabase database;
    ActivityChatBinding binding;
    MessagesAdapter messagesAdapter;
    ArrayList<Message> messages;
    String senderRoom, recieverRoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        messages = new ArrayList<>();

        String name = getIntent().getStringExtra("userName");
        String recieverUid = getIntent().getStringExtra("userId");
        String senderUid = FirebaseAuth.getInstance().getUid();

        senderRoom = senderUid + recieverUid;
        recieverRoom = recieverUid + senderUid;

        messagesAdapter = new MessagesAdapter(this, messages, senderRoom, recieverRoom);
        binding.singleChatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.singleChatRecyclerView.setAdapter(messagesAdapter);

        database = FirebaseDatabase.getInstance();

        database.getReference().child("chats")
                .child(senderRoom)
                .child("messages")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messages.clear();
                        for(DataSnapshot snapshot1 : snapshot.getChildren()){
                            Message message = snapshot1.getValue(Message.class);
                            message.setMessageId(snapshot1.getKey());
                            messages.add(message);
                        }
                        messagesAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        binding.sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msgText = binding.messageBoxEditText.getText().toString();

                Date date = new Date();
                Message message = new Message(msgText, senderUid, date.getTime());
                binding.messageBoxEditText.setText("");

                String messageKey = database.getReference().push().getKey();

                database.getReference().child("chats")
                        .child(senderRoom)
                        .child("messages")
                        .child(messageKey)
                        .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                database.getReference().child("chats")
                                        .child(recieverRoom)
                                        .child("messages")
                                        .child(messageKey)
                                        .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {

                                            }
                                        });
                            }
                        });
            }
        });

        getSupportActionBar().setTitle(name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}