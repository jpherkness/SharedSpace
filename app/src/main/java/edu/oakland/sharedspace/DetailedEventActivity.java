package edu.oakland.sharedspace;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * The DetailedEventActivity displays detailed information about an event.
 *
 * @author      Joseph Herkness
 * @version     1.0 November 25, 2015
 */
public class DetailedEventActivity extends AppCompatActivity {

    private Event mEvent;

    private TextView titleView, descriptionView, locationView, dateView;
    private GoogleMap map;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_event);

        // Adds a back button the the action bar
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }

        // Get a reference to each view
        titleView = (TextView) findViewById(R.id.detailedEventTitle);
        descriptionView = (TextView) findViewById(R.id.detailedEventDescription);
        dateView = (TextView)findViewById(R.id.detailedEventDate);

        // Map View
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.detailedEventMapView);
        map = mapFragment.getMap();

        // Get the event object
        mEvent = (Event) getIntent().getExtras().getSerializable("Event");

        if(mEvent != null){
            setTitle("Events");
            titleView.setText(mEvent.getTitle());
            descriptionView.setText(mEvent.getDescription());

            // Update the map
            LatLng eventLocation = new LatLng(mEvent.getLatitude(), mEvent.getLongitude());
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(eventLocation, 17.0f));
            final Marker marker = this.map.addMarker(new MarkerOptions()
                    .position(new LatLng(mEvent.getLatitude(), mEvent.getLongitude())));

            // set date
            dateView.setText(mEvent.getDate());

        }else{
            this.finish();
        }
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
