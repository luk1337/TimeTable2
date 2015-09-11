package com.luk.timetable2.widget.dark;

import android.content.Intent;
import android.widget.RemoteViewsService;

import com.luk.timetable2.widget.dark.WidgetViewsFactory;

/**
 * Created by luk on 5/12/15.
 */
public class WidgetViewsService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        WidgetViewsFactory dataProvider = new WidgetViewsFactory(getApplicationContext(), intent);
        return dataProvider;
    }
}
