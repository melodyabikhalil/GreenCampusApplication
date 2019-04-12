package com.example.greencampus;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import model.DataModel;


public class AdministratorLightFragment extends Fragment {

    View view;
    ListView lvON, lvOFF;
    DataModel data;
    Button signOut;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Class Light State");
        view = inflater.inflate(R.layout.fragment_administrator_light, container, false);

        lvON = (ListView) view.findViewById(R.id.lvON);
        lvOFF = (ListView) view.findViewById(R.id.lvOFF);
        data = DataModel.instance;

//        data.loadOnClasses();
//        data.loadOffClasses();


        List listON=data.getClassesOn();
        ArrayAdapter adapterON = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, listON);
        lvON.setAdapter(adapterON);

        List listOFF=data.getClassesOff();
        ArrayAdapter adapterOFF = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, listOFF);
        lvOFF.setAdapter(adapterOFF);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        signOut = getView().findViewById(R.id.buttonSignOutAdmin);
        lvON = getView().findViewById(R.id.lvON);
        lvOFF = getView().findViewById(R.id.lvOFF);

        signOut.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                signOut();
            }
        });



        lvON.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), DelegateActivity.class);
                String selectedClass = (String) parent.getItemAtPosition(position);
                intent.putExtra("className",selectedClass);
                intent.putExtra("isON","1");
                data.loadDelegateInfo(selectedClass);
                startActivity(intent);
            }
        });

        lvOFF.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), DelegateActivity.class);
                String selectedClass = (String) parent.getItemAtPosition(position);
                intent.putExtra("className",selectedClass);
                intent.putExtra("isON","0");
                data.loadDelegateInfo(selectedClass);
                data.loadPhotoURL(selectedClass);
                startActivity(intent);
            }
        });

    }

    public void signOut(){
        Intent intent = new Intent(getContext(), SignInActivity.class);
        startActivity(intent);
        getActivity().finish();
    }


}
