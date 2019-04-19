package com.example.greencampus;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.UUID;

import model.Class;
import model.DataModel;
import static android.app.Activity.RESULT_OK;
import static model.Helper.BROADCAST_ACTION_CHANGE_CLASS_STATE_FAILED;
import static model.Helper.BROADCAST_ACTION_CHANGE_CLASS_STATE_SUCESS;
import static model.Helper.BROADCAST_ACTION_UPLOAD_IMAGE_FAILED;
import static model.Helper.BROADCAST_ACTION_UPLOAD_IMAGE_SUCESS;

public class LightFragment extends Fragment{

    TextView tvLightState;
    Button buttonTurnOn, buttonTurnOff;
    String state = "";
    DataModel data;
    Class classe;
    View view;
    ProgressDialog progressDialog;
    String imageURL;

    private final int CAMERA_REQUEST_CODE = 1;

    DatabaseReference firebaseDatabase;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if(action.equals(BROADCAST_ACTION_UPLOAD_IMAGE_FAILED)) {
                progressDialog.dismiss();
                Toast toast = Toast.makeText(getContext(), "Could not upload image. Please try again later", Toast.LENGTH_LONG);
                toast.show();
            }

            if(action.equals(BROADCAST_ACTION_UPLOAD_IMAGE_SUCESS)){
                progressDialog.dismiss();
//                Toast toast = Toast.makeText(getContext(), "Image uploaded successfully", Toast.LENGTH_SHORT);
//                toast.show();
                dataModel.changeClassStateFirebase(GreenCampusApplication.getContext(),"0", imageURL);
                changeState();
            }

            if(action.equals(BROADCAST_ACTION_CHANGE_CLASS_STATE_FAILED)) {
                Toast toast = Toast.makeText(getContext(), "Could not change class state. Please try again later", Toast.LENGTH_LONG);
                toast.show();
            }

            if(action.equals(BROADCAST_ACTION_CHANGE_CLASS_STATE_SUCESS)) {
                changeState();
            }
        }
    };

    DataModel dataModel = DataModel.instance;
    String userName = dataModel.getLocalUserInfo(GreenCampusApplication.getContext()).getFirstName()+" "+dataModel.getLocalUserInfo(GreenCampusApplication.getContext()).getLastName();
    String classID = dataModel.getLocalClassName(GreenCampusApplication.getContext());


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Class");
        data = DataModel.instance;

        classe = data.getLocalClassInfo(getContext());
        progressDialog = new ProgressDialog(getActivity());

        //Outlets
        view = inflater.inflate(R.layout.fragment_light, container, false);

        /*buttonTurnOn = (Button) view.findViewById(R.id.buttonTurnOn);
        buttonTurnOff = (Button) view.findViewById(R.id.buttonTurnOff);*/
        tvLightState = (TextView) view.findViewById(R.id.tvLightState);

        firebaseDatabase = FirebaseDatabase.getInstance().getReference();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        data.saveClasseState(GreenCampusApplication.getContext());

        IntentFilter filter = new IntentFilter();
        filter.addAction(BROADCAST_ACTION_UPLOAD_IMAGE_FAILED);
        filter.addAction(BROADCAST_ACTION_UPLOAD_IMAGE_SUCESS);

        filter.addCategory(Intent.CATEGORY_DEFAULT);
        getActivity().registerReceiver(receiver, filter);

        /*firebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                data.saveFirebaseClassStateLocally(getContext(),(HashMap<String, Object>) dataSnapshot.child("classes").child(classe.getClassName()).getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });*/

        /*if (classe.getIsOn().equals("0")){
            tvLightState.setText("Your class light is OFF");
            state = "OFF";
        }
        if (classe.getIsOn().equals("1")){
            tvLightState.setText("Your class light is ON");
            state="ON";
        }*/

