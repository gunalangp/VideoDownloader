package com.videodownloader.fb.widget;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Handler;
import android.widget.RemoteViewsService;

import com.videodownloader.fb.R;

/**
 * WidgetService is the {@link RemoteViewsService} that will return our RemoteViewsFactory
 */
public class WidgetService extends RemoteViewsService {


    @Override
    public RemoteViewsFactory onGetViewFactory(final Intent intent) {

       /* Handler handler = new Handler();
        Runnable r = new Runnable() {
            @Override
            public void run() {

            }
        };
        handler.postDelayed(r, 1000 * 10);*/
        CollectionWidget.update(WidgetService.this);
        return new WidgetDataProvider(this, intent);
    }
}
