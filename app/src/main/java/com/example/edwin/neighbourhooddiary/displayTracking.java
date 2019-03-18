package com.example.edwin.neighbourhooddiary;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class displayTracking extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    DatabaseReference mMarkerReference;
    DatabaseReference uReference;
    GoogleSignInAccount acct;
    ArrayList<User> allUsers;
    User thisUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_tracking);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        acct = getIntent().getParcelableExtra("acct");

        allUsers = new ArrayList<>();


        mMarkerReference = FirebaseDatabase.getInstance().getReference().child("markers");
        uReference = FirebaseDatabase.getInstance().getReference().child("users");


        ValueEventListener markerListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                LoadInLocations(dataSnapshot);
                locateCurrentUser();


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message

                // ...
            }
        };
        uReference.addValueEventListener(markerListener);

    }


    public void LoadInLocations(DataSnapshot dataSnapshot) {
        allUsers.clear();
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            User user = ds.getValue(User.class);

            allUsers.add(user);

        }
    }

    public void locateCurrentUser() {

        thisUser = new User();

        for (User u : allUsers) {
            String uName = u.getDisplayName();
            String aName = acct.getDisplayName();

            if (uName.matches(aName)) {
                thisUser = u;
            }
        }

        splitIntoLongAndLat();
    }

    public void splitIntoLongAndLat() {

        ArrayList<Double> longs = new ArrayList<>();
        ArrayList<Double> lats = new ArrayList<>();

        Map<String, String> latlngMap = thisUser.getGpsLocations();

        ArrayList<String> latlngString = new ArrayList<String>(latlngMap.values());

        for (String s : latlngString) {

            String snew = s.replace(",", ".");
            snew = snew.replace("lat", "");

            String[] splits = snew.split("lng");

            String LongStr = splits[0];
            String LatStr = splits[1];

            longs.add(Double.parseDouble(LongStr));
            lats.add(Double.parseDouble(LatStr));
        }

        mapPoints(longs, lats);

    }

    public void mapPoints(ArrayList<Double> longs, ArrayList<Double> lats) {

        ArrayList<LatLng> points = new ArrayList<LatLng>();
        PolylineOptions polyLineOptions = new PolylineOptions();

        for (int i = 0; i < lats.size() - 1; i++) {
            points.add(new LatLng(lats.get(i), longs.get(i)));
            points.add(new LatLng(lats.get(i + 1), longs.get(i + 1)));
        }

        double startLatitude = points.get(0).latitude;
        double startLongitude = points.get(0).longitude;
        double endLatitude = points.get(points.size() - 1).latitude;
        double endLongitude = points.get(points.size() - 1).longitude;
        LatLng start = new LatLng(startLatitude, startLongitude);
        LatLng end = new LatLng(endLatitude, endLongitude);

        mMap.addMarker(new MarkerOptions().position(start).title("start"));
        mMap.addMarker(new MarkerOptions().position(end).title("end"));

        polyLineOptions.width(7 * 1);
        polyLineOptions.geodesic(true);
        polyLineOptions.color(getResources().getColor(R.color.black));
        polyLineOptions.addAll(points);
        Polyline polyline = mMap.addPolyline(polyLineOptions);
        polyline.setGeodesic(true);


    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Add a marker in Sydney and move the camera
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
    }
}
