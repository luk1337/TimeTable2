package com.luk.timetable2.widget;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViewsService;

/**
 * Created by luk on 5/12/15.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class WidgetViewsService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new WidgetViewsFactory(
                getApplicationContext(),
                intent.getExtras().getString("variant")
        );
    }
}
