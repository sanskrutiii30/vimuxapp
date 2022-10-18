package com.example.vimux;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.vimux.Model.Content;
import com.example.vimux.Model.ContentAudio;
import com.example.vimux.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class channelAudios extends Fragment {

    RecyclerView recyclerView;
    ArrayList<ContentAudio> list;
    CAdapterAudio adapterAudio;
    DatabaseReference dbRef;
    View vCa;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        vCa = inflater.inflate(R.layout.activity_channel_audios,container,false);

        recyclerView = vCa.findViewById(R.id.recycler_audio);
        recyclerView.setHasFixedSize(true);

        dbRef = FirebaseDatabase.getInstance().getReference().child("audios");

        getAllAudios();

        return vCa;
    }

    private void getAllAudios() {

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
                        ContentAudio model = dataSnapshot.getValue(ContentAudio.class);
                        list.add(model);
                    }

                    Collections.shuffle(list);

                    adapterAudio = new CAdapterAudio(getActivity(),list);
                    recyclerView.setAdapter(adapterAudio);
                    adapterAudio.notifyDataSetChanged();
                }
                else
                {
//                    Toast.makeText(getActivity(), "No audios in your channel", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}