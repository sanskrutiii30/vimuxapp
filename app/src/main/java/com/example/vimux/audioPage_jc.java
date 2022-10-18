package com.example.vimux;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.example.jean.jcplayer.model.JcAudio;
import com.example.jean.jcplayer.view.JcPlayerView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class audioPage_jc extends AppCompatActivity {

    DatabaseReference dbRef;
    JcPlayerView jcPlayerView;
    ImageView click;
    private String getAName="",getATitle="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.audio_page_jc);

        dbRef = FirebaseDatabase.getInstance().getReference();
        click = findViewById(R.id.clickSong);
        jcPlayerView = findViewById(R.id.jcPlayerView);

        getATitle = getIntent().getStringExtra("audioTitle");
        getAName = getIntent().getStringExtra("audioUrl");

        ArrayList<JcAudio> jcAudios = new ArrayList<>();
        jcAudios.add(JcAudio.createFromURL(getATitle,getAName));
        jcPlayerView.initPlaylist(jcAudios,null);
        jcPlayerView.createNotification();

    }
}