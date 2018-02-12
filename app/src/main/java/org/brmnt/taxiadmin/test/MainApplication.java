package org.brmnt.taxiadmin.test;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.util.Log;
import org.brmnt.taxiadmin.test.service.FillingService;

/**
 * @author Bramengton on 12/02/2018.
 */
public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if(!new PermissionUtils(this).checkPermission()) {
            Log.e("QQQQQ", "Start service");
            this.startService(new Intent(this, FillingService.class));
        }
     }

    //Returns an instance of {@link MainApplication} attached to the passed activity.
    public static MainApplication get(Activity activity) {
        return (MainApplication) activity.getApplication();
    }

    public static MainApplication get(Application application) {
        return (MainApplication) application;
    }
}
