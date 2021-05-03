package com.example.miniprojekt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.budiyev.android.codescanner.AutoFocusMode;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.budiyev.android.codescanner.ScanMode;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.Result;

import java.lang.reflect.Type;
import java.util.ArrayList;



public class MainActivity extends AppCompatActivity {
    private CodeScanner mCodeScanner;
    ArrayList<String> scanList = new ArrayList<String>();
    String lastResult = "";
    static boolean checkFirstTime = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkSelfPermissions();
        codeScanner();
        // System.out.println("The Scan List:" + scanList); !!TEST CODE!!

        // Check if this is the first time the MainActivity has been run called since program launch
        if(!checkFirstTime){
            scanList = getIntent().getStringArrayListExtra("scanList");
            System.out.println("Scan We've gotten the scan from another place!");
        } else {
            // Check to see if the array list is = null if so then do not attempt to retrieve it [Prevents Calling ".add" on null object]
            if (getArrayList("key") != null) {
                scanList = getArrayList("key");
            }
            checkFirstTime = false;
        }
    }

    // Quickly checks if camera permissions have already been granted if not then start Permissions Request Activity
    public void checkSelfPermissions() {
        Intent permissionsRequest = new Intent(this, PermissionsActivity.class);
        int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);       // Retrieves permission status for the inbuilt android CAMERA
        if (permission != PackageManager.PERMISSION_GRANTED) {
            startActivity(permissionsRequest);
        }
    }

    private void codeScanner(){     // All of the code scanner functionality
        CodeScannerView scannerView = findViewById(R.id.scanner_view);      // Reference XML formatting from :app/res/layout/activity_main -- Scanner View
        mCodeScanner = new CodeScanner(this, scannerView);          // Declares a new code scanner Object from the "codescanner" library
        codeScannerSetup();

        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {           // Method called when compatible code is detected
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!result.getText().equals(lastResult)) {         // Rapid scan repetition suppression
                            lastResult = result.getText();
                            scanList.add(0, result.getText());        // Adds valid result to the collective list of results
                        }

                        // System.out.println("Scan Last Result:" + lastResult);            // TEST CODE
                        // System.out.println("Scan Current Result:" + result.getText());   // TEST CODE

                        saveArrayList(scanList, "key");                                // Save the result list array list too shared preferences
                        TextView textView = (TextView)findViewById(R.id.output_textView);   // Reference XML formatting from :app/res/layout/activity_main -- Scanner View
                        textView.setText("Code scanned:\n" + result.getText());             // Set text for text view
                    }
                });
            }
        });
    }

    private void codeScannerSetup(){    // Sets standard parameters for the declared scanner object
        mCodeScanner.setCamera(CodeScanner.CAMERA_BACK);
        mCodeScanner.setFormats(CodeScanner.ALL_FORMATS);
        mCodeScanner.setAutoFocusMode(AutoFocusMode.SAFE);
        mCodeScanner.setScanMode(ScanMode.CONTINUOUS);
        mCodeScanner.setAutoFocusEnabled(true);
        mCodeScanner.setFlashEnabled(false);
    }

    @Override
    protected void onResume() {         //when project resumes from paused state
        super.onResume();
        mCodeScanner.startPreview();
        TextView textView = (TextView)findViewById(R.id.output_textView);
        textView.setText("Please scan a barcode:");
        lastResult = "";
    }

    @Override
    protected void onPause() {  // when program is paused / in background --> release resources
        mCodeScanner.releaseResources();
        super.onPause();
    }

    public void sendMessage(View view) {        // Transfer Array list to "InfoScreen Activity
        Intent infoScreen = new Intent(this, InfoScreen.class);
        infoScreen.putStringArrayListExtra("scanList", scanList);
        startActivity(infoScreen);
    }

    public void saveArrayList(ArrayList<String> scanList, String key){      // Save the scan array list to the shared preferences using the JSON library
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(scanList);
        editor.putString(key, json);
        editor.apply();
    }

    public ArrayList<String> getArrayList(String key){                      // Retrieve array list from shared preferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Gson gson = new Gson();
        String json = prefs.getString(key, null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        return gson.fromJson(json, type);
    }
}