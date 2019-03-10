package com.example.avira.playoffadmin;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity {

    NfcAdapter nfcAdapter;
    ImageButton loginbtn;
    EditText secretpin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        loginbtn = (ImageButton)findViewById(R.id.loginbtn);
        secretpin = (EditText)findViewById(R.id.pin);

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginact();
            }
        });

    }

    public void loginact() {

        String pin = secretpin.getText().toString();
        if (pin.equals("1234")) {
            Intent i = new Intent(MainActivity.this, check.class);
            startActivity(i);
        }
        else {
            Toast.makeText(MainActivity.this,"Incorrect Pin",Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        enableforegroundsystem();
    }

    @Override
    protected void onPause() {
        super.onPause();
        disableforegroundsystem();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.hasExtra(nfcAdapter.EXTRA_TAG)) {


            Parcelable[] parcelables = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

            if (parcelables != null && parcelables.length > 0) {

                readTextFromMessage((NdefMessage) parcelables[0]);
            } else {
                Toast.makeText(MainActivity.this, "NO NFC", Toast.LENGTH_LONG).show();
            }
        }


    }

    private void readTextFromMessage(NdefMessage ndefMessage) {
        NdefRecord[] ndefRecords = ndefMessage.getRecords();
        if (ndefRecords != null && ndefRecords.length > 0) {
            NdefRecord ndefRecord = ndefRecords[0];
            final String Tagcontent = getTextFromNdefRecord(ndefRecord);

            if (Tagcontent.equals("1234")){
                Intent i = new Intent(MainActivity.this,check.class);
                startActivity(i);

            }
            else {
                Toast.makeText(MainActivity.this, "no record found", Toast.LENGTH_LONG).show();
            }


        }
    }

    private void enableforegroundsystem() {
        Intent intent = new Intent(this, MainActivity.class).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        IntentFilter[] intentFilters = new IntentFilter[]{};
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, null);

    }

    private void disableforegroundsystem() {

    }

    public String getTextFromNdefRecord(NdefRecord ndefRecord){
        String tagcontent = null;
        try{
            byte[] paylaod = ndefRecord.getPayload();
            String TextEncoding = ((paylaod[
                    0]&128)==0)? "UTF-8":"UTF-16";
            int languageSize = paylaod[0] & 0063;
            tagcontent = new String(paylaod,languageSize+1, paylaod.length - languageSize -1,TextEncoding);

        } catch (UnsupportedEncodingException e){
            Log.e("get text from record ",e.getMessage(),e );

        }
        return tagcontent;

    }



}
