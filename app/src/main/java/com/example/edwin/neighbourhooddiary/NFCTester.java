package com.example.edwin.neighbourhooddiary;

import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class NFCTester extends AppCompatActivity {

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
