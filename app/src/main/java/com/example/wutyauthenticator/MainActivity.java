package com.example.wutyauthenticator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.wutyauthenticator.databinding.ActivityMainBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    
    private App app;
    
    private NetworkSettings.AuthServices authServices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = App.getApp();
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authServices = NetworkSettings
                .getAuthServices(NetworkSettings.getInstance(app.getPreferences()));

        debug();
        setListeners();
    }

    private void debug() {
        if (NetworkSettings.debug) {
            binding.etEmail.setText(App.Chad);
            binding.etPw.setText("123");
        }
    }

    private void setListeners () {
        binding.btnLogin.setOnClickListener(view -> {
            authServices.login(
                    binding.etEmail.getText().toString(),
                    binding.etPw.getText().toString()
            ).enqueue(new Callback<Models.Response<Models.AuthData>>() {
                @Override
                public void onResponse(@NonNull Call<Models.Response<Models.AuthData>> call,
                                       @NonNull Response<Models.Response<Models.AuthData>> response) {
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        Models.User user = response.body().data.user;
                        Models.AccessToken accessToken = response.body().data.accessToken;

                        app.setValue(App.USER_ID, user.id);
                        app.setValue(App.EMAIL, user.email);
                        app.setValue(App.USERNAME, user.username);
                        app.setValue(App.TOKEN, accessToken.token);
                        app.setValue(App.REFRESH_TOKEN, accessToken.refreshToken);

                        Intent i = new Intent(app, HomeActivity.class);
                        startActivity(i);
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
                        }
                    }
                }

                @Override
                public void onFailure(Call<Models.Response<Models.AuthData>> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        });
    }
}