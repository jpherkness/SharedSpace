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

public class MainEventActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GeoQueryEventListener,
        SlidingUpPanelLayout.PanelSlideListener{

    final Firebase ref = new Firebase("https://shared-space.firebaseio.com");

    private ListView listView;
    private GoogleMap map;
    private SupportMapFragment mapFragment;
    private SlidingUpPanelLayout slidingUpPanel;

    private GoogleApiClient mGoogleApiClient;
    private GeoFire geoFire;
    private GeoQuery geoQuery;

    private Location mLocation;
    private Circle circle;
    private HashMap<String, Marker> markers = new HashMap<>();
    private ArrayList<Event> events = new ArrayList<>();
    private Marker mMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_event);

        // Set up the google API client
        buildGoogleApiClient();

        // List View
        listView = (ListView) findViewById(R.id.eventListView);

        // Map View
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.eventMapView);
        map = mapFragment.getMap();
        map.setMyLocationEnabled(true);

        // Geofire
        geoFire = new GeoFire(new Firebase("https://shared-space.firebaseio.com/geofire"));

        //Sliding Up Panel Layout
        slidingUpPanel = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        slidingUpPanel.setAnchorPoint(0.6f);
        slidingUpPanel.setClickable(true);
        slidingUpPanel.setPanelSlideListener(this);
    }

    public void update(){
        map.clear();

        events.clear();
        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLocation != null) {
            // setup GeoFire
            this.geoFire = new GeoFire(new Firebase("https://shared-space.firebaseio.com/geofire"));
            GeoLocation center = new GeoLocation(mLocation.getLatitude(), mLocation.getLongitude());
            this.geoQuery = this.geoFire.queryAtLocation(center, 1.608);
            this.geoQuery.removeAllListeners();
            this.geoQuery.addGeoQueryEventListener(this);

            if(circle == null){
                this.circle = map.addCircle(new CircleOptions()
                        .center(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()))
                        .radius(1608)
                        .strokeColor(ContextCompat.getColor(this, R.color.map_circle_stroke))
                        .fillColor(ContextCompat.getColor(this, R.color.map_circle_fill))
                        .strokeWidth(5.0f));
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

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    //GoogleApiClient method that is invoked when the client is connected
    @Override
    public void onConnected(Bundle bundle) {
        Log.d("GoogleApiClient", "Google API Client connected");
        update();
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

        Firebase eventRef = ref.child("events/" + key);
        eventRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Event event = snapshot.getValue(Event.class);
                events.add(event);
                marker.setTitle(event.getTitle());

                ListAdapter adapter = new EventListAdapter(getBaseContext(), events);
                listView.setAdapter(adapter);
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
    }

    //GeoQuery method that is invoked when a key in the database changes
    @Override
    public void onKeyMoved(String key, GeoLocation location) {

    }

    //GeoQuery method that is invoked when the query is ready
    @Override
    public void onGeoQueryReady() {

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
    public void onPause() {
        super.onPause();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();

        // remove all event listeners to stop updating in the background
        if(this.geoQuery != null){
            this.geoQuery.removeAllListeners();
        }
    }

    @Override
    public void onPanelSlide(View panel, float slideOffset) {
        listView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                (int) (slidingUpPanel.getHeight() * slideOffset) - 68));
    }

    @Override
    public void onPanelCollapsed(View panel) {}

    @Override
    public void onPanelExpanded(View panel) {
    }

    @Override
    public void onPanelAnchored(View panel) {}

    @Override
    public void onPanelHidden(View panel) {}

}