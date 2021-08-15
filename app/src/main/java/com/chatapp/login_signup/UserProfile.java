package com.chatapp.login_signup;

public class UserProfile {

    String username, userUID;

    public UserProfile(String username, String userUID) {
        this.username = username;
        this.userUID = userUID;
    }

    public String getUsername() {
        return username;
    }

    public String getUserUID() {
        return userUID;
    }
}
