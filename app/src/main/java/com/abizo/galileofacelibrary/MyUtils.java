package com.abizo.galileofacelibrary;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class MyUtils {
    private Context mContext;

    //Constants
    private final String TAG = "Avery";
    public static String CAMERA_PERM = Manifest.permission.CAMERA;
    public static String FINE_LOC_PERM = Manifest.permission.ACCESS_FINE_LOCATION;
    public static String COARSE_LOC_PERM = Manifest.permission.ACCESS_COARSE_LOCATION;
    public static String WRITE_EXT_PERM = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    public static String READ_EXT_PERM = Manifest.permission.READ_EXTERNAL_STORAGE;

    public MyUtils(Context context) {
        mContext = context;
    }

    public Perms checkPermissions() {
        Perms perms = new Perms();
        ArrayList<String> permissions = new ArrayList<>();
        if (ActivityCompat.checkSelfPermission(mContext, CAMERA_PERM) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mContext, FINE_LOC_PERM) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mContext, COARSE_LOC_PERM) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mContext, WRITE_EXT_PERM) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mContext, READ_EXT_PERM) == PackageManager.PERMISSION_GRANTED) {
            perms.setPermitted(true);
            perms.setPerms(null);
        } else {
            perms.setPermitted(false);

            if (ActivityCompat.checkSelfPermission(mContext, CAMERA_PERM) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(CAMERA_PERM);
            }
            if (ActivityCompat.checkSelfPermission(mContext, FINE_LOC_PERM) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(FINE_LOC_PERM);
            }
            if (ActivityCompat.checkSelfPermission(mContext, COARSE_LOC_PERM) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(COARSE_LOC_PERM);
            }
            if (ActivityCompat.checkSelfPermission(mContext, WRITE_EXT_PERM) == PackageManager.PERMISSION_GRANTED) {
                permissions.add(WRITE_EXT_PERM);
            }
            if (ActivityCompat.checkSelfPermission(mContext, READ_EXT_PERM) == PackageManager.PERMISSION_GRANTED) {
                permissions.add(READ_EXT_PERM);
            }
            perms.setPerms(permissions);
        }
        return perms;
    }

    public Perms checkPermissions(List<String> permissions) {
        return null;
    }

    public void requestPermissions(List<String> permissions, int requestCode) {
        if (permissions.size() > 0) {
            ActivityCompat.requestPermissions((Activity) mContext, permissions.toArray(new String[permissions.size()]), requestCode);
            return;
        }
        Log.e(TAG, "Permissions are null");

    }

    public boolean isGPSEnabled() {
        final LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } else {
            Log.d(TAG, "Location manager is null");
            return false;
        }
    }

    public Task<Location> getLocationTask(int requestCode) {
        FusedLocationProviderClient fusedLocationProviderClient;

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mContext);

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, requestCode);
            return null;
        } else {
            Task<Location> getLocTask = fusedLocationProviderClient.getLastLocation();
            return getLocTask;
        }

    }



    //Intents
    public void goToLocSettings() {
        mContext.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
    }
}
