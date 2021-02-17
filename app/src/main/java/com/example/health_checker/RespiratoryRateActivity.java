package com.example.health_checker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;


import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class RespiratoryRateActivity extends AppCompatActivity {
    //TextView RespiRateView;
    private ProgressDialog progressDialog;
    /*Handler textHandler = new Handler() {
        public void handleMessage(String msg){
            TextView RespiRateView = (TextView) findViewById(R.id.RespiRateTextView);
            RespiRateView.setText("Respiratory Rate is "+msg);
        }
    };*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_respiratory_rate);

        Button measure = (Button) findViewById(R.id.RespiRateBtn);

        measure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent measureRate = new Intent(RespiratoryRateActivity.this, RespiratoryRateService.class);
                progressDialog = new ProgressDialog(getApplicationContext());
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                //progressDialog.setCancelable(false);
                //progressDialog.setMessage("Measuring Respiratory Rate...");
                //Log.d("as", getApplicationContext())
                //progressDialog.setVisibility(View.VISIBLE);
                progressDialog = progressDialog.show(RespiratoryRateActivity.this, "Please wait", "Measuring Respiratory Rate...", false, true);
                startService(measureRate);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(msgReceiver, new IntentFilter("Respiratory Rate"));
    }

    protected void onPause (){
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(msgReceiver);
    }

    /*private void showProgressDialogWithTitle(String substring) {
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(substring);
        progressDialog.show();
    }

    private void hideProgressDialogWithTitle() {
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.dismiss();
    }*/

    private BroadcastReceiver msgReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Display the respiratory rate
            String value = intent.getStringExtra("RRvalue");

            if(value == null){
                Log.d("Null"," null");
            }
            //progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            Log.d("as", String.valueOf(progressDialog.isShowing()));
            progressDialog.dismiss();
            Log.d("as", String.valueOf(progressDialog.isShowing()));

            TextView RespiRateView = (TextView) findViewById(R.id.RespiRateValTextView);
            RespiRateView.setText("RESPIRATORY RATE IS " + value);
            Button measure = (Button) findViewById(R.id.RespiRateBtn);

            measure.setText("MEASURE RESPIRATORY RATE AGAIN");
            //hideProgressDialogWithTitle();
        }
    };
}