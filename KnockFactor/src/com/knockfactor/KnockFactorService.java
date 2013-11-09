package com.knockfactor;

import android.app.IntentService;
import android.content.Intent;
import android.hardware.SensorManager;
import android.util.Log;

public class KnockFactorService extends IntentService {

    public static final String STATUS = "com.knockfactor.knockfactorservice.STATUS";
    private KnockEventListener knockListener;

    @Override
    public void onCreate() {
        Log.v("knockListener", "service");
        knockListener = new KnockEventListener((SensorManager)this.getSystemService(SENSOR_SERVICE));
    }

    public KnockFactorService () {
        super("KnockFactorService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // doesn't really handle given intent bc always the same?
        Log.v("knockListener", "onHandleIntent");
        Intent localIntent = new Intent().putExtra(STATUS, knockListener.knockDetected);
        sendBroadcast(localIntent);
    }
}
