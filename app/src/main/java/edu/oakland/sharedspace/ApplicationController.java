package edu.oakland.sharedspace;

import android.app.Application;

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
    }
    //Appplication wide methods
}
