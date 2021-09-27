package com.example.health_checker;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Main Algorithm class
 */
public class Algorithms {
    /**
     * Calculate Moving Avg
     * @param period
     * @param data
     * @return
     */
    public List<Double> calcMovingAvg(int period, List<Double> data) {
        SimpleMovingAverage sma = new SimpleMovingAverage(period);
        List<Double> avgData = sma.getMA(data);
        return avgData;
    }

    /**
     * Method to count zero crossing
     * @param points
     * @return
     */
    public int countZeroCrossings(List<Double> points) {
        double prev = points.get(0);
        double prevSlope = 0;
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

    /**
     * Method for count Zero Threshold
     * @param points
     * @return
     */
    public int countZerosThreshold(List<Double> points) {
        double prev = points.get(0);
        double prevSlope = 0;
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

/**
 * Main class for simple moving avg
 */
class SimpleMovingAverage {
    private final int period;
    Queue<Double> window = new LinkedList<Double>();
    private double sum;

    /**
     * Constructor Mathod
     * @param period
     */
    public SimpleMovingAverage(int period) {
        assert period > 0 : "Period must be a positive integer!";
        this.period = period;
    }

    /**
     * Get a new List of Number
     * @param data
     * @return
     */
    public List<Double> getMA(List<Double> data) {
        List<Double> myData = new ArrayList<Double>(data.size());

        for (double x : data) {
            newNum(x);
            myData.add(getAvg());
        }
        return myData;
    }

    /**
     * Update a number
     * @param num
     */
    public void newNum(double num) {
        sum += num;
        window.add(num);
        if (window.size() > period) {
            sum -= window.remove();
        }
    }

    /**
     * Get the avg in the window
     * @return
     */
    public double getAvg() {
        if (window.isEmpty())
            return 0;
        return sum / window.size();
    }
}
