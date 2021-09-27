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

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.List;

/**
 * RespritoryRateService Method
 */
public class RespiratoryRateService extends Service implements SensorEventListener {
    int count;
    final int MAX_SIZE = 1280;
    double[] accelValuesX;
    double[] accelValuesY;
    double[] accelValuesZ;
    private SensorManager sensorManager;
    private Sensor sensorAccel;


    public RespiratoryRateService() {
        count = 0;
        accelValuesX = new double[MAX_SIZE];
        accelValuesY = new double[MAX_SIZE];
        accelValuesZ = new double[MAX_SIZE];
    }

    /**
     * throws exception if not implemented
     * @param intent
     * @return
     */
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Method to start the service
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorAccel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, sensorAccel, SensorManager.SENSOR_DELAY_NORMAL);

        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return START_NOT_STICKY;
    }

    /**
     * Main methdo to calculate RespiratoryRate
     * @return int
     */
    private int getRespiratoryRate() {
        List<Double> x = new ArrayList<Double>();
        List<Double> y = new ArrayList<Double>();
        List<Double> z = new ArrayList<Double>();

        for (int i = 0; i < accelValuesX.length; i++) {
            Double val = accelValuesX[i];
            x.add(val);
        }

        for (int i = 0; i < accelValuesY.length; i++) {
            Double val = accelValuesY[i];
            y.add(val);
        }

        for (int i = 0; i < accelValuesZ.length; i++) {
            Double val = accelValuesZ[i];
            z.add(val);
        }

        int mov_period = 50;
        Algorithms algos = new Algorithms();
        List<Double> x_avg = algos.calcMovingAvg(mov_period, x);
        int peaksX = algos.countZerosThreshold(x_avg);

        List<Double> y_avg = algos.calcMovingAvg(mov_period, y);
        int peaksY = algos.countZerosThreshold(y_avg);

        List<Double> z_avg = algos.calcMovingAvg(mov_period, z);
        int peaksZ = algos.countZerosThreshold(z_avg);

        Toast.makeText(this, "Measure: " + peaksY / 2, Toast.LENGTH_LONG).show();
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return (int) peaksZ / 2;
    }

    /**
     * Method to use if you want to use different Sensor
     * @param event
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor accelSensor = event.sensor;

        if (accelSensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            count++;
            accelValuesX[count] = event.values[0];
            accelValuesY[count] = event.values[1];
            accelValuesZ[count] = event.values[2];

            if (count >= MAX_SIZE-1) {
                count = 0;
                sensorManager.unregisterListener(this);

                Intent intent = new Intent("Respiratory Rate"); //put the same message as in the filter you used in the activity when registering the receiver
                String val = Integer.toString(getRespiratoryRate());
                intent.putExtra("RRvalue", val);
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}