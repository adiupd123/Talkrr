package com.adiupd123.talkrr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.adiupd123.talkrr.databinding.ActivityPhoneNumberBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;

public class PhoneNumberActivity extends AppCompatActivity {

    ActivityPhoneNumberBinding binding;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPhoneNumberBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        getSupportActionBar().hide();

        if(mAuth.getCurrentUser() != null){
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
        binding.continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openOtpActivity();
            }
        });
    }

    public void openOtpActivity(){
        Intent verifyIntent = new Intent(PhoneNumberActivity.this, OTPActivity.class);
        verifyIntent.putExtra("countryCode", binding.ccp.getSelectedCountryCodeWithPlus().toString());
        verifyIntent.putExtra("number", binding.phoneNumberEditText.getText().toString());
        startActivity(verifyIntent);
        finishAffinity();
    }
}