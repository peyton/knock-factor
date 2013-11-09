package com.knockfactor;

import android.app.IntentService;
import android.content.Intent;
import android.hardware.SensorManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class KnockFactorService extends IntentService {

    public static final String STATUS = "com.knockfactor.knockfactorservice.STATUS";
    private KnockEventListener knockListener;

    public KnockFactorService () {
        super("KnockFactorService");
        knockListener = new KnockEventListener((SensorManager)getSystemService(SENSOR_SERVICE));
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // doesn't really handle given intent bc always the same?
        Log.v("knockListener", "onHandleIntent");
        Intent localIntent = new Intent().putExtra(STATUS, knockListener.knockDetected);
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }
}
