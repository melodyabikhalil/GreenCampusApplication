package com.example.greencampus;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

import model.DataModel;
import model.Report;

public class ReportFragment extends Fragment {

    Button buttonSend;
    View view;
    EditText etMessage;
    DatabaseReference firebaseDatabase;
    DataModel data;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Report");

        view = inflater.inflate(R.layout.fragment_report, container, false);
        buttonSend = (Button) view.findViewById(R.id.buttonSend);
        etMessage = (EditText) view.findViewById(R.id.etMessage);
        data = DataModel.instance;

        firebaseDatabase = FirebaseDatabase.getInstance().getReference();

        buttonSend.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendReport();
            }
        });
        return view;
    }


    public void sendReport() {
        String message = etMessage.getText().toString();

        if (message.equals("")) {
            Toast toast = Toast.makeText(getActivity(), "Please write inside message box", Toast.LENGTH_SHORT);
            toast.show();
        }
        else{
            String userId = data.getLocalUserId(getContext());
            String reportId = UUID.randomUUID().toString().replace("-", "");
            Report report = new Report(userId, message);
            firebaseDatabase.child("reports").child(reportId).setValue(report);
            Toast toast = Toast.makeText(getActivity(), "Report sent", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}