package com.example.vimux;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class newPassSet extends AppCompatActivity {

    String newUname="";
    EditText newpas,newpascon;
    Button newSetPasBtn;
    private ProgressDialog loading;
    private AwesomeValidation awesomeValidation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_pass_set);

        newUname=getIntent().getStringExtra("uName");

        newpas=findViewById(R.id.new_password_input);
        newpascon=findViewById(R.id.new_password_confirm);
        newSetPasBtn= findViewById(R.id.new_pass_btn);

        awesomeValidation=new AwesomeValidation(ValidationStyle.BASIC);
        loading=new ProgressDialog(this);


        newSetPasBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emptyNewPassword();
            }
        });
    }

    private void emptyNewPassword() {
        String newpassword=newpas.getText().toString();
        awesomeValidation.addValidation(this, R.id.new_password_input, ".{6,}",R.string.invalid_password);
        awesomeValidation.addValidation(this, R.id.new_password_input, RegexTemplate.NOT_EMPTY,R.string.empty_password);
        awesomeValidation.addValidation(this,R.id.new_password_confirm,newpas.getText().toString(),R.string.invalid_pass_con);

        if(awesomeValidation.validate())
        {
            loading.setTitle("Set Password");
            loading.setMessage("Please wait while we set your password");
            loading.setCanceledOnTouchOutside(false);
            loading.show();

            setNewPassword(newpassword);
            loading.dismiss();

        }
    }

    private void setNewPassword(String newpassword) {
        final DatabaseReference rootRef;
        rootRef = FirebaseDatabase.getInstance().getReference().child("users").child(newUname);

        HashMap<String, Object> usernewPas = new HashMap<>();
        usernewPas.put("password", newpassword);
        rootRef.updateChildren(usernewPas).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(newPassSet.this, "Set Password Successful", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });

    }
}