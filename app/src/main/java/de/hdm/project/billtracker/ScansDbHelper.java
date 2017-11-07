package de.hdm.project.billtracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class ScansDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Scans.db";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + ScansContract.ScanEntry.TABLE_NAME + "( " +
                    ScansContract.ScanEntry._ID + " INTEGER PRIMARY KEY, " +
                    ScansContract.ScanEntry.COLUMN_NAME_IMAGE_PATH + " TEXT, " +
                    ScansContract.ScanEntry.COLUMN_NAME_DATE + " INT, " +
                    ScansContract.ScanEntry.COLUMN_NAME_SUM + " DOUBLE, " +
                    ScansContract.ScanEntry.COLUMN_NAME_CATEGORY + " TEXT)";

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
    public long createScan(ScansDbHelper dbHelper, String imagePath, Double totalSum, String category) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        System.out.println("createScan() imagePath: " + imagePath);

        ContentValues values = new ContentValues();
        values.put(ScansContract.ScanEntry.COLUMN_NAME_IMAGE_PATH, imagePath);
        values.put(ScansContract.ScanEntry.COLUMN_NAME_SUM, totalSum);
        values.put(ScansContract.ScanEntry.COLUMN_NAME_DATE, System.currentTimeMillis());
        values.put(ScansContract.ScanEntry.COLUMN_NAME_CATEGORY, category);

        long newRowId = db.insert(ScansContract.ScanEntry.TABLE_NAME, null, values);

        return newRowId;
    }

    // Update a scan entry's COLUMN_NAME_SUM
    public void updateScanSum(ScansDbHelper dbHelper, String imagePath, Double totalSum) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ScansContract.ScanEntry.COLUMN_NAME_SUM, totalSum);

        String selection = ScansContract.ScanEntry.COLUMN_NAME_IMAGE_PATH + " = ?";
        String[] selectionArgs = {imagePath};

        int count = db.update(
                ScansContract.ScanEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs
        );
    }

    // Update a scan entry's COLUMN_NAME_CATEGORY
    public void updateScanCategory(ScansDbHelper dbHelper, String imagePath, String category) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ScansContract.ScanEntry.COLUMN_NAME_CATEGORY, category);

        String selection = ScansContract.ScanEntry.COLUMN_NAME_IMAGE_PATH + " = ?";
        String[] selectionArgs = {imagePath};

        int count = db.update(
                ScansContract.ScanEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs
        );
    }

    // Update a scan entry's COLUMN_NAME_DATE
    public void updateScanDate(ScansDbHelper dbHelper, String imagePath, int date) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ScansContract.ScanEntry.COLUMN_NAME_DATE, date);

        String selection = ScansContract.ScanEntry.COLUMN_NAME_IMAGE_PATH + " = ?";
        String[] selectionArgs = {imagePath};

        int count = db.update(
                ScansContract.ScanEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs
        );
    }

    // Get all persisted scans
    public List<Scan> getScans(ScansDbHelper dbHelper) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String sortOrder = ScansContract.ScanEntry.COLUMN_NAME_DATE + " DESC";

        Cursor cursor = db.query(
                ScansContract.ScanEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                sortOrder
        );

        List<Scan> scanList = new ArrayList<>();

        while(cursor.moveToNext()) {
            int date = cursor.getInt(cursor.getColumnIndexOrThrow(ScansContract.ScanEntry.COLUMN_NAME_DATE));
            String category = cursor.getString(cursor.getColumnIndexOrThrow(ScansContract.ScanEntry.COLUMN_NAME_CATEGORY));
            Double sum = cursor.getDouble(cursor.getColumnIndexOrThrow(ScansContract.ScanEntry.COLUMN_NAME_SUM));
            String imagePath = cursor.getString(cursor.getColumnIndexOrThrow(ScansContract.ScanEntry.COLUMN_NAME_IMAGE_PATH));

            scanList.add(new Scan(date, category, sum, imagePath));
        }
        cursor.close();

        return scanList;
    }

    // Get all persisted scans of a category
    public List<Scan> getCategoryScans(ScansDbHelper dbHelper, String category) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selection = ScansContract.ScanEntry.COLUMN_NAME_CATEGORY + " = ?";
        String[] selectionArgs = {category};
        String sortOrder = ScansContract.ScanEntry.COLUMN_NAME_DATE + " DESC";

        Cursor cursor = db.query(
                ScansContract.ScanEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );

        List<Scan> scanList = new ArrayList<>();

        while(cursor.moveToNext()) {
            int date = cursor.getInt(cursor.getColumnIndexOrThrow(ScansContract.ScanEntry.COLUMN_NAME_DATE));
            String cat = cursor.getString(cursor.getColumnIndexOrThrow(ScansContract.ScanEntry.COLUMN_NAME_CATEGORY));
            Double sum = cursor.getDouble(cursor.getColumnIndexOrThrow(ScansContract.ScanEntry.COLUMN_NAME_SUM));
            String imagePath = cursor.getString(cursor.getColumnIndexOrThrow(ScansContract.ScanEntry.COLUMN_NAME_IMAGE_PATH));

            scanList.add(new Scan(date, cat, sum, imagePath));
        }
        cursor.close();

        return scanList;
    }

    // Delete all scans in a category
    public void deleteScans() {

    }

    // Delete a scan entry
    public void deleteScan() {

    }

}
