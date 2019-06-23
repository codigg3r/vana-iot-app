package com.example.codigger.vana;

import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.design.widget.NavigationView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OfflineActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mRecyclerAdapter;
    private RecyclerView.LayoutManager layoutManager;
    public  ArrayList<Tekrar> tekrars;
    public  ArrayList<Tekrar> tekrars1;
    List<WifiConfiguration> configuredNetworks;

    LottieAnimationView animationView;
    Button vana;
    String vanaDurum = "";
    String pass = "1234";
    String ssid = "vana";
    String key  = "vanakey00";
    WifiManager wifiManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
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
        //FAB
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent setTimeIntent = new Intent(getBaseContext(),SetTimeActivity.class);
                startActivity(setTimeIntent);
            }
        });
        wifiManager = (WifiManager)getApplicationContext().getSystemService(WIFI_SERVICE);


        mRecyclerView = (RecyclerView)findViewById(R.id.timer_view);
        animationView = findViewById(R.id.sync_animation);
        animationView.setVisibility(View.VISIBLE);
        animationView.playAnimation();
        connectVana();
        getVanaState(pass);

    }
    public WifiConfiguration setWifiConfig(String ssid,String key){
        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = String.format("\"%s\"", ssid);
        wifiConfig.preSharedKey = String.format("\"%s\"", key);

        return wifiConfig;
    }
    public int getNetworks(WifiManager wifiManager, String ssid){
        int id = -1;
        configuredNetworks = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration conf : configuredNetworks){
            if (conf.SSID.equals(String.format("\"%s\"", ssid))){
                id = (conf.networkId);
                return id;
            }
        }
        return -1;
    }
    private void connectVana() {
        //CONNECT WiFi

            int netId = getNetworks(wifiManager, ssid);
            Log.d("puta","id: "+netId);
            if (netId == -1){
                netId = wifiManager.addNetwork(setWifiConfig(ssid,key));
            }else{
                wifiManager.removeNetwork(netId);
                netId = wifiManager.addNetwork(setWifiConfig(ssid,key));

            }

            wifiManager.disableNetwork(wifiManager.getConnectionInfo().getNetworkId());
            wifiManager.enableNetwork(netId, true);
            wifiManager.reconnect();

    }

    private void getVanaState(String pass) {
        String url = "http://10.10.10.2/getVanaState:"+pass;

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
                vanaDurum = response.header("vanaState");
            }
        });

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
            Intent act = new Intent(getBaseContext(),SetupActivity.class);
            startActivity(act);
        } else if (id == R.id.cikis) {
            //TODO Cıkıs işleleri

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
