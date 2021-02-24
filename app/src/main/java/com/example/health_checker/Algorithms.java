package com.example.health_checker;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Algorithms {
    public List<Double> calcMovingAvg(int period, List<Double> data) {
        SimpleMovingAverage sma = new SimpleMovingAverage(period);
        List<Double> avgData = sma.getMA(data);
        return avgData;
    }

    public int countZeroCrossings(List<Double> points) {
        double prev = points.get(0);
        double prevSlope = 0;
        float st = 1280;
        double p;
        List<Double> extremes = new ArrayList<Double>();
        int peakCount = 0;

        for (int i = 1; i < points.size(); i++) {
            p = points.get(i);
            double slope = p - prev;
            if (slope * prevSlope < 0) {
                extremes.add(prev);
                peakCount += 1;
            }
            prevSlope = slope;
            prev = p;
        }
        return peakCount;
    }

    public int countZerosThreshold(List<Double> points) {
        double prev = points.get(0);
        double prevSlope = 0;
        float st = 1280;
        double p;
        List<Double> extremes = new ArrayList<Double>();
        List<Double> widths = new ArrayList<Double>();
        double sumWidth = 0.0;
        double avgWidth;
        int peakCount = 0;

        for (int i = 1; i < points.size(); i++) {
            p = points.get(i);
            double slope = p - prev;
            if (slope * prevSlope < 0) {
                extremes.add(prev);
                peakCount += 1;
            }
            prevSlope = slope;
            prev = p;
        }

        boolean sd = false;
        for (int i = 1; i < extremes.size(); i++) {

            widths.add(Math.abs(extremes.get(i) - extremes.get(i - 1)));
        }

        for (int i = 0; i < widths.size(); i++) {
            sumWidth += widths.get(i);
        }

        avgWidth = sumWidth / widths.size();

        int peaksNew = 0;
        for (int i = 1; i < extremes.size(); i++) {
            if (Math.abs(extremes.get(i) - extremes.get(i - 1)) >= avgWidth) {
                peaksNew += 1;
            }
        }

        return peaksNew;
    }
}

class SimpleMovingAverage {
    private final int period;
    Queue<Double> window = new LinkedList<Double>();
    private double sum;
    private float[] tmp = new float[20];

    public SimpleMovingAverage(int period) {
        assert period > 0 : "Period must be a positive integer!";
        this.period = period;
    }

    public List<Double> getMA(List<Double> data) {
        List<Double> myData = new ArrayList<Double>(data.size());
        float[] tmp = new float[20];

        for (double x : data) {
            newNum(x);
            myData.add(getAvg());
        }
        return myData;
    }

    public void newNum(double num) {
        sum += num;
        window.add(num);
        if (window.size() > period) {
            sum -= window.remove();
        }
    }

    public double getAvg() {
        if (window.isEmpty()) return 0;
        return sum / window.size();
    }
}
