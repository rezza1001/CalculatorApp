package com.rezzza.calculatorapp.tools;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.util.Objects;

public class Utility {

    public static boolean hasPermission(Activity activity, String[] permissions){
        if(!hasPermissions(activity, permissions)){
            Log.d("Utility","hasPermission false");
            ActivityCompat.requestPermissions(Objects.requireNonNull(activity), permissions, 32);
            return false;
        }
        else {
            Log.d("Utility","hasPermission true");
            return true;
        }
    }

    private static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    Log.d("Utility",permission+" false");
                    return false;
                }
            }
        }
        return true;
    }



}