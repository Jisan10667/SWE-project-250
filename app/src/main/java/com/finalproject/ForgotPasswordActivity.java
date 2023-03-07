package com.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class ForgotPasswordActivity extends AppCompatActivity {

    private Button btnresetpass;
    private EditText etresetemail;
    private ProgressBar progressBar;
    private FirebaseAuth authProfile;

    public static final String Tag = "ForgotPasswordActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        btnresetpass = findViewById(R.id.btn_reset_pass);
        etresetemail = findViewById(R.id.et_reset_email);
        progressBar = findViewById(R.id.forgot_pass_progressbar);

        btnresetpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = etresetemail.getText().toString();

                if(TextUtils.isEmpty(email)){
                    Toast.makeText(ForgotPasswordActivity.this,"Please enter your email address",Toast.LENGTH_SHORT).show();
                    etresetemail.setError("Email is required");
                    etresetemail.requestFocus();
                }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    Toast.makeText(ForgotPasswordActivity.this,"Please enter valid email address",Toast.LENGTH_SHORT).show();
                    etresetemail.setError("Valid Email is required");
                    etresetemail.requestFocus();
                }else {
                    progressBar.setVisibility(View.VISIBLE);
                    resetPassword(email);
                }
            }
        });

    }

    private void resetPassword(String email) {
        authProfile = FirebaseAuth.getInstance();
        authProfile.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){
                    Toast.makeText(ForgotPasswordActivity.this,"Please Check your inbox for password reset link ",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ForgotPasswordActivity.this,MainMenu.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }else{
                    try{
                        throw task.getException();
                    }catch(FirebaseAuthInvalidUserException e){
                        etresetemail.setError("User does not exist or is no longer valid. Please register again");
                        etresetemail.requestFocus();
                    }catch (Exception e){
                        Log.e(Tag, e.getMessage());
                        Toast.makeText(ForgotPasswordActivity.this, "Something went wrong! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(ForgotPasswordActivity.this,"Something went wrong! ",Toast.LENGTH_SHORT).show();
                }

                progressBar.setVisibility(View.GONE);
            }
        });
    }
}