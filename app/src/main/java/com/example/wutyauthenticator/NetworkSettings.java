package com.example.wutyauthenticator;

import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.squareup.moshi.Moshi;
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import okhttp3.ConnectionSpec;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public class NetworkSettings {
    public static final String localUrl = "192.168.100.79:3333";
    public static final String qaUrl = "api.salutemonte.space";
    public static final String qaProtocol = "https";
    public static final String localProtocol = "http";
    public static final String localWS = "ws";
    public static final String qaWS = "wss";
    public static final boolean production = true;
    public static final boolean debug = true;
    public static Retrofit instance;
    public interface AuthServices {
        @POST("app/login")
        @FormUrlEncoded
        Call<Models.Response<Models.AuthData>> login (
                @Field("email") String email,
                @Field("password") String password
        );

        @POST("auth-confirmation")
        @FormUrlEncoded
        Call<Models.Response<Models.AuthData>> threeFactor (
                @Field("confirmation") boolean confirmation,
                @Field("email") String email,
                @Field("password") String password
        );

        @POST("check-refresh")
        @FormUrlEncoded Call<Models.Response<Models.AccessToken>> checkRefresh (
                @Field("refresh_token") String refreshToken
        );
    }

    public static String getUrl() {
        return String.format("%s://%s/", production ? qaProtocol : localProtocol ,production ? qaUrl : localUrl);
    }

    public static String getWs () {
        return String.format("%s://%s/ws", production ? qaWS : localWS, production ? qaUrl : localUrl);
    }

    public static Retrofit getInstance(SharedPreferences sharedPreferences) {
        if (instance == null) {
            ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.CLEARTEXT)
                    .build();
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient.Builder clientBuilder = new OkHttpClient().newBuilder()
                    .addInterceptor(new AuthInterceptor(sharedPreferences))
                    .addInterceptor(loggingInterceptor);
            if (!production) {
                clientBuilder.connectionSpecs(Collections.singletonList(spec));
            }
            OkHttpClient client = clientBuilder.build();
            Moshi moshi = new Moshi.Builder()
                    .add(new KotlinJsonAdapterFactory())
                    .build();
            MoshiConverterFactory converterFactory = MoshiConverterFactory.create(moshi);
            instance = new Retrofit.Builder()
                    .addConverterFactory(converterFactory)
                    .baseUrl(getUrl())
                    .client(client)
                    .build();
        }
        return instance;
    }

    public static AuthServices getAuthServices  (Retrofit retrofit) {
        return retrofit.create(AuthServices.class);
    }

    public static final class AuthInterceptor implements Interceptor {
        private final SharedPreferences preferences;
        public AuthInterceptor(SharedPreferences preferences) {
            this.preferences = preferences;
        }

        @NonNull
        @Override
        public Response intercept(@NonNull Chain chain) throws IOException {
            Request.Builder builder = chain.request().newBuilder();
            String token = preferences.getString("token", "");
            builder.addHeader("Accept", "application/json");
            if (!token.equals("")) {
                builder.addHeader("Authorization", "Bearer " + token);
            }
            return chain.proceed(builder.build());
        }
    }
}
