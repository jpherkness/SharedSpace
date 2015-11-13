package edu.oakland.sharedspace;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.firebase.client.Firebase;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

import java.util.Calendar;

public class CreateEventActivity extends AppCompatActivity{

    final Firebase ref = new Firebase("https://shared-space.firebaseio.com");

    private static final int PLACE_PICKER_REQUEST_CODE = 1;

    private EditText etTitle, etDescription, etLocation;

    private LatLng eventLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        etTitle = (EditText)findViewById(R.id.etTitle);
        etDescription = (EditText)findViewById(R.id.etDescription);
        etLocation = (EditText)findViewById(R.id.etLocation);
        etLocation.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                try {
                    PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
                    Intent intent = intentBuilder.build(getApplicationContext());
                    startActivityForResult(intent, PLACE_PICKER_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

        // Adds a back button the the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PLACE_PICKER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            // The user has selected a place. Extract the name and possibly address
            final Place place = PlacePicker.getPlace(data, this);
            eventLocation = place.getLatLng();
            etLocation.setText(place.getName());

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void createEvent(View view) {
        // Create a new event if the input is valid
        if(isInputValid()){
            String title = etTitle.getText().toString();
            String description = etDescription.getText().toString();
            GeoLocation location = new GeoLocation(eventLocation.latitude, eventLocation.longitude);

            Event.addEvent(title, description, Calendar.getInstance().getTime(), location);
        }
    }

    public boolean isInputValid(){
        Boolean valid = true;

        if(eventLocation == null){
            etLocation.setError(getResources().getString(R.string.err_invalid_location));
            valid = false;
        }

        String title = etTitle.getText().toString();
        if(title.length() == 0){
            etTitle.setError(getResources().getString(R.string.err_invalid_title));
            valid = false;
        }

        return valid;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
