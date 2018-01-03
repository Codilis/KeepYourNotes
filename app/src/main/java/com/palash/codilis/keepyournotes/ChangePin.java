package com.palash.codilis.keepyournotes;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ChangePin extends AppCompatActivity implements View.OnClickListener {

    EditText newPin, confirmPin;
    Button OK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_pin);
        newPin = (EditText) findViewById(R.id.newPin);
        confirmPin = (EditText) findViewById(R.id.confirmPin);

        OK = (Button) findViewById(R.id.OK);
        OK.setOnClickListener(this);
    }

    public void savePin(String Pin) {
        String filename = getString(R.string.password);
        String data = Pin;

        FileOutputStream fos;
        try {
            fos = openFileOutput(filename, Context.MODE_PRIVATE);
            fos.write(data.getBytes());
            fos.close();

            Toast.makeText(getApplicationContext(), "PIN saved", Toast.LENGTH_LONG).show();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {

        String newpin = newPin.getText().toString();
        String confirmpin = confirmPin.getText().toString();

        switch (v.getId()) {
            case R.id.OK:
                if (newpin.equals(confirmpin) && !newpin.equals("")) {
                    savePin(newpin);
                    Intent in = new Intent(ChangePin.this, PrivateNote.class);
                    ChangePin.this.startActivity(in);
                    finish();
                } else if (!newpin.equals(confirmpin)) {
                    Toast.makeText(getApplicationContext(), "Pin does not match", Toast.LENGTH_LONG).show();
                }
                /*else{
                    Toast.makeText(getApplicationContext(),"Pin is empty",Toast.LENGTH_LONG).show();
                }*/
                break;
            default:
                break;
        }

    }

    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ChangePin.this, MainActivity.class));
        finish();

    }
}
