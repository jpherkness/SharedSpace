package edu.oakland.sharedspace;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

    public void signOut(View view) {

        // Un-authenticate the user
        ref.unauth();

        // Show sign in activity
        Intent signIn = new Intent(this, SignInActivity.class);
        startActivity(signIn);
    }

    public void createEvent(View view) {
        // Retrieve information about event
        String title = etTitle.getText().toString();
        String description = etDescription.getText().toString();

        // Check to make sure the event information is valid and create the event if it is
        if(eventLocation != null && title.length() > 0){
            GeoLocation location = new GeoLocation(eventLocation.latitude, eventLocation.longitude);
            Event.addEvent(title, description, Calendar.getInstance().getTime(), location);
        }else{
            if(eventLocation == null){
                etLocation.setError("You must enter a valid location");
            }
            if(title.length() == 0){
                etTitle.setError("You must enter a valid title");
            }
        }
    }
}
