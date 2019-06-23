package com.example.codigger.vana;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class ModeActivity extends AppCompatActivity {
    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();

        if (sharedpreferences.getString("mode","def").equals("online")){
            //start the activity
            Intent onlineActivity = new Intent(this,OnlineActivity.class);
            startActivity(onlineActivity);
            finish();
        }else if (sharedpreferences.getString("mode","def").equals("offline")){
            Intent offlineActivity = new Intent(this,OfflineActivity.class);
            startActivity(offlineActivity);
            finish();
        }
    }

    public void setOfline(View view) {
        editor.putString("mode","offline");
        editor.apply();
        editor.commit();
        //start activity
        Intent offlineActivity = new Intent(this,OfflineActivity.class);
        startActivity(offlineActivity);
        finish();
    }

    public void setOnline(View view) {
        editor.putString("mode","online");
        editor.apply();
        editor.commit();
        //start activity
        Intent onlineActivity = new Intent(this,OnlineActivity.class);
        startActivity(onlineActivity);
        finish();
    }
}
