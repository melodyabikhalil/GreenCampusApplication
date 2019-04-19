package com.example.greencampus;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import model.DataModel;
import model.Helper;
import model.User;

import static model.Helper.BROADCAST_ACTION_ADMINS_LOADED;
import static model.Helper.BROADCAST_ACTION_LOGIN_FAILED;
import static model.Helper.BROADCAST_ACTION_LOGIN_SUCCESS;
import static model.Helper.BROADCAST_ACTION_SIGN_UP_FAILED;
import static model.Helper.BROADCAST_ACTION_SIGN_UP_SUCCESS;
import static model.Helper.BROADCAST_ACTION_SIGN_UP_USER_DATA_FAILED;
import static model.Helper.BROADCAST_ACTION_SIGN_UP_USER_DATA_SUCCESS;
import static model.Helper.BROADCAST_ACTION_USER_CLASS_FAILED;

public class SignUpActivity extends AppCompatActivity {

    TextInputEditText etEmail, etPassword, etFirstName, etLastName, etPhoneNumber;
    Button buttonSignUp;
    CheckBox checkboxDelegue;
    Spinner spinnerClasses;
    TextView tvError;
    Toolbar toolbar;
    String signedUpUserID;
    ProgressDialog progressDialog;

    FirebaseAuth firebaseAuth;
    DatabaseReference firebaseDatabase;
    DataModel data;

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if(action.equals(BROADCAST_ACTION_SIGN_UP_FAILED)) {
                progressDialog.dismiss();
                tvError.setText("Invalid email");
                tvError.setVisibility(View.VISIBLE);
            }

            if(action.equals(Helper.BROADCAST_ACTION_SIGN_UP_SUCCESS)){
                progressDialog.dismiss();
                Intent intentToSignIn = new Intent(SignUpActivity.this, SignInActivity.class);
                startActivity(intentToSignIn);
                signedUpUserID = firebaseAuth.getCurrentUser().getUid();
                createUserData();
                finish();
            }

            if(action.equals(Helper.BROADCAST_ACTION_SIGN_UP_USER_DATA_FAILED)){
                Toast toast = Toast.makeText(SignUpActivity.this, "Could not create user.", Toast.LENGTH_SHORT);
                toast.show();
            }

