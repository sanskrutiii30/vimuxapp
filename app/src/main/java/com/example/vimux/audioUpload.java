package com.example.vimux;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jean.jcplayer.model.JcAudio;
import com.example.jean.jcplayer.view.JcPlayerView;
import com.example.vimux.Prevalent.Prevalent;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class audioUpload extends AppCompatActivity {

    DatabaseReference audioRef;
    StorageReference storageRef;
    EditText title,description;
    Button uploadBtn;

    Uri audioUri;

    ProgressBar progressBar;
    TextView progTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.audio_upload);

        audioRef = FirebaseDatabase.getInstance().getReference().child("audios");
        storageRef = FirebaseStorage.getInstance().getReference().child("audios");

        title = findViewById(R.id.audio_upload_title);
        description = findViewById(R.id.audio_upload_desc);
        uploadBtn = findViewById(R.id.audio_upload_btn);
        progressBar = findViewById(R.id.audio_upload_progBar);
        progTxt = findViewById(R.id.audio_upload_progTxt);

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String audioTitle = title.getText().toString();
                String audioDesc = description.getText().toString();

                if (audioTitle.isEmpty() || audioDesc.isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "Enter all details", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    uploadAudio(audioTitle,audioDesc);
                }
            }
        });
        Intent intent = getIntent();
        if (intent != null)
        {
            audioUri = intent.getData();
        }
    }


    private void uploadAudio(String audioTitle, String audioDesc) {

        uploadBtn.setEnabled(false);

        final  StorageReference sRef = storageRef.child(Prevalent.currentOnlineUser.getUsername())
                .child(System.currentTimeMillis()+"."+getFileExtension(audioUri));
        sRef.putFile(audioUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                sRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(@NonNull Uri uri) {
                        String audioUrl = uri.toString();
                        saveToFirebase(audioTitle,audioDesc,audioUrl);
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progress = 100.0*snapshot.getBytesTransferred()/snapshot.getTotalByteCount();
                progressBar.setProgress((int)progress);
                progTxt.setText("Uploading "+(int)progress+"%");
            }
        });
    }

    private void saveToFirebase(String audioTitle, String audioDesc, String audioUrl) {

        String currentDate = DateFormat.getDateInstance().format(new Date());
        String audioId = audioRef.push().getKey();

        HashMap<String,Object> vidMap = new HashMap<>();
        vidMap.put("audioId",audioId);
        vidMap.put("title",audioTitle);
        vidMap.put("description",audioDesc);
        vidMap.put("audio_url",audioUrl);
        vidMap.put("date",currentDate);
        vidMap.put("listened",0);
        vidMap.put("publisher",Prevalent.currentOnlineUser.getUsername());

        audioRef.child(audioId).setValue(vidMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    Toast.makeText(getApplicationContext(), "Audio Uploaded", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(audioUpload.this,Home.class)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                    finish();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Error uploading " +task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

}