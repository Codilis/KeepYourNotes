package com.palash.codilis.keepyournotes;

/**
 * Will receive content from user and will save on click
 * will switch back to main activity on completion
 */

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.text.TextRecognizer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.util.Date;

public class GetNote extends AppCompatActivity implements View.OnClickListener {

    private static final String LOG_TAG = "Text API";
    private static final int PHOTO_REQUEST = 10;
    private static final int REQUEST_WRITE_PERMISSION = 20;
    private static final String SAVED_INSTANCE_URI = "uri";
    private static final String SAVED_INSTANCE_RESULT = "result";
    private static int RESULT_LOAD_IMG = 1;
    Button saveButton, privateButton, ocrButton, loadButton;
    EditText editNote;
    String text = "";
    String imgDecodableString;
    private TextView scanResults;
    private Uri imageUri;
    private TextRecognizer detector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.get_note);
        Intent get = getIntent();
        text = get.getStringExtra("text");

        saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(this);
        privateButton = findViewById(R.id.privateButton);
        privateButton.setOnClickListener(this);
        ocrButton = findViewById(R.id.ocr);
        ocrButton.setOnClickListener(this);

        editNote = findViewById(R.id.editNote);
        editNote.setText(text);

        detector = new TextRecognizer.Builder(getApplicationContext()).build();

        ActivityCompat.requestPermissions(GetNote.this, new
                String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
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


    public void loadImagefromGallery(View view) {
        // Create intent to Open Image applications like Gallery, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgDecodableString = cursor.getString(columnIndex);
                cursor.close();

                String root = Environment.getExternalStorageDirectory().getAbsolutePath() + "/1g7k.jpg";

                File source = new File(imgDecodableString);
                File destination = new File(root);

                FileChannel src = new FileInputStream(source).getChannel();
                FileChannel dst = new FileOutputStream(destination).getChannel();
                dst.transferFrom(src, 0, src.size());       // copy the first file to second.....
                src.close();
                dst.close();


            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

    }
}

