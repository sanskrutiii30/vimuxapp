package com.example.vimux;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gauravk.audiovisualizer.visualizer.BarVisualizer;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.IOException;
import java.util.List;

public class audioPage extends AppCompatActivity {

    Button btnPlayTry,btnForwardTry,btnRewindTry;
    TextView songNameTry,played_durationTry,total_song_durationTry;
    SeekBar seekMusicTry;
    ProgressBar progg;
    MediaPlayer mediaPlayer;
    Thread updateSeekbarTry;
    private String audioUrl="",getATitle="";
    BarVisualizer visualizer;

//    String audioUrl = "https://firebasestorage.googleapis.com/v0/b/vimux-c03e0.appspot.com/o/audios%2Fsmm%2F1644415231847.mp3?alt=media&token=c4276fc6-a30a-443f-ab3e-b16af3b0cc61";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.audio_page);



        btnPlayTry = findViewById(R.id.play_btn_try);
        btnForwardTry = findViewById(R.id.btn_fast_forward_try);
        btnRewindTry = findViewById(R.id.btn_rewind_try);
        progg = findViewById(R.id.prog_audiopg);

        visualizer = findViewById(R.id.blast);

        songNameTry = findViewById(R.id.song_name_try);
        played_durationTry = findViewById(R.id.player_time_current_try);
        total_song_durationTry = findViewById(R.id.player_time_total_try);



        seekMusicTry = findViewById(R.id.seekbar_try);

        audioUrl = getIntent().getStringExtra("audioUrl");
        getATitle = getIntent().getStringExtra("audioTitle");

        songNameTry.setText(getATitle);

        btnPlayTry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer.isPlaying())
                {
                    btnPlayTry.setBackgroundResource(R.drawable.ic_play);
                    mediaPlayer.pause();
                }
                else {
                    btnPlayTry.setBackgroundResource(R.drawable.ic_pause);
                    mediaPlayer.start();
                }
            }
        });

        Dexter.withContext(getApplicationContext()).withPermissions(Manifest.permission.RECORD_AUDIO).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                if (multiplePermissionsReport.areAllPermissionsGranted())
                {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            playAudio();
                        }
                    },500);
                }
                else
                {
                    showSettingsDialog();
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();




    }

    private void playAudio() {

        mediaPlayer = new MediaPlayer();

        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(audioUrl);
            mediaPlayer.prepare();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    progg.setVisibility(View.GONE);
                    btnPlayTry.setVisibility(View.VISIBLE);
                    btnPlayTry.setBackgroundResource(R.drawable.ic_pause);
                    mediaPlayer.start();
                }
            });
        } catch (IOException e) {
            Toast.makeText(this, "Error found: " + e, Toast.LENGTH_SHORT).show();
        }

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                btnPlayTry.setBackgroundResource(R.drawable.ic_play);
            }
        });

        btnForwardTry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()+10000);
            }
        });

        btnRewindTry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()-10000);
            }
        });

        updateSeekbarTry = new Thread()
        {
            @Override
            public void run() {
                int totalDuration = mediaPlayer.getDuration();
                int currentPosition = 0;

                while (currentPosition<totalDuration)
                {
                    try {

                        sleep(500);
                        currentPosition = mediaPlayer.getCurrentPosition();
                        seekMusicTry.setProgress(currentPosition);

                    }
                    catch (InterruptedException | IllegalStateException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        };

        seekMusicTry.setMax(mediaPlayer.getDuration());
        updateSeekbarTry.start();
        seekMusicTry.getProgressDrawable().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.MULTIPLY);
        seekMusicTry.getThumb().setColorFilter(getResources().getColor(R.color.white),PorterDuff.Mode.SRC_IN);

        seekMusicTry.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
        total_song_durationTry.setText(endTime);

        final Handler handler = new Handler();
        final int delay = 1000;

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String currentTime = createTime(mediaPlayer.getCurrentPosition());
                played_durationTry.setText(currentTime);
                handler.postDelayed(this,delay);
            }
        },delay);


        int audioSesId = mediaPlayer.getAudioSessionId();
        if (audioSesId != -1){
            visualizer.setAudioSessionId(audioSesId);
        }

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mediaPlayer!=null)
        {
            mediaPlayer.stop();
        }
    }

    private void showSettingsDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(audioPage.this);

        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivityForResult(intent, 101);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                onBackPressed();
            }
        });
        builder.show();
    }
}