package com.chatapp.profile;

public class UserProfile {

    String username, userUID;

    public UserProfile() {
    }

    public UserProfile(String username, String userUID) {
        this.username = username;
        this.userUID = userUID;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUserUID(String userUID) {
        this.userUID = userUID;
    }

    public String getUsername() {
        return username;
    }

    public String getUserUID() {
        return userUID;
    }
}
