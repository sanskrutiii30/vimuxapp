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

public class contactUs extends AppCompatActivity {

    EditText subject,desc;
    Button subCon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact);

        subject = findViewById(R.id.contact_subject);
        desc = findViewById(R.id.contact_desc);
        subCon = findViewById(R.id.contact_btn);

        subCon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (subject.getText().toString().isEmpty() || desc.getText().toString().isEmpty()){
                    Toast.makeText(contactUs.this, "Please enter all details.", Toast.LENGTH_SHORT).show();
                }
                else {
                    sendToDb();
                }
            }
        });
    }

    private void sendToDb() {

        DatabaseReference dbRefCn = FirebaseDatabase.getInstance().getReference("contactUs");
        String loggedUser = Prevalent.currentOnlineUser.getUsername();
        String loggedUserName = Prevalent.currentOnlineUser.getName();
        String con_subject = subject.getText().toString();
        String con_desc = desc.getText().toString();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currDate = new SimpleDateFormat("dd-MMM-yyyy");
        SimpleDateFormat currTime = new SimpleDateFormat("HH:mm:ss");
        String saveCurrentDate = currDate.format(calendar.getTime());
        String saveCurrentTime = currTime.format(calendar.getTime());

        HashMap<String,Object> cont = new HashMap<>();

        cont.put("username",loggedUser);
        cont.put("name",loggedUserName);
        cont.put("subject",con_subject);
        cont.put("description",con_desc);

        dbRefCn.child(saveCurrentDate+" "+saveCurrentTime).updateChildren(cont).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getApplicationContext(), "Query submitted. Will revert within 24 working hours", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(contactUs.this,Home.class));
            }
        });
    }

}