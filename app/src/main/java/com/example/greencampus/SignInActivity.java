package com.example.greencampus;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.preference.PreferenceManager;
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


import model.DataModel;
import model.Helper;

import static model.Helper.*;

public class SignInActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    TextInputEditText etEmail,etPassword;
    TextView tvSignUp, tvInvalidCredentials;
    Button buttonSignIn;
    DataModel data;
    boolean isAdmin;
    ProgressDialog progressDialog;

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if(action.equals(Helper.BROADCAST_ACTION_LOGIN_SUCCESS)){
                progressDialog.dismiss();
                startActivity(makeIntent());
                finish();
            }

            if(action.equals(Helper.BROADCAST_ACTION_LOGIN_FAILED)){
                progressDialog.dismiss();
                tvInvalidCredentials.setText("Invalid credentials");
                tvInvalidCredentials.setVisibility(View.VISIBLE);
            }

            if(action.equals(Helper.BROADCAST_ACTION_USER_CLASS_FAILED)){
                progressDialog.dismiss();
                Toast toast = Toast.makeText(SignInActivity.this, "Error, please try again later.", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        data= DataModel.instance;

        //Outlets
        tvSignUp = (TextView) findViewById(R.id.tvSignUp);
        tvInvalidCredentials = (TextView) findViewById(R.id.tvError);
        buttonSignIn = (Button) findViewById(R.id.buttonSignIn);
        etEmail = (TextInputEditText) findViewById(R.id.tietEmail);
        etPassword = (TextInputEditText) findViewById(R.id.tietPassword);
        progressDialog = new ProgressDialog(SignInActivity.this);

        isAdmin = false;

        tvInvalidCredentials.setVisibility(View.INVISIBLE);

        SharedPreferences mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(GreenCampusApplication.getContext());
        SharedPreferences.Editor editor= mySharedPreferences.edit();
        editor.putString("userID","");
        editor.apply();


        IntentFilter filter = new IntentFilter();
        filter.addAction(BROADCAST_ACTION_LOGIN_SUCCESS);
        filter.addAction(BROADCAST_ACTION_LOGIN_FAILED);
        filter.addAction(BROADCAST_ACTION_USER_CLASS_FAILED);

        filter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(receiver, filter);

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

        for(int i =0; i<adminsEmailsList.size();++i){
            if(email.equals(adminsEmailsList.get(i))){
                isAdmin=true;
            }
        }

        if (email.equals("") || password.equals("")){
            tvInvalidCredentials.setText("Please fill in the required fields");
            tvInvalidCredentials.setVisibility(View.VISIBLE);
        }
        else{
            progressDialog.setMessage("Please wait..");
            progressDialog.show();
            data.loginUser(email,password);
        }
    }

    public Intent makeIntent(){
        Intent intent;
        intent = new Intent(SignInActivity.this, HomeActivity.class);
        if(isAdmin){
            intent.putExtra("role","2");
        }
        else{
            intent.putExtra("role","1");
        }
        return intent;
    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(receiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BROADCAST_ACTION_LOGIN_SUCCESS);
        filter.addAction(BROADCAST_ACTION_LOGIN_FAILED);
        filter.addAction(BROADCAST_ACTION_USER_CLASS_FAILED);

        filter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(receiver, filter);
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast toast = Toast.makeText(GreenCampusApplication.getContext(),"No internet connection. Please make sure you are connected to Wi-Fi or Mobile network.", Toast.LENGTH_LONG);
        toast.show();
    }
}
