package com.example.vimux;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.vimux.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class profileView extends AppCompatActivity {

    Button editBtn;
    TextView name, userName, email, phone, subDet,expDet;
    CircleImageView image;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);

        image = findViewById(R.id.profile_view_image);
        name = findViewById(R.id.view_fullName);
        email = findViewById(R.id.view_email);
        userName = findViewById(R.id.view_uname);
        phone = findViewById(R.id.view_phone);
//        subDet = findViewById(R.id.view_subs);
//        expDet = findViewById(R.id.view_exp);
        progressBar = findViewById(R.id.profile_view_progress);

        editBtn = findViewById(R.id.view_edit_btn);
//
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(profileView.this,profileEdit.class));
            }
        });

        viewDetails(image,name, email, userName, phone);

    }

    private void viewDetails(CircleImageView userImage,TextView name, TextView email, TextView userName, TextView phone) {

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users").child(Prevalent.currentOnlineUser.getUsername());

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    if (snapshot.child("image").exists())
                    {
                        String user_image =snapshot.child("image").getValue().toString();
                        Glide.with(getApplicationContext()).load(user_image).placeholder(R.drawable.profile).listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                progressBar.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                progressBar.setVisibility(View.GONE);
                                return false;
                            }
                        }).into(userImage);
                    }
                    String user_name = snapshot.child("name").getValue().toString();
                    String user_email = snapshot.child("email").getValue().toString();
                    String user_uName = snapshot.child("username").getValue().toString();
                    String user_phone = snapshot.child("phone").getValue().toString();

                    name.setText("Name: " + user_name);
                    email.setText("Email: " + user_email);
                    userName.setText("Username: " + user_uName);
                    phone.setText("Phone: " + user_phone);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}