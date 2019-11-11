package com.abizo.galileofacelibrary;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    //Utils
    MyUtils myUtils;

    //Views
    private Button buttonRegister;
    private Button buttonMatch;
    private TextInputEditText editTextPhilhealthId;

    //Misc
    private String TAG = "Avery";
    private final int permsRequestCode = 100;

    //Data
    private String philhealthId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myUtils = new MyUtils(MainActivity.this);

        checkPerms();

        initViews();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!myUtils.isGPSEnabled()) {
            Log.d(TAG, "GPS provider is not enabled");
            myUtils.goToLocSettings();
        } else {
            Log.d(TAG, "GPS provider is enabled");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case permsRequestCode:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Permissions granted!");
                } else {
                    //Hindi granted lahat
                }
                break;
        }
    }

    public void checkPerms() {
        Perms perms;
        boolean isPermitted;
        List<String> permissions;
        perms = myUtils.checkPermissions();
        isPermitted = perms.isPermitted();
        permissions = perms.getPerms();

        if (isPermitted) {
            //Permitted na lahat yung permissions
            Log.d(TAG, "All permissions are granted");
        } else {
            Log.e(TAG, "Some permissions are not granted");
            myUtils.requestPermissions(permissions, permsRequestCode);
        }
    }

    public void initViews() {
        buttonRegister = findViewById(R.id.buttonRegister);
        buttonMatch = findViewById(R.id.buttonMatchFace);
        editTextPhilhealthId = findViewById(R.id.editTextPhilhealthNumber);

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                philhealthId = editTextPhilhealthId.getText().toString();
                goToFaceCapture(TaskFlag.FACE_ENROLL);
            }
        });

        buttonMatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                philhealthId = editTextPhilhealthId.getText().toString();
                goToFaceCapture(TaskFlag.FACE_MATCH);
            }
        });
    }

    public void goToFaceCapture(int camCode) {
        Intent intent = new Intent(MainActivity.this, FaceCapture.class);
        intent.putExtra(FaceCapture.EXTRA_ELEMENT_USER_ID, philhealthId);
        intent.putExtra(FaceCapture.EXTRA_LIVENESS_DETECTION, true);
        intent.putExtra(FaceCapture.EXTRA_TUTORIAL, true);
        intent.putExtra(TaskFlag.FACE_TASK_EXTRA, camCode);
        startActivity(intent);
    }
}
