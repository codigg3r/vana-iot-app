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

public class SignupActivity extends AppCompatActivity {

    EditText email_view;
    EditText pass_1_view;
    EditText pass_2_view;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //--Initialize EditText Views
        email_view   = findViewById(R.id.email);
        pass_1_view  = findViewById(R.id.pass);
        pass_2_view  = findViewById(R.id.pass_2);

        mAuth = FirebaseAuth.getInstance();
    }

    public void sign_up(View view) {

        String email  = email_view.getText().toString();
        String pass_1 = pass_1_view.getText().toString();
        String pass_2 = pass_2_view.getText().toString();

        if (email.isEmpty() | pass_1.isEmpty() | pass_2.isEmpty()){
            Toast.makeText(this, "Email veya Şifre Eksik Girildi.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!pass_1.equals(pass_2)){
            Toast.makeText(this, "Şifreler aynı değil.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!email_view.getText().toString().isEmpty()){
            mAuth.createUserWithEmailAndPassword(email, pass_1)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(SignupActivity.this, "Kayıt Başarılı!", Toast.LENGTH_SHORT).show();
                                Intent activation = new Intent(SignupActivity.this,ActivationActivity.class);
                                startActivity(activation);
                            } else {
                                Toast.makeText(SignupActivity.this, "Kayıt Yapılamadı.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }



}
