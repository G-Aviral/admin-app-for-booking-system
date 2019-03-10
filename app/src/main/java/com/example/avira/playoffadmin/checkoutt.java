package com.example.avira.playoffadmin;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;

public class checkoutt extends AppCompatActivity {

    SurfaceView camerapreview;
    TextView txtresult;
    BarcodeDetector barcodeDetector;
    CameraSource cameraSource;
    final int RequestCameraPermissionID = 1001;
    ImageButton home;

    private DatabaseReference g1;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RequestCameraPermissionID: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                        return;
                    }
                    try {
                        cameraSource.start(camerapreview.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkoutt);

        //home button

        home = (ImageButton)findViewById(R.id.home);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gohome();
            }
        });


        //code


        camerapreview = (SurfaceView) findViewById(R.id.camerapreview);
        txtresult = (TextView) findViewById(R.id.txtresult);
        barcodeDetector = new BarcodeDetector.Builder(checkoutt.this).setBarcodeFormats(Barcode.QR_CODE).build();
        cameraSource = new CameraSource.Builder(this, barcodeDetector).setRequestedPreviewSize(800, 800).build();
        //event adding
        camerapreview.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                if (ActivityCompat.checkSelfPermission(checkoutt.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    //request permission
                    ActivityCompat.requestPermissions(checkoutt.this,new String[]{Manifest.permission.CAMERA},RequestCameraPermissionID);
                    return;
                }
                try {
                    cameraSource.start(camerapreview.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> qrcodes = detections.getDetectedItems();
                if (qrcodes.size() != 0 )
                {
                    Vibrator vibrator = (Vibrator)getApplicationContext().getSystemService(VIBRATOR_SERVICE);
                    vibrator.vibrate(50);

                    final String qrstring = qrcodes.valueAt(0).displayValue;
                    txtresult.setText(qrstring);

                    //db reference
                    //mAuth = FirebaseAuth.getInstance();
                    g1 = FirebaseDatabase.getInstance().getReferenceFromUrl("https://playoff-7e1ac.firebaseio.com/users");
                    g1.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild(qrstring)){
                                Toast.makeText(checkoutt.this,"VALID",Toast.LENGTH_SHORT).show();



                                //moving back to screen
                                Intent i = new Intent(checkoutt.this,process.class);
                                //getting Uid to next activity
                                i.putExtra("Uid",qrstring);
                                startActivity(i);
                                finish();
                            }
                            else {
                                Toast.makeText(checkoutt.this,"INVALID CODE",Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });





                }
            }
        });




    }


    //home
    public void gohome(){
        Intent i = new Intent(checkoutt.this,check.class);
        startActivity(i);
    }


}
