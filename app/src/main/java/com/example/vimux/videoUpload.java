package com.example.vimux;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.vimux.Prevalent.Prevalent;
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
import java.util.Date;
import java.util.HashMap;

public class videoUpload extends AppCompatActivity {

    VideoView videoView;
    Uri videoUri;
    MediaController mediaController;

    DatabaseReference dbRef;
    StorageReference storageReference;

    EditText inputVideoTitle,inputVideoDesc;
    LinearLayout progressLayout;
    ProgressBar progressBar;
    TextView progTxt,uploadTxt;

    private String vidType="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_upload);

        dbRef = FirebaseDatabase.getInstance().getReference().child("videos");
        storageReference = FirebaseStorage.getInstance().getReference().child("videos");

        videoView = findViewById(R.id.videoView);
        mediaController = new MediaController(videoUpload.this);

        inputVideoTitle = findViewById(R.id.videoTitle);
        inputVideoDesc = findViewById(R.id.videoDesc);
        progressLayout = findViewById(R.id.progLayout);
        progTxt = findViewById(R.id.prog_text);
        progressBar = findViewById(R.id.progressBar);
        uploadTxt = findViewById
                (R.id.layout_upload_txt);
        Spinner typeSpin = findViewById(R.id.typeSpin);
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(getApplicationContext(),
                R.layout.selected_item,getResources()
                .getStringArray(R.array.types));
        adapter.setDropDownViewResource(R.layout.drop_down_items);
        typeSpin.setAdapter(adapter);

        typeSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(!typeSpin.getItemAtPosition(i).toString().equals("Select video type"))
                {
                    vidType=typeSpin.getItemAtPosition(i).toString();
                }
                else
                {
                    vidType="tt";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        uploadTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = inputVideoTitle.getText().toString();
                String description = inputVideoDesc.getText().toString();

                if (title.isEmpty() || description.isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "Enter all details", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if (!vidType.equals("tt"))
                    {
                        uploadVideo(title, description);
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Please select video type", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        Intent intent = getIntent();

        if (intent != null)
        {
            videoUri = intent.getData();
            videoView.setVideoURI(videoUri);
            videoView.setMediaController(mediaController);
            videoView.start();
        }

    }

    private void uploadVideo(String title, String description) {

        uploadTxt.setEnabled(false);
        progressLayout.setVisibility(View.VISIBLE);

        final StorageReference sRef = storageReference.child(Prevalent.currentOnlineUser.getUsername())
                .child(System.currentTimeMillis()+"."+getFileExtension(videoUri));
        sRef.putFile(videoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                sRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(@NonNull Uri uri) {
                        String videoUrl = uri.toString();

                        saveToFirebase(title,description,videoUrl);
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

    private void saveToFirebase(String title, String description, String videoUrl) {

        String currentDate = DateFormat.getDateInstance().format(new Date());
        String videoId = dbRef.push().getKey();

        HashMap<String,Object> vidMap = new HashMap<>();
        vidMap.put("videoId",videoId);
        vidMap.put("title",title);
        vidMap.put("description",description);
        vidMap.put("video_url",videoUrl);
        vidMap.put("video_type",vidType);
        vidMap.put("date",currentDate);
        vidMap.put("views",0);
        vidMap.put("publisher",Prevalent.currentOnlineUser.getUsername());

        dbRef.child(videoId).setValue(vidMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    progressLayout.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "Video Uploaded", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(videoUpload.this,Home.class)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                    finish();
                }
                else
                {
                    progressLayout.setVisibility(View.GONE);
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