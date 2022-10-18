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
import android.widget.TextView;
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
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class profileEdit extends AppCompatActivity {

    private CircleImageView profileImageView;
    private EditText fullNameEdit,userEmailEdit;
    private TextView changeButton;
    private Button saveButton,closeButton;
    private ProgressBar progressBarEdit;

    private Uri imageUri;
    private String myUri="";
    private StorageTask uploadTask;
    private StorageReference StoreProfPic;
    private String checker="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_edit);

        StoreProfPic = FirebaseStorage.getInstance().getReference().child("Profile Pictures");

        profileImageView=findViewById(R.id.imageCircle);
        fullNameEdit=findViewById(R.id.editName);
        userEmailEdit=findViewById(R.id.editEmail);
        changeButton= findViewById(R.id.txtChangeProf);
        saveButton=findViewById(R.id.btnUpdate);
        closeButton=findViewById(R.id.btnClose);
        progressBarEdit = findViewById(R.id.progress_edit_profile);

        userInfoDisplay(profileImageView, fullNameEdit, userEmailEdit);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checker.equals("clicked")){
                    userInfoSaved();
                }
                else
                {
                    updateOnly();
                }
            }
        });

        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checker="clicked";
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,1);
            }
        });

    }

    @Override
    public void onBackPressed() {
    }

    private void userInfoDisplay(CircleImageView profileImageView, EditText fullNameEdit, EditText userEmailEdit) {
        DatabaseReference usersRef= FirebaseDatabase.getInstance().getReference().child("users").child(Prevalent.currentOnlineUser.getUsername());
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    if(snapshot.child("image").exists()) {
                        String image = snapshot.child("image").getValue().toString();
                        Picasso.get().load(image).into(profileImageView, new Callback() {
                            @Override
                            public void onSuccess() {
                                progressBarEdit.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError(Exception e) {

                            }
                        });
                    }
                    String name = snapshot.child("name").getValue().toString();
                    String email = snapshot.child("email").getValue().toString();

                    fullNameEdit.setText(name);
                    userEmailEdit.setText(email);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void updateOnly() {

        DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("users");
        if(TextUtils.isEmpty(fullNameEdit.getText().toString()))
        {
            Toast.makeText(this, "Please Enter Name", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(userEmailEdit.getText().toString()))
        {
            Toast.makeText(this, "Please Enter Email", Toast.LENGTH_SHORT).show();
        }
        else
        {
            HashMap<String,Object> userMap=new HashMap<>();

            userMap.put("name",fullNameEdit.getText().toString());
            userMap.put("email",userEmailEdit.getText().toString());

            ref.child(Prevalent.currentOnlineUser.getUsername()).updateChildren(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()) {
                        startActivity(new Intent(profileEdit.this, profileView.class));
                        Toast.makeText(profileEdit.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1 && resultCode==RESULT_OK && data!=null)
        {
            imageUri=data.getData();
            profileImageView.setImageURI(imageUri);
        }
        else
        {
            Toast.makeText(this, "Error !", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(profileEdit.this,profileEdit.class));
            finish();
        }
    }

    private void userInfoSaved() {
        if(TextUtils.isEmpty(fullNameEdit.getText().toString()))
        {
            Toast.makeText(this, "Please Enter Name", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(userEmailEdit.getText().toString()))
        {
            Toast.makeText(this, "Please Enter Email", Toast.LENGTH_SHORT).show();
        }
        else if (checker.equals("clicked"))
        {
            uploadImage();
        }
    }

    private void uploadImage() {
        final ProgressDialog progressDialog= new ProgressDialog(this);
        progressDialog.setTitle("Update Profile");
        progressDialog.setMessage("Please wait while we are updating your information");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if(imageUri!=null)
        {
            final StorageReference fileRef=StoreProfPic.child(Prevalent.currentOnlineUser.getUsername()+".jpg");
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

                        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("users");

                        HashMap<String,Object> userMap=new HashMap<>();
                        userMap.put("name",fullNameEdit.getText().toString());
                        userMap.put("email",userEmailEdit.getText().toString());

                        userMap.put("image",myUri);
                        ref.child(Prevalent.currentOnlineUser.getUsername()).updateChildren(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()) {
                                    progressDialog.dismiss();
                                    startActivity(new Intent(profileEdit.this, profileView.class));
                                    Toast.makeText(profileEdit.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }
                        });

                    }
                    else
                    {
                        progressDialog.dismiss();
                        Toast.makeText(profileEdit.this, "Error !", Toast.LENGTH_SHORT).show();
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