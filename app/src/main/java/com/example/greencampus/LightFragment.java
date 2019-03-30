package com.example.greencampus;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class LightFragment extends Fragment{

    TextView tvLightState;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Light");

        //Outlets

        View v=inflater.inflate(R.layout.fragment_light, container, false);
        //check in place of container here you need to use inflated view (`v`) instance
        final Button buttonTurnOn = (Button) v.findViewById(R.id.buttonTurnOn);
        final Button buttonTurnOff = (Button) v.findViewById(R.id.buttonTurnOff);
        tvLightState = (TextView) v.findViewById(R.id.tvLightState);
        //Call isLightOn

        tvLightState.setText("Your class light is ON");

        buttonTurnOn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("kabasnaaa on");
                buttonTurnOn.setEnabled(false);
                buttonTurnOn.setClickable(false);
                buttonTurnOff.setEnabled(true);
                buttonTurnOff.setClickable(true);
                tvLightState.setText("Your class light is ON");
            }
        });


        buttonTurnOff.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonTurnOn.setEnabled(true);
                buttonTurnOn.setClickable(true);
                buttonTurnOff.setEnabled(false);
                buttonTurnOff.setClickable(false);
                tvLightState.setText("Your class light is OFF");
            }
        });

        return inflater.inflate(R.layout.fragment_light, null);
    }



}