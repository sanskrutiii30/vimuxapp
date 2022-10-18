package com.example.vimux;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vimux.Prevalent.Prevalent;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class channelHome extends Fragment {

    CardView uploadVideo,uploadAudio,channelDetail;
    Uri videoUri,audioUri;
    View vCH;

    private static final int PICK_VIDEO = 111;
    private static final int PICK_AUDIO = 112;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        vCH = inflater.inflate(R.layout.activity_channel_home,container,false);

        uploadVideo = vCH.findViewById(R.id.channel_home_uploadVideo);
        uploadAudio = vCH.findViewById(R.id.channel_home_uploadAudio);
        channelDetail = vCH.findViewById(R.id.channel_home_channelDetails);

        uploadVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("video/*");
                startActivityForResult(Intent.createChooser(intent,"Select Video"),PICK_VIDEO);
            }
        });
        uploadAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("audio/*");
                startActivityForResult(Intent.createChooser(intent,"Select audio"),PICK_AUDIO);
            }
        });
        channelDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.channel_detail_layout);
                dialog.setCancelable(true);
                dialog.setCanceledOnTouchOutside(true);

                CircleImageView logo = dialog.findViewById(R.id.channel_detail_logo);
                TextView name = dialog.findViewById(R.id.channel_detail_name);
                TextView desc = dialog.findViewById(R.id.channel_detail_desc);
                TextView creator = dialog.findViewById(R.id.channel_detail_creator);
                TextView date = dialog.findViewById(R.id.channel_detail_date);

                DatabaseReference userChannelRef = FirebaseDatabase.getInstance().getReference().child("channels")
                        .child(Prevalent.currentOnlineUser.getUsername());

                userChannelRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists())
                        {
                            String channel_logo = snapshot.child("logo").getValue().toString();
                            String channel_name = snapshot.child("name").getValue().toString();
                            String channel_desc = snapshot.child("description").getValue().toString();
                            String channel_creator = snapshot.child("creator").getValue().toString();
                            String channel_date = snapshot.child("joined").getValue().toString();

                            name.setText("Name: "+channel_name);
                            desc.setText("Description: "+channel_desc);
                            creator.setText("Created By: "+channel_creator);
                            date.setText("Joined on: "+channel_date);
                            Picasso.get().load(channel_logo).into(logo);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getActivity(), "error! ", Toast.LENGTH_SHORT).show();
                    }
                });

                dialog.show();
            }
        });
        return vCH;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.chan_menu,menu);
            super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode==111 && resultCode == Activity.RESULT_OK && data!= null)
       {
           videoUri = data.getData();
           Intent intent = new Intent(getActivity(),videoUpload.class);
           intent.putExtra("type","video");
           intent.setData(videoUri);
           startActivity(intent);
       }
        if (requestCode==112 && resultCode == Activity.RESULT_OK && data!= null)
        {
            audioUri = data.getData();
            Intent intent = new Intent(getActivity(),audioUpload.class);
            intent.putExtra("type","audio");
            intent.setData(audioUri);
            startActivity(intent);
        }
    }
}