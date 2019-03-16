package com.example.edwin.neighbourhooddiary;

import android.content.Intent;
import android.location.Location;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class NFCTester extends AppCompatActivity implements NfcAdapter.OnNdefPushCompleteCallback, NfcAdapter.CreateNdefMessageCallback {

    private FBDatabaseHelper fbDatabaseHelper = new FBDatabaseHelper();
    DatabaseReference mMarkerReference;
    ArrayList<CustomMarker> activeCustomMarkers = new ArrayList<>();
    TextView currentEvent;
    ArrayList<String> eventNames;

    DatabaseReference ref;
    FirebaseDatabase database;
    TextView currentmarker;
    TextView hiddenText;
    int currentcounter = 0;

    private NfcAdapter mNfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfctester);


        database = FirebaseDatabase.getInstance();
        ref = database.getReference("/markers");


        currentmarker = findViewById(R.id.eventName);
        hiddenText = findViewById(R.id.hiddenLocationView);


        readData(new FirebaseCallback() {
            @Override
            public void onCallback(List<CustomMarker> list) {
                for(CustomMarker l : list){
                    hiddenText.append(l.getEventName()+"£");
                }
                currentmarker.setText(list.get(0).getEventName());
                activeCustomMarkers = (ArrayList<CustomMarker>) list;
            }
        });



        if(activeCustomMarkers.size() != 0){
            Toast.makeText(this, activeCustomMarkers.get(0).getEventName(), Toast.LENGTH_SHORT).show();
        }


        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if(mNfcAdapter != null) {
            //This will refer back to createNdefMessage for what it will send
            mNfcAdapter.setNdefPushMessageCallback(this, this);

            //This will be called if the message is sent successfully
            mNfcAdapter.setOnNdefPushCompleteCallback( this, this);
        }
        else {
            Toast.makeText(this, "NFC unavailable",
                    Toast.LENGTH_SHORT).show();
        }


    }

    private void loadInMarkers(DataSnapshot dataSnapshot) {

        ArrayList<CustomMarker> activeCustomMarkers = new ArrayList<>();

        for(DataSnapshot ds : dataSnapshot.getChildren() ){
            CustomMarker customMarker = new CustomMarker();
            customMarker.setAddedBy(ds.getValue(CustomMarker.class).getAddedBy());
            customMarker.setDescrip(ds.getValue(CustomMarker.class).getDescrip());
            customMarker.setEndTime(ds.getValue(CustomMarker.class).getEndTime());
            customMarker.setEventName(ds.getValue(CustomMarker.class).getEventName());
            customMarker.setEventType(ds.getValue(CustomMarker.class).getEventType());
            customMarker.setExpirable(ds.getValue(CustomMarker.class).isExpirable());
            customMarker.setLat(ds.getValue(CustomMarker.class).getLat());
            customMarker.setLng(ds.getValue(CustomMarker.class).getLng());
            customMarker.setStartTime(ds.getValue(CustomMarker.class).getStartTime());

            //Toast.makeText(this, customMarker.getEventName(), Toast.LENGTH_SHORT).show();

            activeCustomMarkers.add(customMarker);

        }
    }

    public void nextMarker(View view) {
        if((currentcounter + 1) < activeCustomMarkers.size() )
        {
            currentmarker.setText(activeCustomMarkers.get(currentcounter+1).getEventName());
            currentcounter+=1;
        } else {
            currentmarker.setText(activeCustomMarkers.get(0).getEventName());
            currentcounter=0;
        }
    }

    public void prevMarker(View view) {

        int arraysize = activeCustomMarkers.size();

        if((currentcounter - 1) >= 0){
            currentmarker.setText(activeCustomMarkers.get(currentcounter-1).getEventName());
            currentcounter-=1;
        } else {
            currentmarker.setText(activeCustomMarkers.get(arraysize-1).getEventName());
            currentcounter = arraysize-1;
        }


    }

    public void setNFC(View view) {
    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {


        //This will be called when another NFC capable device is detected.

        //We'll write the createRecords() method in just a moment
        NdefRecord recordToAttach = createRecords();
        //When creating an NdefMessage we need to provide an NdefRecord[]
        return new NdefMessage(recordToAttach);
    }

    public NdefRecord createRecords() {
        //To Create Messages Manually if API is less than
        NdefRecord record;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {

            byte[] payload = "ATM".
                    getBytes(Charset.forName("UTF-8"));

            record = new NdefRecord(
                    NdefRecord.TNF_WELL_KNOWN,      //Our 3-bit Type name format
                    NdefRecord.RTD_TEXT,            //Description of our payload
                    new byte[0],                    //The optional id for our Record
                    payload);                       //Our payload for the Record

        }
        //Api is high enough that we can use createMime, which is preferred.
        else {
            byte[] payload = "ATM".getBytes(Charset.forName("UTF-8"));

            record = NdefRecord.createMime("text/plain",payload);
        }
        return record;
    }


    @Override
    public void onNdefPushComplete(NfcEvent event) {

    }

    @Override
    public void onNewIntent(Intent intent) {
        recieveNFCIntent(intent);
    }

    private void recieveNFCIntent(Intent NfcIntent) {
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(NfcIntent.getAction())) {
            Parcelable[] receivedArray =
                    NfcIntent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

            if(receivedArray != null) {
                NdefMessage receivedMessage = (NdefMessage) receivedArray[0];
                NdefRecord[] attachedRecords = receivedMessage.getRecords();

                for (NdefRecord r:attachedRecords) {
                    String feedback = new String(r.getPayload());
                    Toast.makeText(this, feedback , Toast.LENGTH_LONG).show();
                    if (feedback.equals(getPackageName())) {
                        continue;
                    }
                }

            }
            else {
                Toast.makeText(this, "Received Nothing", Toast.LENGTH_LONG).show();
            }
        }
    }

    //Save our Array Lists of Messages for if the user navigates away
    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    //Load our Array Lists of Messages for when the user navigates back
    @Override
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }



    private void readData(final FirebaseCallback firebaseCallback){

        final ValueEventListener markerListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        CustomMarker marker =userSnapshot.getValue(CustomMarker.class);
                        activeCustomMarkers.add(marker);
                    }
                    firebaseCallback.onCallback(activeCustomMarkers);               }
                else{
                    System.out.println("not found");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message

                // ...
            }
        };
        ref.addValueEventListener(markerListener);


    }

    private interface FirebaseCallback{
        void onCallback(List<CustomMarker> list);

    }
}
