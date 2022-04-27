package com.example.wutyauthenticator;

import android.app.Application;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.MutableLiveData;

import org.json.JSONException;
import org.json.JSONObject;

import np.com.blackspring.adoniswebsocketclient.Socket;

public class App extends Application {
    private static App app;

    private String TAG = "APP";

    private Socket socket;

    public MutableLiveData<Socket.State> connectionState = new MutableLiveData<>();

    public static final String USER_ID = "USER_ID";
    public static final String EMAIL = "EMAIL";
    public static final String USERNAME = "USERNAME";
    public static final String TOKEN = "TOKEN";
    public static final String REFRESH_TOKEN = "REFRESH_TOKEN";

    public static final String Chad = "ght159hdztorreslol@gmail.com";
    public static final String Cuck = "guty.dev.test@gmail.com";
    public static final String Virgin = "alexferlozanom@gmail.com";

    public static App getApp () {
        return app;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        socket = Socket.Builder.with(NetworkSettings.getWs()).build();
        socket.connect();
        app = this;

        socket.onEvent(Socket.EVENT_OPEN, new Socket.OnEventListener() {
            @Override
            public void onMessage(String event) {
                Log.i(TAG, "onMessage: " + event);
                Toast.makeText(App.this, "Conectado", Toast.LENGTH_SHORT).show();
            }
        });
        
        socket.onEvent(Socket.EVENT_RECONNECT_ATTEMPT, new Socket.OnEventListener() {
            @Override
            public void onMessage(String event) {
                Log.i(TAG, "onMessage: " + event);
                Toast.makeText(App.this, "Reconectando", Toast.LENGTH_SHORT).show();
            }
        });

        socket.onEvent(Socket.EVENT_CLOSED, new Socket.OnEventListener() {
            @Override
            public void onMessage(String event) {
                socket.connect();
                Log.i(TAG, "onMessage: " + event);
                Toast.makeText(App.this, "Desconectado", Toast.LENGTH_SHORT).show();
            }
        });

        socket.setOnChangeStateListener(new Socket.OnStateChangeListener() {
            @Override
            public void onChange(Socket.State status) {
                connectionState.postValue(status);
                Log.i(TAG, "onChange: " + status);
                switch (status) {
                    case CONNECT_ERROR:
                        Toast.makeText(App.this, "Error al conectar socket",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case RECONNECTING:
                        Toast.makeText(App.this, "Reconectando socket", Toast.LENGTH_SHORT).show();
                        break;
                    case RECONNECT_ATTEMPT:
                        Toast.makeText(App.this, "Intentando reconectar socket", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

    }

    public SharedPreferences getPreferences () {
        return this.getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
    }

    public int getId () {
        return getPreferences().getInt(USER_ID, 0 );
    }

    public String getUsername () {
        return getPreferences().getString(USERNAME, "");
    }

    public String getEmail () {
        return getPreferences().getString(EMAIL, "");
    }

    public String getToken () {
        return getPreferences().getString(TOKEN, "");
    }

    public String getRefreshToken () {
        return getPreferences().getString(REFRESH_TOKEN, "");
    }

    public void setValue (String name, String value) {
        getPreferences()
                .edit()
                .putString(name, value)
                .apply();
    }

    public void setValue (String name, int value) {
        getPreferences()
                .edit()
                .putInt(name, value)
                .apply();
    }

    public void logOut () {
        socket.leave("access:" + String.valueOf(getId()));
        socket.close();
        getPreferences()
                .edit()
                .clear()
                .apply();
    }

    public String getTopic () {
        return "access:" + getId();
    }

    public void join () {
//        socket.connect();
        if (socket.join(getTopic())) {
            Log.i(TAG, "join: " + socket.getState());
        }
        socket.setMessageListener(new Socket.OnMessageListener() {
            @Override
            public void onMessage(String data) {
                Log.i(TAG, "onMessage: " + data);
                try {
                    JSONObject jsonMsg = new JSONObject(data);
                    int t = jsonMsg.optInt("t", -1);
                    if (t != 7) {
                        return;
                    }
                    JSONObject d = jsonMsg.optJSONObject("d");
                    if (d == null) {
                        throw new NullPointerException();
                    }
                    String topic = d.optString("topic", "");
                    JSONObject jsonData = d.optJSONObject("data");
                    if (jsonData == null) {
                        throw new NullPointerException();
                    }
                    String type = jsonData.optString("type", "");
                    if (topic.equals(getTopic()) && type.equals("request")) {
                        Intent intent = new Intent(app, AuthActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        PendingIntent pendingIntent = PendingIntent.getActivity(app, 0, intent, 0);
                        startActivity(intent);
                    }
                } catch (JSONException | NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void emmit (boolean confirmation) {
        JSONObject data = new JSONObject();
        try {
            data.put("type", "response");
            String tk = "";
            String rt = "";
            if (confirmation) {
                tk = getToken();
                rt = getRefreshToken();
            }
            data.put("token", tk);
            data.put("refreshToken", rt);
            socket.send(getTopic(), data.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
