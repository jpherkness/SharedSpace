package edu.oakland.sharedspace;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import com.firebase.client.Firebase;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CreateEventActivity extends AppCompatActivity implements
        View.OnClickListener,
        DatePickerDialog.OnDateSetListener{

    final Firebase ref = new Firebase("https://shared-space.firebaseio.com");

    private static final int PLACE_PICKER_REQUEST_CODE = 1;

    private EditText etTitle, etDescription, etLocation, etDate;
    private Date date;
    private LatLng eventLocation;
    private List<String> tags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        etTitle = (EditText)findViewById(R.id.etTitle);
        etDescription = (EditText)findViewById(R.id.etDescription);
        etLocation = (EditText)findViewById(R.id.etLocation);
        etLocation.setOnClickListener(this);
        etDate = (EditText) findViewById(R.id.etFromDate);
        etDate.setOnClickListener(this);

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

            addEvent(title, description, Calendar.getInstance().getTime(), location, tags);
            finish();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.etLocation:

                // Show Google Place Picker
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    /**
     * Probably a temporary method that provides the ability to easily add events.
     *
     * Here is a data tree showing how the event information is actually stored in the database
     *
     *  ┌── events
     *  │   └── eventID          <- unique event ID
     *  │       ├── title
     *  │       ├── description
     *  │       └── date
     *  ├── geofire
     *  │   ├── eventID          <- unique event ID
     *  │   │   ├── g            <- encoded geofire data
     *  │   │   └── l
     *  │   │       ├── 0        <- latitude
     *  │   │       └── 1        <- longitude
     *  │   └── bar
     */
    public static void addEvent(String title, String description, Date date, GeoLocation location, List<String> tags){

        // Create a reference to the firebase application
        Firebase ref = new Firebase("https://shared-space.firebaseio.com");

        // Create a event object
        Event event = new Event(ref.getAuth().getUid(), title, description, date, tags);

        // Store the event in the database under a unique identifier (push generates that uid)
        Firebase newEventRef = ref.child("events").push();
        newEventRef.setValue(event);
        String eventID = newEventRef.getKey();

        // Store the location in the database using the same identifier as the event
        Firebase newGeoFireRef = ref.child("geofire");
        GeoFire geoFire = new GeoFire(newGeoFireRef);
        geoFire.setLocation(eventID, location);

        date.setTime(SystemClock.currentThreadTimeMillis());

    }

    public void pickDate(){
        DatePickerDialog datePicker = new DatePickerDialog(this, R.style.AppTheme_DialogTheme ,this, 2015, 11, 21);
        datePicker.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

    }
}
