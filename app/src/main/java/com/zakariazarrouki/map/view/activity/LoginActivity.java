package com.zakariazarrouki.map.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
//import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.zakariazarrouki.map.R;
import com.zakariazarrouki.map.databinding.ActivityLoginBinding;
import com.zakariazarrouki.map.model.User;
import com.zakariazarrouki.map.viewModel.LoginViewModel;

import static com.zakariazarrouki.map.utility.Functions.showInfoToast;

public class LoginActivity extends AppCompatActivity {

    private String TAG = this.getClass().getName();
    private FirebaseAuth mFirebaseAuth;
    private Context mContext;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mContext = this;

        mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        ActivityLoginBinding activityLoginBinding = DataBindingUtil.setContentView(this,R.layout.activity_login);
        LoginViewModel loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        activityLoginBinding.setLoginViewModel(loginViewModel);

        TextView signUpText = activityLoginBinding.signUpText;
        progressBar = activityLoginBinding.progressBar;
        progressBar.setVisibility(View.GONE);

        signUpText.setOnClickListener(v -> {
            startActivity(new Intent(mContext,SignUpActivity.class));
            finish();
        });

        loginViewModel.getUser().observe(this, user -> {
            progressBar.setVisibility(View.VISIBLE);
            //btnLogin.startAnimation();
            signIn(user);
        });

        loginViewModel.getErrorMessage().observe(this, s -> showInfoToast(mContext,s));
    }

    public void signIn(final User user){
        mFirebaseAuth.signInWithEmailAndPassword(user.getEmail(), user.getPassword())
            .addOnCompleteListener(this, task -> {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    goToMainActivity();
                }else{
                    //btnLogin.stopAnimation();
                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                    Exception ex = task.getException();
                    if (ex == null){
                        showInfoToast(mContext,getString(R.string.auth_failed_msg));
                    }else{
                        showInfoToast(mContext,ex.getMessage());
                    }
                }
            });
    }

    public void goToMainActivity(){
        startActivity(new Intent(this,MainActivity.class));
        finish();
    }
}
