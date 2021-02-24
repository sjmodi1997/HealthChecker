package com.example.health_checker;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.opencv.android.OpenCVLoader;

public class MainActivity extends AppCompatActivity {
    private DatabaseHandler db;

    static {
        if (OpenCVLoader.initDebug()) {
            Log.d("TAG", "OpenCV done");
        } else {
            Log.d("TAG", "OpenCV error");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView img = (ImageView) findViewById(R.id.logoImageView);
        img.setImageResource(R.drawable.logo_image);

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

        FloatingActionButton saveBtn = (FloatingActionButton) findViewById(R.id.SaveDataButton);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                db = new DatabaseHandler();
                db.create_database();
                db.create_table();

                //db.isComplete()
                if(true) {
                    if(db.insert_row())
                        Toast.makeText(MainActivity.this, "Data Uploaded Sucessfully", Toast.LENGTH_LONG).show();
                        saveBtn.setVisibility(View.INVISIBLE);
                } else {
                    Toast.makeText(MainActivity.this, "Measured symptoms long ago try measuring again", Toast.LENGTH_LONG).show();
                }
            }
        });

    }


}