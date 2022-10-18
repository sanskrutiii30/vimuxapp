package com.example.vimux.ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vimux.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public CircleImageView comImage;
    public TextView comment,date;

    public CommentHolder(@NonNull View itemView) {
        super(itemView);

        comImage = itemView.findViewById(R.id.comment_lay_img);
        comment = itemView.findViewById(R.id.comment_lay_text);
        date = itemView.findViewById(R.id.comment_lay_date);
    }

    @Override
    public void onClick(View view) {

    }
}
