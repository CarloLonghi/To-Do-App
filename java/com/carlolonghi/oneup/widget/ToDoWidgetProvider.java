package com.carlolonghi.oneup.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.carlolonghi.oneup.R;
import com.carlolonghi.oneup.activities.MainActivity;


public class ToDoWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        for (int appWidgetId : appWidgetIds) {
            // Bind the widget with the factory used to retrive data
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            Intent intent = new Intent(context, MyWidgetRemoteViewsService.class);
            views.setRemoteAdapter(R.id.widgetListView, intent);

            // Create an Intent to launch MainActivity when the widget is clicked
            Intent intent1 = new Intent(context, MainActivity.class);
            intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent1, 0);
            views.setOnClickPendingIntent(R.id.widgetContainer, pendingIntent);
            views.setOnClickPendingIntent(R.id.emptyWidgetText,pendingIntent);
            views.setPendingIntentTemplate(R.id.widgetListView, pendingIntent);

            //Set the textview to show when the widget is empty
            views.setEmptyView(R.id.widgetListView, R.id.emptyWidgetText);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}