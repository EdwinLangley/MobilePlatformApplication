package com.example.edwin.neighbourhooddiary;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class MarkerEdit extends AppCompatActivity {

    private Spinner spinner;
    private ImageView markerIcon;
    private Button saveButton;
    private Button startTimeButton;
    private Button endTimeButton;
    private TextView startTimeText;
    private TextView endTimeText;
    private EditText descriptionTextView;
    private CheckBox expirableCheckbox;


    private long startTime;
    private long endTime;
    private String description;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker_edit);

        loadSpinner();

        final LatLng latlng = (LatLng) getIntent().getParcelableExtra("location");

        final EditText title = (EditText) findViewById(R.id.title);
        saveButton = (Button) findViewById(R.id.save);
        markerIcon = (ImageView) findViewById(R.id.markerIcon);
        startTimeButton = (Button) findViewById(R.id.startTimeButton);
        endTimeButton = (Button) findViewById(R.id.endTimebutton);
        startTimeText = (TextView) findViewById(R.id.startTimeTextView);
        endTimeText = (TextView) findViewById(R.id.endTimeTextView);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                MarkerOptions marker = new MarkerOptions().position(latlng);
                if (title.getText() != null) {
                    marker.title(title.getText().toString());
                }

                String eventType = "";
                eventType = spinner.getSelectedItem().toString();

                switch (eventType) {
                    case "PostBox":   marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.post));
                        break;
                    case "ATM":  marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.atm));
                        break;
                    case "Public WC":  marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.wc));
                        break;
                    case "Parking":   marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.parking));
                        break;
                    case "Car Charging":  marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.carcharge));
                        break;
                    case "General Attractions":  marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.attraction));
                        break;
                    case "Picnic Areas":  marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.picnic));
                        break;

                }

                descriptionTextView = (EditText) findViewById(R.id.descriptionEditText);
                description = descriptionTextView.getText().toString();

                boolean isExpirable = false;
                expirableCheckbox = (CheckBox) findViewById(R.id.expirableCheckBox);
                if(expirableCheckbox.isChecked()){
                    isExpirable = true;
                } else {
                    isExpirable = false;
                }



                Intent resultIntent = new Intent();

                resultIntent.putExtra("eventType",eventType);
                resultIntent.putExtra("description", description);
                resultIntent.putExtra("marker", marker);
                resultIntent.putExtra("startTime",startTime);
                resultIntent.putExtra("endTime",endTime);
                resultIntent.putExtra("expirable",isExpirable);

                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selection = spinner.getSelectedItem().toString();


                switch (selection) {
                    case "PostBox":   markerIcon.setImageResource(R.drawable.post);
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

        startTimeButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                openStartTime();
            }
        });

        endTimeButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                openEndTime();
            }
        });


    }

    public void openStartTime(){
        final View dialogView = View.inflate(MarkerEdit.this, R.layout.date_time_picker, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(MarkerEdit.this).create();

        final DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.date_picker);
        final TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.time_picker);
        final Button setDate = (Button) dialogView.findViewById(R.id.date_set);
        final Button setTime = (Button) dialogView.findViewById(R.id.time_set);

        dialogView.findViewById(R.id.date_set).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePicker.setVisibility(View.GONE);
                setDate.setVisibility(View.GONE);
                setTime.setVisibility(View.VISIBLE);
                timePicker.setVisibility(View.VISIBLE);
            }});

        dialogView.findViewById(R.id.time_set).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = new GregorianCalendar(datePicker.getYear(),
                        datePicker.getMonth(),
                        datePicker.getDayOfMonth(),
                        timePicker.getHour(),
                        timePicker.getMinute());
                startTime = calendar.getTimeInMillis();

                Date currentDate = new Date(startTime);

                DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

                startTimeText.setText(df.format(currentDate));

                alertDialog.dismiss();
            }
        });
        alertDialog.setView(dialogView);
        alertDialog.show();
    }

    public void openEndTime(){
        final View dialogView = View.inflate(MarkerEdit.this, R.layout.date_time_picker, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(MarkerEdit.this).create();

        final DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.date_picker);
        final TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.time_picker);
        final Button setDate = (Button) dialogView.findViewById(R.id.date_set);
        final Button setTime = (Button) dialogView.findViewById(R.id.time_set);

        dialogView.findViewById(R.id.date_set).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePicker.setVisibility(View.GONE);
                setDate.setVisibility(View.GONE);
                setTime.setVisibility(View.VISIBLE);
                timePicker.setVisibility(View.VISIBLE);
            }});

        dialogView.findViewById(R.id.time_set).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = new GregorianCalendar(datePicker.getYear(),
                        datePicker.getMonth(),
                        datePicker.getDayOfMonth(),
                        timePicker.getHour(),
                        timePicker.getMinute());
                endTime = calendar.getTimeInMillis();

                Date currentDate = new Date(endTime);

                DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

                endTimeText.setText(df.format(currentDate));

                alertDialog.dismiss();
            }
        });
        alertDialog.setView(dialogView);
        alertDialog.show();
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
