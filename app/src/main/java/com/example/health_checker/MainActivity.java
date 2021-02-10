package com.example.health_checker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //val MeasureRespiratoryRateButton = findViewById(R.id.MeasureRespiratoryButton)
        Button RespBtn = (Button) findViewById(R.id.MeasureRespiratoryButton);
        Button HeartRateBtn = (Button) findViewById(R.id.MeasureHeartRateButton);
        Button SymptomsBtn = (Button) findViewById(R.id.ReportSymptomsButton);

        RespBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
            }
        });

                
                
    }
}