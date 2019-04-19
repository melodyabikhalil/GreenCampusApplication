package com.example.greencampus;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import model.DataModel;
import model.Class;
import model.User;

import static model.Helper.BROADCAST_ACTION_CHANGE_CLASS_STATE_FAILED;
import static model.Helper.BROADCAST_ACTION_CHANGE_CLASS_STATE_SUCESS;
import static model.Helper.BROADCAST_ACTION_PROFILE_FAILED;
import static model.Helper.BROADCAST_ACTION_PROFILE_LOADED;
import static model.Helper.BROADCAST_ACTION_UPLOAD_IMAGE_FAILED;
import static model.Helper.BROADCAST_ACTION_UPLOAD_IMAGE_SUCESS;

public class ProfileFragment extends Fragment {

    TextView name, className, classState, phoneNumber;
    Button buttonSignOut;
    View rootView;
    /*FirebaseAuth firebaseAuth;
    DatabaseReference firebaseDatabase;*/
    DataModel data;
//    ProgressDialog progressDialog;
    private User user;
    private Class classe;

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if(action.equals(BROADCAST_ACTION_PROFILE_FAILED)) {
                Toast toast = Toast.makeText(getContext(), "Could not load profile. Please try again later", Toast.LENGTH_LONG);
                toast.show();
            }

            if(action.equals(BROADCAST_ACTION_PROFILE_LOADED)){
                user = data.getLocalUserInfo(GreenCampusApplication.getContext());
                classe = data.getLocalClassInfo(GreenCampusApplication.getContext());
                showProfileInfo();
            }

        }
    };


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        name = rootView.findViewById(R.id.tvName);
        className = rootView.findViewById(R.id.tvClassName);
        classState = rootView.findViewById(R.id.tvClassState);
        phoneNumber = rootView.findViewById(R.id.tvPhoneNumber);
        buttonSignOut = rootView.findViewById(R.id.buttonSignOut);
//        progressDialog = new ProgressDialog(getActivity());
        /*firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();*/

        data = data.instance;

        IntentFilter filter = new IntentFilter();
        filter.addAction(BROADCAST_ACTION_PROFILE_FAILED);
        filter.addAction(BROADCAST_ACTION_PROFILE_LOADED);

        filter.addCategory(Intent.CATEGORY_DEFAULT);
        getActivity().registerReceiver(receiver, filter);

        buttonSignOut.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                signOut();
            }
        });

        /*firebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //String className = getClassName();
                //saveClassState((HashMap<String, Object>) dataSnapshot.child("classes").child(className).getValue());
                String className = data.instance.getClassName(getActivity());
                data.saveClassState(getActivity(),(HashMap<String, Object>) dataSnapshot.child("classes").child(className).getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/

//        progressDialog.setMessage("Loading. Please wait..");
//        progressDialog.show();
        data.loadProfile(this.getActivity());

        /*User user = getUserInfo();
        Class classe = getClassInfo();*/


        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Profile");
        return rootView;

    }

    /*public String getUserId(){
        int mode = Activity.MODE_PRIVATE;
        SharedPreferences  mySharedPreferences ;
        mySharedPreferences=this.getActivity().getSharedPreferences("UserInfo",mode);
        String userId = mySharedPreferences.getString("userID","");
        return userId;
    }

    public String getClassName(){
        int mode = Activity.MODE_PRIVATE;
        SharedPreferences  mySharedPreferences ;
        mySharedPreferences=this.getActivity().getSharedPreferences("UserInfo",mode);
        String userClass = mySharedPreferences.getString("userClass","");
        return userClass;
    }

    public void saveClassState(HashMap<String, Object> classInfo){
        int mode= Activity.MODE_PRIVATE;
        SharedPreferences mySharedPreferences;
        mySharedPreferences=this.getActivity().getSharedPreferences("UserInfo",mode);
        SharedPreferences.Editor editor= mySharedPreferences.edit();

        String classState = (String) classInfo.get("isOn");

        editor.putString("classState",classState);

        editor.commit();
    }

    public User getUserInfo(){
        int mode = Activity.MODE_PRIVATE;
        SharedPreferences  mySharedPreferences ;
        mySharedPreferences=this.getActivity().getSharedPreferences("UserInfo",mode);
        String userId = mySharedPreferences.getString("userID","");
        String userFirstName = mySharedPreferences.getString("userFirstName", "");
        String userLastName = mySharedPreferences.getString("userLastName","");
        String userPhoneNumber = mySharedPreferences.getString("userPhoneNumber","");
        String userRole = mySharedPreferences.getString("userRole","");
        String userClass = mySharedPreferences.getString("userClass","");

        User user = new User(userId, userFirstName, userLastName, userPhoneNumber, userRole, userClass);
        return user;
    }

    public Class getClassInfo(){
        int mode = Activity.MODE_PRIVATE;
        SharedPreferences  mySharedPreferences ;
        mySharedPreferences=this.getActivity().getSharedPreferences("UserInfo",mode);

        String className = mySharedPreferences.getString("userClass","");
        String classState = mySharedPreferences.getString("classState","");

       Class classe = new Class(className,classState);
       return classe;
    }
*/
    public void signOut(){
        Intent intent = new Intent(getActivity(), SignInActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    public void showProfileInfo(){
        name.setText(user.getFirstName()+" "+user.getLastName());
        className.setText(user.getClassID());
        if(classe.getIsOn().equals("1")){
            classState.setText("ON");
        }
        if(classe.getIsOn().equals("0")){
            classState.setText("OFF");
        }

        phoneNumber.setText(user.getPhoneNumber());
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(receiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BROADCAST_ACTION_PROFILE_FAILED);
        filter.addAction(BROADCAST_ACTION_PROFILE_LOADED);

        filter.addCategory(Intent.CATEGORY_DEFAULT);
        getActivity().registerReceiver(receiver, filter);
    }
}