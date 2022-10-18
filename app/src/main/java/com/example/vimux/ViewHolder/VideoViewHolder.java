package com.example.vimux.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vimux.ItemClickListener;
import com.example.vimux.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class VideoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView videoName,publisherName,views,date;
    public ImageView videoImg;
    public ItemClickListener listener;
    public CircleImageView userChannelImg;

    public VideoViewHolder(@NonNull View itemView) {
        super(itemView);
        videoName = itemView.findViewById(R.id.videoName);
        publisherName = itemView.findViewById(R.id.video_publisher);
        userChannelImg = itemView.findViewById(R.id.user_channel_image_allVids);
        views = itemView.findViewById(R.id.vid_views);
        videoImg = itemView.findViewById(R.id.videoImg);
        date = itemView.findViewById(R.id.vidDate);

    }
    public void setItemClickListener(ItemClickListener listener){
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        listener.onClick(view,getAdapterPosition(),false);
    }
}
