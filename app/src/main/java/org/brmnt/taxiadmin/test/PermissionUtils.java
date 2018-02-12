package org.brmnt.taxiadmin.test;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

/**
 * @author by Bramengton
 * @date 12.02.18.
 */
public class PermissionUtils extends ContextWrapper {
    public final static int REQUEST_CODE = 1023;
    private final String[] mPermissions = {Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION};


    public PermissionUtils(Context base) {
        super(base);
    }

    public boolean checkPermission(){
        return  ActivityCompat.checkSelfPermission(this, mPermissions[0]) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, mPermissions[1]) !=
                        PackageManager.PERMISSION_GRANTED;

    }

    public void requestPermission(final Activity activity){
        if(checkPermission())
            ActivityCompat.requestPermissions(activity, mPermissions, REQUEST_CODE);
    }
}
