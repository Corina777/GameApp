package com.devcookie.estimote.gameapp;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.estimote.sdk.SystemRequirementsChecker;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MyApplication.MyBeaconFoundListener {

    BeaconDatabaseHelper beaconDatabaseHelper;
    RecyclerView recyclerView;
    AlertDialog dialog;
    ImageView image;
    String qrInput;
    Bitmap bitmap;

    public final static int WHITE = 0xFFFFFFFF;
    public final static int BLACK = 0xFF000000;
    public final static int WIDTH = 600;
    public final static int HEIGHT = 600;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MyApplication) getApplicationContext()).setBeaconListener(this);

        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                19);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FloatingActionButton myFab = (FloatingActionButton) findViewById(R.id.fab);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.show();
            }
        });

        dialog = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.dialog, null);
        dialog.setView(dialogLayout);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface d) {
                image = (ImageView) dialog.findViewById(R.id.cake);
                if (getFromDatabase().size() == 9) {
                    image.setImageBitmap(bitmap);
                } else {
                    Bitmap icon = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                            R.drawable.locked_cake);
                    float imageWidthInPX = (float) image.getWidth();
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(Math.round(imageWidthInPX),
                            Math.round(imageWidthInPX * (float) icon.getHeight() / (float) icon.getWidth()));
                    image.setLayoutParams(layoutParams);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        SystemRequirementsChecker.checkWithDefaultDialogs(this);
    }

    public List<FoundBeacon> getFromDatabase() {
        beaconDatabaseHelper = BeaconDatabaseHelper.getsInstance(this);
        SQLiteDatabase db = beaconDatabaseHelper.getReadableDatabase();
        String selectQuery = "SELECT * FROM BEACONS_FOUND WHERE TIMESTAMP IS NOT NULL";
        Cursor cursor = db.rawQuery(selectQuery, null);
        List<FoundBeacon> foundBeacons = new ArrayList<>();
        try {
            if (cursor.moveToFirst()) {
                do {
                    foundBeacons.add(new FoundBeacon(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4)));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d("error", "Error querying the database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return foundBeacons;
    }

    @Override
    public void newBeaconFound(FoundBeacon foundBeacon) {
        if (getFromDatabase().size() == 9) {
            makeQRCode();
            dialog.show();
        } else {
            showNotification("You found a new beacon!", "Tap to see the clue.");
            RecyclerAdapter adapter = new RecyclerAdapter(this, getFromDatabase());
            recyclerView.setAdapter(adapter);
            recyclerView.invalidate();
        }
    }

    public void showNotification(String title, String message) {
        Intent notifyIntent = new Intent(this, MainActivity.class);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivities(this, 0,
                new Intent[]{notifyIntent}, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();
        notification.defaults |= Notification.DEFAULT_SOUND;
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
    }

    public void makeQRCode() {
        qrInput = (DateFormat.format("dd-MM-yyyy hh:mm:ss", new java.util.Date()).toString());
        try {
            bitmap = encodeAsBitmap(qrInput);

        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    Bitmap encodeAsBitmap(String str) throws WriterException {
        BitMatrix result;
        try {
            result = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, WIDTH, HEIGHT, null);
        } catch (IllegalArgumentException iae) {
            return null;
        }
        int w = result.getWidth();
        int h = result.getHeight();
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            int offset = y * w;
            for (int x = 0; x < w; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, w, 0, 0, w, h);
        return bitmap;
    }
}
