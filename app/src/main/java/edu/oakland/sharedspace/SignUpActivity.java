package edu.oakland.sharedspace;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.HashMap;
import java.util.Map;

/**
 * The SignUpActivity will register a new user, then authenticate that user so that they can access our database.
 *
 * @author      Joseph Herkness
 * @version     1.0 October 9, 2015
 */
public class SignUpActivity extends AppCompatActivity{

    final Firebase ref = new Firebase("https://shared-space.firebaseio.com");

    private EditText etFirstNameSignUp, etLastNameSignUp, etEmailSignUp, etPasswordSignUp;
    private Button btnSignUp;

    private String uid;
    private String email;
    private String password;

    private Intent onAuthenticate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        onAuthenticate = new Intent(this, MainEventActivity.class);

        // Get our views from their ids
        etFirstNameSignUp = (EditText) findViewById(R.id.etFirstNameSignUp);
        etLastNameSignUp = (EditText) findViewById(R.id.etLastNameSignUp);
        etEmailSignUp = (EditText) findViewById(R.id.etEmailSignUp);
        etPasswordSignUp = (EditText) findViewById(R.id.etPasswordSignUp);

        // Adds a back button the the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void signUp(View view) {

        // Get the email and password the user entered
        email = etEmailSignUp.getText().toString();
        password = etPasswordSignUp.getText().toString();

        // Create a user with that email and password
        ref.createUser(email, password, new Firebase.ValueResultHandler<Map<String, Object>>() {
            @Override
            public void onSuccess(Map<String, Object> result) {
                // Authenticate the user
                uid = result.get("uid").toString();
                ref.authWithPassword(email, password, new Firebase.AuthResultHandler() {
                    @Override
                    public void onAuthenticated(AuthData authData) {
                        // Add the users information to the database
                        String userID = uid;
                        String firstName = etFirstNameSignUp.getText().toString();
                        String lastName = etLastNameSignUp.getText().toString();

                        Map<String, String> map = new HashMap<String, String>();
                        map.put("first", firstName);
                        map.put("last", lastName);

                        ref.child("users").child(userID).setValue(map);

                        startActivity(onAuthenticate);
                        finish();
                    }

                    @Override
                    public void onAuthenticationError(FirebaseError error) {
                        // Something went wrong, user was not authenticated

                        // We need to add some kind of popup or massage so the user knows the error
                    }
                });
            }

            @Override
            public void onError(FirebaseError firebaseError) {
                switch (firebaseError.getCode()) {
                    case FirebaseError.EMAIL_TAKEN:
                        etEmailSignUp.setError(getResources().getString(R.string.err_email_taken));
                        break;
                }
            }
        });

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
