package edu.oakland.sharedspace;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.firebase.geofire.GeoLocation;

import java.util.Calendar;

/**
 * The MainActivity will function as a splash screen. It will determine whether to launch
 * straight into the application, or authenticate the user.
 *
 * MainActivity -> SignInActivity -> Rest of application
 *
 * @author Joseph
 * @version 1.0 October 6, 2014
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //This is a temporary testing method that adds an event to the database
        Event.addEvent("Study Sesh", "We gonna study! :D", Calendar.getInstance().getTime(), new GeoLocation(1,1));
    }
}
