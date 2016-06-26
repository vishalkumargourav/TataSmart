package com.tatasmart.in.tatasmart;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

public class SignInActivity extends AppCompatActivity {

    private EditText email,password;
    private Button signin;
    //private CheckBox checkBox;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        email = (EditText)findViewById(R.id.emailInput);
        password = (EditText)findViewById(R.id.passwordInput);
        signin = (Button)findViewById(R.id.signInButton);
        //checkBox = (CheckBox)findViewById(R.id.ch);

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((email.getText().toString().equals("tata_vinod123") && password.getText().toString().equals("erudite"))
                        || (email.getText().toString().equals("tata_mandeep123") && password.getText().toString().equals("erudite"))
                        || (email.getText().toString().equals("tata_maninder123") && password.getText().toString().equals("erudite"))){

                    Intent intent = new Intent("com.tatasmart.in.tatasmart.MainActivity");
                    startActivity(intent);
                }
                else{
                    Toast.makeText(getBaseContext(),"Invalid email or password",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}
