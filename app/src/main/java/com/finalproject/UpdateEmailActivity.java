package com.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UpdateEmailActivity extends AppCompatActivity {

    private FirebaseAuth profileauth;
    private FirebaseUser firebaseUser;
    private ProgressBar progressBar;
    private TextView tvAuthenticated;
    private String userOldEmail, userNewEmail, userPassword;
    private Button btnUpdateEmail;
    private EditText etOldEmail, etNewEmail, etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_email);

        getSupportActionBar().setTitle("Update Email");

        progressBar = findViewById(R.id.update_email_progressbar);
        etPassword = findViewById(R.id.et_updatemail_password);
        etNewEmail = findViewById(R.id.et_updatemail_newemail);
        etOldEmail = findViewById(R.id.et_updatemail_email);
        tvAuthenticated = findViewById(R.id.tv_update_email_head2);
        btnUpdateEmail = findViewById(R.id.btn_updatemail_email);

        btnUpdateEmail.setEnabled(false);
        etNewEmail.setEnabled(false);

        profileauth = FirebaseAuth.getInstance();
        firebaseUser = profileauth.getCurrentUser();

        userOldEmail = firebaseUser.getEmail();
        TextView tvOldEmail = findViewById(R.id.tv_updatemail_email);
        etOldEmail.setText(userOldEmail);

        if(firebaseUser.equals("")){
            Toast.makeText(UpdateEmailActivity.this,"Something went wrong",Toast.LENGTH_LONG).show();
        }else{
            reAuthenticate(firebaseUser);
        }

    }

    private void reAuthenticate(FirebaseUser firebaseUser) {
        Button btnVerifyUser = findViewById(R.id.btn_updatemail_authenticate);
        btnVerifyUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userPassword = etPassword.getText().toString();

                if(TextUtils.isEmpty(userPassword)){
                    Toast.makeText(UpdateEmailActivity.this,"Password is needed to continue ",Toast.LENGTH_LONG).show();
                    etPassword.setError("Please Enter Your password for authentication");
                    etPassword.requestFocus();
                }else{
                    progressBar.setVisibility(View.VISIBLE);

                    AuthCredential credential = EmailAuthProvider.getCredential(userOldEmail,userPassword);
                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(UpdateEmailActivity.this,"Password has been verified. You can update your email now ",Toast.LENGTH_LONG).show();

                                tvAuthenticated.setText("You are authenticated. You can update your email now");
                                etNewEmail.setEnabled(true);
                                btnUpdateEmail.setEnabled(true);
                                btnVerifyUser.setEnabled(false);
                                etPassword.setEnabled(false);

                                btnUpdateEmail.setBackgroundTintList(ContextCompat.getColorStateList(UpdateEmailActivity.this,R.color.darkgreen));

                                btnUpdateEmail.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        userNewEmail = etNewEmail.getText().toString();
                                        if (TextUtils.isEmpty(userNewEmail)){
                                            Toast.makeText(UpdateEmailActivity.this,"New email is required",Toast.LENGTH_LONG).show();
                                            etNewEmail.setError("Please enter new email");
                                            etNewEmail.requestFocus();
                                        }else if (!Patterns.EMAIL_ADDRESS.matcher(userNewEmail).matches()){
                                            Toast.makeText(UpdateEmailActivity.this,"Please re-enter your email",Toast.LENGTH_LONG).show();
                                            etNewEmail.setError("Valid email is required");
                                            etNewEmail.requestFocus();
                                        }else if(userOldEmail.matches(userNewEmail)){
                                            Toast.makeText(UpdateEmailActivity.this,"New email can not be the same as old email",Toast.LENGTH_LONG).show();
                                            etNewEmail.setError("Please enter new email");
                                            etNewEmail.requestFocus();
                                        }else{
                                            progressBar.setVisibility(View.VISIBLE);
                                            updateEmail(firebaseUser);
                                        }
                                    }
                                });
                            }else{
                                try{
                                    throw task.getException();
                                }catch (Exception e){
                                    Toast.makeText(UpdateEmailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    private void updateEmail(FirebaseUser firebaseUser) {

        firebaseUser.updateEmail(userNewEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isComplete()){
                    firebaseUser.sendEmailVerification();
                    Toast.makeText(UpdateEmailActivity.this, "Email has been updated. Please verify your new email.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(UpdateEmailActivity.this, UserProfile.class);
                    startActivity(intent);
                    finish();
                }else{
                    try{
                        throw task.getException();
                    }catch (Exception e){
                        Toast.makeText(UpdateEmailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.common_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id== R.id.menu_refresh){
            startActivity(getIntent());
            finish();
            overridePendingTransition(0,0);
        }else if(id == R.id.menu_update_profile){
            Intent intent = new Intent(UpdateEmailActivity.this,UpdateProfileActivity.class);
            startActivity(intent);
        }else if(id == R.id.menu_update_email){
            Intent intent = new Intent(UpdateEmailActivity.this,UpdateEmailActivity.class);
            startActivity(intent);
            finish();
        }/*else if(id == R.id.menu_settings){
            //Intent intent = new Intent(UserProfile.this,UserProfileActivity.class);
            //startActivity(intent);
            Toast.makeText(UserProfile.this,"menu settings",Toast.LENGTH_LONG).show();
        }else if(id == R.id.menu_change_password){
            Intent intent = new Intent(UserProfile.this,ChangePasswordActivity.class);
            startActivity(intent);
        }else if(id == R.id.menu_delete_profile){
            Intent intent = new Intent(UserProfile.this,DeleteProfileActivity.class);
            startActivity(intent);
        }*/else if(id == R.id.menu_logout){
            profileauth.signOut();
            Toast.makeText(UpdateEmailActivity.this,"Logged Out",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(UpdateEmailActivity.this,MainMenu.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }else{
            Toast.makeText(UpdateEmailActivity.this,"Something went wrong!",Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }
}