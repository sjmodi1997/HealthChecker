package com.example.health_checker;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class RespiratoryRateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_respiratory_rate);

        Button measure = (Button) findViewById(R.id.RespiRateBtn);

        measure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent measureRate = new Intent(getApplicationContext(), RespiratoryRateService.class);
                /*ProgressDialog progressDialog = new ProgressDialog(null);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setCancelable(false);
                progressDialog.setMessage("sd");
                progressDialog.show();*/
                startService(measureRate);
            }
        });

    }
}