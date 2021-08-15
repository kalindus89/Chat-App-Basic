package com.chatapp.chats_adapter;

public class ChatsModel {

    private String name; // same name define in fireStore documents
    private String image;
    private String uid;
    private String status;

    public ChatsModel() {
    }

    public ChatsModel(String name, String image, String uid, String status) {
        this.name = name;
        this.image = image;
        this.uid = uid;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
