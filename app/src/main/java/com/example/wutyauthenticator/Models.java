package com.example.wutyauthenticator;

public final class Models {
    public static class Response<Datatype> {
        public final String msg;
        public final Datatype data;

        public Response(String msg, Datatype data) {
            this.msg = msg;
            this.data = data;
        }
    }

    public static class User {
        public final String username;
        public final String email;
        public final int id;

        public User(String username, String email, int id) {
            this.username = username;
            this.email = email;
            this.id = id;
        }
    }

    public static class AccessToken {
        public final String type;
        public final String token;
        public final String refreshToken;

        public AccessToken(String type, String token, String refresh_token) {
            this.type = type;
            this.token = token;
            this.refreshToken = refresh_token;
        }
    }

    public static class AuthData {
        public final User user;
        public final AccessToken accessToken;

        public AuthData(User user, AccessToken access_token) {
            this.user = user;
            this.accessToken = access_token;
        }
    }
}
