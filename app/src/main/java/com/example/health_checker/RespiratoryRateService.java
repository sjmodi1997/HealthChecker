package com.example.health_checker;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class RespiratoryRateService extends Service implements SensorEventListener {
    private SensorManager manager;
    private Sensor sensorAccel;
    int i = 0;
    double accelValuesX[] = new double[1280];
    double accelValuesY[] = new double[1280];
    double accelValuesZ[] = new double[1280];


    public RespiratoryRateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorAccel = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        manager.registerListener(this, sensorAccel, SensorManager.SENSOR_DELAY_NORMAL);

        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return START_NOT_STICKY;
    }

    private int getRespiratoryRate() {
        List<Double> x = new ArrayList<Double>();
        List<Double> y = new ArrayList<Double>();
        List<Double> z = new ArrayList<Double>();

        for (int i=0; i< accelValuesX.length; i++) {
            Double val = accelValuesX[i];
            x.add(val);
        }

        for (int i=0; i< accelValuesY.length; i++) {
            Double val = accelValuesY[i];
            y.add(val);
        }

        for (int i=0; i< accelValuesZ.length; i++) {
            Double val = accelValuesZ[i];
            z.add(val);
        }

        int mov_period = 50;
        Algorithms algos = new Algorithms();
        List<Double> x_avg = algos.calculate_moving_avg(mov_period, x);
        int peaksX = algos.count_zero_crossings_threshold(x_avg);

        List<Double> y_avg = algos.calculate_moving_avg(mov_period, y);
        int peaksY = algos.count_zero_crossings_threshold(y_avg);

        List<Double> z_avg = algos.calculate_moving_avg(mov_period, z);
        int peaksZ = algos.count_zero_crossings_threshold(z_avg);

        Toast.makeText(this, "Measuring: " + Integer.toString(peaksY/2), Toast.LENGTH_LONG).show();
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return peaksZ/2;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor accelSensor = event.sensor;

        if (accelSensor.getType()==Sensor.TYPE_ACCELEROMETER) {
            i++;
            accelValuesX[i] = event.values[0];
            accelValuesY[i] = event.values[1];
            accelValuesZ[i] = event.values[2];

            if(i>=1279) {
                i = 0;
                manager.unregisterListener(this);
                getRespiratoryRate();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}