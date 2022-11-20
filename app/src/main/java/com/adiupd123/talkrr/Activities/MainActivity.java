package com.adiupd123.talkrr.Activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.adiupd123.talkrr.Adapters.TopStatusAdapter;
import com.adiupd123.talkrr.Models.UserStatus;
import com.adiupd123.talkrr.R;
import com.adiupd123.talkrr.Models.User;
import com.adiupd123.talkrr.Adapters.UsersAdapter;
import com.adiupd123.talkrr.databinding.ActivityMainBinding;
import com.github.drjacky.imagepicker.ImagePicker;
import com.github.drjacky.imagepicker.constant.ImageProvider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {

    ActivityMainBinding binding;
    FirebaseDatabase database;
    ArrayList<User> users;
    UsersAdapter usersAdapter;
    TopStatusAdapter statusAdapter;
    ArrayList<UserStatus> userStatuses;
    ActivityResultLauncher<Intent> statusImagePicker;
    ProgressDialog progressDialog;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialog = new ProgressDialog((this));
        progressDialog.setMessage("Uploading Image...");
        progressDialog.setCancelable(false);

        database = FirebaseDatabase.getInstance();
        users = new ArrayList<>();
        userStatuses = new ArrayList<>();

        database.getReference().child("users").child(FirebaseAuth.getInstance().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        user = snapshot.getValue(User.class);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



        usersAdapter = new UsersAdapter(this, users);
        statusAdapter = new TopStatusAdapter(this, userStatuses);

        binding.chatsRecyclerView.setAdapter(usersAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        binding.statusRecyclerView.setLayoutManager(linearLayoutManager);
        binding.statusRecyclerView.setAdapter(statusAdapter);

        database.getReference().child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    User user = snapshot1.getValue(User.class);
                    users.add(user);
                }
                usersAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.bottomNavigationView.setOnItemSelectedListener(this);

        statusImagePicker = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),(ActivityResult result)->{
           if(result.getResultCode() == RESULT_OK){
               Uri uri = result.getData().getData();
               progressDialog.show();
               FirebaseStorage storage = FirebaseStorage.getInstance();
               Date date = new Date();
               StorageReference reference = storage.getReference().child("status").child(date.getTime() + "");
               reference.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                   @Override
                   public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                       if(task.isSuccessful()){
                           reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                               @Override
                               public void onSuccess(Uri uri) {
                                   UserStatus userStatus = new UserStatus();
                                   userStatus.setName(user.getName());
                                   userStatus.setProfileImage(user.getProfileImage());
                                   userStatus.setLastUpdated(date.getTime());

                                   HashMap<String,Object> obj = new HashMap<>();
                                   obj.put("name", userStatus.getName());
                                   obj.put("profileImage",userStatus.getProfileImage());
                                   obj.put("lastUpdated",userStatus.getLastUpdated());

                                   database.getReference().child("stories").child(FirebaseAuth.getInstance().getUid())
                                                   .updateChildren(obj);
                                   progressDialog.dismiss();
                               }
                           });
                       }
                   }
               });
           }
           else if(result.getResultCode() == ImagePicker.RESULT_ERROR) {
               ImagePicker.Companion.getError(result.getData());
           }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.search_item:
                Toast.makeText(this, "Search", Toast.LENGTH_SHORT).show();
                break;
            case R.id.groups_item:
                Toast.makeText(this, "Groups", Toast.LENGTH_SHORT).show();
                break;
            case R.id.settings_item:
                Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
                break;
            case R.id.invite_item:
                Toast.makeText(this, "Invite", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.chat_item: break;
            case R.id.status_item:
                ImagePicker.Companion.with(MainActivity.this)
                        .crop(1f, 1f)
                        .maxResultSize(1440, 1440,false)
                        .setMultipleAllowed(true)
                        .provider(ImageProvider.BOTH) //Or bothCameraGallery()
                        .createIntentFromDialog((Function1)(new Function1(){
                            public Object invoke(Object var1){
                                this.invoke((Intent)var1);
                                return Unit.INSTANCE;
                            }
                            public final void invoke(@NotNull Intent it){
                                Intrinsics.checkNotNullParameter(it,"it");
                                statusImagePicker.launch(it);
                            }
                        }));
                break;
            case R.id.calls_item: break;
        }
        return true;
    }
}