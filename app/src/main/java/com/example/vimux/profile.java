package com.example.vimux;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.vimux.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class profile extends navigation {

    ConstraintLayout viewProfile,createChannel,subDetails,logout;
    DatabaseReference dbRef;
    TextView textView;
    CircleImageView mImage;
    ProgressBar prog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater=LayoutInflater.from(this);
        View vP= inflater.inflate(R.layout.activity_profile,null,false);
        drawerLayout.addView(vP,1);

        viewProfile = findViewById(R.id.your_profile);
        createChannel = findViewById(R.id.profile_create_channel);
        textView = findViewById(R.id.textView);
        mImage = findViewById(R.id.imageView);
        prog = findViewById(R.id.progress_prof);
        subDetails = findViewById(R.id.profile_subscription);
        logout = findViewById(R.id.profile_logout);
        dbRef = FirebaseDatabase.getInstance().getReference();

        Toolbar toolbar = findViewById(R.id.tool_profile);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Your Profile");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        textView.setText(Prevalent.currentOnlineUser.getName());
        Glide.with(getApplicationContext()).load(Prevalent.currentOnlineUser.getImage()).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                prog.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                prog.setVisibility(View.GONE);
                return false;
            }
        }).into(mImage);


        subDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog subsDialog = new Dialog(profile.this);
                subsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                subsDialog.setContentView(R.layout.subscription_details);
                subsDialog.setCancelable(true);
                subsDialog.setCanceledOnTouchOutside(true);

                TextView total = subsDialog.findViewById(R.id.sub_details_total);
                TextView start_dt = subsDialog.findViewById(R.id.sub_details_startDT);
                TextView end_dt = subsDialog.findViewById(R.id.sub_details_endDT);

                start_dt.setText("Started from: " + Prevalent.currentOnlineUser.getDate());
                end_dt.setText("Will end on: " + Prevalent.currentOnlineUser.getExpiry());

                String mAmt = Prevalent.currentOnlineUser.getAmount();

                if (mAmt.equals("49"))
                {
                    total.setText("1 month subscription active");
                }
                if (mAmt.equals("129"))
                {
                    total.setText("3 month subscription active");
                }
                if (mAmt.equals("199"))
                {
                    total.setText("6 month subscription active");
                }
                if (mAmt.equals("299"))
                {
                    total.setText("1 Year subscription active");
                }
                subsDialog.show();
            }
        });

        viewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),profileView.class));
            }
        });

        createChannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkUserChannel();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(profile.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Logout")
                        .setMessage("Are you sure you want to Logout?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Paper.book().destroy();
                                Intent intent= new Intent(profile.this,MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                Toast.makeText(profile.this, "Successfully Logged Out !", Toast.LENGTH_SHORT).show();
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.profile_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.changePas) {
            startActivity(new Intent(getApplicationContext(), reset_password.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(profile.this,Home.class));
    }

    private void checkUserChannel() {
        dbRef.child("channels").child(Prevalent.currentOnlineUser.getUsername()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    Intent intent =new Intent(getApplicationContext(),channelDash.class);
                    intent.putExtra("tabNum","0");
                    startActivity(intent);
                }
                else
                {
                    showCreateDialog();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void showCreateDialog() {
        Dialog dialog = new Dialog(profile.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.channel_dialog);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        EditText channel_name = dialog.findViewById(R.id.input_channel_name);
        EditText channel_desc = dialog.findViewById(R.id.input_channel_desc);
        TextView createTxt = dialog.findViewById(R.id.txt_create);

        createTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = channel_name.getText().toString();
                String description = channel_desc.getText().toString();

                if (name.isEmpty() || description.isEmpty())
                {
                    Toast.makeText(profile.this, "Please enter channel details", Toast.LENGTH_SHORT).show();
                }
                else{
                    createChannel(name,description,dialog);
                }
            }
        });
        dialog.show();

    }

    private void createChannel(String name, String description, Dialog dialog) {
        ProgressDialog progressDialog = new ProgressDialog(profile.this);
        progressDialog.setTitle("New channel");
        progressDialog.setMessage("Creating...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        String date = DateFormat.getDateInstance().format(new Date());
        String def_img = "https://firebasestorage.googleapis.com/v0/b/vimux-c03e0.appspot.com/o/vimux_logo.jpg?alt=media&token=b90fcd32-de11-496f-b38f-ada100798b3b";

        HashMap<String,Object> channelDet = new HashMap<>();

        channelDet.put("name",name);
        channelDet.put("description",description);
        channelDet.put("joined",date);
        channelDet.put("logo",def_img);
        channelDet.put("creator", Prevalent.currentOnlineUser.getUsername());

        dbRef.child("channels").child(Prevalent.currentOnlineUser.getUsername()).setValue(channelDet).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    progressDialog.dismiss();
                    dialog.dismiss();
                    Toast.makeText(profile.this, name+" channel created", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    progressDialog.dismiss();
                    dialog.dismiss();
                    Toast.makeText(profile.this, "Error creating channel" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}