//        setState();

       /*buttonTurnOn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(state.equals("OFF")){
                    data.changeClassStateFirebase(getContext(),"1");
                }
                tvLightState.setText("Your class light is ON");
            }
        });


        buttonTurnOff.setOnClickListener( new View.OnClickListener() {
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");

            @Override
            public void onClick(View v) {
                if(state.equals("ON")){
                    data.changeClassStateFirebase(getContext(),"0");
                }
                tvLightState.setText("Your class light is OFF");

            }
        });*/

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        setState();
        Button buttonTurnOn = getView().findViewById(R.id.buttonTurnOn);
        Button buttonTurnOff = getView().findViewById(R.id.buttonTurnOff);

        buttonTurnOn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (state.equals("OFF")) {
                    data.changeClassStateFirebase(GreenCampusApplication.getContext(), "1", "");
                    changeState();
                } else {
                    Toast toast = Toast.makeText(getContext(), "Class is already turned ON", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });


        buttonTurnOff.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (state.equals("ON")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, CAMERA_REQUEST_CODE);
                    //data.changeClassStateFirebase(GreenCampusApplication.getContext(), "0");
                    //tvLightState.setText("Your class light is OFF");
                   // state = "OFF";
                } else {
                    Toast toast = Toast.makeText(getContext(), "Class is already turned OFF", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
    }

    public void changeState(){
//        TextView textViewLightState = getView().findViewById(R.id.tvLightState);
        if (state.equals("ON")){
            state="OFF";
            tvLightState.setText("Your class is OFF");
            return;
        }
        if (state.equals("OFF")){
            state="ON";
            tvLightState.setText("Your class is ON");
            return;
        }
    }

    public void setState(){
        TextView textViewLightState = getView().findViewById(R.id.tvLightState);
        if (classe.getIsOn().equals("0")){
            textViewLightState.setText("Your class is OFF");
            state = "OFF";
        }
        if (classe.getIsOn().equals("1")){
            textViewLightState.setText("Your class is ON");
            state="ON";
        }
    }

        /*buttonTurnOff.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (state.equals("ON")) {

                    CameraFragment nextFrag= new CameraFragment();
                    getActivity().getSupportFragmentManager().beginTransaction()
                           .replace(R.id.fragment_container, nextFrag, "findThisFragment")
                            .addToBackStack(null)
                            .commit();

                    System.out.println("nkabas el button off");
                    state="OFF";

//                    data.changeClassStateFirebase(GreenCampusApplication.getContext(), "0");
                    tvLightState.setText("Your class light is OFF");

                }
                System.out.println("kabaset off");

            }
        });
    }


    public void changeClassState(String newState) {
        String classeName = getClassInfo().getClassName();
        System.out.println("classame: "+classeName);
        System.out.println("li 3am hota hye: "+newState);
        firebaseDatabase.child("classes").child(classeName).child("isOn").setValue(newState);
        saveNewClassState(newState);
    }

    public Class getClassInfo(){
        int mode = Activity.MODE_PRIVATE;
        SharedPreferences mySharedPreferences ;
        mySharedPreferences=this.getActivity().getSharedPreferences("UserInfo",mode);
        String className = mySharedPreferences.getString("userClass","");
        String classState = mySharedPreferences.getString("classState","");
        System.out.println("state el msayyave hye: "+classState);
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
    }*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {

            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("Uploading image. Please wait..");
            progressDialog.show();

            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] dataImage = baos.toByteArray();

            Date date = new Date();
            final String imageName = UUID.randomUUID().toString().replace("-", "");
            imageURL = imageName;
            dataModel.uploadPicture(imageName, dataImage);
//            UploadTask uploadTask = storageReference.child(imageName).putBytes(dataImage);
//            uploadTask.addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception exception) {
//                    System.out.println("failed");
//                }
//            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    System.out.println("done uploading");
//                    dataModel.changeClassStateFirebase(GreenCampusApplication.getContext(), "0", imageName);
//                    changeState();
//                }
//            });
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
        filter.addAction(BROADCAST_ACTION_UPLOAD_IMAGE_FAILED);
        filter.addAction(BROADCAST_ACTION_UPLOAD_IMAGE_SUCESS);

        filter.addCategory(Intent.CATEGORY_DEFAULT);
        getActivity().registerReceiver(receiver, filter);
    }
}
