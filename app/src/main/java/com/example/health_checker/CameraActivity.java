package com.example.health_checker;

// This class computes the Heart rate

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CameraActivity {
    public static String TAG = "Debug";
    Algorithms algorithms = new Algorithms();

    // Function to measure the heart rate
    public String measure_heart_rate(String videoPath, String videoName) throws IOException {
        VideoCapture videoCapture = new VideoCapture();

        if (new File(videoPath + videoName).exists()) {
            videoCapture.open(videoPath + videoName);
            if (videoCapture.isOpened()) {
                List<Double> extremes = new ArrayList<Double>();
                List<Double> list = new ArrayList<Double>();
                List<Double> new_list = new ArrayList<Double>();
                Mat current_frame = new Mat();
                Mat next_frame = new Mat();
                Mat diff_frame = new Mat();

                int video_length = (int) videoCapture.get(Videoio.CAP_PROP_FRAME_COUNT);
                int fps = (int) videoCapture.get(Videoio.CAP_PROP_FPS);


                videoCapture.read(current_frame);
                for (int k = 0; k < video_length - 1; k++) {
                    videoCapture.read(next_frame);
                    Core.subtract(next_frame, current_frame, diff_frame);
                    next_frame.copyTo(current_frame);
                    list.add(Core.mean(diff_frame).val[0] + Core.mean(diff_frame).val[1] + Core.mean(diff_frame).val[2]);
                }

                for (int i = 0; i < (list.size() / 5) - 1; i++) {
                    List<Double> sublist = list.subList(i * 5, (i + 1) * 5);
                    double sum = 0.0;
                    for (int j = 0; j < sublist.size(); j++) {
                        sum += sublist.get(j);
                    }

                    new_list.add(sum / 5);
                }

                int mov_period = 50;

                List<Double> avg_data = algorithms.calcMovingAvg(mov_period, new_list);
                int peakCounts = algorithms.countZeroCrossings(avg_data);

                double fpsSec = (video_length / fps);
                double count_heart_rate = (peakCounts / 2) * (60) / fpsSec;

                return "" + (int) count_heart_rate;

            } else {
                return "";
            }
        } else {

            return "";
        }
    }
}
