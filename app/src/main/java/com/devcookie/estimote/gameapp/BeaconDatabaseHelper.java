package com.devcookie.estimote.gameapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class BeaconDatabaseHelper extends SQLiteOpenHelper {
    private static BeaconDatabaseHelper sInstance;

    public static final String DB_NAME = "beaconStuff";
    public static final int DB_VERSION = 1;

    public static synchronized BeaconDatabaseHelper getsInstance(Context context) {
        if (sInstance == null) {
            sInstance = new BeaconDatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    BeaconDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE BEACONS_FOUND ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "REGION TEXT, "
                + "MESSAGE TEXT, "
                + "CLUE TEXT, "
                + "TIMESTAMP TEXT);");

        insertBeacon(db, "beacon1", "Yay! First beacon! Say hi to Marco.", "TBD");
        insertBeacon(db, "beacon2", "Did you have coffee today?", "TBD");
        insertBeacon(db, "beacon4", "Up and down, up and down.", "TBD");
        insertBeacon(db, "beacon5", "Paint the web blue!", "TBD");
        insertBeacon(db, "beacon6", "On the house: marketing.", "TBD");
        insertBeacon(db, "beacon7", "Are you on facebook?", "TBD");
        insertBeacon(db, "beacon8", "Up for a movie?", "TBD");
        insertBeacon(db, "beacon9", "Take my breath away!", "TBD");
        insertBeacon(db, "beacon10", "There's no place like home.", "TBD");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    private static FoundBeacon insertBeacon(SQLiteDatabase db, String region, String message, String clue) {
        ContentValues beaconValues = new ContentValues();
        beaconValues.put("REGION", region);
        beaconValues.put("MESSAGE", message);
        beaconValues.put("CLUE", clue);
        long id = db.insert("BEACONS_FOUND", null, beaconValues);

        return new FoundBeacon((int) id, region, message, clue, String.valueOf(System.currentTimeMillis()));
    }


}
