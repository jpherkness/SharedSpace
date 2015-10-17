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
public class SignInActivity extends AppCompatActivity {

    final Firebase ref = new Firebase("https://shared-space.firebaseio.com");

    private Intent onAuthenticate;

    private EditText etEmailSignIn, etPasswordSignIn;
    private Button btnSignIn, etCreateAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        onAuthenticate = new Intent(this, CreateEventActivity.class);

        // Get our views from their ids
        etEmailSignIn = (EditText) findViewById(R.id.etEmailSignIn);
        etPasswordSignIn = (EditText) findViewById(R.id.etPasswordSignIn);
        btnSignIn = (Button) findViewById(R.id.btnSignIn);
        etCreateAccount = (Button) findViewById(R.id.btnCreateAccount);
    }

    public void signIn(View view) {

        //Retrieve the email and password that the user entered
        String email = etEmailSignIn.getText().toString();
        String password = etPasswordSignIn.getText().toString();

        // Send Authentication request
        ref.authWithPassword(email, password, new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                startActivity(onAuthenticate);
            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                switch (firebaseError.getCode()){
                    case FirebaseError.INVALID_EMAIL:
                        etEmailSignIn.setError(getResources().getString(R.string.err_email_does_not_exist));
                        break;
                    case FirebaseError.INVALID_PASSWORD:
                        etPasswordSignIn.setError(getResources().getString(R.string.err_incorrect_password));
                        break;
                }
            }
        });

    }

    public void createAccount(View view) {

        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);

    }
}

