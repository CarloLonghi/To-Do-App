package com.carlolonghi.todo.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.carlolonghi.todo.R;
import com.carlolonghi.todo.data.ItemsViewModel;


public class ToDoWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            Intent intent = new Intent(context, MyWidgetRemoteViewsService.class);
            views.setRemoteAdapter(R.id.widgetListView, intent);
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
