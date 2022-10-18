package com.example.vimux;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.vimux.Model.Content;
import com.example.vimux.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class channelVideos extends Fragment {

    RecyclerView recyclerView;
    ArrayList<Content> list;
    CAdapter adapter;
    DatabaseReference dbRef;
    View vCv;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        vCv = inflater.inflate(R.layout.activity_channel_videos,container,false);

        recyclerView = vCv.findViewById(R.id.recycler_video);
        recyclerView.setHasFixedSize(true);

        dbRef = FirebaseDatabase.getInstance().getReference().child("videos");
        getAllVideos();

        return vCv;
    }

    private void getAllVideos() {

        String currentUser = Prevalent.currentOnlineUser.getUsername();
        list = new ArrayList<>();
        dbRef.orderByChild("publisher").startAt(currentUser).endAt(currentUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    list.clear();
                    for (DataSnapshot dataSnapshot:snapshot.getChildren())
                    {
                        Content model = dataSnapshot.getValue(Content.class);
                        list.add(model);
                    }

                    Collections.shuffle(list);

                    adapter = new CAdapter(getActivity(),list);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
                else
                {
//                    Toast.makeText(getActivity(), "No videos in your channel", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}