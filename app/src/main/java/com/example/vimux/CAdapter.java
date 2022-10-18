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
import com.example.vimux.Model.Content;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class CAdapter extends RecyclerView.Adapter<CAdapter.ViewHolder> {

    Context context;
    ArrayList<Content> list;




    public CAdapter(Context context, ArrayList<Content> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_video,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Content model = list.get(position);
        if (model != null)
        {
            Glide.with(context).asBitmap().load(model.getVideo_url()).into(holder.thumbnail);
            holder.videoTitle.setText("video name: " + model.getTitle());
            holder.views.setText("views: " + String.valueOf(model.getViews()));
            holder.date.setText("date: " + model.getDate());
            
//            setData(model.getPublisher(),holder.channel_logo, holder.channelName);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent =new Intent(context,videoExo.class);
                    intent.putExtra("videoUrl",model.getVideo_url());
                    intent.putExtra("videoid",model.getVideoId());
                    intent.putExtra("videoTitle",model.getTitle());
                    context.startActivity(intent);
                    String videoid = model.getVideoId();
                    long videoviews = model.getViews();
                    updateViews(videoid,videoviews);
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
                            videoEditDialog(model.getTitle(), model.getDescription(), model.getVideoId());
                            dialog.dismiss();
                        }
                    });

                    deleteItem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            AlertDialog.Builder delDialog = new AlertDialog.Builder(context);
                            delDialog.setTitle("Delete video?");
                            delDialog.setMessage("Do you really want to delete "+ model.getTitle()+" ?");
                            delDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("videos");
                                    dbRef.child(model.getVideoId()).removeValue();
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

    private void videoEditDialog(String title, String description, String vidId) {

        Dialog editDialog = new Dialog(context);
        editDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        editDialog.setContentView(R.layout.item_edit_dialog);
        editDialog.setCancelable(true);
        editDialog.setCanceledOnTouchOutside(true);

        EditText video_title = editDialog.findViewById(R.id.item_edit_title);
        EditText video_desc = editDialog.findViewById(R.id.item_edit_desc);
        TextView submit_txt = editDialog.findViewById(R.id.item_edit_done);

        video_title.setText(title);
        video_desc.setText(description);

        submit_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = video_title.getText().toString();
                String description = video_desc.getText().toString();

                if (name.isEmpty() || description.isEmpty())
                {
                    Toast.makeText(context, "Please enter all details", Toast.LENGTH_SHORT).show();
                }
                else{
                    saveEditedToFirebase(name,description,vidId);
                    editDialog.dismiss();
                    Toast.makeText(context, "Successfully modified", Toast.LENGTH_SHORT).show();
                }
            }
        });
        editDialog.show();


    }

    private void saveEditedToFirebase(String video_title, String video_desc, String videoId) {

        DatabaseReference vidRef = FirebaseDatabase.getInstance().getReference("videos");
        HashMap<String,Object> map = new HashMap<>();
        map.put("title",video_title);
        map.put("description",video_desc);
        vidRef.child(videoId).updateChildren(map);
    }



    private void updateViews(String videoid,long videoview) {

        HashMap<String, Object> viewMap = new HashMap<>();
        viewMap.put("views", videoview+1);
        DatabaseReference videosRef = FirebaseDatabase.getInstance().getReference().child("videos");
        videosRef.child(videoid).updateChildren(viewMap);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView thumbnail;
        TextView videoTitle,views,date;

       public ViewHolder(@NonNull View itemView){
           super(itemView);

           thumbnail = itemView.findViewById(R.id.thumbnail);
           videoTitle = itemView.findViewById(R.id.video_title);
           views = itemView.findViewById(R.id.view_count);
           date = itemView.findViewById(R.id.video_date);
       }
   }
}
