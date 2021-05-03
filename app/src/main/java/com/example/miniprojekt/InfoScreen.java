package com.example.miniprojekt;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.Gson;

import java.util.ArrayList;


public class InfoScreen extends AppCompatActivity {
    ListView listView;
    ArrayList<String> scanList = new ArrayList<String>();
    ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_screen);

        listView = findViewById(R.id.listView);                             // Initialize list in reference to the XML formatting
        scanList = getIntent().getStringArrayListExtra("scanList");   // Retrieve the passed array list of scans

        //Adapt array list too be compatible with list view
        adapter = new ArrayAdapter(InfoScreen.this, android.R.layout.simple_list_item_1, scanList);
        listView.setAdapter(adapter);

        // System.out.println("The Scan List:" + scanList);
    }

    public void sendMessage(View view) {        // Pass array list back to main activity class and switch activities
        Intent scanner = new Intent(this, MainActivity.class);
        scanner.putStringArrayListExtra("scanList", scanList);
        startActivity(scanner);
    }

    public void sendMessage2(View view) {       // Reset button [Empty array list and commit to shared preferences]
        scanList.clear();
        adapter = new ArrayAdapter(InfoScreen.this, android.R.layout.simple_list_item_1, scanList);
        listView.setAdapter(adapter);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(scanList);
        editor.putString("key", json);
        editor.apply();
    }
}