package com.example.edwin.neighbourhooddiary;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MarkerEdit extends AppCompatActivity {

    private Spinner spinner;
    private ImageView markerIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker_edit);

        loadSpinner();

        final LatLng latlng = (LatLng) getIntent().getParcelableExtra("location");

        final EditText title = (EditText) findViewById(R.id.title);
        Button button = (Button) findViewById(R.id.save);
        markerIcon = (ImageView) findViewById(R.id.markerIcon);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                MarkerOptions marker = new MarkerOptions().position(latlng);
                if (title.getText() != null) {
                    marker.title(title.getText().toString());
                }
                marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.post));


                Intent resultIntent = new Intent();
                resultIntent.putExtra("marker", marker);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selection = spinner.getSelectedItem().toString();


                switch (selection) {
                    case "Post":   markerIcon.setImageResource(R.drawable.post);
                        break;
                    case "ATM":  markerIcon.setImageResource(R.drawable.atm);
                        break;
                    case "Public WC":  markerIcon.setImageResource(R.drawable.wc);
                        break;
                    case "Parking":   markerIcon.setImageResource(R.drawable.parking);
                        break;
                    case "Car Charging":  markerIcon.setImageResource(R.drawable.carcharge);
                        break;
                    case "General Attractions":  markerIcon.setImageResource(R.drawable.attraction);
                        break;
                    case "Picnic Areas":  markerIcon.setImageResource(R.drawable.picnic);
                        break;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }

        });
    }


    public void loadSpinner(){
        String[] arraySpinner = new String[] {
                "PostBox", "ATM", "Public WC", "Parking", "Car Charging", "Picnic Areas", "General Attractions"
        };

        spinner = (Spinner) findViewById(R.id.spinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }
}
