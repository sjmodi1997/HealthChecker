package com.example.health_checker;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class HeartRateActivity extends AppCompatActivity {
    private static final int VIDEO_CAPTURE = 101;
    CameraActivity cameraActivity;
    String videoName = "heartRate.mp4";
    String folderPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Health-Checker/";
    String aviName = "heartRate.avi";
    private int fileId;
    String mpjegName = "heartRate.mjpeg";
    DatabaseHandler dbHandler;
    String value;
    Button upload;
    private Uri fileUri;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart_rate);
        upload = findViewById(R.id.heartRateUpData);
        upload.setVisibility(View.INVISIBLE);
        configurePermissions();

        cameraActivity = new CameraActivity();
        Button measure = findViewById(R.id.heartRateBtn);

        if (!hasCamera()) {
            measure.setEnabled(false);
        }

        measure.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                start_recording_intent();
                progressDialog = new ProgressDialog(getApplicationContext());
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog = ProgressDialog.show(HeartRateActivity.this, "Please wait", "Measuring Heart Rate...", false, false);
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TextView RespiRateView = findViewById(R.id.RespiRateValTextView);
                dbHandler = new DatabaseHandler();
                dbHandler.create_logging_database();
                dbHandler.create_logging_table();

                if (dbHandler.upload_logging_data(Integer.parseInt(value), "HeartRate")) {
                    Toast.makeText(HeartRateActivity.this, "Data Uploaded", Toast.LENGTH_LONG).show();
                    upload.setVisibility(View.INVISIBLE);
                } else {
                    Toast.makeText(HeartRateActivity.this, "Upload Failed", Toast.LENGTH_LONG).show();
                    upload.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    private boolean hasCamera() {
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }

    public void start_recording_intent() {
        int recordValue = 0;
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 45);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, VIDEO_CAPTURE);
    }

    void configurePermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}
                        , 10);
            }
            return;
        }
    }


    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {
        int ix;
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VIDEO_CAPTURE) {
            if (resultCode == RESULT_OK) {
                AssetFileDescriptor videoAsset = null;
                FileInputStream inputStream = null;
                OutputStream outputStream = null;
                File newFile;
                try {
                    videoAsset = getContentResolver().openAssetFileDescriptor(data.getData(), "r");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                try {
                    inputStream = videoAsset.createInputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                File dir = new File(folderPath);
                if (!dir.exists())
                    dir.mkdirs();

                newFile = new File(dir, videoName);

                if (newFile.exists())
                    newFile.delete();

                try {
                    outputStream = new FileOutputStream(newFile);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                int[] ie = new int[1024];
                byte[] buf = new byte[1024];
                int len;

                while (true) {
                    try {
                        if (((len = inputStream.read(buf)) > 0)) {
                            outputStream.write(buf, 0, len);
                        } else {
                            inputStream.close();
                            outputStream.close();
                            break;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                convertVideoCommands();

                Toast.makeText(this, "Captured video is saved to:\n" + data.getData(), Toast.LENGTH_LONG).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Video recording is cancelled.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Error: Video recording failed", Toast.LENGTH_LONG).show();
            }
        }
    }


    public void convertVideoCommands() {
        String cmd = "new";
        FFmpeg ffmpeg = FFmpeg.getInstance(this);
        try {
            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {

                @Override
                public void onStart() {
                }

                @Override
                public void onFailure() {
                }

                @Override
                public void onSuccess() {
                }

                @Override
                public void onFinish() {
                }
            });
        } catch (FFmpegNotSupportedException e) {
        }

        File newfile = new File(folderPath + mpjegName);

        if (newfile.exists()) {
            newfile.delete();
        }

        try {
            int status = -1;
            ffmpeg.execute(new String[]{"-i", folderPath + videoName, "-vcodec", "mjpeg", folderPath + mpjegName}, new ExecuteBinaryResponseHandler() {

                @Override
                public void onStart() {
                }

                @Override
                public void onProgress(String message) {

                }

                @Override
                public void onFailure(String message) {
                }

                @Override
                public void onSuccess(String message) {

                }

                @Override
                public void onFinish() {

                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
        }

        File avi_newfile = new File(folderPath + aviName);

        if (avi_newfile.exists()) {
            avi_newfile.delete();
        }
        try {
            int status = 2;
            ffmpeg.execute(new String[]{"-i", folderPath + mpjegName, "-vcodec", "mjpeg", folderPath + aviName}, new ExecuteBinaryResponseHandler() {

                @Override
                public void onStart() {

                }

                @Override
                public void onProgress(String message) {

                }

                @Override
                public void onFailure(String message) {

                }

                @Override
                public void onSuccess(String message) {

                }

                @Override
                public void onFinish() {
                int status = 0;
                    while (true) {

                        try {
                            String heart_rate = cameraActivity.measure_heart_rate(folderPath, aviName);
                            if (heart_rate != "") {
                                TextView textView = findViewById(R.id.heartRateValTextView);
                                Button button = findViewById(R.id.heartRateBtn);
                                value = heart_rate;
                                textView.setText("HEART RATE IS: " + heart_rate + "\n");
                                button.setText("MEASURE HEART RATE AGAIN");
                                progressDialog.dismiss();
                                upload.setVisibility(View.VISIBLE);
                                break;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

        } catch (FFmpegCommandAlreadyRunningException e) {
            e.printStackTrace();
        }
    }


}