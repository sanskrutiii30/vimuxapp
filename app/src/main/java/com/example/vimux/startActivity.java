package com.example.vimux;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class startActivity extends AppCompatActivity {

    TextView vimux,vix;
    RelativeLayout relativeLayout;
    Animation txtAnimation,layoutAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        txtAnimation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fall_down);
        layoutAnimation = AnimationUtils.loadAnimation( getApplicationContext(),R.anim.bottom_to_top);

        vimux = findViewById(R.id.vimux);
        vix = findViewById(R.id.vix);
        relativeLayout=findViewById(R.id.relMain);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                relativeLayout.setVisibility(View.VISIBLE);
                relativeLayout.setAnimation(layoutAnimation);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        vimux.setVisibility(View.VISIBLE);
                        vix.setVisibility(View.VISIBLE);
                        vimux.setAnimation(txtAnimation);
                        vix.setAnimation(txtAnimation);
                    }

                }, 500);
            }
        }, 1000);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent Intent =new Intent(startActivity.this,MainActivity.class);
                startActivity(Intent);
            }


        },5000 );

    }
    }
