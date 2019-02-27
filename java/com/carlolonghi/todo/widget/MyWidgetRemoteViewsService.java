package com.carlolonghi.todo.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

import com.carlolonghi.todo.widget.MyWidgetRemoteViewsFactory;

public class MyWidgetRemoteViewsService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new MyWidgetRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}

