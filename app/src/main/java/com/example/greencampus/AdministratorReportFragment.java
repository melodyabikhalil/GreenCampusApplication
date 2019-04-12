package com.example.greencampus;

import android.content.Context;
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

import java.util.ArrayList;
import java.util.List;

import model.DataModel;

public class AdministratorReportFragment extends Fragment {

    View view;
    ListView lvReports;
    DataModel data;
    List reports;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Reports");
        view = inflater.inflate(R.layout.fragment_administrator_report, container, false);

        lvReports = view.findViewById(R.id.lvReports);
        data = DataModel.instance;

//        data.loadReports();
        reports = data.getReports();

        ArrayAdapter adapterReports = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, reports);
        lvReports.setAdapter(adapterReports);

        return view;
    }
}