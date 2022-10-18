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
import com.example.vimux.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import io.paperdb.Paper;

public class reset_password extends AppCompatActivity {

    EditText oldPas,resetPas,resetPasCon;
    Button resetPasBtn;
    private ProgressDialog loading;
    private AwesomeValidation awesomeValidation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_password);

        oldPas=findViewById(R.id.reset_old_password_input);
        resetPas=findViewById(R.id.reset_new_password_input);
        resetPasCon=findViewById(R.id.reset_new_confirm_input);
        resetPasBtn= findViewById(R.id.reset_pass_btn);

        awesomeValidation=new AwesomeValidation(ValidationStyle.BASIC);
        loading=new ProgressDialog(this);

        resetPasBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emptyResetPassword();
            }
        });
    }

    private void emptyResetPassword() {
        String newpassword=resetPas.getText().toString();
        String oldPassword=oldPas.getText().toString();
        awesomeValidation.addValidation(this, R.id.reset_new_password_input, ".{6,}",R.string.invalid_password);
        awesomeValidation.addValidation(this, R.id.reset_old_password_input, RegexTemplate.NOT_EMPTY,R.string.empty_password);
        awesomeValidation.addValidation(this,R.id.reset_new_confirm_input,resetPas.getText().toString(),R.string.invalid_pass_con);

        if(awesomeValidation.validate())
        {
            loading.setTitle("Set Password");
            loading.setMessage("Please wait while we set your password");
            loading.setCanceledOnTouchOutside(false);
            loading.show();

            resetNewPassword(oldPassword,newpassword);
            loading.dismiss();

        }
    }

    private void resetNewPassword(String oldPassword,String newPassword) {
        final DatabaseReference resetRef;
        resetRef = FirebaseDatabase.getInstance().getReference().child("users").child(Prevalent.currentOnlineUser.getUsername());

        if (Prevalent.currentOnlineUser.getPassword().equals(oldPassword)) {
            HashMap<String, Object> userResetPas = new HashMap<>();
            userResetPas.put("password", newPassword);
            resetRef.updateChildren(userResetPas).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(reset_password.this, "Reset Password Successful.", Toast.LENGTH_SHORT).show();
                    Paper.book().destroy();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    Toast.makeText(reset_password.this, "Please login again.", Toast.LENGTH_SHORT).show();
                }
            });
        } else
        {
            Toast.makeText(this, "Old Password is incorrect", Toast.LENGTH_SHORT).show();
        }


    }
}