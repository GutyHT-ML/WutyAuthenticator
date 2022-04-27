package com.example.wutyauthenticator;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.wutyauthenticator.databinding.ActivityHomeBinding;

public class HomeActivity extends AppCompatActivity {
    private ActivityHomeBinding binding;

    private App app;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = App.getApp();
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setData();
    }

    private void setData() {
        app.connectionState.observe(this, state -> {
            binding.tvStatus.setText(state.toString());
        });
        Log.i(TAG, "setData: " + app.getEmail());
        binding.tvEmail.setText(app.getEmail());
        binding.tvUsername.setText(app.getUsername());
        binding.btnTest.setOnClickListener(view -> {
            app.logOut();
            startActivity(new Intent(HomeActivity.this, MainActivity.class));
            finish();
        });
//        binding.btnTest.setOnClickListener(view -> {
//            app.emmit(false);
//        });
        app.join();
    }

}
