package edu.oakland.sharedspace;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
public class SignUpActivity extends AppCompatActivity implements View.OnClickListener{

    final Firebase ref = new Firebase("https://shared-space.firebaseio.com");

    EditText etFirstNameSignUp, etLastNameSignUp, etEmailSignUp, etPasswordSignUp;
    Button btnSignUp;

    String uid;
    String email;
    String password;

    Intent onAuthenticate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        onAuthenticate = new Intent(this, CreateEventActivity.class);

        // Get our views from their ids
        etFirstNameSignUp = (EditText) findViewById(R.id.etFirstNameSignUp);
        etLastNameSignUp = (EditText) findViewById(R.id.etLastNameSignUp);
        etEmailSignUp = (EditText) findViewById(R.id.etEmailSignUp);
        etPasswordSignUp = (EditText) findViewById(R.id.etPasswordSignUp);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);

        btnSignUp.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()) {
            case R.id.btnSignUp:

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
                        // Something went wrong, user was not created

                        // We need to add some kind of popup or massage so the user knows the error
                    }
                });

                break;

        }
    }
}
