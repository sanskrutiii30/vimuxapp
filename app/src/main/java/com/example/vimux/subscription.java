package com.example.vimux;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class subscription extends AppCompatActivity implements PaymentResultListener {

    Button btn49,btn129,btn199,btn299;
    String email="",phone="",username="",amount="",amt="";
    int days;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subscription);

        email=getIntent().getStringExtra("Email");
        phone=getIntent().getStringExtra("Phone");
        username=getIntent().getStringExtra("Username");

        amt=getIntent().getStringExtra("Amount");

        btn49 = findViewById(R.id.btn_49);
        btn129 = findViewById(R.id.btn_129);
        btn199 = findViewById(R.id.btn_199);
        btn299 = findViewById(R.id.btn_299);

        Checkout.preload(subscription.this);

        btn49.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                amt = "49";
                days = 31;
                amount = "4900";
                startPayment(amount);
            }
        });

        btn129.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                amt = "129";
                days = 91;
                amount = "12900";
                startPayment(amount);
            }
        });

        btn199.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                amt = "199";
                days = 181;
                amount = "19900";
                startPayment(amount);
            }
        });

        btn299.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                amt = "299";
                days = 365;
                amount = "29900";
                startPayment(amount);
            }
        });
    }

    private void startPayment(String amount) {

        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_test_JLcIun9PAexao3");
        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name","ViMuX");
            jsonObject.put("description","Subscription");
            jsonObject.put("theme.color","#3399cc");
            jsonObject.put("currency","INR");
            jsonObject.put("amount",amount);
            jsonObject.put("prefill.email", email);
            jsonObject.put("prefill.contact",phone);

            JSONObject retryObj = new JSONObject();
            retryObj.put("enabled",true);
            retryObj.put("max_count",4);

            jsonObject.put("retry",retryObj);

            checkout.open(subscription.this,jsonObject);
        }catch (Exception e){
            Toast.makeText(subscription.this, "Error " +e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
            Intent intent= new Intent(subscription.this,MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
    }

    @Override
    public void onPaymentSuccess(String s) {

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currDate = new SimpleDateFormat("dd-MMM-yyyy");
        String saveCurrentDate = currDate.format(calendar.getTime());
        String expDt = saveCurrentDate;
        try {
            calendar.setTime(currDate.parse(expDt));
            calendar.add(Calendar.DATE,days);
            expDt = currDate.format(calendar.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }


        final DatabaseReference rootRefPay = FirebaseDatabase.getInstance().getReference().child("users");

        Map<String,Object> userPay = new HashMap<>();
        userPay.put("Amount",amt);
        userPay.put("expiry",expDt);

        rootRefPay.child(username).updateChildren(userPay).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                startActivity(new Intent(getApplicationContext(),subscription_success.class));
            }
        });

    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(this, "Error! Payment Failed", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(getApplicationContext(),MainActivity.class));


    }
}