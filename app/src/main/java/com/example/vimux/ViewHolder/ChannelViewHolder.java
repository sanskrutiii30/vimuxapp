package com.example.vimux.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vimux.ItemClickListener;
import com.example.vimux.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChannelViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView channelName,channelUser;
    public CircleImageView channelImage;
    public ItemClickListener listener;

    public ChannelViewHolder(@NonNull View itemView) {
        super(itemView);
        channelImage = itemView.findViewById(R.id.channelImg);
        channelName = itemView.findViewById(R.id.allChannelName);
        channelUser = itemView.findViewById(R.id.allChanneluName);
    }

    public void setItemClickListener(ItemClickListener listener){
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        listener.onClick(view,getAdapterPosition(),false);
    }
}
