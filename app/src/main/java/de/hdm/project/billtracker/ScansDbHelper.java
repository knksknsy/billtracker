package de.hdm.project.billtracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ScansDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Scans.db";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + ScansContract.ScanEntry.TABLE_NAME + "( " +
                    ScansContract.ScanEntry._ID + " INTEGER PRIMARY KEY, " +
                    ScansContract.ScanEntry.COLUMN_NAME_IMAGE_PATH + " TEXT, " +
                    ScansContract.ScanEntry.COLUMN_NAME_DATE + " TEXT, " +
                    ScansContract.ScanEntry.COLUMN_NAME_SUM + " DOUBLE)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + ScansContract.ScanEntry.TABLE_NAME;

    public ScansDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    // Persist a new scan entry
    public long createScan(SQLiteDatabase db, String imagePath, Double totalSum) {
        ContentValues values = new ContentValues();
        values.put(ScansContract.ScanEntry.COLUMN_NAME_IMAGE_PATH, imagePath);
        values.put(ScansContract.ScanEntry.COLUMN_NAME_SUM, totalSum);
        String date = new SimpleDateFormat("yyyyMMdd").format(new Date());
        values.put(ScansContract.ScanEntry.COLUMN_NAME_DATE, date);

        long newRowId = db.insert(ScansContract.ScanEntry.TABLE_NAME, null, values);
        return newRowId;
    }

    // Update a scan entry
    public void updateScan() {

    }

    // Delete a scan entry
    public void deleteScan() {

    }

    // Get all persisted scans
    public void getScans() {

    }

    // Delete all scans in a category
    public void deleteScans() {

    }

}
