package com.example.vimux;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.vimux.Prevalent.Prevalent;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class editChannel extends AppCompatActivity {

    EditText title,desc;
    Button updateBtn;
    CircleImageView logo;
    Uri imageUri;
    DatabaseReference dbRef;
    StorageReference storeRef;
    UploadTask uploadTask;
    ProgressBar progressBar;
    String myUri="";
    private String checker="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_channel);

        title = findViewById(R.id.channelTitle);
        desc = findViewById(R.id.channelDescription);
        logo = findViewById(R.id.channelLogo);
        updateBtn = findViewById(R.id.updateChannelBtn);
        progressBar = findViewById(R.id.edit_channel_progress);

        dbRef = FirebaseDatabase.getInstance().getReference();
        storeRef = FirebaseStorage.getInstance().getReference("channel_images");

        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checker="clicked";
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,1);
            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checker.equals("clicked")){
                    userInfoSaved();
                }
                else
                {
                    updateOnly();
                }
            }
        });

        retrieveChannelDetails(title,desc,logo);
    }

    private void updateOnly() {

        if(TextUtils.isEmpty(title.getText().toString()))
        {
            Toast.makeText(this, "Please enter channel name", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(desc.getText().toString()))
        {
            Toast.makeText(this, "Please enter channel description", Toast.LENGTH_SHORT).show();
        }
        else
        {
            HashMap<String,Object> userMap=new HashMap<>();

            userMap.put("name",title.getText().toString());
            userMap.put("description",desc.getText().toString());

            dbRef.child("channels").child(Prevalent.currentOnlineUser.getUsername()).updateChildren(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful())
                    {
                        startActivity(new Intent(editChannel.this,profile.class));
                        Toast.makeText(getApplicationContext(), "Details updated successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            });
        }

    }

    private void userInfoSaved() {

        if (TextUtils.isEmpty(title.getText().toString()))
        {
            Toast.makeText(this, "Please enter channel name", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(desc.getText().toString()))
        {
            Toast.makeText(this, "Please enter channel description", Toast.LENGTH_SHORT).show();
        }
        else if (checker.equals("clicked"))
        {
            uploadImage();
        }
    }

    private void retrieveChannelDetails(EditText title, EditText desc, CircleImageView logo) {

        dbRef.child("channels").child(Prevalent.currentOnlineUser.getUsername()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    if (snapshot.hasChild("logo"))
                    {
                        String image = snapshot.child("logo").getValue().toString();

                        Glide.with(getApplicationContext()).load(image).listener(new RequestListener<Drawable>() {
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
                        }).into(logo);
//                        Picasso.get().load(image).into(logo);
                    }
                    String name = snapshot.child("name").getValue().toString();
                    String description = snapshot.child("description").getValue().toString();

                    title.setText(name);
                    desc.setText(description);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data.getData() != null)
        {
            imageUri = data.getData();
            logo.setImageURI(imageUri);
        }
    }

    private void uploadImage() {
        final ProgressDialog progressDialog= new ProgressDialog(this);
        progressDialog.setTitle("Uploading image");
        progressDialog.setMessage("Please wait while we are updating your information");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if(imageUri!=null)
        {
            final StorageReference fileRef=storeRef.child(Prevalent.currentOnlineUser.getUsername()+".jpg");
            uploadTask=fileRef.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isSuccessful())
                    {
                        throw task.getException();
                    }
                    return fileRef.getDownloadUrl();

                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful())
                    {
                        Uri downloadUrl=task.getResult();
                        myUri=downloadUrl.toString();

                        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("channels");

                        HashMap<String,Object> userMap=new HashMap<>();

                        userMap.put("logo",myUri);
                        ref.child(Prevalent.currentOnlineUser.getUsername()).updateChildren(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()) {
                                    progressDialog.dismiss();
                                    Toast.makeText(editChannel.this, "Image uploaded", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }
                        });

                    }
                    else
                    {
                        progressDialog.dismiss();
                        Toast.makeText(editChannel.this, "Error !", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else
        {
            Toast.makeText(this, "Image Not Selected", Toast.LENGTH_SHORT).show();
        }
    }
}