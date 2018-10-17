package com.carlolonghi.todo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListAdapter;

import java.util.List;

public class NonCheckedItemsAdapter extends ArrayAdapter<String> {

    private final Context context;
    private List<String> items;

    public NonCheckedItemsAdapter(Context context, List<String> items) {
        super(context,R.layout.item,items);
        this.context=context;
        this.items=items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View item=inflater.inflate(R.layout.item,parent,false);
        CheckBox checkBox=(CheckBox) item.findViewById(R.id.itemCheckbox);
        checkBox.setText(items.get(position));
        //Add the listener for the checkbox;

        return item;
    }

    @Override
    public void add(String item){
        this.items.add(item);
    }
}
