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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import model.Admin;
import model.DataModel;
import model.Helper;

import static model.Helper.*;

public class SignInActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    TextInputEditText etEmail,etPassword;
    TextView tvSignUp, tvInvalidCredentials;
    Button buttonSignIn;
//    FirebaseAuth firebaseAuth;
    DataModel data;
    boolean isAdmin;
    List<Admin> admins;
    ProgressDialog progressDialog;

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if(action.equals(BROADCAST_ACTION_ADMINS_LOADED)) {
               admins = data.getAdmins();
            }

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
        data.clearSharedPreferences(this);

        //Outlets
        tvSignUp = (TextView) findViewById(R.id.tvSignUp);
        tvInvalidCredentials = (TextView) findViewById(R.id.tvError);
        buttonSignIn = (Button) findViewById(R.id.buttonSignIn);
        etEmail = (TextInputEditText) findViewById(R.id.tietEmail);
        etPassword = (TextInputEditText) findViewById(R.id.tietPassword);
        admins = new ArrayList();
        progressDialog = new ProgressDialog(SignInActivity.this);

//        firebaseAuth = FirebaseAuth.getInstance();
        isAdmin = false;

        data.clearSharedPreferences(SignInActivity.this);

        tvInvalidCredentials.setVisibility(View.INVISIBLE);

        SharedPreferences mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(GreenCampusApplication.getContext());
        SharedPreferences.Editor editor= mySharedPreferences.edit();
        editor.putString("userID","");
        editor.apply();

        data.getAdministrators();

        IntentFilter filter = new IntentFilter();
        filter.addAction(BROADCAST_ACTION_ADMINS_LOADED);
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

    public void loadClassesForAdministrator(){
        data.setClassesOn(new ArrayList());
        data.loadOnClasses();

        data.setClassesOff(new ArrayList());
        data.loadOffClasses();
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

//            final boolean finalIsAdmin = isAdmin;
            progressDialog.setMessage("Please wait..");
            progressDialog.show();
            data.loginUser(email,password);
            /*firebaseAuth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
//                                finish();
                                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                                String userId = currentUser.getUid();

                                Toast toast = Toast.makeText(SignInActivity.this, "Please wait..", Toast.LENGTH_SHORT);
                                toast.show();

                                data.saveUserIDLocally(SignInActivity.this, userId);

                                DatabaseReference firebaseDatabase;
                                firebaseDatabase = FirebaseDatabase.getInstance().getReference();

                                firebaseDatabase.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        String userId = data.getLocalUserId(SignInActivity.this);
                                        data.saveFirebaseUserInfoLocally(SignInActivity.this,(HashMap<String, Object>) dataSnapshot.child("users").child(userId).getValue());
                                        String className = data.getLocalClassName(SignInActivity.this);
                                        data.saveFirebaseClassStateLocally(SignInActivity.this, (HashMap<String, Object>) dataSnapshot.child("classes").child(className).getValue());
                                        startActivity(makeIntent());
                                        finish();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            } else {
                                tvInvalidCredentials.setText("Invalid credentials");
                                tvInvalidCredentials.setVisibility(View.VISIBLE);
                            }

                        }

                    });*/
        }
    }

    public Intent makeIntent(){
        Intent intent;
        intent = new Intent(SignInActivity.this, HomeActivity.class);
        if(isAdmin){
//            data.loadReports();
//            loadClassesForAdministrator();
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
        filter.addAction(BROADCAST_ACTION_ADMINS_LOADED);
        filter.addAction(BROADCAST_ACTION_LOGIN_SUCCESS);
        filter.addAction(BROADCAST_ACTION_LOGIN_FAILED);
        filter.addAction(BROADCAST_ACTION_USER_CLASS_FAILED);

        filter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(receiver, filter);
    }

    /*public String getUserId(){
        int mode = Activity.MODE_PRIVATE;
        SharedPreferences  mySharedPreferences ;
        mySharedPreferences=getSharedPreferences("UserInfo",mode);
        String userId = mySharedPreferences.getString("userID","");
        return userId;
    }

    public void collectUserInfo(HashMap<String, Object> userInfo){
        int mode= Activity.MODE_PRIVATE;
        SharedPreferences mySharedPreferences;
        mySharedPreferences=getSharedPreferences("UserInfo",mode);
        SharedPreferences.Editor editor= mySharedPreferences.edit();

        String firstname = (String) userInfo.get("firstName");
        String lastname = (String) userInfo.get("lastName");
        String phonenumber = (String) userInfo.get("phoneNumber");
        String role = (String) userInfo.get("role");
        System.out.println("The role is: "+role);
        String classe;

        if(role.equals("1")){
            classe = (String) userInfo.get("classID");
            System.out.println("sayavna el userClass: "+ classe);
            editor.putString("userClass",classe);
        }

        editor.putString("userFirstName",firstname);
        editor.putString("userLastName",lastname);
        editor.putString("userPhoneNumber",phonenumber);
        editor.putString("userRole",role);

        editor.commit();
    }

    public void clearSharedPreferences(){
        int mode= Activity.MODE_PRIVATE;
        SharedPreferences mySharedPreferences;
        mySharedPreferences=getSharedPreferences("UserInfo",mode);
        SharedPreferences.Editor editor= mySharedPreferences.edit();
        editor.clear();
        editor.commit();
    }

    public String getClassName(){
        int mode = Activity.MODE_PRIVATE;
        SharedPreferences  mySharedPreferences ;
        mySharedPreferences=getSharedPreferences("UserInfo",mode);
        String userClass = mySharedPreferences.getString("userClass","");
        return userClass;
    }

    public void saveClassState(HashMap<String, Object> classInfo){
        int mode= Activity.MODE_PRIVATE;
        SharedPreferences mySharedPreferences;
        mySharedPreferences=getSharedPreferences("UserInfo",mode);
        SharedPreferences.Editor editor= mySharedPreferences.edit();

        String classState = (String) classInfo.get("isOn");
        editor.putString("classState",classState);

        String classStatee = mySharedPreferences.getString("classState","");
        System.out.println("state li now sayyavta: "+classState);

        editor.commit();
    }
*/

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
