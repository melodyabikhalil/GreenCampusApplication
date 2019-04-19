package com.example.greencampus;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import model.DataModel;
import model.Helper;
import model.User;

import static model.Helper.BROADCAST_ACTION_DELEGATE_LOADED_FAILED;
import static model.Helper.BROADCAST_ACTION_DELEGATE_LOADED_SUCCESS;
import static model.Helper.BROADCAST_ACTION_DOWNLOAD_IMAGE_FAILED;
import static model.Helper.BROADCAST_ACTION_DOWNLOAD_IMAGE_SUCESS;

public class DelegateActivity extends AppCompatActivity {

    Toolbar toolbar;
    DataModel data;
    User delegate;
    TextView tvFirstName, tvLastName, tvPhoneNumber;
    ImageView ivPhoto;
    String photoURL;
//    FirebaseStorage storage;
    Bitmap image;
    Button call;

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if(action.equals(Helper.BROADCAST_ACTION_DELEGATE_LOADED_FAILED)){
                Toast toast = Toast.makeText(DelegateActivity.this, "Could not load delegate information. Please try again later.", Toast.LENGTH_SHORT);
                toast.show();
            }

            if(action.equals(Helper.BROADCAST_ACTION_DELEGATE_LOADED_SUCCESS)){
                delegate = data.getDelegate();

                tvPhoneNumber.setText(delegate.getPhoneNumber());
                tvLastName.setText(delegate.getLastName());
                tvFirstName.setText(delegate.getFirstName());
            }

            if(action.equals(Helper.BROADCAST_ACTION_DOWNLOAD_IMAGE_FAILED)){
                Toast toast = Toast.makeText(DelegateActivity.this, "Could not load class photo. Please try again later.", Toast.LENGTH_SHORT);
                toast.show();
            }

            if(action.equals(Helper.BROADCAST_ACTION_DOWNLOAD_IMAGE_SUCESS)){
                ivPhoto.setImageBitmap(data.getRealImage());
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delegate);
        data = DataModel.instance;

        IntentFilter filter = new IntentFilter();
        filter.addAction(BROADCAST_ACTION_DELEGATE_LOADED_FAILED);
        filter.addAction(BROADCAST_ACTION_DELEGATE_LOADED_SUCCESS);
        filter.addAction(BROADCAST_ACTION_DOWNLOAD_IMAGE_FAILED);
        filter.addAction(BROADCAST_ACTION_DOWNLOAD_IMAGE_SUCESS);

        filter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(receiver, filter);

        Bundle extras = getIntent().getExtras();
        String className= extras.getString("className");
        String state = extras.getString("isON");
        setTitle(className);

        data.loadDelegateInfo(className);

//        storage = FirebaseStorage.getInstance();

        if(state.equals("0")){
            photoURL = data.getPhotoURL();
            data.downloadPicture(photoURL);
        }

        tvFirstName = (TextView) findViewById(R.id.tvFirstName);
        tvLastName = (TextView) findViewById(R.id.tvLastName);
        tvPhoneNumber = (TextView) findViewById(R.id.tvPhoneNumber);
        ivPhoto = (ImageView) findViewById(R.id.ivPhoto);
        call = (Button) findViewById(R.id.buttonCall);
        toolbar = (Toolbar) findViewById(R.id.toolbarDelegate);
        delegate = new User();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Back button
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.GRAY);
        }

        call.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DelegateActivity.this);
                alertDialogBuilder.setTitle("Call Delegate");
                alertDialogBuilder
                        .setMessage("\nAre you sure that you want to call the delegate?")
                        .setCancelable(false)
                        .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                callDelegate();
                            }
                        })
                        .setNegativeButton("No",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();

                alertDialog.show();
            }
        });
    }

    public void callDelegate(){
        String phoneNumber = delegate.getPhoneNumber();
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:"+phoneNumber));
        startActivity(callIntent);
    }

//    public void loadImage(String photoURL){
//        System.out.println("photo "+photoURL);
//        try {
//            StorageReference ref = storage.getReference().child(photoURL);
//            final File localFile = File.createTempFile("Images", "bmp");
//            ref.getFile(localFile).addOnSuccessListener(new OnSuccessListener< FileDownloadTask.TaskSnapshot >() {
//                @Override
//                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
//                    image = BitmapFactory.decodeFile(localFile.getAbsolutePath());
//                    ivPhoto.setImageBitmap(image);
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    Toast.makeText(DelegateActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
//                }
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(receiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BROADCAST_ACTION_DELEGATE_LOADED_FAILED);
        filter.addAction(BROADCAST_ACTION_DELEGATE_LOADED_SUCCESS);
        filter.addAction(BROADCAST_ACTION_DOWNLOAD_IMAGE_FAILED);
        filter.addAction(BROADCAST_ACTION_DOWNLOAD_IMAGE_SUCESS);

        filter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(receiver, filter);
    }
}
