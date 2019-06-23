package com.example.codigger.vana;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SetTimeActivity extends AppCompatActivity {
    String uid;
    TimePicker sTime;
    TimePicker eTime;
    public FirebaseAuth mAuth;
    String getRes = "";
    String gun ="";

    int count = 0 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_time);

        sTime = findViewById(R.id.sTime);
        eTime = findViewById(R.id.eTime);

        sTime.setIs24HourView(true);
        eTime.setIs24HourView(true);
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();
        getData();




    }

    public void selectDay(View view) {
        if (view.getTag().equals("0")) {
            view.setBackgroundResource(R.drawable.status_button_on);
            view.setTag("1");
        }else {
            view.setBackgroundResource(R.drawable.status_button);
            view.setTag("0");
        }

    }

    public void timeSet(View view) {

        String sTimeString = "";
        String eTimeString = "";
        String days= "";
        int shour;
        int smin;
        int ehour;
        int emin;
        ArrayList<Integer> data = new ArrayList<>();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            shour = sTime.getHour();
            smin  = sTime.getMinute();
            ehour = eTime.getHour();
            emin  = eTime.getMinute();
            sTimeString = shour + ":" + smin ;
            eTimeString = ehour + ":" + emin ;
        }else{
            shour = sTime.getCurrentHour();
            smin  = sTime.getCurrentMinute();
            ehour = eTime.getCurrentHour();
            emin  = eTime.getCurrentMinute();
            sTimeString = shour + ":" + smin;
            eTimeString = ehour + ":" + emin;
        }
        if(findViewById(R.id.pzt).getTag().equals("1")) {
            days = days + "pzt ";
            data.add(100000 + 10*(shour*60 +smin) + 1);
            data.add(100000 + 10*(ehour*60 +emin));
        }

        if(findViewById(R.id.sal).getTag().equals("1")) {
            days = days + "sal ";
            data.add(200000 + 10*(shour*60 +smin) + 1);
            data.add(200000 + 10*(ehour*60 +emin));
        }

        if(findViewById(R.id.car).getTag().equals("1")) {
            days = days + "car ";
            data.add(300000 + 10*(shour*60 +smin) + 1);
            data.add(300000 + 10*(ehour*60 +emin));
        }

        if(findViewById(R.id.per).getTag().equals("1")) {
            days = days + "per ";
            data.add(400000 + 10*(shour*60 +smin) + 1);
            data.add(400000 + 10*(ehour*60 +emin));
        }

        if(findViewById(R.id.cum).getTag().equals("1")) {
            days = days + "cum ";
            data.add(500000 + 10*(shour*60 +smin) + 1);
            data.add(500000 + 10*(ehour*60 +emin));
        }

        if(findViewById(R.id.cmt).getTag().equals("1")) {
            days = days + "cmt ";
            data.add(600000 + 10*(shour*60 +smin) + 1);
            data.add(600000 + 10*(ehour*60 +emin));
        }

        if(findViewById(R.id.paz).getTag().equals("1")) {
            days = days + "paz ";
            data.add(700000 + 10*(shour*60 +smin) + 1);
            data.add(700000 + 10*(ehour*60 +emin));
        }




        if (days.length()>1){
            if (getRes.length()>10) {

                getRes = getRes.substring(1, getRes.length() - 1);
                getRes = getRes.substring(getRes.lastIndexOf("{"), getRes.length());
                Gson g = new Gson();
                Tekrar tr = g.fromJson(getRes, Tekrar.class);
                count = tr.getCount()+1;
                gun = tr.getDays();
            }else{
                count = 0;
            }
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("users");
            myRef.child(uid).child("vana").child("tekrar").child("node").child(String.valueOf(count)).setValue(data);
            myRef.child(uid).child("vana").child("tekrar").child("android").child(String.valueOf(count)).setValue(new Tekrar(sTimeString,eTimeString,days,true,count));
            Intent setTimeIntent = new Intent(getBaseContext(),DashActivity.class);
            startActivity(setTimeIntent);
            finish();
        }else{
            Toast.makeText(this, "Lütfen gün seçiniz!", Toast.LENGTH_LONG).show();
        }
    }

    public void getData(){
        String url = "https://vana-6640a.firebaseio.com/users/" + uid + "/vana/tekrar/android.json";

        OkHttpClient client = new OkHttpClient();
        Request req = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(req);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                    getRes = getRes + response.body().string();
            }
        });
    }
}
