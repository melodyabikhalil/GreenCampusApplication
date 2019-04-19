package com.example.greencampus;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import model.DataModel;

import static model.Helper.BROADCAST_ACTION_OFF_CLASSES_LOADED_FAILED;
import static model.Helper.BROADCAST_ACTION_OFF_CLASSES_LOADED_SUCCESS;
import static model.Helper.BROADCAST_ACTION_ON_CLASSES_LOADED_FAILED;
import static model.Helper.BROADCAST_ACTION_ON_CLASSES_LOADED_SUCCESS;
import static model.Helper.BROADCAST_ACTION_PROFILE_FAILED;
import static model.Helper.BROADCAST_ACTION_PROFILE_LOADED;


public class AdministratorLightFragment extends Fragment {

    View view;
    ListView lvON, lvOFF;
    DataModel data;
    Button signOut;

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if(action.equals(BROADCAST_ACTION_OFF_CLASSES_LOADED_FAILED)) {
                Toast toast = Toast.makeText(GreenCampusApplication.getContext(), "Could not load OFF classes. Please try again later", Toast.LENGTH_LONG);
                toast.show();
            }

            if(action.equals(BROADCAST_ACTION_OFF_CLASSES_LOADED_SUCCESS)){
                listOFF= new ArrayList();
                listOFF=data.getClassesOff();
                ArrayAdapter adapterOFF = new ArrayAdapter(GreenCampusApplication.getContext(), android.R.layout.simple_list_item_1, listOFF);
                lvOFF.setAdapter(adapterOFF);
            }

            if(action.equals(BROADCAST_ACTION_ON_CLASSES_LOADED_FAILED)) {
                Toast toast = Toast.makeText(GreenCampusApplication.getContext(), "Could not load ON classes. Please try again later", Toast.LENGTH_LONG);
                toast.show();
            }

            if(action.equals(BROADCAST_ACTION_ON_CLASSES_LOADED_SUCCESS)){
                listON= new ArrayList();
                listON=data.getClassesOn();
                ArrayAdapter adapterON = new ArrayAdapter(GreenCampusApplication.getContext(), android.R.layout.simple_list_item_1, listON);
                lvON.setAdapter(adapterON);
            }

        }
    };

    private List listON;
    private List listOFF;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Classes");
        view = inflater.inflate(R.layout.fragment_administrator_light, container, false);

        lvON = (ListView) view.findViewById(R.id.lvON);
        lvOFF = (ListView) view.findViewById(R.id.lvOFF);
        listOFF= new ArrayList();
        listON= new ArrayList();

        data = DataModel.instance;
//        data.addListener(this);

//        data.loadOnClasses();
//        data.loadOffClasses();

        IntentFilter filter = new IntentFilter();
        filter.addAction(BROADCAST_ACTION_OFF_CLASSES_LOADED_FAILED);
        filter.addAction(BROADCAST_ACTION_OFF_CLASSES_LOADED_SUCCESS);
        filter.addAction(BROADCAST_ACTION_ON_CLASSES_LOADED_FAILED);
        filter.addAction(BROADCAST_ACTION_ON_CLASSES_LOADED_SUCCESS);

        filter.addCategory(Intent.CATEGORY_DEFAULT);
        getActivity().registerReceiver(receiver, filter);

        data.loadClassesForAdmin();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
//        data.loadOnClasses();
//        data.loadOffClasses();
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


//    @Override
//    public void classesDataHasChanged() {
//        lvON = getView().findViewById(R.id.lvON);
//        List listON=data.getClassesOn();
//        ArrayAdapter adapterON = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, listON);
//        lvON.setAdapter(adapterON);
//
//        lvOFF = getView().findViewById(R.id.lvOFF);
//        List listOFF=data.getClassesOff();
//        ArrayAdapter adapterOFF = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, listOFF);
//        lvOFF.setAdapter(adapterOFF);
//    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(receiver);
    }

    @Override
    public void onResume() {
        super.onResume();

        IntentFilter filter = new IntentFilter();
        filter.addAction(BROADCAST_ACTION_OFF_CLASSES_LOADED_FAILED);
        filter.addAction(BROADCAST_ACTION_OFF_CLASSES_LOADED_SUCCESS);
        filter.addAction(BROADCAST_ACTION_ON_CLASSES_LOADED_FAILED);
        filter.addAction(BROADCAST_ACTION_ON_CLASSES_LOADED_SUCCESS);

        filter.addCategory(Intent.CATEGORY_DEFAULT);
        getActivity().registerReceiver(receiver, filter);
    }
}
