package com.example.greencampus;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
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
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;

import model.DataModel;
import model.User;

public class DelegateActivity extends AppCompatActivity {

    Toolbar toolbar;
    DataModel data;
    User delegate;
    TextView tvFirstName, tvLastName, tvPhoneNumber;
    ImageView ivPhoto;
    String photoURL;
    FirebaseStorage storage;
    Bitmap image;
    Button call;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delegate);
        setTitle("Classes List");
        data = DataModel.instance;

        Bundle extras = getIntent().getExtras();
        String className= extras.getString("className");
        String state = extras.getString("isON");
        data.loadDelegateInfo(className);

        storage = FirebaseStorage.getInstance();
        if(state.equals("0")){
            photoURL = data.getPhotoURL();
            System.out.println("state is "+state);
            System.out.println("photo name is: "+photoURL);
            loadImage(photoURL);
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


        delegate = data.getDelegate();


        tvPhoneNumber.setText(delegate.getPhoneNumber());
        tvLastName.setText(delegate.getLastName());
        tvFirstName.setText(delegate.getFirstName());

        //Back button
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.GRAY);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        call.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                callDelegate();
            }
        });
    }

    public void loadImage(String photoURL){
        System.out.println("photo "+photoURL);
        StorageReference ref = storage.getReference().child(photoURL);

        try {
            final File localFile = File.createTempFile("Images", "bmp");
            ref.getFile(localFile).addOnSuccessListener(new OnSuccessListener< FileDownloadTask.TaskSnapshot >() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    image = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    ivPhoto.setImageBitmap(image);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(DelegateActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void callDelegate(){
        String phoneNumber = delegate.getPhoneNumber();
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:"+phoneNumber));
        startActivity(callIntent);
    }

}
