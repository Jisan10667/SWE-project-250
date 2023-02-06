package com.firsttry.swe_project_main.chefFoodPanel;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.firsttry.swe_project_main.*;
public class ChefProfileFragment extends Fragment {

    Button postDish;
    ConstraintLayout backimg;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chef_prifile,null);
        getActivity().setTitle("Post Dish");

        AnimationDrawable animationDrawable = new AnimationDrawable();
        animationDrawable.addFrame(getResources().getDrawable(R.drawable.ch1),3000);
        animationDrawable.addFrame(getResources().getDrawable(R.drawable.ch2),3000);
        animationDrawable.addFrame(getResources().getDrawable(R.drawable.ch3),3000);
        animationDrawable.addFrame(getResources().getDrawable(R.drawable.ch4),3000);
        animationDrawable.addFrame(getResources().getDrawable(R.drawable.ch5),3000);
        animationDrawable.addFrame(getResources().getDrawable(R.drawable.ch6),3000);
        animationDrawable.addFrame(getResources().getDrawable(R.drawable.ch7),3000);
        animationDrawable.addFrame(getResources().getDrawable(R.drawable.ch8),3000);
        animationDrawable.addFrame(getResources().getDrawable(R.drawable.ch9),3000);
        animationDrawable.addFrame(getResources().getDrawable(R.drawable.ch10),3000);
        animationDrawable.addFrame(getResources().getDrawable(R.drawable.ch11),3000);
        animationDrawable.addFrame(getResources().getDrawable(R.drawable.ch3),3000);
        animationDrawable.addFrame(getResources().getDrawable(R.drawable.ch2),3000);

        animationDrawable.setOneShot(false);
        animationDrawable.setEnterFadeDuration(850);
        animationDrawable.setExitFadeDuration(1600);

        backimg = v.findViewById(R.id.back1);
        backimg.setBackgroundDrawable(animationDrawable);
        animationDrawable.start();

        postDish =  (Button)v.findViewById(R.id.post_dish);

        postDish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(),chef_postDish.class));
            }
        });

        return v;
    }
}
