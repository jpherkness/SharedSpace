package edu.oakland.sharedspace;

import android.app.Application;
import android.content.Intent;

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

        // Required for successful firebase implementation
        Firebase.setAndroidContext(this);

        // Listens to changes in the authentication state
        Firebase ref = new Firebase("https://shared-space.firebaseio.com");
        ref.addAuthStateListener(new Firebase.AuthStateListener() {

            @Override
            public void onAuthStateChanged(AuthData authData) {
                if (authData != null) {
                    // There is a user logged in
                    Firebase ref = new Firebase("https://shared-space.firebaseio.com");
                    ref.unauth();
                } else {
                    // There is NOT a user logged in, so show login activity
                    Intent intent = new Intent("edu.oakland.sharedspace.SIGN_IN");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }

        });

    }
    // Application wide methods go here
}
