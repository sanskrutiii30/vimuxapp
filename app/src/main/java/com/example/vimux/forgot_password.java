package com.example.vimux;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.example.vimux.Model.Users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class forgot_password extends AppCompatActivity {

    EditText forgotUser,fAnswer;
    TextView fQues;
    Button CheckUser,VerifyAnswer;
    ProgressDialog loading;
    AwesomeValidation awesome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password);

        forgotUser=findViewById(R.id.username_input);
        fAnswer=findViewById(R.id.secAns);
        fQues=findViewById(R.id.secQues);
        CheckUser=findViewById(R.id.check_btn);
        VerifyAnswer=findViewById(R.id.verifyAns_btn);
        awesome=new AwesomeValidation(ValidationStyle.BASIC);
        loading=new ProgressDialog(this);


        fQues.setVisibility(View.INVISIBLE);
        fAnswer.setVisibility(View.INVISIBLE);
        VerifyAnswer.setVisibility(View.INVISIBLE);

        CheckUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emptyUser();

            }
        });
        VerifyAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emptyAnswer();
            }
        });
    }

    private void emptyUser() {


        awesome.addValidation(this, R.id.username_input, RegexTemplate.NOT_EMPTY, R.string.empty_username);
        if (awesome.validate()) {
            loading.setTitle("Check Username");
            loading.setMessage("Please wait while we verify username");
            loading.setCanceledOnTouchOutside(false);
            loading.show();
            checkUser();
        }
    }


    private void checkUser() {
        String forgotuser=forgotUser.getText().toString();
        final DatabaseReference db= FirebaseDatabase.getInstance().getReference();
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.child("users").child(forgotuser).exists())
                {
                    fQues.setVisibility(View.VISIBLE);
                    fAnswer.setVisibility(View.VISIBLE);
                    VerifyAnswer.setVisibility(View.VISIBLE);
                    Users uData=snapshot.child("users").child(forgotuser).getValue(Users.class);
                    String ques = uData.getQuestion();
                    fQues.setText(ques);
                    loading.dismiss();

                }
                else
                {
                    Toast.makeText(forgot_password.this, "User does not exist", Toast.LENGTH_SHORT).show();
                    loading.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void emptyAnswer() {
        String answerInput=fAnswer.getText().toString();

        awesome.addValidation(this, R.id.secAns, RegexTemplate.NOT_EMPTY, R.string.empty_answer);
        if (awesome.validate()) {
            loading.setTitle("Verify Answer");
            loading.setMessage("Please wait while we verify answer");
            loading.setCanceledOnTouchOutside(false);
            loading.show();
            securityAnswer(answerInput);
        }
    }
    private void securityAnswer(String answerInput) {
        String forgotuser1=forgotUser.getText().toString();
        final DatabaseReference dbase=FirebaseDatabase.getInstance().getReference();
        dbase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.child("users").child(forgotuser1).exists())
                {
                    Users uData=snapshot.child("users").child(forgotuser1).getValue(Users.class);

                    if(uData.getAnswer().equals(answerInput))
                    {
                        Toast.makeText(forgot_password.this, "Verification Successful", Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(forgot_password.this,newPassSet.class);
                        intent.putExtra("uName",uData.getUsername());
                        startActivity(intent);
                    }
                    else
                    {
                        Toast.makeText(forgot_password.this, "Answer is incorrect", Toast.LENGTH_SHORT).show();
                    }

                    loading.dismiss();

                }
                else
                {
                    Toast.makeText(forgot_password.this, "User does not exist", Toast.LENGTH_SHORT).show();
                    loading.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}