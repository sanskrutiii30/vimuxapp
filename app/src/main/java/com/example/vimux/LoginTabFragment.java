package com.example.vimux;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.example.vimux.Model.Users;
import com.example.vimux.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class LoginTabFragment extends Fragment {

    EditText username,pass;
    TextView forgetpass;
    Button login;
    CheckBox remember;

    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

    ProgressDialog loadingBar;
    AwesomeValidation awesomeValidation;
    float v=0;


    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ViewGroup root = (ViewGroup)  inflater.inflate(R.layout.login_tab_fragment, container, false);

        username = root.findViewById(R.id.login_uname);
        pass = root.findViewById(R.id.login_pass);
        forgetpass = root.findViewById(R.id.forget_text);
        login = root.findViewById(R.id.login_btn);
        remember = root.findViewById(R.id.remember_chkbox);
        Paper.init(getContext());



        loadingBar=new ProgressDialog(getContext());
        awesomeValidation= new AwesomeValidation(ValidationStyle.BASIC);

        username.setTranslationY(800);
        pass.setTranslationY(800);
        forgetpass.setTranslationY(800);
        login.setTranslationY(800);
        remember.setTranslationY(800);

        username.setAlpha(v);
        pass.setAlpha(v);
        forgetpass.setAlpha(v);
        login.setAlpha(v);
        remember.setAlpha(v);

        username.animate().translationY(0).alpha(1).setDuration(800).setStartDelay(500).start();
        pass.animate().translationY(0).alpha(1).setDuration(800).setStartDelay(500).start();
        forgetpass.animate().translationY(0).alpha(1).setDuration(800).setStartDelay(500).start();
        remember.animate().translationY(0).alpha(1).setDuration(800).setStartDelay(500).start();
        login.animate().translationY(0).alpha(1).setDuration(800).setStartDelay(700).start();


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });

        forgetpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(),forgot_password.class));
            }
        });
        return root;
    }

    private void loginUser() {

        String uName = username.getText().toString();
        String password = pass.getText().toString();

        awesomeValidation.addValidation(getActivity(), R.id.login_uname, RegexTemplate.NOT_EMPTY,R.string.empty_username);
        awesomeValidation.addValidation(getActivity(), R.id.login_pass, RegexTemplate.NOT_EMPTY,R.string.empty_password);

        if(awesomeValidation.validate())
        {
            loadingBar.setTitle("Login Account");
            loadingBar.setMessage("Please Wait While We Check Your Credentials");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            AllowAccessToAccount(uName, password);
        }else{
            Toast.makeText(getContext(),"Error! Please Enter Username and Password",Toast.LENGTH_SHORT).show();
            loadingBar.dismiss();
        }

    }

    private void AllowAccessToAccount(String username, String password) {

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("users").child(username).exists()) {

                    Users userdata = snapshot.child("users").child(username).getValue(Users.class);

                    if (userdata.getUsername().equals(username)) {
                        if (userdata.getPassword().equals(password)) {

                            if(!userdata.getAmount().equals(""))
                            {
                                Toast.makeText(getContext(), "Welcome "+userdata.getName(), Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                                Intent intent = new Intent(getContext(), Home.class);
                                Prevalent.currentOnlineUser=userdata;
                                startActivity(intent);
                                if(remember.isChecked())
                                {
                                    Paper.book().write(Prevalent.UserNameKey,username);
                                    Paper.book().write(Prevalent.UserPasswordKey,password);

                                }
                            }
                            else{
                                Toast.makeText(getActivity(), "Not A member! Please Subscribe", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                                Intent intent = new Intent(getContext(), subscription.class);
                                intent.putExtra("Email",userdata.getEmail());
                                intent.putExtra("Phone",userdata.getPhone());
                                intent.putExtra("Username",username);
                                intent.putExtra("Amount","");
                                startActivity(intent);
                            }
                        }
                        else {
                            Toast.makeText(getContext(), "Password Is Incorrect!", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                    }
                }else {
                    Toast.makeText(getContext(), "Account Does Not Exist", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    startActivity(intent);

                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
