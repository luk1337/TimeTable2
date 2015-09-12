package com.luk.timetable2.widget.light;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by luk on 5/12/15.
 */
public class WidgetViewsService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        WidgetViewsFactory dataProvider = new WidgetViewsFactory(getApplicationContext());
        return dataProvider;
    }
}
