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

/**
 * The SignInActivity will authenticate the user so that they can access our database.
 *
 * @author      Joseph Herkness
 * @version     1.0 October 9, 2015
 */
public class SignInActivity extends AppCompatActivity implements View.OnClickListener{

    final Firebase ref = new Firebase("https://shared-space.firebaseio.com");

    Intent onAuthenticate;

    EditText etEmail, etPassword;
    Button btnSignIn, etCreateAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        onAuthenticate = new Intent(this, CreateEventActivity.class);

        // Set the title of the activity
        setTitle("Sign in to Shared Space");

        // Get our views from their ids
        etEmail = (EditText) findViewById(R.id.etEmailSignIn);
        etPassword = (EditText) findViewById(R.id.etPasswordSignIn);
        btnSignIn = (Button) findViewById(R.id.btnSignIn);
        etCreateAccount = (Button) findViewById(R.id.btnCreateAccount);

        btnSignIn.setOnClickListener(this);
        etCreateAccount.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btnSignIn:

                //Retrieve the email and password that the user entered
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();

                // Send Authentication request
                ref.authWithPassword(email, password, new Firebase.AuthResultHandler() {
                    @Override
                    public void onAuthenticated(AuthData authData) {
                        // User was authenticated

                        startActivity(onAuthenticate);
                    }

                    @Override
                    public void onAuthenticationError(FirebaseError error) {
                        // Something went wrong, user was not authenticated
                    }
                });


                break;

            case R.id.btnCreateAccount:

                Intent intent = new Intent(this, SignUpActivity.class);
                startActivity(intent);

                break;
        }
    }
}

