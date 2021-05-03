package com.example.miniprojekt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.budiyev.android.codescanner.AutoFocusMode;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.budiyev.android.codescanner.ScanMode;
import com.google.zxing.Result;

import static android.Manifest.*;

//ToDo 2x Activies (Scanner Screen, List of scanned Numbers)
//ToDo 3x Original Classes (MainActivity?, Scan, Save scanned nunbers, Permission Request )

//ToDo Permission Request Window
//ToDo Notifications
//ToDo Display Last Scanned Item

/*Activities:
Data
Scanner
 */

/* Classes:
Data Management

 */


public class MainActivity extends AppCompatActivity {
    private CodeScanner mCodeScanner;
    Intent permissionsRequest = new Intent(this, PermissionsActivity.class);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkSelfPermissions();
        codeScanner();
    }

    public void checkSelfPermissions() {
        int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            startActivity(permissionsRequest);
        }
    }

    private void codeScanner(){
        CodeScannerView scannerView = findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(this, scannerView);
        codeScannerSetup();

        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) { // When compatible code is detected
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, result.getText(), Toast.LENGTH_SHORT).show();
                        //When code is detected in image
                    }
                });
            }
        });
        scannerView.setOnClickListener(new View.OnClickListener() { //when screen is tapped
            @Override
            public void onClick(View view) {
              mCodeScanner.startPreview(); // Camera Visibility
            }
        });
    }

    private void codeScannerSetup(){
        mCodeScanner.setCamera(CodeScanner.CAMERA_BACK);
        mCodeScanner.setFormats(CodeScanner.ALL_FORMATS);
        mCodeScanner.setAutoFocusMode(AutoFocusMode.SAFE);
        mCodeScanner.setScanMode(ScanMode.CONTINUOUS);
        mCodeScanner.setAutoFocusEnabled(true);
        mCodeScanner.setFlashEnabled(false);
    }

    @Override
    protected void onResume() { //when project opens / resumes
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    protected void onPause() { // when program is closed / in background release resources
        mCodeScanner.releaseResources();
        super.onPause();
    }
}