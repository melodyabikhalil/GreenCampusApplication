package com.example.greencampus;

import android.app.Activity;
import android.content.Intent;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import model.Class;
import model.User;

public class LightFragment extends Fragment{

    TextView tvLightState;
    DatabaseReference firebaseDatabase;
    Button buttonTurnOn, buttonTurnOff;
    String state;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Light");
        Class classe = getClassInfo();

        //Outlets
        View v=inflater.inflate(R.layout.fragment_light, container, false);
        buttonTurnOn = (Button) v.findViewById(R.id.buttonTurnOn);
        buttonTurnOff = (Button) v.findViewById(R.id.buttonTurnOff);
        tvLightState = (TextView) v.findViewById(R.id.tvLightState);
        state="";

        firebaseDatabase = FirebaseDatabase.getInstance().getReference();

        firebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String className = getClassName();
                saveClassState((HashMap<String, Object>) dataSnapshot.child("classes").child(className).getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        System.out.println("getIsOn 3am bt redele: "+ classe.getIsOn());
        if (classe.getIsOn().equals("0")){
            tvLightState.setText("Your class light is OFF");
            state = "OFF";
        }
        if (classe.getIsOn().equals("1")){
            tvLightState.setText("Your class light is ON");
            state="ON";
        }

        buttonTurnOn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("kabasnaaa on");
                if(state.equals("OFF")){
                    changeClassState("0");
                }
                tvLightState.setText("Your class light is ON");
            }
        });


        buttonTurnOff.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(state.equals("ON")){
                    changeClassState("1");
                }
                tvLightState.setText("Your class light is OFF");
            }
        });
        return v;
    }


    public void changeClassState(String newState) {
        String classeName = getClassInfo().getClassName();
        System.out.println("classame: "+classeName);
        firebaseDatabase.child("classes").child(classeName).child("isOn").setValue(newState);
        saveNewClassState(newState);
    }

    public Class getClassInfo(){
        int mode = Activity.MODE_PRIVATE;
        SharedPreferences mySharedPreferences ;
        mySharedPreferences=this.getActivity().getSharedPreferences("UserInfo",mode);
        String className = mySharedPreferences.getString("userClass","");
        String classState = mySharedPreferences.getString("classState","");
        return new Class(className,classState);
    }

    public void saveNewClassState(String newState){
        int mode= Activity.MODE_PRIVATE;
        SharedPreferences mySharedPreferences;
        mySharedPreferences=this.getActivity().getSharedPreferences("UserInfo",mode);
        SharedPreferences.Editor editor= mySharedPreferences.edit();
        editor.putString("classState",newState);
        editor.commit();
    }

    public String getClassName(){
        int mode = Activity.MODE_PRIVATE;
        SharedPreferences mySharedPreferences ;
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
}