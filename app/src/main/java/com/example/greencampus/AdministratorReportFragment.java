package com.example.greencampus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import model.DataModel;

import static model.Helper.BROADCAST_ACTION_OFF_CLASSES_LOADED_FAILED;
import static model.Helper.BROADCAST_ACTION_OFF_CLASSES_LOADED_SUCCESS;
import static model.Helper.BROADCAST_ACTION_ON_CLASSES_LOADED_FAILED;
import static model.Helper.BROADCAST_ACTION_ON_CLASSES_LOADED_SUCCESS;
import static model.Helper.BROADCAST_ACTION_REPORTS_LOADED_FAILED;
import static model.Helper.BROADCAST_ACTION_REPORTS_LOADED_SUCCESS;

public class AdministratorReportFragment extends Fragment {

    View view;
    ListView lvReports;
    DataModel data;
    List reports;

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if(action.equals(BROADCAST_ACTION_REPORTS_LOADED_FAILED)) {
                Toast toast = Toast.makeText(GreenCampusApplication.getContext(), "Could not load reports. Please try again later", Toast.LENGTH_LONG);
                toast.show();
            }

            if(action.equals(BROADCAST_ACTION_REPORTS_LOADED_SUCCESS)){
                reports = data.getReports();
                ArrayAdapter adapterReports = new ArrayAdapter(GreenCampusApplication.getContext(), android.R.layout.simple_list_item_1, reports);
                lvReports.setAdapter(adapterReports);
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Reports");
        view = inflater.inflate(R.layout.fragment_administrator_report, container, false);

        lvReports = view.findViewById(R.id.lvReports);
        data = DataModel.instance;

        IntentFilter filter = new IntentFilter();
        filter.addAction(BROADCAST_ACTION_REPORTS_LOADED_FAILED);
        filter.addAction(BROADCAST_ACTION_REPORTS_LOADED_SUCCESS);

        filter.addCategory(Intent.CATEGORY_DEFAULT);
        getActivity().registerReceiver(receiver, filter);

        data.loadReports();
//        reports = data.getReports();

        return view;
    }


//    @Override
//    public void reportsDataHasChanged() {
//        data.loadReports();
//        lvReports = getView().findViewById(R.id.lvReports);
//        reports = data.getReports();
//        ArrayAdapter adapterReports = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, reports);
//        lvReports.setAdapter(adapterReports);
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
        filter.addAction(BROADCAST_ACTION_REPORTS_LOADED_FAILED);
        filter.addAction(BROADCAST_ACTION_REPORTS_LOADED_SUCCESS);

        filter.addCategory(Intent.CATEGORY_DEFAULT);
        getActivity().registerReceiver(receiver, filter);
    }
}