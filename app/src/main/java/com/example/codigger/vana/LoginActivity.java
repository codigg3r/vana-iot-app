package com.example.codigger.vana;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    public  FirebaseAuth mAuth;

    EditText email_view;
    EditText pass_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email_view = findViewById(R.id.email);
        pass_view  = findViewById(R.id.pass);

        boolean signed = true;
        mAuth = FirebaseAuth.getInstance();
        //check if already logged or not.
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

    public void reset_pass(View view) {
        String email = email_view.getText().toString();
        if (email.isEmpty() ){
            Toast.makeText(this, "Lütfen Email Adresinizi Giriniz.", Toast.LENGTH_SHORT).show();
            return;
        }
        mAuth.sendPasswordResetEmail(email);
        Toast.makeText(this, "Email gönderildi.", Toast.LENGTH_SHORT).show();
    }

    public void sign_in(View view) {

        String email = email_view.getText().toString();
        String password = pass_view.getText().toString();
        if (email.isEmpty() | password.isEmpty()){
            Toast.makeText(this, "Email veya Şifre Eksik Girildi.", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(LoginActivity.this, "Giriş Başarılı!", Toast.LENGTH_SHORT).show();
                            Intent dashIntent = new Intent(getBaseContext(),ActivationActivity.class);
                            startActivity(dashIntent);
                            finish();
                        } else {


                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Email veya Şifre Hatalı.", Toast.LENGTH_SHORT).show();
                        }
                        // ...
                    }
                });


    }
}
