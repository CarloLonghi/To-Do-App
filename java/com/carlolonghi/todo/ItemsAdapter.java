package com.carlolonghi.todo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListAdapter;

import java.util.List;
import java.util.Map;

public class ItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int NEWITEM_TYPE=1;
    public static final int ADDNEW_TYPE=0;

    private Map<String,Items> items;
    private String listTitle;

    // Provide a reference to the views for each data item
    public static class ItemsViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout myCheckBoxContainer;
        public ItemsViewHolder(LinearLayout checkBox) {
            super(checkBox);
            myCheckBoxContainer = checkBox;
        }
    }

    public static class AddNewItemViewHolder extends  RecyclerView.ViewHolder{
        public LinearLayout newItemLayout;
        public AddNewItemViewHolder(LinearLayout newItemLayout){
            super(newItemLayout);
            this.newItemLayout=newItemLayout;
        }
    }

    public ItemsAdapter(Map<String,Items> items, String listTitle) {
        this.items=items;
        this.listTitle=listTitle;
    }

    @Override
    public int getItemViewType(int position) {
        if(position==items.get(listTitle).getNonCheckedItems().size())
            return ADDNEW_TYPE;
        else
            return NEWITEM_TYPE;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        switch(viewType) {
            case ADDNEW_TYPE:
                LinearLayout addNew=(LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.add_new_layout,parent,false);
                AddNewItemViewHolder vh2=new AddNewItemViewHolder(addNew);
                Button addNewButton=(Button)addNew.findViewById(R.id.addNewButton);
                addNewButton.setOnClickListener(new addNewClickListener());
                return vh2;
            case NEWITEM_TYPE:
            default:
                LinearLayout newItem = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
                ItemsViewHolder vh1 = new ItemsViewHolder(newItem);
                return vh1;
        }
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        int itemType=getItemViewType(position);
        if(itemType==ADDNEW_TYPE){
            //COMPLETE WITH CODE HERE
        }
        else if(itemType==NEWITEM_TYPE){
            ((CheckBox)((ItemsViewHolder)holder).myCheckBoxContainer.getChildAt(0)).setText(items.get(listTitle).getNonCheckedItems().get(position));
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return items.get(listTitle).getTotalSize()+1;
    }

    public class addNewClickListener implements View.OnClickListener{
        public void onClick(View view){
            LinearLayout container=(LinearLayout)view.getParent();
            EditText editText=(EditText)container.getChildAt(0);
            items.get(listTitle).getNonCheckedItems().add(editText.getText().toString());
            ((RecyclerView)container.getParent()).getAdapter().notifyDataSetChanged();
            editText.setText("");
        }
    }

    /*@Override
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
    }*/
}
