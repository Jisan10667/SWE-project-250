package com.firsttry.swe_project_main;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.firsttry.swe_project_main.customerFoodPanel.CustomerHomeFragment;
import com.firsttry.swe_project_main.customerFoodPanel.CustomerTrackFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.firsttry.swe_project_main.chefFoodPanel.*;


public class ChefFoodPanel_BottomNavigation extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chef_food_panel__bottom_navigation);
        BottomNavigationView navigationView = findViewById(R.id.chef_bottom_navigation);
        navigationView.setOnNavigationItemSelectedListener(this);
        String name = getIntent().getStringExtra("PAGE");
        //FragmentManager fragmentManager = getSupportFragmentManager();
        //FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if(name!=null)
        {
            if(name.equalsIgnoreCase("Orderpage")){
                loadcheffragment(new ChefPendingOrderFragment());
            }
            else if(name.equalsIgnoreCase("ConfirmPage")){
                loadcheffragment(new ChefOrderFragment());
            }
            else if(name.equalsIgnoreCase("AcceptOrderPage")){
                loadcheffragment(new ChefOrderFragment());
            }
            else if(name.equalsIgnoreCase("DeliveredPage")){
                loadcheffragment(new ChefOrderFragment());
            }
        }
        else {
            loadcheffragment(new ChefHomeFragment());
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        switch (item.getItemId()){
            case R.id.chefHome:
                fragment=new ChefHomeFragment();
                break;
            case R.id.PendingOrders:
                fragment=new ChefPendingOrderFragment();
                break;
            case R.id.Orders:
                fragment=new ChefOrderFragment();
                break;
            case R.id.chefProfile:
                fragment=new ChefProfileFragment();
                break;
        }
        return loadcheffragment(fragment);
    }

    private boolean loadcheffragment(Fragment fragment) {

        if (fragment != null){
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_container,fragment).commit();
            return true;
        }
        return false;
    }
}