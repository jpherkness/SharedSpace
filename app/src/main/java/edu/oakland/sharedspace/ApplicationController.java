package edu.oakland.sharedspace;

import android.app.Application;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;

/**
 * The ApplicationController class maintains global state across the application
 *
 * @author      Joseph Herkness
 * @version     1.0 October 4, 2015
 */
public class ApplicationController extends Application {

    //Application wide instance variables go here
    //Preferable to expose them via getter/setter methods

    @Override
    public void onCreate() {
        super.onCreate();

        //Do Application initialization over here
        Firebase.setAndroidContext(this);

        Firebase ref = new Firebase("https://shared-space.firebaseio.com");

        //Listens to changes in the authentication state
        ref.addAuthStateListener(new Firebase.AuthStateListener() {

            //This will be called whenever the authentication state changes
            @Override
            public void onAuthStateChanged(AuthData authData) {
                if (authData != null) {
                    // user is logged in
                } else {
                    // user is not logged in
                }
            }
        });

    }
    //Appplication wide methods
}
