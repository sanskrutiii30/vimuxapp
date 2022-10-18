package com.example.vimux;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.vimux.Model.Comment;
import com.example.vimux.ViewHolder.CommentHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class commentsPage extends AppCompatActivity {

    DatabaseReference comRef;
    private RecyclerView recyclerComment;
    RecyclerView.LayoutManager layoutCom;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comments_page);


        recyclerComment = findViewById(R.id.comment_recycler);
        recyclerComment.setHasFixedSize(true);
        layoutCom = new LinearLayoutManager(this);
        recyclerComment.setLayoutManager(layoutCom);

        String videoID = getIntent().getStringExtra("videoId");
        String videoTitle = getIntent().getStringExtra("videott");

        Toolbar toolbar = findViewById(R.id.toolbar_comment);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Comments for video: " + videoTitle);

        comRef = FirebaseDatabase.getInstance().getReference("comments").child(videoID);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Comment> options = new FirebaseRecyclerOptions.Builder<Comment>().setQuery(comRef,Comment.class).build();
        FirebaseRecyclerAdapter<Comment, CommentHolder> adapter = new FirebaseRecyclerAdapter<Comment, CommentHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CommentHolder holder, int position, @NonNull Comment model) {
                holder.comment.setText(model.getName() + ": " + model.getComment());
                holder.date.setText("on: " + model.getDate());
                Glide.with(getApplicationContext()).load(model.getImage()).into(holder.comImage);
            }

            @NonNull
            @Override
            public CommentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_row,parent,false);
                CommentHolder holder = new CommentHolder(v);
                return holder;
            }
        };
        recyclerComment.setAdapter(adapter);
        adapter.startListening();
    }
}