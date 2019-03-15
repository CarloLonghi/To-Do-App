package com.carlolonghi.todo.widget;

import android.content.Context;
import android.content.Intent;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.carlolonghi.todo.R;
import com.carlolonghi.todo.activities.MainActivity;
import com.carlolonghi.todo.data.TodayItems;

import java.io.FileInputStream;
import java.io.ObjectInputStream;

public class MyWidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private final Context mContext;
    private TodayItems todaysItems;

    public MyWidgetRemoteViewsFactory(Context applicationContext, Intent intent) {
        mContext = applicationContext;
        }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        loadItems();
    }

    // Load the items from memory
    private void loadItems(){
        todaysItems=new TodayItems();
        try{
            FileInputStream inputStream = mContext.openFileInput("today.dat");
            ObjectInputStream reader = new ObjectInputStream(inputStream);
            todaysItems=(TodayItems) reader.readObject();
            inputStream.close();
            reader.close();
        } catch (Exception e){
            e.printStackTrace();
        }
        if(todaysItems==null)
            todaysItems=new TodayItems();
    }

    @Override
    public void onDestroy() {
    }

    @Override
    public int getCount() {
        return todaysItems.getNonCheckedItems().size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (position == AdapterView.INVALID_POSITION)
            return null;

        // Sets the text of the items
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_list_item);
        rv.setTextViewText(R.id.widgetItemTaskNameLabel, todaysItems.getNonCheckedItems().get(position).getName());

        // Opens the app if the items are clicked
        Intent intent = new Intent(mContext, MainActivity.class);
        rv.setOnClickFillInIntent(R.id.widgetItemContainer, intent);

        return rv;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

}
