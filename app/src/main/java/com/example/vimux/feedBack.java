package com.example.vimux;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.vimux.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class feedBack extends AppCompatActivity {

    EditText feedbackGiven;
    Button submitFb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feed_back);

        feedbackGiven = findViewById(R.id.feedback_desc);
        submitFb = findViewById(R.id.feedback_btn_submit);

        submitFb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (feedbackGiven.getText().toString().isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "Please enter something.", Toast.LENGTH_SHORT).show();
                }
                else{
                    submitFeedBack();
                }
            }
        });

    }

    private void submitFeedBack() {

        DatabaseReference dbRefFB = FirebaseDatabase.getInstance().getReference("feedbacks");
        String loggedUser = Prevalent.currentOnlineUser.getUsername();
        String loggedUserName = Prevalent.currentOnlineUser.getName();
        String feedGiven = feedbackGiven.getText().toString();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currDate = new SimpleDateFormat("dd-MMM-yyyy");
        SimpleDateFormat currTime = new SimpleDateFormat("HH:mm:ss");
        String saveCurrentDate = currDate.format(calendar.getTime());
        String saveCurrentTime = currTime.format(calendar.getTime());

        HashMap<String,Object> feedback = new HashMap<>();

        feedback.put("username",loggedUser);
        feedback.put("name",loggedUserName);
        feedback.put("feedback",feedGiven);

        dbRefFB.child(saveCurrentDate+" "+saveCurrentTime).updateChildren(feedback).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getApplicationContext(), "Feedback submitted", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(feedBack.this,Home.class));
            }
        });
    }


}