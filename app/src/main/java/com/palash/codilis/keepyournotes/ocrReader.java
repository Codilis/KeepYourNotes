package com.palash.codilis.keepyournotes;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.File;
import java.io.FileNotFoundException;

public class ocrReader extends AppCompatActivity {
    private static final String LOG_TAG = "Text API";
    private static final int PHOTO_REQUEST = 10;
    private static final int REQUEST_WRITE_PERMISSION = 20;
    private static final String SAVED_INSTANCE_URI = "uri";
    private static final String SAVED_INSTANCE_RESULT = "result";
    Button button;
    private TextView scanResults;
    private Uri imageUri;
    private TextRecognizer detector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = findViewById(R.id.button);
        scanResults = findViewById(R.id.results);
        if (savedInstanceState != null) {
            imageUri = Uri.parse(savedInstanceState.getString(SAVED_INSTANCE_URI));
            scanResults.setText(savedInstanceState.getString(SAVED_INSTANCE_RESULT));
        }
        detector = new TextRecognizer.Builder(getApplicationContext()).build();
        ActivityCompat.requestPermissions(ocrReader.this, new
                String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ActivityCompat.requestPermissions(ocrReader.this, new
//                        String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
//            }
//        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_WRITE_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePicture();
                } else {
                    Toast.makeText(ocrReader.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PHOTO_REQUEST && resultCode == RESULT_OK) {
            launchMediaScanIntent();
            try {
                Bitmap bitmap = decodeBitmapUri(this, imageUri);
                if (detector.isOperational() && bitmap != null) {
                    Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                    SparseArray<TextBlock> textBlocks = detector.detect(frame);
                    String blocks = "";
                    String lines = "";
                    String words = "";
                    for (int index = 0; index < textBlocks.size(); index++) {
                        //extract scanned text blocks here
                        TextBlock tBlock = textBlocks.valueAt(index);
                        blocks = blocks + tBlock.getValue() + "\n" + "\n";
                        for (Text line : tBlock.getComponents()) {
                            //extract scanned text lines here
                            lines = lines + line.getValue() + "\n";
                            for (Text element : line.getComponents()) {
                                //extract scanned text words here
                                words = words + element.getValue() + ", ";
                            }
                        }
                    }
                    if (textBlocks.size() == 0) {
                        scanResults.setText("Scan Failed: Found nothing to scan");
                    } else {
                        scanResults.setText(scanResults.getText() + "Blocks: " + "\n");
                        scanResults.setText(scanResults.getText() + blocks + "\n");
                        scanResults.setText(scanResults.getText() + "---------" + "\n");
                        scanResults.setText(scanResults.getText() + "Lines: " + "\n");
                        scanResults.setText(scanResults.getText() + lines + "\n");
                        scanResults.setText(scanResults.getText() + "---------" + "\n");
                        scanResults.setText(scanResults.getText() + "Words: " + "\n");
                        scanResults.setText(scanResults.getText() + words + "\n");
                        scanResults.setText(scanResults.getText() + "---------" + "\n");
                    }
                } else {
                    scanResults.setText("Could not set up the detector!");
                }
            } catch (Exception e) {
                Toast.makeText(this, "Failed to load Image", Toast.LENGTH_SHORT)
                        .show();
                Log.e(LOG_TAG, e.toString());
            }
        }
    }

    private void takePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photo = new File(Environment.getExternalStorageDirectory(), "picture.jpg");
        imageUri = FileProvider.getUriForFile(ocrReader.this,
                BuildConfig.APPLICATION_ID + ".provider", photo);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, PHOTO_REQUEST);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (imageUri != null) {
            outState.putString(SAVED_INSTANCE_URI, imageUri.toString());
            outState.putString(SAVED_INSTANCE_RESULT, scanResults.getText().toString());
        }
        super.onSaveInstanceState(outState);
    }

    private void launchMediaScanIntent() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(imageUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private Bitmap decodeBitmapUri(Context ctx, Uri uri) throws FileNotFoundException {
        int targetW = 600;
        int targetH = 600;
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(ctx.getContentResolver().openInputStream(uri), null, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        return BitmapFactory.decodeStream(ctx.getContentResolver()
                .openInputStream(uri), null, bmOptions);
    }
}


//package com.palash.codilis.keepyournotes;
//
//import android.Manifest;
//import android.content.Context;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Environment;
//import android.provider.MediaStore;
//import android.support.annotation.NonNull;
//import android.support.v4.app.ActivityCompat;
//import android.support.v7.app.AppCompatActivity;
//import android.util.Log;
//import android.util.SparseArray;
//import android.view.View;
//import android.widget.Button;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.google.android.gms.vision.Frame;
//import com.google.android.gms.vision.text.Text;
//import com.google.android.gms.vision.text.TextBlock;
//import com.google.android.gms.vision.text.TextRecognizer;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.text.DateFormat;
//import java.util.Date;
//
//public class ocrReader extends AppCompatActivity implements View.OnClickListener {
//
//    private static final String LOG_TAG = "Text API";
//    private static final int PHOTO_REQUEST = 10;
//    private static final int REQUEST_WRITE_PERMISSION = 20;
//    private static final String SAVED_INSTANCE_URI = "uri";
//    private static final String SAVED_INSTANCE_RESULT = "result";
//    Button saveButton, privateButton, ocrButton;
//    private TextView scanResults;
//    private Uri imageUri;
//    private TextRecognizer detector;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.get_note);
//
//        Intent get = getIntent();
//
//        //Button button = (Button) findViewById(R.id.ocr);
//        saveButton = (Button) findViewById(R.id.saveButton);
//        saveButton.setOnClickListener(this);
//        privateButton = (Button) findViewById(R.id.privateButton);
//        privateButton.setOnClickListener(this);
//        ocrButton = (Button) findViewById(R.id.ocr);
//        ocrButton.setOnClickListener(this);
//
//        scanResults = (TextView) findViewById(R.id.editNote);
//
//        if (savedInstanceState != null) {
//            imageUri = Uri.parse(savedInstanceState.getString(SAVED_INSTANCE_URI));
//            scanResults.setText(savedInstanceState.getString(SAVED_INSTANCE_RESULT));
//        }
//        detector = new TextRecognizer.Builder(getApplicationContext()).build();
//        /*button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {*/
//        ActivityCompat.requestPermissions(ocrReader.this, new
//                String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
//        //}
//        //});
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        switch (requestCode) {
//            case REQUEST_WRITE_PERMISSION:
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    takePicture();
//                } else {
//                    Toast.makeText(ocrReader.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
//                }
//        }
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == PHOTO_REQUEST && resultCode == RESULT_OK) {
//            launchMediaScanIntent();
//            try {
//                Bitmap bitmap = decodeBitmapUri(this, imageUri);
//                if (detector.isOperational() && bitmap != null) {
//                    Frame frame = new Frame.Builder().setBitmap(bitmap).build();
//                    SparseArray<TextBlock> textBlocks = detector.detect(frame);
//                    String blocks = "";
//                    String lines = "";
//                    String words = "";
//                    for (int index = 0; index < textBlocks.size(); index++) {
//                        //extract scanned text blocks here
//                        TextBlock tBlock = textBlocks.valueAt(index);
//                        blocks = blocks + tBlock.getValue() + "\n" + "\n";
//                        for (Text line : tBlock.getComponents()) {
//                            //extract scanned text lines here
//                            lines = lines + line.getValue() + "\n";
//                            for (Text element : line.getComponents()) {
//                                //extract scanned text words here
//                                words = words + element.getValue() + ", ";
//                            }
//                        }
//                    }
//                    if (textBlocks.size() == 0) {
//                        Toast.makeText(this, "Nothing to be found", Toast.LENGTH_SHORT).show();
//                    } else {
//                        scanResults.setText(scanResults.getText() + "\n");
//                        scanResults.setText(scanResults.getText() + blocks + "\n");
//                        //  scanResults.setText(scanResults.getText() + "---------" + "\n");
//                        //scanResults.setText(scanResults.getText() + "Lines: " + "\n");
//                        // scanResults.setText(scanResults.getText() + lines + "\n");
//                        /*scanResults.setText(scanResults.getText() + "---------" + "\n");
//                        scanResults.setText(scanResults.getText() + "Words: " + "\n");
//                        scanResults.setText(scanResults.getText() + words + "\n");
//                        */
//                        scanResults.setText(scanResults.getText() + "---------" + "\n");
//                    }
//                } else {
//                    Toast.makeText(this, "Could not setup the detector", Toast.LENGTH_SHORT).show();
//                }
//            } catch (Exception e) {
//                Toast.makeText(this, "Failed to load Image", Toast.LENGTH_SHORT).show();
//                Log.e(LOG_TAG, e.toString());
//            }
//        }
//    }
//
//    private void takePicture() {
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        File photo = new File(Environment.getExternalStorageDirectory(), "1g7k.jpg");
//        imageUri = Uri.fromFile(photo);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//        startActivityForResult(intent, PHOTO_REQUEST);
//    }
//
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        if (imageUri != null) {
//            outState.putString(SAVED_INSTANCE_URI, imageUri.toString());
//            outState.putString(SAVED_INSTANCE_RESULT, scanResults.getText().toString());
//        }
//        super.onSaveInstanceState(outState);
//    }
//
//    private void launchMediaScanIntent() {
//        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//        mediaScanIntent.setData(imageUri);
//        this.sendBroadcast(mediaScanIntent);
//    }
//
//    private Bitmap decodeBitmapUri(Context ctx, Uri uri) throws FileNotFoundException {
//        int targetW = 600;
//        int targetH = 600;
//        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
//        bmOptions.inJustDecodeBounds = true;
//        BitmapFactory.decodeStream(ctx.getContentResolver().openInputStream(uri), null, bmOptions);
//        int photoW = bmOptions.outWidth;
//        int photoH = bmOptions.outHeight;
//
//        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);
//        //int scaleFactor = Math.min(photoW, photoH);
//        bmOptions.inJustDecodeBounds = false;
//        bmOptions.inSampleSize = scaleFactor;
//
//        return BitmapFactory.decodeStream(ctx.getContentResolver()
//                .openInputStream(uri), null, bmOptions);
//    }
//
//    @Override
//    public void onClick(View v) {
//        String data = scanResults.getText().toString();
//        String filename = DateFormat.getDateTimeInstance().format(new Date());
//
//
//        switch (v.getId()) {
//
//            case R.id.saveButton:
//
//                filename = filename.concat("Note\n");
//                FileOutputStream fos;
//                try {
//                    fos = openFileOutput(filename, Context.MODE_PRIVATE);
//                    fos.write(data.getBytes());
//                    fos.close();
//
//                    Toast.makeText(getApplicationContext(), "note saved", Toast.LENGTH_LONG).show();
//
//
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//                FileOutputStream main;
//                try {
//                    main = openFileOutput(getString(R.string.noteKepper), Context.MODE_APPEND);
//                    main.write(filename.getBytes());
//                    main.close();
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//                scanResults.setText("");
//                Intent myIntent = new Intent(ocrReader.this, MainActivity.class);
//                ocrReader.this.startActivity(myIntent);
//                finish();
//                break;
//
//
//            case R.id.privateButton:
//                filename = filename.concat("Private\n");
//                try {
//                    fos = openFileOutput(filename, Context.MODE_PRIVATE);
//                    fos.write(data.getBytes());
//                    fos.close();
//
//                    Toast.makeText(getApplicationContext(), "note saved", Toast.LENGTH_LONG).show();
//
//
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                try {
//                    main = openFileOutput(getString(R.string.noteKepper), Context.MODE_APPEND);
//                    main.write(filename.getBytes());
//                    main.close();
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//                scanResults.setText("");
//                Intent myIntent1 = new Intent(ocrReader.this, MainActivity.class);
//                ocrReader.this.startActivity(myIntent1);
//                finish();
//                break;
//
//            case R.id.ocr:
//                Intent myIntent2 = new Intent(ocrReader.this, ocrReader.class);
//                ocrReader.this.startActivity(myIntent2);
//                break;
//
//            default:
//                break;
//        }
//    }
//}
//
