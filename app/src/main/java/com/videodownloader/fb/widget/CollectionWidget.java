package com.videodownloader.fb.widget;

import android.annotation.TargetApi;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.videodownloader.fb.R;

/**
 * Implementation of App Widget functionality.
 */
public class CollectionWidget extends AppWidgetProvider {

    Handler handler = new Handler();
    public static void updateAppWidget(final Context context, AppWidgetManager appWidgetManager,
                                int[] appWidgetId) {

        final int[] level = {0};
        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.collection_widget);

        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);

        if (batteryStatus != null) {
            level[0] = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
        }
        views.setTextViewText(R.id.tv_level, String.valueOf(level[0]) + " %");
        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver,
                new IntentFilter(""));

        //  registerReceiver(mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        // Set up the collection
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            setRemoteAdapter(context, views);
        } else {
            setRemoteAdapterV11(context, views);
        }
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    public static void update(Context context) {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);
        int level = 0;
        if (batteryStatus != null) {
            level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
        }
        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.collection_widget);
        views.setTextViewText(R.id.tv_level, String.valueOf(level) + " %");
    }

    public static BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.collection_widget);
            IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = context.registerReceiver(null, ifilter);
            int level = 0;
            if (batteryStatus != null) {
                level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            }
            views.setTextViewText(R.id.tv_level, String.valueOf(level) + " %");
        }
    };

    @Override
    public void onUpdate(final Context context, final AppWidgetManager appWidgetManager, final int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
    /*    Runnable r = new Runnable() {
            @Override
            public void run() {
                updateAppWidget(context, appWidgetManager, appWidgetIds);
            }
        };
        handler.postDelayed(r, 1000 * 10);*/

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    /**
     * Sets the remote adapter used to fill in the list items
     *
     * @param views RemoteViews to set the RemoteAdapter
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private static void setRemoteAdapter(Context context, @NonNull final RemoteViews views) {
        views.setRemoteAdapter(R.id.tv_level,
                new Intent(context, WidgetService.class));
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);
        int level = 0;
        if (batteryStatus != null) {
            level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
        }
        views.setTextViewText(R.id.tv_level, String.valueOf(level) + " %");
    }

    /**
     * Sets the remote adapter used to fill in the list items
     *
     * @param views RemoteViews to set the RemoteAdapter
     */
    @SuppressWarnings("deprecation")
    private static void setRemoteAdapterV11(Context context, @NonNull final RemoteViews views) {
        views.setRemoteAdapter(0, R.id.tv_level,
                new Intent(context, WidgetService.class));
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);
        int level = 0;
        if (batteryStatus != null) {
            level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
        }
        views.setTextViewText(R.id.tv_level, String.valueOf(level) + " %");
    }


}

