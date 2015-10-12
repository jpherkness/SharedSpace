package edu.oakland.sharedspace;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.firebase.client.Firebase;

public class CreateEventActivity extends AppCompatActivity implements View.OnClickListener{

    final Firebase ref = new Firebase("https://shared-space.firebaseio.com");

    Button btnSignOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        btnSignOut = (Button)findViewById(R.id.btnSignOut);

        btnSignOut.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnSignOut:
                ref.unauth();
                Intent intent = new Intent(this, SignInActivity.class);
                startActivity(intent);
                break;
        }
    }
}
