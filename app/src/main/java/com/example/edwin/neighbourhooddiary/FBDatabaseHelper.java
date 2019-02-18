package com.example.edwin.neighbourhooddiary;

import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.firebase.FirebaseError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class FBDatabaseHelper {

    private DatabaseReference mDatabase;

    CustomMarker targetMarker = new CustomMarker();

    DatabaseReference mUsersReference;
    DatabaseReference mMarkersReference;

    public FBDatabaseHelper() {
        mDatabase =  FirebaseDatabase.getInstance().getReference();

        mUsersReference = FirebaseDatabase.getInstance().getReference().child("users");
        mMarkersReference = FirebaseDatabase.getInstance().getReference().child("markers");

    }

    public void writeNewUser(String userId, String name, String email) {
        User user = new User(name, email);
        mDatabase.child("users").child(userId).setValue(user);
    }

    public void writeNewMarker(double lat, double lng, boolean isExpirable, String eventName, String eventType, long startTime, long endTime, String descrip, String addedBy){
        CustomMarker customMarker = new CustomMarker( lat,  lng,  isExpirable,  eventName,  eventType,  startTime,  endTime,  descrip,   addedBy);
        mDatabase.child("markers").child(eventName + addedBy).setValue(customMarker);
    }

//    public void getMarkerInfo(String eventName){
//
//        Query query = FirebaseDatabase.getInstance().getReference("markers").orderByChild("eventName").equalTo(eventName);
//
//        query.addListenerForSingleValueEvent();
//
//        ValueEventListener valueEventListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        }
//
//    }

//    public CustomMarker getMarkerInfoNew(final String eventName){
//        mDatabase.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot messageSnapshot: dataSnapshot.getChildren()) {
//                    if(messageSnapshot.getKey().equals(CustomMarker (eventName))){
//                        retrievedUser = messageSnapshot.getValue(User.class);
//                        doSomethingWithTheUser(retrievedUser);
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//
//            @Override
//            public void onCancelled(FirebaseError firebaseError) {}
//        });
//    }


    public void setTargetMarker(CustomMarker customMarker){
        targetMarker = customMarker;
    }
}



