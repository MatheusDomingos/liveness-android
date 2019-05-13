package com.example.acessobiosample.utils;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thomaz on 17/12/16.
 */
public class SupportPermissions {

    private static final int MY_PERMISSIONS_REQUEST = 1;

    /**
     * @param activity {@link Activity}
     * @param permissions {@link String}
     */
    public void requestForPermission(Activity activity, String... permissions) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return;

        List<String> toRequest = new ArrayList<>();
        for (String permission : permissions) {
            boolean result = (ContextCompat
                    .checkSelfPermission(activity, permission) == PackageManager
                    .PERMISSION_GRANTED);
            if (!result) {
                toRequest.add(permission);
            }
        }

        if( toRequest.size() > 0 ) {
            ActivityCompat.requestPermissions(activity,
                    toRequest.toArray(new String[toRequest.size()]),
                    MY_PERMISSIONS_REQUEST);
        }
    }

}
