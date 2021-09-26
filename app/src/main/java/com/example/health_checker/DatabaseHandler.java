package com.example.health_checker;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.Queue;

import static java.lang.Math.abs;

public class DatabaseHandler {
    final String loggingTableName = "logs", dataTable = "DataTable";
    final String TAG = "DB";
    final String FAIL_TAG = "FAIL";
    String folder_path = "/storage/emulated/0/Health-Checker/";
    //String folder_path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Health-Checker/";
    private String databaseName = "HealthHistory";
    private SQLiteDatabase db;
    String[] columnsList = {
            "Breathing Problem",
            "Fever",
            "Cough",
            "Headache",
            "Loss of Smell or Taste",
            "Sore throat",
            "RespiratoryRate",
            "HeartRate"
    };

    /*
        run the query and catch the error if there are any
     */
    private void runQuery(String query) {
        if(query.length()==0){
            Log.d(TAG, "Query is Empty");
            return;
        }
        try {
            db.beginTransaction();
            Log.d(TAG, "Running the Query :: " + query);
            db.execSQL(query);
            db.setTransactionSuccessful();
        } catch (SQLiteException e) {
            Log.d(FAIL_TAG, e.getMessage());
        } finally {
            db.endTransaction();
        }
        return;
    }

    public void createLoggingDatabase() {
        try {
            File dir = new File(folder_path);
            if (!dir.exists()) {
                dir.mkdirs();
                Log.d(TAG, "Creating a new Logging Directory :: " + dir);
            }
            Log.d(TAG, "Creating a new Logging Database :: " + folder_path + databaseName);
            db = SQLiteDatabase.openOrCreateDatabase(folder_path + databaseName, null);
            db.beginTransaction();
            db.setTransactionSuccessful();
        } catch (SQLiteException e) {
            Log.d(FAIL_TAG, e.getMessage());
        } finally {
            db.endTransaction();
        }
    }
    /*
        Create a new Database
     */
    public void createDB() {
        try {
            File dir = new File(folder_path);
            if (!dir.exists()) {
                dir.mkdirs();
                Log.d(TAG, "Creating a new Directory :: " + dir);
            }
            Log.d(TAG, "Creating a new Database :: " + folder_path + databaseName);
            db = SQLiteDatabase.openOrCreateDatabase(folder_path + databaseName, null);
            db.beginTransaction();
            db.setTransactionSuccessful();
        } catch (SQLiteException e) {
            Log.d(TAG, e.getMessage());
        } finally {
            db.endTransaction();
        }
    }

    /*
        Creates a new Logging Table
     */
    public void createLoggingTable() {

        String query = "CREATE TABLE IF NOT EXISTS " + loggingTableName + "("
                    + "time TEXT PRIMARY KEY, "
                    + "value REAL NOT NULL, "
                    + "type TEXT NOT NULL);";
        runQuery(query);
        return;
    }

    public void createTable() {
        String query = "CREATE TABLE IF NOT EXISTS " + dataTable + "("
                + "recID integer PRIMARY KEY autoincrement, "
                + "Shortness_of_Breath  real, "
                + "Fever real, "
                + "Cough real, "
                + "Headache real, "
                + "Loss_of_Smell_or_Taste real, "
                + "Sore_throat real, "
                + "HeartRate real, "
                + "RespiratoryRate real);";
        runQuery(query);
        return;
    }

    public boolean uploadLoggingData(double value, String type) {
        String query = "INSERT into " + loggingTableName
                    + "(time, value, type) values ("
                    + "\"" + System.currentTimeMillis() + "\"" + ','
                    + value + ',' + "\"" + type + "\"" + ");";
        try{
            runQuery(query);
        }
        catch (Exception e){
            return false;
        }
        return true;
    }

    public boolean insertRow(){
        float values[] = new float[12];

        try {
            db.beginTransaction();
            for(int i = 0; i < columnsList.length; i++) {
                String query = "select * from "
                        + loggingTableName
                        + " where type = \""
                        + columnsList[i]
                        + "\" Order By time DESC LIMIT 1;";
                Cursor c = db.rawQuery(query, null);

                c.moveToLast();
                values[i] = c.getFloat(c.getColumnIndex("value"));
                Log.d("val", String.valueOf(values[i]));

            }
            String query = "INSERT into " + dataTable
                    + " (Shortness_of_Breath, Fever, Cough ,"
                    + " Headache, Loss_of_Smell_or_Taste, Sore_throat,"
                    + " RespiratoryRate, HeartRate) values (" + values[0] + ","
                    + values[1] + "," + values[2] + "," + values[3] + "," + values[4] + ","
                    + values[5] + "," + values[6] + "," + values[7] + "," + values[8] + ");";
            db.execSQL(query);
            db.setTransactionSuccessful();
        } catch (SQLiteException e) {
            e.printStackTrace();
            return false;
        } finally {
            db.endTransaction();
        }
        return true;
    }
}
