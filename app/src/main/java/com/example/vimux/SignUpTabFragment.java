package com.example.vimux;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class SignUpTabFragment extends Fragment {

    EditText fullName,email,password,cPass,phone,username,answer;
    ProgressDialog loadingBar;
    AwesomeValidation awesomeValidation;
    Button registerBtn;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    private String question="",amt="";

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ViewGroup root = (ViewGroup)  inflater.inflate(R.layout.signup_tab_fragment, container, false);

        fullName = root.findViewById(R.id.s_Name);
        email = root.findViewById(R.id.s_email);
        password = root.findViewById(R.id.s_pass);
        cPass = root.findViewById(R.id.s_ConPass);
        phone = root.findViewById(R.id.s_phone);
        username = root.findViewById(R.id.s_user_name);
        answer = root.findViewById(R.id.s_answer);
        registerBtn = root.findViewById(R.id.signUp_btn);

        loadingBar=new ProgressDialog(getContext());
        awesomeValidation= new AwesomeValidation(ValidationStyle.BASIC);

        fAuth = FirebaseAuth.getInstance();
        fStore=FirebaseFirestore.getInstance();

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount();
            }
        });

        Spinner forgotSpin=(Spinner) root.findViewById(R.id.quesSpin);
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(getContext(),
                R.layout.selected_item,getResources()
                .getStringArray(R.array.questions));
        adapter.setDropDownViewResource(R.layout.drop_down_items);
        forgotSpin.setAdapter(adapter);

        forgotSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(!forgotSpin.getItemAtPosition(position).toString().equals("Select security question"))
                {
                    question=forgotSpin.getItemAtPosition(position).toString();
                }
                else
                {
                    question="qq";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return root;
    }

    private void createAccount() {

        String user_full_Name=fullName.getText().toString();
        String user_uName=username.getText().toString();
        String user_phone = phone.getText().toString();
        String user_email=email.getText().toString();
        String user_password=password.getText().toString();
        String user_answer=answer.getText().toString();
        String user_cPass=cPass.getText().toString();


        awesomeValidation.addValidation(getActivity(), R.id.s_Name, RegexTemplate.NOT_EMPTY,R.string.invalid_name);
        awesomeValidation.addValidation(getActivity(), R.id.s_user_name, RegexTemplate.NOT_EMPTY,R.string.empty_username);
        awesomeValidation.addValidation(getActivity(), R.id.s_phone, "[5-9]{1}[0-9]{9}",R.string.invalid_mobile);
        awesomeValidation.addValidation(getActivity(), R.id.s_email, Patterns.EMAIL_ADDRESS,R.string.invalid_email);
        awesomeValidation.addValidation(getActivity(), R.id.s_pass, ".{6,}",R.string.invalid_password);
        awesomeValidation.addValidation(getActivity(), R.id.s_answer, RegexTemplate.NOT_EMPTY,R.string.empty_answer);
        if(awesomeValidation.validate()) {
            if (user_password.equals(user_cPass)) {

                if (!question.equals("qq") ) {
                    loadingBar.setTitle("Create Account");
                    loadingBar.setMessage("Please Wait While We Confirm All Details");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();

                    ValidateUser(user_full_Name,user_uName,user_phone,user_email,user_password,question,user_answer);
                } else {
                    Toast.makeText(getActivity(), "Please Select a security question", Toast.LENGTH_SHORT).show();
                }
            }
            else{
                Toast.makeText(getContext(), "Password does not match", Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(getContext(),"Validation Failed",Toast.LENGTH_SHORT).show();
        }

    }

    private void ValidateUser(String user_full_name, String user_uName, String user_phone, String user_email, String user_password, String question, String user_answer) {

        final DatabaseReference rootRef;
        rootRef = FirebaseDatabase.getInstance().getReference().child("users");

        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (!(snapshot.child(user_uName).exists())) {

                    rootRef.orderByChild("phone").equalTo(user_phone).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.getValue()!=null)
                            {
                                Toast.makeText(getContext(), "This Phone Number Already Exists", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }else {

                                fAuth.createUserWithEmailAndPassword(user_email,user_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            Calendar calendar = Calendar.getInstance();
                                            SimpleDateFormat currDate = new SimpleDateFormat("dd-MMM-yyyy");
                                            SimpleDateFormat currTime = new SimpleDateFormat("HH:mm:ss");
                                            String saveCurrentDate = currDate.format(calendar.getTime());
                                            String saveCurrentTime = currTime.format(calendar.getTime());
                                            String expDt = saveCurrentDate;
                                            try {
                                                calendar.setTime(currDate.parse(expDt));
                                                calendar.add(Calendar.DATE,31);
                                                expDt = currDate.format(calendar.getTime());
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }

                                            String user_image = "https://firebasestorage.googleapis.com/v0/b/vimux-c03e0.appspot.com/o/Profile%20Pictures%2Fdefault.jpg?alt=media&token=2230894f-ca11-4ab2-abc3-70e33a1bb1d1";


                                            HashMap<String, Object> userdataMap = new HashMap<>();

                                            userdataMap.put("phone", user_phone);
                                            userdataMap.put("username", user_uName);
                                            userdataMap.put("email", user_email);
                                            userdataMap.put("name", user_full_name);
                                            userdataMap.put("password", user_password);
                                            userdataMap.put("question", question);
                                            userdataMap.put("answer", user_answer);
                                            userdataMap.put("image", user_image);
                                            userdataMap.put("Amount", amt);
                                            userdataMap.put("Date", saveCurrentDate);
                                            userdataMap.put("Time", saveCurrentTime);

                                            toFirestore(user_full_name,user_email);

                                            rootRef.child(user_uName).updateChildren(userdataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(getContext(), "Congratulations, Your Account Has Been Created", Toast.LENGTH_SHORT).show();
                                                        loadingBar.dismiss();
                                                        Intent intent = new Intent(getContext(), subscription.class);
                                                        intent.putExtra("Email",user_email);
                                                        intent.putExtra("Phone",user_phone);
                                                        intent.putExtra("Username",user_uName);
                                                        intent.putExtra("Amount",amt);
                                                        startActivity(intent);

                                                    } else {
                                                        Toast.makeText(getContext(), "Error! Please Try Again", Toast.LENGTH_SHORT).show();
                                                        loadingBar.dismiss();

                                                    }
                                                }
                                            });
                                        }
                                        else{
                                            if(task.getException().getMessage().equals("The email address is already in use by another account."))
                                            {
                                                loadingBar.dismiss();
                                                Toast.makeText(getContext(), "Email already exist", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                                });
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                } else {

                    Toast.makeText(getContext(), "This User Name Already Exists", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void toFirestore(String name,String email) {

        String model= Build.MODEL;
        String mfr= Build.MANUFACTURER;
        String device= Build.DEVICE;
        String hard= Build.HARDWARE;
        String product= Build.PRODUCT;
        String brand= Build.BRAND;
        int sdk= Build.VERSION.SDK_INT;
        WifiManager wifiManager = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);
        String ipAddress = Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress());



        Map<String,Object> userMap=new HashMap<>();

        userMap.put("Name: ",name);
        userMap.put("Email: ",email);
        userMap.put("brand: ",brand);
        userMap.put("model: ",model);
        userMap.put("manufacturer: ",mfr);
        userMap.put("product: ",product);
        userMap.put("device: ",device);
        userMap.put("hardware: ",hard);
        userMap.put("IP: ",ipAddress);
        userMap.put("sdk: ",sdk);
        fStore.collection("devices").document(name).set(userMap);

    }
}
