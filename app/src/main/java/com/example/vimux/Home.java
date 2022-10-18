package com.example.vimux;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.example.vimux.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.paperdb.Paper;

public class Home extends navigation {

    DatabaseReference dbRef;
    CardView gotoVideos,gotoAudios,allVideos,allAudios,profile,feedback;
    String checker="";
    TextView username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = LayoutInflater.from(this);
        View v =inflater.inflate(R.layout.activity_home,null,false);
        drawerLayout.addView(v,0);

        dbRef = FirebaseDatabase.getInstance().getReference();
        username = findViewById(R.id.home_username);
        gotoVideos = v.findViewById(R.id.home_gotoVideos);
        gotoAudios = v.findViewById(R.id.home_gotoAudios);
        allVideos = v.findViewById(R.id.home_all_videos);
        allAudios = v.findViewById(R.id.home_all_audios);
        profile = v.findViewById(R.id.home_profile);
        feedback = v.findViewById(R.id.home_feedback);

        allVideos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),all_videos.class));
            }
        });

        allAudios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),all_audios.class));
            }
        });

        gotoVideos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checker = "videos";
                checkUserChannel();
            }
        });

        gotoAudios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checker = "audios";
                checkUserChannel();
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),profile.class));
            }
        });

        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),feedBack.class));
            }
        });
        username.setText(Prevalent.currentOnlineUser.getName());
        checkDate();
    }

    private void checkUserChannel() {

        dbRef.child("channels").child(Prevalent.currentOnlineUser.getUsername()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    Intent intent =new Intent(getApplicationContext(),channelDash.class);
                    if (checker.equals("videos"))
                    {
                        intent.putExtra("tabNum","1");
                    }
                    else
                    {
                        intent.putExtra("tabNum","2");
                    }
                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "You don't have a channel! Please create by visiting profile section.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void checkDate() {


        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
        try {
            String sysDate = sdf.format(calendar.getTime());

            Date sysDt = sdf.parse(sysDate);
            Date dateDB = sdf.parse(Prevalent.currentOnlineUser.getExpiry());

            int result = dateDB.compareTo(sysDt);

            if (result==0)
            {
                final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("users");
                Map<String,Object> userAmt = new HashMap<>();
                userAmt.put("Amount","");
                dbRef.child(Prevalent.currentOnlineUser.getUsername()).updateChildren(userAmt).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplicationContext(), "Subscription over! Please renew.", Toast.LENGTH_SHORT).show();
                        Paper.book().destroy();
                        startActivity(new Intent(Home.this,MainActivity.class));
                    }
                });

            }
            else
            {
                System.out.println("date_continue");
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

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