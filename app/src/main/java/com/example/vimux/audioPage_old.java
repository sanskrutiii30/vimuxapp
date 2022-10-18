package com.example.vimux;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

public class audioPage_old extends AppCompatActivity {

    Button btnPlay,btnForward,btnRewind;
    TextView songName,played_duration,total_song_duration;
    SeekBar seekMusic;
    ImageView playImg;
    ProgressBar progressBar;
    private String getAUrl="",getATitle="";

    Thread updateSeekbar;
    static MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.audio_page_old);

        btnPlay = findViewById(R.id.play_btn);
        btnForward = findViewById(R.id.btn_fast_forward);
        btnRewind = findViewById(R.id.btn_rewind);
        songName = findViewById(R.id.song_name);
        played_duration = findViewById(R.id.player_time_current);
        total_song_duration = findViewById(R.id.player_time_total);
        seekMusic = findViewById(R.id.seekbar);
        playImg = findViewById(R.id.center_image);
        progressBar = findViewById(R.id.progress_audioPage);

        getATitle = getIntent().getStringExtra("audioTitle");
        getAUrl = getIntent().getStringExtra("audioUrl");

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        songName.setText(getATitle);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                playAudio();
            }
        },500);

    }
        private void playAudio() {

            Uri uri = Uri.parse(getAUrl);
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            mediaPlayer.start();
            progressBar.setVisibility(View.INVISIBLE);
            btnPlay.setVisibility(View.VISIBLE);

            updateSeekbar = new Thread() {
                @Override
                public void run() {
                    int totalDuration = mediaPlayer.getDuration();
                    int currentPosition = 0;

                    while (currentPosition < totalDuration) {
                        try {

                            sleep(500);
                            currentPosition = mediaPlayer.getCurrentPosition();
                            seekMusic.setProgress(currentPosition);

                        } catch (InterruptedException | IllegalStateException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };

            seekMusic.setMax(mediaPlayer.getDuration());
            updateSeekbar.start();
            seekMusic.getProgressDrawable().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.MULTIPLY);
            seekMusic.getThumb().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);

            seekMusic.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    mediaPlayer.seekTo(seekBar.getProgress());
                }
            });

            String endTime = createTime(mediaPlayer.getDuration());
            total_song_duration.setText(endTime);

            final Handler handler = new Handler();
            final int delay = 1000;

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    String currentTime = createTime(mediaPlayer.getCurrentPosition());
                    played_duration.setText(currentTime);
                    handler.postDelayed(this, delay);
                }
            }, delay);

            btnPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (mediaPlayer.isPlaying()) {
                        btnPlay.setBackgroundResource(R.drawable.ic_play);
                        mediaPlayer.pause();
                    } else {
                        btnPlay.setBackgroundResource(R.drawable.ic_pause);
                        mediaPlayer.start();
//                    startAnimation(playImg,360f);

//                    TranslateAnimation moveAnim = new TranslateAnimation(-25,25,-25,25);
//                    moveAnim.setInterpolator(new AccelerateInterpolator());
//                    moveAnim.setDuration(600);
//                    moveAnim.setFillEnabled(true);
//                    moveAnim.setFillAfter(true);
//                    moveAnim.setRepeatMode(Animation.REVERSE);
//                    moveAnim.setRepeatCount(100);
//                    playImg.startAnimation(moveAnim);

                    }
                }
            });

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    btnPlay.setBackgroundResource(R.drawable.ic_play);
                }
            });

            btnForward.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 10000);
                }
            });

            btnRewind.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - 10000);
                }
            });
        }


    public void startAnimation(View view,Float degree){
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(playImg,"rotation",0f,degree);
        objectAnimator.setDuration(50000);
        objectAnimator.setRepeatCount(2);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(objectAnimator);
        animatorSet.start();
    }

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
}