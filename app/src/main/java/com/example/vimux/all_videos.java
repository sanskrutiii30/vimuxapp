package com.example.vimux;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.vimux.Model.Content;
import com.example.vimux.Prevalent.Prevalent;
import com.example.vimux.ViewHolder.VideoViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class all_videos extends navigation {

    DatabaseReference videosRef,reference;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    TextView txtSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater=LayoutInflater.from(this);
        View v2= inflater.inflate(R.layout.activity_all_videos,null,false);
        drawerLayout.addView(v2,1);

        videosRef = FirebaseDatabase.getInstance().getReference().child("videos");

        recyclerView = findViewById(R.id.recycler_menu);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        txtSearch =findViewById(R.id.inputSearch);

        txtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (editable.toString()!=null)
                {
                    loadData(editable.toString());
                }
                else{
                    loadData("");
                }

            }
        });
    }

    private void loadData(String search) {

        Query query = videosRef.orderByChild("title").startAt(search).endAt(search + "\uf8ff");

        FirebaseRecyclerOptions<Content> options =new FirebaseRecyclerOptions.Builder<Content>()
                .setQuery(query,Content.class).build();
        FirebaseRecyclerAdapter<Content,VideoViewHolder> adapter = new FirebaseRecyclerAdapter<Content, VideoViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull VideoViewHolder holder, int position, @NonNull Content model) {

                holder.videoName.setText("Name: " + model.getTitle());
                holder.publisherName.setText("Publisher: " + model.getPublisher());
                holder.views.setText("Views: " + String.valueOf(model.getViews()));
                holder.date.setText("on: "+ model.getDate());
                Glide.with(getApplicationContext()).asBitmap().load(model.getVideo_url()).into(holder.videoImg);

                setLogo(holder.userChannelImg,model.getPublisher());


                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent =new Intent(all_videos.this,videoExo.class);
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
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_videos_layout,parent,false);
                VideoViewHolder holder = new VideoViewHolder(v);
                return holder;
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
        }

    private void updateViews(String videoid,long videoview) {

            HashMap<String, Object> viewMap = new HashMap<>();
            viewMap.put("views", videoview+1);
            videosRef.child(videoid).updateChildren(viewMap);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Content> options =new FirebaseRecyclerOptions.Builder<Content>().setQuery(videosRef,Content.class).build();
        FirebaseRecyclerAdapter<Content,VideoViewHolder> adapter = new FirebaseRecyclerAdapter<Content, VideoViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull VideoViewHolder holder, int position, @NonNull Content model) {
                holder.videoName.setText("Name: " + model.getTitle());
                holder.publisherName.setText("Publisher: " + model.getPublisher());
                holder.views.setText("Views: " + String.valueOf(model.getViews()));
                holder.date.setText("on: "+ model.getDate());
                Glide.with(getApplicationContext()).asBitmap().load(model.getVideo_url()).into(holder.videoImg);

                setLogo(holder.userChannelImg,model.getPublisher());



                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent =new Intent(all_videos.this,videoExo.class);
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
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_videos_layout,parent,false);
                VideoViewHolder holder = new VideoViewHolder(v);
                return holder;
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }



    private void setLogo(CircleImageView logo, String user) {

        reference = FirebaseDatabase.getInstance().getReference().child("channels");

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

}