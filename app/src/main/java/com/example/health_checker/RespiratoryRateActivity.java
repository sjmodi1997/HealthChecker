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

public class RespiratoryRateActivity extends AppCompatActivity {
    DatabaseHandler db_handler;
    String value;
    private ProgressDialog progressDialog;
    private final BroadcastReceiver msgReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Display the respiratory rate
            value = intent.getStringExtra("RRvalue");

            if (value == null) {
                Log.d("Null", " null");
            }
            progressDialog.dismiss();

            TextView RespiRateView = findViewById(R.id.RespiRateValTextView);
            RespiRateView.setText("RESPIRATORY RATE IS " + value);
            Button measure = findViewById(R.id.RespiRateBtn);
            Button upload = findViewById(R.id.RespiRateUpData);

            upload.setVisibility(View.VISIBLE);

            measure.setText("MEASURE RESPIRATORY RATE AGAIN");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_respiratory_rate);

        Button measure = findViewById(R.id.RespiRateBtn);
        Button upload = findViewById(R.id.RespiRateUpData);
        upload.setVisibility(View.INVISIBLE);

        measure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent measureRate = new Intent(RespiratoryRateActivity.this, RespiratoryRateService.class);
                progressDialog = new ProgressDialog(getApplicationContext());
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog = ProgressDialog.show(RespiratoryRateActivity.this, "Please wait", "Measuring Respiratory Rate...", false, false);
                startService(measureRate);
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TextView RespiRateView = findViewById(R.id.RespiRateValTextView);
                db_handler = new DatabaseHandler();
                db_handler.create_logging_database();
                db_handler.create_logging_table();

                if (db_handler.upload_logging_data(Integer.parseInt(value), "RespiratoryRate")) {
                    Toast.makeText(RespiratoryRateActivity.this, "Data Uploaded", Toast.LENGTH_LONG).show();
                    upload.setVisibility(View.INVISIBLE);
                } else {
                    Toast.makeText(RespiratoryRateActivity.this, "Upload Failed", Toast.LENGTH_LONG).show();
                    upload.setVisibility(View.VISIBLE);
                }
                //upload.setClickable(false);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(msgReceiver, new IntentFilter("Respiratory Rate"));
    }

    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(msgReceiver);
    }
}