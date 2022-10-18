package com.example.vimux;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.vimux.Model.Channels;
import com.example.vimux.Model.Content;
import com.example.vimux.Model.ContentAudio;
import com.example.vimux.ViewHolder.AudioViewHolder;
import com.example.vimux.ViewHolder.VideoViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class channel_page extends navigation {

    private DatabaseReference videosRef,audiosRef;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    private CircleImageView channelImg;
    private TextView channelName,channelCreator,channelDesc;
    private String getCName="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater=LayoutInflater.from(this);
        View v= inflater.inflate(R.layout.activity_channel_page,null,false);
        drawerLayout.addView(v,1);

        videosRef =FirebaseDatabase.getInstance().getReference().child("videos");
        audiosRef =FirebaseDatabase.getInstance().getReference().child("audios");

        getCName = getIntent().getStringExtra("CNameView");

        channelImg = findViewById(R.id.channelPage_image);
        channelName = findViewById(R.id.channelPage_name);
        channelDesc = findViewById(R.id.channelPage_Desc);
        channelCreator = findViewById(R.id.channelPage_creator);

        recyclerView = findViewById(R.id.recycler_menu_cVideos);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        getCreatorDetails(getCName);
        getVideos();
//        getAudios();
    }



    private void getCreatorDetails(String getCName) {

        DatabaseReference channelRef = FirebaseDatabase.getInstance().getReference().child("channels");
        channelRef.child(getCName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    Channels channel = snapshot.getValue(Channels.class);
                    channelName.setText("Channel: " +channel.getName());
                    channelCreator.setText("Creator: " + channel.getCreator());
                    channelDesc.setText("Description: " + channel.getDescription());
                    Picasso.get().load(channel.getLogo()).placeholder(R.drawable.profile).into(channelImg);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "No Channels", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getVideos() {

        FirebaseRecyclerOptions<Content> options = new FirebaseRecyclerOptions.Builder<Content>()
                .setQuery(videosRef.orderByChild("publisher").startAt(getCName).endAt(getCName),Content.class).build();
        FirebaseRecyclerAdapter<Content, VideoViewHolder> adapter = new FirebaseRecyclerAdapter<Content, VideoViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull VideoViewHolder holder, int position, @NonNull Content model) {
                holder.videoName.setText("name: " + model.getTitle());
                holder.publisherName.setText("name: " + model.getPublisher());
                holder.views.setText("views: " + String.valueOf(model.getViews()));
                Glide.with(getApplicationContext()).asBitmap().load(model.getVideo_url()).into(holder.videoImg);
                holder.date.setText("on: "+ model.getDate());

                setLogo(holder.userChannelImg,model.getPublisher());

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent =new Intent(channel_page.this,videoExo.class);
                        intent.putExtra("videoUrl",model.getVideo_url());
                        intent.putExtra("videoDesc",model.getDescription());
                        intent.putExtra("videoPub",model.getPublisher());
                        intent.putExtra("videoTitle",model.getTitle());
                        intent.putExtra("videoid",model.getVideoId());
                        startActivity(intent);

                        String videoid = model.getVideoId();
                        long videoviews = model.getViews();
                        updateViews(videoid,videoviews);
                    }
                });

            }

            @NonNull
            @Override
            public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View v =LayoutInflater.from(parent.getContext()).inflate(R.layout.all_videos_layout,parent,false);
                VideoViewHolder holder = new VideoViewHolder(v);
                return holder;

            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void setLogo(CircleImageView logo, String user) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("channels");

        reference.child(user).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    String cLogo = snapshot.child("logo").getValue().toString();
                    Picasso.get().load(cLogo).placeholder(R.drawable.profile).into(logo);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error! "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void getAudios() {

        FirebaseRecyclerOptions<ContentAudio> options = new FirebaseRecyclerOptions.Builder<ContentAudio>()
                .setQuery(audiosRef.orderByChild("publisher").startAt(getCName).endAt(getCName),ContentAudio.class).build();
        FirebaseRecyclerAdapter<ContentAudio, AudioViewHolder> audioAdapter = new FirebaseRecyclerAdapter<ContentAudio, AudioViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull AudioViewHolder holder, int position, @NonNull ContentAudio model) {
                holder.audio_name.setText(model.getTitle());
                holder.audio_publisher.setText(model.getPublisher());
                holder.audio_date.setText(model.getDate());
                Glide.with(getApplicationContext()).asBitmap().load(model.getAudio_url()).into(holder.audio_img);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getApplicationContext(), "clicked audio: " +model.getTitle(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @NonNull
            @Override
            public AudioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v =LayoutInflater.from(parent.getContext()).inflate(R.layout.all_audios_layout,parent,false);
                AudioViewHolder audioHolder = new AudioViewHolder(v);
                return audioHolder;
            }
        };
        recyclerView.setAdapter(audioAdapter);
        audioAdapter.startListening();

    }

    private void updateViews(String videoid,long videoview) {

        HashMap<String, Object> viewMap = new HashMap<>();
        viewMap.put("views", videoview+1);
        videosRef.child(videoid).updateChildren(viewMap);
    }
}