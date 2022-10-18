package com.example.vimux;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vimux.Prevalent.Prevalent;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class channelDash extends navigation {

    ViewPager viewPager;
    TabLayout tabLayout;
    TabItem tabHome,tabVideos,tabAudios;
    TextView channel_name_display;
    CircleImageView channelImage;
    String getTab = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = LayoutInflater.from(this);
        View vDv =inflater.inflate(R.layout.activity_channel_dash,null,false);
        drawerLayout.addView(vDv,1);

        viewPager = findViewById(R.id.vPager);
        tabLayout = findViewById(R.id.tabLayout);

        tabHome = findViewById(R.id.itemHome);
        tabVideos = findViewById(R.id.itemVideos);
        tabAudios = findViewById(R.id.itemAudios);

        channel_name_display = findViewById(R.id.user_channel_display);
        channelImage = findViewById(R.id.channel_dash_image);

        Toolbar toolbar = findViewById(R.id.tool_dash);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(channelDash.this,Home.class));
            }
        });

        getTab = getIntent().getStringExtra("tabNum");

        tabLayout.setupWithViewPager(viewPager);
        VPAdapter vpAdapter = new VPAdapter(getSupportFragmentManager(),FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        vpAdapter.addFragment(new channelHome(),"Home");
        vpAdapter.addFragment(new channelVideos(),"Videos");
        vpAdapter.addFragment(new channelAudios(),"Audios");
        viewPager.setAdapter(vpAdapter);
        viewPager.setCurrentItem(Integer.parseInt(getTab));

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("channels");
        dbRef.child(Prevalent.currentOnlineUser.getUsername()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if (snapshot.exists()) {
                        String name = snapshot.child("name").getValue().toString();
                        String logo = snapshot.child("logo").getValue().toString();
                        channel_name_display.setText(name);
                        Picasso.get().load(logo).into(channelImage);
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error! "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}