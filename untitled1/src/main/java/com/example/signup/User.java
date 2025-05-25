package com.example.signup;

public class User {
    private String username;
    private String encryptedPassword;
    private String firstName;
    private String lastName;
    private byte[] photoId;

    public User() {
    }

    public User(String username, String encryptedPassword, String firstName, String lastName, byte[] photoId) {
        this.username = username;
        this.encryptedPassword = encryptedPassword;
        this.firstName = firstName;
        this.lastName = lastName;
        this.photoId = photoId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    public void setEncryptedPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public byte[] getPhotoId() {
        return photoId;
    }

    public void setPhotoId(byte[] photoId) {
        this.photoId = photoId;
    }
}
