package edu.oakland.sharedspace;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * The MainEventActivity class is the main view of the application
 * and contains/shows local events based on the users current
 * location.
 *
 * @author      Joseph Herkness
 * @version     1.0 November 12, 2015
 */
public class MainEventActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GeoQueryEventListener,
        SlidingUpPanelLayout.PanelSlideListener,
        AdapterView.OnItemClickListener{

    // Firebase Reference
    final Firebase ref = new Firebase("https://shared-space.firebaseio.com");

    // Contains a list of events
    private ListView list;
    private int originalListHeight;

    // Displays markers to show the location of each event
    private GoogleMap map;
    private SlidingUpPanelLayout slidingUpPanel;
    private GoogleApiClient mGoogleApiClient;
    private GeoFire geoFire;
    private GeoQuery geoQuery;
    private Location mLocation;
    private Circle circle;
    private HashMap<String, Marker> markers = new HashMap<>();
    private ArrayList<Event> events = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_event);

        // Set up the google API client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        // List View
        list = (ListView) findViewById(R.id.eventListView);
        list.setOnItemClickListener(this);
        originalListHeight = list.getMeasuredHeight();

        // Map View
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.eventMapView);
        map = mapFragment.getMap();
        map.setMyLocationEnabled(true);

        // Geofire
        geoFire = new GeoFire(new Firebase("https://shared-space.firebaseio.com/geofire"));

        // Sliding Up Panel Layout
        slidingUpPanel = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        slidingUpPanel.setAnchorPoint(0.6f);
        slidingUpPanel.setClickable(true);
        slidingUpPanel.setPanelSlideListener(this);
    }

    /**
     * Initializes any objects that rely on current location information
     * and updates them if they already exist.
     * */
    public void setup(){

        // Get the users last known location (which is usually their current location)
        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLocation != null) {
            // Setup GeoFire
            GeoLocation center = new GeoLocation(mLocation.getLatitude(), mLocation.getLongitude());

            // Setup the GeoQuery
            if(geoQuery != null){
                geoQuery.setCenter(center);
            }else{
                geoQuery = geoFire.queryAtLocation(center, 1.608);
                geoQuery.addGeoQueryEventListener(this);
            }

            // Setup the circle
            if(circle == null){
                circle = map.addCircle(new CircleOptions()
                        .center(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()))
                        .radius(1608)
                        .strokeColor(ContextCompat.getColor(this, R.color.map_circle_stroke))
                        .fillColor(ContextCompat.getColor(this, R.color.map_circle_fill))
                        .strokeWidth(5.0f));
            }else{
                circle.setCenter(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()));
            }
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mLocation.getLatitude(), mLocation.getLongitude()), 14.0f));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.menu_create_event:
                intent = new Intent(this, CreateEventActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_sign_out:
                // Un-authenticate the user
                ref.unauth();

                // Show the sign in activity
                intent = new Intent(this, SignInActivity.class);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //GoogleApiClient method that is invoked when the client is connected
    @Override
    public void onConnected(Bundle bundle) {
        Log.d("GoogleApiClient", "Google API Client connected");
        setup();
    }

    //GoogleApiClient method that is invoked when the client's connection is suspended
    @Override
    public void onConnectionSuspended(int i) {
        Log.d("GoogleApiClient", "Google API Client connection suspended");
    }

    //GoogleApiClient method that is invoked when the client fails to connect
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("GoogleApiClient", "Failed to connect to the Google API Client");
    }

    //GeoQuery method that is invoked when a key is added to the database
    @Override
    public void onKeyEntered(String key, GeoLocation location) {
        
        // Add a new marker to the map
        final Marker marker = this.map.addMarker(new MarkerOptions()
                .position(new LatLng(location.latitude, location.longitude)));
        markers.put(key, marker);

        // Retrieve the Event object based on its key
        Firebase eventRef = ref.child("events/" + key);
        eventRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Event event = snapshot.getValue(Event.class);
                events.add(event);
                marker.setTitle(event.getTitle());
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    //GeoQuery method that is invoked when a key is removed from the database
    @Override
    public void onKeyExited(String key) {
        // Remove marker
        Marker marker = this.markers.get(key);
        if (marker != null) {
            marker.remove();
            this.markers.remove(key);
        }

        // Retrieve the Event object based on its key, and remove it from the list
        Firebase eventRef = ref.child("events/" + key);
        eventRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Event event = snapshot.getValue(Event.class);
                events.remove(event);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    //GeoQuery method that is invoked when a key in the database changes
    @Override
    public void onKeyMoved(String key, GeoLocation location) {

    }

    //GeoQuery method that is invoked when the query is ready
    @Override
    public void onGeoQueryReady() {
        ListAdapter adapter = new EventListAdapter(getBaseContext(), events);
        list.setAdapter(adapter);
    }

    //GeoQuery method that is invoked when there is an error with the query
    @Override
    public void onGeoQueryError(FirebaseError error) {
        Log.d("Firebase Error", error.toString());
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onPanelSlide(View panel, float slideOffset) {
        list.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                (int) ((slidingUpPanel.getHeight() - 68) * slideOffset)));
    }

    @Override
    public void onPanelCollapsed(View panel) {}

    @Override
    public void onPanelExpanded(View panel) {}

    @Override
    public void onPanelAnchored(View panel) {}

    @Override
    public void onPanelHidden(View panel) {}

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Event eventSelected = (Event) list.getItemAtPosition(position);

        Intent intent = new Intent(this, DetailedEventActivity.class);
        intent.putExtra("Event", eventSelected);
        startActivity(intent);
    }
}