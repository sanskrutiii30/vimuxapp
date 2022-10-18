package com.example.vimux;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.jean.jcplayer.view.JcPlayerView;

public class videoPlayer extends AppCompatActivity {

    ProgressBar mProg;
    VideoView videoView;
    ImageButton playBtn;
    SeekBar seekBar;
    TextView startTM,endTM;
    private Handler updateHandler = new Handler();
    private String getVName="",getChannel="",getVTitle="",getVDesc="";
    boolean gone = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        mProg = findViewById(R.id.vid_prog);
        videoView = findViewById(R.id.videoVieww);
        playBtn = findViewById(R.id.btn_ply_ps);
        seekBar = findViewById(R.id.vid_seek);
        startTM = findViewById(R.id.start_time);
        endTM = findViewById(R.id.end_time);

        startTM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
                {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }
                else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
                {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }

            }
        });

        getVName = getIntent().getStringExtra("videoUrl");
        getChannel = getIntent().getStringExtra("videoPub");
        getVTitle = getIntent().getStringExtra("videoTitle");
        getVDesc = getIntent().getStringExtra("videoDesc");

        videoView.setVideoPath(getVName);
        videoView.requestFocus();

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mProg.setVisibility(View.INVISIBLE);
                seekBar.setProgress(0);
                seekBar.setMax(videoView.getDuration());
                mediaPlayer.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
                videoView.start();

                updateHandler.postDelayed(updateVideoTime, 100);
                playBtn.setImageResource(R.drawable.ic_media_pause);

                String endTime = createTime(videoView.getDuration());
                endTM.setText(endTime);

            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                videoView.seekTo(seekBar.getProgress());
            }
        });

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                playBtn.setImageResource(R.drawable.ic_play);
            }
        });

        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (videoView.isPlaying())
                {
                    playBtn.setImageResource(R.drawable.ic_play);
                    videoView.pause();
                }
                else
                {
                    playBtn.setImageResource(R.drawable.ic_pause_white);
                    videoView.start();
                }
            }
        });

    }



    private Runnable updateVideoTime = new Runnable() {
        @Override
        public void run() {
            long currentPosition = videoView.getCurrentPosition();
            seekBar.setProgress((int) currentPosition);
            updateHandler.postDelayed(this, 100);

            String currentTime = createTime(videoView.getCurrentPosition());
            startTM.setText(currentTime);
        }
    };

    public String createTime(int duration){
        String time = "";
        int min = duration/1000/60;
        int sec = duration/1000%60;

        time = time+min+":";
        if (sec<10)
        {
            time+="0";
        }
        time+=sec;
        return time;
    }

    private void hideControls() {

        startTM.setVisibility(View.INVISIBLE);
        endTM.setVisibility(View.INVISIBLE);
        playBtn.setVisibility(View.INVISIBLE);
        seekBar.setVisibility(View.INVISIBLE);
    }

    private void showControls() {

        startTM.setVisibility(View.VISIBLE);
        endTM.setVisibility(View.VISIBLE);
        playBtn.setVisibility(View.VISIBLE);
        seekBar.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN){
            if (!gone){
                hideControls();
                gone = true;
            }
            else
            {
                showControls();
                gone = false;
            }
        }
        return true;
    }
}