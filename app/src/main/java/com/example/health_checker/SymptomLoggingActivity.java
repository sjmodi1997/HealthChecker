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
            "Fever",
            "Cough",
            "Shortness of Breath",
            "Feeling Tired",
            "Muscle Ache",
            "Headache",
            "Loss of Smell or Taste",
            "Sore throat",
            "Nausea",
            "Diarrhea",
    };
    private HashMap<String, Float> hash;
    private RatingBar ratingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symptom_logging);

        Spinner spin = (Spinner) findViewById(R.id.symSpinner);

        Button upload = (Button) findViewById(R.id.symUpData);
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
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TextView RespiRateView = (TextView) findViewById(R.id.RespiRateValTextView);
                db_handler = new DatabaseHandler();
                db_handler.create_logging_database();
                db_handler.create_logging_table();
                boolean status = true;

                for(int i = 0; i < symptomsList.length; i++){
                    status = status ^ db_handler.upload_logging_data(hash.get(symptomsList[i]), symptomsList[i]);
                }
                if(!status){
                    Log.d("Failed","Data Upload Failed");
                    Toast.makeText(SymptomLoggingActivity.this, "Log Failed", Toast.LENGTH_LONG).show();
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