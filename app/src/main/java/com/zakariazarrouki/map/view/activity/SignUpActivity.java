package com.zakariazarrouki.map.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.zakariazarrouki.map.R;
import com.zakariazarrouki.map.databinding.ActivitySignUpBinding;
import com.zakariazarrouki.map.model.User;
import com.zakariazarrouki.map.viewModel.LoginViewModel;
import com.zakariazarrouki.map.viewModel.SignupViewModel;

import static com.zakariazarrouki.map.utility.Functions.showInfoToast;

public class SignUpActivity extends AppCompatActivity {

    private EditText txtUsername;
    private EditText txtEmail;
    private EditText txtTele;
    private EditText txtPassword;
    private EditText reTxtPassword;
    private Button btnSignup;
    private TextView signInText;
    private Context mContext;
    private ProgressBar progressBar;
    private String TAG = this.getClass().getName();
    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        mFirebaseAuth = FirebaseAuth.getInstance();
        ActivitySignUpBinding activitySignUpBinding = DataBindingUtil.setContentView(this,R.layout.activity_sign_up);
        SignupViewModel signupViewModel = new ViewModelProvider(this).get(SignupViewModel.class);
        activitySignUpBinding.setSignupViewModel(signupViewModel);

        signupViewModel.setContext(this);
        signInText = activitySignUpBinding.signInText;
        txtUsername = activitySignUpBinding.txtUsername;
        txtPassword = activitySignUpBinding.txtPassword;
        reTxtPassword = activitySignUpBinding.reTxtPassword;
        txtEmail = activitySignUpBinding.txtEmail;
        txtTele = activitySignUpBinding.txtTele;
        btnSignup = activitySignUpBinding.btnSignup;
        progressBar = activitySignUpBinding.progressBar;
        progressBar.setVisibility(View.GONE);

        signupViewModel.getUser().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                progressBar.setVisibility(View.VISIBLE);
                createUser(user);
            }
        });

        signupViewModel.getErrorMessage().observe(this, s -> showInfoToast(mContext,s));

        signInText.setOnClickListener(v -> {
            startActivity(new Intent(mContext, LoginActivity.class));
            finish();
        });
    }

    public void createUser(final User user){
        mFirebaseAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(user.getName()).build();
                            if(firebaseUser!=null)
                                firebaseUser.updateProfile(profileUpdates);
                            goToMainActivity();
                        }else{
                            progressBar.setVisibility(View.GONE);
                            Log.w(TAG, "Exception : ", task.getException());
                            Exception ex = task.getException();
                            if (ex == null){
                                showInfoToast(mContext,"Authentication echoué, merci de ressayer !");
                            }else{
                                showInfoToast(mContext,ex.getMessage());
                            }
                        }
                    }
                });
    }

    public void goToMainActivity(){
        startActivity(new Intent(mContext, MainActivity.class));
        finish();
    }
}
