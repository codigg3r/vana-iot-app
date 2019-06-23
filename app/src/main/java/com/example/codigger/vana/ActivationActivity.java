package com.example.codigger.vana;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;

public class ActivationActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    LottieAnimationView animationView2;
    LottieAnimationView animationView1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activation);
        mAuth = FirebaseAuth.getInstance();
        animationView1 = findViewById(R.id.animation_view);
        animationView1.setAnimation("preloader.json");
        animationView1.playAnimation();

        if (!mAuth.getCurrentUser().isEmailVerified()){
            mAuth.getCurrentUser().sendEmailVerification();
            new verify().execute();

        }else{
            Intent setTimeIntent = new Intent(getBaseContext(),DashActivity.class);
           startActivity(setTimeIntent);
            finish();
        }

    }

    class verify extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... voids) {


            while (!mAuth.getCurrentUser().isEmailVerified()){
                try {
                    mAuth.getCurrentUser().reload();
                    Thread.sleep(1000);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            setAnim();
        }
    }

    public void setAnim(){
        mAuth.getCurrentUser().reload();
        animationView2 = findViewById(R.id.animation_view2);
        animationView1.setVisibility(View.INVISIBLE);
        animationView2.setVisibility(View.VISIBLE);
        animationView2.playAnimation();

        final Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
               Intent dash = new Intent(getBaseContext(),DashActivity.class);
               startActivity(dash);
               finish();

            }
        }, 1500);

    }
}
