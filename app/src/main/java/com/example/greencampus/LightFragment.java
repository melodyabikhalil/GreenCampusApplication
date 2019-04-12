package com.example.greencampus;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.ContentHandler;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import model.Class;
import model.DataModel;
import model.User;

import static android.app.Activity.RESULT_OK;

public class LightFragment extends Fragment{

    TextView tvLightState;
    Button buttonTurnOn, buttonTurnOff;
    String state = "";
    DataModel data;
    Class classe;
    View view;

    private final int CAMERA_REQUEST_CODE = 1;

    DatabaseReference firebaseDatabase;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    DataModel dataModel = DataModel.instance;
    String userName = dataModel.getLocalUserInfo(GreenCampusApplication.getContext()).getFirstName()+" "+dataModel.getLocalUserInfo(GreenCampusApplication.getContext()).getLastName();
    String classID = dataModel.getLocalClassName(GreenCampusApplication.getContext());


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Light");
        data = DataModel.instance;

        classe = data.getLocalClassInfo(getContext());

        //Outlets
        view = inflater.inflate(R.layout.fragment_light, container, false);

        /*buttonTurnOn = (Button) view.findViewById(R.id.buttonTurnOn);
        buttonTurnOff = (Button) view.findViewById(R.id.buttonTurnOff);
        tvLightState = (TextView) view.findViewById(R.id.tvLightState);*/

        firebaseDatabase = FirebaseDatabase.getInstance().getReference();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        data.saveClasseState(GreenCampusApplication.getContext());

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
                    Toast toast = Toast.makeText(getContext(), "Light is already ON", Toast.LENGTH_SHORT);
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
                    Toast toast = Toast.makeText(getContext(), "Light is already OFF", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
    }

    public void changeState(){
        TextView textViewLightState = getView().findViewById(R.id.tvLightState);
        if (state.equals("ON")){
            state="OFF";
            textViewLightState.setText("Your class light is OFF");
        }
        if (state.equals("OFF")){
            state="ON";
            textViewLightState.setText("Your class light is ON");
        }
    }

    public void setState(){
        TextView textViewLightState = getView().findViewById(R.id.tvLightState);
        if (classe.getIsOn().equals("0")){
            textViewLightState.setText("Your class light is OFF");
            state = "OFF";
        }
        if (classe.getIsOn().equals("1")){
            textViewLightState.setText("Your class light is ON");
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

            final ProgressDialog pd = new ProgressDialog(getContext());
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.setMessage("Uploading image. Please wait..");
            pd.setIndeterminate(true);
            pd.setCancelable(false);
            pd.show();

            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] dataImage = baos.toByteArray();

            Date date = new Date();
            final String imageName = UUID.randomUUID().toString().replace("-", "");
            UploadTask uploadTask = storageReference.child(imageName).putBytes(dataImage);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    System.out.println("failed");
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    System.out.println("done uploading");
                    dataModel.changeClassStateFirebase(GreenCampusApplication.getContext(), "0", imageName);
                    changeState();
                }
            });
        }
    }



}
