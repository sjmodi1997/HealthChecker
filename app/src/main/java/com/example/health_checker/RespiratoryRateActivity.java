package com.example.health_checker;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

/**
 * Class for RespiratoryRate Activity
 */
public class RespiratoryRateActivity extends AppCompatActivity {
    DatabaseHandler dbHandler;
    String respiratoryRateValue;
    private ProgressDialog progressDialog;
    private final BroadcastReceiver msgReceiver = new BroadcastReceiver() {
        /**
         * Display the respiratory rate
         * @param context
         * @param intent
         */
        @Override
        public void onReceive(Context context, Intent intent) {
            respiratoryRateValue = intent.getStringExtra("RRvalue");

            if (respiratoryRateValue == null) {
                Log.d("Null", " null");
            }
            progressDialog.dismiss();

            TextView respiratoryRateView = findViewById(R.id.RespiRateValTextView);
            respiratoryRateView.setText("RESPIRATORY RATE IS " + respiratoryRateValue);
            Button measureButton = findViewById(R.id.RespiRateBtn);
            Button uploadButton = findViewById(R.id.RespiRateUpData);

            uploadButton.setVisibility(View.VISIBLE);

            measureButton.setText("MEASURE RESPIRATORY RATE AGAIN");
        }
    };

    /**
     * On Create Method
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_respiratory_rate);

        Button measureButton = findViewById(R.id.RespiRateBtn);
        Button uploadButton = findViewById(R.id.RespiRateUpData);
        uploadButton.setVisibility(View.INVISIBLE);

        measureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent measureRate = new Intent(RespiratoryRateActivity.this, RespiratoryRateService.class);
                progressDialog = new ProgressDialog(getApplicationContext());
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog = ProgressDialog.show(RespiratoryRateActivity.this, "Please wait", "Measuring Respiratory Rate...", false, false);
                startService(measureRate);
            }
        });

        // To Log the Data
        uploadButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TextView respiratoryRateView = findViewById(R.id.RespiRateValTextView);
                dbHandler = new DatabaseHandler();
                dbHandler.createLoggingDatabase();
                dbHandler.createLoggingTable();

                if (dbHandler.uploadLoggingData(Integer.parseInt(respiratoryRateValue), "RespiratoryRate")) {
                    Toast.makeText(RespiratoryRateActivity.this, "Data Uploaded", Toast.LENGTH_LONG).show();
                    uploadButton.setVisibility(View.INVISIBLE);
                } else {
                    Toast.makeText(RespiratoryRateActivity.this, "Upload Failed", Toast.LENGTH_LONG).show();
                    uploadButton.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    /**
     * Resume Method
     */
    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(msgReceiver, new IntentFilter("Respiratory Rate"));
    }

    /**
     * Pause Method
     */
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(msgReceiver);
    }
}