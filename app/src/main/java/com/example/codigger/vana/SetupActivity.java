package com.example.codigger.vana;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class SetupActivity extends AppCompatActivity {
    WifiManager wifiManager;
    List<WifiConfiguration> configuredNetworks;
    public FirebaseAuth mAuth;
    String[] SSIDs ;
    String currentWifi ;
    String key;
    String nodeSsid = "vana";
    String nodeKey  = "vanakey00";
    EditText eTKey;
    TextView tWWifi;
    Button baglan;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        mAuth = FirebaseAuth.getInstance();
        String uid = mAuth.getCurrentUser().getUid();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancelAll();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users/"+uid+"/vana/hardReset");
        myRef.removeValue();


        wifiManager = (WifiManager)getApplicationContext().getSystemService(WIFI_SERVICE);
        eTKey = findViewById(R.id.eTKey);
        tWWifi = findViewById(R.id.tWWifi);
        baglan = findViewById(R.id.baglan);

        //set wifi enable
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
    }

    // BUTTON ONCLICK METHOD
    @SuppressLint("NewApi")
    public void run(View btn){
        getNetworks(wifiManager);
        ListAdapter wifiList = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,SSIDs);
        ListView wifiView =  findViewById(R.id.resWifi);
        wifiView.setVisibility(View.VISIBLE);
        setVisibility(0);
        wifiView.setAdapter(wifiList);
        onClickItem(wifiView);

    }

    public void onClickItem(final ListView mList){
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setCurrentWifi(String.valueOf(parent.getItemAtPosition(position)));
                mList.setVisibility(View.GONE);
                setVisibility(1);
                tWWifi.setText(currentWifi);
            }

        });

    }

    public void setVisibility(int id){
        if (id == 1) {
            eTKey.setVisibility(View.VISIBLE);
            tWWifi.setVisibility(View.VISIBLE);
            baglan.setVisibility(View.VISIBLE);
        }else{
            eTKey.setVisibility(View.GONE);
            tWWifi.setVisibility(View.GONE);
            baglan.setVisibility(View.GONE);
        }
    }

    public void setCurrentWifi(String mwifi){
        currentWifi = mwifi;
    }

    //CONNECT WiFi
    public void connectWifi(final String ssid, String key) {
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

    // SCAN NETWORKS WITH SSID
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
    // SCAN NETWORKS
    public void getNetworks(WifiManager wifiManager){
        configuredNetworks = wifiManager.getConfiguredNetworks();
        int size = configuredNetworks.size();
        int indexSSID = 0;
        SSIDs = new String[size];
        for (WifiConfiguration conf : configuredNetworks){

            SSIDs[indexSSID]= conf.SSID.split("\"")[1];
            indexSSID += 1;
        }
    }

    // CREATE WiFi CONFiG
    public WifiConfiguration setWifiConfig(String ssid,String key){
        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = String.format("\"%s\"", ssid);
        wifiConfig.preSharedKey = String.format("\"%s\"", key);

        return wifiConfig;
    }
    // CONNECT BUTTON ONCLICK
    public void connect(View view) {
        key = eTKey.getText().toString();

        setVisibility(0);
        connectWifi(nodeSsid,nodeKey);

        //visibilties
        final LottieAnimationView animation = findViewById(R.id.loading);
        final TextView connecting = findViewById(R.id.connecting);
        final TextView connecting2 = findViewById(R.id.connecting_2);
        final Button run = findViewById(R.id.runButton);
        run.setVisibility(View.INVISIBLE);
        connecting.setVisibility(View.VISIBLE);
        connecting2.setVisibility(View.VISIBLE);
        animation.setVisibility(View.VISIBLE);
        //------


        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                sendData();
                run.setVisibility(View.VISIBLE);
                connecting.setVisibility(View.INVISIBLE);
                connecting2.setVisibility(View.INVISIBLE);
                animation.setVisibility(View.INVISIBLE);

            }
        }, 30000);
        final Handler handler1 = new Handler();
        handler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (conResult){
                    connectWifi(currentWifi,key);
                    Intent dashIntent = new Intent(getBaseContext(),DashActivity.class);
                    startActivity(dashIntent);
                    finish();
                } else {
                    SetupActivity.this.run(baglan);
                    Toast.makeText(SetupActivity.this, "Baglantıda Hata oluştu !", Toast.LENGTH_SHORT).show();
                }

            }
        }, 31000);


    }
    Boolean conResult = true;
    public void sendData(){
        String url = "http://10.10.10.2/online//"+currentWifi+":"+key+":"+ mAuth.getCurrentUser().getUid() +":";

        OkHttpClient client = new OkHttpClient();
        Request req = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(req);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                conResult = false;

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
            }
        });
    }


}