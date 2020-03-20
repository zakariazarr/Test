package com.zakariazarrouki.map.model;

import android.text.TextUtils;
import android.util.Patterns;

import java.io.Serializable;

import androidx.annotation.NonNull;

public class User implements Serializable {
    private String id;
    private String email;
    private String password;
    private String rePassword;
    private String phone;
    private String name;
    private String photoUrl;

    public User() {
    }

    public User(@NonNull final String email,
                @NonNull final String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(@NonNull final String email) {
        this.email = email;
    }

    @NonNull
    public String getPassword() {
        return password;
    }

    public void setPassword(@NonNull final String password) {
        this.password = password;
    }

    @NonNull
    public String getRePassword() {
        return rePassword;
    }

    public void setRePassword(@NonNull String mRePassword) {
        this.rePassword = mRePassword;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(@NonNull String mPhone) {
        this.phone = mPhone;
    }

    public String getName() {
        return name;
    }

    public void setName(@NonNull String mName) {
        this.name = mName;
    }

    public boolean isSignUpInputDataValid() {
        return  !TextUtils.isEmpty(getEmail()) &&
                Patterns.EMAIL_ADDRESS.matcher(getEmail()).matches() &&
                !TextUtils.isEmpty(getPhone()) &&
                !TextUtils.isEmpty(getName()) &&
                getPassword().length() > 5 &&
                getPassword().equals(getRePassword());
    }

    public boolean isSignInInputDataValid() {
        return  !TextUtils.isEmpty(getEmail()) &&
                Patterns.EMAIL_ADDRESS.matcher(getEmail()).matches() &&
                getPassword().length() > 5;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}