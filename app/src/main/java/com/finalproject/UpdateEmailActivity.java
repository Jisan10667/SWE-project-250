package com.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class UpdateEmailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_email);
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
        }else if(id == R.id.menu_logout){
            profileauth.signOut();
            Toast.makeText(UpdateEmailActivity.this,"Logged Out",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(UpdateEmailActivity.this,MainMenu.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }*/else{
            Toast.makeText(UpdateEmailActivity.this,"Something went wrong!",Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }
}