package com.example.greencampus;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class SignInActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    TextInputEditText etEmail,etPassword;
    TextView tvSignUp, tvInvalidCredentials;
    Button buttonSignIn;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        clearSharedPreferences();

        //Outlets
        tvSignUp = (TextView) findViewById(R.id.tvSignUp);
        tvInvalidCredentials = (TextView) findViewById(R.id.tvError);
        buttonSignIn = (Button) findViewById(R.id.buttonSignIn);
        etEmail = (TextInputEditText) findViewById(R.id.tietEmail);
        etPassword = (TextInputEditText) findViewById(R.id.tietPassword);

        firebaseAuth = FirebaseAuth.getInstance();

        tvInvalidCredentials.setVisibility(View.INVISIBLE);

        int mode= Activity.MODE_PRIVATE;
        SharedPreferences mySharedPreferences;
        mySharedPreferences=getSharedPreferences("UserInfo",mode);
        SharedPreferences.Editor editor= mySharedPreferences.edit();
        editor.putString("userID","");
        editor.commit();

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
                                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                                String userId = currentUser.getUid();

                                int mode= Activity.MODE_PRIVATE;
                                SharedPreferences mySharedPreferences;
                                mySharedPreferences=getSharedPreferences("UserInfo",mode);
                                SharedPreferences.Editor editor= mySharedPreferences.edit();
                                editor.putString("userID",userId);
                                editor.commit();

                                DatabaseReference firebaseDatabase;
                                firebaseDatabase = FirebaseDatabase.getInstance().getReference();

                                firebaseDatabase.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        String userId = getUserId();
                                        collectUserInfo((HashMap<String, Object>) dataSnapshot.child("users").child(userId).getValue());
                                        String className = getClassName();
                                        saveClassState((HashMap<String, Object>) dataSnapshot.child("classes").child(className).getValue());
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                                //TO REMOVE:
                                mySharedPreferences=getSharedPreferences("UserInfo",mode);
                                String classState = mySharedPreferences.getString("classState","");

                                System.out.println("current class state is: abel ma rouh 3al profile: "+classState);
                                //
                                Intent intent = new Intent(SignInActivity.this, HomeActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            else{
                                tvInvalidCredentials.setText("Invalid credentials");
                                tvInvalidCredentials.setVisibility(View.VISIBLE);
                                int mode= Activity.MODE_PRIVATE;
                                SharedPreferences mySharedPreferences;
                                mySharedPreferences=getSharedPreferences("UserInfo",mode);
                                SharedPreferences.Editor editor= mySharedPreferences.edit();
                                editor.putString("userID","");
                                editor.commit();
                            }
                    }
            });
        }
    }

    public String getUserId(){
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
        editor.commit();
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
