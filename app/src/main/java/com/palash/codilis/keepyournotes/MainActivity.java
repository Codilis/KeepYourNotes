package com.palash.codilis.keepyournotes;

/**
 * Shows all the notes
 * open different activity on different clicks
 */

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    ListView noteList;
    MyAdapter mAdapter = null;
    ArrayList<note> messages = null;
    String[] names = new String[1000];
    int i = 0;
    TextView noteView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        noteList = (ListView) findViewById(R.id.noteList);
        messages = new ArrayList<note>();
        mAdapter = new MyAdapter(this, messages);
        noteList.setAdapter(mAdapter);

        ListView lv = (ListView) findViewById(R.id.noteList);
        TextView emptyText = (TextView) findViewById(R.id.empty);
        lv.setEmptyView(emptyText);

        noteList.setOnItemClickListener(this);
        noteList.setOnItemLongClickListener(this);
        showNotes();


        //Floating Action Button Code
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(MainActivity.this, GetNote.class);
                MainActivity.this.startActivity(myIntent);
            }
        });
    }

    @Override
    //Main Menu Function
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if (id == R.id.action_private) {
            Intent in = new Intent(MainActivity.this, LoginActivity.class);
            MainActivity.this.startActivity(in);
        }

        return super.onOptionsItemSelected(item);
    }

    public void showNotes() {

        String filename = getString(R.string.noteKepper);
        StringBuffer stringBuffer = new StringBuffer();

        StringBuffer fileBuffer = new StringBuffer();
        String data = "";
        try {
            BufferedReader fileReader = new BufferedReader(new InputStreamReader(
                    openFileInput(filename)));
            String fileString;

            while ((fileString = fileReader.readLine()) != null) {
                if (fileString.endsWith("Note")) {
                    try {
                        BufferedReader inputReader = new BufferedReader(new InputStreamReader(openFileInput(fileString)));
                        String inputString;
                        while ((inputString = inputReader.readLine()) != null) {
                            stringBuffer.append(inputString + "\n");

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


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {


        Intent in = new Intent(MainActivity.this, ShowNote.class);
        in.putExtra("note", names[position]);
        startActivity(in);

    }

    @Override
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
        finish();
        moveTaskToBack(true);
        finish();
    }

}





