package com.palash.codilis.keepyournotes;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class PrivateNote extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    ListView noteList;
    MyAdapter mAdapter = null;
    ArrayList<note> messages = null;
    String[] names = new String[1000];
    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.private_note);


        noteList = (ListView) findViewById(R.id.noteList);
        messages = new ArrayList<note>();
        mAdapter = new MyAdapter(this, messages);
        noteList.setAdapter(mAdapter);
        noteList.setOnItemClickListener(this);
        noteList.setOnItemLongClickListener(this);

        ListView lv = (ListView) findViewById(R.id.noteList);
        TextView emptyText = (TextView) findViewById(R.id.empty);
        lv.setEmptyView(emptyText);
        showNote();
    }


    public void showNote() {
        //TODO a file to save names of others files will be created and no extract file name and display on list view
        String filename = getString(R.string.noteKepper);
        StringBuffer stringBuffer = new StringBuffer();
        StringBuffer fileBuffer = new StringBuffer();
        String data = "";
        try {
            BufferedReader fileReader = new BufferedReader(new InputStreamReader(
                    openFileInput(filename)));
            String fileString;

            while ((fileString = fileReader.readLine()) != null) {
                if (fileString.endsWith("Private")) {
                    try {
                        BufferedReader inputReader = new BufferedReader(new InputStreamReader(openFileInput(fileString)));
                        String inputString;
                        while ((inputString = inputReader.readLine()) != null) {
                            stringBuffer.append(inputString);

                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    data = fileString;
                    names[i] = data;
                    i = i + 1;
                    note Note = new note(data);
                    messages.add(Note);
                    mAdapter.notifyDataSetChanged();
                    Note = null;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {


        Intent in = new Intent(PrivateNote.this, ShowNote.class);
        in.putExtra("note", names[position]);
        Toast.makeText(getApplicationContext(), names[position], Toast.LENGTH_SHORT).show();
        startActivity(in);


    }


    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure, You wanted to delete " + names[position]);
        alertDialogBuilder.setPositiveButton("Delete",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        mAdapter.remove(messages.get(position));
                        mAdapter.notifyDataSetChanged();
                        removefile(names[position]);
                    }
                });

        AlertDialog.Builder no = alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        return true;
    }

    public void removefile(String lineToRemove) {

        String data;
        String filename = getString(R.string.noteKepper);
        StringBuffer stringBuffer = new StringBuffer();
        try {
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(
                    openFileInput(filename)));
            String inputString;

            while ((inputString = inputReader.readLine()) != null) {
                if (!inputString.equals(lineToRemove)) {
                    stringBuffer.append(inputString + "\n");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        data = stringBuffer.toString();
        FileOutputStream main;
        try {
            main = openFileOutput(getString(R.string.noteKepper), Context.MODE_PRIVATE);
            main.write(data.getBytes());
            main.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        deleteFile(lineToRemove.concat("\n"));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(PrivateNote.this, MainActivity.class));
        finish();

    }

}
