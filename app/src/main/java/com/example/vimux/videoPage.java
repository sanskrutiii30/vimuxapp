package com.example.vimux;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

public class videoPage extends AppCompatActivity {

    VideoView video;
    Uri videoUri;
    TextView videoTitle,videoDesc,videoPublisher;
    private ProgressBar buffer;
    private String getVName="",getChannel="",getVTitle="",getVDesc="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_page);

        video = findViewById(R.id.video_watch);
        buffer = findViewById(R.id.bufferProg);
        videoTitle = findViewById(R.id.video_page_title);
        videoDesc = findViewById(R.id.video_page_desc);
        videoPublisher = findViewById(R.id.video_page_channelNM);

        getVName = getIntent().getStringExtra("videoUrl");
        getChannel = getIntent().getStringExtra("videoPub");
        getVTitle = getIntent().getStringExtra("videoTitle");
        getVDesc = getIntent().getStringExtra("videoDesc");

        String path = getVName;
        videoUri = Uri.parse(path);
        video.setVideoURI(videoUri);
        video.requestFocus();
        buffer.setVisibility(View.VISIBLE);
        video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                    @Override
                    public boolean onInfo(MediaPlayer mediaPlayer, int i, int i1) {
                        buffer.setVisibility(View.INVISIBLE);
                        return false;
                    }
                });
            }
        });
        video.start();

        MediaController mediaController = new MediaController(this);
        video.setMediaController(mediaController);
        mediaController.setAnchorView(video);

        videoTitle.setText(getVTitle);
        videoDesc.setText(getVDesc);
        videoPublisher.setText(getChannel);

    }
}