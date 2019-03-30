package com.example.greencampus;

import android.content.Intent;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignInActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    TextInputEditText etEmail,etPassword;
    TextView tvSignUp, tvInvalidCredentials;
    Button buttonSignIn;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Outlets
        tvSignUp = (TextView) findViewById(R.id.tvSignUp);
        tvInvalidCredentials = (TextView) findViewById(R.id.tvError);
        buttonSignIn = (Button) findViewById(R.id.buttonSignIn);
        etEmail = (TextInputEditText) findViewById(R.id.tietEmail);
        etPassword = (TextInputEditText) findViewById(R.id.tietPassword);

        firebaseAuth = FirebaseAuth.getInstance();

        tvInvalidCredentials.setVisibility(View.INVISIBLE);

        tvSignUp.setPaintFlags(tvSignUp.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
        tvSignUp.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });


        buttonSignIn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                loginUser();
            }
        });
    }

    public void loginUser(){
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        if (email.equals("") || password.equals("")){
            tvInvalidCredentials.setText("Please fill in the required fields");
            tvInvalidCredentials.setVisibility(View.VISIBLE);
        }
        else{

            Toast toast = Toast.makeText(SignInActivity.this, "Please wait..", Toast.LENGTH_SHORT);
            toast.show();

            firebaseAuth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                finish();
                                Intent intent = new Intent(SignInActivity.this, HomeActivity.class);
                                startActivity(intent);
                            }
                            else{
                                tvInvalidCredentials.setText("Invalid credentials");
                                tvInvalidCredentials.setVisibility(View.VISIBLE);
                            }
                    }
            });
        }
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
