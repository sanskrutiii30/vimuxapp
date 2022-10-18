package com.example.vimux;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vimux.Model.Comment;
import com.example.vimux.Prevalent.Prevalent;
import com.example.vimux.ViewHolder.CommentHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

public class videoExo extends AppCompatActivity {

    DatabaseReference likesRef,comRef;
    boolean testClick = false;

    SimpleExoPlayer player;
    PlayerView playerView;
    ConcatenatingMediaSource concatenatingMediaSource;
    ImageView fullScreen,vidLike,vidComment,goBack;
    TextView vidTitle,vidLikeTxt;
    private String videoURL="",getVTitle="",videoID="";
    String currUser = Prevalent.currentOnlineUser.getUsername();
    String currUserNm = Prevalent.currentOnlineUser.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_exo);

        playerView = findViewById(R.id.exoplayer_view);
        fullScreen = findViewById(R.id.scaling);
        vidTitle = findViewById(R.id.video_title_exo);
        vidLike = findViewById(R.id.like_icon);
        vidLikeTxt = findViewById(R.id.like_count);
        vidComment = findViewById(R.id.comment);
        goBack = findViewById(R.id.video_back);

        likesRef = FirebaseDatabase.getInstance().getReference("likes");
        comRef = FirebaseDatabase.getInstance().getReference("comments");


        videoURL = getIntent().getStringExtra("videoUrl");
        videoID = getIntent().getStringExtra("videoid");
        getVTitle = getIntent().getStringExtra("videoTitle");

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        vidLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                testClick = true;
                likesRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (testClick){
                            if (snapshot.child(videoID).hasChild(currUser)){
                                likesRef.child(videoID).child(currUser).removeValue();
                            }
                            else{
                                likesRef.child(videoID).child(currUser).setValue(true);
                            }
                            testClick = false;
                        }

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

        vidComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog cmDialog = new Dialog(videoExo.this);
                cmDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                cmDialog.setContentView(R.layout.comments_layout);
                cmDialog.setCancelable(true);
                cmDialog.setCanceledOnTouchOutside(true);

                EditText user_comment = cmDialog.findViewById(R.id.comment_insert);
                TextView submit_btn = cmDialog.findViewById(R.id.comment_submit_btn);
                TextView view_cm_btn = cmDialog.findViewById(R.id.comment_view_btn);

                submit_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String comment = user_comment.getText().toString();
                        if (comment.isEmpty())
                        {
                            Toast.makeText(getApplicationContext(), "Please enter something to submit.", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            submitComment(comment,cmDialog);
                        }
                    }
                });

                view_cm_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(videoExo.this,commentsPage.class);
                        intent.putExtra("videoId",videoID);
                        intent.putExtra("videott",getVTitle);
                        startActivity(intent);
                    }
                });

                cmDialog.show();
            }
        });

        vidTitle.setText(getVTitle);

        player = new SimpleExoPlayer.Builder(this).build();
        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this,"app"));
        concatenatingMediaSource = new ConcatenatingMediaSource();
        MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(videoURL));
        concatenatingMediaSource.addMediaSource(mediaSource);

        playerView.setPlayer(player);
        playerView.setKeepScreenOn(true);
        player.prepare(concatenatingMediaSource);
        playerError();

        fullScreen.setOnClickListener(new View.OnClickListener() {
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

        getLikeStatus(currUser,videoID);

    }

    private void submitComment(String commented,Dialog dialog) {

        ProgressDialog progressDialog = new ProgressDialog(videoExo.this);
        progressDialog.setTitle("Comment");
        progressDialog.setMessage("Please wait while comment is submitted...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();


        String user_img = Prevalent.currentOnlineUser.getImage();
        String currentDate = DateFormat.getDateInstance().format(new Date());

        HashMap<String,Object> comMap = new HashMap<>();
        comMap.put("comment",commented);
        comMap.put("image",user_img);
        comMap.put("name",currUserNm);
        comMap.put("date",currentDate);

        comRef.child(videoID).child(currUser).updateChildren(comMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    dialog.dismiss();
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Comment submitted successfully.", Toast.LENGTH_SHORT).show();
                }
                else {
                    dialog.dismiss();
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Error submitting comment.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getLikeStatus(String curUser, String videId) {

        likesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(videId).hasChild(curUser)){
                    int likeCount = (int) snapshot.child(videId).getChildrenCount();
                    vidLikeTxt.setText(String.valueOf(likeCount));
                    vidLike.setImageResource(R.drawable.ic_fav_filled);
                }
                else
                {
                    int likeCount = (int) snapshot.child(videId).getChildrenCount();
                    vidLikeTxt.setText(String.valueOf(likeCount));
                    vidLike.setImageResource(R.drawable.ic_fav_border);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void playerError() {

        player.addListener(new Player.Listener() {
            @Override
            public void onPlayerError(PlaybackException error) {
                Toast.makeText(getApplicationContext(), "video error!" , Toast.LENGTH_SHORT).show();
            }
        });
        player.setPlayWhenReady(true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (player.isPlaying()){
            player.stop();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        player.setPlayWhenReady(false);
        player.getPlaybackState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        player.setPlayWhenReady(true);
        player.getPlaybackState();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        player.setPlayWhenReady(true);
        player.getPlaybackState();
    }
}