            if(action.equals(Helper.BROADCAST_ACTION_SIGN_UP_USER_DATA_SUCCESS)){
                Toast toast = Toast.makeText(SignUpActivity.this, "User created successfully.", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //Outlets
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        spinnerClasses = (Spinner) findViewById(R.id.spinnerClasses);
        checkboxDelegue = (CheckBox) findViewById(R.id.checkBoxDelegue);
        buttonSignUp = (Button) findViewById(R.id.buttonSignUp);
        etEmail = (TextInputEditText) findViewById(R.id.tietNewEmail);
        etPassword = (TextInputEditText) findViewById(R.id.tietNewPassword);
        etFirstName = (TextInputEditText) findViewById(R.id.tietFirstName);
        etLastName = (TextInputEditText) findViewById(R.id.tietLastName);
        etPhoneNumber = (TextInputEditText) findViewById(R.id.tietPhone);
        tvError = (TextView) findViewById(R.id.tvError);

        IntentFilter filter = new IntentFilter();
        filter.addAction(BROADCAST_ACTION_SIGN_UP_FAILED);
        filter.addAction(BROADCAST_ACTION_SIGN_UP_SUCCESS);
        filter.addAction(BROADCAST_ACTION_SIGN_UP_USER_DATA_FAILED);
        filter.addAction(BROADCAST_ACTION_SIGN_UP_USER_DATA_SUCCESS);

        filter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(receiver, filter);

        data = DataModel.instance;
        progressDialog = new ProgressDialog(SignUpActivity.this);
        signedUpUserID = "";
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance().getReference();

        //Set layouts
        setSupportActionBar(toolbar);
        setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        tvError.setVisibility(View.INVISIBLE);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SignInActivity.class));
            }
        });

        //Back button
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.GRAY);
        }

        firebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                collectClasses((Map<String,Object>) dataSnapshot.child("classes").getValue());            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Enable/Disable Dropdown
        /*checkboxDelegue.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                                       @Override
                                                       public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                           if (!buttonView.isChecked()) {
                                                               spinnerClasses.setEnabled(false);
                                                               spinnerClasses.setClickable(false);
                                                           } else {
                                                               spinnerClasses.setEnabled(true);
                                                               spinnerClasses.setClickable(true);
                                                           }
                                                       }
                                                   }
        );*/

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
              createUser();
            }
        });
    }

    public void createUser() {
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        String firstName = etFirstName.getText().toString();
        String lastName = etLastName.getText().toString();
        String phoneNumber = etPhoneNumber.getText().toString();

        if (email.equals("") || password.equals("") || firstName.equals("") || lastName.equals("") || phoneNumber.equals("")) {
            tvError.setText("Please fill in the required fields");
            tvError.setVisibility(View.VISIBLE);
        }
        else if (password.length()<6) {
            tvError.setText("Password too short");
            tvError.setVisibility(View.VISIBLE);
        }
        else{
            progressDialog.setMessage("Please wait..");
            progressDialog.show();
            data.signUpUser(email,password);

//            firebaseAuth.createUserWithEmailAndPassword(email, password)
//                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                        @Override
//                        public void onComplete(@NonNull Task<AuthResult> task) {
//                            if (task.isSuccessful()) {
//                                Toast toast = Toast.makeText(SignUpActivity.this, "User created", Toast.LENGTH_SHORT);
//                                toast.show();
//                                Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
//                                startActivity(intent);
//                                signedUpUserID = firebaseAuth.getCurrentUser().getUid();
//                                createUserData();
//                                finish();
//                            } else {
//                                tvError.setText("Invalid email");
//                                tvError.setVisibility(View.VISIBLE);
//                            }
//                        }
//                    });
        }
    }

    public void createUserData() {
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        String firstName = etFirstName.getText().toString();
        String lastName = etLastName.getText().toString();
        String phoneNumber = etPhoneNumber.getText().toString();
        String classe = spinnerClasses.getSelectedItem().toString();
        String role;
        User user;
        if (email.equals("") || password.equals("") || firstName.equals("") || lastName.equals("") || phoneNumber.equals("") || classe.equals("")) {
            tvError.setText("Please fill in the required fields");
            tvError.setVisibility(View.VISIBLE);
        } else {
            if (checkboxDelegue.isChecked()) {
                role = "1";
                user = new User(signedUpUserID, firstName, lastName, phoneNumber, role, classe);
            } else {
                role = "0";
                user = new User(signedUpUserID, firstName, lastName, phoneNumber, role, classe);
            }
//            firebaseDatabase.child("users").child(user.getID()).setValue(user);
            data.signUpUserData(user);
        }
    }

    public void collectClasses(Map<String, Object> classes){
        ArrayList<String> classesList = new ArrayList<>();

        for (Map.Entry<String, Object> entry : classes.entrySet()){
            String singleClassName = entry.getKey();
            classesList.add(singleClassName);
        }
        Collections.sort(classesList);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, classesList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerClasses.setAdapter(adapter);
//        spinnerClasses.setEnabled(false);
//        spinnerClasses.setClickable(false);
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
        filter.addAction(BROADCAST_ACTION_SIGN_UP_FAILED);
        filter.addAction(BROADCAST_ACTION_SIGN_UP_SUCCESS);
        filter.addAction(BROADCAST_ACTION_SIGN_UP_USER_DATA_FAILED);
        filter.addAction(BROADCAST_ACTION_SIGN_UP_USER_DATA_SUCCESS);

        filter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(receiver, filter);
    }
}
