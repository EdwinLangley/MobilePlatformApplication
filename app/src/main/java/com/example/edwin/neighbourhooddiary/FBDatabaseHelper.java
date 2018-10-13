package com.example.edwin.neighbourhooddiary;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FBDatabaseHelper {

    private DatabaseReference mDatabase;

    public FBDatabaseHelper() {
        mDatabase =  FirebaseDatabase.getInstance().getReference();

        DatabaseReference mUsersReference = FirebaseDatabase.getInstance().getReference().child("users");

    }

    public void writeNewUser(String userId, String name, String email) {
        User user = new User(name, email);
        mDatabase.child("users").child(userId).setValue(user);
    }

    public void writeNewMarker(double lat, double lng, boolean isExpirable, String eventName, String eventType, long startTime, long endTime, String descrip, String addedBy){
        CustomMarker customMarker = new CustomMarker( lat,  lng,  isExpirable,  eventName,  eventType,  startTime,  endTime,  descrip,   addedBy);
        mDatabase.child("markers").child(eventName + addedBy).setValue(customMarker);
    }
}


