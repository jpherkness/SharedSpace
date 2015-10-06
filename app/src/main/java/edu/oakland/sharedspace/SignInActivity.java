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

public class SignInActivity extends AppCompatActivity implements View.OnClickListener{

    final Firebase ref = new Firebase("https://shared-space.firebaseio.com");

    EditText etEmail, etPassword;
    Button btnSignIn, etCreateAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

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
                        // Authentication just completed successfully :)
                    }
                    @Override
                    public void onAuthenticationError(FirebaseError error) {
                        // Something went wrong :(
                    }
                });

                break;

            case R.id.btnCreateAccount:

                Intent intent = new Intent("edu.oakland.sharedspace.SIGN_UP");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                break;
        }
    }
}

