package com.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class UserProfile extends AppCompatActivity {

    private TextView tvwelcome, tvname, tvemail, tvdob, tvgender, tvmobile;
    private ProgressBar userprogressbar;
    private String name,email,dob,gender,mobile;
    private ImageView imageview;
    private FirebaseAuth profileauth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        getSupportActionBar().setTitle("Your Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tvwelcome = findViewById(R.id.show_welcome);
        tvname = findViewById(R.id.show_name);
        tvemail = findViewById(R.id.show_email);
        tvdob = findViewById(R.id.show_dob);
        tvgender = findViewById(R.id.show_gender);
        tvmobile = findViewById(R.id.show_mobile);
        userprogressbar = findViewById(R.id.user_progressbar);

        imageview = findViewById(R.id.profile_dp);
        imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserProfile.this, UploadProfilePicActivity.class);
                startActivity(intent);
            }
        });

        profileauth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = profileauth.getCurrentUser();

        if(firebaseUser == null){
            Toast.makeText(UserProfile.this, "Something went wrong. User details are not available at this moment ", Toast.LENGTH_LONG).show();
        }else{
            checkIfEmailVerified(firebaseUser);
            userprogressbar.setVisibility(View.VISIBLE);
            showUserProfile(firebaseUser);
        }

    }

    private void checkIfEmailVerified(FirebaseUser firebaseUser) {
        if(!firebaseUser.isEmailVerified()){
            showAlertDialog();
        }
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(UserProfile.this);
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

    private void showUserProfile(FirebaseUser firebaseUser) {
        String userID = firebaseUser.getUid();
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Register Users");
        referenceProfile.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                if(readUserDetails != null){
                    name = firebaseUser.getDisplayName();
                    //name = readUserDetails.name;
                    email = firebaseUser.getEmail();
                    dob = readUserDetails.dob;
                    gender = readUserDetails.gender;
                    mobile = readUserDetails.mobile;

                    tvwelcome.setText("Welcome " + name + "! ");
                    tvname.setText(name);
                    tvemail.setText(email);
                    tvdob.setText(dob);
                    tvgender.setText(gender);
                    tvmobile.setText(mobile);

                    Uri uri = firebaseUser.getPhotoUrl();
                    Picasso.get().load(uri).into(imageview);
                }else {
                    Toast.makeText(UserProfile.this, "Something went wrong! ", Toast.LENGTH_LONG).show();
                }
                userprogressbar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserProfile.this, "Something went wrong! ", Toast.LENGTH_LONG).show();
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

        if(id==android.R.id.home){
            NavUtils.navigateUpFromSameTask(UserProfile.this);
        }
        else if(id== R.id.menu_refresh){
            startActivity(getIntent());
            finish();
            overridePendingTransition(0,0);
        }else if(id == R.id.menu_update_profile){
            Intent intent = new Intent(UserProfile.this,UpdateProfileActivity.class);
            startActivity(intent);
        }else if(id == R.id.menu_update_email){
            Intent intent = new Intent(UserProfile.this,UpdateEmailActivity.class);
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
            Toast.makeText(UserProfile.this,"Logged Out",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(UserProfile.this,MainMenu.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }else{
            Toast.makeText(UserProfile.this,"Something went wrong!",Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }
}