package com.example.vimux.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vimux.ItemClickListener;
import com.example.vimux.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class AudioViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView audio_name,audio_publisher,audio_listened,audio_date;
    public ImageView audio_img;
    public ItemClickListener listener;
    public CircleImageView userChannelImage;

    public AudioViewHolder(@NonNull View itemView) {
        super(itemView);
        audio_name = itemView.findViewById(R.id.all_audios_name);
        audio_publisher = itemView.findViewById(R.id.all_audios_publisher);
        audio_listened = itemView.findViewById(R.id.all_audios_listened);
        audio_img = itemView.findViewById(R.id.all_audios_img);
        userChannelImage = itemView.findViewById(R.id.all_audios_user_logo);
        audio_date = itemView.findViewById(R.id.all_audios_date);
    }

    public void setItemClickListener(ItemClickListener listener){
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        listener.onClick(view,getAdapterPosition(),false);
    }
}
