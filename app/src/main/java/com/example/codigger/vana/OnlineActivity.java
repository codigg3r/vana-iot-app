package com.example.codigger.vana;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

public class OnlineActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    boolean signed = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online);
        mAuth = FirebaseAuth.getInstance();
        try {
            mAuth.getCurrentUser().getEmail();
        }catch (Exception e){
            signed = false;
        }

        if (signed){
            if (mAuth.getCurrentUser().isEmailVerified()) {
                Intent dashIntent = new Intent(getBaseContext(), DashActivity.class);
                startActivity(dashIntent);
                finish();
            }else{
                Intent verify = new Intent(getBaseContext(), ActivationActivity.class);
                startActivity(verify);
            }

        }

    }

    public void sign_in(View view) {
        Intent loginActivity = new Intent(this,LoginActivity.class);
        startActivity(loginActivity);
    }

    public void sign_up(View view) {
        Intent signupActivity = new Intent(this,SignupActivity.class);
        startActivity(signupActivity);
    }
}
