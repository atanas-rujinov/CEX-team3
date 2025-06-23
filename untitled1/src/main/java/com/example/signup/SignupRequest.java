package com.example.signup;

import org.springframework.web.multipart.MultipartFile;

public class SignupRequest {
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private MultipartFile photoId;

    public SignupRequest() {
    }

    public SignupRequest(String username, String password, String firstName, String lastName) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public MultipartFile getPhotoId() {
        return photoId;
    }

    public void setPhotoId(MultipartFile photoId) {
        this.photoId = photoId;
    }
}
