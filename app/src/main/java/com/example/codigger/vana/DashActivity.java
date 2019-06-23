package com.example.codigger.vana;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class DashActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mRecyclerAdapter;
    private RecyclerView.LayoutManager layoutManager;
    public  ArrayList<Tekrar> tekrars;
    public  ArrayList<Tekrar> tekrars1;
    LottieAnimationView animationView;
    Button vana;
    String vanaDurum = "";

    String uid;
    String repeats = " ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //DEFAULT INITIALIZE
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_dash);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();
            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);
        //------------------

        //------------------
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();

        //set mail on drawner menu
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.nav_email);
        navUsername.setText(mAuth.getCurrentUser().getEmail());

        //FAB
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent setTimeIntent = new Intent(getBaseContext(),SetTimeActivity.class);
                startActivity(setTimeIntent);
            }
        });


        mRecyclerView = (RecyclerView)findViewById(R.id.timer_view);

        animationView = findViewById(R.id.sync_animation);
        animationView.setVisibility(View.VISIBLE);
        animationView.playAnimation();
        getRepeats();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users/"+uid+"/vana/hardReset");
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
              //  showNofication();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        final Context c = this.getApplicationContext();
        vana = findViewById(R.id.vanaDurum);
        setVana(c);
        final Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setTimerScreen();
                animationView.setVisibility(View.INVISIBLE);

                if (vanaDurum.equals("\"on\"")){
                    vana.setText("AÇIK");
                    vana.setTag("1");
                    vana.setBackgroundResource(R.drawable.vana_on);
                }else if(vanaDurum.equals("\"off\"")){
                    vana.setText("KAPALI");
                    vana.setTag("0");
                    vana.setBackgroundResource(R.drawable.vana_off);
                }else{
                    //first usage..

                }
                setVana(c);

            }

        }, 5000);

    }

    private void showNofication() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(android.R.drawable.ic_dialog_alert);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        Intent intent = new Intent(this,SetupActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        builder.setContentIntent(pendingIntent);
        builder.setContentTitle("Cihazınız Fabrika ayarlarına Döndü");
        builder.setContentText("Lütfen Cihazın Kurulumunu Yapınız.");
        builder.setSubText("Kurulum yapmak için tıklayınız.");

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Will display the notification in the notification bar
        notificationManager.notify(1, builder.build());


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dash, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.kurulum) {
            // Handle the camera action
            Intent act = new Intent(getBaseContext(),SetupActivity.class);
            startActivity(act);
        } else if (id == R.id.cikis) {
            mAuth = FirebaseAuth.getInstance();
            mAuth.signOut();
            Intent act = new Intent(getBaseContext(),OnlineActivity.class);
            startActivity(act);
            finish();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void vanaChange(View view) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");

        if (view.getTag().equals("0")){
            view.setBackgroundResource(R.drawable.vana_on);
           // view.setBackground(ContextCompat.getDrawable(this.getBaseContext(), R.drawable.vana_on));
            ((Button) view).setText("AÇIK");
            myRef.child(uid).child("vana").child("vanaDurum").setValue("on");
            view.setTag("1");
        }else {
            view.setBackgroundResource(R.drawable.vana_off);
            //view.setBackground(ContextCompat.getDrawable(this.getBaseContext(), R.drawable.vana_off));
            ((Button) view).setText("KAPALI");
            myRef.child(uid).child("vana").child("vanaDurum").setValue("off");
            view.setTag("0");
        }
    }

    public void getRepeats(){
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
                repeats = repeats + response.body().string();
            }
        });
    }




    public void setTimerScreen(){
        if (repeats.length()<10) { return; }
        repeats = repeats.substring(2, repeats.length() - 1);
        String[] objects = repeats.split("\\}");
        int size = objects.length - 1;

        tekrars1 = new ArrayList<>();
        while (size>=0){
            Tekrar tr;
            Gson g = new Gson();

            if (size == 0){
                tr = g.fromJson(objects[size] +"}", Tekrar.class);

            }else {
                tr = g.fromJson(objects[size].substring(1,objects[size].length())+"}" , Tekrar.class);

            }
            size = size - 1;
            tekrars1.add(tr);
        }

        mRecyclerAdapter = new TekrarAdapter(tekrars1,getBaseContext());
        ((TekrarAdapter) mRecyclerAdapter).reSet();
        //mRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setAdapter(mRecyclerAdapter);
        mRecyclerView.setLayoutManager(layoutManager);
    }


    public void setVana(final Context c){


        String url = "https://vana-6640a.firebaseio.com/users/" + uid + "/vana/vanaDurum.json";

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
                vanaDurum = vanaDurum + response.body().string();

            }
        });
    }


}
