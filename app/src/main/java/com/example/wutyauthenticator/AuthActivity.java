package com.example.wutyauthenticator;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.wutyauthenticator.databinding.ActivityAuthBinding;
import com.example.wutyauthenticator.databinding.ActivityHomeBinding;

public class AuthActivity extends AppCompatActivity {
    private ActivityAuthBinding binding;

    private App app;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = App.getApp();
        binding = ActivityAuthBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnYes.setOnClickListener(view -> {
            app.emmit(true);
            finish();
        });
        binding.btnNo.setOnClickListener(view -> {
            app.emmit(false);
            finish();
        });
    }
}
