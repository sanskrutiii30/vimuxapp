package com.example.vimux;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.vimux.Model.ContentAudio;
import com.example.vimux.ViewHolder.AudioViewHolder;
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

public class all_audios extends navigation {

    DatabaseReference audiosRef;
    RecyclerView recyclerAudio;
    RecyclerView.LayoutManager layoutManager;
    TextView txtSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater=LayoutInflater.from(this);
        View v= inflater.inflate(R.layout.activity_all_audios,null,false);
        drawerLayout.addView(v,1);

        audiosRef = FirebaseDatabase.getInstance().getReference().child("audios");

        recyclerAudio = findViewById(R.id.recycler_menu_audios);
        recyclerAudio.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerAudio.setLayoutManager(layoutManager);
        txtSearch = findViewById(R.id.inputSearchAudio);

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

        Query query = audiosRef.orderByChild("title").startAt(search).endAt(search + "\uf8ff");

        FirebaseRecyclerOptions<ContentAudio> options = new FirebaseRecyclerOptions.Builder<ContentAudio>()
                .setQuery(query,ContentAudio.class).build();
        FirebaseRecyclerAdapter<ContentAudio, AudioViewHolder> adapter = new FirebaseRecyclerAdapter<ContentAudio, AudioViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull AudioViewHolder holder, int position, @NonNull ContentAudio model) {

                holder.audio_name.setText("Name: " + model.getTitle());
                holder.audio_publisher.setText("Publisher: " + model.getPublisher());
                holder.audio_listened.setText("Views: " + String.valueOf(model.getListened()));
                holder.audio_date.setText("on: "+ model.getDate());
                Glide.with(getApplicationContext()).asBitmap().load(R.drawable.audd).into(holder.audio_img);

                setLogo(model.getPublisher(),holder.userChannelImage);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(all_audios.this, audioPage.class);
                        intent.putExtra("audioTitle",model.getTitle());
                        intent.putExtra("audioUrl",model.getAudio_url());
                        startActivity(intent);
                        String audId = model.getAudioId();
                        long audLis = model.getListened();
                        updateViews(audId,audLis);
                    }
                });
            }

            @NonNull
            @Override
            public AudioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_audios_layout,parent,false);
                AudioViewHolder audioHolder = new AudioViewHolder(v);
                return audioHolder;
            }
        };
        adapter.startListening();
        recyclerAudio.setAdapter(adapter);
        }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<ContentAudio> options =new FirebaseRecyclerOptions.Builder<ContentAudio>().setQuery(audiosRef,ContentAudio.class).build();

        FirebaseRecyclerAdapter<ContentAudio, AudioViewHolder> adapter = new FirebaseRecyclerAdapter<ContentAudio, AudioViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull AudioViewHolder holder, int position, @NonNull ContentAudio model) {

                holder.audio_name.setText("Name: " + model.getTitle());
                holder.audio_publisher.setText("Publisher: " + model.getPublisher());
                holder.audio_listened.setText("Views: " + String.valueOf(model.getListened()));
                holder.audio_date.setText("on: "+ model.getDate());
                Glide.with(getApplicationContext()).asBitmap().load(R.drawable.audd).into(holder.audio_img);

                setLogo(model.getPublisher(),holder.userChannelImage);


                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(all_audios.this, audioPage.class);
                        intent.putExtra("audioTitle",model.getTitle());
                        intent.putExtra("audioUrl",model.getAudio_url());
                        startActivity(intent);
                        String audId = model.getAudioId();
                        long audLis = model.getListened();
                        updateViews(audId,audLis);
                    }
                });
            }

            @NonNull
            @Override
            public AudioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_audios_layout,parent,false);
                AudioViewHolder audioHolder = new AudioViewHolder(v);
                return audioHolder;
            }
        };

        recyclerAudio.setAdapter(adapter);
        adapter.startListening();
    }


    private void setLogo(String user, CircleImageView logo) {

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

    private void updateViews(String audioId,long audioLis) {
        HashMap<String, Object> viewMap = new HashMap<>();
        viewMap.put("listened", audioLis+1);
        DatabaseReference audiosRef = FirebaseDatabase.getInstance().getReference().child("audios");
        audiosRef.child(audioId).updateChildren(viewMap);
    }

}