package com.zakariazarrouki.map.viewModel;

import android.content.Context;

import com.zakariazarrouki.map.R;
import com.zakariazarrouki.map.model.User;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SignupViewModel extends ViewModel {

    private User user;
    private MutableLiveData<User> userLiveData;
    private MutableLiveData<String> mMessage;
    private Context context;

    public SignupViewModel() {
        user = new User();
        userLiveData = new MutableLiveData<>();
        mMessage = new MutableLiveData<>();
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void afterEmailTextChanged(CharSequence s) {
        user.setEmail(s.toString());
    }

    public void afterPasswordTextChanged(CharSequence s) {
        user.setPassword(s.toString());
    }

    public void afterRePasswordTextChanged(CharSequence s) {
        user.setRePassword(s.toString());
    }

    public void afterUsernameTextChanged(CharSequence s) {
        user.setName(s.toString());
    }

    public void afterPhoneTextChanged(CharSequence s) { user.setPhone(s.toString()); }

    public void onSignUpClicked(){
        if (user.isSignUpInputDataValid()){
            userLiveData.setValue(user);
        }else{
            mMessage.setValue(context.getString(R.string.signup_failed_msg));
        }
    }

    public LiveData<User> getUser(){
        return userLiveData;
    }

    public LiveData<String> getErrorMessage(){
        return mMessage;
    }
}
