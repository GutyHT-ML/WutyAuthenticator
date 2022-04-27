package com.example.wutyauthenticator;

import android.content.Intent;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.wutyauthenticator.databinding.ActivitySplashBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {
    private App app;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = App.getApp();
        ActivitySplashBinding binding = ActivitySplashBinding.inflate(getLayoutInflater());
        AnimatedVectorDrawable drawable = (AnimatedVectorDrawable) binding.ivSettings.getDrawable();
        drawable.start();
//        drawable.;
        setContentView(binding.getRoot());

        checkAuth();
    }

    private void checkAuth() {
        if (app.getToken().equals("")) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }
        NetworkSettings.AuthServices authServices = NetworkSettings.getAuthServices(
                NetworkSettings.getInstance(app.getPreferences())
        );
        authServices.checkRefresh(app.getRefreshToken()).enqueue(new Callback<Models.Response<Models.AccessToken>>() {
            @Override
            public void onResponse(Call<Models.Response<Models.AccessToken>> call,
                                   Response<Models.Response<Models.AccessToken>> response) {
                if (response.isSuccessful()) {
                    startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                    finish();
                } else {
                    try {
                        JSONObject object = new JSONObject(response.errorBody().string());
                        Toast.makeText(app, object.optString("msg", "Error del servidor"),
                                Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    }
                }
            }

            @Override
            public void onFailure(Call<Models.Response<Models.AccessToken>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
