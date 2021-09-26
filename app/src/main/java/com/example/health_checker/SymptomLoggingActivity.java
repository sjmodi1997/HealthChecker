package com.example.health_checker;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;

public class SymptomLoggingActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private float value;
    private DatabaseHandler db_handler;
    private String text;
    private String [] symptomsList = {
            "Breathing Problem",
            "Fever",
            "Cough",
            "Headache",
            "Loss of Smell or Taste",
            "Sore throat"
    };
    private HashMap<String, Float> hash;
    private RatingBar ratingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symptom_logging);

        Spinner spin = (Spinner) findViewById(R.id.symSpinner);

        Button uploadButton = (Button) findViewById(R.id.symUpData);
        uploadButton.setActivated(false);

        hash = new HashMap<String, Float>();
        for(int i = 0; i < symptomsList.length; i++){
            hash.put(symptomsList[i], (float) 0.0);
        }

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.symptoms_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(adapter);
        spin.setOnItemSelectedListener(this);

        ratingBar = (RatingBar) findViewById(R.id.symRatingBar);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {


            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {
                //float v = ratingBar.getRating() * 10;
                value = ratingBar.getRating();
                text = spin.getSelectedItem().toString();
                hash.put(text, value);
                int cnt = 0;
                for (Float val : hash.values()) {
                    if(val!=0.0){
                        cnt++;
                    }
                }
                Log.d("TAG", "Size of updated map :: " + cnt);
                if (cnt == hash.size()) {
                    uploadButton.setActivated(true);
                }
            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!uploadButton.isActivated()) {
                    Toast.makeText(SymptomLoggingActivity.this, "Please fill all the Symptom", Toast.LENGTH_LONG).show();
                    return;
                }
                TextView RespiRateView = (TextView) findViewById(R.id.RespiRateValTextView);
                db_handler = new DatabaseHandler();
                db_handler.createLoggingDatabase();
                db_handler.createLoggingTable();
                boolean status = true;

                for(int i = 0; i < symptomsList.length; i++){
                    status = status ^ db_handler.uploadLoggingData(hash.get(symptomsList[i]), symptomsList[i]);
                }
                if(!status){
                    Log.d("Failed","Data Upload Failed");
                    Toast.makeText(SymptomLoggingActivity.this, "Log Failed", Toast.LENGTH_LONG).show();
                }
                else {
                    Log.d("Passed","Data Uploaded Successfully!!");
                    Toast.makeText(SymptomLoggingActivity.this, "Log successfully!", Toast.LENGTH_LONG).show();
                    try {
                        synchronized (this) {
                            wait(1000);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    finish();
                }
            }
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String selected_item = (String) adapterView.getItemAtPosition(i);
        Float rating = hash.get(selected_item);
        ratingBar.setRating(rating);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}