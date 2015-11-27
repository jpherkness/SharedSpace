package edu.oakland.sharedspace;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

/**
 * The DetailedEventActivity displays detailed information about an event.
 *
 * @author      Joseph Herkness
 * @version     1.0 November 25, 2015
 */
public class DetailedEventActivity extends AppCompatActivity {

    private Event mEvent;

    private TextView titleView, descriptionView, locationView, DateView;
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

        // Map View
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.detailedEventMapView);
        map = mapFragment.getMap();

        mEvent = (Event) getIntent().getExtras().getSerializable("Event");

        if(mEvent != null){
            titleView.setText(mEvent.getTitle());
            descriptionView.setText(mEvent.getDescription());
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
