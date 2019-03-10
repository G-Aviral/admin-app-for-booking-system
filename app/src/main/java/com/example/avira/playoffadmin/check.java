package com.example.avira.playoffadmin;

import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class check extends AppCompatActivity {

    ImageButton home;
    ImageButton signout;
    ImageButton checkin;
    ImageButton checkout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);

        //making instance

        home = (ImageButton)findViewById(R.id.home);
        signout = (ImageButton)findViewById(R.id.signout);
        checkin = (ImageButton)findViewById(R.id.checkin);
        checkout = (ImageButton)findViewById(R.id.checkout);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gohome();
            }
        });

        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signoutkro();
            }
        });

        checkin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkinkro();
            }
        });

        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkoutkro();
            }
        });

    }

    //home
    public void gohome(){
        Intent i = new Intent(check.this,check.class);
        startActivity(i);
    }

    //signout
    public void signoutkro(){
        Intent i = new Intent(check.this,MainActivity.class);
        startActivity(i);
        finish();
    }

    //checkin
    public void checkinkro(){
        Intent i = new Intent(check.this,checkin.class);
        startActivity(i);
    }

    //checkout
    public void checkoutkro(){
        Intent i = new Intent(check.this,checkoutt.class);
        startActivity(i);
    }



}
