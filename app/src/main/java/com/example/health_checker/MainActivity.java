package com.example.health_checker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Call Respiratory Rate Activity
        Button RespBtn = (Button) findViewById(R.id.MeasureRespiratoryButton);

        RespBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent respiInt = new Intent(MainActivity.this, RespiratoryRateActivity.class);
                startActivity(respiInt);
            }
        });

        //Call Heart Rate Activity
        Button HeartBtn = (Button) findViewById(R.id.MeasureHeartRateButton);

        HeartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent heartInt = new Intent(MainActivity.this, HeartRateActivity.class);
                startActivity(heartInt);
            }
        });

        //Call Symptom Logging Activity
        Button SymBtn = (Button) findViewById(R.id.ReportSymptomsButton);

        SymBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent symInt = new Intent(MainActivity.this, SymptomLoggingActivity.class);
                startActivity(symInt);
            }
        });



    }


}