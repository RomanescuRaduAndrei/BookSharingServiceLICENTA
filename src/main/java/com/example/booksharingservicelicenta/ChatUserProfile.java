package com.example.booksharingservicelicenta;

public class ChatUserProfile {


    public String username,userUID;

    public ChatUserProfile() {
    }

    public ChatUserProfile(String username, String userUID) {
        this.username = username;
        this.userUID = userUID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserUID() {
        return userUID;
    }

    public void setUserUID(String userUID) {
        this.userUID = userUID;
    }
}
