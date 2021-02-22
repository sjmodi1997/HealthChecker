package com.example.health_checker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
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
    String folder_path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Health-Checker/";
    String vid_name = "heart_rate.mp4";
    String mpjeg_name = "heart_rate.mjpeg";
    String avi_name = "heart_rate.avi";
    DatabaseHandler db_handler;
    String value;
    Button upload;
    private Uri fileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart_rate);
        upload = (Button) findViewById(R.id.heartRateUpData);
        upload.setVisibility(View.INVISIBLE);
        configure_permissions();

        cameraActivity = new CameraActivity();
        Button measure = (Button) findViewById(R.id.heartRateBtn);

        if (!hasCamera()) {
            measure.setEnabled(false);
        }

        measure.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Intent measureRate = new Intent(HeartRateActivity.this, HeartRateService.class);
                start_recording_intent();

            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TextView RespiRateView = (TextView) findViewById(R.id.RespiRateValTextView);
                db_handler = new DatabaseHandler();
                db_handler.create_database();
                db_handler.create_table();

                if (db_handler.upload_data(Integer.parseInt(value), "HeartRate")) {
                    Toast.makeText(HeartRateActivity.this, "Data Uploaded", Toast.LENGTH_LONG).show();
                    upload.setVisibility(View.INVISIBLE);
                } else {
                    Toast.makeText(HeartRateActivity.this, "Upload Failed", Toast.LENGTH_LONG).show();
                    upload.setVisibility(View.VISIBLE);
                }
                //upload.setClickable(false);
            }
        });

    }

    private boolean hasCamera() {
        Log.d("a", Boolean.toString(getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)));
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }

    public void start_recording_intent() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 45);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, VIDEO_CAPTURE);
    }

    void configure_permissions() {
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

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VIDEO_CAPTURE) {
            if (resultCode == RESULT_OK) {
                // The rest of the code takes the video into the input stream and writes it to the location given in the internal storage
                Log.d("uy", "ok res");
                File newfile;
                //data.
                AssetFileDescriptor videoAsset = null;
                FileInputStream inputStream = null;
                OutputStream outputStream = null;
                try {

                    videoAsset = getContentResolver().openAssetFileDescriptor(data.getData(), "r");
                    Log.d("uy", "vid ead");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                try {
                    inputStream = videoAsset.createInputStream();
                    Log.d("uy", "in stream");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Log.d("uy", "dir");
                Log.d("uy", Environment.getExternalStorageDirectory().getAbsolutePath());
                File dir = new File(folder_path);
                if (!dir.exists()) {
                    dir.mkdirs();
                    Log.d("uy", "mkdir");
                }

                newfile = new File(dir, vid_name);
                Log.d("uy", "hr");

                if (newfile.exists()) {
                    newfile.delete();
                }


                try {
                    outputStream = new FileOutputStream(newfile);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                byte[] buf = new byte[1024];
                int len;

                while (true) {
                    try {
                        Log.d("uy", "try");
                        if (((len = inputStream.read(buf)) > 0)) {
                            Log.d("uy", "File write");
                            outputStream.write(buf, 0, len);
                        } else {
                            Log.d("uy", "else");
                            inputStream.close();
                            outputStream.close();
                            break;
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

                // Function to convert video to avi for processing the heart rate
                convert_video_commands();

                Toast.makeText(this, "Video has been saved to:\n" +
                        data.getData(), Toast.LENGTH_LONG).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Video recording cancelled.",
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Failed to record video",
                        Toast.LENGTH_LONG).show();
            }


        }
    }


    // Function to convert video to avi for processing the heart rate
    public void convert_video_commands() {
        //Loads the ffmpeg library
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
            // Handle if FFmpeg is not supported by device
        }

        // If the .mpjep files exist it deletes the older file
        File newfile = new File(folder_path + mpjeg_name);

        if (newfile.exists()) {
            newfile.delete();
        }

        try {
            // to execute "ffmpeg -version" command you just need to pass "-version"
            ffmpeg.execute(new String[]{"-i", folder_path + vid_name, "-vcodec", "mjpeg", folder_path + mpjeg_name}, new ExecuteBinaryResponseHandler() {

                @Override
                public void onStart() {
                    //showProgressDialogWithTitle("Converting to AVI and Measuring Heart Rate");
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
            // Handle if FFmpeg is already running
        }

        // If the .avi file exist it deletes the older file
        File avi_newfile = new File(folder_path + avi_name);

        if (avi_newfile.exists()) {
            avi_newfile.delete();
        }
        try {
            // to execute "ffmpeg -version" command you just need to pass "-version"
            ffmpeg.execute(new String[]{"-i", folder_path + mpjeg_name, "-vcodec", "mjpeg", folder_path + avi_name}, new ExecuteBinaryResponseHandler() {

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

                    while (true) {

                        try {
                            // Calculate the heart rate
                            String heart_rate = cameraActivity.measure_heart_rate(folder_path, avi_name);
                            if (heart_rate != "") {
                                // Display the heart rate
                                TextView textView = (TextView) findViewById(R.id.heartRateValTextView);
                                Button button = (Button) findViewById(R.id.heartRateBtn);
                                value = heart_rate;
                                textView.setText("HEART RATE IS: " + heart_rate + "\n");
                                button.setText("MEASURE HEART RATE AGAIN");
                                upload.setVisibility(View.VISIBLE);
                                //hideProgressDialogWithTitle();
                                break;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

        } catch (FFmpegCommandAlreadyRunningException e) {
            // Handle if FFmpeg is already running
        }
    }


}