package com.example.health_checker;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Environment;
import android.util.Log;

import java.io.File;

public class DatabaseHandler {
    final String table_name = "log";
    String folder_path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Health-Checker/";
    String databse_name = "Health_Logs";
    SQLiteDatabase db;

    public void create_database() {
        try {
            File dir = new File(folder_path);
            if (!dir.exists()) {
                dir.mkdirs();
                Log.d("uy", "mkdir");
            }

            db = SQLiteDatabase.openOrCreateDatabase(folder_path + databse_name, null);
            db.beginTransaction();
            db.setTransactionSuccessful();
            //db.endTransaction();
        } catch (SQLiteException e) {
            Log.d("DB", e.getMessage());
        } finally {
            db.endTransaction();
        }
    }

    public boolean check_table_exists() {
        String sql_table_check = "SELECT name FROM sqlite_master WHERE type='table' AND name='" + table_name + "'";
        Cursor mCursor = db.rawQuery(sql_table_check, null);

        return mCursor.getCount() > 0;
    }

    public void create_table() {

        //if (check_table_exists()) {
        try {
            db.beginTransaction();
            String make_table_query = "CREATE TABLE IF NOT EXISTS " + table_name + "("
                    + "time TEXT PRIMARY KEY, "
                    + "value REAL NOT NULL, "
                    + "type TEXT NOT NULL);";
            db.execSQL(make_table_query);
            db.setTransactionSuccessful();

        } catch (SQLiteException e) {
            Log.d("FAIL", e.getMessage());
        } finally {
            db.endTransaction();
        }
        //}
    }

    public boolean upload_data(double value, String type) {
        try {
            db.beginTransaction();
            String query = "INSERT into " + table_name
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


}
