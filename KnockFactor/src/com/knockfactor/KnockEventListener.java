package com.knockfactor;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class KnockEventListener implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mAcceleromator;
    public boolean knockDetected = false;
    float prevZ;
    float currZ;
    float diffZ;

    KnockEventListener(SensorManager sm) {
        mSensorManager = sm;
        mAcceleromator = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAcceleromator, SensorManager.SENSOR_DELAY_NORMAL);


        // initialize values
        currZ = 0;
        Log.v("knockListener", "initialized");
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = Math.abs(event.values[0]);
        float y = Math.abs(event.values[1]);
        float z = Math.abs(event.values[2]);

        prevZ = currZ;
        currZ = z;
        diffZ = Math.abs(currZ - prevZ);

        if (diffZ > 2) {
            knockDetected = true;
            /*
            Log.v("knockListener", "prevZ " + prevZ);
            Log.v("knockListener", "currZ " + currZ);
            Log.v("knockListener", "" + diffZ);
            */
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}