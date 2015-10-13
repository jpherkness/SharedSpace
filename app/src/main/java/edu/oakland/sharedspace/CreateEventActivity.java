package edu.oakland.sharedspace;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.Calendar;

public class CreateEventActivity extends AppCompatActivity{

    final Firebase ref = new Firebase("https://shared-space.firebaseio.com");

    private static final int PLACE_PICKER_REQUEST = 1;

    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.972090));

    private Button btnSignOut, btnCreateEvent, btnSelectLocation;
    private EditText etTitle, etDescription;
    private TextView tvLocation;

    private LatLng eventLocation;

    private Intent signIn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        signIn = new Intent(this, SignInActivity.class);
        etTitle = (EditText)findViewById(R.id.etTitle);
        etDescription = (EditText)findViewById(R.id.etDescription);

        btnSignOut = (Button)findViewById(R.id.btnSignOut);
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ref.unauth();
                startActivity(signIn);
            }
        });

        btnSelectLocation = (Button)findViewById(R.id.btnSelectLocation);
        btnSelectLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
                    Intent intent = intentBuilder.build(getApplicationContext());
                    startActivityForResult(intent, PLACE_PICKER_REQUEST);

                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

        btnCreateEvent = (Button)findViewById(R.id.btnCreateEvent);
        btnCreateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Retrieve information about event
                String title = etTitle.getText().toString();
                String description = etDescription.getText().toString();
                GeoLocation location = new GeoLocation(eventLocation.latitude, eventLocation.longitude);

                //Add the event to the database
                Event.addEvent(title, description, Calendar.getInstance().getTime(), location);
            }
        });


        tvLocation = (TextView)findViewById(R.id.tvLocation);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PLACE_PICKER_REQUEST && resultCode == Activity.RESULT_OK) {

            // The user has selected a place. Extract the name and address.
            final Place place = PlacePicker.getPlace(data, this);

            eventLocation = place.getLatLng();
            final CharSequence name = place.getName();
            tvLocation.setText(name);

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
