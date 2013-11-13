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
    float minZ;
    float maxZ;

    final int millisInFuture = 1000; // milliseconds
    final long countDownInterval = 1000;
    private int numKnocks = 0;
    private MyCountDownTimer mCountDownTimer;

    KnockEventListener(SensorManager sm) {
        mSensorManager = sm;
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            mAcceleromator = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        } else {

        }
        mSensorManager.registerListener(this, mAcceleromator, SensorManager.SENSOR_DELAY_NORMAL);
        currZ = 0;
        minZ = 2;
        maxZ = 12;
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = Math.abs(event.values[0]);
        float y = Math.abs(event.values[1]);
        float z = Math.abs(event.values[2]);

        prevZ = currZ;
        currZ = z;
        diffZ = Math.abs(currZ - prevZ);

        if (diffZ > minZ && diffZ < maxZ) {
            if (numKnocks == 1 && !mCountDownTimer.finished) {
                knockDetected = true;
                numKnocks = 0;
            } else if (numKnocks == 0) {
                mCountDownTimer = new MyCountDownTimer(millisInFuture, countDownInterval);
                mCountDownTimer.start();
                numKnocks++;
            }
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

    private class MyCountDownTimer extends CountDownTimer {

        boolean finished;

        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
            finished = false;
        }

        @Override
        public void onTick(long l) {

        }

        @Override
        public void onFinish() {
            finished = true;
            numKnocks = 0;
            knockDetected = false;
        }
    }
}