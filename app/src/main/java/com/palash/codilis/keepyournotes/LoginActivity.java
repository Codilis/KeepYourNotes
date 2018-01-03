package com.palash.codilis.keepyournotes;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    EditText pin;
    Button login, button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        pin = (EditText) findViewById(R.id.PIN);

        login = (Button) findViewById(R.id.login);
        login.setOnClickListener(this);

        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        String data = pin.getText().toString();
        switch (v.getId()) {

            case R.id.login:
                if (data.equals(getPin())) {
                    Intent in = new Intent(LoginActivity.this, PrivateNote.class);
                    LoginActivity.this.startActivity(in);
                    finish();

                } else {
                    Toast.makeText(getApplicationContext(), "Wrong PIN", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.button:
                if (data.equals(getPin())) {
                    Intent in = new Intent(LoginActivity.this, ChangePin.class);
                    LoginActivity.this.startActivity(in);

                } else {
                    Toast.makeText(getApplicationContext(), "Wrong PIN", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                break;
        }
    }

    public String getPin() {
        String Pin = "";
        String filename = getString(R.string.password);
        StringBuffer stringBuffer = new StringBuffer();
        try {
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(
                    openFileInput(filename)));
            String inputString;

            while ((inputString = inputReader.readLine()) != null) {
                stringBuffer.append(inputString);
            }
            Pin = stringBuffer.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return Pin;
    }

    public void onBackPressed() {
        super.onBackPressed();
        finish();

    }
}
