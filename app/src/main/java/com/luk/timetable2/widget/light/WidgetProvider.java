package com.luk.timetable2.widget.light;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.RemoteViews;

import com.luk.timetable2.R;
import com.luk.timetable2.Utils;
import com.luk.timetable2.widget.WidgetViewsFactory;
import com.luk.timetable2.widget.WidgetViewsService;

/**
 * Implementation of App Widget functionality.
 */
public class WidgetProvider extends AppWidgetProvider {
    private static String TITLE_CLICKED = "START_APP";
    private static String variant = "light";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                         int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
        ComponentName watchWidget = new ComponentName(context, WidgetProvider.class);

        // setup refresh, title button
        views.setOnClickPendingIntent(R.id.title, getPendingSelfIntent(context, TITLE_CLICKED));
        appWidgetManager.updateAppWidget(watchWidget, views);

        Intent intent = new Intent(context, WidgetViewsFactory.class);
        views.setRemoteAdapter(R.id.widget, intent);

        RemoteViews mView = initViews(context, appWidgetId);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, mView);
    }

    private RemoteViews initViews(Context context, int widgetId) {
        RemoteViews mView = new RemoteViews(context.getPackageName(), R.layout.widget);

        Integer[] widgetColors = Utils.getWidgetColorsForVariant(variant);

        // set colors
        mView.setInt(R.id.background, "setBackgroundResource", widgetColors[1]);
        mView.setInt(R.id.header, "setBackgroundResource", widgetColors[0]);
        mView.setTextColor(R.id.title, context.getResources().getColor(widgetColors[2]));

        Intent intent = new Intent(context, WidgetViewsService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
        intent.putExtra("variant", variant);

        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        mView.setRemoteAdapter(R.id.widget, intent);

        return mView;
    }

    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        super.onReceive(context, intent);

        if (TITLE_CLICKED.equals(intent.getAction())) {
            Intent i = new Intent();
            i.setClassName("com.luk.timetable2", "com.luk.timetable2.activities.MainActivity");
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }

    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }
}

