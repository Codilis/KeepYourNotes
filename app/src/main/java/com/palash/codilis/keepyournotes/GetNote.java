package com.palash.codilis.keepyournotes;

/**
 * Will receive content from user and will save on click
 * will switch back to main activity on completion
 */

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
import java.text.DateFormat;
import java.util.Date;

public class GetNote extends AppCompatActivity implements View.OnClickListener {

    Button saveButton, privateButton, ocrButton;
    EditText editNote;
    String text = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.get_note);
        Intent get = getIntent();
        text = get.getStringExtra("text");

        saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(this);
        privateButton = (Button) findViewById(R.id.privateButton);
        privateButton.setOnClickListener(this);
        ocrButton = (Button) findViewById(R.id.ocr);
        ocrButton.setOnClickListener(this);

        editNote = (EditText) findViewById(R.id.editNote);
        editNote.setText(text);
    }

    //On Click Method

    public void onClick(View v) {

        String data = editNote.getText().toString();
        String filename = DateFormat.getDateTimeInstance().format(new Date());
        //String filename = "hello";


        switch (v.getId()) {

            case R.id.saveButton:

                filename = filename.concat("Note\n");
                FileOutputStream fos;
                try {
                    fos = openFileOutput(filename, Context.MODE_PRIVATE);
                    fos.write(data.getBytes());
                    fos.close();

                    Toast.makeText(getApplicationContext(), "note saved", Toast.LENGTH_LONG).show();


                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                FileOutputStream main;
                try {
                    main = openFileOutput(getString(R.string.noteKepper), Context.MODE_APPEND);
                    main.write(filename.getBytes());
                    main.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                editNote.setText("");
                Intent myIntent = new Intent(GetNote.this, MainActivity.class);
                GetNote.this.startActivity(myIntent);
                finish();
                break;


            case R.id.privateButton:
                filename = filename.concat("Private\n");
                try {
                    fos = openFileOutput(filename, Context.MODE_PRIVATE);
                    fos.write(data.getBytes());
                    fos.close();

                    Toast.makeText(getApplicationContext(), "note saved", Toast.LENGTH_LONG).show();


                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    main = openFileOutput(getString(R.string.noteKepper), Context.MODE_APPEND);
                    main.write(filename.getBytes());
                    main.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                editNote.setText("");
                Intent myIntent1 = new Intent(GetNote.this, MainActivity.class);
                GetNote.this.startActivity(myIntent1);
                finish();
                break;

            case R.id.ocr:
                Intent myIntent2 = new Intent(GetNote.this, ocrReader.class);
                GetNote.this.startActivity(myIntent2);
                break;

            default:
                break;
        }

    }

    public void onBackPressed() {
        super.onBackPressed();
        finish();

    }
}
