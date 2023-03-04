package com.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class SignIn extends AppCompatActivity {


    private TextInputEditText signin_email;
    private TextInputEditText signin_pass;
    private Button btnSignin;
    private TextView tvRegisterHere;

    public static final String Tag = "SignIn";

    ProgressBar progressbar;
    FirebaseAuth mmAuth;

    ConstraintLayout bgimage;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);


        AnimationDrawable animationDrawable = new AnimationDrawable();
        animationDrawable.addFrame(getResources().getDrawable(R.drawable.ch1),3000);
        animationDrawable.addFrame(getResources().getDrawable(R.drawable.ch2),3000);
        animationDrawable.addFrame(getResources().getDrawable(R.drawable.ch3),3000);
        animationDrawable.addFrame(getResources().getDrawable(R.drawable.ch4),3000);
        animationDrawable.addFrame(getResources().getDrawable(R.drawable.ch5),3000);

        animationDrawable.setOneShot(false);
        animationDrawable.setEnterFadeDuration(850);
        animationDrawable.setExitFadeDuration(1600);

        bgimage = findViewById(R.id.back3);
        bgimage.setBackgroundDrawable(animationDrawable);
        animationDrawable.start();

        signin_email = findViewById(R.id.signin_email);
        signin_pass = findViewById(R.id.signin_pass);
        tvRegisterHere = findViewById(R.id.tvSignupHere);
        btnSignin = findViewById(R.id.SignIn_button);
        progressbar = findViewById(R.id.signin_progressbar);

        mmAuth = FirebaseAuth.getInstance();

        signin_email = (TextInputEditText) findViewById(R.id.signin_email);
        signin_pass = (TextInputEditText) findViewById(R.id.signin_pass);
        btnSignin = (Button) findViewById(R.id.SignIn_button);

        /*btnSignin.setOnClickListener(view -> {
            //loginUser();

            if(signin_email.getText().length()>0 && signin_pass.getText().length()>0){
                loginUser(signin_email.getText().toString(),signin_pass.getText().toString());
            }
            else{
                Toast.makeText(getApplicationContext(),"Give correct email and password",Toast.LENGTH_SHORT).show();
            }
        });*/

        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = signin_email.getText().toString();
                String password = signin_pass.getText().toString();

                if (TextUtils.isEmpty(email)){
                    Toast.makeText(SignIn.this,"Please enter your email",Toast.LENGTH_LONG).show();
                    signin_email.setError("Email cannot be empty");
                    signin_email.requestFocus();
                } else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    Toast.makeText(SignIn.this,"Please re-enter your email",Toast.LENGTH_LONG).show();
                    signin_email.setError("Valid email is required");
                    signin_email.requestFocus();
                }else if (TextUtils.isEmpty(password)){
                    Toast.makeText(SignIn.this,"Please enter your password",Toast.LENGTH_LONG).show();
                    signin_pass.setError("Password cannot be empty");
                    signin_pass.requestFocus();
                }else{
                    progressbar.setVisibility(View.VISIBLE);
                    loginUser(email,password);
                }

            }
        });


        tvRegisterHere = findViewById(R.id.tvSignupHere);
        tvRegisterHere.setOnClickListener(view ->{
            startActivity(new Intent(SignIn.this, SignUp.class));
        });
    }

    private void loginUser(String email, String password)
    {
        mmAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(SignIn.this,new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    FirebaseUser firebaseUser = mmAuth.getCurrentUser();
                    if(firebaseUser.isEmailVerified()){
                        Toast.makeText(SignIn.this, "User logged in successfully", Toast.LENGTH_SHORT).show();
                    }else{
                        firebaseUser.sendEmailVerification();
                        mmAuth.signOut();
                        showAlertDialog();
                    }

                    startActivity(new Intent(SignIn.this, Home.class));
                }else{
                    try{
                        throw task.getException();
                    }catch(FirebaseAuthInvalidUserException e){
                        signin_email.setError("User does not exist or is no longer valid. Please register again");
                        signin_email.requestFocus();
                    }catch(FirebaseAuthInvalidCredentialsException e){
                        signin_email.setError("Invalid credentials. Kindly check and re-enter");
                        signin_email.requestFocus();
                    }catch (Exception e){
                        Log.e(Tag, e.getMessage());
                        Toast.makeText(SignIn.this, "Log in Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                progressbar.setVisibility(View.GONE);
            }
        });
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SignIn.this);
        builder.setTitle("Email not verified");
        builder.setMessage("Please verify your email now. You can not login without email verification");

        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mmAuth.getCurrentUser()!=null){
            Toast.makeText(SignIn.this, "Already logged in ", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SignIn.this,UserProfile.class));
            finish();
        }else{
            Toast.makeText(SignIn.this, "You can login now", Toast.LENGTH_SHORT).show();
        }
    }
}