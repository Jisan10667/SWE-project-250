package com.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends AppCompatActivity {

    TextInputEditText etRegName;
    TextInputEditText etRegEmail;
    TextInputEditText etRegPassword;
    TextInputEditText etRegDob;
    TextInputEditText etRegConPassword;
    TextInputEditText etRegMobile;
    TextView tvsigninHere;
    Button btnRegister;

    ProgressBar regprogressbar;
    RadioGroup regradiogroup;
    RadioButton regradiobtn;

    FirebaseAuth mAuth;

    ConstraintLayout bgimage;

    public static final String Tag = "SignUp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //getSupportActionBar().setTitle("Sign Up");
        Toast.makeText(SignUp.this,"You can register now",Toast.LENGTH_LONG).show();

        AnimationDrawable animationDrawable = new AnimationDrawable();
        animationDrawable.addFrame(getResources().getDrawable(R.drawable.ch1),3000);
        animationDrawable.addFrame(getResources().getDrawable(R.drawable.ch2),3000);
        animationDrawable.addFrame(getResources().getDrawable(R.drawable.ch3),3000);
        animationDrawable.addFrame(getResources().getDrawable(R.drawable.ch4),3000);
        animationDrawable.addFrame(getResources().getDrawable(R.drawable.ch5),3000);

        animationDrawable.setOneShot(false);
        animationDrawable.setEnterFadeDuration(850);
        animationDrawable.setExitFadeDuration(1600);

        bgimage = findViewById(R.id.back2);
        bgimage.setBackgroundDrawable(animationDrawable);
        animationDrawable.start();

        etRegName = findViewById(R.id.signup_name);
        etRegEmail = findViewById(R.id.signup_email);
        etRegPassword = findViewById(R.id.signup_pass);
        etRegDob = findViewById(R.id.signup_dob);
        etRegConPassword= findViewById(R.id.signup_conpass);
        etRegMobile = findViewById(R.id.signup_mobile);
        tvsigninHere = findViewById(R.id.tvSigninHere);
        btnRegister = findViewById(R.id.SignUp_button);
        regprogressbar = findViewById(R.id.signup_progressbar);

        mAuth = FirebaseAuth.getInstance();

        regradiogroup = findViewById(R.id.signup_gender_group);
        regradiogroup.clearCheck();

        btnRegister.setOnClickListener(view ->{
            createUser();
        });

        tvsigninHere.setOnClickListener(view ->{
            startActivity(new Intent(SignUp.this, SignIn.class));
        });
    }

    private void createUser(){

        int selectedGenderId = regradiogroup.getCheckedRadioButtonId();
        regradiobtn = findViewById(selectedGenderId);

        String name = etRegName.getText().toString();
        String email = etRegEmail.getText().toString();
        String dob = etRegDob.getText().toString();
        String mobile = etRegMobile.getText().toString();
        String password = etRegPassword.getText().toString();
        String conpassword = etRegConPassword.getText().toString();
        String gender;

        if (TextUtils.isEmpty(email)){
            Toast.makeText(SignUp.this,"Please enter your email",Toast.LENGTH_LONG).show();
            etRegEmail.setError("Email cannot be empty");
            etRegEmail.requestFocus();
        }else if (TextUtils.isEmpty(password)){
            Toast.makeText(SignUp.this,"Please enter your password",Toast.LENGTH_LONG).show();
            etRegPassword.setError("Password cannot be empty");
            etRegPassword.requestFocus();
        }else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(SignUp.this,"Please re-enter your email",Toast.LENGTH_LONG).show();
            etRegPassword.setError("Valid email is required");
            etRegPassword.requestFocus();
        }else if (TextUtils.isEmpty(name)){
            Toast.makeText(SignUp.this,"Please enter your name",Toast.LENGTH_LONG).show();
            etRegName.setError("Name cannot be empty");
            etRegName.requestFocus();
        }else if (TextUtils.isEmpty(dob)){
            Toast.makeText(SignUp.this,"Please enter your date of birth",Toast.LENGTH_LONG).show();
            etRegDob.setError("Date of Birth cannot be empty");
            etRegDob.requestFocus();
        }else if (regradiogroup.getCheckedRadioButtonId()==-1){
            Toast.makeText(SignUp.this,"Please enter your date of birth",Toast.LENGTH_LONG).show();
            regradiobtn.setError("Date of Birth cannot be empty");
            regradiobtn.requestFocus();
        }else if (TextUtils.isEmpty(mobile)){
            Toast.makeText(SignUp.this,"Please enter your mobile number",Toast.LENGTH_LONG).show();
            etRegMobile.setError("Mobile Number cannot be empty");
            etRegMobile.requestFocus();
        }else if (mobile.length() != 11){
            Toast.makeText(SignUp.this,"Please re-enter your mobile number",Toast.LENGTH_LONG).show();
            etRegMobile.setError("Mobile Number should be 11 digits");
            etRegMobile.requestFocus();
        }else if (password.length() < 6){
            Toast.makeText(SignUp.this,"password should be at least 6 digits",Toast.LENGTH_LONG).show();
            etRegPassword.setError("Password is too weak");
            etRegPassword.requestFocus();
        }else if (TextUtils.isEmpty(conpassword)) {
            Toast.makeText(SignUp.this, "Please confirm your password", Toast.LENGTH_LONG).show();
            etRegConPassword.setError("Password cannot be empty");
            etRegConPassword.requestFocus();
        }else if (!password.equals(conpassword)) {
            Toast.makeText(SignUp.this, "Please enter same password", Toast.LENGTH_LONG).show();
            etRegConPassword.setError("Password confirmation is required");
            etRegConPassword.requestFocus();
            etRegPassword.clearComposingText();
            etRegConPassword.clearComposingText();
        }
        else{
            gender = regradiobtn.getText().toString();
            regprogressbar.setVisibility(View.VISIBLE);

            registerUser(name,email,dob,gender,mobile,password);

        }
    }

    private void registerUser(String name, String email, String dob, String gender, String mobile, String password) {
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    FirebaseUser firebaseUser = mAuth.getCurrentUser();

                    ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(name,dob,gender,mobile);
                    DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Register Users");

                    referenceProfile.child(firebaseUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                firebaseUser.sendEmailVerification();
                                Toast.makeText(SignUp.this, "User registered successfully and Please verify your email", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(SignUp.this,SignIn.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                            else{
                                Toast.makeText(SignUp.this, "User registration failed. Please Try again", Toast.LENGTH_SHORT).show();
                                regprogressbar.setVisibility(View.GONE);
                            }
                        }
                    });


                    //startActivity(new Intent(SignUp.this, SignIn.class));
                }else{
                    try{
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e){
                        etRegPassword.setError("Password is too weak");
                        etRegPassword.requestFocus();
                    } catch (FirebaseAuthInvalidCredentialsException e){
                        etRegPassword.setError("Password is invalid or already in use");
                        etRegPassword.requestFocus();
                    } catch (FirebaseAuthUserCollisionException e){
                        etRegPassword.setError("User is already registered with this email");
                        etRegPassword.requestFocus();
                    } catch (Exception e){
                        Log.e(Tag, e.getMessage());
                    }
                    Toast.makeText(SignUp.this, "Registration Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}