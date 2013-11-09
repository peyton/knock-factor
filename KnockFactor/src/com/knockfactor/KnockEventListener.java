package com.knockfactor;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.CountDownTimer;
import android.util.Log;

public class KnockEventListener implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mAcceleromator;
    public boolean knockDetected = false;
    float prevZ;
    float currZ;
    float diffZ;

    final int timeframe = 1000; // milliseconds
    private int numKnocks = 0;
    private CountDownTimer mCountDownTimer;

    KnockEventListener(SensorManager sm) {
        mSensorManager = sm;
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            mAcceleromator = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        } else {

        }
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

        if (numKnocks == 1 && diffZ > 2) {
            // second knock
            knockDetected = true;
            numKnocks = 0;
        } else if (diffZ > 2) {
            // first knock
            numKnocks++;

            // Log.v("knockListener", "prevZ " + prevZ);
            // Log.v("knockListener", "currZ " + currZ);
            Log.v("knockListener", "diffZ" + diffZ);
        } else {
            numKnocks = 0;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void pauseListener() {
        mSensorManager.unregisterListener(this);
    }

    public void resumeListener() {
        mSensorManager.registerListener(this, mAcceleromator, SensorManager.SENSOR_DELAY_NORMAL);
    }
}