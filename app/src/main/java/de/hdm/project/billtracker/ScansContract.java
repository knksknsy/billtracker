package de.hdm.project.billtracker;

import android.provider.BaseColumns;

public class ScansContract {

    private ScansContract() {
    }

    public static class ScanEntry implements BaseColumns {
        public static final String TABLE_NAME = "scans";
        public static final String COLUMN_NAME_IMAGE_PATH = "image_path";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_SUM = "sum";

    }

}

