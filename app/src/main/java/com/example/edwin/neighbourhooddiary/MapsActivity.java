package com.example.edwin.neighbourhooddiary;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Build;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraMoveListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.nio.charset.Charset;
import java.util.ArrayList;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, NfcAdapter.OnNdefPushCompleteCallback, NfcAdapter.CreateNdefMessageCallback {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private static final int EDIT_REQUEST = 1;
    private FBDatabaseHelper fbDatabaseHelper = new FBDatabaseHelper();
    GoogleSignInAccount acct;
    DatabaseReference mMarkerReference;
    ArrayList<CustomMarker> activeCustomMarkers = new ArrayList<>();
    ArrayList<Marker> drawnMarkers = new ArrayList<>();

    private NfcAdapter mNfcAdapter;

    private int markerHeight = 100;
    private int markerWidth = 100;
    private String[] imageUrls;

// =====================================================================
// NAME:
// PURPOSE:
// =====================================================================


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        acct = getIntent().getParcelableExtra("acct");

        mMarkerReference = FirebaseDatabase.getInstance().getReference().child("markers");


        ValueEventListener markerListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                loadInMarkers(dataSnapshot);
                loadOntoMap();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message

                // ...
            }
        };
        mMarkerReference.addValueEventListener(markerListener);

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if(mNfcAdapter != null) {
            //This will refer back to createNdefMessage for what it will send
            mNfcAdapter.setNdefPushMessageCallback(this, this);

            //This will be called if the message is sent successfully
            mNfcAdapter.setOnNdefPushCompleteCallback(this, this);
        }
        else {
            Toast.makeText(this, "NFC unavailable",
                    Toast.LENGTH_SHORT).show();
        }

    }

// =====================================================================
// NAME:
// PURPOSE:
// =====================================================================


    public Bitmap resizeMapIcons(String iconName, int width, int height){
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(),getResources().getIdentifier(iconName, "drawable", getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }

// =====================================================================
// NAME:
// PURPOSE:
// =====================================================================

    private void loadOntoMap() {
        for(CustomMarker customMarker : activeCustomMarkers){

            MarkerOptions markerOptions = new MarkerOptions();

            String eventType = customMarker.getEventType();
            switch (eventType) {
                case "PostBox":    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("post",markerWidth,markerHeight)));
                    break;
                case "ATM":  markerOptions.icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("atm",markerWidth,markerHeight)));
                    break;
                case "Public WC":  markerOptions.icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("wc",markerWidth,markerHeight)));
                    break;
                case "Parking":  markerOptions.icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("parking",markerWidth,markerHeight)));
                    break;
                case "Car Charging":  markerOptions.icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("carcharge",markerWidth,markerHeight)));
                    break;
                case "General Attractions":  markerOptions.icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("attraction",markerWidth,markerHeight)));
                    break;
                case "Picnic Areas":  markerOptions.icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("picnic",markerWidth,markerHeight)));
                    break;
            }

            markerOptions.position(new LatLng(customMarker.getLat(), customMarker.getLng()));
            markerOptions.snippet(customMarker.getDescrip());
            markerOptions.title(customMarker.getEventName());

            Marker marker = mMap.addMarker(markerOptions);
            marker.setVisible(false);
            drawnMarkers.add(marker);

        }
    }

// =====================================================================
// NAME:
// PURPOSE:
// =====================================================================

    private void loadInMarkers(DataSnapshot dataSnapshot) {
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

            activeCustomMarkers.add(customMarker);

        }
    }


    public void displayEventContentDialog(){

        imageUrls = new String[]{
                "https://cdn.pixabay.com/photo/2016/11/11/23/34/cat-1817970_960_720.jpg",
                "https://cdn.pixabay.com/photo/2017/12/21/12/26/glowworm-3031704_960_720.jpg",
                "https://cdn.pixabay.com/photo/2017/12/24/09/09/road-3036620_960_720.jpg",
                "https://cdn.pixabay.com/photo/2017/11/07/00/07/fantasy-2925250_960_720.jpg",
                "https://cdn.pixabay.com/photo/2017/10/10/15/28/butterfly-2837589_960_720.jpg"
        };


        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MapsActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_event_content_screen, null);

        //Button dismissButton = (Button) mView.findViewById(R.id.dismissButton);

        ViewPager viewPager = mView.findViewById(R.id.view_pager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(this, imageUrls);
        viewPager.setAdapter(adapter);


        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();


    }


// =====================================================================
// NAME:
// PURPOSE:
// =====================================================================

    @Override
    public void onMapReady(final GoogleMap mMap) {
        this.mMap = mMap;
        if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
            return;
        }
        this.mMap.setMyLocationEnabled(true);

        //mMap.setInfoWindowAdapter(new InfoWindowCustom(this));

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                displayEventContentDialog();
            }
        });

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
        if (location != null)
        {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 13));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                    .zoom(17)                   // Sets the zoom
                    .bearing(0)                // Sets the orientation of the camera to east
                    .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }


        this.mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(final LatLng latLng) {
                Intent edit = new Intent(MapsActivity.this, MarkerEdit.class);
                edit.putExtra("location", latLng);
                MapsActivity.this.startActivityForResult(edit, EDIT_REQUEST);
            }
        });

        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                CameraPosition cameraPosition = mMap.getCameraPosition();
                    for(Marker m : drawnMarkers){
                        m.setVisible(cameraPosition.zoom > 13.5);
                    }
            }
        });

    }

// =====================================================================
// NAME:
// PURPOSE:
// =====================================================================

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (EDIT_REQUEST) : {
                if (resultCode == Activity.RESULT_OK) {
                    MarkerOptions markerOptions = data.getParcelableExtra("marker");
                    String description = data.getStringExtra("description");
                    boolean isExpirable = data.getBooleanExtra("expirable",false);
                    long startTime = data.getLongExtra("startTime",0L);
                    long endTime = data.getLongExtra("endTime",0L);
                    String eventType = data.getStringExtra("eventType");
                    mMap.addMarker(markerOptions);
                    Double lat = markerOptions.getPosition().latitude;
                    Double lng = markerOptions.getPosition().longitude;

                    fbDatabaseHelper.writeNewMarker(lat,lng,isExpirable,markerOptions.getTitle(),eventType,startTime,endTime,description,acct.getDisplayName());
                }
                break;
            }
        }
    }

// =====================================================================
// NAME:
// PURPOSE:
// =====================================================================


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mMap.setMyLocationEnabled(true);
            }
        }
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

            byte[] payload = "test".
                    getBytes(Charset.forName("UTF-8"));

            record = new NdefRecord(
                    NdefRecord.TNF_WELL_KNOWN,      //Our 3-bit Type name format
                    NdefRecord.RTD_TEXT,            //Description of our payload
                    new byte[0],                    //The optional id for our Record
                    payload);                       //Our payload for the Record

        }
        //Api is high enough that we can use createMime, which is preferred.
        else {
            byte[] payload = "test".getBytes(Charset.forName("UTF-8"));

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
}









