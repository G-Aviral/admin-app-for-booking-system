package com.example.avira.playoffadmin;

import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class process extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Spinner wirecount;
    Spinner framecount;
    TextView showname;
    TextView showtime;
    TextView showextratime;
    TextView showamt;
    TextView showcurrtime;
    ImageButton paymentdone;

    String out;

    ImageButton calcamt;
    ImageButton finalamt;
    TextView showamt1;

    //db
    private DatabaseReference g1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process);

        //getting Uid
        Bundle extra = getIntent().getExtras();
        final String uid = extra.getString("Uid");

        //display instance
        showname = (TextView)findViewById(R.id.showname);
        showtime = (TextView)findViewById(R.id.showtime);
        showamt = (TextView)findViewById(R.id.showamt);
        showextratime = (TextView)findViewById(R.id.showextratime);
        showcurrtime = (TextView)findViewById(R.id.showcurrtime);

        //display current time
        DateFormat df = new SimpleDateFormat("HH:mm");
        String currrdate = df.format(Calendar.getInstance().getTime());
        showcurrtime.setText(currrdate.toString());


        //display name
        g1 = FirebaseDatabase.getInstance().getReferenceFromUrl("https://playoff-7e1ac.firebaseio.com/users").child(uid).child("Name");
        g1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String naamm = dataSnapshot.getValue().toString();
                showname.setText(naamm);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(process.this,"ERROR in connection",Toast.LENGTH_SHORT).show();
            }
        });

        //display time
        g1 = FirebaseDatabase.getInstance().getReferenceFromUrl("https://playoff-7e1ac.firebaseio.com/users").child(uid).child("Check-out Time");
        g1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String samay = (dataSnapshot.getValue().toString());

                SimpleDateFormat dateFormat = new SimpleDateFormat("HH");
                SimpleDateFormat dateFormat2 = new SimpleDateFormat("HH:mm");
                try {
                    Date date = dateFormat.parse(samay);

                    out = dateFormat2.format(date);
                    showtime.setText(out);

                }
                catch (ParseException e) {
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(process.this,"ERROR in connection",Toast.LENGTH_SHORT).show();
            }
        });

        //calculatin EXTRA TIME
        calcamt = (ImageButton)findViewById(R.id.calcamt);
        calcamt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");

                Date date1 = null;
                Date date2 = null;
                try {
                    date1 = simpleDateFormat.parse(showtime.getText().toString());
                    date2 = simpleDateFormat.parse(showcurrtime.getText().toString());
                    //difference of time
                    Long diff = date2.getTime() - date1.getTime();
                    Long seconds = diff / 1000;
                    Long minutes = seconds / 60;

                    String str = String.valueOf(minutes);
                    showextratime.setText(str);

                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
        });

        //getting final amt
        showamt1 = (TextView)findViewById(R.id.showamt1);
        finalamt = (ImageButton) findViewById(R.id.finalamt);
        finalamt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Integer xyzamt = Integer.parseInt(showextratime.getText().toString())*5+Integer.parseInt(showamt.getText().toString());
                String st = String.valueOf(xyzamt);
                showamt1.setText(st);

                g1 = FirebaseDatabase.getInstance().getReferenceFromUrl("https://playoff-7e1ac.firebaseio.com/users").child(uid);
                Map<String,Object> taskmap = new HashMap<>();
                taskmap.put("Penalty Amt Due",st);
                taskmap.put("Final Check out time",showcurrtime.getText().toString());
                g1.updateChildren(taskmap);

            }
        });

        //spinner
        wirecount = (Spinner)findViewById(R.id.wirecount);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.wire,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        wirecount.setAdapter(adapter);
        wirecount.setOnItemSelectedListener(this);


        framecount = (Spinner)findViewById(R.id.framecount);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,R.array.frame,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        framecount.setAdapter(adapter);
        framecount.setOnItemSelectedListener(this);

        paymentdone = (ImageButton)findViewById(R.id.paymentdone);
        paymentdone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(process.this,"Payment Received",Toast.LENGTH_SHORT).show();
                Intent i = new Intent(process.this,check.class);
                startActivity(i);
                finish();
            }
        });


    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String wire = wirecount.getSelectedItem().toString();
        String frame = framecount.getSelectedItem().toString();

        //display amt
        Integer total_amt=(Integer.parseInt(wire)*400)+(Integer.parseInt(frame)*600);
        showamt = (TextView)findViewById(R.id.showamt);
        showamt.setText(total_amt.toString());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
