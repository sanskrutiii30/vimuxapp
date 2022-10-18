package com.example.vimux;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.vimux.Model.Users;
import com.example.vimux.Prevalent.Prevalent;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    ProgressDialog loadingBar;

    TabLayout tabLayout;
    ViewPager viewPager;
//    FloatingActionButton fb, google, twitter;
    float v=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Paper.init(this);
        loadingBar=new ProgressDialog(this);

        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);
//        fb = findViewById(R.id.fab_facebook);
//        google = findViewById(R.id.fab_google);
//        twitter = findViewById(R.id.fab_twitter);

        tabLayout.addTab(tabLayout.newTab().setText("Login"));
        tabLayout.addTab(tabLayout.newTab().setText("SignUp"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final LoginAdapter adapter = new LoginAdapter(getSupportFragmentManager(), this, tabLayout.getTabCount());
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

//        fb.setTranslationY(300);
//        google.setTranslationY(300);
//        twitter.setTranslationY(300);
//        tabLayout.setTranslationY(300);

//        fb.setAlpha(v);
//        google.setAlpha(v);
//        twitter.setAlpha(v);
//        tabLayout.setAlpha(v);

//        fb.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(400).start();
//        google.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(600).start();
//        twitter.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(800).start();
//        tabLayout.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(100).start();

        checkConnection();
    }

    private void checkConnection() {

        ConnectivityManager cManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNt = cManager.getActiveNetworkInfo();
        if (null!=activeNt)
        {
            String UserPhoneKey= Paper.book().read(Prevalent.UserNameKey);
            String UserPasswordKey= Paper.book().read(Prevalent.UserPasswordKey);

            if(UserPhoneKey != "" && UserPasswordKey != ""){
                if(!TextUtils.isEmpty(UserPhoneKey) && !TextUtils.isEmpty(UserPasswordKey)){
                    AllowAccess(UserPhoneKey,UserPasswordKey);
                    loadingBar.setTitle("Already Logged In");
                    loadingBar.setMessage("Please Wait.....");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();
                }
            }
//            if (activeNt.getType() == ConnectivityManager.TYPE_WIFI){
//                Toast.makeText(getApplicationContext(), "wifi enabled", Toast.LENGTH_SHORT).show();
//            }
//            if (activeNt.getType() == ConnectivityManager.TYPE_MOBILE){
//                Toast.makeText(getApplicationContext(), "data enabled", Toast.LENGTH_SHORT).show();
//            }
        }
        else
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage("Please connect to the internet")
                    .setCancelable(false).setPositiveButton("connect", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                }
            }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            }).show();
        }
    }

    private void AllowAccess(String username, String password) {

        final DatabaseReference rootRef;
        rootRef= FirebaseDatabase.getInstance().getReference();


        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("users").child(username).exists()){

                    Users userdata=snapshot.child("users").child(username).getValue(Users.class);

                    if(userdata.getUsername().equals(username)){
                        if(userdata.getPassword().equals(password)){
                            Toast.makeText(MainActivity.this,"Welcome Back "+userdata.getName(),Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                            Intent intent= new Intent(MainActivity.this,Home.class);
                            Prevalent.currentOnlineUser=userdata;
                            startActivity(intent);
                        }else{
                            Toast.makeText(MainActivity.this,"Password Is Incorrect!",Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                    }

                }else{
                    Toast.makeText(MainActivity.this,"Account Does Not Exist",Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Toast.makeText(MainActivity.this,"Please Register A New Account",Toast.LENGTH_SHORT).show();
                    Intent intent= new Intent(MainActivity.this,MainActivity.class);
                    startActivity(intent);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}