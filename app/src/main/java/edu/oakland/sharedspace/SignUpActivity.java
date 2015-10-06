package edu.oakland.sharedspace;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener{

    final Firebase ref = new Firebase("https://shared-space.firebaseio.com");

    EditText etFirstNameSignUp;
    EditText etLastNameSignUp;
    EditText etEmailSignUp;
    EditText etPasswordSignUp;
    Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

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

                //Get the email and password that the user entered
                String email = etEmailSignUp.getText().toString();
                String password = etPasswordSignUp.getText().toString();

                //Create a user with that email and password
                ref.createUser(email, password, new Firebase.ValueResultHandler<Map<String, Object>>() {
                    @Override
                    public void onSuccess(Map<String, Object> result) {
                        // User creation completed successfully :)
                        //Add the user to the tree
                        String userID = result.get("uid").toString();
                        String firstName = etFirstNameSignUp.getText().toString();
                        String lastName = etLastNameSignUp.getText().toString();

                        Map<String, String> map = new HashMap<String, String>();
                        map.put("first", firstName);
                        map.put("last", lastName);

                        ref.child("users").child(userID).setValue(map);
                    }
                    @Override
                    public void onError(FirebaseError firebaseError) {
                        // Something went wrong :(
                    }
                });

                break;

        }
    }
}
