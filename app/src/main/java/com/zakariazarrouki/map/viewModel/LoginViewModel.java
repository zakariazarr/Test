package com.zakariazarrouki.map.viewModel;

import com.zakariazarrouki.map.model.User;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LoginViewModel extends ViewModel {

    private User user;
    private MutableLiveData<User> userLiveData;
    private MutableLiveData<String> mMsgLiveData;

    public LoginViewModel(){
        user = new User();
        userLiveData = new MutableLiveData<>();
        mMsgLiveData = new MutableLiveData<>();
    }

    public void afterEmailTextChanged(CharSequence s) {
        user.setEmail(s.toString());
    }

    public void afterPasswordTextChanged(CharSequence s) {
        user.setPassword(s.toString());
    }

    public void onLoginClicked(){
        if (user.isSignInInputDataValid()){
            userLiveData.setValue(user);
        }else{
            mMsgLiveData.setValue("Adresse email ou mot de passe est incorrect merci de réessayer");
        }
    }

    public LiveData<User> getUser(){
        return userLiveData;
    }

    public LiveData<String> getErrorMessage(){
        return mMsgLiveData;
    }
}
