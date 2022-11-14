package com.adiupd123.talkrr;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.adiupd123.talkrr.databinding.ActivitySetupProfileBinding;
import com.github.drjacky.imagepicker.ImagePicker;
import com.github.drjacky.imagepicker.constant.ImageProvider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;

public class SetupProfileActivity extends AppCompatActivity {

    ActivitySetupProfileBinding binding;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    ActivityResultLauncher<Intent> profilePhotoPicker;
    private Uri profilePhotoUri;
    private String name;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySetupProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating Profile...");
        progressDialog.setCancelable(false);

        profilePhotoPicker = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),(ActivityResult result)->{
            if(result.getResultCode()==RESULT_OK){
                profilePhotoUri = result.getData().getData();
                // Use the uri to load the image
                binding.profilePhotoImageView.setImageURI(profilePhotoUri);
            }else if(result.getResultCode()== ImagePicker.RESULT_ERROR){
                // Use ImagePicker.Companion.getError(result.getData()) to show an error
                ImagePicker.Companion.getError(result.getData());
            }
        });

        binding.profilePhotoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.Companion.with(SetupProfileActivity.this)
                        .crop(1f, 1f)
                        .maxResultSize(1024, 1024, true)
                        .setMultipleAllowed(false)
                        .provider(ImageProvider.BOTH) //Or bothCameraGallery()
                        .createIntentFromDialog((Function1) (new Function1() {
                            public Object invoke(Object var1) {
                                this.invoke((Intent) var1);
                                return Unit.INSTANCE;
                            }

                            public final void invoke(@NotNull Intent it) {
                                Intrinsics.checkNotNullParameter(it, "it");
                                profilePhotoPicker.launch(it);
                            }
                        }));

            }
        });

        binding.setUpUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = binding.nameEditText.getText().toString();
                if (name == null){
                    binding.nameEditText.setError("Name Field can't be empty!");
                    return;
                }
                if(profilePhotoUri != null){
                    progressDialog.show();
                    StorageReference reference = storage.getReference().child("Profiles").child(mAuth.getUid());
                    reference.putFile(profilePhotoUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if(task.isSuccessful()){
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String imageUrl = uri.toString();
                                        User user = new User(mAuth.getUid(), name, mAuth.getCurrentUser().getPhoneNumber(), imageUrl);
                                        database.getReference()
                                                .child("users")
                                                .child(mAuth.getUid())
                                                .setValue(user)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        progressDialog.dismiss();
                                                        Intent intent = new Intent(SetupProfileActivity.this, MainActivity.class);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                });
                                    }
                                });
                            }
                        }
                    });
                }
                else{
                    User user = new User(mAuth.getUid(), name, mAuth.getCurrentUser().getPhoneNumber(), "No Image");
                    database.getReference()
                            .child("users")
                            .child(mAuth.getUid())
                            .setValue(user)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    progressDialog.dismiss();
                                    Intent intent = new Intent(SetupProfileActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                }
            }
        });
    }
}