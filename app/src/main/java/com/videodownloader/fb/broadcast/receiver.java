package com.videodownloader.fb.broadcast;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.videodownloader.fb.MainActivity;
import com.videodownloader.fb.R;
import com.videodownloader.fb.service.DownloadService;
import com.videodownloader.fb.widget.CollectionWidget;

import static android.app.Activity.RESULT_OK;

/**
 * Created by android2 on 18/4/17.
 */

public class receiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            String string = bundle.getString(DownloadService.FILEPATH);
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            int resultCode = bundle.getInt(DownloadService.RESULT);
            CollectionWidget.update(context);
            Intent intent1 = new Intent(context, CollectionWidget.class);
            intent1.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            intent1.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, level);
            context.sendBroadcast(intent1);
          //  Toast.makeText(context, "" + level, Toast.LENGTH_LONG).show();

            if (resultCode == RESULT_OK) {
               // Toast.makeText(context, "Download complete. Download URI: " + string, Toast.LENGTH_LONG).show();
                addNotification(context, "Download Completed");
                //textView.setText("Download done");
            } else {
              //  Toast.makeText(context, "Download failed", Toast.LENGTH_LONG).show();
              //  failedNotification(context, "Download Failed");
                //textView.setText("Download failed");
            }
        }
    }

    private void addNotification(Context context, String status) {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(status)
                        .setContentText("your video dounloaded");

        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }
    private void failedNotification(Context context, String status) {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(status)
                        .setContentText("failed to download try again");

        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }
}
