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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.vimux.Model.Channels;
import com.example.vimux.ViewHolder.ChannelViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class allChannels extends navigation {

    private DatabaseReference channelsRef = FirebaseDatabase.getInstance().getReference().child("channels");
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater=LayoutInflater.from(this);
        View v= inflater.inflate(R.layout.activity_all_channels,null,false);
        drawerLayout.addView(v,1);

        recyclerView = findViewById(R.id.recycler_menu_channel);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Channels> options = new FirebaseRecyclerOptions.Builder<Channels>().setQuery(channelsRef,Channels.class).build();
        FirebaseRecyclerAdapter<Channels, ChannelViewHolder> adapter = new FirebaseRecyclerAdapter<Channels, ChannelViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ChannelViewHolder holder, int position, @NonNull Channels model) {

                holder.channelName.setText("Name: " + model.getName());
                holder.channelUser.setText("Creator: "+ model.getCreator());
                Glide.with(getApplicationContext()).asBitmap().load(model.getLogo()).into(holder.channelImage);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent=new Intent(allChannels.this,channel_page.class);
                        intent.putExtra("CNameView",model.getCreator());
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public ChannelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.allchannels_layout,parent,false);
                ChannelViewHolder holder = new ChannelViewHolder(v);
                return holder;
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}