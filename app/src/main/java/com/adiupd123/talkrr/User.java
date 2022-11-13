package com.adiupd123.talkrr;

public class User {
    String userId, name, phoneNumber, profileImage;

    public User(){}

    public User(String userId, String name, String phoneNumber, String profileImage) {
        this.userId = userId;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.profileImage = profileImage;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
}
