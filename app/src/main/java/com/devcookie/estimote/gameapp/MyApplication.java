package com.devcookie.estimote.gameapp;

import android.app.Application;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.text.format.DateFormat;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class MyApplication extends Application {

    public interface MyBeaconFoundListener {
        void newBeaconFound(FoundBeacon foundBeacon);
    }

    private MyBeaconFoundListener listener;
    private BeaconManager beaconManager;
    private List<Region> regions;
    BeaconDatabaseHelper beaconDatabaseHelper;

    Region beacon1 = new Region("beacon1", UUID.fromString("f7826da6-4fa2-4e98-8024-bc5b71e0893e"), 39225, 45997);
    Region beacon2 = new Region("beacon2", UUID.fromString("f7826da6-4fa2-4e98-8024-bc5b71e0893e"), 26213, 11749);
    Region beacon4 = new Region("beacon4", UUID.fromString("f7826da6-4fa2-4e98-8024-bc5b71e0893e"), 9501, 30672);
    Region beacon5 = new Region("beacon5", UUID.fromString("f7826da6-4fa2-4e98-8024-bc5b71e0893e"), 3427, 57573);
    Region beacon6 = new Region("beacon6", UUID.fromString("f7826da6-4fa2-4e98-8024-bc5b71e0893e"), 14211, 45210);
    Region beacon7 = new Region("beacon7", UUID.fromString("f7826da6-4fa2-4e98-8024-bc5b71e0893e"), 40344, 19924);
    Region beacon8 = new Region("beacon8", UUID.fromString("f7826da6-4fa2-4e98-8024-bc5b71e0893e"), 44977, 50694);
    Region beacon9 = new Region("beacon9", UUID.fromString("f7826da6-4fa2-4e98-8024-bc5b71e0893e"), 1476, 50852);
    Region beacon10 = new Region("beacon10", UUID.fromString("f7826da6-4fa2-4e98-8024-bc5b71e0893e"), 61264, 55862);


    @Override
    public void onCreate() {
        super.onCreate();

        regions = new ArrayList<>();
        regions.add(beacon1);
        regions.add(beacon2);
        regions.add(beacon4);
        regions.add(beacon5);
        regions.add(beacon6);
        regions.add(beacon7);
        regions.add(beacon8);
        regions.add(beacon9);
        regions.add(beacon10);

        beaconDatabaseHelper = BeaconDatabaseHelper.getsInstance(this);


        beaconManager = new BeaconManager(getApplicationContext());
        beaconManager.setMonitoringListener(new BeaconManager.MonitoringListener() {
            @Override
            public void onEnteredRegion(Region region, List<Beacon> list) {
                String timeStamp = (DateFormat.format("dd-MM-yyyy hh:mm:ss", new java.util.Date()).toString());
                String regIdentifier = region.getIdentifier();
                updateTimestamp(timeStamp, regIdentifier);
                beaconManager.stopMonitoring(region);
                listener.newBeaconFound(new FoundBeacon());
            }

            @Override
            public void onExitedRegion(Region region) {
//                do nothing in this case

            }
        });
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                for (Region beacon : regions) {
                    beaconManager.startMonitoring(beacon);
                }
            }
        });
    }

    public int updateTimestamp(String timeStamp, String region) {
        SQLiteDatabase db = beaconDatabaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("TIMESTAMP", timeStamp);

        return db.update("BEACONS_FOUND", values, "REGION = ?", new String[]{region});
    }

    public void setBeaconListener(MyBeaconFoundListener listener) {
        this.listener = listener;
    }
}
