package com.example.greencampus;

import android.content.Intent;
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
import java.util.Map;

import model.User;

public class SignUpActivity extends AppCompatActivity {

    TextInputEditText etEmail, etPassword, etFirstName, etLastName, etPhoneNumber;
    Button buttonSignUp;
    CheckBox checkboxDelegue;
    Spinner spinnerClasses;
    TextView tvError;
    Toolbar toolbar;

    FirebaseAuth firebaseAuth;
    DatabaseReference firebaseDatabase;

    Globals globals = new Globals();

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
        checkboxDelegue.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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
        );

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
        else {
            if(password.length()<6){
                tvError.setText("Password too short");
                tvError.setVisibility(View.VISIBLE);
            }
            Toast toast = Toast.makeText(SignUpActivity.this, "Please wait..", Toast.LENGTH_SHORT);
            toast.show();

            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast toast = Toast.makeText(SignUpActivity.this, "User created", Toast.LENGTH_SHORT);
                                toast.show();
                                Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                                startActivity(intent);
                                globals.setUser(firebaseAuth.getCurrentUser().getUid());
                                createUserData();
                                finish();
                            } else {
                                tvError.setText("Invalid email");
                                tvError.setVisibility(View.VISIBLE);
                            }
                        }
                    });
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
        if (email.equals("") || password.equals("") || firstName.equals("") || lastName.equals("") || phoneNumber.equals("")) {
            tvError.setText("Please fill in the required fields");
            tvError.setVisibility(View.VISIBLE);
        } else {
            if (checkboxDelegue.isChecked()) {
                role = "1";
                user = new User(globals.getUser(), firstName, lastName, phoneNumber, role, classe);
            } else {
                role = "0";
                user = new User(globals.getUser(), firstName, lastName, phoneNumber, role);
            }
            firebaseDatabase.child("users").child(user.getID()).setValue(user);
        }
    }

    public void collectClasses(Map<String, Object> classes){
        ArrayList<String> classesList = new ArrayList<>();

        for (Map.Entry<String, Object> entry : classes.entrySet()){
            String singleClassName = entry.getKey();
            classesList.add(singleClassName);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, classesList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerClasses.setAdapter(adapter);
        spinnerClasses.setEnabled(false);
        spinnerClasses.setClickable(false);
    }
}
