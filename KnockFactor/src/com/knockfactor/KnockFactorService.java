package com.knockfactor;

import android.app.IntentService;
import android.content.Intent;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.Toast;

public class KnockFactorService extends IntentService {

    public static final String STATUS = "com.knockfactor.knockfactorservice.STATUS";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public KnockFactorService () {
        super("KnockFactorService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        boolean knockDetected = intent.getBooleanExtra("STATUS", false);
        Log.v("knockListener", "onHandleIntent " + knockDetected);
    }
}
