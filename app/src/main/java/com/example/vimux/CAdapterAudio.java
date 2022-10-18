package com.example.vimux;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.vimux.Model.ContentAudio;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class CAdapterAudio extends RecyclerView.Adapter<CAdapterAudio.ViewHolder> {

    Context context;
    ArrayList<ContentAudio> list;

    DatabaseReference reference;


    public CAdapterAudio(Context context, ArrayList<ContentAudio> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_audio,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ContentAudio model = list.get(position);
        if (model != null)
        {
            Glide.with(context).asBitmap().load(R.drawable.audd).into(holder.image);
            holder.audioTitle.setText("audio name: " + model.getTitle());
            holder.listened.setText("listened: " + String.valueOf(model.getListened()));
            holder.date.setText("date: " + model.getDate());
            
//            setData(model.getPublisher(),holder.channel_logo, holder.channelName);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, audioPage.class);
                    intent.putExtra("audioTitle",model.getTitle());
                    intent.putExtra("audioUrl",model.getAudio_url());
                    context.startActivity(intent);
                    String audId = model.getAudioId();
                    long audLis = model.getListened();
                    updateViews(audId,audLis);
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    final Dialog dialog = new Dialog(context);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.long_click_dialog);
                    dialog.setCancelable(true);
                    dialog.setCanceledOnTouchOutside(true);

                    LinearLayout editItem = dialog.findViewById(R.id.long_click_edit_linear);
                    LinearLayout deleteItem = dialog.findViewById(R.id.long_click_delete_linear);
                    LinearLayout cancelItem = dialog.findViewById(R.id.long_click_cancel_linear);

                    editItem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            audioEditDialog(model.getTitle(),model.getDescription(),model.getAudioId());
                            dialog.dismiss();
                        }
                    });

                    deleteItem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            AlertDialog.Builder delDialog = new AlertDialog.Builder(context);
                            delDialog.setTitle("Delete audio?");
                            delDialog.setMessage("Do you really want to delete "+ model.getTitle()+" ?");
                            delDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("audios");
                                    dbRef.child(model.getAudioId()).removeValue();
                                    dialog.dismiss();
                                    Toast.makeText(context, "deleted: " + model.getTitle(), Toast.LENGTH_SHORT).show();
                                }
                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialog.cancel();
                                }
                            });
                            delDialog.show();
                        }
                    });

                    cancelItem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.cancel();
                        }
                    });

                    dialog.show();
                    return true;
                }
            });
        }
    }

    private void audioEditDialog(String title, String description, String audId) {

        Dialog editDialog = new Dialog(context);
        editDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        editDialog.setContentView(R.layout.item_edit_dialog);
        editDialog.setCancelable(true);
        editDialog.setCanceledOnTouchOutside(true);

        EditText audio_title = editDialog.findViewById(R.id.item_edit_title);
        EditText audio_desc = editDialog.findViewById(R.id.item_edit_desc);
        TextView submit_txt = editDialog.findViewById(R.id.item_edit_done);

        audio_title.setText(title);
        audio_desc.setText(description);

        submit_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = audio_title.getText().toString();
                String description = audio_desc.getText().toString();

                if (name.isEmpty() || description.isEmpty())
                {
                    Toast.makeText(context, "Please enter all details", Toast.LENGTH_SHORT).show();
                }
                else{
                    saveEditedToFirebase(name,description,audId);
                    editDialog.dismiss();
                    Toast.makeText(context, "Successfully modified", Toast.LENGTH_SHORT).show();
                }
            }
        });
        editDialog.show();


    }

    private void saveEditedToFirebase(String audio_title, String audio_desc, String audioId) {

        DatabaseReference audRef = FirebaseDatabase.getInstance().getReference("audios");
        HashMap<String,Object> map = new HashMap<>();
        map.put("title",audio_title);
        map.put("description",audio_desc);
        audRef.child(audioId).updateChildren(map);
    }

    private void setData(String user,CircleImageView logo, TextView channel_name) {

        reference = FirebaseDatabase.getInstance().getReference().child("channels");

        reference.child(user).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    String cName = snapshot.child("name").getValue().toString();
                    String cLogo = snapshot.child("logo").getValue().toString();
                    channel_name.setText("channel: " + cName);
                    Picasso.get().load(cLogo).placeholder(R.drawable.profile).into(logo);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Error! "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void updateViews(String audioId,long audioLis) {

        HashMap<String, Object> viewMap = new HashMap<>();
        viewMap.put("listened", audioLis+1);
        DatabaseReference audiosRef = FirebaseDatabase.getInstance().getReference().child("audios");
        audiosRef.child(audioId).updateChildren(viewMap);
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView image;
        TextView audioTitle,listened,date;

       public ViewHolder(@NonNull View itemView){
           super(itemView);

           image = itemView.findViewById(R.id.item_audio_image);
           audioTitle = itemView.findViewById(R.id.item_audio_title);
           listened = itemView.findViewById(R.id.item_audio_listen_count);
           date = itemView.findViewById(R.id.item_audio_date);
       }
   }
}
