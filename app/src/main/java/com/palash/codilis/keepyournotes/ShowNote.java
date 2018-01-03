package com.palash.codilis.keepyournotes;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ShowNote extends AppCompatActivity implements View.OnClickListener {

    TextView showNote;
    String info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_note);
        Intent get = getIntent();
        info = get.getStringExtra("note");

        showNote = (TextView) findViewById(R.id.showNote);
        showNote.setOnClickListener(this);
        String info = readNote();
        showNote.setText(info);
        showNote.setMovementMethod(new ScrollingMovementMethod());
    }


    String readNote() {
        String data;
        String filename = info.concat("\n");
        StringBuffer stringBuffer = new StringBuffer();
        try {
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(
                    openFileInput(filename)));
            String inputString;

            while ((inputString = inputReader.readLine()) != null) {
                stringBuffer.append(inputString + "\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        data = stringBuffer.toString();
        return data;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.showNote:
                Intent in = new Intent(ShowNote.this, GetNote.class);
                in.putExtra("text", readNote());
                startActivity(in);
                finish();
                break;
        }
    }

    public void onBackPressed() {
        super.onBackPressed();
        finish();

    }
}

