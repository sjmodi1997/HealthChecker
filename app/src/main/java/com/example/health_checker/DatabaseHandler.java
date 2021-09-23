package com.example.health_checker;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

import static java.lang.Math.abs;

public class DatabaseHandler {
    final String loggingTableName = "logs", dataTable = "DataTable";
    String folder_path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Health-Checker/";
    private String databaseName = "Doshi";
    private SQLiteDatabase db;
    String[] columnsList = {
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
            "RespiratoryRate",
            "HeartRate"
    };


    public void create_logging_database() {
        try {
            File dir = new File(folder_path);
            if (!dir.exists()) {
                dir.mkdirs();
                Log.d("uy", "mkdir");
            }

            db = SQLiteDatabase.openOrCreateDatabase(folder_path + databaseName, null);
            db.beginTransaction();
            db.setTransactionSuccessful();
        } catch (SQLiteException e) {
            Log.d("DB", e.getMessage());
        } finally {
            db.endTransaction();
        }
    }

    public void create_database() {
        try {
            File dir = new File(folder_path);
            if (!dir.exists()) {
                dir.mkdirs();
                Log.d("uy", "mkdir");
            }

            db = SQLiteDatabase.openOrCreateDatabase(folder_path + databaseName, null);
            db.beginTransaction();
            db.setTransactionSuccessful();
        } catch (SQLiteException e) {
            Log.d("DB", e.getMessage());
        } finally {
            db.endTransaction();
        }
    }


    public void create_logging_table() {
        try {
            db.beginTransaction();
            String query = "CREATE TABLE IF NOT EXISTS " + loggingTableName + "("
                    + "time TEXT PRIMARY KEY, "
                    + "value REAL NOT NULL, "
                    + "type TEXT NOT NULL);";
            db.execSQL(query);
            db.setTransactionSuccessful();

        } catch (SQLiteException e) {
            Log.d("FAIL", e.getMessage());
        } finally {
            db.endTransaction();
        }
    }

    public void create_table() {
        try {
            db.beginTransaction();
            String make_table_query = "CREATE TABLE IF NOT EXISTS " + dataTable + "("
                    + "recID integer PRIMARY KEY autoincrement, "
                    + "Fever real, "
                    + "Cough real, "
                    + "Shortness_of_Breath  real, "
                    + "Feeling_Tired real, "
                    + "Muscle_Ache real, "
                    + "Headache real, "
                    + "Loss_of_Smell_or_Taste real, "
                    + "Sore_throat real, "
                    + "Nausea real, "
                    + "Diarrhea real, "
                    + "HeartRate real, "
                    + "RespiratoryRate real);";
            db.execSQL(make_table_query);
            db.setTransactionSuccessful();
        } catch (SQLiteException e) {

        } finally {
            db.endTransaction();
        }

    }

    public boolean isComplete() {
        try {
            db.beginTransaction();
            String query = "select * from "
                    + loggingTableName
                    + " where type = \"RespiratoryRate\" Order By time DESC LIMIT 1;";
            Cursor c = db.rawQuery(query, null);
            if(c.getCount() < 1){
                return false;
            }
            c.moveToLast();
            String RRtime = c.getString(c.getColumnIndex("time"));

            query = "select * from "
                    + loggingTableName
                    + " where type = \"HeartRate\" Order By time DESC LIMIT 1;";
            //c = null;
            c = db.rawQuery(query, null);
            if(c.getCount() < 1){
                return false;
            }
            c.moveToLast();
            String HRtime = c.getString(c.getColumnIndex("time"));

            query = "select * from "
                    + loggingTableName
                    + " where type = \"Fever\" Order By time DESC LIMIT 1;";
            //c = null;
            c = db.rawQuery(query, null);
            if(c.getCount() < 1){
                return false;
            }
            c.moveToLast();
            String Fevertime = c.getString(c.getColumnIndex("time"));

            if(abs(Float.parseFloat(RRtime) - Float.parseFloat(Fevertime)) > 3000000 | Math.abs(Float.parseFloat(RRtime) - Float.parseFloat(HRtime)) > 3000000) {
                return false;
            }

        } catch (SQLiteException e) {
            e.printStackTrace();
            return false;
        } finally {
            db.endTransaction();
        }
        return true;
    }


    public boolean upload_logging_data(double value, String type) {
        try {
            db.beginTransaction();

            String
                    query = "INSERT into " + loggingTableName
                    + "(time, value, type) values ("
                    + "\"" + System.currentTimeMillis() + "\"" + ','
                    + value + ',' + "\"" + type + "\"" + ");";
            db.execSQL(query);
            db.setTransactionSuccessful();
        } catch (SQLiteException e) {
            return false;
        } finally {
            db.endTransaction();
            return true;
        }
    }

    public boolean insert_row(){
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
                    + " (Fever, Cough, Shortness_of_Breath, Feeling_Tired, Muscle_Ache, "
                    + "Headache, Loss_of_Smell_or_Taste, Sore_throat, Nausea, Diarrhea,"
                    + " RespiratoryRate, HeartRate) values (" + values[0] + ","
                    + values[1] + "," + values[2] + "," + values[3] + "," + values[4] + ","
                    + values[5] + "," + values[6] + "," + values[7] + "," + values[8] + ","
                    + values[9] + "," + values[10] + "," + values[11] + ");";
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
