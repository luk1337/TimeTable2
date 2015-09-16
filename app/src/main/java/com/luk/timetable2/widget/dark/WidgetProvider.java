package com.luk.timetable2.widget.dark;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.luk.timetable2.R;
import com.luk.timetable2.Utils;

/**
 * Implementation of App Widget functionality.
 */
public class WidgetProvider extends AppWidgetProvider {

    private static String REFRESH_CLICKED = "REFRESH";
    private static String TITLE_CLICKED = "START_APP";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
            updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
        }
    }

    void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
        ComponentName watchWidget = new ComponentName(context, WidgetProvider.class);

        // setup refresh, title button
        views.setOnClickPendingIntent(R.id.title, getPendingSelfIntent(context, TITLE_CLICKED));
        views.setOnClickPendingIntent(R.id.refresh, getPendingSelfIntent(context, REFRESH_CLICKED));
        appWidgetManager.updateAppWidget(watchWidget, views);

        Intent intent = new Intent(context, WidgetViewsFactory.class);
        views.setRemoteAdapter(appWidgetId, R.id.widget, intent);

        RemoteViews mView = initViews(context, appWidgetManager, appWidgetId);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, mView);
    }

    private RemoteViews initViews(Context context, AppWidgetManager widgetManager, int widgetId) {

        RemoteViews mView = new RemoteViews(context.getPackageName(), R.layout.widget);

        Intent intent = new Intent(context, WidgetViewsService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);

        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        mView.setRemoteAdapter(widgetId, R.id.widget, intent);

        return mView;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (REFRESH_CLICKED.equals(intent.getAction())) {
            Utils.refreshWidgets(context);
        } else if (TITLE_CLICKED.equals(intent.getAction())) {
            Intent i = new Intent();
            i.setClassName("com.luk.timetable2", "com.luk.timetable2.MainActivity");
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

