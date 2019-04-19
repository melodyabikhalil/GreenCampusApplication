package com.example.greencampus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

import static model.Helper.BROADCAST_ACTION_PROFILE_FAILED;
import static model.Helper.BROADCAST_ACTION_PROFILE_LOADED;
import static model.Helper.BROADCAST_ACTION_SEND_REPORT_FAILED;
import static model.Helper.BROADCAST_ACTION_SEND_REPORT_SUCCESS;

public class ReportFragment extends Fragment {

    Button buttonSend;
    View view;
    EditText etMessage;
//    DatabaseReference firebaseDatabase;
    DataModel data;

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if(action.equals(BROADCAST_ACTION_SEND_REPORT_SUCCESS)) {
                Toast toast = Toast.makeText(GreenCampusApplication.getContext(), "Report sent successfully.", Toast.LENGTH_SHORT);
                toast.show();
                etMessage.setText("");
            }

            if(action.equals(BROADCAST_ACTION_SEND_REPORT_FAILED)){
                Toast toast = Toast.makeText(GreenCampusApplication.getContext(), "Could not send report. Please try again later.", Toast.LENGTH_SHORT);
                toast.show();
                etMessage.setText("");
            }

        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Report");

        view = inflater.inflate(R.layout.fragment_report, container, false);
        buttonSend = (Button) view.findViewById(R.id.buttonSend);
        etMessage = (EditText) view.findViewById(R.id.etMessage);
        data = DataModel.instance;

        IntentFilter filter = new IntentFilter();
        filter.addAction(BROADCAST_ACTION_SEND_REPORT_FAILED);
        filter.addAction(BROADCAST_ACTION_SEND_REPORT_SUCCESS);

        filter.addCategory(Intent.CATEGORY_DEFAULT);
        getActivity().registerReceiver(receiver, filter);
//        firebaseDatabase = FirebaseDatabase.getInstance().getReference();

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
//            firebaseDatabase.child("reports").child(reportId).setValue(report);
            data.sendReport(reportId, report);
        }
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
        filter.addAction(BROADCAST_ACTION_SEND_REPORT_FAILED);
        filter.addAction(BROADCAST_ACTION_SEND_REPORT_SUCCESS);

        filter.addCategory(Intent.CATEGORY_DEFAULT);
        getActivity().registerReceiver(receiver, filter);
    }
}