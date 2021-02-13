package com.example.health_checker;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

public class RespiratoryRateService extends Service implements SensorEventListener {
    private SensorManager manager;
    private Sensor sensorAccel;
    float accelValuesX[] = new float[128];
    float accelValuesY[] = new float[128];
    float accelValuesZ[] = new float[128];

    public RespiratoryRateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Toast.makeText(this, "Starting Respi rate", Toast.LENGTH_LONG).show();

        manager
                = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorAccel = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        manager.registerListener(this, sensorAccel, SensorManager.SENSOR_DELAY_NORMAL);

        return START_NOT_STICKY;
    }

    int i = 0;

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor accelSensor = event.sensor;
        i++;
        if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER)
            return;

        if (event.sensor.getType()==Sensor.TYPE_ACCELEROMETER) {
            Toast.makeText(this, "sjsdakdj: " + Float.toString(event.values[0]) + ": " +
                    " "+ Integer.toString(i), Toast.LENGTH_LONG).show();
        }


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}