package com.knockfactor;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class KnockEventListener implements SensorEventListener {

    private SensorManager mSensorManager;
    public boolean knockDetected = false;
    float prevZ;
    float currZ;
    float diffZ;

    KnockEventListener(SensorManager sm) {
        mSensorManager = sm;
        /*
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAcceleromator = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAcceleromator, SensorManager.SENSOR_DELAY_NORMAL);
        */

        // initialize values
        currZ = 0;
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
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}