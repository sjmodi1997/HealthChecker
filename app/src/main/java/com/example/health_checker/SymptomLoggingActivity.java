package com.example.health_checker;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SymptomLoggingActivity extends AppCompatActivity {

    int value;
    DatabaseHandler db_handler;
    String text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symptom_logging);

        Spinner spin = (Spinner) findViewById(R.id.symSpinner);
        RatingBar ratingBar;
        Button upload = (Button) findViewById(R.id.symUpData);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.symptoms_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(adapter);
        ratingBar = (RatingBar) findViewById(R.id.symRatingBar);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {


            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {
                //float v = ratingBar.getRating() * 10;
                value = (int) (ratingBar.getRating() * 10);
                text = spin.getSelectedItem().toString();
                text = text.replaceAll("\\s", "");

                Log.d("as", text);
                Log.d("lll", String.valueOf(value));
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TextView RespiRateView = (TextView) findViewById(R.id.RespiRateValTextView);
                db_handler = new DatabaseHandler();
                db_handler.create_database();
                db_handler.create_table();

                if (db_handler.upload_data(value, text)) {
                    Toast.makeText(SymptomLoggingActivity.this, "Data Uploaded", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(SymptomLoggingActivity.this, "Upload Failed", Toast.LENGTH_LONG).show();
                }
                //upload.setClickable(false);
            }
        });
    }
}