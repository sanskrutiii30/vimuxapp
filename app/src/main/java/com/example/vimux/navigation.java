package com.example.vimux;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.vimux.Prevalent.Prevalent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class navigation extends AppCompatActivity {

    DrawerLayout drawerLayout;
    Toolbar toolbar;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;
    private CircleImageView navImage;
    private TextView navName,navUser;
    private static final int PER = 111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        drawerLayout = findViewById(R.id.drawer);
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.nav_view);

        View header = navigationView.getHeaderView(0);
        navName = header.findViewById(R.id.nav_name);
        navUser = header.findViewById(R.id.nav_user);
        navImage = header.findViewById(R.id.nav_image);
        Paper.init(this);

        setSupportActionBar(toolbar);

        toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,0,0);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId()==R.id.nav_home)
                {
                    startActivity(new Intent(getApplicationContext(),Home.class));
                }
                else if (item.getItemId()==R.id.nav_audio)
                {
                    startActivity(new Intent(getApplicationContext(),all_audios.class));
                }
                else if (item.getItemId()==R.id.nav_video)
                {
                    startActivity(new Intent(getApplicationContext(),all_videos.class));
                }
                else if (item.getItemId()==R.id.nav_channels)
                {
                    startActivity(new Intent(getApplicationContext(),allChannels.class));
                }
                else if (item.getItemId()==R.id.nav_profile)
                {
                    startActivity(new Intent(getApplicationContext(),profile.class));
                }
                else if (item.getItemId()==R.id.nav_logout)
                {

                    new AlertDialog.Builder(navigation.this)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Logout")
                            .setMessage("Are you sure you want to Logout?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Paper.book().destroy();
                                    Intent intent= new Intent(navigation.this,MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    Toast.makeText(navigation.this, "Successfully Logged Out !", Toast.LENGTH_SHORT).show();
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();
                }

                DrawerLayout drawerLayout = findViewById(R.id.drawer);
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
        userDisplay(navName,navUser,navImage);
    }

    private void userDisplay(TextView userfullname, TextView username, CircleImageView userImage) {

        DatabaseReference userRef =FirebaseDatabase.getInstance().getReference().child("users")
                .child(Prevalent.currentOnlineUser.getUsername());
        userRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("image").exists())
                {
                    String userImageDisplay =snapshot.child("image").getValue().toString();
                    Glide.with(getApplicationContext()).load(userImageDisplay).into(userImage);
                }
                String fullNameDisplay =snapshot.child("name").getValue().toString();
                String userNameDisplay =snapshot.child("username").getValue().toString();

                userfullname.setText(fullNameDisplay);
                username.setText(userNameDisplay);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.about:
                startActivity(new Intent(getApplicationContext(),aboutUs.class));
                return true;
            case R.id.contact:
                startActivity(new Intent(getApplicationContext(), contactUs.class));
                return true;
            case R.id.edit_channel:
                startActivity(new Intent(getApplicationContext(),editChannel.class));
                return true;

            case R.id.exit:

                new AlertDialog.Builder(this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Exit App")
                        .setMessage("Are you sure you want to close this app?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Intent.ACTION_MAIN);
                                intent.addCategory(Intent.CATEGORY_HOME);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                android.os.Process.killProcess(android.os.Process.myPid());
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}