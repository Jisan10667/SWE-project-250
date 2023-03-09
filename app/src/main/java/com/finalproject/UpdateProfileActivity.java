package com.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class UpdateProfileActivity extends AppCompatActivity {

    private EditText etUpdateName, etUpdateDob, etUpdateMobile;
    private RadioGroup radioGroupUpdateGender;
    private RadioButton radioButtonUpdateGenderSelected;
    private String name, dob, gender, mobile;
    private ProgressBar progressBar;
    private FirebaseAuth authProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        getSupportActionBar().setTitle("Update Profile Details");

        etUpdateName = findViewById(R.id.et_update_name);
        etUpdateDob = findViewById(R.id.et_update_dob);
        etUpdateMobile = findViewById(R.id.et_update_mobile);

        radioGroupUpdateGender = findViewById(R.id.update_profile_gender_group);
        progressBar = findViewById(R.id.update_profile_progressbar);

        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        showProfile(firebaseUser);

        Button btnUploadProfile = findViewById(R.id.btn_upload_profile);
        btnUploadProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UpdateProfileActivity.this, UploadProfilePicActivity.class);
                startActivity(intent);
                finish();
            }
        });

        /*Button btnUpdateEmail = findViewById(R.id.btn_update_email);
        btnUpdateEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UpdateProfileActivity.this, UpdateEmailActivity.class);
                startActivity(intent);
                finish();
            }
        });
*/
        etUpdateDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textSADob[] = dob.split("/");

                int day = Integer.parseInt(textSADob[0]);
                int month = Integer.parseInt(textSADob[1]) - 1;
                int year = Integer.parseInt(textSADob[2]);

                DatePickerDialog picker;

                picker = new DatePickerDialog(UpdateProfileActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        etUpdateDob.setText(i2+"/"+(i1+1)+"/"+i);
                    }
                },year,month,day);
                picker.show();
            }
        });

        Button btnUpdateProfile = findViewById(R.id.btn_update_profile);
        btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile(firebaseUser);
            }
        });
    }

    private void updateProfile(FirebaseUser firebaseUser) {
        int selectedGenderID = radioGroupUpdateGender.getCheckedRadioButtonId();
        radioButtonUpdateGenderSelected = findViewById(selectedGenderID);


       if (TextUtils.isEmpty(name)){
            Toast.makeText(UpdateProfileActivity.this,"Please enter your name",Toast.LENGTH_LONG).show();
            etUpdateName.setError("Name cannot be empty");
            etUpdateName.requestFocus();
       }else if (TextUtils.isEmpty(dob)){
            Toast.makeText(UpdateProfileActivity.this,"Please enter your date of birth",Toast.LENGTH_LONG).show();
            etUpdateDob.setError("Date of Birth cannot be empty");
            etUpdateDob.requestFocus();
       }else if (radioGroupUpdateGender.getCheckedRadioButtonId()==-1){
            Toast.makeText(UpdateProfileActivity.this,"Please enter your date of birth",Toast.LENGTH_LONG).show();
            radioButtonUpdateGenderSelected.setError("Date of Birth cannot be empty");
            radioButtonUpdateGenderSelected.requestFocus();
       }else if (TextUtils.isEmpty(mobile)){
            Toast.makeText(UpdateProfileActivity.this,"Please enter your mobile number",Toast.LENGTH_LONG).show();
            etUpdateMobile.setError("Mobile Number cannot be empty");
            etUpdateMobile.requestFocus();
       }else if (mobile.length() != 11){
            Toast.makeText(UpdateProfileActivity.this,"Please re-enter your mobile number",Toast.LENGTH_LONG).show();
            etUpdateMobile.setError("Mobile Number should be 11 digits");
            etUpdateMobile.requestFocus();
       }
       else{
            gender = radioButtonUpdateGenderSelected.getText().toString();
            name = etUpdateName.getText().toString();
            dob = etUpdateDob.getText().toString();
            mobile = etUpdateMobile.getText().toString();

            ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(name, dob, gender, mobile);
            DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Register Users");
            String userID = firebaseUser.getUid();


            progressBar.setVisibility(View.VISIBLE);

            referenceProfile.child(userID).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
                        firebaseUser.updateProfile(profileUpdates);

                        Toast.makeText(UpdateProfileActivity.this,"Update Successful! ", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(UpdateProfileActivity.this, UserProfile.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else{
                        try{
                            throw task.getException();
                        }catch (Exception e){
                            Toast.makeText(UpdateProfileActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }
                    progressBar.setVisibility(View.GONE);
                }
            });
       }
    }

    private void showProfile(FirebaseUser firebaseUser) {

        String userIDofRegistered = firebaseUser.getUid();
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Register Users");

        progressBar.setVisibility(View.VISIBLE);

        referenceProfile.child(userIDofRegistered).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readUserDetails = snapshot.getValue(ReadWriteUserDetails.class);

                if(readUserDetails != null){
                    name = firebaseUser.getDisplayName();
                    dob = readUserDetails.dob;
                    gender = readUserDetails.gender;
                    mobile = readUserDetails.mobile;

                    etUpdateName.setText(name);
                    etUpdateDob.setText(dob);
                    etUpdateMobile.setText(mobile);

                    if(gender.equals("Male")){
                        radioButtonUpdateGenderSelected = findViewById(R.id.update_radio_male);
                    }else{
                        radioButtonUpdateGenderSelected = findViewById(R.id.update_radio_female);
                    }
                    radioButtonUpdateGenderSelected.setChecked(true);
                }else {
                    Toast.makeText(UpdateProfileActivity.this,"Something went wrong! ", Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpdateProfileActivity.this,"Something went wrong! ", Toast.LENGTH_SHORT).show();
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
            Intent intent = new Intent(UpdateProfileActivity.this,UpdateProfileActivity.class);
            startActivity(intent);
            finish();
        }else if(id == R.id.menu_update_email){
            Intent intent = new Intent(UpdateProfileActivity.this,UpdateEmailActivity.class);
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
            authProfile.signOut();
            Toast.makeText(UpdateProfileActivity.this,"Logged Out",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(UpdateProfileActivity.this,MainMenu.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }else{
            Toast.makeText(UpdateProfileActivity.this,"Something went wrong!",Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }
}