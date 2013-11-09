package com.knockfactor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class KnockFactorReceiver extends BroadcastReceiver {

    private KnockFactorReceiver() {
        // prevents instantiation by other packages
    }

    public void onReceive(Context context, Intent intent) {
        if (intent.getBooleanExtra(KnockFactorService.STATUS, false)) {
            Log.v("knockListener", "onReceive, knock detected");
            Toast.makeText(context, "knock detected", Toast.LENGTH_LONG);
        }
    }
}
